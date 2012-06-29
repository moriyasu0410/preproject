package daos.https
import models.https.SampleHttp
import models.https.Response

trait SampleHttpDao extends HttpDao {
    self: SampleHttp =>

  override def get(): Response = {
    params = a match {
      case None => Map.empty[String, String]
      case Some(a) => Map("a" -> a)
    }
    super.get()
  }

}