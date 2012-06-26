package utils
import play.Logger
import play.mvc.Http.Header
import play.Play
import play.mvc.Http.Cookie
import play.mvc.Http
import play.mvc.Http.Request
//import models.AdRequest
//import play.modules.mobileips.MobileIps

trait Requests {
  val userIdPrefix = Play.configuration.getProperty("openids.useridprefix");
  val cookieDomainKeyPrefix = "openids.cookie.domain.";
  val cookieIdKeyPrefix = "openids.cookie.keyid.";
  val cookieTargetKeyPrefix = "openids.cookie.keytg.";
  val cookieKeySuffixs = Play.configuration.getProperty("openids.cookie.key.suffixs");
  val cookieSecurity = Play.configuration.getProperty("openids.cookie.security").toBoolean;
  val xidcookieDomain = Play.configuration.getProperty("xid.cookie.domain")
  val xidcookieIdKey = Play.configuration.getProperty("xid.cookie.key")
  val xidcookieSecurity = Play.configuration.getProperty("xid.cookie.security").toBoolean
  val optoutCookieDomain = Play.configuration.getProperty("optout.cookie.domain")
  val optoutCookieIdKey = Play.configuration.getProperty("optout.cookie.key")
  val optoutCookieOptInValue = Play.configuration.getProperty("optout.cookie.optin")
  val optoutCookieOptOutValue = Play.configuration.getProperty("optout.cookie.optout")
  val optoutCookieSecurity = Play.configuration.getProperty("optout.cookie.security").toBoolean
  val iOsVersionsPattern = """(\d)_(\d)(_(\d))?""".r

  /**
   * ユーザIDを取得する関数。<br/>
   * <br/>
   * @param xid 広告リクエスト元からのリクエストオブジェクトに含まれるユーザID<br/>
   * @param xidCookie 広告リクエスト元からのリクエストのクッキーに含まれるユーザID<br/>
   * @param uid 広告リクエスト元からのリクエストに含まれる外部ADNWから渡されるユーザID<br/>
   * @param ua 広告リクエスト元からのリクエストオブジェクトに含まれるUA<br/>
   * @param ip 広告リクエスト元からのリクエストオブジェクトに含まれるIP<br/>
   * @return ユーザID
   */
  def getXid(
    xid: Option[String],
    cookie: java.util.Map[java.lang.String, play.mvc.Http.Cookie],
    uid: String,
    ua: Option[String],
    ip: Option[String]): Option[String] = {

    //広告リクエスト元からのリクエストオブジェクトにすでにユーザIDが設定されているかチェック
    if (Checks.isExist(xid)) {
      //含まれている場合はそれを返す
      return xid
    }

    //リクエストのクッキーに含まれるユーザIDについてチェック
    if (cookie != null && isExistXidCookie(cookie)) {
      //含まれている場合はそれを返す
      return Some(cookie.get(xidcookieIdKey).value)
    }

    //外外部ADNWから渡されるユーザIDについてチェック
    if (Checks.isExist(uid)) {
      //含まれている場合はそれを返す
      return Some(uid)
    }
    //uaについてチェック
    var uuidSeed = if (Checks.isExist(ua)) ua.get else ""

    //ipについてチェック
    uuidSeed = if (Checks.isExist(ip)) uuidSeed + ip.get else uuidSeed
    //ミリ秒付加
    var currentDate = Dates.getCurrentDateStr("yyyyMMddHHmmssSS")
    uuidSeed += currentDate
    //上記以外の場合はUA+IP+ミリ秒をSHA1ハッシュしたものをユーザIDとする
    Some(Codecs.sha1(uuidSeed))
  }

  /**
   * UAを取得する関数。<br/>
   * <br/>
   * @param parameterUa 広告リクエスト元からのリクエストパラメタオブジェクトに含まれるUA<br/>
   * @param headers 広告リクエスト元からのリクエストヘッダオブジェクト
   * @return UA
   */
  def getUa(
    parameterUa: Option[String],
    headers: java.util.Map[java.lang.String, play.mvc.Http.Header]): Option[String] = {
     getParameterOrHeader("user-agent", parameterUa, headers)
  }

