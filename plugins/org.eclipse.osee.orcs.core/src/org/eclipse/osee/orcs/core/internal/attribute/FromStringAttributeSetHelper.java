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
public class FromStringAttributeSetHelper implements AttributeSetHelper<Object, String> {

   private final AttributeCollection attributes;
   private final AttributeManager manager;

   public FromStringAttributeSetHelper(AttributeCollection attributes, AttributeManager manager) {
      super();
      this.attributes = attributes;
      this.manager = manager;
   }

   private String asString(Object object) {
      return String.valueOf(object);
   }

   @Override
   public boolean matches(Attribute<Object> attribute, String value) {
      Object attrValue = attribute.getValue();
      return value.equals(asString(attrValue));
   }

   @Override
   public void setAttributeValue(Attribute<Object> attribute, java.lang.String value) {
      attribute.setFromString(value);
   }

   @Override
   public void createAttribute(AttributeTypeId attributeType, String value) {
      ResultSet<Attribute<Object>> result =
         attributes.getAttributeSetFromString(attributeType, DeletionFlag.EXCLUDE_DELETED, value);
      if (result.getOneOrNull() == null) {
         manager.createAttributeFromString(attributeType, value);
      }
   }
}