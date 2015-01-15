/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.agile.AgileSprint;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class WorkItemFactory implements IAtsWorkItemFactory {

   private final IAtsClient atsClient;

   public WorkItemFactory(IAtsClient atsClient) {
      this.atsClient = atsClient;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(Object artifact) throws OseeCoreException {
      IAtsTeamWorkflow team = null;
      if (artifact instanceof TeamWorkFlowArtifact) {
         team = (TeamWorkFlowArtifact) artifact;
      }
      return team;
   }

   @Override
   public IAtsWorkItem getWorkItem(Object object) throws OseeCoreException {
      IAtsWorkItem workItem = null;
      if (object instanceof AbstractWorkflowArtifact) {
         workItem = (AbstractWorkflowArtifact) object;
      }
      return workItem;
   }

   @Override
   public IAtsTask getTask(Object artifact) throws OseeCoreException {
      IAtsTask task = null;
      if (artifact instanceof IAtsTask) {
         task = (IAtsTask) artifact;
      }
      return task;
   }

   @Override
   public IAtsAbstractReview getReview(Object artifact) throws OseeCoreException {
      IAtsAbstractReview review = null;
      if (artifact instanceof IAtsAbstractReview) {
         review = (IAtsAbstractReview) artifact;
      }
      return review;
   }

   @Override
   public IAtsGoal getGoal(Object artifact) throws OseeCoreException {
      IAtsGoal goal = null;
      if (artifact instanceof IAtsGoal) {
         goal = (IAtsGoal) artifact;
      }
      return goal;
   }

   @Override
   public IAgileSprint getAgileSprint(Object artifact) throws OseeCoreException {
      IAgileSprint sprint = null;
      if (artifact instanceof IAgileSprint) {
         sprint = (IAgileSprint) artifact;
      } else {
         sprint = new AgileSprint(atsClient, (Artifact) artifact);
      }
      return sprint;
   }

   @Override
   public IAtsAction getAction(Object artifact) {
      IAtsAction action = null;
      if (artifact instanceof IAtsAction) {
         action = (IAtsAction) artifact;
      }
      return action;
   }

   @Override
   public IAtsWorkItem getWorkItemByAtsId(String atsId) {
      Artifact artifact =
         ArtifactQuery.getArtifactFromAttribute(AtsAttributeTypes.AtsId, atsId, AtsUtilCore.getAtsBranch());
      return getWorkItem(artifact);
   }

}
