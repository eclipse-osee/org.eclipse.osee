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
package org.eclipse.osee.ats.core.client.artifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public class AtsArtifactFactory extends ArtifactFactory {

   List<IArtifactType> disabledUserCreationTypes = null;

   public AtsArtifactFactory() {
      super(AtsArtifactTypes.Action, AtsArtifactTypes.PeerToPeerReview, AtsArtifactTypes.DecisionReview,
         AtsArtifactTypes.Task, AtsArtifactTypes.TeamWorkflow, AtsArtifactTypes.Goal, AtsArtifactTypes.AgileSprint);
      try {
         for (IArtifactType teamWorkflowTypeName : AtsClientService.get().getStoreService().getTeamWorkflowArtifactTypes()) {
            registerAsResponsible(teamWorkflowTypeName);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public Artifact getArtifactInstance(String guid, BranchId branch, IArtifactType artifactType, boolean inDataStore) throws OseeCoreException {
      Artifact toReturn;
      if (artifactType.equals(AtsArtifactTypes.Task)) {
         toReturn = new TaskArtifact(guid, branch, artifactType);
      } else if (ArtifactTypeManager.inheritsFrom(artifactType, AtsArtifactTypes.TeamWorkflow)) {
         toReturn = new TeamWorkFlowArtifact(guid, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.DecisionReview)) {
         toReturn = new DecisionReviewArtifact(guid, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.PeerToPeerReview)) {
         toReturn = new PeerToPeerReviewArtifact(guid, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.Goal)) {
         toReturn = new GoalArtifact(guid, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.AgileSprint)) {
         toReturn = new SprintArtifact(guid, branch, artifactType);
      } else if (artifactType.equals(AtsArtifactTypes.Action)) {
         toReturn = new ActionArtifact(branch);
      } else {
         throw new OseeArgumentException("AtsArtifactFactory did not recognize the artifact type [%s]", artifactType);
      }
      return toReturn;
   }

   @Override
   public Collection<IArtifactType> getEternalArtifactTypes() {
      List<IArtifactType> artifactTypes = new ArrayList<>();
      artifactTypes.add(AtsArtifactTypes.WorkDefinition);
      artifactTypes.add(AtsArtifactTypes.Version);
      artifactTypes.add(AtsArtifactTypes.TeamDefinition);
      artifactTypes.add(AtsArtifactTypes.ActionableItem);
      return artifactTypes;
   }

   @Override
   public boolean isUserCreationEnabled(IArtifactType artifactType) {
      if (getDisabledUserCreationArtifactTypes().contains(artifactType)) {
         return false;
      } else if (ArtifactTypeManager.inheritsFrom(artifactType, AtsArtifactTypes.TeamWorkflow)) {
         return false;
      }
      return true;
   }

   public List<IArtifactType> getDisabledUserCreationArtifactTypes() {
      if (disabledUserCreationTypes == null) {
         disabledUserCreationTypes = new ArrayList<IArtifactType>(
            Arrays.asList(AtsArtifactTypes.Action, AtsArtifactTypes.PeerToPeerReview, AtsArtifactTypes.DecisionReview,
               AtsArtifactTypes.Task, AtsArtifactTypes.TeamWorkflow, AtsArtifactTypes.Goal,
               AtsArtifactTypes.AgileSprint, AtsArtifactTypes.AgileTeam, AtsArtifactTypes.AgileFeatureGroup));
         String configValue = AtsClientService.get().getConfigValue(AtsUtilCore.USER_CREATION_DISABLED);
         if (Strings.isValid(configValue)) {
            for (String artifactTypeToken : configValue.split(";")) {
               IArtifactType artifactTypeFromToken = TokenFactory.createArtifactTypeFromToken(artifactTypeToken);
               if (artifactTypeFromToken == null) {
                  OseeLog.logf(Activator.class, Level.SEVERE,
                     "Artifact Type Name [%s] specified in AtsConfig.[%s] is invalid",
                     AtsUtilCore.USER_CREATION_DISABLED);
               } else {
                  disabledUserCreationTypes.add(artifactTypeFromToken);
               }
            }
         }
      }
      return disabledUserCreationTypes;
   }
}