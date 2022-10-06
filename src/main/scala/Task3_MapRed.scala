package org.CS441HW1

/*
* Task 3 computes the distribution of logs types based on logs messages which match the injected pattern
* Mapper is just like mapper from task 1
* Reducer computes the distribution ( aggregates the output from mapper ) */

import com.typesafe.config.ConfigFactory
import org.CS441HW1.CommonUtils.CreateLogger
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

object Task3_MapRed {
  // Initialize the logger
  private val logger = CreateLogger(classOf[Task3_MapRed.type])
  // Initialize the config from .conf
  private val config = ConfigFactory.load()

  class Map extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable] :
    private final val one = new IntWritable(1)
    private val word = new Text()

    @throws[IOException]
    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit =
      val line: String = value.toString
      val tokens: Array[String] = line.split(" ")
      val pattern = config.getString("HW1_Mapred.Pattern").r
      pattern.findFirstMatchIn(tokens(tokens.length - 1)) match
        case Some(_) =>
          word.set(tokens(2))
          output.collect(word, one)
        case None =>

  class Reduce extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable] :

    override def reduce(key: Text, values: util.Iterator[IntWritable], output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit =
      val sum = values.asScala.reduce((valueOne, valueTwo) => new IntWritable(valueOne.get() + valueTwo.get()))
      output.collect(key, new IntWritable(sum.get()))

  @main def runMapReduce3(inputPath: String, outputPath: String) =
    logger.info("Starting Application for Task 3")
    val conf: JobConf = new JobConf(this.getClass)
    conf.set("mapred.textoutputformat.separator", ",")
    conf.setJobName("HW1_MAPRED_TASK_3")
    //conf.set("fs.defaultFS", "file:///")
    conf.set("mapreduce.job.maps", config.getString("HW1_Mapred.numOfMappers"))
    conf.set("mapreduce.job.reduces", config.getString("HW1_Mapred.numOfReducers"))
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[IntWritable])
    conf.setMapperClass(classOf[Map])
    conf.setReducerClass(classOf[Reduce])
    conf.setInputFormat(classOf[TextInputFormat])
    FileInputFormat.setInputPaths(conf, new Path(inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(outputPath))
    JobClient.runJob(conf)
}

