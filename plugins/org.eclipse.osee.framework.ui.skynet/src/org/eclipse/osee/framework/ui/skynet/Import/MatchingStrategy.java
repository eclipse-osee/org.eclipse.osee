/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.Import;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.AttributeBasedArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.GuidBasedArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.NewArtifactImportResolver;

public enum MatchingStrategy {
   ATTRIBUTE(), GUID(), NONE();

   private MatchingStrategy() {
   }

   public IArtifactImportResolver getResolver(ArtifactType primaryArtifactType, Collection<AttributeType> nonChangingAttributes, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) throws OseeCoreException {
      ArtifactType secondaryArtifactType = ArtifactTypeManager.getType("Heading");
      if (this == ATTRIBUTE) {
         return new AttributeBasedArtifactResolver(primaryArtifactType, secondaryArtifactType,
               nonChangingAttributes, createNewIfNotExist, deleteUnmatchedArtifacts);
      } else if (this == GUID) {
         return new GuidBasedArtifactResolver(primaryArtifactType, secondaryArtifactType, createNewIfNotExist,
               deleteUnmatchedArtifacts);
      } else {
         return new NewArtifactImportResolver(primaryArtifactType, secondaryArtifactType);
      }
   }
}
