/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow;

import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;

public class AtsWorkItemService {

   private static AtsWorkItemService instance;
   private IAtsWorkItemService service;

   public static IAtsWorkItemService get() {
      if (instance == null) {
         throw new IllegalStateException("ATS Work Item Service has not been activated");
      }
      return instance.service;
   }

   public void setWorkItemService(IAtsWorkItemService service) {
      this.service = service;
   }

   public void start() {
      instance = this;
   }

}
