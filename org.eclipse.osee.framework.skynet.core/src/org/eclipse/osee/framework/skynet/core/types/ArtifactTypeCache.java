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
package org.eclipse.osee.framework.skynet.core.types;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactTypeCache extends OseeTypeCacheData<ArtifactType> {

   public ArtifactTypeCache(OseeTypeCache cache, IOseeTypeFactory factory, IOseeTypeDataAccessor dataAccessor) {
      super(cache, factory, dataAccessor);
   }

   @Override
   public void reloadCache() throws OseeCoreException {
      getDataAccessor().loadAllArtifactTypes(getCache(), getDataFactory());
   }

   @Override
   protected void storeItems(Collection<ArtifactType> items) throws OseeCoreException {
      getDataAccessor().storeArtifactType(getCache(), items);
   }

   public ArtifactType createType(String guid, boolean isAbstract, String artifactTypeName) throws OseeCoreException {
      ArtifactType artifactType = getTypeByGuid(guid);
      if (artifactType == null) {
         artifactType = getDataFactory().createArtifactType(guid, isAbstract, artifactTypeName, getCache());
      } else {
         decacheType(artifactType);
         artifactType.setName(artifactTypeName);
         artifactType.setAbstract(isAbstract);
      }
      cacheType(artifactType);
      return artifactType;
   }
}
