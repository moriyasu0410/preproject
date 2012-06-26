package exchanges
//import models.AdRequest
//import models.Exchange
//import models.JsonDeSerializer
//import models.exchanges._
//import play._

/**
 * ADNW種類によりADNWよりクリック時に呼び出されるURLを取得しOpenXより取得したコンテンツに埋め込む
 * HtmlParserより呼び出される
 */
object ExchangeFunction {
  /**
   * OpenXより取得したコンテンツからAdnwより取得した内容に変換したコンテンツを返却する関数を生成する
   * @param adconnect
   * @param adr
   * @return	String=>Stringの関数を返却
   */
//	def apply(adconnect:String, adr:AdRequest): (String => String) = {
//	  val adnw = Exchange.deserialize(adconnect)
//	  adnw.adnwname match {
//	    	case Some("Searchteria")
//	    		=> val st = SearchTeria.deserialize(adconnect);st(adr)
//	    	case Some("LeadBolt")
//	    		=> val lb = LeadBolt.deserialize(adconnect);lb(adr)
//	    	case Some("Smaato")
//	    		=> val sm = Smaato.deserialize(adconnect);sm(adr)
//	    	case Some("Tapit")
//	    		=> val tp = Tapit.deserialize(adconnect);tp(adr)
//	    	case Some("Millennial")
//	    		=> val ml = Millennial.deserialize(adconnect);ml(adr)
//	    	case Some("Komlimobile")
//	    		=> val km = Komlimobile.deserialize(adconnect);km(adr)
//	    	case Some("MicroAd")
//	    		=> val ma = MicroAd.deserialize(adconnect);ma(adr)
//	    	case Some("StrikeAd")
//	    		=> val sa = StrikeAd.deserialize(adconnect);sa(adr)
//	    		// ADNWが増えた場合ここにケースを追加する。
//	    		// 外部アドネットワーク名称のマスタhttp://mstrw02.mediba.co.jp/redmine/projects/sdkadc/wiki/%E5%A4%96%E9%83%A8ADNW%E3%81%AE%E5%90%8D%E5%89%8D%E3%83%9E%E3%82%B9%E3%82%BF
//	    		// case 外部アドネットワーク名称
//	    		// アドネットワークがまだ準備されていない場合そのままSDKへ返却する
//	    	case _ => {
//	    		// Unknown adnw name
//	    		Logger.warn("外部ADNW %s はサービス提供外です。".format(adnw.adnwname.getOrElse("")))
//	    		a:String => a }
//	  }
//	}
}