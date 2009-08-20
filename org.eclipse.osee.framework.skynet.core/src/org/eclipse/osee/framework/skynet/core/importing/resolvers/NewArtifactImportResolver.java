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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactProcessor;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;

/**
 * @author Ryan D. Brooks
 */
public class NewArtifactImportResolver implements IArtifactImportResolver {
   private final ArtifactType primaryArtifactType;
   private final ArtifactType secondaryArtifactType;

   public NewArtifactImportResolver(ArtifactType primaryArtifactType, ArtifactType secondaryArtifactType) {
      this.primaryArtifactType = primaryArtifactType;
      this.secondaryArtifactType = secondaryArtifactType;
   }

   public Artifact resolve(final RoughArtifact roughArtifact, final Branch branch) throws OseeCoreException {
      ArtifactType artifactType = null;
      if (roughArtifact.getRoughArtifactKind() == RoughArtifactKind.PRIMARY) {
         artifactType = primaryArtifactType;
      } else if (roughArtifact.getRoughArtifactKind() == RoughArtifactKind.SECONDARY) {
         artifactType = secondaryArtifactType;
      } else if (roughArtifact.getRoughArtifactKind() == RoughArtifactKind.CONTAINER) {
         artifactType = ArtifactTypeManager.getType("Folder");
      }

      Artifact realArtifact =
            artifactType.getFactory().makeNewArtifact(branch, artifactType, roughArtifact.getGuid(),
                  roughArtifact.getHumandReadableId(), new ArtifactProcessor() {
                     @Override
                     public void run(Artifact artifact) throws OseeCoreException {
                        translateAttributes(roughArtifact, artifact);
                     }
                  });

      return realArtifact;
   }

   protected void translateAttributes(RoughArtifact roughArtifact, Artifact artifact) throws OseeCoreException {
      for (Entry<String, String> roughtAttribute : roughArtifact.getAttributes().entrySet()) {
         if (roughtAttribute.getKey() != null) {
            artifact.addAttributeFromString(roughtAttribute.getKey(), roughtAttribute.getValue());
         }
      }
      transferBinaryAttributes(roughArtifact, artifact);
   }

   private void transferBinaryAttributes(RoughArtifact roughArtifact, Artifact artifact) throws OseeCoreException {
      for (Entry<String, URI> entry : roughArtifact.getURIAttributes().entrySet()) {
         try {
            artifact.setSoleAttributeFromStream(entry.getKey(), new BufferedInputStream(
                  entry.getValue().toURL().openStream()));
         } catch (MalformedURLException ex) {
            throw new OseeWrappedException(ex);
         } catch (IOException ex) {
            throw new OseeWrappedException(ex);
         }
      }
   }
}