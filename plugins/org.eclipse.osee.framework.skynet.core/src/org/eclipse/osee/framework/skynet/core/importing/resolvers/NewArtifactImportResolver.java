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
package org.eclipse.osee.framework.skynet.core.importing.resolvers;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactProcessor;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;

/**
 * @author Ryan D. Brooks
 */
public class NewArtifactImportResolver implements IArtifactImportResolver {
   private final IArtifactType primaryArtifactType;
   private final IArtifactType secondaryArtifactType;

   public NewArtifactImportResolver(IArtifactType primaryArtifactType, IArtifactType secondaryArtifactType) {
      this.primaryArtifactType = primaryArtifactType;
      this.secondaryArtifactType = secondaryArtifactType;
   }

   @Override
   public Artifact resolve(final RoughArtifact roughArtifact, final Branch branch, Artifact realParent, Artifact root) throws OseeCoreException {
      IArtifactType artifactType = getArtifactType(roughArtifact.getRoughArtifactKind());

      Artifact realArtifact =
         ArtifactTypeManager.getFactory(artifactType).makeNewArtifact(branch, artifactType, roughArtifact.getGuid(),
            roughArtifact.getHumanReadableId(), new ArtifactProcessor() {
               @Override
               public void run(Artifact artifact) throws OseeCoreException {
                  roughArtifact.translateAttributes(artifact);
               }
            });

      return realArtifact;
   }

   private IArtifactType getArtifactType(RoughArtifactKind kind) throws OseeCoreException {
      switch (kind) {
         case PRIMARY:
            return primaryArtifactType;
         case SECONDARY:
            return secondaryArtifactType;
         case CONTAINER:
            return CoreArtifactTypes.Folder;
         default:
            throw new OseeCoreException("Unknown Artifact Kind " + kind);
      }
   }

}
