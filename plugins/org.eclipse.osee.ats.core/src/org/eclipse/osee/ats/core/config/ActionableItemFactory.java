/*
 * Created on Jun 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.core.config.impl.ActionableItem;
import org.eclipse.osee.ats.core.model.IAtsActionableItem;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;

public class ActionableItemFactory {

   public static IAtsActionableItem createActionableItem(String guid, String title) {
      return createActionableItem(title, guid, HumanReadableId.generate());
   }

   public static IAtsActionableItem createActionableItem(String title, String guid, String humanReadableId) {
      if (guid == null) {
         throw new IllegalArgumentException("guid can not be null");
      }
      IAtsActionableItem ai = new ActionableItem(title, guid, humanReadableId);
      AtsConfigCache.cache(ai);
      return ai;
   }

   public static IAtsActionableItem getOrCreate(String guid, String name) {
      IAtsActionableItem ai = AtsConfigCache.getSoleByGuid(guid, IAtsActionableItem.class);
      if (ai == null) {
         ai = createActionableItem(guid, name);
      }
      return ai;
   }
}
