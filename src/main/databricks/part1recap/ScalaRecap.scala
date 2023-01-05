// Databricks notebook source
// MAGIC %md
// MAGIC # Variables and Values
// MAGIC * Variables can be reassigned
// MAGIC * Values cannot

// COMMAND ----------

var anInt: Int = 1
println(anInt)
anInt = 2
println(anInt)

val aBoolean: Boolean = false
println(aBoolean)
// aBoolean = true // throws compliation error
// println(aBoolean)

// COMMAND ----------

// MAGIC %md
// MAGIC # Instructions vs Expressions
// MAGIC ## Instructions
// MAGIC * The fundamental building blocks of imperative programming
// MAGIC   * Tells the interpreter what to do
// MAGIC * Executed in order
// MAGIC 
// MAGIC ## Expressions
// MAGIC * The fundamental building blocks of functional programming
// MAGIC   * Always evaluate down to a single value
// MAGIC * Denoted in scala as the Unit type
// MAGIC   * Unit = void, "no meaningful value"

// COMMAND ----------

// example conditional expression
val anIfExpression = if(2>3) "Bigger" else "Smaller" // evaluates to a single value

// COMMAND ----------

// Functions are still expressions because they evaluate to values
val theUnit = println("Hello, Scala") // the print function returns Unit

// COMMAND ----------

// MAGIC %md
// MAGIC # Functions

// COMMAND ----------

def myFunction(x: Int) = 42
myFunction(0)

// COMMAND ----------

// MAGIC %md
// MAGIC # OOP
// MAGIC * Scala uses single-class inheritence
// MAGIC * `object`s are singleton classes where the class is defined and instantiated into a single object in one line
// MAGIC * `traits` can be used to expand classes and force the existence of methods
// MAGIC   * Similar to Java's interfaces
// MAGIC * `object`s that share the name of a `trait` or `class` defined in the same file/cell is considered its companion
// MAGIC   * Companions can see each others private features as well as a [whole host of other things](https://docs.scala-lang.org/scala3/book/domain-modeling-tools.html#companion-objects)

// COMMAND ----------

// Example of single inheritence
class Spirit
class Plant
class Flower extends Plant
// class Leshy extends Spirit, Plant // Throws error

// COMMAND ----------

// singleton classes/objects
object MySingleton

// COMMAND ----------

// Example of trait usage
trait Carnivore {
  def eat(plant: Plant): Unit
}
class VenusFlyTrap extends Plant with Carnivore {
  override def eat(plant: Plant): Unit = println("Nom nom nom")
}
// class PitcherPlant extends Plant with Carnivore // Throws Error

// objects defined at the same time or the same file
// as a class or trait with the same name is considered a companion
object Carnivore // can share private members with its companion and much, much more

// COMMAND ----------

// MAGIC %md
// MAGIC # Generics
// MAGIC * types can be defined as containers to hold other types
// MAGIC * `A` can be used to make that container type hold any other type
// MAGIC * Preceding that `A` with a `-` or `+` makes it covariant or contravariant
// MAGIC   * [Type variance definition and example](https://docs.scala-lang.org/tour/variances.html)

// COMMAND ----------

trait MyList[A]
trait CovariantList[+A]
trait ContravariantList[-A]

// COMMAND ----------

// MAGIC %md
// MAGIC # Method Notation
// MAGIC * Methods that take a single parameter can be called with an infix notation
// MAGIC   * This [should be avoided](https://docs.scala-lang.org/style/method-invocation.html) aside from the cases of operators and higher-order functions

// COMMAND ----------

val x = 1 + 2
val y = 1.+(2)

// COMMAND ----------

// MAGIC %md
// MAGIC # Functional Programming
// MAGIC * This paradigm treats functions as if they were values
// MAGIC   * Because of this, it is imperative to create [pure functions](https://docs.scala-lang.org/scala3/book/fp-pure-functions.html) where possible
// MAGIC * Functions are actually implementations of the FunctionX traits in scala

// COMMAND ----------

