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
package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeFactory implements IOseeTypeFactory {

   public ArtifactTypeFactory() {
   }

   public ArtifactType create(String guid, boolean isAbstract, String name) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(name, "artifact type name");
      String checkedGuid = Conditions.checkGuidCreateIfNeeded(guid);
      return new ArtifactType(checkedGuid, name, isAbstract);
   }

   public ArtifactType createOrUpdate(ArtifactTypeCache cache, String guid, boolean isAbstract, String name) throws OseeCoreException {
      ArtifactType artifactType = cache.getByGuid(guid);
      if (artifactType == null) {
         artifactType = create(guid, isAbstract, name);
      } else {
         cache.decache(artifactType);
         artifactType.setName(name);
         artifactType.setAbstract(isAbstract);
      }
      cache.cache(artifactType);
      return artifactType;
   }

   public ArtifactType createOrUpdate(IOseeCache<ArtifactType> cache, int uniqueId, ModificationType modificationType, String guid, boolean isAbstract, String name) throws OseeCoreException {
      ArtifactType artifactType = cache.getById(uniqueId);
      if (artifactType == null) {
         artifactType = create(guid, isAbstract, name);
         artifactType.setId(uniqueId);
         artifactType.setModificationType(modificationType);
      } else {
         cache.decache(artifactType);
         artifactType.setName(name);
         artifactType.setAbstract(isAbstract);
      }
      cache.cache(artifactType);
      return artifactType;
   }
}
