import java.io.{BufferedReader, FileReader}
/**
 * Created by mtao60 on 5/23/16.
 */
object SetCompare {

  def main(args: Array[String]) {
    if(args.length!=2){
      println("We need to input file")
      return
    }
    val f1=new BufferedReader(new FileReader(args(0)))
    val f2=new BufferedReader(new FileReader(args(1)))

    val readLineToSet={(f:BufferedReader,s:collection.mutable.Set[String])=>{
      var line=f.readLine()
      while(line!=null){
        s += line
        line=f.readLine()
      }
      s
    }}

    val s1=(args(0),readLineToSet(f1, collection.mutable.Set[String]()))
    val s2=(args(1),readLineToSet(f2, collection.mutable.Set[String]()))

    val findDiffer={(s1:collection.mutable.Set[String],s2:collection.mutable.Set[String])=>{

      val notIn2=collection.mutable.Set[String]()
      val both=collection.mutable.Set[String]()
      s1.foreach(o=>{
        if(!s2.contains(o)){
          notIn2 += o;
        }
        else{
          both +=o
        }
      })
      (notIn2,both)
    }}

    val (notIn2,both1)=findDiffer(s1._2,s2._2);
    val (notIn1,both2)=findDiffer(s2._2,s1._2);


    println("Elements in both file")
    both2.foreach(println(_))
    println("====================================")
    println("====================================")

    if(notIn2.size>0){
      println(s"Element in ${s1._1} but not in ${s2._1}")
      notIn2.foreach(println(_))
      println("====================================")
      println("====================================")
    }

    if(notIn1.size>0){
      println(s"Element in ${s2._1} but not in ${s1._1}")
      notIn1.foreach(println(_))

    }



  }
}
