/*
 * Created on Aug 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;

public interface IActionableItemFactory {

   IAtsActionableItem createActionableItem(String create, String teamDefName);

   IAtsActionableItem getOrCreate(String guid, String name);

}
