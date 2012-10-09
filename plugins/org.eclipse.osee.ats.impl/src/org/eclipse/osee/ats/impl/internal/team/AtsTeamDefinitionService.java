/*
 * Created on Aug 2, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.impl.internal.team;

import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;

public class AtsTeamDefinitionService {

   public static AtsTeamDefinitionService instance;
   private static IAtsTeamDefinitionService service;

   public void start() {
      AtsTeamDefinitionService.instance = this;
   }

   public static IAtsTeamDefinitionService getService() {
      if (instance == null) {
         throw new IllegalStateException("Ats Team Definition Service has not been activated");
      }
      return service;
   }

   public void setTeamDefinitionService(IAtsTeamDefinitionService service) {
      AtsTeamDefinitionService.service = service;
   }
}
