package utils

import org.joda.time.DateTime
import org.apache.commons.lang.RandomStringUtils


object Random {
  def getPositiveLongNumber():String = {
     var num = scala.util.Random.nextLong()
     if(0 > num){
       return getPositiveLongNumber()
     }
     return num.toString
  } 
  def create = {
    Codecs.sha1(RandomStringUtils.randomAlphanumeric(10) + System.nanoTime().toString())
  }
}