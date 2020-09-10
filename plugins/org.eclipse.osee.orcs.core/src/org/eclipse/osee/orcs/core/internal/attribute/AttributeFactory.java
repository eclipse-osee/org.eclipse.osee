/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.attribute;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
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
import org.eclipse.osee.orcs.core.internal.attribute.primitives.LongAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.StringAttribute;

/**
 * @author Roberto E. Escobar
 */
public class AttributeFactory {

   private final AttributeDataFactory dataFactory;
   private final OrcsTokenService tokenService;

   public AttributeFactory(AttributeDataFactory dataFactory, OrcsTokenService tokenService) {
      this.dataFactory = dataFactory;
      this.tokenService = tokenService;
   }

   public <T> Attribute<T> createAttributeWithDefaults(AttributeContainer container, ArtifactData artifactData, AttributeTypeToken attributeType) {
      AttributeData<T> data = dataFactory.create(artifactData, tokenService.getAttributeType(attributeType.getId()));
      return createAttribute(container, data, true, true);
   }

   public <T> Attribute<T> createAttribute(AttributeContainer container, AttributeData<T> data) {
      return createAttribute(container, data, false, false);
   }

   private <T> Attribute<T> createAttribute(AttributeContainer container, AttributeData<T> data, boolean isDirty, boolean createWithDefaults) {
      Attribute<T> attribute = createAttribute(data.getType(), data);

      DataProxy<T> proxy = data.getDataProxy();
      ResourceNameResolver resolver = createResolver(attribute);
      proxy.setResolver(resolver);
      proxy.setAttribute(attribute);

      Reference<AttributeContainer> artifactRef = new WeakReference<>(container);

      attribute.internalInitialize(artifactRef, data, isDirty, createWithDefaults, tokenService);
      container.add(data.getType(), attribute);

      return attribute;
   }

   private <T> Attribute<T> createAttribute(AttributeTypeId attributeTypeId, AttributeId attributeId) {
      AttributeTypeGeneric<?> attributeType = tokenService.getAttributeType(attributeTypeId.getId());
      Long id = attributeId.getId();
      Attribute<?> attribute;

      // Note: these comparisons are in order of likelihood of matching for the ever so slight advantage of fewer String comparisons
      if (attributeType.isString()) {
         attribute = new StringAttribute(id);
      } else if (attributeType.isBoolean()) {
         attribute = new BooleanAttribute(id);
      } else if (attributeType.isEnumerated()) {
         attribute = new EnumeratedAttribute(id);
      } else if (attributeType.isDate()) {
         attribute = new DateAttribute(id);
      } else if (attributeType.isInteger()) {
         attribute = new IntegerAttribute(id);
      } else if (attributeType.isDouble()) {
         attribute = new FloatingPointAttribute(id);
      } else if (attributeType.isLong()) {
         attribute = new LongAttribute(id);
      } else if (attributeType.isArtifactId()) {
         attribute = new ArtifactReferenceAttribute(id);
      } else if (attributeType.isBranchId()) {
         attribute = new BranchReferenceAttribute(id);
      } else if (attributeType.isObject()) {
         attribute = new JavaObjectAttribute(id);
      } else if (attributeType.isInputStream()) {
         attribute = new CompressedContentAttribute(id);
      } else {
         attribute = new StringAttribute(id);
      }
      return (Attribute<T>) attribute;
   }

   public <T> Attribute<T> copyAttribute(AttributeData<T> source, BranchId ontoBranch, AttributeContainer destinationContainer) {
      AttributeData<T> attributeData = dataFactory.copy(ontoBranch, source);
      return createAttribute(destinationContainer, attributeData, true, false);
   }

   public <T> Attribute<T> cloneAttribute(AttributeData<T> source, AttributeContainer destinationContainer) {
      AttributeData<T> attributeData = dataFactory.clone(source);
      Attribute<T> destinationAttribute = createAttribute(destinationContainer, attributeData, false, false);
      return destinationAttribute;
   }

   public <T> Attribute<T> introduceAttribute(AttributeData<T> source, BranchId ontoBranch, AttributeManager destination) {
      AttributeData<T> attributeData = dataFactory.introduce(ontoBranch, source);
      // In order to reflect attributes they must exist in the data store
      Attribute<T> destinationAttribute = null;
      if (source.getVersion().isInStorage()) {
         try {
            destinationAttribute = destination.getAttributeById(source, DeletionFlag.INCLUDE_DELETED);
            Reference<AttributeContainer> artifactRef = new WeakReference<>(destination);
            destinationAttribute.internalInitialize(artifactRef, attributeData, true, false, tokenService);
         } catch (AttributeDoesNotExist ex) {
            destinationAttribute = createAttribute(destination, attributeData);
         }
      }
      return destinationAttribute;
   }

   private ResourceNameResolver createResolver(Attribute<?> attribute) {
      return new AttributeResourceNameResolver(attribute);
   }
}