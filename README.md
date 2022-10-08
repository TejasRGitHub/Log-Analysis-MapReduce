# Tejas Dhananjay Rajopadhye ( UIN - 675873639)

## CS441_HW1_MapReduce
CS 441 HW1 using Hadoop's Map Reduce to process logs files 

Apache Hadoop MapReduce is a framework which can be used to process data in distributed style. MapReduce uses a mapper functions which 
creates key value pairs and each independent data item. For example, in this project log files ( having specified log format ) are
used as input. Thus, each log line is and independent input and that is used a input to the mapper function. 

Reducer works by taking the output from the mapper (about the key value pairs) and processes and combines the results from mappers and produces the final results

#### Youtube Video - https://www.youtube.com/watch?v=x-pt8sSvxjE

## Instructions on Setting up Project

1. Please clone / pull the repository from https://github.com/Tejas-UIC/CS441_HW1_MapReduce.git
2. Once the repository is cloned, open the project in IntelliJ (This code was built and tested on IntelliJ IDEA 2022.2.1)
3. If sbt is installed on the system, type on terminal in this order `sbt clean`, `sbt compile`, `sbt assembly` OR if you have sbt and scala plugins installed on IntelliJ then type `clean`, `compile`, `assembly`.
4. `sbt assembly` will compile and create a fat jar inside the `target\scala-x.x.x` (where x.x.x is the scala version)
5. Then use this Jar to run Mapreduce jobs on Hadoop or AWS EMR. Please refer to below sections for running the jar.

To run using sbt run , please type `sbt run <logFile path> <output file path>`. Then the terminal will prompt to run select map reduce tasks. Below table summarizes this mapping.
Also note that if you are running task 2, then add intermediate output path (refer to hadoop commands for the same)

| Package.Main method name | Task  |
|--------------------------|-------|
| org.CS441HW1.runMapReduce | task1 |
| org.CS441HW1.runMapReduce2 | task2 |
| org.CS441HW1.runMapReduce3 | task3 |
| org.CS441HW1.runMapReduce4 | task4 |


## Running MapReduce Tasks (On Hadoop)

Once you have the Jar ready, you can run each tasks by following the below commands

For running locally on IntelliJ you can run each tasks by opening `Task1_MapRed` file from `./src/main/scala/` for task 1. Similarly, for other tasks

For running jobs on Hadoop installation locally, start the hadoop namenode and datanodes / yarn setup. Please create a folder inside the HDFS file system. For creating a folder and copying files from local to HDFS system please use issue the following commands on the terminal.
1. `hdfs dfs -mkdir /user/<user name>/input/`
2. `hdfs dfs -copyFromLocal -r <local File input path> /user/<user name>/input/`

For Running Task 1: 
`yarn jar <file path to the jar file>/HW1_MapRed-assembly-0.1.0-SNAPSHOT.jar org.CS441HW1.runMapReduce /user/<user name>/input /user/<user name>/output`

For Running Task 2:
`yarn jar <file path to the jar file>/HW1_MapRed-assembly-0.1.0-SNAPSHOT.jar org.CS441HW1.runMapReduce2 /user/<user name>/input /user/<user name>/output_temp /user/<user name>/output`

For Running Task 3:
`yarn jar <file path to the jar file>/HW1_MapRed-assembly-0.1.0-SNAPSHOT.jar org.CS441HW1.runMapReduce3 /user/<user name>/input /user/<user name>/output`

For Running Task 4:
`yarn jar <file path to the jar file>/HW1_MapRed-assembly-0.1.0-SNAPSHOT.jar org.CS441HW1.runMapReduce4 /user/<user name>/input /user/<user name>/output`


Note - The timeinterval, pattern to match the log message, and other configs are set in application.conf. In order to change these please open the project in IntelliJ editor and edit them in the application.conf and create a fat Jar by issuing `sbt assembly` from the terminal.

## Running on AWS EMR 

