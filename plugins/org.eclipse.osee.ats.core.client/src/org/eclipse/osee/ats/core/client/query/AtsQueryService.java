/*
 * Created on Aug 2, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.query;

import org.eclipse.osee.ats.api.query.IAtsQueryService;

public class AtsQueryService {

   private static AtsQueryService instance;
   private static IAtsQueryService service;

   public void start() {
      AtsQueryService.instance = this;
   }

   public static IAtsQueryService getService() {
      if (instance == null) {
         throw new IllegalStateException("Ats Query Service has not been activated");
      }
      return service;
   }

   public void setQueryService(IAtsQueryService service) {
      AtsQueryService.service = service;
   }
}
