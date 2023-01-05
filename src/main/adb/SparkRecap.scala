// Databricks notebook source
// MAGIC %md
// MAGIC # Preface
// MAGIC For the purposes of this course a container was created in the banzeaustopbtstg storage account. The following two cells exist to setup the connection to that container. The value `strgDir` needs to be used in place of the path `src/main/resources/data` from the original scripts for this and all following notebooks.

// COMMAND ----------

// dbutils.widgets.text("storagename", "banzeaustopbtstg")
// dbutils.widgets.text("storagecntner", "udemy-spark-streaming")
// dbutils.widgets.text(
//   "storage_acc_key",
//   "eZ21Kpe+wsZibXfL6GEFDHixCP2IvtKNcN5WiQ/5z5Nzzvp7l8jDTrNewn7PDoqn1n+7Sef1kS37+AStXlg5Pw=="
// )

// COMMAND ----------

val strgAcct = dbutils.widgets.get("storagename")
val strgCntnr = dbutils.widgets.get("storagecntner")
val strgKey = dbutils.widgets.get("storage_acc_key")

spark.conf.set(s"fs.azure.account.key.$strgAcct.blob.core.windows.net", strgKey)
val strgDir = s"wasbs://$strgCntnr@$strgAcct.blob.core.windows.net/"

// COMMAND ----------

// MAGIC %md
// MAGIC # Spark Structured API
// MAGIC * Consists of the SQL API and the DataFrame (DF) API

// COMMAND ----------

// // This cell initializes the Spark Strutured API
// // It is not required on ADB as this API was initialized when the cluster starts
// import org.apache.spark.sql.SparkSession

// val spark = SparkSession.builder()
//   .appName("Spark Recap")
//   .master("local[2]") // The [2] tells it to use 2 threads
//   .getOrCreate()

// COMMAND ----------

// MAGIC %md
// MAGIC ## Reading and displaying a DF

// COMMAND ----------

val cars = spark.read
  .format("json")
  .option("inferSchema", "true")
  .load(s"$strgDir/cars")

// cars.printSchema() // this happens by default in ADB on the variable assignment
display(cars) // this is in place of cars.show()

// COMMAND ----------

// MAGIC %md
// MAGIC ## Selecting columns and calculations

// COMMAND ----------

import org.apache.spark.sql.functions._ // required for using sql functions like `col`
// import spark.implicits._ // allows for the use of implicits/interpolators like the $ but ADB has it loaded by default

// selecting rows from a DF

val usefulCarsData = cars.select(
  col("Name"), // single column object
  $"Year", // another column object using the $ interpolator
  ($"Weight_in_lbs" / 2.2).as("Weight_in_kg"), // an expression for a dynamically calculated column and its alias
  expr("Weight_in_lbs / 2.2").as("Weight_in_kg2") // same as above using a SQL expression instead of a scla one
)
display(usefulCarsData)

// COMMAND ----------

// same as above using only SQL expressions
val carWeights = cars.selectExpr("Name", "Year", "Weight_in_lbs / 2.2 as Weight_in_kg")
display(carWeights)

// COMMAND ----------

// MAGIC %md
// MAGIC ## Filtering columns

// COMMAND ----------

val nonAmericanCars = cars.where($"Origin" =!= "USA") // can also be cars.filter(...)
display(nonAmericanCars)

// COMMAND ----------

// MAGIC %md
// MAGIC ## Aggregations
// MAGIC * [Several options](https://spark.apache.org/docs/latest/sql-ref-functions-builtin.html) including avg, sum, mean, stddev, min, and max

// COMMAND ----------

// aggregations
val averageHP = cars.select(avg($"Horsepower").as("avg_hp"))
display(averageHP)

// COMMAND ----------

// MAGIC %md
// MAGIC ## Grouping

// COMMAND ----------

val countByOrigin = cars.groupBy($"Origin").count()
display(countByOrigin)

// COMMAND ----------

// MAGIC %md
// MAGIC ## Joining

// COMMAND ----------

val guitarPlayers = spark.read
  .option("inferSchema", "true")
  .json(s"$strgDir/guitarPlayers") // shorthand for .format("json").load(path)
display(guitarPlayers)

// COMMAND ----------

val bands = spark.read
  .option("inferSchema", "true")
  .json(s"$strgDir/bands")
display(bands)

// COMMAND ----------

val guitaristsBands = guitarPlayers.join(
  bands,
  guitarPlayers.col("band") === bands.col("id")
  // defaults to inner join but outer (L, R, F), semi, and anti  are all available
)
display(guitaristsBands)

// COMMAND ----------

// MAGIC %md
// MAGIC ## Datasets
// MAGIC * are distributed collections of typed objects instead of records in a table/DF
// MAGIC * require spark.implicits for the conversion
// MAGIC * enforce type safety as data is extracted from the DF

// COMMAND ----------

// converting a DF to a Dataset

// creating a class to represent each record/object
case class GuitarPlayer(id: Long, name: String, guitars: Seq[Long], band: Long)

// casting the DF to a DS using the record/object type defined above
val guitarPlayersDS = guitarPlayers.as[GuitarPlayer]
display(guitarPlayersDS.map(_.name))

// COMMAND ----------

// MAGIC %md
// MAGIC ## Spark SQL

// COMMAND ----------

cars.createOrReplaceTempView("cars") // establishes cars DF as a SQL-queryable view

val americanCars = spark.sql("select Name from cars where Origin = 'USA'")
display (americanCars)

// COMMAND ----------

// MAGIC %md
// MAGIC # Low-level API
// MAGIC * Resilient Distributed Datasets (RDDs)
// MAGIC * Doesn't have SQL API, only has functional

// COMMAND ----------

val sc = spark.sparkContext
val numbersRDD = sc.parallelize(1 to 1000000)

// COMMAND ----------

// running functional code on the RDD
val doubles = numbersRDD.map(_ * 2)

// COMMAND ----------

// converting RDD -> DF
val numbersDF = numbersRDD.toDF("number") // loses type info but gain SQL capabilities
display(numbersDF)

// COMMAND ----------

// converting RDD -> DS
val numbersDS = spark.createDataset(numbersRDD) // keeps typing and adds SQL
display(numbersDS)

// COMMAND ----------

// converting DS -> RDD
val guitarPlayersRDD = guitarPlayersDS.rdd

// DF -> RDD
val carsRDD = cars.rdd // RDD[row] where row is an untyped collection of data

// COMMAND ----------


