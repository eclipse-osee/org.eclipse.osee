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
package org.eclipse.osee.framework.skynet.core.importing;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactProcessor;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Ryan D. Brooks
 */
public class NewArtifactImportResolver implements IArtifactImportResolver {
   private ArtifactType primaryArtifactType;
   private ArtifactType secondaryArtifactType;

   public NewArtifactImportResolver(ArtifactType primaryArtifactType, ArtifactType secondaryArtifactType) {
      this.primaryArtifactType = primaryArtifactType;
      this.secondaryArtifactType = secondaryArtifactType;
   }

   public Artifact resolve(final RoughArtifact roughArtifact) throws OseeCoreException {
      ArtifactType artifactType = null;
      if (roughArtifact.getRoughArtifactKind() == RoughArtifactKind.PRIMARY) {
         artifactType = primaryArtifactType;
      } else if (roughArtifact.getRoughArtifactKind() == RoughArtifactKind.SECONDARY) {
         artifactType = secondaryArtifactType;
      } else if (roughArtifact.getRoughArtifactKind() == RoughArtifactKind.CONTAINER) {
         artifactType = ArtifactTypeManager.getType("Folder");
      }

      Artifact realArtifact =
            artifactType.getFactory().makeNewArtifact(roughArtifact.getBranch(), artifactType, roughArtifact.getGuid(),
                  roughArtifact.getHumandReadableId(), new ArtifactProcessor() {
                     @Override
                     public void run(Artifact artifact) throws OseeCoreException {
                        roughArtifact.conferAttributesUpon(artifact);
                     }
                  });

      return realArtifact;
   }
}