  /**
   * リファラーを取得する関数。<br/>
   * <br/>
   * @param parameterReferer 広告リクエスト元からのリクエストパラメタオブジェクトに含まれるリファラー
   * @param headers 広告リクエスト元からのリクエストヘッダオブジェクト
   * @return リファラー
   */
  def getReferer(
    parameterReferer: Option[String],
    headers: java.util.Map[java.lang.String, play.mvc.Http.Header]): Option[String] = {
     getParameterOrHeader("referer", parameterReferer, headers)
  }

  /**
   * AcceptLanguageを取得する関数。<br/>
   * <br/>
   * @param parameterLanguage 広告リクエスト元からのリクエストパラメタオブジェクトに含まれるAcceptLanguage
   * @param headers 広告リクエスト元からのリクエストヘッダオブジェクト
   * @return AcceptLanguage
   */
  def getAcceptLanguage(
    parameterLanguage: Option[String],
    headers: java.util.Map[java.lang.String, play.mvc.Http.Header]): Option[String] = {
    getParameterOrHeader("accept-language", parameterLanguage, headers)
  }

  /**
   * 指定された名前の値のヘッダーもしくはパラメータの取得する関数。<br/>
   * <br/>
   * @param headerName ヘッダ名
   * @param parameter 広告リクエスト元からのリクエストパラメタの値
   * @param headers 広告リクエスト元からのリクエストヘッダオブジェクト
   * @return 値
   */
  def getParameterOrHeader(
    headerName: String,
    parameter: Option[String],
    headers: java.util.Map[java.lang.String, play.mvc.Http.Header]): Option[String] = {
    //広告リクエスト元からのリクエストパラメタオブジェクトに含まれる値が設定されているかチェック
    if (Checks.isExist(parameter)) {
      return parameter
    }
    //広告リクエスト元からのリクエストヘッダオブジェクトに含まれる値が設定されているかチェック
    if (headers == null) {
      return None
    }
    headers.get(headerName) match {
      case header: Header if header != null && header.value() != null && !header.value().isEmpty() => Some(header.value())
      case _ => None
    }
  }

  /**
   * IPを取得する関数。<br/>
   * <br/>
   * @param xForwardedIp 拡張ヘッダ「X-Forwarded-Ip」のIP<br/>
   * @param remoteAddress 拡張ヘッダ「X-Forwarded-For」のIP<br/>
   * @param xRemoteAddr 拡張ヘッダ「X-Remote-Addr」のIP<br/>
   * @return IP
   */
  def getIp(request:Request): Option[String] = {

    //パラメタ「ip」が設定されているかチェック
    val ip = request.params.get("ip")
    if (Checks.isExist(ip)) {
      //含まれている場合はそれを返す
      return Some(ip)
    }

    val headers = request.headers
    val xForwardedIp = headers.get("x-forwarded-ip")
    //拡張ヘッダ「X-Forwarded-Ip」のIPが設定されているかチェック
    if (Checks.isExistHeader(xForwardedIp)) {
      //含まれている場合はそれを返す
      return Some(xForwardedIp.value())
    }

    //remoteAddressが設定されているかチェック
    val remoteAddress = request.remoteAddress
    if (Checks.isExist(remoteAddress)) {
      //含まれている場合はそれを返す
      return Some(remoteAddress.split(",")(0))
    }

    val xRemoteAddr = headers.get("x-remote-addr")
    //拡張ヘッダ「X-Remote-Addr」のIPが設定されているかチェック
    if (Checks.isExistHeader(xRemoteAddr)) {
      //含まれている場合はそれを返す
      return Some(xRemoteAddr.value())
    }

    None
  }

