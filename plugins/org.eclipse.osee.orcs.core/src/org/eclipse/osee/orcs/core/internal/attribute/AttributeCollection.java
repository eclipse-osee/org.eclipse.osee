/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.attribute;

import static com.google.common.base.Predicates.and;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.attributeStringEquals;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.attributeValueEquals;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.deletionFlagEquals;
import com.google.common.base.Predicate;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.util.AbstractTypeCollection;

/**
 * @author Roberto E. Escobar
 */
public class AttributeCollection extends AbstractTypeCollection<AttributeTypeToken, Attribute<?>, AttributeTypeId, Attribute<?>> {

   private final AttributeExceptionFactory exceptionFactory;

   public AttributeCollection(AttributeExceptionFactory exceptionFactory) {
      super();
      this.exceptionFactory = exceptionFactory;
   }

   //////////////////////////////////////////////////////////////

   @Override
   @SuppressWarnings({"unchecked", "rawtypes"})
   protected ResultSet<Attribute<?>> createResultSet(List<Attribute<?>> values) {
      return new AttributeResultSet(exceptionFactory, values);
   }

   @Override
   @SuppressWarnings({"unchecked", "rawtypes"})
   protected <T extends Attribute<?>> ResultSet<T> createResultSet(AttributeTypeId attributeType, List<T> values) {
      return new AttributeResultSet(exceptionFactory, attributeType, values);
   }

   @Override
   protected Attribute<?> asMatcherData(Attribute<?> data) {
      return data;
   }

   @Override
   protected AttributeTypeToken getType(Attribute<?> data)  {
      return data.getAttributeType();
   }

   public <T> ResultSet<Attribute<T>> getResultSet(AttributeTypeId attributeType, DeletionFlag includeDeleted)  {
      List<Attribute<T>> result = getList(attributeType, includeDeleted);
      return createResultSet(attributeType, result);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public <T> ResultSet<Attribute<T>> getAttributeSetFromString(AttributeTypeId attributeType, DeletionFlag includeDeleted, String value)  {
      Predicate deleteStateMatch = deletionFlagEquals(includeDeleted);
      Predicate stringEqualsMatch = attributeStringEquals(value);
      Predicate filter = and(deleteStateMatch, stringEqualsMatch);
      return getSetByFilter(attributeType, filter);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public <T> ResultSet<Attribute<T>> getAttributeSetFromValue(AttributeTypeId attributeType, DeletionFlag includeDeleted, T value)  {
      Predicate deleteStateMatch = deletionFlagEquals(includeDeleted);
      Predicate attributeValueEquals = attributeValueEquals(value);
      Predicate filter = and(attributeValueEquals, deleteStateMatch);
      return getSetByFilter(attributeType, filter);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public <T> List<Attribute<T>> getList(AttributeTypeId attributeType, DeletionFlag includeDeleted)  {
      Predicate attributeDeletionFlagEquals = deletionFlagEquals(includeDeleted);
      return getListByFilter(attributeType, attributeDeletionFlagEquals);
   }

}
