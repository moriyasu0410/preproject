package utils
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import org.apache.commons.codec.binary.Hex
import play.Logger
import play.Play
import java.security.MessageDigest

object Blowfish {
  val cryptType = "Blowfish"

  def makeKey(key:String):String ={
    Codecs.md5(key)
  }
  
  def encrypt(text: String,key:String): String = {
    val sksSpec = new SecretKeySpec(makeKey(key).getBytes("UTF-8"), cryptType)
    var cipher = Cipher.getInstance(cryptType)
    cipher.init(Cipher.ENCRYPT_MODE, sksSpec)
    var encrypted = cipher.doFinal(text.getBytes())
    return String.valueOf(Hex.encodeHex(encrypted))
  }
  def decrypt(text: String,key:String): String = {
    var encrypted: Array[Byte] = null
    try {
      encrypted = Hex.decodeHex(text.toCharArray())
    } catch {
      case e: Throwable => {
        throw e
      }
    }
    val sksSpec = new SecretKeySpec(makeKey(key).getBytes("UTF-8"), cryptType)
    var cipher = Cipher.getInstance(cryptType)
    cipher.init(Cipher.DECRYPT_MODE, sksSpec)
    var decrypted = cipher.doFinal(encrypted)
    return new String(decrypted)
  }
}