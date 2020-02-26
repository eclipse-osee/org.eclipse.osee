/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse  default public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow.hooks;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkflowHook {

   default public Result committing(IAtsTeamWorkflow workItem) {
      return Result.TrueResult;
   }

   default public String getBranchShortName(IAtsWorkItem workItem, AtsApi atsApi) {
      return null;
   }

   default public Collection<AtsUser> getOverrideTransitionToAssignees(IAtsWorkItem workItem, String decision) {
      return null;
   }

   default public boolean isAccessControlViaAssigneesEnabledForBranching() {
      return false;
   }

   default public String getFullName() {
      return getClass().getName();
   }

   String getDescription();

   default public Result workingBranchCreated(IAtsTeamWorkflow teamWf) {
      return Result.TrueResult;
   }

}
