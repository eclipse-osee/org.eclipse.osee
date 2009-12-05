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

import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeFactory implements IOseeTypeFactory {

   public RelationTypeFactory() {
   }

   public RelationType create(String guid, String name, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, String defaultOrderTypeGuid) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(name, "relation type name");
      Conditions.checkNotNullOrEmpty(sideAName, "side A name");
      Conditions.checkNotNullOrEmpty(sideBName, "side B name");
      Conditions.checkNotNull(artifactTypeSideA, "artifact type A");
      Conditions.checkNotNull(artifactTypeSideB, "relation type B");
      Conditions.checkNotNull(multiplicity, "multiplicity");
      String checkedGuid = Conditions.checkGuidCreateIfNeeded(guid);
      return new RelationType(checkedGuid, name, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB,
            multiplicity, defaultOrderTypeGuid);
   }

   public RelationType createOrUpdate(RelationTypeCache cache, String guid, String typeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, String defaultOrderTypeGuid) throws OseeCoreException {
      RelationType relationType = cache.getByGuid(guid);
      if (relationType == null) {
         relationType =
               create(guid, typeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity,
                     defaultOrderTypeGuid);
      } else {
         cache.decache(relationType);
         relationType.setFields(typeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity,
               defaultOrderTypeGuid);
      }
      cache.cache(relationType);
      return relationType;
   }

   public RelationType createOrUpdate(IOseeCache<RelationType> cache, int typeId, ModificationType modificationType, String guid, String typeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, String defaultOrderTypeGuid) throws OseeCoreException {
      RelationType relationType = cache.getById(typeId);
      if (relationType == null) {
         relationType =
               create(guid, typeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity,
                     defaultOrderTypeGuid);
         relationType.setId(typeId);
         relationType.setModificationType(modificationType);
      } else {
         cache.decache(relationType);
         relationType.setFields(typeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity,
               defaultOrderTypeGuid);
      }
      cache.cache(relationType);
      return relationType;
   }
}
