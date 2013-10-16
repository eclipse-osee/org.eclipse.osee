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
package org.eclipse.osee.framework.core.model.type;

import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactTypeFactory implements IOseeTypeFactory {

   public ArtifactType create(Long guid, boolean isAbstract, String name) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(name, "artifact type name");
      return new ArtifactType(guid, name, isAbstract);
   }

   public ArtifactType createOrUpdate(ArtifactTypeCache cache, Long guid, boolean isAbstract, String name) throws OseeCoreException {
      Conditions.checkNotNull(cache, "ArtifactTypeCache");
      ArtifactType artifactType = cache.getByGuid(guid);
      if (artifactType == null) {
         artifactType = create(guid, isAbstract, name);
         cache.cache(artifactType);
      } else {
         artifactType.setName(name);
         artifactType.setAbstract(isAbstract);
      }
      return artifactType;
   }

   public ArtifactType createOrUpdate(IOseeCache<Long, ArtifactType> cache, int uniqueId, StorageState storageState, Long guid, boolean isAbstract, String name) throws OseeCoreException {
      Conditions.checkNotNull(cache, "ArtifactTypeCache");
      ArtifactType artifactType = cache.getById(uniqueId);
      if (artifactType == null) {
         artifactType = create(guid, isAbstract, name);
         artifactType.setId(uniqueId);
         artifactType.setStorageState(storageState);
         cache.cache(artifactType);
      } else {
         artifactType.setName(name);
         artifactType.setAbstract(isAbstract);
      }
      return artifactType;
   }
}
