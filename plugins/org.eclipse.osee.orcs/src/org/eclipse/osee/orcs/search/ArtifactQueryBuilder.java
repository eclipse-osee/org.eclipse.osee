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
package org.eclipse.osee.orcs.search;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface ArtifactQueryBuilder<T> {

   public static AttributeTypeToken ANY_ATTRIBUTE_TYPE =
      AttributeTypeToken.valueOf(Long.MIN_VALUE, "Any Attribute Type");

   T includeDeletedArtifacts();

   T includeDeletedArtifacts(boolean enabled);

   boolean areDeletedArtifactsIncluded();

   T includeDeletedAttributes();

   T includeDeletedAttributes(boolean enabled);

   boolean areDeletedAttributesIncluded();

   T includeDeletedRelations();

   T includeDeletedRelations(boolean enabled);

   boolean areDeletedRelationsIncluded();

   T fromTransaction(TransactionId transaction);

   TransactionId getFromTransaction();

   T headTransaction();

   boolean isHeadTransaction();

   T excludeDeleted();

   T andId(ArtifactId id);

   T andIds(Collection<? extends ArtifactId> ids);

   T andIds(ArtifactId... ids);

   /**
    * Search criteria that finds the artifact with given artifact id
    */
   T andUuid(long id);

   /**
    * Search criteria that finds the artifact with given artifact id
    */
   T andId(long id);

   /**
    * Search criteria that finds the artifacts of given uuids (artifact ids)
    */
   T andUuids(Collection<Long> uuids);

   /**
    * Search criteria that finds the artifacts of given uuids (artifact ids)
    */
   T andIdsL(Collection<Long> ids);

   /**
    * Search criteria that finds a given artifact with guid
    */
   T andGuid(String guid);

   /**
    * Search criteria that finds a given artifact with guids
    */
   T andGuids(Collection<String> ids);

   /**
    * Search criteria that finds a given artifact type using type inheritance
    */
   T andIsOfType(ArtifactTypeId... artifactType);

   /**
    * Search criteria that finds a given artifact types using type inheritance
    */
   T andIsOfType(Collection<? extends ArtifactTypeId> artifactType);

   /**
    * Search criteria that finds a given artifact types by matching type exactly
    */
   T andTypeEquals(ArtifactTypeId... artifactType);

   /**
    * Search criteria that finds a given artifact types by matching type exactly
    */
   T andTypeEquals(Collection<? extends ArtifactTypeId> artifactType);

   /**
    * Search criteria that checks for the existence of an attribute type(s).
    */
   T andExists(AttributeTypeId... attributeType);

   /**
    * Search criteria that checks for the existence of an attribute types.
    */
   T andExists(Collection<AttributeTypeId> attributeTypes);

   /**
    * Search criteria that checks for the non-existence of an attribute type(s).
    */
   T andNotExists(Collection<AttributeTypeId> attributeTypes);

   /**
    * Search criteria that checks for the non-existence of an attribute type(s).
    */
   T andNotExists(AttributeTypeId attributeType);

   /**
    * Search criteria that checks for the non-existence of an attribute type(s).
    */
   T andNotExists(AttributeTypeId attributeType, String value);

   /**
    * Search criteria that follows the relation link ending on the given side
    *
    * @param relationType the type to start following the link from
    */
   T andExists(IRelationType relationType);

   /**
    * Search criteria that checks for non-existence of a relation type
    *
    * @param relationTypeSide the type to check for non-existence
    */
   T andNotExists(RelationTypeSide relationTypeSide);

   /**
    * Search criteria that follows the relation link ending on the given side
    *
    * @param relationTypeSide the type to start following the link from
    */
   T andExists(RelationTypeSide relationTypeSide);

   /**
    * Search criteria that checks for non-existence of a relation type
    *
    * @param relationType the type to check for non-existence
    */
   T andNotExists(IRelationType relationType);

   /**
    * Artifact name exactly equals value
    */
   T andNameEquals(String artifactName);

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   T and(AttributeTypeId attributeType, Collection<String> values, QueryOption... options);

   /**
    * Search criteria that finds an attribute of the given type with its current value relative to the given value.
    */
   T and(AttributeTypeId attributeType, String value, QueryOption... options);

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   T and(Collection<AttributeTypeId> attributeTypes, String value, QueryOption... options);

   T and(Collection<AttributeTypeId> attributeTypes, Collection<String> value, QueryOption... options);

   /**
    * Search for related artifacts
    *
    * @param relationTypeSide the type-side to search on
    */
   T andRelatedTo(RelationTypeSide relationTypeSide, ArtifactReadable... artifacts);

   /**
    * Search for related artifacts
    *
    * @param relationTypeSide the type-side to search on
    */
   T andRelatedTo(RelationTypeSide relationTypeSide, Collection<? extends ArtifactId> artifacts);

   /**
    * Search for related artifacts
    *
    * @param relationTypeSide the type-side to search on
    */
   T andRelatedTo(RelationTypeSide relationTypeSide, ArtifactId artifactId);

   /**
    * Search related artifacts with specific criteria. Will only follow first level of relations
    *
    * @param relationTypeSide the type-side to search on
    */
   T followRelation(RelationTypeSide relationTypeSide);

   /**
    * @return DefaultHeirarchicalRootArtifact
    */
   T andIsHeirarchicalRootArtifact();
}