/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.impl.internal;

import org.eclipse.osee.ats.api.version.IAtsVersionStore;

public class AtsVersionStore {

   public static AtsVersionStore instance;
   private static IAtsVersionStore versionStore;

   public void start() {
      AtsVersionStore.instance = this;
   }

   public static IAtsVersionStore getService() {
      if (instance == null) {
         throw new IllegalStateException("Ats Version Store Service has not been activated");
      }
      return versionStore;
   }

   public void setVersionStore(IAtsVersionStore definitionStore) {
      AtsVersionStore.versionStore = definitionStore;
   }

}
