package controllers.stub
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import play.Play
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpExchange
import play.Logger
import com.sun.net.httpserver.Headers
import daos.files.FileDao

trait SampleStub {
  val server = HttpServer.create(new InetSocketAddress(Play.configuration.getProperty("webserver.port").toInt), 0)

  def startServer() {
    Logger.info("start")
    server.createContext("/", handler)
    server.start
  }

  def stopServer() {
    Logger.info("stop")
    server.stop(1)
  }

  val handler = new HttpHandler {
    def handle(exchange: HttpExchange) = {
      Logger.info("SampleStub handle init")
      var out = exchange.getResponseBody
      var path = exchange.getRequestURI().getPath()
      Logger.info("SampleStub handle path=" + path)
      var params: Map[String, String] =
        if (exchange.getRequestURI().getQuery() != null) {
          Logger.info("SampleStub handle query=" + exchange.getRequestURI().getQuery())
          exchange.getRequestURI().getQuery().split("&") match {
            case a if a.length > 0 => a.map(parameter => {
              val param = parameter.split("=")
              param(0) -> (if (param.length >= 2) param(1) else "")
            }).toMap[String, String]
            case _ => null
          }
        } else { null }
      /**
       * Paramの中身取得
       */
      def getValueFromParam(key: String): String = {
        try {
          val ret = params.get(key) match {
            case Some(str: String) => str
            case _ => ""
          }
          ret.split(",") match {
            case ar: Array[String] if ar.length > 0 => ar(0)
            case _ => ""
          }
        } catch {
          case e: NoSuchElementException => ""
          case e: Exception => {
            e.printStackTrace()
            throw e
          }
        }
      }
      Logger.debug("SampleStub handle params:" + params.toString())

      path match {
        case "/sampleStub" => {
          try {
            exchange.getResponseHeaders().set("content-Type", "text/javascript; charset=UTF-8")
            exchange.sendResponseHeaders(200, 0)

            out.write(SampleStubFileDao.readFileAsString(
              "a=" + getValueFromParam("a")).getBytes())
          } finally {
            out.close
          }
        }
        case "/timeout/" => {
          try {
            Logger.debug("Timeout 15000ms /timeout/1.0/")
            Thread.sleep(15000)
          } finally {
            out.close
          }

        }
        case _ => out.write(("PATH ERROR (" + path + ")").getBytes()); out.close
      }
    }
  }
}
object SampleStubFileDao extends FileDao {
  override def dir = "testdata/Sample/"
}
