/**
 * Created by mtao60 on 2/25/16.
 */
object ShellHelp1 {

  def main(args: Array[String]) {
    val pre1=if(args.length>0) args(0) else ""
    val after1=if(args.length>1) args(1) else ""

    println("#!/bin/sh");
    (1 to 30).foreach(i=>{
      //println(s"if [ \$# == ${i} ]")
      if(i==1) println(s"if [ $$# == ${i} ]")
      else println(s"elif [ $$# == ${i} ]")
      println("then")
      //println(s"\techo ${i}")
      print(s"\t${pre1} $${1} ${after1}")
      (2 to i).foreach(j=>{
        print(s" $${${j}}")
      })
      println

      println()
    })
    println()
    println("fi")
  }
}
