/*
 * Created on Mar 22, 2018
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.disposition.model;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

@XmlRootElement(name = "CiItemData")
public class CiItemData implements Identifiable<String> {

   private String scriptName;
   private String ciSet;
   private CiTestPoint testPoints;
   private List<DispoAnnotationData> annotations;

   @Override
   public String getGuid() {
      return null;
   }

   public String getScriptName() {
      return scriptName;
   }

   public void setScriptName(String scriptName) {
      this.scriptName = scriptName;
   }

   public CiTestPoint getTestPoints() {
      return testPoints;
   }

   public void setTestPoints(CiTestPoint testPoints) {
      this.testPoints = testPoints;
   }

   public List<DispoAnnotationData> getAnnotations() {
      return annotations;
   }

   public void setAnnotations(List<DispoAnnotationData> annotations) {
      this.annotations = annotations;
   }

   public String getCiSet() {
      return ciSet;
   }

   public void setCiSet(String ciSet) {
      this.ciSet = ciSet;
   }

}
