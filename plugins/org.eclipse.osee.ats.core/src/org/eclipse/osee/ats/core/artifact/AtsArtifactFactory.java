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
package org.eclipse.osee.ats.core.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.action.ActionArtifact;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkflowProviders;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.version.VersionArtifact;
import org.eclipse.osee.ats.core.workflow.ITeamWorkflowProvider;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactFactory extends ArtifactFactory {

   public AtsArtifactFactory() {
      super(AtsArtifactTypes.Action, AtsArtifactTypes.Version, AtsArtifactTypes.PeerToPeerReview,
         AtsArtifactTypes.DecisionReview, AtsArtifactTypes.ActionableItem, AtsArtifactTypes.Task,
         AtsArtifactTypes.TeamWorkflow, AtsArtifactTypes.TeamDefinition, AtsArtifactTypes.Goal);
      for (ITeamWorkflowProvider atsTeamWorkflow : TeamWorkflowProviders.getAtsTeamWorkflowProviders()) {
         try {
            for (IArtifactType teamWorkflowTypeName : atsTeamWorkflow.getTeamWorkflowArtifactTypes()) {
               registerAsResponsible(teamWorkflowTypeName);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public Artifact getArtifactInstance(String guid, String humandReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      if (artifactType.equals(AtsArtifactTypes.Task)) {
         return new TaskArtifact(this, guid, humandReadableId, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.TeamWorkflow)) {
         return new TeamWorkFlowArtifact(this, guid, humandReadableId, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.TeamDefinition)) {
         return new TeamDefinitionArtifact(this, guid, humandReadableId, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.ActionableItem)) {
         return new ActionableItemArtifact(this, guid, humandReadableId, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.DecisionReview)) {
         return new DecisionReviewArtifact(this, guid, humandReadableId, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.PeerToPeerReview)) {
         return new PeerToPeerReviewArtifact(this, guid, humandReadableId, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.Version)) {
         return new VersionArtifact(this, guid, humandReadableId, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.Goal)) {
         return new GoalArtifact(this, guid, humandReadableId, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.Action)) {
         return new ActionArtifact(this, guid, humandReadableId, branch, artifactType);
      } else if (TeamWorkflowProviders.getAllTeamWorkflowArtifactTypes().contains(artifactType)) {
         return new TeamWorkFlowArtifact(this, guid, humandReadableId, branch, artifactType);
      } else {
         throw new OseeArgumentException("AtsArtifactFactory did not recognize the artifact type [%s]", artifactType);
      }
   }

   @Override
   public Collection<IArtifactType> getEternalArtifactTypes() {
      List<IArtifactType> artifactTypes = new ArrayList<IArtifactType>();
      artifactTypes.add(AtsArtifactTypes.Version);
      artifactTypes.add(AtsArtifactTypes.TeamDefinition);
      artifactTypes.add(AtsArtifactTypes.ActionableItem);
      artifactTypes.add(AtsArtifactTypes.WorkDefinition);
      artifactTypes.add(CoreArtifactTypes.WorkRuleDefinition);
      artifactTypes.add(CoreArtifactTypes.WorkFlowDefinition);
      artifactTypes.add(CoreArtifactTypes.WorkWidgetDefinition);
      artifactTypes.add(CoreArtifactTypes.WorkPageDefinition);
      return artifactTypes;
   }

}