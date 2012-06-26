package exceptions

import _root_.models.https.Response

/**
 * HttpResponseHandle例外.
 * httpレスポンスステータスが200以外の場合に発生する.
 */
class HttpResponseHandleException(val response:Response) extends Exception {

}