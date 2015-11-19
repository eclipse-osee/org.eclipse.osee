/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.search.WorldSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class OpenWorkflowsByTeamDefSearchItem extends WorldSearchItem {

   private final List<Long> teamDefUuids;
   private final IAtsUser assignee;
   private final boolean includeCompletedCancelled;
   private ReleasedOption releasedOption;

   public OpenWorkflowsByTeamDefSearchItem(String name, Collection<IAtsTeamDefinition> teamDefs) {
      this(name, teamDefs, null);
   }

   public OpenWorkflowsByTeamDefSearchItem(String name, Collection<IAtsTeamDefinition> teamDefs, IAtsUser assignee) {
      this(name, teamDefs, assignee, false);
   }

   public OpenWorkflowsByTeamDefSearchItem(String name, Collection<IAtsTeamDefinition> teamDefs, IAtsUser assignee, boolean includeCompletedCancelled) {
      super(name);
      this.assignee = assignee;
      this.includeCompletedCancelled = includeCompletedCancelled;
      this.teamDefUuids = AtsObjects.toUuids(teamDefs);
   }

   public OpenWorkflowsByTeamDefSearchItem(String name, List<IAtsTeamDefinition> teamDefs, boolean includeCompletedCancelled, ReleasedOption releasedOption) {
      this(name, teamDefs, null, includeCompletedCancelled);
      this.releasedOption = releasedOption;
   }

   @Override
   public WorldUISearchItem copy() {
      AtsSearchData data = getData();
      if (!Strings.isValid(data.getUserId())) {
         data.getWorkItemTypes().add(WorkItemType.TeamWorkflow);
         data.setTeamDefUuids(teamDefUuids);
         if (!includeCompletedCancelled) {
            data.setStateTypes(Arrays.asList(StateType.Working));
         }
         if (assignee != null) {
            data.setUserId(assignee.getUserId());
            data.setUserType(AtsSearchUserType.Assignee);
         }
         if (releasedOption != null) {
            data.setReleasedOption(releasedOption);
         }
      }
      return new WorldSearchItem(data);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.REVIEW);
   }

}
