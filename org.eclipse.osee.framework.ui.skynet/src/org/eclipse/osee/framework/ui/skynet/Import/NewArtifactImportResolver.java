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

import java.io.IOException;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.factory.PolymorphicArtifactFactory;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;

/**
 * @author Ryan D. Brooks
 */
public class NewArtifactImportResolver implements IArtifactImportResolver {
   private static boolean usePolymorphicArtifactFactory = false;
   private static PolymorphicArtifactFactory polymorphicArtifactFactory = PolymorphicArtifactFactory.getInstance();

   public NewArtifactImportResolver() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.IArtifactImportResolver#resolve(org.eclipse.osee.framework.ui.skynet.Import.RoughArtifact)
    */
   public Artifact resolve(RoughArtifact roughArtifact) throws SQLException, IllegalStateException, IOException {
      ArtifactSubtypeDescriptor descriptor = roughArtifact.getDescriptorForGetReal();

      Artifact realArtifact = null;
      if (usePolymorphicArtifactFactory) {
         realArtifact =
               polymorphicArtifactFactory.makeNewArtifact(roughArtifact.getBranch(), descriptor,
                     roughArtifact.getGuid(), roughArtifact.getHumandReadableId());
      } else {
         realArtifact =
               descriptor.makeNewArtifact(roughArtifact.getBranch(), roughArtifact.getGuid(),
                     roughArtifact.getHumandReadableId());
      }

      roughArtifact.conferAttributesUpon(realArtifact);

      return realArtifact;
   }

   /**
    * @return the usePolymorphicArtifactFactory
    */
   public static boolean isUsePolymorphicArtifactFactory() {
      return usePolymorphicArtifactFactory;
   }

   /**
    * @param usePolymorphicArtifactFactory the usePolymorphicArtifactFactory to set
    */
   public static void setUsePolymorphicArtifactFactory(boolean usePolymorphicArtifactFactory) {
      NewArtifactImportResolver.usePolymorphicArtifactFactory = usePolymorphicArtifactFactory;
   }
}
