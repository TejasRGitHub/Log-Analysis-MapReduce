package org.CS441HW1

/*
* Task 2 computes the error log type messages and their counts for different time intervals
* This code contains two mappers and one reducer implementations
* The first mapper creates keys like ( key : "Timestamp,ERROR" value : numberOfErrorInThatTimestamp )
* The reducer then counts the number of error msg for a grp number based on time interval
* the second mapper is used to collect the values from the reducer and create the key : numberOfErrorInThatTimestamp
* This key is then sorted by the setOutputKeyComparatorClass config added while configuring the second job
*/



import com.typesafe.config.ConfigFactory
import org.CS441HW1.CommonUtils.{CommonFunctions, CreateLogger}
import org.apache.hadoop.conf.*
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.*
import org.apache.hadoop.mapred.*
import org.apache.hadoop.util.*

import java.io.IOException
import java.text.SimpleDateFormat
import java.util
import java.util.Date
import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.WritableComparable

object Task2_MapRed {

  // Iniliaze the logger
  private val logger = CreateLogger(classOf[Task2_MapRed.type])
  // Initiliase the configs written in .conf file
  private val config = ConfigFactory.load()

  // Set the interval length
  val intervalLength: Int = config.getInt("HW1_Mapred.timeInterval")

  class Map extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable] :
    private final val one = new IntWritable(1)
    private val word = new Text()

    @throws[IOException]
    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit =
      val line: String = value.toString
      // Split the input line on " "
      val tokens: Array[String] = line.split(" ")
      val pattern =  config.getString("HW1_Mapred.Pattern").r
      pattern.findFirstMatchIn(tokens(tokens.length - 1)) match
        case Some(_) =>
          // Get the batch number / grp number
          val grpNum = CommonFunctions.getTimeSlot(tokens, intervalLength)
          if tokens(2).equalsIgnoreCase("ERROR") then
            word.set(grpNum.toString + "," + tokens(2))
            output.collect(word, one)
        case None =>

  class Reduce extends MapReduceBase with Reducer[Text, IntWritable, IntWritable, Text] :
    override def reduce(key: Text, values: util.Iterator[IntWritable], output: OutputCollector[IntWritable, Text], reporter: Reporter): Unit =
      val sum = values.asScala.reduce((valueOne, valueTwo) => new IntWritable(valueOne.get() + valueTwo.get()))
      // Convert the key from grp num to secs to HH:mm:ss:SSS format
      // Get the grpValue
      val grpValue = key.toString.split(",")(0).toInt
      val timeAsKey = CommonFunctions.convertToTimeStamp(grpValue, intervalLength)
      output.collect(new IntWritable(sum.get()), new Text(timeAsKey + "," + key.toString.split(",")(1)))

  // Second mapper required to make the keys as the number of error message for the time interval
  class Map2 extends MapReduceBase with Mapper[LongWritable, Text, IntWritable, Text] :
    @throws[IOException]
    override def map(key: LongWritable, value: Text, output: OutputCollector[IntWritable, Text], reporter: Reporter): Unit =
      val line: String = value.toString
      val tokens: Array[String] = line.split(",")
      output.collect(new IntWritable(tokens(0).toInt),new Text(tokens(1) + "," + tokens(2)) )

  // Comparator to sort the output generated from the second mapper (MAP2) in descending order
  class DecreasingComparator extends IntWritable.Comparator {
      override def compare(b1: Array[Byte], s1: Int, l1: Int, b2: Array[Byte], s2: Int, l2: Int): Int = -super.compare(b1, s1, l1, b2, s2, l2)
  }

  @main def runMapReduce2(inputPath: String, outputPath: String, outputPath2: String) =
    logger.info("Starting Application for TASK 2")
    val conf = new JobConf(this.getClass)
    conf.set("mapred.textoutputformat.separator", ",")
    conf.setJobName("HW1_MAPRED_TASK_1-A")
    conf.set("mapreduce.job.maps", config.getString("HW1_Mapred.numOfMappers"))
    conf.set("mapreduce.job.reduces", config.getString("HW1_Mapred.numOfReducers"))
    conf.setOutputKeyClass(classOf[IntWritable])
    conf.setOutputValueClass(classOf[Text])
    conf.setMapperClass(classOf[Map])
    conf.setMapOutputKeyClass(classOf[Text])
    conf.setMapOutputValueClass(classOf[IntWritable])
    conf.setReducerClass(classOf[Reduce])
    conf.setInputFormat(classOf[TextInputFormat])
    FileInputFormat.setInputPaths(conf, new Path(inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(outputPath))
    val jobRet = JobClient.runJob(conf)
    if jobRet.isComplete then
      logger.info("Completed Job to create Key value pairs where key is the number of error messages & value is the timestamp and the ERROR str")
      val conf2 = new JobConf(this.getClass)
      conf2.set("mapred.textoutputformat.separator", ",")
      conf2.setJobName("HW1_MAPRED_TASK_1-B")
      conf2.set("mapreduce.job.maps", config.getString("HW1_Mapred.numOfMappers"))
      conf2.set("mapreduce.job.reduces", config.getString("HW1_Mapred.numOfReducers"))
      conf2.setOutputKeyClass(classOf[IntWritable])
      conf2.setOutputValueClass(classOf[Text])
      conf2.setMapperClass(classOf[Map2])
      conf2.setMapOutputKeyClass(classOf[IntWritable])
      conf2.setMapOutputValueClass(classOf[Text])
      conf2.setInputFormat(classOf[TextInputFormat])
      conf2.setOutputKeyComparatorClass(classOf[DecreasingComparator])
      FileInputFormat.setInputPaths(conf2, new Path(outputPath))
      FileOutputFormat.setOutputPath(conf2, new Path(outputPath2))
      JobClient.runJob(conf2)
}

