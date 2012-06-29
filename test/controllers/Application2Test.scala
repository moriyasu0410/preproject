package controllers
import play.test.UnitFlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfterEach
import org.scalatest.BeforeAndAfterAll
import play.Logger
import play.test.Browser
import controllers.stub.SampleStub
import play.mvc.Http.Header

class Application2Test extends UnitFlatSpec with ShouldMatchers with BeforeAndAfterAll with BeforeAndAfterEach with SampleStub {

  override def beforeAll() {
    //このテストクラスが始まる前に一度だけ実行
    startServer()
  }

  override def afterAll() {
    //このテストクラス終了後に一度だけ実行
    stopServer()
  }

  override def beforeEach() = {
    //テストケースの前に都度実行=setUp
  }

  override def afterEach() = {
    //テストケースの後に都度実行=tearDown
  }

  val path = "/sample"
  val accept_language = "ja-JP"
  val accept = "text/html"
  val accept_charset = "utf-8"
  val referer = "referer"
  val ua = "Mozilla/5.0 (Linux; U; Android 2.2.1; ja-jp; ISW11HT Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"

  "Application2.index" can "recieve stub file" in {

    //準備
    val request = Browser.newRequest()
    request.params.put("a", "1")
    request.headers.put("user-agent", new Header("user-agent", ua))
    request.headers.put("accept", new Header("accept", accept))
    request.headers.put("accept-charset", new Header("accept-charset", accept_charset))
    request.headers.put("referer", new Header("referer", referer))
    request.headers.put("accept-language", new Header("accept-language", accept_language))

    //実行
    val response = Browser.GET(request, path)

    //検証
    response.status should be(200)
    //    response.cookies.containsKey("cookieKey") should be(true)
    Browser.getContent(response).trim should be("this is stub file")
  }

}