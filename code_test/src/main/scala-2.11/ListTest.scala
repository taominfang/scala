/**
 * Created by mtao60 on 7/9/15.
 */
object ListTest {

  def main(argv:Array[String]): Unit ={
    foreachTest();
  }

  def foreachTest(): Unit ={
    var t=new T1().getT1()

    t.foreach(name => println(name))

  }


  def getList()={
     List(1,3,4)
  }



}
