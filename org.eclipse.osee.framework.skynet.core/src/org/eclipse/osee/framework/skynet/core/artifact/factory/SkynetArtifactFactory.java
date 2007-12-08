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
package org.eclipse.osee.framework.skynet.core.artifact.factory;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceFileArtifact;

/**
 * @author Ryan D. Brooks
 */
public class SkynetArtifactFactory extends ArtifactFactory<Artifact> {
   private static SkynetArtifactFactory factory = null;

   private SkynetArtifactFactory(int factoryId) {
      super(factoryId);
   }

   public static SkynetArtifactFactory getInstance(int factoryId) {
      if (factory == null) {
         factory = new SkynetArtifactFactory(factoryId);
      }
      return factory;
   }

   public static SkynetArtifactFactory getInstance() {
      return factory;
   }

   public @Override
   Artifact getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException {
      if (factoryKey.equals(User.ARTIFACT_NAME)) {
         return new User(this, guid, humandReadableId, branch);
      }
      if (factoryKey.equals(WorkspaceFileArtifact.ARTIFACT_NAME)) {
         return new WorkspaceFileArtifact(this, guid, humandReadableId, branch);
      }
      throw new IllegalArgumentException("did not recognize the factory key: " + factoryKey);
   }
}