/*
 * Created on Mar 22, 2018
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.disposition.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CiTestPoint")
public class CiTestPoint {

   private String pass;
   private String fail;

   public String getPass() {
      return pass;
   }

   public void setPass(String pass) {
      this.pass = pass;
   }

   public String getFail() {
      return fail;
   }

   public void setFail(String fail) {
      this.fail = fail;
   }

}
