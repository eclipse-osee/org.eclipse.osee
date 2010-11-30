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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.AttributeBasedArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.GuidBasedArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.NewArtifactImportResolver;

public enum MatchingStrategy {
   ATTRIBUTE(),
   GUID(),
   NONE();

   public IArtifactImportResolver getResolver(IArtifactType primaryArtifactType, Collection<IAttributeType> nonChangingAttributes, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      switch (this) {
         case ATTRIBUTE:
            return new AttributeBasedArtifactResolver(primaryArtifactType, CoreArtifactTypes.Heading,
               nonChangingAttributes, createNewIfNotExist, deleteUnmatchedArtifacts);
         case GUID:
            return new GuidBasedArtifactResolver(primaryArtifactType, CoreArtifactTypes.Heading, createNewIfNotExist,
               deleteUnmatchedArtifacts);
         default:
            return new NewArtifactImportResolver(primaryArtifactType, CoreArtifactTypes.Heading);
      }
   }
}
