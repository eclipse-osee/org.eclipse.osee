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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.AttributeDataFactory;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.ArtifactReferenceAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.BooleanAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.BranchReferenceAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.CompressedContentAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.DateAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.EnumeratedAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.FloatingPointAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.IntegerAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.JavaObjectAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.StringAttribute;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Roberto E. Escobar
 */
public class AttributeFactory {

   private final AttributeDataFactory dataFactory;
   private final AttributeTypes cache;

   public AttributeFactory(AttributeDataFactory dataFactory, AttributeTypes cache) {
      this.dataFactory = dataFactory;
      this.cache = cache;
   }

   public <T> Attribute<T> createAttributeWithDefaults(AttributeContainer container, ArtifactData artifactData, AttributeTypeId attributeType) throws OseeCoreException {
      AttributeData<T> data = dataFactory.create(artifactData, attributeType);
      return createAttribute(container, data, true, true);
   }

   public <T> Attribute<T> createAttribute(AttributeContainer container, AttributeData<T> data) throws OseeCoreException {
      return createAttribute(container, data, false, false);
   }

   private <T> Attribute<T> createAttribute(AttributeContainer container, AttributeData<T> data, boolean isDirty, boolean createWithDefaults) throws OseeCoreException {
      AttributeTypeId type = cache.get(data.getTypeUuid());
      Conditions.checkNotNull(type, "attributeType", "Cannot find attribute type with uuid[%s]", data.getTypeUuid());

      Attribute<T> attribute = createAttribute(type);

      DataProxy<T> proxy = data.getDataProxy();
      ResourceNameResolver resolver = createResolver(attribute);
      proxy.setResolver(resolver);
      proxy.setAttribute(attribute);

      Reference<AttributeContainer> artifactRef = new WeakReference<>(container);

      attribute.internalInitialize(cache, artifactRef, data, isDirty, createWithDefaults);
      container.add(type, attribute);

      return attribute;
   }

   private <T> Attribute<T> createAttribute(AttributeTypeId attributeType) {
      String baseAttributeType = cache.getBaseAttributeTypeId(attributeType);

      Attribute<?> attribute;

      // Note: these comparisons are in order of likelihood of matching for the ever so slight advantage of fewer String comparisons
      if (baseAttributeType.equals(StringAttribute.NAME)) {
         attribute = new StringAttribute();
      } else if (baseAttributeType.equals(BooleanAttribute.NAME)) {
         attribute = new BooleanAttribute();
      } else if (baseAttributeType.equals(EnumeratedAttribute.NAME)) {
         attribute = new EnumeratedAttribute();
      } else if (baseAttributeType.equals(DateAttribute.NAME)) {
         attribute = new DateAttribute();
      } else if (baseAttributeType.equals(IntegerAttribute.NAME)) {
         attribute = new IntegerAttribute();
      } else if (baseAttributeType.equals(FloatingPointAttribute.NAME)) {
         attribute = new FloatingPointAttribute();
      } else if (baseAttributeType.equals(ArtifactReferenceAttribute.NAME)) {
         attribute = new ArtifactReferenceAttribute();
      } else if (baseAttributeType.equals(BranchReferenceAttribute.NAME)) {
         attribute = new BranchReferenceAttribute();
      } else if (baseAttributeType.equals(JavaObjectAttribute.NAME)) {
         attribute = new JavaObjectAttribute();
      } else if (baseAttributeType.equals(CompressedContentAttribute.NAME)) {
         attribute = new CompressedContentAttribute();
      } else {
         attribute = new StringAttribute();
      }
      return (Attribute<T>) attribute;
   }

   public <T> Attribute<T> copyAttribute(AttributeData<T> source, BranchId ontoBranch, AttributeContainer destinationContainer) throws OseeCoreException {
      AttributeData<T> attributeData = dataFactory.copy(ontoBranch, source);
      return createAttribute(destinationContainer, attributeData, true, false);
   }

   public <T> Attribute<T> cloneAttribute(AttributeData<T> source, AttributeContainer destinationContainer) throws OseeCoreException {
      AttributeData<T> attributeData = dataFactory.clone(source);
      Attribute<T> destinationAttribute = createAttribute(destinationContainer, attributeData, false, false);
      return destinationAttribute;
   }

   public <T> Attribute<T> introduceAttribute(AttributeData<T> source, BranchId ontoBranch, AttributeManager destination) throws OseeCoreException {
      AttributeData<T> attributeData = dataFactory.introduce(ontoBranch, source);
      // In order to reflect attributes they must exist in the data store
      Attribute<T> destinationAttribute = null;
      if (source.getVersion().isInStorage()) {
         try {
            destinationAttribute = destination.getAttributeById(source.getLocalId(), DeletionFlag.INCLUDE_DELETED);
            Reference<AttributeContainer> artifactRef = new WeakReference<>(destination);
            destinationAttribute.internalInitialize(cache, artifactRef, attributeData, true, false);
         } catch (AttributeDoesNotExist ex) {
            destinationAttribute = createAttribute(destination, attributeData);
         }
      }
      return destinationAttribute;
   }

   private ResourceNameResolver createResolver(Attribute<?> attribute) {
      return new AttributeResourceNameResolver(cache, attribute);
   }

   public int getMaxOccurrenceLimit(AttributeTypeId attributeType) throws OseeCoreException {
      return cache.getMaxOccurrences(attributeType);
   }

   public int getMinOccurrenceLimit(AttributeTypeId attributeType) throws OseeCoreException {
      return cache.getMinOccurrences(attributeType);
   }

}
