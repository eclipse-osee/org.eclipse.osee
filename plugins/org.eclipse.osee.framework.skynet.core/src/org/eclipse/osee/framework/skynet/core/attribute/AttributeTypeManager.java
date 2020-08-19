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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
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

   public static AbstractOseeCache<AttributeType> getCache() {
      return getCacheService().getAttributeTypeCache();
   }

   public static BranchCache getBranchCache() {
      return getCacheService().getBranchCache();
   }

   public static Collection<AttributeTypeToken> getValidAttributeTypes(BranchId branch) {
      Set<AttributeTypeToken> attributeTypes = new HashSet<>(100);
      for (ArtifactTypeToken artifactType : ArtifactTypeManager.getAllTypes()) {
         attributeTypes.addAll(ArtifactTypeManager.getAttributeTypes(artifactType, BranchManager.getBranch(branch)));
      }
      return attributeTypes;
   }

   public static Collection<AttributeType> getAllTypes() {
      return getCache().getAll();
   }

   public static Collection<AttributeTypeToken> getTaggableTypes() {
      Collection<AttributeTypeToken> taggableTypes = new ArrayList<>();
      for (AttributeType type : getAllTypes()) {
         if (type.isTaggable()) {
            taggableTypes.add(type);
         }
      }
      return taggableTypes;
   }

   public static Collection<AttributeTypeId> getSingleMultiplicityTypes() {
      Collection<AttributeTypeId> types = new ArrayList<>();
      for (AttributeType type : getAllTypes()) {
         if (type.getMaxOccurrences() == 1) {
            types.add(type);
         }
      }
      return types;
   }

   public static boolean typeExists(String name) {
      return getCache().existsByName(name);
   }

   public static AttributeTypeGeneric<?> getAttributeType(Long id) {
      return getCacheService().getTokenService().getAttributeType(id);
   }

   public static AttributeType getTypeById(Long id) {
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

   public static AttributeType getType(AttributeTypeId type) {
      return getTypeById(type.getId());
   }

   /**
    * @throws OseeTypeDoesNotExist if it does not exist.
    */
   public static AttributeType getType(String name) {
      return getCache().getByName(name);
   }

   private static Set<String> getEnumerationValues(AttributeType attributeType) {
      if (attributeType.isEnumerated() && attributeType.getOseeEnumType() != null) {
         return attributeType.getOseeEnumType().valuesAsOrderedStringSet();
      }
      return Collections.emptySet();
   }

   public static Set<String> getEnumerationValues(AttributeTypeId attributeType) {
      return getEnumerationValues(getType(attributeType));
   }

   public static Map<String, String> getEnumerationValueDescriptions(AttributeTypeId attributeType) {
      Map<String, String> values = new HashMap<>();
      for (OseeEnumEntry entry : AttributeTypeManager.getType(attributeType).getOseeEnumType().values()) {
         values.put(entry.getName(), entry.getDescription());
      }
      return values;
   }

   public static int getMinOccurrences(AttributeTypeId attributeType) {
      return getType(attributeType).getMinOccurrences();
   }

   public static int getMaxOccurrences(AttributeTypeId attributeType) {
      return getType(attributeType).getMaxOccurrences();
   }

   public static Set<String> getEnumerationValues(Long id) {
      AttributeType type = getTypeById(id);
      Conditions.checkNotNull(type, "Attribute Type");
      return getEnumerationValues(type);
   }

   public static Set<String> getEnumerationValues(String attributeName) {
      AttributeTypeToken type = getType(attributeName);
      Conditions.checkNotNull(type, "Attribute Type");
      return getEnumerationValues(type);
   }

   @SuppressWarnings("rawtypes")
   public static boolean isBaseTypeCompatible(Class<? extends Attribute> baseType, AttributeTypeId attributeType) {
      return baseType.isAssignableFrom(getAttributeBaseClass(attributeType));
   }

   public static Class<? extends Attribute<?>> getAttributeBaseClass(AttributeTypeId attributeType) {
      AttributeTypeToken attributeTypeToken =
         getCacheService().getTokenService().getAttributeType(attributeType.getId());
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

   public static boolean checkIfRemovalAllowed(AttributeTypeId attributeType, Collection<? extends Artifact> artifacts) {
      boolean removalAllowed = false;
      if (getType(attributeType).getMinOccurrences() == 0) {
         removalAllowed = true;
      }
      // if there is any artifact that allows the type, then removal is not allowed
      boolean notAllowed = false;
      for (Artifact art : artifacts) {
         notAllowed = art.isAttributeTypeValid(attributeType);
         if (notAllowed) {
            break;
         }
      }

      return removalAllowed || !notAllowed;
   }

   public static String getName(AttributeTypeId type) {
      return getCache().get(type).getName();
   }
}