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

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.review.DecisionReview;
import org.eclipse.osee.ats.core.review.PeerToPeerReview;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class WorkItemFactory extends AbstractWorkItemFactory {

   private final IAtsServices services;

   public WorkItemFactory(IAtsServices services) {
      super(services);
      this.services = services;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactToken artifact) throws OseeCoreException {
      IAtsTeamWorkflow team = null;
      if (artifact instanceof IAtsTeamWorkflow) {
         team = (IAtsTeamWorkflow) artifact;
      } else if (services.getStoreService().isOfType(artifact, AtsArtifactTypes.TeamWorkflow)) {
         team = new TeamWorkflow(services.getLogger(), services, artifact);
      }
      return team;
   }

   @Override
   public IAtsGoal getGoal(ArtifactToken artifact) throws OseeCoreException {
      IAtsGoal goal = null;
      if (artifact instanceof IAtsGoal) {
         goal = (IAtsGoal) artifact;
      } else if (services.getStoreService().isOfType(artifact, AtsArtifactTypes.Goal)) {
         goal = new Goal(services.getLogger(), services, artifact);
      }
      return goal;
   }

   @Override
   public IAtsTask getTask(ArtifactToken artifact) throws OseeCoreException {
      IAtsTask task = null;
      if (artifact instanceof IAtsTask) {
         task = (IAtsTask) artifact;
      } else if (services.getStoreService().isOfType(artifact, AtsArtifactTypes.Task)) {
         task = new Task(services.getLogger(), services, artifact);
      }
      return task;
   }

   @Override
   public IAtsAbstractReview getReview(ArtifactToken artifact) throws OseeCoreException {
      IAtsAbstractReview review = null;
      if (artifact instanceof IAtsAbstractReview) {
         review = (IAtsAbstractReview) artifact;
      } else if (services.getStoreService().isOfType(artifact, AtsArtifactTypes.PeerToPeerReview)) {
         review = new PeerToPeerReview(services.getLogger(), services, artifact);
      } else {
         review = new DecisionReview(services.getLogger(), services, artifact);
      }
      return review;
   }

   @Override
   public IAtsAction getAction(ArtifactToken artifact) {
      IAtsAction action = null;
      if (artifact instanceof IAtsAction) {
         action = (IAtsAction) artifact;
      } else if (services.getStoreService().isOfType(artifact, AtsArtifactTypes.Action)) {
         action = new Action(services, artifact);
      }
      return action;
   }

}
