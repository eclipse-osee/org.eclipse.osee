/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.impl.internal;

import org.eclipse.osee.ats.workdef.api.IAtsWorkDefinitionStore;

public class AtsWorkDefinitionStore {

   public static AtsWorkDefinitionStore instance;
   private static IAtsWorkDefinitionStore definitionStore;

   public void start() {
      AtsWorkDefinitionStore.instance = this;
   }

   public static IAtsWorkDefinitionStore getService() {
      if (instance == null) {
         throw new IllegalStateException("Service has not been activated");
      }
      return definitionStore;
   }

   public void setWorkDefinitionStore(IAtsWorkDefinitionStore definitionStore) {
      AtsWorkDefinitionStore.definitionStore = definitionStore;
   }
}
