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

import java.util.Arrays;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactFactory extends ArtifactFactory {

   public AtsArtifactFactory() {
      super(Arrays.asList(ActionArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME,
            DecisionReviewArtifact.ARTIFACT_NAME, ActionableItemArtifact.ARTIFACT_NAME, TaskArtifact.ARTIFACT_NAME,
            TeamWorkFlowArtifact.ARTIFACT_NAME, TeamDefinitionArtifact.ARTIFACT_NAME, VersionArtifact.ARTIFACT_NAME,
            ActionableItemArtifact.ARTIFACT_NAME));
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
      throw new OseeArgumentException("did not recognize the artifact type: " + artifactType.getName());
   }
}