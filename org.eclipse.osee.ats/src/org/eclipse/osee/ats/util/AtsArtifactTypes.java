/*
 * Created on Nov 29, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import org.eclipse.osee.framework.core.data.IOseeType;

public enum AtsArtifactTypes implements IOseeType {
   Action("Action", "AAMFDhY_rns71KvX14QA"),
   TeamDefinition("Team Definition", "AAMFDhUrlytusKbaQGAA"),
   ActionableItem("Actionable Item", "AAMFDhW2LmhtRFsVyzwA");
   private final String name;
   private final String guid;

   private AtsArtifactTypes(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return this.name;
   }

   public String getGuid() {
      return guid;
   }
}
