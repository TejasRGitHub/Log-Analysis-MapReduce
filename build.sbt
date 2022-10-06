ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

val logbackVersion = "1.3.0-alpha10"
val sfl4sVersion = "2.0.0-alpha5"
val typesafeConfigVersion = "1.4.1"
val apacheCommonIOVersion = "2.11.0"
val scalacticVersion = "3.2.9"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

resolvers += Resolver.jcenterRepo

lazy val root = (project in file("."))
  .settings(
    name := "HW1_MapRed"
//    idePackagePrefix := Some("org.CS441HW1")
  )

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-core" % logbackVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.slf4j" % "slf4j-api" % sfl4sVersion,
  "com.typesafe" % "config" % typesafeConfigVersion,
  "commons-io" % "commons-io" % apacheCommonIOVersion,
  "org.scalactic" %% "scalactic" % scalacticVersion,
  "org.scalatest" %% "scalatest" % scalacticVersion % Test,
  "org.scalatest" %% "scalatest-featurespec" % scalacticVersion % Test,
  "com.typesafe" % "config" % typesafeConfigVersion,
  // https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common
  "org.apache.hadoop" % "hadoop-common" % "3.3.4",
  // https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-mapreduce-client-core
  "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.3.4",
  "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "3.3.4"
)
