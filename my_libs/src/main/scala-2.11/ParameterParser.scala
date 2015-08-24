/**
 * Created by mtao60 on 7/6/15.
 */
package org.openfly

import scala.collection.mutable
import scala.util.control.Breaks._

case class ParameterParser(var argv:Array[String]=null) {


  var enableStandaloneParameter = false;

  var errorMessages = mutable.MutableList[String]();
  val parameterKeyMaps = scala.collection.mutable.Map[String, Parameter]();

  var standaloneParameters = mutable.MutableList[String]();

  var parameterNameMap = scala.collection.mutable.Map[String, Parameter]();

  var parameters=mutable.MutableList[Parameter]();

  def add(p: Parameter) = {
    p.getKeys.foreach { key: String => parameterKeyMaps.update(key, p) }

    parameterNameMap.update(p.getName, p)

    parameters+=p;

  }

  def enableStandaloneParameters = {
    enableStandaloneParameter = true;
  }

  def usage = {
    val sb = new StringBuilder

    sb.append("")

    parameters.foreach { p => sb.append(p.usage).append("\n") }


    sb.toString()

  }

  def parse(intputArgv:Array[String]):Unit={
    this.argv=intputArgv;
    this.parse();
  }


  def parse():Unit={


    require(this.argv!=null,"argv is not setup")

    var ind = 0;
    breakable {
      while (ind < argv.length) {
        val key = argv(ind);



        if (parameterKeyMaps.contains(key)) {



          val p = parameterKeyMaps(key)

          val size = p.getValueSize;

          if (size > 0) {
            if ((ind + size) >= argv.length) {
              errorMessages += "" + argv(ind) + " need " + size + " following parameters, but there is " + (argv.length - ind);
              break;

            }
            else {
              for (t1 <- 0 until size) {

                ind += 1;

                p.addValue(argv(ind))
              }
              ind += 1;
              p.done
            }
          }
          else {
            p.done
            ind += 1;
          }


        }
        else {
          if (enableStandaloneParameter) {
            standaloneParameters += argv(ind);

          }
          else {
            errorMessages += "Could not identify parameter:" + argv(ind);
          }
          ind += 1;
        }
      }
    }

    //valid

    parameterNameMap.foreach { case (name, pa) => {
      if (pa.isRequired && !pa.prepare) {
        errorMessages += name + " is required"
      }

      if (pa.isPrepare) {
        val depends = pa.getDependents;

        depends.foreach {
          dn => {

            val pa = parameterNameMap.get(dn)
            if (pa == None) {
              errorMessages += name + " dependent on " + dn + " but there is not [" + dn + "] defined !";
            }
            else if (!pa.get.isPrepare) {
              errorMessages += name + " dependent on " + dn + " but  [" + dn + "] is not prepared !";
            }
          }
        };

        pa.getAgainsts().foreach {
          oneAgainst => {
            val pa = parameterNameMap.get(oneAgainst);
            if (pa != None && pa.get.isPrepare) {
              errorMessages += name + " is against " + oneAgainst + " but bath are prepared!";
            }
          }
        };

        //rugn arg validation


        val sb=new StringBuilder();
        if(!pa.argValidate(sb) ){
          errorMessages+=sb.toString();
        }

      }

    }
    };


  }


  def isError() = {
    errorMessages.length > 0
  }

  def getErrorMessage() = {
    errorMessages
  }

  def isSet(key: String): Boolean = {
    val p = parameterNameMap.get(key)
    if (p == None) {
      return false;
    }
    else {
      return p.get.isPrepare();
    }
  }

  def getValues(key: String) = {
    val p = parameterNameMap.get(key)
    if (p == None) {
      None;
    }
    else {
      p.get.getValues();


    }
  }



  def getFirstValue(key: String) = {
    val p = parameterNameMap.get(key)
    if (p == None) {
      None;
    }
    else {
      p.get.getFirstValue();

    }

  }


  def getValueSize(key: String) ={
    val p = parameterNameMap.get(key)
    if (p == None) {
      None;
    }
    else {
      Some(p.get.getValueSize)

    }
  }

}
