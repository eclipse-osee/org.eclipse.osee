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
package org.eclipse.osee.ats.config.demo.artifact;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactory;

/**
 * Provides the factory for the loading of the XYZ demo artifact types.
 * 
 * @author Donald G. Dunne
 */
public class DemoArtifactFactory extends ArtifactFactory<Artifact> {
   private static DemoArtifactFactory factory = null;

   private DemoArtifactFactory(int factoryId) {
      super(factoryId);
   }

   public static DemoArtifactFactory getInstance(int factoryId) {
      if (factory == null) {
         factory = new DemoArtifactFactory(factoryId);
      }
      return factory;
   }

   public static DemoArtifactFactory getInstance() {
      return factory;
   }

   @Override
   public Artifact getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException {
      if (factoryKey.equals(DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME)) return new DemoCodeTeamWorkflowArtifact(this,
            guid, humandReadableId, branch);
      if (factoryKey.equals(DemoTestTeamWorkflowArtifact.ARTIFACT_NAME)) return new DemoTestTeamWorkflowArtifact(this,
            guid, humandReadableId, branch);
      if (factoryKey.equals(DemoReqTeamWorkflowArtifact.ARTIFACT_NAME)) return new DemoReqTeamWorkflowArtifact(this,
            guid, humandReadableId, branch);
      throw new IllegalArgumentException("did not recognize the factory key: " + factoryKey);
   }
}