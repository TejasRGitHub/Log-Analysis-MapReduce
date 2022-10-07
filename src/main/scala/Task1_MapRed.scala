package org.CS441HW1
/*
* HW1 CS 441 Task 1 - Find the logs which has injected pattern string and specified time intervale ( in secs_)
* This package contains one Mapper and one Reducer functions to implement this task
* The mapper takes and input string ( which is the logs line ) and processes it and creates a key ( time_interval,LogType) and value like (IntWritable number)
* The reducer combines and aggregates the values of log type corresponding to a time interval*/


import com.typesafe.config.ConfigFactory
import CommonUtils.{CommonFunctions, CreateLogger}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.conf.*
import org.apache.hadoop.io.*
import org.apache.hadoop.util.*
import org.apache.hadoop.mapred.*

import java.io.IOException
import java.text.SimpleDateFormat
import java.util
import java.util.Date
import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

object Task1_MapRed {

  // Initialize the logger for Task 1 Mapper
  private val logger = CreateLogger(classOf[Task1_MapRed.type])
  // Initialize the config factory
  private val config = ConfigFactory.load()

  // Initialize the interval Length
  val intervalLength: Int = config.getInt("HW1_Mapred.timeInterval")

  // Map Class which creates the key value pairs ( key : "Time_interval,Log_type" value : [1, 1, ...] )
  class Map extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable] :
    private final val one = new IntWritable(1)
    private val word = new Text()

    @throws[IOException]
    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit =
      val line: String = value.toString
      // Split the input text line on " "
      val tokens: Array[String] = line.split(" ")
      // Get the injected pattern string from the .conf and use if as a Regex pattern
      val pattern =  config.getString("HW1_Mapred.Pattern").r
      pattern.findFirstMatchIn(tokens(tokens.length - 1)) match
        case Some(_) =>
          // Get the batch number from the time slot and the interval length. Please check the function description for more details
          // The grpNum identifies each time interval ( e.g. 17:56:32:sss --> Grp num = 12232 )
          val grpNum = CommonFunctions.getTimeSlot(tokens, intervalLength)
          word.set(grpNum.toString + "," + tokens(2))
          output.collect(word, one)
        case None =>

  class Reduce extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable] :

    override def reduce(key: Text, values: util.Iterator[IntWritable], output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit =
      // Aggregate the values from the mapper for a key
      val sum = values.asScala.reduce((valueOne, valueTwo) => new IntWritable(valueOne.get() + valueTwo.get()))
      // Below processing step is to convert from grp number to timestamp
      val grpValue = key.toString.split(",")(0).toInt
      val timeAsKey = CommonFunctions.convertToTimeStamp(grpValue, intervalLength)
      output.collect(new Text(timeAsKey + "," + key.toString.split(",")(1)), new IntWritable(sum.get()))

  @main def runMapReduce(inputPath: String, outputPath: String) =
    logger.info("Starting Application For Task 1")
    val conf: JobConf = new JobConf(this.getClass)
    //conf.set("mapred.textoutputformat.separator", ",")
    conf.set("mapreduce.output.textoutputformat.separator", ",")
    conf.setJobName("HW1_MAPRED_TASK_1")
    //conf.set("fs.defaultFS", "file:///")
    if config.getString("HW1_Mapred.setMappers").toInt == 1 then
      conf.set("mapreduce.job.maps", config.getString("HW1_Mapred.numOfMappers"))
    if config.getString("HW1_Mapred.setReducers").toInt == 1 then
      conf.set("mapreduce.job.reduces", config.getString("HW1_Mapred.numOfReducers"))
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[IntWritable])
    conf.setMapperClass(classOf[Map])
    conf.setReducerClass(classOf[Reduce])
    conf.setInputFormat(classOf[TextInputFormat])
    FileInputFormat.setInputPaths(conf, new Path(inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(outputPath))
    val jobRet: RunningJob = JobClient.runJob(conf)
    if jobRet.isComplete then
      logger.info("Completed the Job Successfully")
}

