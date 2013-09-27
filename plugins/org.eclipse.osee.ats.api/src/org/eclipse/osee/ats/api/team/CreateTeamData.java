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
package org.eclipse.osee.ats.api.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.user.IAtsUser;

/**
 * @author Donald G Dunne
 */
public class CreateTeamData {

   private final IAtsTeamDefinition teamDef;
   private final Collection<IAtsActionableItem> actionableItems;
   private final List<? extends IAtsUser> assignees;
   private final Date createdDate;
   private final IAtsUser createdBy;
   private final CreateTeamOption[] createTeamOption;

   public CreateTeamData(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, CreateTeamOption... createTeamOption) {
      this.teamDef = teamDef;
      this.actionableItems = new ArrayList<IAtsActionableItem>(actionableItems);
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

   public List<? extends IAtsUser> getAssignees() {
      return assignees;
   }

   public Date getCreatedDate() {
      return createdDate;
   }

   public IAtsUser getCreatedBy() {
      return createdBy;
   }

   public CreateTeamOption[] getCreateTeamOption() {
      return createTeamOption;
   }
}
