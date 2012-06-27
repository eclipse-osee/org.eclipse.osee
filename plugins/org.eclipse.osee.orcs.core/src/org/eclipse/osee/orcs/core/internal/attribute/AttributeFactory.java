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
import org.eclipse.osee.orcs.core.ds.VersionData;
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

   public <T> Attribute<T> createAttribute(AttributeManager container, ArtifactData artifactData, IAttributeType attributeType) throws OseeCoreException {
      AttributeData data = dataFactory.create(artifactData, attributeType);
      VersionData versionData = data.getVersion();
      versionData.setBranchId(artifactData.getVersion().getBranchId());
      return createAttribute(container, data);
   }

   public <T> Attribute<T> createAttribute(AttributeManager container, AttributeData data) throws OseeCoreException {
      boolean isDirty = false;

      AttributeType type = cache.getByGuid(data.getTypeUuid());
      Conditions.checkNotNull(type, "attributeType", "Cannot find attribute type with uuid[%s]", data.getTypeUuid());

      Attribute<T> attribute = classResolver.createAttribute(type);

      DataProxy proxy = data.getDataProxy();
      ResourceNameResolver resolver = createResolver(attribute);
      proxy.setResolver(resolver);

      Reference<AttributeManager> artifactRef = new WeakReference<AttributeManager>(container);
      attribute.internalInitialize(artifactRef, data, type, isDirty, false);

      synchronized (container) {
         container.add(type, attribute);
      }
      return attribute;
   }

   public <T> Attribute<T> copyAttribute(AttributeData source, IOseeBranch ontoBranch, AttributeManager destinationContainer) throws OseeCoreException {
      AttributeData attributeData = dataFactory.copy(ontoBranch, source);
      Attribute<T> destinationAttribute = createAttribute(destinationContainer, attributeData);
      return destinationAttribute;
   }

   public boolean introduceAttribute(AttributeData source, IOseeBranch ontoBranch, AttributeManager destination) throws OseeCoreException {
      boolean result = false;
      // In order to reflect attributes they must exist in the data store
      if (source.getVersion().isInStorage()) {
         AttributeData attributeData = dataFactory.introduce(ontoBranch, source);

         Attribute<?> introducedAttribute = createAttribute(destination, attributeData);
         result = introducedAttribute != null;
      }
      return result;
   }

   public AttributeType getAttribeType(IAttributeType token) throws OseeCoreException {
      return token instanceof AttributeType ? (AttributeType) token : cache.get(token);
   }

   private ResourceNameResolver createResolver(Attribute<?> attribute) {
      return new AttributeResourceNameResolver(attribute);
   }

}
