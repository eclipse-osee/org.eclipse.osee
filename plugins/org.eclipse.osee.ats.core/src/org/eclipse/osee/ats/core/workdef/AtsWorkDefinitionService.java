/*
 * Created on Jun 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.workdef.api.IAtsWorkDefinitionService;

public class AtsWorkDefinitionService {

   private static AtsWorkDefinitionService instance;
   private IAtsWorkDefinitionService service;

   public static IAtsWorkDefinitionService getService() {
      if (instance == null) {
         throw new IllegalStateException("Service has not been activated");
      }
      return instance.service;
   }

   public void setWorkDefinitionService(IAtsWorkDefinitionService service) {
      this.service = service;
   }

   public void start() {
      instance = this;
   }
}
