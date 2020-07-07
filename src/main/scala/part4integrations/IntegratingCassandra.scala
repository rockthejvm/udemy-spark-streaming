package part4integrations

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.sql.{Dataset, ForeachWriter, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.cassandra._
import common._

object IntegratingCassandra {

  val spark = SparkSession.builder()
    .appName("Integrating Cassandra")
    .master("local[2]")
    .config("spark.cassandra.connection.host", "localhost")
    .config("spark.cassandra.connection.port", "9042")
    .getOrCreate()

  import spark.implicits._

  def writeStreamToCassandraInBatches() = {
    val carsDS = spark.readStream
      .schema(carsSchema)
      .json("src/main/resources/data/cars")
      .as[Car]

    carsDS
      .writeStream
      .foreachBatch { (batch: Dataset[Car], _: Long) =>
        // save this batch to Cassandra in a single table write
        batch
          .select(col("Name"), col("Horsepower"))
          .write
          .cassandraFormat("cars", "public") // type enrichment
          .mode(SaveMode.Append)
          .save()
      }
      .start()
      .awaitTermination()
  }

  class CarCassandraForeachWriter extends ForeachWriter[Car] {

    /*
      - on every batch, on every partition `partitionId`
        - on every "epoch" = chunk of data
          - call the open method; if false, skip this chunk
          - for each entry in this chunk, call the process method
          - call the close method either at the end of the chunk or with an error if it was thrown
     */

    val keyspace = "public"
    val table = "cars"
    val connector = CassandraConnector(spark.sparkContext.getConf)

    override def open(partitionId: Long, epochId: Long): Boolean = {
      println("Open connection")
      true
    }

    override def process(car: Car): Unit = {
      connector.withSessionDo { session =>
        session.execute(
          s"""
             |insert into $keyspace.$table("Name", "Horsepower")
             |values ('${car.Name}', ${car.Horsepower.orNull})
           """.stripMargin)
      }
    }

    override def close(errorOrNull: Throwable): Unit = println("Closing connection")

  }

  def writeStreamToCassandra() = {
    val carsDS = spark.readStream
      .schema(carsSchema)
      .json("src/main/resources/data/cars")
      .as[Car]

    carsDS
      .writeStream
      .foreach(new CarCassandraForeachWriter)
      .start()
      .awaitTermination()
  }


  def main(args: Array[String]): Unit = {
    writeStreamToCassandra()
  }
}
