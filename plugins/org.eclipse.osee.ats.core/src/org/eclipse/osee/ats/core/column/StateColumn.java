/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class StateColumn extends AbstractServicesColumn {

   public StateColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      if (atsObject instanceof IAtsWorkItem) {
         String isBlocked =
            atsApi.getAttributeResolver().getSoleAttributeValue(atsObject, AtsAttributeTypes.BlockedReason, "");
         if (Strings.isValid(isBlocked)) {
            return ((IAtsWorkItem) atsObject).getStateMgr().getCurrentStateName() + " (Blocked)";
         } else {
            return ((IAtsWorkItem) atsObject).getStateMgr().getCurrentStateName();
         }
      } else if (atsObject instanceof IAtsAction) {
         Set<String> strs = new HashSet<>();
         for (IAtsTeamWorkflow team : ((IAtsAction) atsObject).getTeamWorkflows()) {
            strs.add(team.getStateMgr().getCurrentStateName());
         }
         return org.eclipse.osee.framework.jdk.core.util.Collections.toString(";", strs);
      }
      return "";
   }
}
