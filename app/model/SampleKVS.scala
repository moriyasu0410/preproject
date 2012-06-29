package model
import daos.kvses.SampleRequestDao
import models.JsonSerializer

case class SampleKVS(
    val pk:Option[String],
	val a:Option[String],
    val b:Option[String],
    val c:Option[String]) extends SampleRequestDao with JsonSerializer {
	def prefix = "sample_request_"
	override def key = pk

}



object SampleKVS {
  def get(req:SampleRequest): SampleKVS = {

    val reqpk = req.get() match {
      case Some(pk) => req.pk.toString()
      case _ => "none"
    }
    val reqa = req.get() match {
      case Some(a) => req.a.toString()
      case _ => "none"
    }
    val reqb = req.get() match {
      case Some(b) => req.b.toString()
      case _ => "none"
    }
    val reqc = req.get() match {
      case Some(c) => req.c.toString()
      case _ => "none"
    }

    new SampleKVS(
        pk = Some(reqpk),
        a = Some(reqa),
        b = Some(reqb),
        c = Some(reqc))

  }
}