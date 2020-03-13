# The official repository for the Rock the JVM Spark Streaming with Scala course

This repository contains the code we wrote during the *Udemy* edition of [Rock the JVM's Spark Streaming with Scala](https://rockthejvm.com/course/spark-streaming). Unless explicitly mentioned, the code in this repository is exactly what was caught on camera.

## How to install

- install Docker
- either clone the repo or download as zip
- open with IntelliJ as an SBT project
- in a terminal window, navigate to the folder where you downloaded this repo and run `docker-compose up` to build and start the containers - we will need them to integrate various stuff e.g. Kafka with  Spark

### How to start

Clone this repository and checkout the `start` tag by running the following in the repo folder:

```
git checkout start
```

### How to see the final code

Checkout the master branch:
```
git checkout master
```

### How to run an intermediate state

The repository was built while recording the lectures. Prior to each lecture, I tagged each commit so you can easily go back to an earlier state of the repo!

The tags are as follows (most recent first):

* `5.3-watermarks`
* `5.2-processing-time-windows`
* `5.1-event-time-windows`
* `4.4-cassandra`
* `4.3-jdbc`
* `4.2-kafka-dstreams`
* `4.1-kafka-structured-streaming`
* `3.2-dstreams-transformations`
* `3.1-dstreams`
* `2.4-streaming-datasets`
* `2.3-streaming-joins`
* `2.2-streaming-aggregations`
* `2.1-streaming-dataframes`
* `1.2-spark-recap`
* `1.1-scala-recap`

When you watch a lecture, you can `git checkout` the appropriate tag and the repo will go back to the exact code I had when I started the lecture.

### For questions or suggestions

If you have changes to suggest to this repo, either
- submit a GitHub issue
- tell me in the course Q/A forum
- submit a pull request!


