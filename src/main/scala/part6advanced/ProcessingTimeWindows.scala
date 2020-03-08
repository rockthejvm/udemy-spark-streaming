package part6advanced

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._


object ProcessingTimeWindows {

  val spark = SparkSession.builder()
    .appName("Processing Time Windows")
    .master("local[2]")
    .getOrCreate()

  def aggregateByProcessingTime() = {
    val linesCharCountByWindowDF = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 12346)
      .load()
      .select(col("value"), current_timestamp().as("processingTime")) // this is how you add processing time to a record
      .groupBy(window(col("processingTime"),  "10 seconds").as("window"))
      .agg(sum(length(col("value"))).as("charCount")) // counting characters every 10 seconds by processing time
      .select(
        col("window").getField("start").as("start"),
        col("window").getField("end").as("end"),
        col("charCount")
      )

    linesCharCountByWindowDF.writeStream
      .format("console")
      .outputMode("complete")
      .start()
      .awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    aggregateByProcessingTime()
  }
}
