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
package org.eclipse.osee.framework.ui.skynet.Import;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactProcessor;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;

/**
 * @author Ryan D. Brooks
 */
public class NewArtifactImportResolver implements IArtifactImportResolver {

   public NewArtifactImportResolver() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.IArtifactImportResolver#resolve(org.eclipse.osee.framework.ui.skynet.Import.RoughArtifact)
    */
   public Artifact resolve(final RoughArtifact roughArtifact) throws OseeCoreException {
      ArtifactType descriptor = roughArtifact.getDescriptorForGetReal();

      Artifact realArtifact = null;
      ArtifactFactory factory = descriptor.getFactory();
      realArtifact =
            factory.makeNewArtifact(roughArtifact.getBranch(), descriptor, roughArtifact.getGuid(),
                  roughArtifact.getHumandReadableId(), new ArtifactProcessor() {
                     @Override
                     public void run(Artifact artifact) throws OseeCoreException {
                        roughArtifact.conferAttributesUpon(artifact);
                     }
                  });

      return realArtifact;
   }

}
