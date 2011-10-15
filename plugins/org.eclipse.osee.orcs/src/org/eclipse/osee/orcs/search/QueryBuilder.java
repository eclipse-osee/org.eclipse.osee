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
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface QueryBuilder {

   public static IAttributeType ANY_ATTRIBUTE_TYPE = TokenFactory.createAttributeType(Long.MIN_VALUE,
      "Any Attribute Type");

   QueryBuilder includeCache();

   QueryBuilder includeCache(boolean enabled);

   boolean isCacheIncluded();

   QueryBuilder includeDeleted();

   QueryBuilder includeDeleted(boolean enabled);

   boolean areDeletedIncluded();

   QueryBuilder includeTypeInheritance();

   QueryBuilder includeTypeInheritance(boolean enabled);

   boolean isTypeInheritanceIncluded();

   QueryBuilder fromTransaction(int transactionId);

   int getFromTransaction();

   QueryBuilder headTransaction();

   boolean isHeadTransaction();

   QueryBuilder excludeCache();

   QueryBuilder excludeDeleted();

   QueryBuilder excludeTypeInheritance();

   /**
    * Resets query builder to default settings. This also clear all criteria added to original query.
    */
   QueryBuilder resetToDefaults();

   /**
    * Search criteria that finds a given artifact id
    */
   QueryBuilder withLocalId(int... artifactId) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact ids
    */
   QueryBuilder withLocalIds(Collection<Integer> artifactIds) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact with guids or hrids
    */
   QueryBuilder withGuidsOrHrids(String... ids) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact with guids or hrids
    */
   QueryBuilder withGuidsOrHrids(Collection<String> ids) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact type
    */
   QueryBuilder and(IArtifactType... artifactType) throws OseeCoreException;

   /**
    * Search criteria that finds a given artifact types
    */
   QueryBuilder and(Collection<? extends IArtifactType> artifactType) throws OseeCoreException;

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
    * @param relationType the type-side to start following the link from
    */
   QueryBuilder andExists(IRelationTypeSide relationType) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value relative to the given value based
    * on the operator provided.
    */
   QueryBuilder and(IAttributeType attributeType, Operator operator, String value) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   QueryBuilder and(IAttributeType attributeType, Operator operator, Collection<String> values) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value relative to the given value based
    * on the operator provided.
    */
   QueryBuilder and(IAttributeType attributeType, StringOperator operator, CaseType match, String value) throws OseeCoreException;

   /**
    * Search criteria that finds an attribute of the given type with its current value exactly equal (or not equal) to
    * any one of the given literal values. If the list only contains one value, then the search is conducted exactly as
    * if the single value constructor was called. This search does not support the (* wildcard) for multiple values.
    */
   QueryBuilder and(IAttributeType attributeType, StringOperator operator, CaseType match, Collection<String> values) throws OseeCoreException;

   /**
    * Creates a result set based on query settings
    * 
    * @see LoadLevel level
    */
   ResultSet<ReadableArtifact> build(LoadLevel loadLevel) throws OseeCoreException;

   /**
    * Creates a result set based on query settings
    * 
    * @see LoadLevel level
    */
   ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> buildMatches(LoadLevel loadLevel) throws OseeCoreException;

   /**
    * Count potential results
    */
   int getCount() throws OseeCoreException;
}
