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

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactory;

/**
 * @author Ryan D. Brooks
 */
public class AtsArtifactFactory extends ArtifactFactory<Artifact> {
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
   public Artifact getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException {
      if (factoryKey.equals(ActionArtifact.ARTIFACT_NAME)) return new ActionArtifact(this, guid, humandReadableId,
            branch);
      if (factoryKey.equals(TaskArtifact.ARTIFACT_NAME)) return new TaskArtifact(this, guid, humandReadableId, branch);
      if (factoryKey.equals(TeamWorkFlowArtifact.ARTIFACT_NAME)) return new TeamWorkFlowArtifact(this, guid,
            humandReadableId, branch);
      if (factoryKey.equals(TeamDefinitionArtifact.ARTIFACT_NAME)) return new TeamDefinitionArtifact(this, guid,
            humandReadableId, branch);
      if (factoryKey.equals(VersionArtifact.ARTIFACT_NAME)) return new VersionArtifact(this, guid, humandReadableId,
            branch);
      if (factoryKey.equals(ActionableItemArtifact.ARTIFACT_NAME)) return new ActionableItemArtifact(this, guid,
            humandReadableId, branch);
      if (factoryKey.equals(DecisionReviewArtifact.ARTIFACT_NAME)) return new DecisionReviewArtifact(this, guid,
            humandReadableId, branch);
      if (factoryKey.equals(PeerToPeerReviewArtifact.ARTIFACT_NAME)) return new PeerToPeerReviewArtifact(this, guid,
            humandReadableId, branch);
      throw new IllegalArgumentException("did not recognize the factory key: " + factoryKey);
   }
}