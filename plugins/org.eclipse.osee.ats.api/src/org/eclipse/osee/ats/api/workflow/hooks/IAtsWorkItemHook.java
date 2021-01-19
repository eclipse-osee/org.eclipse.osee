/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.api.workflow.hooks;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemHook {

   /**
    * Called before commit for other applications/extensions to do checks and provide status. Result.False will stop the
    * commit.
    */
   default public XResultData committing(IAtsTeamWorkflow teamWf, XResultData rd) {
      return rd;
   }

   default public XResultData creatingBranch(IAtsTeamWorkflow teamWf, XResultData rd) {
      return rd;
   }

   default public XResultData postBranchCreation(IAtsTeamWorkflow teamWf, XResultData rd) {
      return rd;
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

   default public XResultData workingBranchCreated(IAtsTeamWorkflow teamWf, XResultData rd) {
      return rd;
   }

}
