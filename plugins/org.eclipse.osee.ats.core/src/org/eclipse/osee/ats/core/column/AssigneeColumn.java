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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsImplementerService;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.Action;
import org.eclipse.osee.ats.core.workflow.AtsImplementersService;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Return current list of assignees sorted if in Working state or string of implementors surrounded by ()
 *
 * @author Donald G. Dunne
 */
public class AssigneeColumn extends AbstractServicesColumn {

   private static IAtsImplementerService implementStrProvider;

   public AssigneeColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      return getAssigneeStr(atsObject);
   }

   public String getAssigneeStr(IAtsObject atsObject) {
      return getAssigneeStrr(atsObject);
   }

   public static String getAssigneeStrr(IAtsObject atsObject) {
      if (atsObject instanceof Action) {
         // ensure consistent order by using lists
         List<AtsUser> pocs = new ArrayList<>();
         List<AtsUser> implementers = new ArrayList<>();
         for (IAtsWorkItem workItem : ((Action) atsObject).getTeamWorkflows()) {
            StateType stateType = workItem.getCurrentStateType();
            if (stateType != null) {
               if (stateType.isCompletedOrCancelled()) {
                  for (AtsUser user : workItem.getImplementers()) {
                     if (!implementers.contains(user)) {
                        implementers.add(user);
                     }
                  }
               } else {
                  for (AtsUser user : workItem.getAssignees()) {
                     if (!pocs.contains(user)) {
                        pocs.add(user);
                     }
                  }
               }
            }
         }
         Collections.sort(pocs);
         Collections.sort(implementers);
         return AtsObjects.toString("; ",
            pocs) + (implementers.isEmpty() ? "" : "(" + AtsObjects.toString("; ", implementers) + ")");
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         if (workItem.isCompletedOrCancelled()) {
            String implementers = getImplementersStringProvider().getImplementersStr(workItem);
            if (Strings.isValid(implementers)) {
               return "(" + implementers + ")";
            }
         }
         return workItem.getAssigneesStr();
      }
      return "";
   }

   private static IAtsImplementerService getImplementersStringProvider() {
      if (implementStrProvider == null) {
         implementStrProvider = new AtsImplementersService();
      }
      return implementStrProvider;
   }
}
