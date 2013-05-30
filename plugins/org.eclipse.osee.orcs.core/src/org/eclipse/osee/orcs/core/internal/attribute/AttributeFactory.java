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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.AttributeDataFactory;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;
import org.eclipse.osee.orcs.core.internal.artifact.AttributeManager;

/**
 * @author Roberto E. Escobar
 */
public class AttributeFactory {

   private final AttributeClassResolver classResolver;
   private final AttributeTypeCache cache;
   private final AttributeDataFactory dataFactory;

   public AttributeFactory(AttributeClassResolver classResolver, AttributeTypeCache cache, AttributeDataFactory dataFactory) {
      this.classResolver = classResolver;
      this.cache = cache;
      this.dataFactory = dataFactory;
   }

   public <T> Attribute<T> createAttributeWithDefaults(AttributeManager container, ArtifactData artifactData, IAttributeType attributeType) throws OseeCoreException {
      AttributeData data = dataFactory.create(artifactData, attributeType);
      return createAttribute(container, data, true, true);
   }

   public <T> Attribute<T> createAttribute(AttributeManager container, AttributeData data) throws OseeCoreException {
      return createAttribute(container, data, false, false);
   }

   private <T> Attribute<T> createAttribute(AttributeManager container, AttributeData data, boolean isDirty, boolean createWithDefaults) throws OseeCoreException {
      AttributeType type = cache.getByGuid(data.getTypeUuid());
      Conditions.checkNotNull(type, "attributeType", "Cannot find attribute type with uuid[%s]", data.getTypeUuid());

      Attribute<T> attribute = classResolver.createAttribute(type);

      DataProxy proxy = data.getDataProxy();
      ResourceNameResolver resolver = createResolver(attribute);
      proxy.setResolver(resolver);

      Reference<AttributeManager> artifactRef = new WeakReference<AttributeManager>(container);

      attribute.internalInitialize(cache, artifactRef, data, isDirty, createWithDefaults);
      container.add(type, attribute);

      return attribute;
   }

   public <T> Attribute<T> copyAttribute(AttributeData source, IOseeBranch ontoBranch, AttributeManager destinationContainer) throws OseeCoreException {
      AttributeData attributeData = dataFactory.copy(ontoBranch, source);
      Attribute<T> destinationAttribute = createAttribute(destinationContainer, attributeData, true, false);
      return destinationAttribute;
   }

   public <T> Attribute<T> cloneAttribute(AttributeData source, AttributeManager destinationContainer) throws OseeCoreException {
      AttributeData attributeData = dataFactory.clone(source);
      Attribute<T> destinationAttribute = createAttribute(destinationContainer, attributeData, false, false);
      return destinationAttribute;
   }

   public <T> Attribute<T> introduceAttribute(AttributeData source, IOseeBranch ontoBranch, AttributeManager destination) throws OseeCoreException {
      Attribute<T> introducedAttribute = null;
      // In order to reflect attributes they must exist in the data store
      if (source.getVersion().isInStorage()) {
         AttributeData attributeData = dataFactory.introduce(ontoBranch, source);
         introducedAttribute = createAttribute(destination, attributeData, true, false);
      }
      return introducedAttribute;
   }

   private AttributeType getAttribeType(IAttributeType token) throws OseeCoreException {
      return token instanceof AttributeType ? (AttributeType) token : cache.get(token);
   }

   private ResourceNameResolver createResolver(Attribute<?> attribute) {
      return new AttributeResourceNameResolver(cache, attribute);
   }

   public int getMaxOccurrenceLimit(IAttributeType attributeType) throws OseeCoreException {
      AttributeType type = getAttribeType(attributeType);
      return type.getMaxOccurrences();
   }

   public int getMinOccurrenceLimit(IAttributeType attributeType) throws OseeCoreException {
      AttributeType type = getAttribeType(attributeType);
      return type.getMinOccurrences();
   }

}
