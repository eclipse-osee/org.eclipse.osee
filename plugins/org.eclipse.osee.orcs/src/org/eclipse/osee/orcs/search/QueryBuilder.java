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
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.HasLocalId;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface QueryBuilder {

   public static IAttributeType ANY_ATTRIBUTE_TYPE = TokenFactory.createAttributeType(Long.MIN_VALUE,
      "Any Attribute Type");

   QueryBuilder includeDeletedArtifacts();

   QueryBuilder includeDeletedArtifacts(boolean enabled);

   boolean areDeletedArtifactsIncluded();

   QueryBuilder includeDeletedAttributes();

   QueryBuilder includeDeletedAttributes(boolean enabled);

   boolean areDeletedAttributesIncluded();

   QueryBuilder includeDeletedRelations();

   QueryBuilder includeDeletedRelations(boolean enabled);

   boolean areDeletedRelationsIncluded();

   QueryBuilder fromTransaction(int transactionId);

   int getFromTransaction();

   QueryBuilder headTransaction();

   boolean isHeadTransaction();

   QueryBuilder excludeDeleted();

   /**
    * Search criteria that finds a given artifact id
    */
   QueryBuilder andLocalId(int... artifactId) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact ids
    */
   QueryBuilder andLocalIds(Collection<Integer> artifactIds) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact with guid
    */
   QueryBuilder andGuid(String guid) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact with guids
    */
   QueryBuilder andGuids(Collection<String> ids) throws OseeCoreException;

   /**
    * Artifacts id(s)
    */
   QueryBuilder andIds(Identifiable<String>... ids) throws OseeCoreException;

   /**
    * Artifacts matching token id(s)
    */
   QueryBuilder andIds(Collection<? extends Identifiable<String>> ids) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact type using type inheritance
    */
   QueryBuilder andIsOfType(IArtifactType... artifactType) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact types using type inheritance
    */
   QueryBuilder andIsOfType(Collection<? extends IArtifactType> artifactType) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact types by matching type exactly
    */
   QueryBuilder andTypeEquals(IArtifactType... artifactType) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact types by matching type exactly
    */
   QueryBuilder andTypeEquals(Collection<? extends IArtifactType> artifactType) throws OseeCoreException;

   /**
    * Search criteria that checks for the existence of an attribute type(s).
    */
   QueryBuilder andExists(IAttributeType... attributeType) throws OseeCoreException;

   /**
    * Search criteria that checks for the existence of an attribute types.
    */
   QueryBuilder andExists(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException;

   /**
    * Search criteria that follows the relation link ending on the given side
    * 
    * @param relationType the type to start following the link from
    */
   QueryBuilder andExists(IRelationType relationType) throws OseeCoreException;

   /**
    * Search criteria that checks for non-existence of a relation type
    * 
    * @param relationTypeSide the type to check for non-existence
    */
   QueryBuilder andNotExists(IRelationTypeSide relationTypeSide) throws OseeCoreException;

   /**
    * Search criteria that follows the relation link ending on the given side
    * 
    * @param relationTypeSide the type to start following the link from
    */
   QueryBuilder andExists(IRelationTypeSide relationTypeSide) throws OseeCoreException;

   /**
    * Search criteria that checks for non-existence of a relation type
    * 
    * @param relationType the type to check for non-existence
    */
   QueryBuilder andNotExists(IRelationType relationType) throws OseeCoreException;

   /**
    * Artifact name equals value
    */
   QueryBuilder andNameEquals(String artifactName) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   QueryBuilder and(IAttributeType attributeType, Collection<String> values, QueryOption... options) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value relative to the given value based
    * on the operator provided.
    */
   QueryBuilder and(IAttributeType attributeType, String value, QueryOption... options) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   QueryBuilder and(Collection<IAttributeType> attributeTypes, String value, QueryOption... options) throws OseeCoreException;

   /**
    * Search for related artifacts
    * 
    * @param relationTypeSide the type-side to search on
    */
   QueryBuilder andRelatedTo(IRelationTypeSide relationTypeSide, ArtifactReadable... artifacts) throws OseeCoreException;

   /**
    * Search for related artifacts
    * 
    * @param relationTypeSide the type-side to search on
    */
   QueryBuilder andRelatedTo(IRelationTypeSide relationTypeSide, Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException;

   /**
    * Search for related artifacts
    * 
    * @param relationTypeSide the type-side to search on
    */
   QueryBuilder andRelatedToLocalIds(IRelationTypeSide relationTypeSide, int... artifactIds) throws OseeCoreException;

   /**
    * Search for related artifacts
    * 
    * @param relationTypeSide the type-side to search on
    */
   QueryBuilder andRelatedToLocalIds(IRelationTypeSide relationTypeSide, Collection<Integer> artifactIds) throws OseeCoreException;

   /**
    * Executes query
    * 
    * @return artifact search results
    */
   ResultSet<ArtifactReadable> getResults() throws OseeCoreException;

   /**
    * Executes query
    * 
    * @return artifact search results with match locations
    */
   ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> getMatches() throws OseeCoreException;

   /**
    * Executes query
    * 
    * @return localIds search results
    */
   ResultSet<HasLocalId<Integer>> getResultsAsLocalIds() throws OseeCoreException;

   /**
    * Count search results
    */
   int getCount() throws OseeCoreException;

   /**
    * Schedule a count search results
    */
   CancellableCallable<Integer> createCount() throws OseeCoreException;

   /**
    * Schedule query
    * 
    * @return artifact search results
    */
   CancellableCallable<ResultSet<ArtifactReadable>> createSearch() throws OseeCoreException;

   /**
    * Schedule query and find matching locations
    * 
    * @return artifact search results with match locations
    */
   CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> createSearchWithMatches() throws OseeCoreException;

   /**
    * Schedule query and find matching locations
    * 
    * @return localIds search results
    */
   CancellableCallable<ResultSet<HasLocalId<Integer>>> createSearchResultsAsLocalIds() throws OseeCoreException;

}
