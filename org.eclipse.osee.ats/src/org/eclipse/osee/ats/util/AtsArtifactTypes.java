/*
 * Created on Nov 29, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import org.eclipse.osee.framework.core.data.IArtifactType;

public enum AtsArtifactTypes implements IArtifactType {
   Action("Action", "AAMFDhY_rns71KvX14QA"),
   ActionableItem("Actionable Item", "AAMFDhW2LmhtRFsVyzwA"),
   DecisionReview("Decision Review", "AAMFDhfrdR7BGTL7H_wA"),
   PeerToPeerReview("PeerToPeer Review", "AAMFDhh_300dpgmNtRAA"),
   Task("Task", "AAMFDhbTAAB6h+06fuAA"),
   TeamDefinition("Team Definition", "AAMFDhUrlytusKbaQGAA"),
   TeamWorkflow("Team Workflow", "AAMFDhSiF2OD+wiUqugA"),
   Version("Version", "AAMFDhder0oETnv14xQA");

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
