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
package org.eclipse.osee.ats.impl.internal.workitem;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class WorkItemFactory implements IAtsWorkItemFactory {

   private final Log logger;
   private final IAtsServer atsServer;

   public WorkItemFactory(Log logger, IAtsServer atsServer) {
      this.logger = logger;
      this.atsServer = atsServer;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(Object artifact) throws OseeCoreException {
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
   public IAtsWorkItem getWorkItem(Object artifact) {
      IAtsWorkItem workItem = null;
      try {
         if (artifact instanceof ArtifactReadable) {
            ArtifactReadable artRead = (ArtifactReadable) artifact;
            if (artRead.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               workItem = getTeamWf(artifact);
            } else if (artRead.isOfType(AtsArtifactTypes.PeerToPeerReview) || artRead.isOfType(AtsArtifactTypes.DecisionReview)) {
               workItem = getReview(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.Task)) {
               workItem = getTask(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.Goal)) {
               workItem = getGoal(artRead);
            }
         }
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error getting work item for [%s]", artifact);
      }
      return workItem;
   }

   @Override
   public IAtsGoal getGoal(Object artifact) throws OseeCoreException {
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
   public IAtsTask getTask(Object artifact) throws OseeCoreException {
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
   public IAtsAbstractReview getReview(Object artifact) throws OseeCoreException {
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

}
