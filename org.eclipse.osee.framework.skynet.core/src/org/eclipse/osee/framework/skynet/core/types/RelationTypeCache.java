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

import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Roberto E. Escobar
 */
public final class RelationTypeCache extends AbstractOseeCache<RelationType> {

   public RelationTypeCache(IOseeTypeFactory factory, IOseeDataAccessor<RelationType> dataAccessor) {
      super(factory, dataAccessor);
   }

   public RelationType createType(String guid, String typeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, String defaultOrderTypeGuid) throws OseeCoreException {
      RelationType relationType = getByGuid(guid);
      if (relationType == null) {
         relationType =
               getDataFactory().createRelationType(this, guid, typeName, sideAName, sideBName, artifactTypeSideA,
                     artifactTypeSideB, multiplicity, defaultOrderTypeGuid);
      } else {
         decache(relationType);
         relationType.setFields(typeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity,
               defaultOrderTypeGuid);
      }
      cache(relationType);
      return relationType;
   }
}
