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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;

/**
 * @author Donald G. Dunne
 */
public class SkynetCoreArtifactFactory extends ArtifactFactory {

   public static Collection<String> BASIC_ARTIFACTS =
         Arrays.asList("User", "User Group", "Code Unit", "Component", "Spreadsheet", "Artifact", "Folder",
               "Root Artifact", "Universal Group", "Url", "General Data", "Workspace File");

   public static Collection<String> NATIVE_ARTIFACTS = Arrays.asList("General Document");

   public static Collection<String> WORD_ARTIFACTS =
         Arrays.asList("Renderer Template", "Support Document", "Heading", "Support Document", "Saftey Assessment");

   @SuppressWarnings("unchecked")
   public SkynetCoreArtifactFactory() {
      super(Collections.setUnion(BASIC_ARTIFACTS, NATIVE_ARTIFACTS, WORD_ARTIFACTS));
   }

   public @Override
   Artifact getArtifactInstance(String guid, String humandReadableId, Branch branch, ArtifactType artifactType) throws OseeCoreException {
      if (artifactType.getName().equals(User.ARTIFACT_NAME)) {
         return new User(this, guid, humandReadableId, branch, artifactType);
      }
      if (BASIC_ARTIFACTS.contains(artifactType.getName())) {
         return new Artifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (NATIVE_ARTIFACTS.contains(artifactType.getName())) {
         return new NativeArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (WORD_ARTIFACTS.contains(artifactType.getName())) {
         return new WordArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      throw new OseeArgumentException("did not recognize the artifact type: " + artifactType.getName());
   }

}
