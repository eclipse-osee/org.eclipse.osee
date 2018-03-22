/*
 * Created on Mar 22, 2018
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.disposition.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CiTestPoint")
public class CiTestPoint {

   private String passing;
   private String failing;

   public String getPassing() {
      return passing;
   }

   public void setPassing(String passing) {
      this.passing = passing;
   }

   public String getFailing() {
      return failing;
   }

   public void setFailing(String failing) {
      this.failing = failing;
   }

}
