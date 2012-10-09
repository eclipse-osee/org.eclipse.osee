/*
 * Created on Aug 2, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.impl.internal.workflow;

import org.eclipse.osee.ats.api.workflow.IAtsWorkItemStore;

public class AtsWorkItemServiceStore {

   public static AtsWorkItemServiceStore instance;
   private static IAtsWorkItemStore service;

   public void start() {
      AtsWorkItemServiceStore.instance = this;
   }

   public static IAtsWorkItemStore getService() {
      if (instance == null) {
         throw new IllegalStateException("Ats WorkItem Store Service has not been activated");
      }
      return service;
   }

   public void setWorkItemStore(IAtsWorkItemStore service) {
      AtsWorkItemServiceStore.service = service;
   }
}
