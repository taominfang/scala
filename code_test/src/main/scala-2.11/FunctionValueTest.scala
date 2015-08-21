import scala.collection.mutable

/**
 * Created by mtao60 on 7/15/15.
 */
object FunctionValueTest {

  def main(arg:Array[String]): Unit ={
    println ("start")


    var f:(Int)=>Int=null;

    f=(v)=>{
      return v+5
    }

    println(f(3));



    println ("end")

  }

}
