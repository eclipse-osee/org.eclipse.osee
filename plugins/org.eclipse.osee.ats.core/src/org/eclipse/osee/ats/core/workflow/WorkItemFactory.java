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
package org.eclipse.osee.ats.core.workflow;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.review.DecisionReview;
import org.eclipse.osee.ats.core.review.PeerToPeerReview;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class WorkItemFactory extends AbstractWorkItemFactory {

   private final AtsApi atsApi;

   public WorkItemFactory(AtsApi atsApi) {
      super(atsApi);
      this.atsApi = atsApi;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactToken artifact) {
      IAtsTeamWorkflow team = null;
      if (artifact instanceof IAtsTeamWorkflow) {
         team = (IAtsTeamWorkflow) artifact;
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.TeamWorkflow)) {
         team = new TeamWorkflow(atsApi.getLogger(), atsApi, artifact);
      }
      return team;
   }

   @Override
   public IAtsGoal getGoal(ArtifactToken artifact) {
      IAtsGoal goal = null;
      if (artifact instanceof IAtsGoal) {
         goal = (IAtsGoal) artifact;
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.Goal)) {
         goal = new Goal(atsApi.getLogger(), atsApi, artifact);
      }
      return goal;
   }

   @Override
   public IAtsTask getTask(ArtifactToken artifact) {
      IAtsTask task = null;
      if (artifact instanceof IAtsTask) {
         task = (IAtsTask) artifact;
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.Task)) {
         task = new Task(atsApi.getLogger(), atsApi, artifact);
      }
      return task;
   }

   @Override
   public IAtsAbstractReview getReview(ArtifactToken artifact) {
      IAtsAbstractReview review = null;
      if (artifact instanceof IAtsAbstractReview) {
         review = (IAtsAbstractReview) artifact;
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.PeerToPeerReview)) {
         review = new PeerToPeerReview(atsApi.getLogger(), atsApi, artifact);
      } else {
         review = new DecisionReview(atsApi.getLogger(), atsApi, artifact);
      }
      return review;
   }

   @Override
   public IAtsAction getAction(ArtifactToken artifact) {
      IAtsAction action = null;
      if (artifact instanceof IAtsAction) {
         action = (IAtsAction) artifact;
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.Action)) {
         action = new Action(atsApi, artifact);
      }
      return action;
   }

}
