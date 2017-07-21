/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class WorkflowManagerCore {

   public static boolean isEditable(IAtsUser user, IAtsWorkItem workItem, IAtsStateDefinition stateDef, boolean privilegedEditEnabled, IAtsUserService userService) throws OseeCoreException {
      return isEditable(workItem, stateDef, privilegedEditEnabled, user, userService.isAtsAdmin());
   }

   public static boolean isEditable(IAtsWorkItem workItem, IAtsStateDefinition stateDef, boolean privilegedEditEnabled, IAtsUser currentUser, boolean isAtsAdmin) throws OseeCoreException {
      WorkflowManagerCore wmc = new WorkflowManagerCore();
      return wmc.isWorkItemEditable(workItem, stateDef, privilegedEditEnabled, currentUser, isAtsAdmin);
   }

   protected boolean isWorkItemEditable(IAtsWorkItem workItem, IAtsStateDefinition stateDef, boolean privilegedEditEnabled, IAtsUser currentUser, boolean isAtsAdmin) throws OseeCoreException {
      // must be current state
      return (stateDef == null || workItem.getStateDefinition().getName().equals(stateDef.getName())) &&
      // and one of these
      //
      // page is define to allow anyone to edit
         (workItem.getStateDefinition().hasRule(RuleDefinitionOption.AllowEditToAll.name()) ||
         // team definition has allowed anyone to edit
            teamDefHasRule(workItem, RuleDefinitionOption.AllowEditToAll) ||
            // privileged edit mode is on
            privilegedEditEnabled ||
            // current user is assigned
            workItem.getAssignees().contains(currentUser) ||
            // current user is ats admin
            isAtsAdmin);
   }

   protected boolean teamDefHasRule(IAtsWorkItem workItem, RuleDefinitionOption option) {
      boolean hasRule = false;
      IAtsTeamWorkflow teamWf = null;
      try {
         if (workItem instanceof IAtsTeamWorkflow) {
            teamWf = (IAtsTeamWorkflow) workItem;
         } else if (workItem instanceof IAtsAbstractReview) {
            teamWf = ((IAtsAbstractReview) workItem).getParentTeamWorkflow();
         }
         if (teamWf != null) {
            hasRule = teamWf.getTeamDefinition().hasRule(option.name());
         }
      } catch (Exception ex) {
         OseeLog.log(WorkflowManagerCore.class, Level.SEVERE, ex);
      }
      return hasRule;
   }

}
