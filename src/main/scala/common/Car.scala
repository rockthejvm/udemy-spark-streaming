package common

case class Car(
                Name: String,
                Miles_per_Gallon: Option[Double],
                Cylinders: Option[Long],
                Displacement: Option[Double],
                Horsepower: Option[Long],
                Weight_in_lbs: Option[Long],
                Acceleration: Option[Double],
                Year: String,
                Origin: String
              )
