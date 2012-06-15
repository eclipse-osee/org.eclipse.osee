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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.internal.artifact.AttributeContainer;

/**
 * @author Roberto E. Escobar
 */
public class AttributeFactory {

   private final AttributeClassResolver classResolver;
   private final IOseeCache<Long, AttributeType> cache;

   public AttributeFactory(Log logger, AttributeClassResolver classResolver, IOseeCache<Long, AttributeType> cache) {
      this.classResolver = classResolver;
      this.cache = cache;
   }

   public <T> void createAttribute(AttributeContainer container, AttributeData row) throws OseeCoreException {
      AttributeType type = cache.getByGuid(row.getAttrTypeUuid());
      Conditions.checkNotNull(type, "attributeType", "Cannot find attribute type with uuid[%s]", row.getAttrTypeUuid());

      boolean markDirty = false;

      Class<? extends Attribute<?>> attributeClass = classResolver.getBaseClazz(type);
      if (attributeClass == null) {
         // TODO Word Attributes etc -  Default to StringAttribute if Null
         attributeClass = classResolver.getBaseClazz("StringAttribute");
      }
      Attribute<T> attribute = createAttribute(attributeClass);

      DataProxy proxy = row.getDataProxy();

      proxy.setResolver(new AttributeResourceNameResolver(attribute));

      Reference<AttributeContainer> artifactRef = new WeakReference<AttributeContainer>(container);
      attribute.internalInitialize(type, proxy, artifactRef, markDirty, false, row);
      container.add(type, attribute);
   }

   /**
    * Creates an instance of <code>Attribute</code> of the given attribute type. This method should not be called by
    * applications. Use addAttribute() instead
    */
   @SuppressWarnings("unchecked")
   private <T> Attribute<T> createAttribute(Class<? extends Attribute<?>> attributeClass) throws OseeCoreException {
      Attribute<T> attribute = null;
      try {
         attribute = (Attribute<T>) attributeClass.newInstance();
      } catch (InstantiationException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (IllegalAccessException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return attribute;
   }
}
