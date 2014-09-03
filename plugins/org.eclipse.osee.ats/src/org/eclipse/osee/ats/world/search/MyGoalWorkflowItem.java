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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class MyGoalWorkflowItem extends UserSearchItem {

   private final GoalSearchState goalSearchState;

   public enum GoalSearchState {
      InWork,
      All
   };

   public MyGoalWorkflowItem(String name, IAtsUser user, GoalSearchState goalSearchState) {
      super(name, user, AtsImage.GOAL);
      this.goalSearchState = goalSearchState;
   }

   public MyGoalWorkflowItem(MyGoalWorkflowItem myReviewWorkflowItem) {
      super(myReviewWorkflowItem, AtsImage.GOAL);
      this.goalSearchState = myReviewWorkflowItem.goalSearchState;
   }

   @Override
   protected Collection<Artifact> searchIt(IAtsUser user) throws OseeCoreException {

      Set<Artifact> assigned = AtsUtil.getAssigned(user);
      Set<Artifact> artifacts = new HashSet<Artifact>(50);
      // Because user can be assigned directly to review or through being assigned to task, add in
      // all the original artifacts.
      artifacts.addAll(assigned);

      if (goalSearchState == GoalSearchState.InWork) {
         artifacts.addAll(RelationManager.getRelatedArtifacts(assigned, 1, AtsRelationTypes.TeamWfToTask_TeamWf));
      } else {
         artifacts.addAll(ArtifactQuery.getArtifactListFromAttribute(AtsAttributeTypes.State,
            "<" + user.getUserId() + ">", AtsUtilCore.getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS));
      }

      List<Artifact> artifactsToReturn = new ArrayList<Artifact>(artifacts.size());
      for (Artifact artifact : artifacts) {
         if (artifact.isOfType(AtsArtifactTypes.Goal) && (goalSearchState == GoalSearchState.All || goalSearchState == GoalSearchState.InWork && !((AbstractWorkflowArtifact) artifact).isCompletedOrCancelled())) {
            artifactsToReturn.add(artifact);
         }
      }
      return artifactsToReturn;
   }

   @Override
   public WorldUISearchItem copy() {
      return new MyGoalWorkflowItem(this);
   }

}