  /**
   * デバイス名・各種OS情報を取得する関数。<br/>
   * <br/>
   * @param parameterUa 広告リクエスト元からのリクエストオブジェクトに含まれるUA<br/>
   * @return デバイス名、各種OS情報のタプル
   */
  def getDeviceInfomation(
    ua: Option[String]): (Option[String], Option[String], Option[String], Option[String]) = {

    /**ADPから引き継いだ特殊なUAに対する正規表現パターン*/
    val NewsExAppPattern = """.*Android\s+([a-zA-Z0-9]+)[^a-zA-Z0-9]+([a-zA-Z0-9]+)(?:[^;]+)?;\s+([^\(]+)\(NewsEx\s+\3\);\s+NewsExApp.*""".r
    /**ADPから引き継いだau one market由来のUAに対する正規表現パターン*/
    val AuonemarketPattern = """.*[^\s]+\s+[^/]+/[^/]+/[^/]+/([^/]+)/.*auonemarket.*""".r
    /**Android SDKの情報を取得するための正規表現パターン*/
    val AndroidPattern = """.*Android\s+([a-zA-Z0-9]+)[^a-zA-Z0-9]+([a-zA-Z0-9]+)(?:[^;]+)?;(?:[^;]+;)?(.+)(?:;[^/]+|[\s_]+Build)/[^\)]+\).*""".r
    /**iOS SDKの情報を取得するための正規表現パターン*/
    val IOsPattern = """.*\((iPhone|iPad|iPod);(?:\s+U;)?\s+CPU(?:\s+iPhone)?\s+OS\s+(\d+)_(\d+)(_(\d+))?.*""".r

    //広告リクエスト元からのリクエストオブジェクトに含まれるUAが設定されているかチェック
    if (!Checks.isExist(ua)) {
      //設定されていなければ空のデバイス名、端末種類固定値、各種OS情報のタプルを返す
      return (None, None, None, None)
    }

    try {
      //ADPから引き継いだ特殊なUAに対する処理
      if (ua.get.contains("NewsExApp")) ua.get match { case NewsExAppPattern(osMajorVersion, osMinorVersion, deviceName) => (Some(deviceName.trim()), Some("Android"), Some(osMajorVersion), Some(osMinorVersion)) case _ => (None, None, None, None) }
      //ADPから引き継いだau one market由来のUAに対する処理
      else if (ua.get.contains("auonemarket")) ua.get match { case AuonemarketPattern(deviceName) => (Some(deviceName.trim()), Some("Android"), None, None) case _ => (None, None, None, None) }
      //AndroidSDK由来のUAに対する処理
      else if (ua.get.contains("Android")) ua.get match { case AndroidPattern(osMajorVersion, osMinorVersion, deviceName) => (Some(deviceName.trim()), Some("Android"), Some(osMajorVersion), Some(osMinorVersion)) case _ => (None, None, None, None) }
      else if (ua.get.contains("iPhone")
        || ua.get.contains("iPad")
        || ua.get.contains("iPod")) {
        ua.get match {
          //iOSSDK由来のUAに対する処理
          case IOsPattern(deviceName, osMajorVersion, osMinorVersion, _, osBuild) => {
            var version = osMajorVersion + "." + osMinorVersion

            version = if (Checks.isExist(osBuild)) version + "." + osBuild else version
            (Some(deviceName.trim() + version), Some("iOS"), Some(osMajorVersion), Some(osMinorVersion))
          }
          case _ => (None, None, None, None)
        }
      } else {
        (None, None, None, None)
      }
    } catch {
      //今後様々なUA出てくることを考えてUAに対するパースで発生した例外は無視する
      case e: java.lang.Exception => {
        Logger.info(String.format("User-Agentが不正です。User-Agent:%s", ua.get));
        (None, None, None, None)
      }
    }
  }
  /**
   * RequestHeaderの値を取得し、Option[String]形式で返却。
   * Headerから値が取得不可の場合、Noneを返却。
   * @param header名
   * @return header値
   */
  def getHeader(header: Header): Option[String] = {
    if (Checks.isExistHeader(header)) {
      return Some(header.value)
    }
    return None
  }
  /**
   * クッキーを設定する.
   */
  def setCookie(userIdStr: String, cookieExpire: Int, targetType: Int) = {
    val usrId = userIdStr.replace(userIdPrefix, "")
    cookieKeySuffixs.split(",").foreach(suffix => {
      val cookieDomain = Play.configuration.getProperty(cookieDomainKeyPrefix + suffix)
      val cookieIdKey = Play.configuration.getProperty(cookieIdKeyPrefix + suffix)
      val cookieTargetKey = Play.configuration.getProperty(cookieTargetKeyPrefix + suffix)
      if (Logger.isDebugEnabled()) {
        Logger.debug("set cookie key:[%s,%s], id:[%s],target:[%s],domain:[%s],security:[%s],expire:[%s]",
          cookieIdKey, cookieTargetKey, usrId, targetType.toString(), cookieDomain, cookieSecurity.toString(), cookieExpire.toString())
      }
      addCookie(cookieIdKey, usrId, cookieDomain, "/", cookieExpire, cookieSecurity)
      addCookie(cookieTargetKey, targetType.toString, cookieDomain, "/", cookieExpire, cookieSecurity)
    })
  }
  /**
   * xidを設定する
   */
  def setXidCookie(xid: Option[String]) {
    val cookieExireDef = Play.configuration.getProperty("xid.cookie.expiredef").toInt;
    if (Logger.isDebugEnabled()) {
      Logger.debug("set cookie key:[%s], id:[%s],domain:[%s],security:[%s]",
        xidcookieIdKey, xid.getOrElse(""), xidcookieDomain, xidcookieSecurity.toString())
    }
    addCookie(xidcookieIdKey, xid.getOrElse(""), xidcookieDomain, "/", cookieExireDef, xidcookieSecurity)
  }
  /**
   * cookieにオプトアウト設定とxidリセットを行う
   */
  def setOptoutAndResetXidCookie() {
    val xidcookieExireDef = Play.configuration.getProperty("xid.cookie.expiredef").toInt;
    val optoutcookieExireDef = Play.configuration.getProperty("optout.cookie.expiredef").toInt;
    val optoutValue = optoutCookieOptOutValue
    val xidEmptyValue = ""
    if (Logger.isDebugEnabled()) {
      Logger.debug("set cookie key:[%s], id:[%s],domain:[%s],security:[%s]",
        optoutCookieIdKey, optoutValue, optoutCookieDomain, optoutCookieSecurity.toString())
      Logger.debug("set cookie key:[%s], id:[%s],domain:[%s],security:[%s]",
        xidcookieIdKey, xidEmptyValue, xidcookieDomain, xidcookieSecurity.toString())
    }
    addCookie(xidcookieIdKey, xidEmptyValue, xidcookieDomain, "/", xidcookieExireDef, xidcookieSecurity)
    addCookie(optoutCookieIdKey, optoutValue, optoutCookieDomain, "/", optoutcookieExireDef, optoutCookieSecurity)
  }
  /**
   * クッキーを登録する.
   */
  def addCookie(name: String, value: String, domain: String, path: String, maxAge: Integer, secure: Boolean, httpOnly: Boolean = false): Unit = {
    val cookie = new Cookie()
    cookie.name = name
    cookie.value = value
    cookie.path = path
    cookie.secure = secure
    cookie.httpOnly = httpOnly
    if (domain != null) {
      cookie.domain = domain
    } else {
      cookie.domain = Cookie.defaultDomain
    }
    cookie.maxAge = maxAge;
    Http.Response.current().cookies.put(name, cookie)
  }
  /**
   * cookieのxidが設定されているかを判定
   * @return false:未設定 true:設定有り
   */
  def isExistXidCookie(cookies: java.util.Map[java.lang.String, play.mvc.Http.Cookie]): Boolean = {
    if (Logger.isDebugEnabled()) {
      Logger.debug("cookieXid:" + cookies.get(xidcookieIdKey))
      if (cookies.get(xidcookieIdKey) != null) {
        Logger.debug("cookieXid2:" + cookies.get(xidcookieIdKey).value)
        Logger.debug("cookieXid3:" + Checks.isExistCookie(cookies.get(xidcookieIdKey)))
      }
    }
    Checks.isExistCookie(cookies.get(xidcookieIdKey))
  }

//  /**
//   * cookie/ローカルストレージ(KVS)のどちらでもoptoutされていないかを確認。
//   * @param cookie
//   * @param xid(ローカルストレージ)
//   * @return optout:false optin:true
//   */
//  def isOptIn(cookies: java.util.Map[java.lang.String, play.mvc.Http.Cookie], xid: Option[String]):Boolean = {
//    var cookieResult = isOptInCookie(cookies)
//    Logger.debug("cookieResult:" + cookieResult)
//    var paramResult = true
//    if (Checks.isExist(xid)) {
//      paramResult = OptInOutChecker.isOptIn(xid = xid)
//    }
//    Logger.debug("paramResult:" + paramResult)
//    return cookieResult && paramResult
//  }

