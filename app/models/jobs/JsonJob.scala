package models.jobs

import java.io.InputStream
import java.io.ByteArrayInputStream
import scala.io.Source
import play.Logger
import _root_.models.JsonDeSerializer
import _root_.validators.jobs.AbstractJobValidator

/**
 * <p>
 * JSON形式のデータを取得するジョブのためのModelクラス。<br/>
 * </p>
 * <p>
 * JSON形式のデータを取得するための<br/>
 * オブジェクトをコンストラクタにて生成する。<br/>
 * </p>
 * @author keisuke sekiguchi
 */
case class JsonJob[Model : Manifest](
  /**ジョブの名前*/
  name: String,
  /**JSON形式のデータを取得する処理を実行する関数*/
  getData: () => Option[Source],
  /**JSON形式のデータをmodelクラスのオブジェクトにデシリアイズする関数*/
  deserialize: String => Model,
  /**JSON形式のデータに対して目的の処理を実行する関数*/
  executeFunction: (() => Option[Source], String => Model) => Unit) extends Job {
  override lazy val jobName = name

  /**
   * ジョブを処理する関数。<br/>
   */
  def execute() = {
    //親クラスのジョブ実行関数に対してこのクラス固有のジョブ処理を部分関数として挿入
    super.execute {
      //取得したデータをJSON形式に対応するmodelクラスのオブジェクトに置き換え
      //modelクラス固有のジョブ処理を実行
      executeFunction(getData, deserialize)
    }
  }
}
