package daos.files
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import org.apache.commons.lang.NotImplementedException
import play.Logger
import play.exceptions.UnexpectedException
import play.libs.IO
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import utils.Dates

/**
 * ファイルアクセス
 * 対象ディレクトリ毎に当該traitをextendsしてDaoを作成し、dirを定義すること
 */
trait FileDao {

  /**
   * プロジェクトルートからのファイル格納場所への相対パスもしくは、絶対パスを指定
   * @return ディレクトリパス（ファイル名を含まず"/"で終わる）
   */
  def dir(): String = {
    throw new NotImplementedException() //各クラスで定義すること
  }

  /**
   * 定義されたディレクトリ配下の指定ファイルを読み込み、文字列で返却する
   * @param filename ファイル名
   * @return ファイル内容
   */
  def readFileAsString(fileName: String): String = {
    var file = new File(dir + fileName)
    try {
      IO.readContentAsString(file)
    } catch {
      case e => {
        Logger.error(e, "ファイル読み込みに失敗しました:" + dir + fileName)
        throw e
      }
    }
  }

  /**
   * 定義されたファイルへ第二引数の内容をファイル出力する。
   * ※ファイルパスが存在しない場合、ディレクトリを作成する。（初回出力時のみ）
   */
  def writeFileAsString(fileName:String,contents:String) = {
    var dirFile = new File(dir)
    if (!dirFile.exists()){
Logger.debug("★ディレクトリ作成："+dir)
      dirFile.mkdirs()
    }
    var file = new File(dir + fileName )
    try {
      IO.write(new ByteArrayInputStream(contents.getBytes()),file)
    }
  }
}