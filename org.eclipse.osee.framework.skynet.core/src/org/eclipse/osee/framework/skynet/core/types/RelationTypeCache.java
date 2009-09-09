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
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Roberto E. Escobar
 */
public final class RelationTypeCache extends OseeTypeCacheData<RelationType> {

   public RelationTypeCache(OseeTypeCache cache, IOseeTypeFactory factory, IOseeTypeDataAccessor dataAccessor) {
      super(cache, factory, dataAccessor);
   }

   @Override
   public void reloadCache() throws OseeCoreException {
      getDataAccessor().loadAllRelationTypes(getCache(), getDataFactory());
   }

   @Override
   protected void storeItems(Collection<RelationType> items) throws OseeCoreException {
      getDataAccessor().storeRelationType(items);
   }

   public RelationType createType(String guid, String typeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isUserOrdered, String defaultOrderTypeGuid) throws OseeCoreException {
      RelationType relationType = getTypeByGuid(guid);
      if (relationType == null) {
         relationType =
               getDataFactory().createRelationType(guid, typeName, sideAName, sideBName, artifactTypeSideA,
                     artifactTypeSideB, multiplicity, isUserOrdered, defaultOrderTypeGuid);
      } else {
         decacheType(relationType);

      }
      cacheType(relationType);
      return relationType;
   }
}
