/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.core.test;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.BranchToken;

/**
 * @author Donald G. Dunne
 */
public class MockTeamWorkflow extends MockWorkItem implements IAtsTeamWorkflow {

   private IAtsTeamDefinition teamDefinition;
   private BranchToken workingBranch;
   private Set<IAtsActionableItem> actionableItems = new HashSet<>();

   public MockTeamWorkflow(Long id, String name) {
      super(id, name);
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() {
      return teamDefinition;
   }

   public void setTeamDefinition(IAtsTeamDefinition teamDefinition) {
      this.teamDefinition = teamDefinition;
   }

   @Override
   public BranchToken getWorkingBranch() {
      return workingBranch;
   }

   public void setWorkingBranch(BranchToken workingBranch) {
      this.workingBranch = workingBranch;
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems() {
      return actionableItems;
   }

   public void setActionableItems(Set<IAtsActionableItem> actionableItems) {
      this.actionableItems = actionableItems;
   }

   @Override
   public boolean isTeamWorkflow() {
      return true;
   }

}
