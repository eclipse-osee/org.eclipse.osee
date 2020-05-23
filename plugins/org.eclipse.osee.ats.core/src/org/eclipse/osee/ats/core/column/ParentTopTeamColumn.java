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

import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemProvider;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Provides for rollup of parent workflow's top team definition
 *
 * @author Donald G. Dunne
 */
public final class ParentTopTeamColumn {

   private ParentTopTeamColumn() {
      // do nothing
   }

   public static String getColumnText(IAtsObject atsObject) {
      String result = "";
      IAtsTeamDefinition teamDef = null;
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         IAtsTeamWorkflow teamArt = workItem.getParentTeamWorkflow();
         if (teamArt != null) {
            teamDef = teamArt.getTeamDefinition();
         }
      }
      result = getTopTeamDefName(teamDef);
      if (!Strings.isValid(result) && atsObject instanceof IAtsActionableItemProvider) {
         IAtsActionableItemProvider provider = (IAtsActionableItemProvider) atsObject;
         Set<IAtsActionableItem> actionableItems = provider.getActionableItems();
         if (!actionableItems.isEmpty()) {
            teamDef = actionableItems.iterator().next().getTeamDefinition();
            if (teamDef == null) {
               IAtsActionableItem ai = actionableItems.iterator().next();
               teamDef = ai.getAtsApi().getActionableItemService().getTeamDefinitionInherited(ai);
            }
            result = getTopTeamDefName(teamDef);
         }
      }
      return result;
   }

   protected static String getTopTeamDefName(IAtsTeamDefinition teamDef) {
      String result = "";
      if (teamDef != null) {
         IAtsTeamDefinition teamDefinitionHoldingVersions =
            teamDef.getAtsApi().getTeamDefinitionService().getTeamDefHoldingVersions(teamDef);
         if (teamDefinitionHoldingVersions != null) {
            teamDef = teamDefinitionHoldingVersions;
         }
         result = teamDef.getName();
      }
      return result;
   }
}
