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

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class WorkItemFactory implements IAtsWorkItemFactory {

   @Override
   public IAtsTeamWorkflow getTeamWf(Object artifact) throws OseeCoreException {
      IAtsTeamWorkflow team = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            team = new TeamWorkflow((ArtifactReadable) artifact);
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
         OseeLog.log(WorkItemFactory.class, Level.SEVERE, ex);
      }
      return workItem;
   }

   @Override
   public IAtsConfigObject getConfigObject(Object artifact) throws OseeCoreException {
      IAtsConfigObject configObject = null;
      try {
         if (artifact instanceof ArtifactReadable) {
            ArtifactReadable artRead = (ArtifactReadable) artifact;
            if (artRead.isOfType(AtsArtifactTypes.Version)) {
               configObject = getVersion(artifact);
            } else if (artRead.isOfType(AtsArtifactTypes.TeamDefinition)) {
               configObject = getTeamDef(artRead);
            } else if (artRead.isOfType(AtsArtifactTypes.ActionableItem)) {
               configObject = getActionableItem(artRead);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(WorkItemFactory.class, Level.SEVERE, ex);
      }
      return configObject;
   }

   @Override
   public IAtsVersion getVersion(Object artifact) {
      IAtsVersion version = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Version)) {
            version = new Version((ArtifactReadable) artifact);
         }
      }
      return version;
   }

   @Override
   public IAtsGoal getGoal(Object artifact) throws OseeCoreException {
      IAtsGoal goal = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.Goal)) {
            goal = new Goal((ArtifactReadable) artifact);
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
            task = new Task((ArtifactReadable) artifact);
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
            review = new PeerToPeerReview(artRead);
         } else {
            review = new DecisionReview(artRead);
         }
      }
      return review;
   }

   @Override
   public IAtsTeamDefinition getTeamDef(Object artifact) throws OseeCoreException {
      IAtsTeamDefinition teamDef = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.TeamDefinition)) {
            teamDef = new TeamDefinition((ArtifactReadable) artifact);
         }
      }
      return teamDef;
   }

   @Override
   public IAtsActionableItem getActionableItem(Object artifact) throws OseeCoreException {
      IAtsActionableItem ai = null;
      if (artifact instanceof ArtifactReadable) {
         ArtifactReadable artRead = (ArtifactReadable) artifact;
         if (artRead.isOfType(AtsArtifactTypes.ActionableItem)) {
            ai = new ActionableItem((ArtifactReadable) artifact);
         }
      }
      return ai;
   }

}
