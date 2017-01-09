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
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.agile.AgileBacklog;
import org.eclipse.osee.ats.core.client.agile.AgileItem;
import org.eclipse.osee.ats.core.client.agile.AgileSprint;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.AbstractWorkItemFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class WorkItemFactory extends AbstractWorkItemFactory {

   private final IAtsClient atsClient;

   public WorkItemFactory(IAtsClient atsClient) {
      super();
      this.atsClient = atsClient;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactId artifact) throws OseeCoreException {
      IAtsTeamWorkflow team = null;
      if (artifact instanceof TeamWorkFlowArtifact) {
         team = (TeamWorkFlowArtifact) artifact;
      }
      return team;
   }

   @Override
   public IAtsWorkItem getWorkItem(ArtifactId artifact) throws OseeCoreException {
      IAtsWorkItem workItem = null;
      if (artifact instanceof AbstractWorkflowArtifact) {
         workItem = (AbstractWorkflowArtifact) artifact;
      }
      return workItem;
   }

   @Override
   public IAtsTask getTask(ArtifactId artifact) throws OseeCoreException {
      IAtsTask task = null;
      if (artifact instanceof IAtsTask) {
         task = (IAtsTask) artifact;
      }
      return task;
   }

   @Override
   public IAtsAbstractReview getReview(ArtifactId artifact) throws OseeCoreException {
      IAtsAbstractReview review = null;
      if (artifact instanceof IAtsAbstractReview) {
         review = (IAtsAbstractReview) artifact;
      }
      return review;
   }

   @Override
   public IAtsGoal getGoal(ArtifactId artifact) throws OseeCoreException {
      IAtsGoal goal = null;
      if (artifact instanceof IAtsGoal) {
         goal = (IAtsGoal) artifact;
      }
      return goal;
   }

   @Override
   public IAgileSprint getAgileSprint(ArtifactId artifact) throws OseeCoreException {
      IAgileSprint sprint = null;
      if (artifact instanceof IAgileSprint) {
         sprint = (IAgileSprint) artifact;
      } else {
         sprint = new AgileSprint(atsClient, (Artifact) artifact);
      }
      return sprint;
   }

   @Override
   public IAgileBacklog getAgileBacklog(ArtifactId artifact) throws OseeCoreException {
      IAgileBacklog backlog = null;
      if (artifact instanceof IAgileBacklog) {
         backlog = (IAgileBacklog) artifact;
      } else {
         backlog = new AgileBacklog(atsClient, (Artifact) artifact);
      }
      return backlog;
   }

   @Override
   public IAtsAction getAction(ArtifactId artifact) {
      IAtsAction action = null;
      if (artifact instanceof IAtsAction) {
         action = (IAtsAction) artifact;
      }
      return action;
   }

   @Override
   public IAtsWorkItem getWorkItemByAtsId(String atsId) {
      Artifact artifact =
         ArtifactQuery.getArtifactFromAttribute(AtsAttributeTypes.AtsId, atsId, AtsClientService.get().getAtsBranch());
      return getWorkItem(artifact);
   }

   @Override
   public IAgileItem getAgileItem(ArtifactId artifact) {
      IAgileItem item = null;
      if (artifact instanceof IAgileItem) {
         item = (IAgileItem) artifact;
      } else {
         item = new AgileItem(atsClient, (Artifact) artifact);
      }
      return item;
   }

}
