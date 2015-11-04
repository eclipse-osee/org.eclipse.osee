/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class GoalSearchItem extends WorldUISearchItem {

   private final IAtsUser user;
   private final String title;
   private final boolean showCompleted;
   private final boolean showCancelled;

   public GoalSearchItem(String displayName, String title, boolean showCompletedCancelled, IAtsUser user) {
      this(displayName, title, true, true, user);
   }

   public GoalSearchItem(String displayName, String title, boolean showCompleted, boolean showCancelled, IAtsUser user) {
      super(displayName, AtsImage.GOAL);
      this.title = title;
      this.showCompleted = showCompleted;
      this.showCancelled = showCancelled;
      this.user = user;
   }

   public GoalSearchItem(GoalSearchItem goalWorldUISearchItem) {
      super(goalWorldUISearchItem, AtsImage.GOAL);
      this.user = goalWorldUISearchItem.user;
      this.title = goalWorldUISearchItem.title;
      this.showCancelled = goalWorldUISearchItem.showCancelled;
      this.showCompleted = goalWorldUISearchItem.showCompleted;
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return super.getSelectedName(searchType);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      IAtsQuery query = AtsClientService.get().getQueryService().createQuery(WorkItemType.Goal);
      if (user != null) {
         query.andAssignee(user);
      }
      if (!showCancelled && !showCompleted) {
         query.andStateType(StateType.Working);
      } else if (!showCancelled) {
         query.andStateType(StateType.Working, StateType.Completed);
      } else if (!showCompleted) {
         query.andStateType(StateType.Working, StateType.Cancelled);
      }
      if (Strings.isValid(title)) {
         query.andAttr(AtsAttributeTypes.Title, title, QueryOption.CONTAINS_MATCH_OPTIONS);
      }
      return Collections.castAll(query.getResultArtifacts().getList());
   }

   @Override
   public WorldUISearchItem copy() {
      return new GoalSearchItem(this);
   }

}
