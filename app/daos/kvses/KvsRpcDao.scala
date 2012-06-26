package daos.kvses

import play._
import play.libs.WS
import collection.JavaConversions._
import play.exceptions.UnexpectedException
import org.apache.commons.io.IOUtils
import java.io.InputStream
import play.libs.Codec
import java.net.URLDecoder
import _root_.exceptions.KvsRpcException

/**
 * KvsにRPC接続するDao.
 * spymemcachedにて容易されていないAPIを実装する
 * KyotoTycoonのTSV-RPCを使用している
 */
object KvsRpcDao {

  private val rpcHost = Play.configuration.getProperty("mem.rpc.slave.host")
  private val timeout = Play.configuration.getProperty("mem.rpc.timeout")
  private val encoding = Play.configuration.getProperty("mem.rpc.encoding")

  private val colencRegex = """colenc=([B|Q|U])""".r

  /**
   * キー一覧を前方一致で取得する.
   * @param prefix 接頭語
   * @param max 取得最大件数
   * @return キー一覧
   */
  def matchPrefix(prefix: String, max: Int = -1) = {
    val params = max match {
      case max if max > 0 => Map("prefix" -> prefix, "max" -> max.toString())
      case _ => Map("prefix" -> prefix)
    }
    rpcCall("GET", "match_prefix", params)
  }

  /**
   * KTに指定されて方法でアクセスしKeyの一覧を取得する.
   * @param method GET等
   * @param rpcType rpcの種類
   * @param params パラメータ
   * @return キー一覧
   */
  private[kvses] def rpcCall(method: String, rpcType: String, params: Map[String, String] = Map.empty[String, String]) = {
    val request = WS.withEncoding(this.encoding).url(rpcHost + "/rpc/" + rpcType).timeout(this.timeout).params(params)
    val response = method match {
      case "GET" => request.get()
      case _ => throw new UnexpectedException("未対応のメソッドです。%s".format(method))
    }
    val contentType = response.getContentType()
    response.getStatus().intValue() match {
      case 200 => {
        if (!contentType.startsWith("text/tab-separated-values"))
          throw new UnexpectedException("未対応のContentTypeです。%s".format(contentType))
      }
      case status => throw new KvsRpcException(status, response.getString())
    }
    val encType = contentType match {
      case colencRegex(encType) => Some(encType)
      case _ => None
    }
    var in: InputStream = null
    var keys = List.empty[String]
    try {
      in = response.getStream()
      var reader = IOUtils.lineIterator(response.getStream(), response.getEncoding())
      while (reader.hasNext()) {
        val line = encType match {
          case Some("U") => URLDecoder.decode(reader.nextLine(), response.getEncoding())
          case Some("B") => new String(Codec.decodeBASE64(reader.nextLine()), response.getEncoding())
          case Some("Q") => throw new UnexpectedException("Quoted-printableエンコードには未対応です")
          case _ => reader.nextLine()
        }
        val array = line match {
          case line if line != null && line.startsWith("_") => line.substring(1).split("\t")
          case _ => new Array[String](0)
        }
        array match {
          case array if array.length > 0 => keys :+= array(0)
          case _ =>
        }
      }
      keys
    } finally {
      IOUtils.closeQuietly(in)
    }
  }
}
