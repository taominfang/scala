import scala.collection.mutable

/**
 * Created by mtao60 on 7/9/15.
 */
object collection {

  def main(argv: Array[String]): Unit = {

    mapTest();

    listTest();

    setTest();
  }

  def listTest(): Unit = {
    println("list test")
    mutablelistTest();
    immutablListTest();
  }

  def immutablListTest(): Unit = {
    println("immutablListTest ")
    val a = scala.collection.immutable.List[Int](1, 2, 3);

    a.foreach(i => {
      println(i);
    })
  }


  def mutablelistTest(): Unit = {
    println("mutablelistTest ")
    var a = scala.collection.mutable.MutableList[Option[Int]]()
    a += Some(1)
    a += Some(2)
    a += None
    a += Some(3)


    a.map (i => {
      println (i)
    })


    a.foreach(
      println _
    )

    var b=mutable.MutableList[Int](1,2,3,5);

    var x=b.map(i=>i+1)

    println ("x:"+x);

   // val x = a.flatMap( i => i.getOrElse(0))


    ///println(x)


  }

  def mapTest(): Unit = {

    println("map test")

    val te=Map [String,String]("a" -> "a1");

    te.foreach{case(key,value) => println(key)}


    var t2=te.map{case(key,value)=> (key,value+"22")}

    println(t2)

    var t3=t2.get("a")

    if(t3==None){
      println("cc")
    }

    println(t3+" cc`")




  }

  def setTest(): Unit = {

  }

}
