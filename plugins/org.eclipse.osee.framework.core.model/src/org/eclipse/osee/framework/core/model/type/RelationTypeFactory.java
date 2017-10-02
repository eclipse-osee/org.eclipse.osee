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

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeFactory implements IOseeTypeFactory {

   public RelationType create(Long guid, String name, String sideAName, String sideBName, IArtifactType artifactTypeSideA, IArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, RelationSorter defaultRelationSorter)  {
      Conditions.checkNotNullOrEmpty(name, "relation type name");
      Conditions.checkNotNullOrEmpty(sideAName, "side A name");
      Conditions.checkNotNullOrEmpty(sideBName, "side B name");
      Conditions.checkNotNull(artifactTypeSideA, "artifact type A");
      Conditions.checkNotNull(artifactTypeSideB, "relation type B");
      Conditions.checkNotNull(multiplicity, "multiplicity");
      return new RelationType(guid, name, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity,
         defaultRelationSorter);
   }

   public RelationType createOrUpdate(RelationTypeCache cache, Long guid, String typeName, String sideAName, String sideBName, IArtifactType artifactTypeSideA, IArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, RelationSorter defaultRelationSorter)  {
      Conditions.checkNotNull(cache, "RelationTypeCache");
      RelationType relationType = cache.getByGuid(guid);
      if (relationType == null) {
         relationType = create(guid, typeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity,
            defaultRelationSorter);
         cache.cache(relationType);
      } else {
         relationType.setFields(typeName, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity,
            defaultRelationSorter);
      }
      return relationType;
   }
}
