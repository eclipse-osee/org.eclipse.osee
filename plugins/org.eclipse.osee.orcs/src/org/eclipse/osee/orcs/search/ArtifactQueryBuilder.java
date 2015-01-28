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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface ArtifactQueryBuilder<T> {

   public static IAttributeType ANY_ATTRIBUTE_TYPE = TokenFactory.createAttributeType(Long.MIN_VALUE,
      "Any Attribute Type");

   T includeDeletedArtifacts();

   T includeDeletedArtifacts(boolean enabled);

   boolean areDeletedArtifactsIncluded();

   T includeDeletedAttributes();

   T includeDeletedAttributes(boolean enabled);

   boolean areDeletedAttributesIncluded();

   T includeDeletedRelations();

   T includeDeletedRelations(boolean enabled);

   boolean areDeletedRelationsIncluded();

   T fromTransaction(int transactionId);

   int getFromTransaction();

   T headTransaction();

   boolean isHeadTransaction();

   T excludeDeleted();

   /**
    * Search criteria that finds a given artifact id
    */
   T andLocalId(int... artifactId) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact ids
    */
   T andLocalIds(Collection<Integer> artifactIds) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact with guid
    */
   T andGuid(String guid) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact with guids
    */
   T andGuids(Collection<String> ids) throws OseeCoreException;

   /**
    * Artifacts id(s)
    */
   T andIds(Identifiable<String>... ids) throws OseeCoreException;

   /**
    * Artifacts matching token id(s)
    */
   T andIds(Collection<? extends Identifiable<String>> ids) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact type using type inheritance
    */
   T andIsOfType(IArtifactType... artifactType) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact types using type inheritance
    */
   T andIsOfType(Collection<? extends IArtifactType> artifactType) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact types by matching type exactly
    */
   T andTypeEquals(IArtifactType... artifactType) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact types by matching type exactly
    */
   T andTypeEquals(Collection<? extends IArtifactType> artifactType) throws OseeCoreException;

   /**
    * Search criteria that checks for the existence of an attribute type(s).
    */
   T andExists(IAttributeType... attributeType) throws OseeCoreException;

   /**
    * Search criteria that checks for the existence of an attribute types.
    */
   T andExists(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException;

   /**
    * Search criteria that checks for the non-existence of an attribute type(s).
    */
   T andNotExists(IAttributeType attributeType) throws OseeCoreException;

   /**
    * Search criteria that follows the relation link ending on the given side
    * 
    * @param relationType the type to start following the link from
    */
   T andExists(IRelationType relationType) throws OseeCoreException;

   /**
    * Search criteria that checks for non-existence of a relation type
    * 
    * @param relationTypeSide the type to check for non-existence
    */
   T andNotExists(IRelationTypeSide relationTypeSide) throws OseeCoreException;

   /**
    * Search criteria that follows the relation link ending on the given side
    * 
    * @param relationTypeSide the type to start following the link from
    */
   T andExists(IRelationTypeSide relationTypeSide) throws OseeCoreException;

   /**
    * Search criteria that checks for non-existence of a relation type
    * 
    * @param relationType the type to check for non-existence
    */
   T andNotExists(IRelationType relationType) throws OseeCoreException;

   /**
    * Artifact name equals value
    */
   T andNameEquals(String artifactName) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   T and(IAttributeType attributeType, Collection<String> values, QueryOption... options) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value relative to the given value based
    * on the operator provided.
    */
   T and(IAttributeType attributeType, String value, QueryOption... options) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   T and(Collection<IAttributeType> attributeTypes, String value, QueryOption... options) throws OseeCoreException;

   T and(Collection<IAttributeType> attributeTypes, Collection<String> value, QueryOption... options) throws OseeCoreException;

   /**
    * Search for related artifacts
    * 
    * @param relationTypeSide the type-side to search on
    */
   T andRelatedTo(IRelationTypeSide relationTypeSide, ArtifactReadable... artifacts) throws OseeCoreException;

   /**
    * Search for related artifacts
    * 
    * @param relationTypeSide the type-side to search on
    */
   T andRelatedTo(IRelationTypeSide relationTypeSide, Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException;

   /**
    * Search for related artifacts
    * 
    * @param relationTypeSide the type-side to search on
    */
   T andRelatedToLocalIds(IRelationTypeSide relationTypeSide, int... artifactIds) throws OseeCoreException;

   /**
    * Search for related artifacts
    * 
    * @param relationTypeSide the type-side to search on
    */
   T andRelatedToLocalIds(IRelationTypeSide relationTypeSide, Collection<Integer> artifactIds) throws OseeCoreException;

   /**
    * Search related artifacts with specific criteria. Will only follow first level of relations
    * 
    * @param relationTypeSide the type-side to search on
    */
   T followRelation(IRelationTypeSide relationTypeSide);

   /**
    * @return DefaultHeirarchicalRootArtifact
    */
   T andIsHeirarchicalRootArtifact();
}
