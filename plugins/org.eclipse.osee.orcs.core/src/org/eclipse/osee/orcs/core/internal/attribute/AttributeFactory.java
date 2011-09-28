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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.ds.AttributeRow;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.internal.AttributeDataProxyFactory;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.BooleanAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.CompressedContentAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.DateAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.EnumeratedAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.FloatingPointAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.IntegerAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.JavaObjectAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.StringAttribute;

/**
 * @author Roberto E. Escobar
 */
public class AttributeFactory {

   private final Map<String, Class<? extends Attribute<?>>> primitiveAttributes =
      new HashMap<String, Class<? extends Attribute<?>>>();

   private final AttributeDataProxyFactory dataProxyFactory;
   private final AttributeTypeCache attributeTypeCache;

   public AttributeFactory(Log logger, AttributeDataProxyFactory dataProxyFactory, AttributeTypeCache attributeTypeCache) {
      this.dataProxyFactory = dataProxyFactory;
      this.attributeTypeCache = attributeTypeCache;

      primitiveAttributes.put("", BooleanAttribute.class);
      primitiveAttributes.put("", IntegerAttribute.class);
      primitiveAttributes.put("", FloatingPointAttribute.class);
      primitiveAttributes.put("", StringAttribute.class);
      primitiveAttributes.put("", DateAttribute.class);
      primitiveAttributes.put("", EnumeratedAttribute.class);
      primitiveAttributes.put("", JavaObjectAttribute.class);
      primitiveAttributes.put("", CompressedContentAttribute.class);
   }

   private DataProxy createDataProxy(AttributeType attributeType) throws OseeCoreException {
      return dataProxyFactory.createDataProxy(attributeType.getAttributeProviderId());
   }

   public <T> void loadAttribute(AttributeContainer<?> container, AttributeRow row) throws OseeCoreException {
      AttributeType attributeType = attributeTypeCache.getByGuid(row.getAttrTypeUuid());
      String value = row.getValue();
      if (isEnumOrBoolean(attributeType)) {
         value = Strings.intern(value);
      }
      boolean markDirty = false;

      Class<? extends Attribute<T>> attributeClass = null;
      Attribute<T> attribute = createAttribute(attributeClass);
      container.add(attributeType, attribute);

      DataProxy dataProxy = createDataProxy(attributeType);
      Reference<AttributeContainer<?>> artifactRef = new WeakReference<AttributeContainer<?>>(container);

      attribute.internalInitialize(attributeType, dataProxy, artifactRef, row.getModType(), row.getAttrId(),
         row.getGammaId(), markDirty, false);
   }

   @SuppressWarnings("unused")
   private boolean isEnumOrBoolean(IAttributeType attributeType) throws OseeCoreException {
      boolean isBooleanAttribute = false;
      //    AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeType);
      boolean isEnumAttribute = false;
      // AttributeTypeManager.isBaseTypeCompatible(EnumeratedAttribute.class, attributeType);
      return isBooleanAttribute || isEnumAttribute;
   }

   /**
    * Creates an instance of <code>Attribute</code> of the given attribute type. This method should not be called by
    * applications. Use addAttribute() instead
    */
   private <T> Attribute<T> createAttribute(Class<? extends Attribute<T>> attributeClass) throws OseeCoreException {
      Attribute<T> attribute = null;
      try {
         attribute = attributeClass.newInstance();
      } catch (InstantiationException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (IllegalAccessException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return attribute;
   }

   //   public static boolean isBaseTypeCompatible(Class<? extends Attribute> baseType, IAttributeType attributeType) throws OseeCoreException {
   //      return baseType.isAssignableFrom(getAttributeBaseClass(attributeType));
   //   }
   //
   //   public static Class<? extends Attribute<?>> getAttributeBaseClass(IAttributeType attributeType) throws OseeCoreException {
   //      return AttributeExtensionManager.getAttributeClassFor(getType(attributeType).getBaseAttributeTypeId());
   //   }
   //
   //   public static Class<? extends IAttributeDataProvider> getAttributeProviderClass(AttributeType attributeType) throws OseeCoreException {
   //      return AttributeExtensionManager.getAttributeProviderClassFor(attributeType.getAttributeProviderId());
   //   }
}
