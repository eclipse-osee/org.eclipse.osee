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
package org.eclipse.osee.orcs.core.internal.search;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllArtifacts;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactHrids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeOther;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("unused")
public class CriteriaFactory {

   private final ArtifactTypes artifactTypeCache;
   private final AttributeTypes attributeTypeCache;

   public CriteriaFactory(ArtifactTypes artifactTypeCache, AttributeTypes attributeTypeCache) {
      this.artifactTypeCache = artifactTypeCache;
      this.attributeTypeCache = attributeTypeCache;
   }

   private Collection<? extends IAttributeType> checkForAnyType(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException {
      Collection<? extends IAttributeType> toReturn;
      if (attributeTypes.contains(QueryBuilder.ANY_ATTRIBUTE_TYPE)) {
         Collection<IAttributeType> temp = new LinkedList<IAttributeType>();
         temp.addAll(attributeTypeCache.getAll());
         toReturn = temp;
      } else {
         toReturn = attributeTypes;
      }
      return toReturn;
   }

   public Criteria<QueryOptions> createExistsCriteria(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException {
      return new CriteriaAttributeTypeExists(attributeTypes);
   }

   public Criteria<QueryOptions> createExistsCriteria(IRelationType relationType) throws OseeCoreException {
      return new CriteriaRelationTypeExists(relationType);
   }

   public Criteria<QueryOptions> createAttributeCriteria(IAttributeType attributeType, Operator operator, Collection<String> values) throws OseeCoreException {
      if (operator == Operator.EQUAL) {
         return createAttributeCriteria(Collections.singleton(attributeType), values);
      } else {
         return new CriteriaAttributeOther(attributeType, values, operator);
      }
   }

   public Criteria<QueryOptions> createAttributeCriteria(Collection<? extends IAttributeType> attributeTypes, Collection<String> values, QueryOption... options) throws OseeCoreException {
      Collection<? extends IAttributeType> types = checkForAnyType(attributeTypes);
      boolean isIncludeAllTypes = attributeTypes.contains(QueryBuilder.ANY_ATTRIBUTE_TYPE);
      return new CriteriaAttributeKeywords(isIncludeAllTypes, types, attributeTypeCache, values, options);
   }

   public Criteria<QueryOptions> createArtifactTypeCriteria(Collection<? extends IArtifactType> artifactTypes) throws OseeCoreException {
      return new CriteriaArtifactType(artifactTypeCache, artifactTypes, false);
   }

   public Criteria<QueryOptions> createArtifactTypeCriteriaWithInheritance(Collection<? extends IArtifactType> artifactTypes) throws OseeCoreException {
      return new CriteriaArtifactType(artifactTypeCache, artifactTypes, true);
   }

   public Criteria<QueryOptions> createArtifactIdCriteria(Collection<Integer> artifactIds) throws OseeCoreException {
      return new CriteriaArtifactIds(artifactIds);
   }

   public Criteria<QueryOptions> createArtifactGuidCriteria(Set<String> guids) throws OseeCoreException {
      return new CriteriaArtifactGuids(guids);
   }

   public Criteria<QueryOptions> createArtifactHridCriteria(Set<String> hrids) throws OseeCoreException {
      return new CriteriaArtifactHrids(hrids);
   }

   public Criteria<QueryOptions> createRelatedToCriteria(IRelationTypeSide relationType, Collection<Integer> artifactIds) throws OseeCoreException {
      return new CriteriaRelatedTo(relationType, artifactIds);
   }

   public Criteria<QueryOptions> createAllArtifactsCriteria() {
      return new CriteriaAllArtifacts();
   }

}
