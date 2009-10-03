/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.types.field;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactSuperTypeField extends AbstractOseeField<Collection<ArtifactType>> {

   private final ArtifactType artifactType;
   private final ArtifactTypeCache cache;

   public ArtifactSuperTypeField(ArtifactTypeCache cache, ArtifactType artifactType) {
      super();
      this.artifactType = artifactType;
      this.cache = cache;
   }

   @Override
   public Collection<ArtifactType> get() throws OseeCoreException {
      return cache.getArtifactSuperType(artifactType);
   }

   @Override
   public void set(Collection<ArtifactType> superType) throws OseeCoreException {
      Collection<ArtifactType> original = get();
      cache.cacheArtifactSuperType(artifactType, superType);
      Collection<ArtifactType> newTypes = artifactType.getSuperArtifactTypes();
      isDirty |= ChangeUtil.isDifferent(original, newTypes);
   }
}