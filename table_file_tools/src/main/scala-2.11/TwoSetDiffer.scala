import java.io.{BufferedReader, File, FileReader}

import scala.collection.mutable

/**
 * Created by mtao60 on 12/3/15.
 */
object TwoSetDiffer {

  def readFileIntoSet(f1: File) = {
    val s = mutable.Set[String]()
    val br = new BufferedReader(new FileReader(f1));
    var line = br.readLine()
    while (line != null) {
      s.add(line)
      line = br.readLine()
    }
    br.close()
    s
  }

  def main(argv: Array[String]): Unit = {
    if (argv.length != 2) {
      println("Need tow set files")
      return
    }
    val f1 = new File(argv(0))
    val f2 = new File(argv(1))
    if (!f1.isFile) {
      println(f1.getPath + " is not a file")
      return
    }
    if (!f2.isFile) {
      println(f2.getPath + " is not a file")
      return
    }

    val seta = readFileIntoSet(f1);
    val setb = readFileIntoSet(f2);

    val onlyIna = mutable.Set[String]();
    val onlyInb = mutable.Set[String]();
    val inBoth = mutable.Set[String]();

    seta.foreach(one => {
      if (setb.contains(one)) {
        inBoth.add(one)
      }
      else {
        onlyIna.add(one)
      }
    })

    setb.foreach(one => {
      if (!seta.contains(one)) {
        onlyInb.add(one)
      }
    })

    println("####################### in both #############")
    inBoth.foreach(println(_))
    println();
    println("####################### only in "+f1.getPath+" #############")
    onlyIna.foreach(println(_))
    println();
    println("####################### only in "+f2.getPath+" #############")
    onlyInb.foreach(println(_))

  }
}
