package utils

import play.libs.Codec

object Codecs {

  def sha1(text: String): String = {
    if (text == null || text.isEmpty()) {
      return ""
    }
    Codec.hexSHA1(text)
  }
  def md5(text: String): String = {
    if (text == null || text.isEmpty()) {
      return ""
    }
    Codec.hexMD5(text)
  }
  def encBase64(text: Array[Byte]): String = {
    if (text == null || text.size == 0) {
      return ""
    }
    Codec.encodeBASE64(text)
  }
  def decBase64(text: String): Array[Byte] = {
    if (text == null || text.isEmpty) {
      return Array[Byte]()
    }
    Codec.decodeBASE64(text)
  }

  def asciiToNative(input: String): String = {
    if (input == null) {
      return input
    }
    var buffer = new StringBuffer(input.length());
    var precedingBackslash = false;
    var i = 0
    val end = input.length()
    while (i < end) {
      var t = input.charAt(i)
      if (precedingBackslash) {
        t = t match {
          case 'f' => '\f'
          case 'n' => '\n'
          case 'r' => '\r'
          case 't' => '\t'
          case 'u' => {
            val hex = input.substring(i + 1, i + 5)
            i = i + 4
            Integer.parseInt(hex, 16).asInstanceOf[Char]
          }
          case _ => t
        }
        precedingBackslash = false;
        buffer.append(t);
        i = i + 1
      } else {
        precedingBackslash = (t == '\\');
        if (!precedingBackslash) {
          buffer.append(t);
        }
        i = i + 1
      }
    }
    return buffer.toString();
  }

}
