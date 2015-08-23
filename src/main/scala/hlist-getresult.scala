import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

object HListGetResult extends App {

  import slick.driver.H2Driver.api._
  import shapeless._
  import slickless._

  import slick.jdbc.{ GetResult, PositionedResult }

  implicit object hnilGetResult extends GetResult[HNil] {
    def apply(r: PositionedResult) = HNil
  }

  implicit def hlistConsGetResult[H, T <: HList]
    (implicit
      h: GetResult[H],
      t: GetResult[T]
    ) =
      new GetResult[H :: T] {
        def apply(r: PositionedResult) = (r << h) :: t(r)
      }

  // Examples:
  implicitly[GetResult[Int :: HNil]]
  implicitly[GetResult[Int :: Float  :: HNil]]
  implicitly[GetResult[Int :: String :: HNil]]
  implicitly[GetResult[Int :: String :: Float :: HNil]]
  implicitly[GetResult[Int :: (String :: Float :: HNil) :: HNil]]

  case class SimpleFloat(x: Float)
  implicit val sfGetResult: GetResult[SimpleFloat] = GetResult(r => SimpleFloat(r.<<))
  implicitly[GetResult[SimpleFloat]]
  implicitly[GetResult[SimpleFloat :: HNil]]
  implicitly[GetResult[Int :: SimpleFloat :: HNil]]
}