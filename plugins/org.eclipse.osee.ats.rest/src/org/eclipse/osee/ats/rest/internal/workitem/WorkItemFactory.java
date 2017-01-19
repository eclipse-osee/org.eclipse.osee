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
package org.eclipse.osee.ats.rest.internal.workitem;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.agile.AgileBacklog;
import org.eclipse.osee.ats.core.agile.AgileSprint;
import org.eclipse.osee.ats.core.workflow.AbstractWorkItemFactory;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.workitem.model.Action;
import org.eclipse.osee.ats.rest.internal.workitem.model.DecisionReview;
import org.eclipse.osee.ats.rest.internal.workitem.model.Goal;
import org.eclipse.osee.ats.rest.internal.workitem.model.PeerToPeerReview;
import org.eclipse.osee.ats.rest.internal.workitem.model.Task;
import org.eclipse.osee.ats.rest.internal.workitem.model.TeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class WorkItemFactory extends AbstractWorkItemFactory {

   private final Log logger;
   private final IAtsServer atsServer;

   public WorkItemFactory(Log logger, IAtsServer atsServer) {
      super();
      this.logger = logger;
      this.atsServer = atsServer;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactId artifact) throws OseeCoreException {
      IAtsTeamWorkflow team = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            team = new TeamWorkflow(logger, atsServer, (ArtifactReadable) artifact);
         }
      }
      return team;
   }

   @Override
   public IAtsWorkItem getWorkItem(ArtifactId artifact) {
      IAtsWorkItem workItem = null;
      try {
         if (artifact instanceof ArtifactReadable) {
            ArtifactReadable artRead = (ArtifactReadable) artifact;
            if (artRead.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               workItem = getTeamWf(artifact);
            } else if (artRead.isOfType(AtsArtifactTypes.PeerToPeerReview) || artRead.isOfType(
               AtsArtifactTypes.DecisionReview)) {
               workItem = getReview(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.Task)) {
               workItem = getTask(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.Goal)) {
               workItem = getGoal(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.AgileSprint)) {
               workItem = getAgileSprint(artRead);
            }
         }
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error getting work item for [%s]", artifact);
      }
      return workItem;
   }

   @Override
   public IAtsGoal getGoal(ArtifactId artifact) throws OseeCoreException {
      IAtsGoal goal = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Goal)) {
            goal = new Goal(logger, atsServer, (ArtifactReadable) artifact);
         }
      }
      return goal;
   }

   @Override
   public IAgileSprint getAgileSprint(ArtifactId artifact) throws OseeCoreException {
      IAgileSprint sprint = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.AgileSprint)) {
            sprint = new AgileSprint(logger, atsServer, (ArtifactReadable) artifact);
         }
      }
      return sprint;
   }

   @Override
   public IAgileItem getAgileItem(ArtifactId artifact) {
      IAgileItem item = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
            item = new org.eclipse.osee.ats.core.agile.AgileItem(logger, atsServer,
               (ArtifactReadable) artifact);
         }
      }
      return item;
   }

   @Override
   public IAgileBacklog getAgileBacklog(ArtifactId artifact) throws OseeCoreException {
      IAgileBacklog backlog = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Goal)) {
            backlog = new AgileBacklog(logger, atsServer, (ArtifactReadable) artifact);
         }
      }
      return backlog;
   }

   @Override
   public IAtsTask getTask(ArtifactId artifact) throws OseeCoreException {
      IAtsTask task = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Task)) {
            task = new Task(logger, atsServer, (ArtifactReadable) artifact);
         }
      }
      return task;
   }

   @Override
   public IAtsAbstractReview getReview(ArtifactId artifact) throws OseeCoreException {
      IAtsAbstractReview review = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.PeerToPeerReview)) {
            review = new PeerToPeerReview(logger, atsServer, artRead);
         } else {
            review = new DecisionReview(logger, atsServer, artRead);
         }
      }
      return review;
   }

   @Override
   public IAtsAction getAction(ArtifactId artifact) {
      IAtsAction action = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Action)) {
            action = new Action(atsServer, artRead);
         }
      }
      return action;
   }

   @Override
   public IAtsWorkItem getWorkItemByAtsId(String atsId) {
      ArtifactReadable artifact = atsServer.getQuery().and(AtsAttributeTypes.AtsId, atsId).getResults().getOneOrNull();
      return getWorkItem(artifact);
   }

}
