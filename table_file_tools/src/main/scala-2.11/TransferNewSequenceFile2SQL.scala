import java.io._

import org.openfly.{Parameter, ParameterParser}

import scala.collection.mutable

/**
 * Created by mtao60 on 9/3/15.
 */
object TransferNewSequenceFile2SQL {

  var isDebug=false

  def debug(s:String): Unit ={
    if(isDebug){
      println(s);
    }
  }

  var elementMaxSize = 100

  def writeToFile(tableName:String,outputDmlFile: FileWriter, data: mutable.MutableList[(String, String, String,String)]) = {
    val sql="INSERT INTO `"+tableName+"` (`product_id`, `sequence_pid`, `score`, `sequence_order`) VALUES ";

    debug(sql)

    outputDmlFile.write(sql)

    var st=0;
    data.foreach(one=>{
      val (pid,sque,score,order) =one
      val s= (if(st!=0) "\n," else "\n") + "('"+pid+"','"+sque+"','"+score+"',"+order+")"
      debug(s)

      outputDmlFile.write(s)
      st+=1
    })

    debug(";\n");
    outputDmlFile.write(";\n");






  }

  def  main (args: Array[String]) {
    val p=ParameterParser(args)

    p.add(new Parameter("new-sequences-file").setRequired(true).setFollowingValueSize(1).addAliasKey("-i"))
    p.add(new Parameter("out-put-sql-path").setRequired(true).setFollowingValueSize(1).addAliasKey("-o"))


    p.add(new Parameter("max-element-size-in-one-query").setDefaultValue("200").setFollowingValueSize(1))

    p.add(new Parameter("advertiser-id").setRequired(true).setFollowingValueSize(1).addAliasKey("-a"))

    p.add(new Parameter("remove-all-previous-sequences").setFollowingValueSize(0).setRequired(false).addAliasKey("--remove-all"))
    p.add(new Parameter("debug").setFollowingValueSize(0).setRequired(false).addAliasKey("-d"))

    p.parse();



    // print help
    if (p.isSet("help")) {
      println("Usage:" + p.usage)
      return
    }

    //is parameter set error quit

    val es = p.getErrorMessage()

    if (es.size > 0) {
      es.foreach(println(_))
      println("error, quite")
      println("Usage:" + p.usage)
      return
    }

    val outPath=new File(p.getFirstValue("out-put-sql-path").get)

    if(!outPath.isDirectory){
      throw new IOException(outPath.getPath+" is not a dir");
    }

    isDebug=p.isSet("debug")

    elementMaxSize=Integer.parseInt(p.getFirstValue("max-element-size-in-one-query").get)





    val input=new BufferedReader(new FileReader(p.getFirstValue("new-sequences-file").get))

    val adId=p.getFirstValue("advertiser-id").get

    val tableName="adv_"+adId;

    val ddlFile=new File(outPath,adId+ "_ddl.sql");
    val dmlFile=new File(outPath,adId+ "_dml.sql")
    val outputDmlFile=new FileWriter(dmlFile)
    val outputDdlFile=new FileWriter(ddlFile)

    //ddl

    var createTable=""
    if(p.isSet("remove-all-previous-sequences")){
      createTable=
        """
          |DROP TABLE IF EXISTS `TABLE_NAME`;
          |
          |CREATE TABLE `TABLE_NAME` (
          |  `product_id` varchar(128) NOT NULL DEFAULT '',
          |  `sequence_pid` varchar(128) NOT NULL DEFAULT '',
          |  `score` varchar(64) NOT NULL,
          |  `sequence_order` int(11) NOT NULL,
          |  `insert_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
          |  KEY `product_id` (`product_id`)
          |) ENGINE=MyISAM DEFAULT CHARSET=utf8;
          |
        """.stripMargin
    }
    else{

        createTable="""
          |
          |
          |CREATE TABLE IF NOT EXISTS `TABLE_NAME` (
          |  `product_id` varchar(128) NOT NULL DEFAULT '',
          |  `sequence_pid` varchar(128) NOT NULL DEFAULT '',
          |  `score` varchar(64) NOT NULL,
          |  `sequence_order` int(11) NOT NULL,
          |  `insert_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
          |  KEY `product_id` (`product_id`)
          |) ENGINE=MyISAM DEFAULT CHARSET=utf8;
          |
        """.stripMargin
    }

    outputDdlFile.write(createTable.replace("TABLE_NAME",tableName))

    val allPids=new mutable.MutableList[String]();
    var t8=new mutable.MutableList[Tuple4[String,String,String,String]]();
    var line=input.readLine();
    while(line!=null){

      val t1=line.split("\t")

      if(t1.length>1){
        val pid=t1(0)
        val t2=t1(1);
        val t3=t2.split(",").toList;

        var c=0

        t3.foreach(one=>{
          val t4=one.split(":")
          if(t4.length==2){
            val sque=t4(0)
            val score=t4(1)

            t8+=new Tuple4(pid,sque,score,""+c);

            if(t8.size>=elementMaxSize){

              writeToFile(tableName,outputDmlFile,t8)

              t8=new mutable.MutableList[Tuple4[String,String,String,String]]();
            }

             c+=1


            allPids+=pid;



          }
        })

      }
      line=input.readLine();
    }//end while
    writeToFile(tableName,outputDmlFile,t8)

    if(allPids.size>0){
      val sb=new mutable.StringBuilder
      allPids.foreach(one=>{
        if(sb.size!=0){
          sb.append(",")
        }
        sb.append("'").append(one).append("'")
      })

      val s="delete from `"+tableName+"` where product_id in  ("+sb.toString()+");\n"
      debug(s)
      outputDdlFile.write(s)
    }


    input.close();
    outputDmlFile.close();

    outputDdlFile.close();

    println("finished, total line processed:"+allPids.size+" products")


    println("To update db: ");
    println("mysql -u pmto  -pPassword -h m-dev-bst-mysql01.advertising.aol.com new_sequences  <"+ddlFile.getPath)
    println("mysql -u pmto  -pPasswrod -h m-dev-bst-mysql01.advertising.aol.com new_sequences  <"+dmlFile.getPath)


  }

}

//scp /Users/mtao60/pub_github/scala/table_file_tools/target/scala-2.11/table_file_tools-assembly-1.0.jar rfeed3:/usr/local/permuto/apps/new_sequence2_db