  /**
   * cookieにオプトアウトが設定されているかを判定
   * @return false:オプトアウト済み true:オプトイン
   */
  def isOptInCookie(cookies: java.util.Map[java.lang.String, play.mvc.Http.Cookie]): Boolean = {
    if (Logger.isDebugEnabled()) {
      Logger.debug("cookieOptout:" + cookies.get(optoutCookieIdKey))
      if (cookies.get(optoutCookieIdKey) != null) {
        Logger.debug("cookieOptout2:" + cookies.get(optoutCookieIdKey).value)
        Logger.debug("cookieOptout3:" + Checks.isExistCookie(cookies.get(optoutCookieIdKey)))
      }
    }
    var isOptin = false
    if (!Checks.isExistCookie(cookies.get(optoutCookieIdKey))
      || (Checks.isExistCookie(cookies.get(optoutCookieIdKey)) && cookies.get(optoutCookieIdKey).value != optoutCookieOptOutValue)) {
      Logger.debug("isOptin:true")
      isOptin = true
    }
    return isOptin
  }

  /**
   * クッキーを削除する.
   */
  def removeCookie() = {
    cookieKeySuffixs.split(",").foreach(suffix => {
      val cookieIdKey = Play.configuration.getProperty(cookieIdKeyPrefix + suffix)
      val cookieTargetKey = Play.configuration.getProperty(cookieTargetKeyPrefix + suffix)
      Http.Response.current().removeCookie(cookieIdKey)
      Http.Response.current().removeCookie(cookieTargetKey)
    })
  }
  /**
   * click時に紐付くcookieを取得し、KTの取得キーとして採用する
   */
  def getClickId(request: Request): Option[String] = {
    getCookieValue(request.cookies.get(Play.configuration.getProperty("click.cookie.key")))
  }
  
