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
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface QueryBuilder extends Query {

   ArtifactToken asArtifactToken();

   /**
    * @return a single ArtifactToken if exactly one found. Return ArtifactToken.SENTINEL is none found, else throw
    * exception for finding more than one
    */
   ArtifactToken asArtifactTokenOrSentinel();

   List<ArtifactToken> asArtifactTokens();

   List<ArtifactReadable> asArtifacts();

   Map<ArtifactId, ArtifactReadable> asArtifactMap();

   ArtifactReadable asArtifact();

   Map<ArtifactId, ArtifactToken> asArtifactTokenMap();

   List<ArtifactId> asArtifactIds();

   ArtifactId asArtifactId();

   List<Map<String, Object>> asArtifactMaps();

   /**
    * @return a single ArtifactId if exactly one found. Return ArtifactId.SENTINEL is none found, else throw exception
    * for finding more than one
    */
   ArtifactId asArtifactIdOrSentinel();

   /**
    * @param attributeType is used in place of the natural Name attribute to populate the name fields in the returned
    * artifact tokens
    */
   List<ArtifactToken> asArtifactTokens(AttributeTypeId attributeType);

   Map<ArtifactId, ArtifactReadable> loadArtifactMap();

   /**
    * @return artifact search results
    */
   ResultSet<ArtifactReadable> getResults();

   ArtifactReadable getArtifact();

   /**
    * @return artifact search results with match locations
    */
   ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> getMatches();

   ArtifactToken getArtifactOrNull();

   /**
    * @return first artifact or sentinal
    */
   ArtifactToken getArtifactOrSentinal();

   /**
    * @return sole artifact or sentinal or exception if > 1
    */
   ArtifactToken getAtMostOneOrSentinal();

   public static AttributeTypeToken ANY_ATTRIBUTE_TYPE =
      AttributeTypeToken.valueOf(Long.MIN_VALUE, "Any Attribute Type");

   QueryBuilder includeDeletedArtifacts();

   QueryBuilder includeDeletedArtifacts(boolean enabled);

   boolean areDeletedArtifactsIncluded();

   QueryBuilder includeDeletedAttributes();

   QueryBuilder includeDeletedAttributes(boolean enabled);

   boolean areDeletedAttributesIncluded();

   QueryBuilder includeDeletedRelations();

   QueryBuilder includeDeletedRelations(boolean enabled);

   boolean areDeletedRelationsIncluded();

   QueryBuilder fromTransaction(TransactionId transaction);

   TransactionId getFromTransaction();

   QueryBuilder headTransaction();

   boolean isHeadTransaction();

   QueryBuilder excludeDeleted();

   QueryBuilder andId(ArtifactId id);

   QueryBuilder andIds(Collection<? extends ArtifactId> ids);

   QueryBuilder andIds(ArtifactId... ids);

   /**
    * Search criteria that finds the artifact with given artifact id
    */
   QueryBuilder andUuid(long id);

   /**
    * Search criteria that finds the artifacts of given uuids (artifact ids)
    */
   QueryBuilder andUuids(Collection<Long> uuids);

   /**
    * Search criteria that finds the artifacts of given uuids (artifact ids)
    */
   QueryBuilder andIdsL(Collection<Long> ids);

   /**
    * Search criteria that finds a given artifact with guid
    */
   QueryBuilder andGuid(String guid);

   /**
    * Search criteria that finds a given artifact with guids
    */
   QueryBuilder andGuids(Collection<String> ids);

   /**
    * Search criteria that finds a given artifact type using type inheritance
    */
   QueryBuilder andIsOfType(ArtifactTypeId... artifactType);

   /**
    * Search criteria that finds a given artifact types using type inheritance
    */
   QueryBuilder andIsOfType(Collection<? extends ArtifactTypeId> artifactType);

   /**
    * Search criteria that finds a given artifact types by matching type exactly
    */
   QueryBuilder andTypeEquals(ArtifactTypeId... artifactType);

   /**
    * Search criteria that finds a given artifact types by matching type exactly
    */
   QueryBuilder andTypeEquals(Collection<? extends ArtifactTypeId> artifactType);

   /**
    * Search criteria that checks for the existence of an attribute type(s).
    */
   QueryBuilder andExists(AttributeTypeId... attributeType);

   /**
    * Search criteria that checks for the existence of an attribute types.
    */
   QueryBuilder andExists(Collection<AttributeTypeId> attributeTypes);

   /**
    * Search criteria that checks for the non-existence of an attribute type(s).
    */
   QueryBuilder andNotExists(Collection<AttributeTypeId> attributeTypes);

   /**
    * Search criteria that checks for the non-existence of an attribute type(s).
    */
   QueryBuilder andNotExists(AttributeTypeId attributeType);

   /**
    * Search criteria that checks for the non-existence of an attribute type(s).
    */
   QueryBuilder andNotExists(AttributeTypeId attributeType, String value);

   /**
    * Search criteria that follows the relation link ending on the given side
    *
    * @param relationType the type to start following the link from
    */
   QueryBuilder andExists(IRelationType relationType);

   /**
    * Search criteria that checks for non-existence of a relation type
    *
    * @param relationTypeSide the type to check for non-existence
    */
   QueryBuilder andNotExists(RelationTypeSide relationTypeSide);

   /**
    * Search criteria that follows the relation link ending on the given side
    *
    * @param relationTypeSide the type to start following the link from
    */
   QueryBuilder andExists(RelationTypeSide relationTypeSide);

   /**
    * Search criteria that checks for non-existence of a relation type
    *
    * @param relationType the type to check for non-existence
    */
   QueryBuilder andNotExists(IRelationType relationType);

   /**
    * Artifact name exactly equals value
    */
   QueryBuilder andNameEquals(String artifactName);

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   QueryBuilder and(AttributeTypeId attributeType, Collection<String> values, QueryOption... options);

   /**
    * Search criteria that finds an attribute of the given type with its current value relative to the given value.
    */
   QueryBuilder and(AttributeTypeId attributeType, String value, QueryOption... options);

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   QueryBuilder and(Collection<AttributeTypeId> attributeTypes, String value, QueryOption... options);

   QueryBuilder and(Collection<AttributeTypeId> attributeTypes, Collection<String> value, QueryOption... options);

   /**
    * Search for related artifacts
    *
    * @param relationTypeSide the type-side to search on
    */
   QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, ArtifactReadable... artifacts);

   /**
    * Search for related artifacts
    *
    * @param relationTypeSide the type-side to search on
    */
   QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, Collection<? extends ArtifactId> artifacts);

   /**
    * Search for related artifacts
    *
    * @param relationTypeSide the type-side to search on
    */
   QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, ArtifactId artifactId);

   QueryBuilder andRelatedRecursive(RelationTypeSide relationTypeSide, ArtifactId artifactId);

   /**
    * @return DefaultHeirarchicalRootArtifact
    */
   QueryBuilder andIsHeirarchicalRootArtifact();

   QueryBuilder andAttributeIs(AttributeTypeId attributeType, String value);

   QueryBuilder follow(RelationTypeSide relationTypeSide);

   /**
    * @param relationTypeSide side of of the relation following to (not starting from)
    * @param artifacType of the artifacts following to
    * @return
    */
   QueryBuilder follow(RelationTypeSide relationTypeSide, ArtifactTypeToken artifacType);

   QueryBuilder followNoSelect(RelationTypeSide relationTypeSide, ArtifactTypeToken artifacType);

   /**
    * @deprecated use follow instead, currently still needed only for ORCS script
    */
   @Deprecated
   QueryBuilder followRelation(RelationTypeSide typeSide);

}