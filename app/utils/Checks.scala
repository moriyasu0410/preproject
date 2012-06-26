package utils

import play.mvc.Http.Cookie
import play.mvc.Http.Header

/**
 * <p>
 * 引数として与えられた変数に対してチェック処理を行うメソッドを集約したUtilクラス。<br/>
 * </p>
 * <p>
 * 引数として与えられた変数に対してチェック処理を行うメソッドをこのクラスに集約している。<br/>
 * </p>
 * @author keisuke sekiguchi
 */
object Checks {
  /**
   * 引数がnull,空文字でないことをチェックする関数。<br/>
   * <br/>
   * @param チェック対象の文字列<br/>
   * @return 引数がnull,空文字でなければtrue
   */
  def isExist(evalTarget: String): Boolean = {
    evalTarget != null && !evalTarget.isEmpty()
  }

  /**
   * 引数がnull,None,保持している値がnull、空文字でないことをチェックする関数。<br/>
   * <br/>
   * @param チェック対象の文字列を格納しているOptionオブジェクト<br/>
   * @return 引数がnull,None,保持している値がnull、空文字でなければtrue
   */
  def isExist(evalTarget: Option[String]): Boolean = {
    evalTarget != null && evalTarget != None && isExist(evalTarget.get)
  }

  /**
   * 引数がnull,保持している値がnull、空文字でないことをチェックする関数。<br/>
   * <br/>
   * @param チェック対象のHeaderオブジェクト<br/>
   * @return 引数がnull,保持している値がnull、空文字でなければtrue
   */
  def isExistHeader(evalTarget: Header): Boolean = {
    evalTarget != null && evalTarget.value != null && !evalTarget.value.isEmpty()
  }

  /**
   * 引数がnull,None,保持している値がnull、空文字でないことをチェックする関数。<br/>
   * <br/>
   * @param チェック対象のHeaderオブジェクトを格納しているOptionオブジェクト<br/>
   * @return 引数がnull,None,保持している値がnull、空文字でなければtrue
   */
  def isExistHeader(evalTarget: Option[Header]): Boolean = {
    evalTarget != null && evalTarget != None && isExistHeader(evalTarget.get)
  }

  /**
   * 引数がnull,保持している値がnull、空文字でないことをチェックする関数。<br/>
   * <br/>
   * @param チェック対象のCookieオブジェクトを格納しているCookieオブジェクト<br/>
   * @return 引数がnull,保持している値がnull、空文字でなければtrue
   */
  def isExistCookie(evalTarget: Cookie): Boolean = {
    evalTarget != null && evalTarget.value != null && !evalTarget.value.isEmpty()
  }

  /**
   * 引数がnull,None,保持している値がnull、空文字でないことをチェックする関数。<br/>
   * <br/>
   * @param チェック対象のCookieオブジェクトを格納しているOptionオブジェクト<br/>
   * @return 引数がnull,None,保持している値がnull、空文字でなければtrue
   */
  def isExistCookie(evalTarget: Option[Cookie]): Boolean = {
    evalTarget != null && evalTarget != None && isExistCookie(evalTarget.get)
  }

  /**
   * 引数がnull,非Noneもしくは保持している値がnull、空文字でないことをチェックする関数。<br/>
   * <br/>
   * @param チェック対象の文字列を格納しているOptionオブジェクト<br/>
   * @return IPがnull,非Noneもしくは保持している値がnull、空文字でなければtrue
   */
  def isExistOrNone(evalTarget: Option[String]): Boolean = {
    evalTarget != null && (evalTarget == None || (evalTarget.get != null && !evalTarget.get.isEmpty()))
  }
  /**
   * 引数の配列が空、もしくは空文字のみで構成された配列かをチェックする関数。
   * @param チェック対象Array
   * @return 空、もしくは空文字のみで構成された配列の場合true
   */
  def isEmpty(array:Array[String]):Boolean = {
    var allEmpty = true
    array.foreach{id =>
      if(!id.isEmpty()){
        allEmpty = false
      }
    }
    return allEmpty
  }

}
