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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class MyGoalWorkflowItem extends UserSearchItem {

   private final GoalSearchState goalSearchState;

   public enum GoalSearchState {
      InWork, All
   };

   public MyGoalWorkflowItem(String name, User user, GoalSearchState goalSearchState) {
      super(name, user, AtsImage.GOAL);
      this.goalSearchState = goalSearchState;
   }

   public MyGoalWorkflowItem(MyGoalWorkflowItem myReviewWorkflowItem) {
      super(myReviewWorkflowItem, AtsImage.GOAL);
      this.goalSearchState = myReviewWorkflowItem.goalSearchState;
   }

   @Override
   protected Collection<Artifact> searchIt(User user) throws OseeCoreException {

      Set<Artifact> assigned =
            RelationManager.getRelatedArtifacts(Arrays.asList(user), 1, CoreRelationEnumeration.Users_Artifact);
      Set<Artifact> artifacts = new HashSet<Artifact>(50);
      // Because user can be assigned directly to review or through being assigned to task, add in
      // all the original artifacts.
      artifacts.addAll(assigned);

      if (goalSearchState == GoalSearchState.InWork) {
         artifacts.addAll(RelationManager.getRelatedArtifacts(assigned, 1, AtsRelation.SmaToTask_Sma));
      } else {
         artifacts.addAll(ArtifactQuery.getArtifactListFromAttribute(ATSAttributes.STATE_ATTRIBUTE.getStoreName(),
               "%<" + user.getUserId() + ">%", AtsUtil.getAtsBranch()));
      }

      List<Artifact> artifactsToReturn = new ArrayList<Artifact>(artifacts.size());
      for (Artifact artifact : artifacts) {
         if (artifact instanceof GoalArtifact) {
            if (goalSearchState == GoalSearchState.All || (goalSearchState == GoalSearchState.InWork && !((StateMachineArtifact) artifact).getSmaMgr().isCancelledOrCompleted())) {
               artifactsToReturn.add(artifact);
            }
         }
      }
      return artifactsToReturn;
   }

   @Override
   public WorldUISearchItem copy() {
      return new MyGoalWorkflowItem(this);
   }

}