Once you have generated the jar file, login into the AWS Account and create an S3 bucket and put your jar and input logs files there
Then goto AWS EMR section and create an EMR Cluster with default setting. Please select Hadoop ^=3.2.1 version (or ^=emr-6.8.0).
Create a step and provide it path to the jar file (which you kept in S3 bucket). Then provide the following arguments depending on the task

e.g. for task1, use these argument , org.CS441HW1.runMapReduce s3://hw1-441-logfiles/ s3://hw1-441-outputs/task1

Please check the [youtube](https://www.youtube.com/watch?v=x-pt8sSvxjE) video for more details

## Input / Log files used for this project

The log files used as an input to the mapreduce are - 
5 log files each of 1MB each, 2 log files each of 12 - 15 MB each and one large log file 70MB.
Each log file generates 1 mapper task inside a job. 

Note - If the number of mappers and reducers is given then those many mappers and reducers are used for each task
For testing, the number of mappers is not set but the number of reducer is set to 1.

## Configuration Settings 

| Configs       | Value of Config                                       |
|---------------|-------------------------------------------|
| timeInterval  | 5 seconds                                             |
| setMappers    | 0 (Number of mapper not set while configuring job)    |
| setReducers   | 1 (Number of reducer will be set while configuring job) |
| numOfReducers | 1                                                     |
| logType       | ERROR                                                 |

Note - These configuration are mentioned in application.conf (/src/main/resources/). If you want to modify them, change it in application.conf and compile and create the fat jar again with `assembly` command in IntelliJ OR `sbt assembly` command 

## Project Deliverables 

#### Task 1 - 

Task one was about finding the log types based on some certain time interval. For this, the time interval is taken
from the `application.conf` file from the config setting `timeInterval`. This time interval is in seconds. 
Thus, each timestamp from the log file is clubbed into a batch number / group number 

e.g. 22:45:18.475 , 22:45:18.476. For these two timestamps, the batch number for these will be the same. The batch 
number is calculated by converting these timestamps to integer values. 

The mapper for the task 1 maps the key : timestamp,logType and value : [1]
For e.g. key=22:45:18.000,ERROR & value=[1]

This intermediate output is then fed to the reducer which produces key value pairs after aggregating values from the mapper output
For e.g. output from mapper after shuffling and sort stage key=22:45:18.000,ERROR & value=[1,1,1] . Then the output from reducer will be
key=22:45:18.000,ERROR & value=[3]

#### Task 2 - 

For task 2, there are two mapper and one reducer outputting the log messages in descending order given the timestamp and logtype

Mapper 1 - this produces the same output as the mapper from the task 1. Here, though only error log messages are considered ( which also have the injected pattern string embedded into the logging string )

Reducer - The reducer performs the exact same function as in task 1

Mapper 2 - This mapper uses the output from the first map reduce job (The first map reduce has to complete in order for this job to start)
The mapper creates a intermediate output in which the value from the reducer becomes the key and the key from the reducer becomes the value

For e.g. key=22:45:18.000,ERROR & value=[3] from the reducer stage 
Output from Mapper 2 is key=[3] value=22:45:18.000,ERROR

This is made so that the `DecreasingComparator` can be used to sort the keys in decreasing order 

#### Task 3 -

For task 3, there is one mapper and one reducer.

The mapper does the same work as that of task 1 except the key only contains the logtype 
e.g. - key={ERROR, INFO, DEBUG, etc} value=[1]

The reducer aggregates the values for each key and the produces the output and writes it to the output path

#### Task 4 - 

The mapper for task 4 outputs the key value pairs in the following manner 

key = {ERROR, INFO, etc} , value = length of the log string

e.g. key=INFO , value=[321]

The reducer simply calculates the maximum length of log message for each log type

e.g. key=INFO , value=[12,232,1212,323] 
From this value list , the maximum value (which corresponds to the length of the log string) is calculated in the reducer stage




