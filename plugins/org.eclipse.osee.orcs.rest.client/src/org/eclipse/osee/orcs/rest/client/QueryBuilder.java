/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.client;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResult;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface QueryBuilder {

   public static AttributeTypeId ANY_ATTRIBUTE_TYPE = AttributeTypeToken.valueOf(Long.MIN_VALUE, "Any Attribute Type");

   QueryBuilder includeDeleted();

   QueryBuilder includeDeleted(boolean enabled);

   boolean areDeletedIncluded();

   QueryBuilder fromTransaction(TransactionId transactionId);

   TransactionId getFromTransaction();

   QueryBuilder headTransaction();

   boolean isHeadTransaction();

   QueryBuilder excludeDeleted();

   /**
    * Search criteria that finds a given artifact id
    */
   QueryBuilder andLocalId(ArtifactId artifactId);

   /**
    * Search criteria that finds a given artifact ids
    */
   QueryBuilder andLocalIds(Collection<? extends ArtifactId> artifactIds);

   /**
    * Search criteria that finds a given artifact with guids
    */
   QueryBuilder andGuids(String... ids) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact with guids
    */
   QueryBuilder andGuids(List<String> ids) throws OseeCoreException;

   /**
    * Artifacts matching token id(s)
    */
   QueryBuilder andIds(ArtifactToken... artifactToken) throws OseeCoreException;

   /**
    * Artifacts matching token id(s)
    */
   QueryBuilder andIds(Collection<? extends ArtifactToken> artifactTokens) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact type using type inheritance
    */
   QueryBuilder andIsOfType(ArtifactTypeId... artifactType) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact types using type inheritance
    */
   QueryBuilder andIsOfType(Collection<? extends ArtifactTypeId> artifactType) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact type by matching type exactly
    */
   QueryBuilder andTypeEquals(ArtifactTypeId... artifactType);

   /**
    * Search criteria that finds a given artifact types by matching type exactly
    */
   QueryBuilder andTypeEquals(Collection<? extends ArtifactTypeId> artifactType);

   /**
    * Search criteria that checks for the existence of an attribute type(s).
    */
   QueryBuilder andExists(AttributeTypeId... attributeType) throws OseeCoreException;

   /**
    * Search criteria that checks for the existence of an attribute types.
    */
   QueryBuilder andExists(Collection<? extends AttributeTypeId> attributeTypes) throws OseeCoreException;

   /**
    * Search criteria that checks for the non-existence of an attribute type(s).
    */
   QueryBuilder andNotExists(AttributeTypeId attributeType) throws OseeCoreException;

   /**
    * Search criteria that checks for the non-existence of an attribute type(s).
    */
   QueryBuilder andNotExists(Collection<? extends AttributeTypeId> attributeTypes) throws OseeCoreException;

   /**
    * Search criteria that follows the relation link ending on the given side
    *
    * @param relationType the type to start following the link from
    */
   QueryBuilder andExists(IRelationType relationType) throws OseeCoreException;

   /**
    * Search criteria that follows the relation link ending on the given side
    *
    * @param relationTypeSide the type to start following the link from
    */
   QueryBuilder andExists(RelationTypeSide relationTypeSide) throws OseeCoreException;

   /**
    * Search criteria that checks for non-existence of a relation type
    *
    * @param relationType the type to check for non-existence
    */
   QueryBuilder andNotExists(IRelationType relationType) throws OseeCoreException;

   /**
    * Search criteria that checks for non-existence of a relation type
    *
    * @param relationTypeSide the type to check for non-existence
    */
   QueryBuilder andNotExists(RelationTypeSide relationTypeSide) throws OseeCoreException;

   /**
    * Artifact name equals value
    */
   QueryBuilder andNameEquals(String artifactName) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   QueryBuilder and(AttributeTypeId attributeType, Collection<String> values, QueryOption... options) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value relative to the given value based
    * on the operator provided.
    */
   QueryBuilder and(AttributeTypeId attributeType, String value, QueryOption... options) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   QueryBuilder and(Collection<? extends AttributeTypeId> attributeTypes, String value, QueryOption... options) throws OseeCoreException;

   /**
    * Search for related artifacts
    *
    * @param relationTypeSide the type-side to search on
    */
   QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, ArtifactId... artifacts) throws OseeCoreException;

   /**
    * Search for related artifacts
    *
    * @param relationTypeSide the type-side to search on
    */
   QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, Collection<ArtifactId> artifactIds) throws OseeCoreException;

   /**
    * Executes query
    *
    * @return artifact search results
    */
   SearchResult getSearchResult(RequestType request) throws OseeCoreException;

   /**
    * Count search results
    */
   int getCount() throws OseeCoreException;

   /**
    * Convenience method for getting art ids of results
    *
    * @return artifact search ids
    */
   List<ArtifactId> getIds();

   QueryBuilder getQueryBuilder();
}