  /**
   * medibaDSPからIDSyncで発行されたcookieのdspuidを取得する
   */
  def getDspUid(request: Request): Option[String] = {
    getCookieValue(request.cookies.get(Play.configuration.getProperty("click.cookie.key")))
  }
  /**
   * 引数のcookieオブジェクトから値を取得する
   */
  def getCookieValue(cookie:Cookie):Option[String]={
    if (Checks.isExistCookie(cookie)) {
      return Some(cookie.value)
    }
    return None
  } 
//  /**
//   * 外部システム送信用Headerを構築する
//   * これ以外に必要な項目がある場合は別途戻り値に追加すること
//   */
//  def makeHeaders(adr: AdRequest,contentType: Option[String]): Map[String, String] = {
//    val ctValue = contentType match {
//      case Some(ct) => contentType.get
//      case None => getHeader("content-type").getOrElse("")
//    }
//    return Map[String, String](
//      "Accept-Language" -> adr.acceptLanguage.getOrElse(""),
//      "Accept" -> adr.accept.getOrElse(""),
//      "User-Agent" -> adr.ua.getOrElse(""),
//      "Accept-Charset" -> adr.acceptCharset.getOrElse(""),
//      "Referer" -> adr.referer.getOrElse(""),
//      "Content-Type" -> ctValue)
//  }
    def getHeader(key: String): Option[String] = {
      var header = Request.current().headers.get(key)
      if (header != null && !header.value().isEmpty) {
        return Some(header.value())
      } else {
        return None
      }
    }

}
