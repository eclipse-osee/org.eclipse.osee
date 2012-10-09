/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.impl.internal;

import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionStore;

public class AtsTeamDefinitionStore {

   public static AtsTeamDefinitionStore instance;
   private static IAtsTeamDefinitionStore teamDefStore;

   public void start() {
      AtsTeamDefinitionStore.instance = this;
   }

   public static IAtsTeamDefinitionStore getService() {
      if (instance == null) {
         throw new IllegalStateException("Ats Team Definition Store Service has not been activated");
      }
      return teamDefStore;
   }

   public void setTeamDefinitionStore(IAtsTeamDefinitionStore definitionStore) {
      AtsTeamDefinitionStore.teamDefStore = definitionStore;
   }

}
