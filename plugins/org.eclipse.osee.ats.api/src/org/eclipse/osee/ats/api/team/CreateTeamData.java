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

package org.eclipse.osee.ats.api.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.user.AtsUser;

/**
 * @author Donald G Dunne
 */
public class CreateTeamData {

   private final IAtsTeamDefinition teamDef;
   private final Collection<IAtsActionableItem> actionableItems;
   private final Collection<AtsUser> assignees;
   private final Date createdDate;
   private final AtsUser createdBy;
   private final CreateTeamOption[] createTeamOption;

   public CreateTeamData(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems, Collection<AtsUser> assignees, Date createdDate, AtsUser createdBy, CreateTeamOption... createTeamOption) {
      this.teamDef = teamDef;
      this.actionableItems = new ArrayList<>(actionableItems);
      this.assignees = assignees;
      this.createdDate = createdDate;
      this.createdBy = createdBy;
      this.createTeamOption = createTeamOption;

   }

   public IAtsTeamDefinition getTeamDef() {
      return teamDef;
   }

   public Collection<IAtsActionableItem> getActionableItems() {
      return actionableItems;
   }

   public Collection<AtsUser> getAssignees() {
      return assignees;
   }

   public Date getCreatedDate() {
      return createdDate;
   }

   public AtsUser getCreatedBy() {
      return createdBy;
   }

   public CreateTeamOption[] getCreateTeamOption() {
      return createTeamOption;
   }
}
