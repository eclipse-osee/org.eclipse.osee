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

package org.eclipse.osee.framework.skynet.core.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DefaultAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.attribute.providers.UriAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeManager {

   private static IOseeCachingService getCacheService() {
      return ServiceUtil.getOseeCacheService();
   }

   private static OrcsTokenService getTokenService() {
      return getCacheService().getTokenService();
   }

   private static AbstractOseeCache<AttributeType> getCache() {
      return getCacheService().getAttributeTypeCache();
   }

   public static Collection<AttributeTypeToken> getValidAttributeTypes(BranchId branch) {
      Set<AttributeTypeToken> attributeTypes = new HashSet<>(100);
      for (ArtifactTypeToken artifactType : ArtifactTypeManager.getAllTypes()) {
         attributeTypes.addAll(artifactType.getValidAttributeTypes());
      }
      return attributeTypes;
   }

   public static Collection<AttributeTypeGeneric<?>> getAllTypes() {
      return getTokenService().getAttributeTypes();
   }

   public static Collection<AttributeTypeToken> getTaggableTypes() {
      Collection<AttributeTypeToken> taggableTypes = new ArrayList<>();
      for (AttributeTypeToken type : getAllTypes()) {
         if (type.isTaggable()) {
            taggableTypes.add(type);
         }
      }
      return taggableTypes;
   }

   public static Set<AttributeTypeToken> getSingleMultiplicityTypes() {
      return getTokenService().getSingletonAttributeTypes();
   }

   public static boolean typeExists(String name) {
      return getTokenService().attributeTypeExists(name);
   }

   public static AttributeTypeGeneric<?> getAttributeType(Long id) {
      return getTokenService().getAttributeType(id);
   }

   public static AttributeType getType(AttributeTypeId type) {
      Long id = type.getId();
      if (id == null) {
         throw new OseeArgumentException("[%s] is not a valid guid", id);
      }
      AttributeType attributeType = getCache().getByGuid(id);
      if (attributeType == null) {
         getCacheService().reloadTypes();
         attributeType = getCache().getByGuid(id);
         if (attributeType == null) {
            throw new OseeTypeDoesNotExist("Attribute Type [%s] is not available.", id);
         }
      }
      return attributeType;
   }

   /**
    * @throws OseeTypeDoesNotExist if it does not exist.
    */
   public static AttributeTypeToken getType(String name) {
      return getTokenService().getAttributeType(name);
   }

   @SuppressWarnings("rawtypes")
   public static boolean isBaseTypeCompatible(Class<? extends Attribute> baseType, AttributeTypeId attributeType) {
      return baseType.isAssignableFrom(getAttributeBaseClass(attributeType));
   }

   public static Class<? extends Attribute<?>> getAttributeBaseClass(AttributeTypeId attributeType) {
      AttributeTypeToken attributeTypeToken = getTokenService().getAttributeType(attributeType.getId());
      if (attributeTypeToken.isInputStream()) {
         return CompressedContentAttribute.class;
      } else if (attributeTypeToken.isBoolean()) {
         return BooleanAttribute.class;
      } else if (attributeTypeToken.isDate()) {
         return DateAttribute.class;
      } else if (attributeTypeToken.isDouble()) {
         return FloatingPointAttribute.class;
      } else if (attributeTypeToken.isArtifactId()) {
         return ArtifactReferenceAttribute.class;
      } else if (attributeTypeToken.isBranchId()) {
         return BranchReferenceAttribute.class;
      } else if (attributeTypeToken.isInteger()) {
         return IntegerAttribute.class;
      } else if (attributeTypeToken.isLong()) {
         return LongAttribute.class;
      } else if (attributeTypeToken.isString()) {
         return StringAttribute.class;
      } else if (attributeTypeToken.isEnumerated()) {
         return EnumeratedAttribute.class;
      } else if (attributeTypeToken.equals(CoreAttributeTypes.ParagraphNumber)) {
         return OutlineNumberAttribute.class;
      } else if (attributeTypeToken.equals(CoreAttributeTypes.WordTemplateContent)) {
         return WordTemplateAttribute.class;
      } else if (attributeTypeToken.equals(CoreAttributeTypes.WholeWordContent)) {
         return WordWholeDocumentAttribute.class;
      } else {
         throw new OseeCoreException("Unexpected Attribute Base Type for: " + attributeType);
      }
   }

   public static <T> IAttributeDataProvider getAttributeProvider(Long attributeTypeId, Attribute<T> attribute) {
      AttributeTypeToken attributeType = AttributeTypeManager.getAttributeType(attributeTypeId);
      if (attributeType.isUri()) {
         return new UriAttributeDataProvider(attribute);
      }
      return new DefaultAttributeDataProvider<T>(attribute);
   }

   public static boolean checkIfRemovalAllowed(AttributeTypeToken attributeType, Collection<? extends Artifact> artifacts) {
      for (Artifact art : artifacts) {
         if (art.getArtifactType().getMin(attributeType) > 0) {
            return false;
         }
      }
      return true;
   }
}