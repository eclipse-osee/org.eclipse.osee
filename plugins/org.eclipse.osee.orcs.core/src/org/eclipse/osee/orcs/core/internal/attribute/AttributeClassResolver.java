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
package org.eclipse.osee.orcs.core.internal.attribute;

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Roberto E. Escobar
 */
public class AttributeClassResolver {

   private final AttributeClassRegistry registry;
   private final AttributeTypes attributeTypes;

   public AttributeClassResolver(AttributeClassRegistry registry, AttributeTypes attributeTypes) {
      super();
      this.registry = registry;
      this.attributeTypes = attributeTypes;
   }

   public Class<? extends Attribute<?>> getBaseClazz(String alias) {
      return registry.getBaseClazz(alias);
   }

   protected Class<? extends Attribute<?>> getBaseClazz(AttributeTypeId attributeType) throws OseeCoreException {
      String alias = attributeTypes.getBaseAttributeTypeId(attributeType);
      if (alias.contains(".")) {
         alias = Lib.getExtension(alias);
      }
      return getBaseClazz(alias);
   }

   public boolean isBaseTypeCompatible(Class<? extends Attribute<?>> baseType, IAttributeType attributeType) throws OseeCoreException {
      Conditions.checkNotNull(baseType, "baseType", "Unable to determine base type from null");
      Conditions.checkNotNull(attributeType, "attributeType");
      Class<? extends Attribute<?>> clazz = getBaseClazz(attributeType);
      Conditions.checkNotNull(clazz, "base attribute type class", "Unable to find base attribute type class for [%s]",
         attributeType);
      return baseType.isAssignableFrom(clazz);
   }

   public <T> Attribute<T> createAttribute(AttributeTypeId type) throws OseeCoreException {
      Class<? extends Attribute<?>> attributeClass = getAttributeClass(type);
      Conditions.checkNotNull(attributeClass, "attributeClass",
         "Cannot find attribute class base type for attributeType[%s]", type);
      return createAttribute(attributeClass);
   }

   private Class<? extends Attribute<?>> getAttributeClass(AttributeTypeId type) throws OseeCoreException {
      Class<? extends Attribute<?>> attributeClass = getBaseClazz(type);
      if (attributeClass == null) {
         // TODO Word Attributes etc -  Default to StringAttribute if Null
         attributeClass = getBaseClazz("StringAttribute");
      }
      return attributeClass;
   }

   /**
    * Creates an instance of <code>Attribute</code> of the given attribute type. This method should not be called by
    * applications. Use addAttribute() instead
    */
   @SuppressWarnings("unchecked")
   private <T> Attribute<T> createAttribute(Class<? extends Attribute<?>> attributeClass) throws OseeCoreException {
      try {
         return (Attribute<T>) attributeClass.newInstance();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }
}
