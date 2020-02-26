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
package org.eclipse.osee.ats.ide.world.search;

import java.util.Arrays;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.search.WorldSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class OpenWorkflowsByTeamDefSearchItem extends WorldSearchItem {

   private final AtsUser assignee;
   private final boolean includeCompletedCancelled;
   private ReleasedOption releasedOption;
   private final ILazyTeamDefinitionProvider teamDefProvider;

   public OpenWorkflowsByTeamDefSearchItem(String name, ILazyTeamDefinitionProvider teamDefProvider) {
      this(name, teamDefProvider, null);
   }

   public OpenWorkflowsByTeamDefSearchItem(String name, ILazyTeamDefinitionProvider teamDefProvider, AtsUser assignee) {
      this(name, teamDefProvider, assignee, false);
   }

   public OpenWorkflowsByTeamDefSearchItem(String name, ILazyTeamDefinitionProvider teamDefProvider, AtsUser assignee, boolean includeCompletedCancelled) {
      super(name);
      this.teamDefProvider = teamDefProvider;
      this.assignee = assignee;
      this.includeCompletedCancelled = includeCompletedCancelled;
   }

   public OpenWorkflowsByTeamDefSearchItem(String name, ILazyTeamDefinitionProvider teamDefProvider, boolean includeCompletedCancelled, ReleasedOption releasedOption) {
      this(name, teamDefProvider, null, includeCompletedCancelled);
      this.releasedOption = releasedOption;
   }

   @Override
   public WorldUISearchItem copy() {
      AtsSearchData data = getData();
      if (!Strings.isValid(data.getUserId())) {
         data.getWorkItemTypes().add(WorkItemType.TeamWorkflow);
         data.setTeamDefIds(AtsObjects.toIds(teamDefProvider.getTeamDefs()));
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
