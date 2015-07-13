import scala.collection.mutable

/**
 * Created by mtao60 on 7/6/15.
 */
class Parameter(name: String) {

  var longKey = "--" + name;
  var aliasKeys = mutable.MutableList[String]();

  var valueSize = 0;

  var userSet = false;

  var required = true;

  var dependents = mutable.MutableList[String]();
  var againsts = mutable.MutableList[String]();


  var prepare = false;

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


  def isRequired: Boolean = {
    required
  }

  def isUserSet: Boolean = {
    userSet
  }


  def usage = {
    val sb = new StringBuilder

    sb.append("--").append(name)

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

}
