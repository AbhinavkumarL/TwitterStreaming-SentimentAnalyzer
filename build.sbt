name := "sentiment-analyzer"
description := "A demo application to showcase sentiment analysis using Stanford CoreNLP and Scala"

version := "0.1"

scalaVersion := "2.11.4"

resolvers ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/"
)

libraryDependencies ++= {
  val sparkVer = "2.1.0"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVer,
    "org.apache.spark" %% "spark-sql" % sparkVer,
    "org.apache.spark" %% "spark-mllib" % sparkVer,
    "org.apache.spark" %% "spark-streaming" % sparkVer,
    "org.apache.spark" %% "spark-streaming-kafka" % "1.6.3",
    "org.apache.spark" %% "spark-hive" % sparkVer,
    "org.apache.bahir" %% "spark-streaming-twitter" % "2.0.1",
    "edu.stanford.nlp" % "stanford-corenlp" % "3.8.0" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp"))
  )
}