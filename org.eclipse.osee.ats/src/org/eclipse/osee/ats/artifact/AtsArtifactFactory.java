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
package org.eclipse.osee.ats.artifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactFactory extends ArtifactFactory {

   public AtsArtifactFactory() {
      super(Arrays.asList(ActionArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME,
            DecisionReviewArtifact.ARTIFACT_NAME, ActionableItemArtifact.ARTIFACT_NAME, TaskArtifact.ARTIFACT_NAME,
            TeamWorkFlowArtifact.ARTIFACT_NAME, TeamDefinitionArtifact.ARTIFACT_NAME, VersionArtifact.ARTIFACT_NAME,
            ActionableItemArtifact.ARTIFACT_NAME, GoalArtifact.ARTIFACT_NAME));
   }

   @Override
   public Artifact getArtifactInstance(String guid, String humandReadableId, Branch branch, ArtifactType artifactType) throws OseeCoreException {
      if (artifactType.getName().equals(ActionArtifact.ARTIFACT_NAME)) return new ActionArtifact(this, guid,
            humandReadableId, branch, artifactType);
      if (artifactType.getName().equals(TaskArtifact.ARTIFACT_NAME)) return new TaskArtifact(this, guid,
            humandReadableId, branch, artifactType);
      if (artifactType.getName().equals(TeamWorkFlowArtifact.ARTIFACT_NAME)) return new TeamWorkFlowArtifact(this,
            guid, humandReadableId, branch, artifactType);
      if (artifactType.getName().equals(TeamDefinitionArtifact.ARTIFACT_NAME)) return new TeamDefinitionArtifact(this,
            guid, humandReadableId, branch, artifactType);
      if (artifactType.getName().equals(VersionArtifact.ARTIFACT_NAME)) return new VersionArtifact(this, guid,
            humandReadableId, branch, artifactType);
      if (artifactType.getName().equals(ActionableItemArtifact.ARTIFACT_NAME)) return new ActionableItemArtifact(this,
            guid, humandReadableId, branch, artifactType);
      if (artifactType.getName().equals(DecisionReviewArtifact.ARTIFACT_NAME)) return new DecisionReviewArtifact(this,
            guid, humandReadableId, branch, artifactType);
      if (artifactType.getName().equals(PeerToPeerReviewArtifact.ARTIFACT_NAME)) return new PeerToPeerReviewArtifact(
            this, guid, humandReadableId, branch, artifactType);
      if (artifactType.getName().equals(GoalArtifact.ARTIFACT_NAME)) return new GoalArtifact(this, guid,
            humandReadableId, branch, artifactType);
      throw new OseeArgumentException("did not recognize the artifact type: " + artifactType.getName());
   }

   @Override
   public Collection<ArtifactType> getEternalArtifactTypes() throws OseeCoreException {
      List<ArtifactType> artifactTypes = new ArrayList<ArtifactType>();
      try {
         artifactTypes.add(ArtifactTypeManager.getType(VersionArtifact.ARTIFACT_NAME));
         artifactTypes.add(ArtifactTypeManager.getType(TeamDefinitionArtifact.ARTIFACT_NAME));
         artifactTypes.add(ArtifactTypeManager.getType(ActionableItemArtifact.ARTIFACT_NAME));
         artifactTypes.add(ArtifactTypeManager.getType(WorkRuleDefinition.ARTIFACT_NAME));
         artifactTypes.add(ArtifactTypeManager.getType(WorkFlowDefinition.ARTIFACT_NAME));
         artifactTypes.add(ArtifactTypeManager.getType(WorkWidgetDefinition.ARTIFACT_NAME));
         artifactTypes.add(ArtifactTypeManager.getType(WorkPageDefinition.ARTIFACT_NAME));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
      return artifactTypes;
   }

}