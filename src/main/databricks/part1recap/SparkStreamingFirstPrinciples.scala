// Databricks notebook source
// MAGIC %md
// MAGIC # Spark Architecture
// MAGIC * Applications
// MAGIC   * Streaming \*
// MAGIC   * ML
// MAGIC   * GraphX
// MAGIC   * Other Libraries
// MAGIC * High-level (strutured) APIs \*
// MAGIC   * DataFrames
// MAGIC   * Datasets
// MAGIC   * Spark SQL
// MAGIC * Low-level APIs
// MAGIC   * DStreams \*
// MAGIC   * RDDs
// MAGIC   * Distributed variables
// MAGIC 
// MAGIC \* Denotes this course will focus on this as a topic

// COMMAND ----------

// MAGIC %md
// MAGIC # Why Spark Streaming?
// MAGIC * Once we compute something valuable, we want updates
// MAGIC * We need continuous processing

// COMMAND ----------

// MAGIC %md
// MAGIC # Stream Processing

// COMMAND ----------

// MAGIC %md
// MAGIC ## What is it?
// MAGIC * Calculations reprocess as new data comes in
// MAGIC * No definitive end of incoming data
// MAGIC * Unlike batch processing which operates on a fixed dataset and computes once
// MAGIC * In practice, Stream and batch work together
// MAGIC   * Incoming data is joined with a fixed dataset
// MAGIC   * Stream outputs queried by batch jobs
// MAGIC   * Maintain consistency between batch and streaming jobs

// COMMAND ----------

// MAGIC %md
// MAGIC ## Real life examples
// MAGIC * Sensor readings (IOT)
// MAGIC * Interactions with an application/website
// MAGIC * Credit card transactions
// MAGIC * Real-time dashboards
// MAGIC * Alerts and notifications
// MAGIC * Incremental big data
// MAGIC * Incremental transactional data (e.g. analytics or accounts)
// MAGIC * Interactive machine learning

// COMMAND ----------

// MAGIC %md
// MAGIC ## Pros and Cons (vs Batch Processing)
// MAGIC * Pros
// MAGIC   * Much Lower letency
// MAGIC   * Greater performance/efficienfy (especially with incremental data)
// MAGIC * Difficulties
// MAGIC   * Maintaining state and order of incoming data
// MAGIC     * We use Event Time Processing to handle this later in the course
// MAGIC   * Exactly-once processing in the context of machine failures
// MAGIC     * Spark implements Fault Tolerance for this
// MAGIC   * Responding to events at low latency
// MAGIC     * Must process data, include logic, etc. faster than data is flowing in

// COMMAND ----------

// MAGIC %md
// MAGIC # Spark Streaming Principles
// MAGIC * Declarative API
// MAGIC   * We write "what" we want spark to do and it decides "how" it gets done
// MAGIC   * Alternative: RaaT (Record-at-a-Time) Processing
// MAGIC     * A set of APIs that processes each element as it arrives
// MAGIC     * Very low-level: maintaining state and resource usage is up to the coder
// MAGIC     * hard to develop
// MAGIC * Event time vs Processing time API
// MAGIC   * event time = when the event was produced
// MAGIC   * processing time = when the event arrives
// MAGIC   * event time is critical: allows detection of late data points
// MAGIC * Continuous vs Micro-batch execution
// MAGIC   * continuous = include each data point as it arrives
// MAGIC     * Lower latency because only one record is included with each computation
// MAGIC   * micro-batch = wait for a few data points, process them all in teh new result
// MAGIC     * Higher through-put because multiple records are computed at a time
// MAGIC * Low-level (DStreams) vs High-level (Spark Structured Streaming)
// MAGIC * Spark Streaming works on micro-batches
// MAGIC   * Continuous is experimental (at the time of recording)
