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
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.util.AtsIdProvider;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactFactory extends ArtifactFactory {

   public AtsArtifactFactory() {
      super(AtsArtifactTypes.Action, AtsArtifactTypes.PeerToPeerReview, AtsArtifactTypes.DecisionReview,
         AtsArtifactTypes.Task, AtsArtifactTypes.TeamWorkflow, AtsArtifactTypes.Goal);
      try {
         for (IArtifactType teamWorkflowTypeName : TeamWorkFlowManager.getTeamWorkflowArtifactTypes()) {
            registerAsResponsible(teamWorkflowTypeName);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public Artifact getArtifactInstance(String guid, Branch branch, IArtifactType artifactType, boolean inDataStore) throws OseeCoreException {
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
      } else if (artifactType.equals(AtsArtifactTypes.Action)) {
         toReturn = new ActionArtifact(guid, branch, artifactType);
      } else {
         throw new OseeArgumentException("AtsArtifactFactory did not recognize the artifact type [%s]", artifactType);
      }

      if (!inDataStore) {
         String atsId = HumanReadableId.generate();
         if (OseeInfo.isBooleanUsingCache("new.ats.ids")) {
            atsId = AtsIdProvider.get().getNextId();
         } else {
            toReturn.setHrid(atsId);
         }
         toReturn.setSoleAttributeFromString(AtsAttributeTypes.AtsId, atsId);
      }

      return toReturn;
   }

   @Override
   public Collection<IArtifactType> getEternalArtifactTypes() {
      List<IArtifactType> artifactTypes = new ArrayList<IArtifactType>();
      artifactTypes.add(AtsArtifactTypes.WorkDefinition);
      artifactTypes.add(AtsArtifactTypes.Version);
      artifactTypes.add(AtsArtifactTypes.TeamDefinition);
      artifactTypes.add(AtsArtifactTypes.ActionableItem);
      return artifactTypes;
   }

}