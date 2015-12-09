package org.openfly
import scala.collection.mutable

/**
 * Created by mtao60 on 7/6/15.
 */
case class Parameter(name: String) {



  var longKey = "--" + name;
  var aliasKeys = mutable.MutableList[String]();

  var valueSize = 0;

  var userSet = false;

  var required = true;

  var dependents = mutable.MutableList[String]();
  var againsts = mutable.MutableList[String]();

  var argValidtions=mutable.Map[Int,Parameter=>String]();

  var prepare = false;

  var desc:String=_

  var values = mutable.MutableList[String]();
  var defaultValues = mutable.MutableList[String]();


  def setFollowingValueSize(size: Int) = {
    valueSize = size;
    this
  }

  def setDefaultValue(dv: String) = {
    defaultValues = mutable.MutableList[String]();
    defaultValues += dv;
    valueSize = 1;
    prepare = true;
    this
  }



  def setDefaultValues(dvs: Array[String]) ={
    defaultValues = mutable.MutableList[String]();
    dvs.foreach(one => defaultValues += one);
    prepare = true;
    valueSize = dvs.size
    this
  }

  def setLongKey(key: String) = {
    longKey = key;
    this
  }

  def addAliasKey(key: String) = {
    aliasKeys += key;
    this
  }

  def setRequired(r: Boolean) = {
    required = r;

    this;
  }


  def setDependents(ds: Array[String]) = {
    ds.foreach(one => dependents += one);

    this
  }

  //for example: enable-cc is against disable-cc
  def setAgainsts(ds: Array[String]) = {
    ds.foreach(one => againsts += one);

    this
  }


  def getValueSize = {
    valueSize
  }

  def done: Unit = {
    userSet = true;
    prepare = true;
  }

  def setArgValidation(ind:Int,vFunction:(Parameter)=>String)={
    argValidtions.update(ind,vFunction);
    this;
  }

  def argValidate(sb:mutable.StringBuilder): Boolean ={

    if(!this.prepare){
      sb.append(name+" No Args prepared!");
      return false;
    }

    var re=true;

    for(i<- 1 to valueSize){
      val vf=this.argValidtions.get(i).getOrElse(null);
      if(vf!=null){

        val em=vf(this);

        if( em != null){
          sb.append(em).append("\n");
          re=false;
        }
      }
    }


    return re;

  }




  def getKeys = {

    val re = new Array[String](1 + aliasKeys.length)

    re(0) = longKey;

    var c = 1;

    aliasKeys.foreach(

      one => {
        re(c) = one;
        c += 1
      }
    );

    re;
  }

  def setPrepare(p: Boolean): Unit = {
    prepare = p
  }

  def isPrepare() = {
    prepare
  }

  def getName = {
    name
  }

  def getDependents = {

    dependents
  }

  def setDesc(d:String)={
    desc=d;
    this
  }


  def isRequired: Boolean = {
    required
  }

  def isUserSet: Boolean = {
    userSet
  }


  def usage = {
    val sb = new StringBuilder

    sb.append(longKey);
    for(i<- 1 to valueSize){
      sb.append(" arg").append(i).append("");
    }
    sb.append("\n");

    if(desc!=null){
      sb.append(desc).append("\n");
    }

    if(aliasKeys.size>0){
      if(aliasKeys.size > 1){
        sb.append("Aliases:")
      }
      else{
        sb.append("Alias:")
      }
      aliasKeys.foreach(one => sb.append(" ").append(one))

      sb.append("\n")
    }

    sb.append("Required:");

    if(this.required){
      sb.append("TRUE");
    }
    else{
      sb.append("FALSE");
    }
    sb.append("\n");


    if(this.defaultValues.size>0){
      for(i<- 1 to defaultValues.size){
        sb.append("arg").append(i).append (" Default value:").append(defaultValues.get(i-1).get).append("\n");
      }
    }

    sb.toString()
  }

  def addValue(v: String): Unit = {
    values += v;
    userSet=true;
  }


  def getAgainsts() = {
    againsts;
  }

  def getValues()={
    if(!prepare){
      None;
    }
    else{
      if(userSet){
        Some(values.toArray)
      }
      else{
        Some(defaultValues.toArray);
      }
    }

  }

  def getFirstValue() ={
    if(!prepare){
      None;
    }
    else{
      var t=defaultValues;
      if(userSet){
        t=values;
      }
      t.get(0);
    }
  }

  def getValueAt(index:Int):Option[String]  ={
    if(!isPrepare()){
      return  None;
    }
    val vs=getValues().getOrElse(null);
    if(vs==null){
      return None
    }

    if(index>=vs.length){
      return None;
    }

    return Some(vs(index));



  }

  def argValidateIsNumber(argInd:Int):String={

    val s=getValueAt(argInd-1).getOrElse(null);

    if(s==null){
      return "Error, Could not get arg at:"+argInd+ " for " +name
    }

    var re:String=null;

    try {
      java.lang.Double.parseDouble(s)
    }
    catch{
      case e:Exception=>{
        re="Arg"+argInd+ " for " +name+" requires a number, but "+s+" is not a nunmber";
      }
    }


    return re;

  }



  def argValidateIsInteger(argInd:Int):String={

    val s=getValueAt(argInd-1).getOrElse(null);

    if(s==null){
       return "Error, Could not get arg at:"+argInd+ " for " +name
    }

    var re:String=null;

    try {
      java.lang.Integer.parseInt(s)
    }
    catch{
      case e:Exception=>{
        re="Arg"+argInd+ " for " +name+" requires an integer, but "+s+" is not an integer";
      }
    }


    return re;

  }

  def argValidateIsNumberRange(argInd:Int,largerAndEquale:Double,lessAndEqunale:Double): String ={
    val s=getValueAt(argInd-1).getOrElse(null);

    if(s==null){
      return "Error, Could not get arg at:"+argInd+ " for " +name
    }

    var re:String=null;

    try {
      val v=java.lang.Double.parseDouble(s)

      if(v<largerAndEquale){
        re="Arg"+argInd+ " for " +name+" is less than "+largerAndEquale;
      }
      else if(v>lessAndEqunale){
        re="Arg"+argInd+ " for " +name+" is larger than "+lessAndEqunale;
      }

    }
    catch{
      case e:Exception=>{
        re="Arg"+argInd+ " for " +name+" requires a number, but "+s+" is not a nunmber";
      }
    }

    return re;
  }

}
