/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.column;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.AtsApiService;

/**
 * Provides for rollup of actionable items
 *
 * @author Donald G. Dunne
 */
public class ActionableItemsColumn extends AbstractServicesColumn {

   public ActionableItemsColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      return getActionableItemsStr(atsObject);
   }

   public String getActionableItemsStr(IAtsObject atsObject) {
      StringBuilder sb = new StringBuilder();
      for (IAtsActionableItem ai : getActionableItems(atsObject)) {
         sb.append(ai.getName());
         sb.append(", ");
      }
      return sb.toString().replace(", $", "");
   }

   public static Collection<IAtsActionableItem> getActionableItems(IAtsObject atsObject) {
      if (atsObject instanceof IAtsTeamWorkflow) {
         return AtsApiService.get().getActionableItemService().getActionableItems((IAtsTeamWorkflow) atsObject);
      }
      return Collections.emptyList();
   }
}