// Functions are actually implementations of the FunctionX traits in scala
val fullIncrementer: Function1[Int,Int] = new Function1[Int,Int] {
  override def apply(x: Int): Int = x + 1
}

// can be sugared as a lambda (anonymous function) using => to denote inputs and outputs
val lambdaIncrementer: Int => Int = x => x + 1
// val name: inType => outType = inVal => outExpression

val inc1 = fullIncrementer(42)
val inc2 = lambdaIncrementer(42)

// either can be used in higher order funcitions (map, flatMap, filter)
val processed1 = List(1,2,3).map(fullIncrementer)
// or
val processed2 = List(1,2,3).map(x => x+1)
// or better yet
val processed3 = List(1,2,3).map (_ + 1) // _ is a generic for an iterative value

// COMMAND ----------

// MAGIC %md
// MAGIC # Pattern Matching
// MAGIC * Has a basic, switch-like form
// MAGIC * Used in try-catch blocks
// MAGIC * Can be used for generators in [`for` comprehensions](https://docs.scala-lang.org/tour/for-comprehensions.html)

// COMMAND ----------

// Basic form example
val unknown: Any = 45
val ordinal = unknown match {
  case 1 => "first"
  case 2 => "second"
  case _ => "unknown"
}

// COMMAND ----------

// try-catch example
try {
  throw new NullPointerException
} catch {
  case e: NullPointerException => "some returned value"
  case _ => "something else"
}

// COMMAND ----------

// MAGIC %md
// MAGIC # Futures
// MAGIC * Scala's implementation of concurrent processing
// MAGIC * Futures will be typed for the end result of their function
// MAGIC * [For more information](https://docs.scala-lang.org/scala3/book/concurrency.html)

// COMMAND ----------

import scala.concurrent.ExecutionContext.Implicits.global // implicitly adds this to the Future
import scala.concurrent.duration._
import scala.concurrent.{Future, Await}
import scala.util.{Failure, Success}

val aFuture = Future {
  // some expensive computation, runs on another thread
  42 // ends up being typed for return value after computation completes
}

aFuture.onComplete { // Handling successful or failed runs in the other thread
  case Success(meaningOfLife) => println(s"I've found $meaningOfLife!")
  case Failure(ex) => println(s"I have failed: $ex")
} // must yield Unit for each

// COMMAND ----------

// Await is to be used to block the main thread and return the results of a Future
Await.result(aFuture, 10.seconds) // seems like there's an implicit conversion for Int => Duration (see below)

// COMMAND ----------

// MAGIC %md
// MAGIC # Implicits
// MAGIC * can be used to act as automatically injected default values for functions, convert data types implicitly, and much more
// MAGIC * It is likely that there is a built in conversion method for `Int`s to cast to `Duration`s base on the `.seconds`, `.nanos`, and similar methods
// MAGIC * Searches in these three areas to attempt the implicit match
// MAGIC   1. Local scope
// MAGIC       - Like `implicitInt` example below
// MAGIC   2. Imported scope
// MAGIC       - Like `10.seconds` and `...ExecutionContext.Implicits.global` examples above
// MAGIC   3. Companion objects of the types involved in the method call

// COMMAND ----------

// like default arguments (compiler auto-injects)
def methodWithImplicitArg (implicit x: Int) = x + 43
implicit val implicitInt = 67
val implicitCall = methodWithImplicitArg
// 67 is implied as the value of the argument since its the only implicit of that type
// defining another implicit Int will throw an ambiguour implicit error

// COMMAND ----------

// implicit conversions - implicit def
case class Person(name: String) {
  def greet = println(s"Hi, my name is $name")
}
implicit def fromStringToPerson(name: String) = Person(name)
"Bob".greet // calling this method implicitly converts String to Person then calls greet

// COMMAND ----------

// implicit conversion - implicit classes (almost always preferred to implicit defs)
implicit class Dog(name: String) {
  def bark = println("Bark!")
}
"Lassie".bark // implicitly converts the String Lassie to a Dog and calls bark

// COMMAND ----------


