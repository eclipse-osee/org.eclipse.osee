/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.model.type;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeFactory implements IOseeTypeFactory {

   public RelationType create(Long guid, String name, String sideAName, String sideBName, ArtifactTypeToken artifactTypeSideA, ArtifactTypeToken artifactTypeSideB, RelationTypeMultiplicity multiplicity, RelationSorter defaultRelationSorter) {
      Conditions.checkNotNullOrEmpty(name, "relation type name");
      Conditions.checkNotNullOrEmpty(sideAName, "side A name");
      Conditions.checkNotNullOrEmpty(sideBName, "side B name");
      Conditions.checkNotNull(artifactTypeSideA, "artifact type A");
      Conditions.checkNotNull(artifactTypeSideB, "relation type B");
      Conditions.checkNotNull(multiplicity, "multiplicity");
      return new RelationType(guid, name, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity,
         defaultRelationSorter);
   }

   public RelationType createOrUpdate(RelationTypeCache cache, Long guid, String typeName, String sideAName, String sideBName, ArtifactTypeToken artifactTypeSideA, ArtifactTypeToken artifactTypeSideB, RelationTypeMultiplicity multiplicity, RelationSorter defaultRelationSorter) {
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
