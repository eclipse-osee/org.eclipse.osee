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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeDirtyFilter.DirtyFlag;

/**
 * @author Roberto E. Escobar
 */
public class AttributeCollection {

   private static final AttributeFilter FILTER_NONE_DIRTY = new AttributeDirtyFilter(DirtyFlag.DIRTY);
   private static final AttributeFilter INCLUDE_DELETED = new AttributeDeletedFilter(DeletionFlag.INCLUDE_DELETED);
   private static final AttributeFilter EXCLUDE_DELETED = new AttributeDeletedFilter(DeletionFlag.EXCLUDE_DELETED);

   private final HashCollection<IAttributeType, Attribute<?>> attributes =
      new HashCollection<IAttributeType, Attribute<?>>(false, LinkedHashSet.class, 12);

   private final AttributeExceptionFactory exceptionFactory;

   public AttributeCollection(AttributeExceptionFactory exceptionFactory) {
      super();
      this.exceptionFactory = exceptionFactory;
   }

   public void addAttribute(IAttributeType type, Attribute<?> attribute) {
      attributes.put(type, attribute);
   }

   public void removeAttribute(IAttributeType attributeType, Attribute<?> attribute) {
      attributes.removeValue(attributeType, attribute);
   }

   public List<Attribute<?>> getAllAttributes() {
      return attributes.getValues();
   }

   public Collection<IAttributeType> getExistingTypes(DeletionFlag includeDeleted) throws OseeCoreException {
      List<IAttributeType> toReturn = new ArrayList<IAttributeType>();
      for (Attribute<?> attribute : getAttributeList(includeDeleted)) {
         toReturn.add(attribute.getAttributeType());
      }
      return toReturn;
   }

   public List<Attribute<Object>> getAttributeListDirties() throws OseeCoreException {
      return getListByFilter(attributes.getValues(), FILTER_NONE_DIRTY);
   }

   public boolean hasAttributesDirty() {
      return hasItemMatchingFilter(attributes.getValues(), FILTER_NONE_DIRTY);
   }

   //////////////////////////////////////////////////////////////

   public <T> ResultSet<Attribute<T>> getAttributeSet(IAttributeType attributeType, DeletionFlag includeDeleted) throws OseeCoreException {
      List<Attribute<T>> result = getListByFilter(attributes.getValues(attributeType), includeDeleted);
      return new AttributeResultSet<T>(exceptionFactory, attributeType, result);
   }

   public <T> ResultSet<Attribute<T>> getAttributeSet(DeletionFlag includeDeleted) throws OseeCoreException {
      return getSetByFilter(attributes.getValues(), includeDeleted);
   }

   public <T> ResultSet<Attribute<T>> getAttributeSetFromString(IAttributeType attributeType, DeletionFlag includeDeleted, String value) throws OseeCoreException {
      AttributeFilter filter = new AttributeDeletedFilter(includeDeleted);
      filter = filter.and(new AttributeFromStringFilter(value));
      return getSetByFilter(attributes.getValues(attributeType), filter);
   }

   public <T> ResultSet<Attribute<T>> getAttributeSetFromValue(IAttributeType attributeType, DeletionFlag includeDeleted, T value) throws OseeCoreException {
      AttributeFilter filter = new AttributeDeletedFilter(includeDeleted);
      filter = filter.and(new AttributeValueFilter<T>(value));
      return getSetByFilter(attributes.getValues(attributeType), filter);
   }

   private <T> ResultSet<Attribute<T>> getSetByFilter(Collection<Attribute<?>> source, DeletionFlag includeDeleted) throws OseeCoreException {
      return getSetByFilter(source, DeletionFlag.INCLUDE_DELETED == includeDeleted ? INCLUDE_DELETED : EXCLUDE_DELETED);
   }

   //////////////////////////////////////////////////////////////

   public <T> List<Attribute<T>> getAttributeList(IAttributeType attributeType, DeletionFlag includeDeleted) throws OseeCoreException {
      return getListByFilter(attributes.getValues(attributeType), includeDeleted);
   }

   public <T> List<Attribute<T>> getAttributeList(DeletionFlag includeDeleted) throws OseeCoreException {
      return getListByFilter(attributes.getValues(), includeDeleted);
   }

   private <T> List<Attribute<T>> getListByFilter(Collection<Attribute<?>> source, DeletionFlag includeDeleted) throws OseeCoreException {
      return getListByFilter(source, DeletionFlag.INCLUDE_DELETED == includeDeleted ? INCLUDE_DELETED : EXCLUDE_DELETED);
   }

   //////////////////////////////////////////////////////////////

   private <T> AttributeResultSet<T> getSetByFilter(Collection<Attribute<?>> source, AttributeFilter filter) throws OseeCoreException {
      List<Attribute<T>> values = getListByFilter(source, filter);
      return new AttributeResultSet<T>(exceptionFactory, values);
   }

   @SuppressWarnings("unchecked")
   private <T> List<Attribute<T>> getListByFilter(Collection<Attribute<?>> source, AttributeFilter filter) throws OseeCoreException {
      List<Attribute<T>> toReturn;
      if (source != null && !source.isEmpty()) {
         toReturn = new LinkedList<Attribute<T>>();
         for (Attribute<?> attribute : source) {
            if (filter.accept(attribute)) {
               toReturn.add((Attribute<T>) attribute);
            }
         }
      } else {
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }

   private boolean hasItemMatchingFilter(Collection<Attribute<?>> source, AttributeFilter filter) {
      boolean result = false;
      if (source != null && !source.isEmpty()) {
         for (Attribute<?> attribute : source) {
            try {
               if (filter.accept(attribute)) {
                  result = true;
                  break;
               }
            } catch (OseeCoreException ex) {
               // do nothing
            }
         }
      }
      return result;
   }

}
