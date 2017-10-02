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
package org.eclipse.osee.orcs.core.internal.attribute;

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.core.ds.Attribute;

/**
 * @author Roberto E. Escobar
 */
public class TypedValueAttributeSetHelper<T> implements AttributeSetHelper<T, T> {

   private final AttributeCollection attributes;
   private final AttributeManager manager;

   public TypedValueAttributeSetHelper(AttributeCollection attributes, AttributeManager manager) {
      super();
      this.attributes = attributes;
      this.manager = manager;
   }

   @Override
   public boolean matches(Attribute<T> attribute, T value)  {
      return value.equals(attribute.getValue());
   }

   @Override
   public void setAttributeValue(Attribute<T> attribute, T value)  {
      attribute.setValue(value);
   }

   @Override
   public void createAttribute(AttributeTypeId attributeType, T value)  {
      ResultSet<Attribute<T>> result =
         attributes.getAttributeSetFromValue(attributeType, DeletionFlag.EXCLUDE_DELETED, value);
      if (result.getOneOrNull() == null) {
         manager.createAttribute(attributeType, value);
      }
   }
}