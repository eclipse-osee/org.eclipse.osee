/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.version.IAtsVersionService;

public class AtsVersionService {

   private static AtsVersionService instance;
   private IAtsVersionService service;

   public static IAtsVersionService get() {
      if (instance == null) {
         throw new IllegalStateException("ATS Version Service has not been activated");
      }
      return instance.service;
   }

   public void setVersionService(IAtsVersionService service) {
      this.service = service;
   }

   public void start() {
      instance = this;
   }

}
