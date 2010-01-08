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
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactFactory extends ArtifactFactory {

   public AtsArtifactFactory() {
      super(Arrays.asList(AtsArtifactTypes.Action.getName(), AtsArtifactTypes.PeerToPeerReview.getName(),
            AtsArtifactTypes.DecisionReview.getName(), AtsArtifactTypes.ActionableItem.getName(),
            AtsArtifactTypes.Task.getName(), AtsArtifactTypes.TeamWorkflow.getName(),
            AtsArtifactTypes.TeamDefinition.getName(), AtsArtifactTypes.Version.getName(),
            AtsArtifactTypes.Goal.getName()));
   }

   @Override
   public Artifact getArtifactInstance(String guid, String humandReadableId, Branch branch, ArtifactType artifactType) throws OseeCoreException {
      if (artifactType.equals(AtsArtifactTypes.Action)) {
         return new ActionArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.equals(AtsArtifactTypes.Task)) {
         return new TaskArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.equals(AtsArtifactTypes.TeamWorkflow)) {
         return new TeamWorkFlowArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.equals(AtsArtifactTypes.TeamDefinition)) {
         return new TeamDefinitionArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.equals(AtsArtifactTypes.Version)) {
         return new VersionArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.equals(AtsArtifactTypes.ActionableItem)) {
         return new ActionableItemArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.equals(AtsArtifactTypes.DecisionReview)) {
         return new DecisionReviewArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.equals(AtsArtifactTypes.PeerToPeerReview)) {
         return new PeerToPeerReviewArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.equals(AtsArtifactTypes.Goal)) {
         return new GoalArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      throw new OseeArgumentException("did not recognize the artifact type: " + artifactType.getName());
   }

   @Override
   public Collection<ArtifactType> getEternalArtifactTypes() throws OseeCoreException {
      List<ArtifactType> artifactTypes = new ArrayList<ArtifactType>();
      try {
         artifactTypes.add(ArtifactTypeManager.getType(AtsArtifactTypes.Version));
         artifactTypes.add(ArtifactTypeManager.getType(AtsArtifactTypes.TeamDefinition));
         artifactTypes.add(ArtifactTypeManager.getType(AtsArtifactTypes.ActionableItem));
         artifactTypes.add(ArtifactTypeManager.getType(WorkRuleDefinition.ARTIFACT_NAME));
         artifactTypes.add(ArtifactTypeManager.getType(CoreArtifactTypes.WorkFlowDefinition));
         artifactTypes.add(ArtifactTypeManager.getType(WorkWidgetDefinition.ARTIFACT_NAME));
         artifactTypes.add(ArtifactTypeManager.getType(WorkPageDefinition.ARTIFACT_NAME));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
      return artifactTypes;
   }

}