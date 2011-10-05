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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;

public interface QueryBuilder {

   QueryBuilder resetToDefaults();

   QueryBuilder includeCache();

   QueryBuilder includeCache(boolean enabled);

   QueryBuilder excludeCache();

   QueryBuilder includeDeleted();

   QueryBuilder includeDeleted(boolean enabled);

   QueryBuilder excludeDeleted();

   QueryBuilder includeTypeInheritance();

   QueryBuilder includeTypeInheritance(boolean enabled);

   QueryBuilder excludeTypeInheritance();

   QueryBuilder matchCase();

   QueryBuilder matchCase(boolean enabled);

   QueryBuilder dontMatchCase();

   QueryBuilder fromTransaction(int transactionId);

   QueryBuilder headTransaction();

   /**
    * Search criteria that checks for the existence of an attribute type.
    */
   QueryBuilder andExists(IAttributeType attributeType) throws OseeCoreException;

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

   <T extends ReadableArtifact> ResultSet<T> build();
}
