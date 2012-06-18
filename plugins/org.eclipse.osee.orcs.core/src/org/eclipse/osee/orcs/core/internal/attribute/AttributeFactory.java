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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.AttributeDataFactory;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;
import org.eclipse.osee.orcs.core.internal.artifact.AttributeContainer;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Roberto E. Escobar
 */
public class AttributeFactory {

   private final AttributeClassResolver classResolver;
   private final IOseeCache<Long, AttributeType> cache;
   private final AttributeDataFactory dataFactory;

   public AttributeFactory(Log logger, AttributeClassResolver classResolver, IOseeCache<Long, AttributeType> cache, AttributeDataFactory dataFactory) {
      this.classResolver = classResolver;
      this.cache = cache;
      this.dataFactory = dataFactory;
   }

   public <T> Attribute<T> asAttributeImpl(AttributeReadable<T> readable) {
      Attribute<T> toReturn = null;
      if (readable instanceof Attribute) {
         toReturn = (Attribute<T>) readable;
      }
      return toReturn;
   }

   public <T> Attribute<T> createAttribute(AttributeContainer container, AttributeData data) throws OseeCoreException {
      boolean isDirty = false;

      AttributeType type = cache.getByGuid(data.getTypeUuid());
      Conditions.checkNotNull(type, "attributeType", "Cannot find attribute type with uuid[%s]", data.getTypeUuid());

      Attribute<T> attribute = classResolver.createAttribute(type);

      DataProxy proxy = data.getDataProxy();
      ResourceNameResolver resolver = createResolver(attribute);
      proxy.setResolver(resolver);

      synchronized (container) {
         Reference<AttributeContainer> artifactRef = new WeakReference<AttributeContainer>(container);
         attribute.internalInitialize(artifactRef, data, type, isDirty, false);
         container.add(type, attribute);
      }
      return attribute;
   }

   public Attribute<?> copyAttribute(AttributeReadable<?> source, IOseeBranch ontoBranch, AttributeContainer destinationContainer) throws OseeCoreException {
      AttributeData attributeData = dataFactory.copy(ontoBranch, getOrcsData(source));
      Attribute<?> destinationAttribute = createAttribute(destinationContainer, attributeData);
      return destinationAttribute;
   }

   public boolean introduceAttribute(AttributeReadable<?> source, IOseeBranch ontoBranch, AttributeContainer destination) throws OseeCoreException {
      boolean result = false;
      AttributeData sourceAttrData = getOrcsData(source);
      // In order to reflect attributes they must exist in the data store
      if (sourceAttrData.getVersion().isInStorage()) {
         AttributeData attributeData = dataFactory.introduce(ontoBranch, sourceAttrData);

         Attribute<?> introducedAttribute = createAttribute(destination, attributeData);
         result = introducedAttribute != null;
      }
      return result;
   }

   private ResourceNameResolver createResolver(Attribute<?> attribute) {
      return new AttributeResourceNameResolver(attribute);
   }

   private AttributeData getOrcsData(AttributeReadable<?> item) {
      return asAttributeImpl(item).getOrcsData();
   }

}
