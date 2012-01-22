package com.crashnote.test.util

import java.util.Properties
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

trait FactoryUtil {

    type javaEnum[T] = java.util.Enumeration[T]

    def toEnum[T](seq: Seq[T]): java.util.Enumeration[T] =
        seq.iterator

    def toProps(map: Map[String, String]) = {
        val p = new Properties()
        map.foreach(kv => p.setProperty(kv._1, kv._2))
        p
    }
}