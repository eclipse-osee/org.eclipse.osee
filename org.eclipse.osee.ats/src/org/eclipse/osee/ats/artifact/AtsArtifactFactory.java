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

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactFactory extends ArtifactFactory {
   private static AtsArtifactFactory factory = null;

   private AtsArtifactFactory(int factoryId) {
      super(factoryId);
   }

   public static AtsArtifactFactory getInstance(int factoryId) {
      if (factory == null) {
         factory = new AtsArtifactFactory(factoryId);
      }
      return factory;
   }

   public static AtsArtifactFactory getInstance() {
      return factory;
   }

   @Override
   public Artifact getArtifactInstance(String guid, String humandReadableId, String factoryKey, Branch branch, ArtifactType artifactType) {
      if (factoryKey.equals(ActionArtifact.ARTIFACT_NAME)) return new ActionArtifact(this, guid, humandReadableId,
            branch, artifactType);
      if (factoryKey.equals(TaskArtifact.ARTIFACT_NAME)) return new TaskArtifact(this, guid, humandReadableId, branch,
            artifactType);
      if (factoryKey.equals(TeamWorkFlowArtifact.ARTIFACT_NAME)) return new TeamWorkFlowArtifact(this, guid,
            humandReadableId, branch, artifactType);
      if (factoryKey.equals(TeamDefinitionArtifact.ARTIFACT_NAME)) return new TeamDefinitionArtifact(this, guid,
            humandReadableId, branch, artifactType);
      if (factoryKey.equals(VersionArtifact.ARTIFACT_NAME)) return new VersionArtifact(this, guid, humandReadableId,
            branch, artifactType);
      if (factoryKey.equals(ActionableItemArtifact.ARTIFACT_NAME)) return new ActionableItemArtifact(this, guid,
            humandReadableId, branch, artifactType);
      if (factoryKey.equals(DecisionReviewArtifact.ARTIFACT_NAME)) return new DecisionReviewArtifact(this, guid,
            humandReadableId, branch, artifactType);
      if (factoryKey.equals(PeerToPeerReviewArtifact.ARTIFACT_NAME)) return new PeerToPeerReviewArtifact(this, guid,
            humandReadableId, branch, artifactType);
      throw new IllegalArgumentException("did not recognize the factory key: " + factoryKey);
   }
}