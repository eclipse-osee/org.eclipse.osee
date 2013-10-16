/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.HasActions;
import org.eclipse.osee.ats.api.workflow.HasAssignees;
import org.eclipse.osee.ats.api.workflow.HasWorkData;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Return current list of assignees sorted if in Working state or string of implementors surrounded by ()
 *
 * @author Donald G. Dunne
 */
public class AssigneeColumn {

   public static AssigneeColumn instance = new AssigneeColumn(ImplementersColumn.instance);
   private final ImplementersStringProvider implementStrProvider;

   public AssigneeColumn(ImplementersStringProvider implementStrProvider) {
      this.implementStrProvider = implementStrProvider;
   }

   public String getAssigneeStr(Object object) throws OseeCoreException {
      if (object instanceof HasActions) {
         HasActions hasActions = (HasActions) object;
         // ensure consistent order by using lists
         List<IAtsUser> pocs = new ArrayList<IAtsUser>();
         List<IAtsUser> implementers = new ArrayList<IAtsUser>();
         for (IAtsWorkItem action : hasActions.getActions()) {
            if (action.getWorkData().isCompletedOrCancelled()) {
               for (IAtsUser user : action.getImplementers()) {
                  if (!implementers.contains(user)) {
                     implementers.add(user);
                  }
               }
            } else {
               for (IAtsUser user : action.getAssignees()) {
                  if (!pocs.contains(user)) {
                     pocs.add(user);
                  }
               }
            }
         }
         Collections.sort(pocs);
         Collections.sort(implementers);
         return AtsObjects.toString("; ", pocs) + (implementers.isEmpty() ? "" : "(" + AtsObjects.toString("; ",
            implementers) + ")");
      } else if (object instanceof HasWorkData) {
         HasWorkData workData = (HasWorkData) object;
         if (workData.getWorkData().isCompletedOrCancelled()) {
            String implementers = implementStrProvider.getImplementersStr(workData);
            if (Strings.isValid(implementers)) {
               return "(" + implementers + ")";
            }
         }
         if (object instanceof HasAssignees) {
            return AtsObjects.toString("; ", ((HasAssignees) object).getAssignees());
         }
      }
      return "";
   }
}
