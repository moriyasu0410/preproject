package models

import com.codahale.jerkson.Json._
import java.io.InputStream

/**
 * JsonSerializerTrait.
 */
trait JsonSerializer {
  /**
   * JSON文字列化する.
   */
  def serialize(): String = generate(this)
}

/**
 * JsonDeSerializer
 */
trait JsonDeSerializer[Model] {
  /**
   * Json文字列をModelにする.
   */
  def deserialize(json: String)(implicit mf:Manifest[Model]): Model = {
    parse[Model](json)
  }
  /**
   * InputStream(複数行)をList[Model]にする.
   */
  def deserializeStream(json: InputStream)(implicit mf:Manifest[Model]): List[Model] = {
    stream[Model](json).toList
  }

}
