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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeManager {

   private static IOseeCachingService getCacheService()  {
      return ServiceUtil.getOseeCacheService();
   }

   public static AbstractOseeCache<AttributeType> getCache()  {
      return getCacheService().getAttributeTypeCache();
   }

   public static BranchCache getBranchCache()  {
      return getCacheService().getBranchCache();
   }

   public static Collection<AttributeTypeToken> getValidAttributeTypes(BranchId branch)  {
      Set<AttributeTypeToken> attributeTypes = new HashSet<>(100);
      for (ArtifactType artifactType : ArtifactTypeManager.getAllTypes()) {
         attributeTypes.addAll(artifactType.getAttributeTypes(BranchManager.getBranch(branch)));
      }
      return attributeTypes;
   }

   public static Collection<AttributeType> getAllTypes()  {
      return getCache().getAll();
   }

   public static Collection<AttributeTypeId> getTaggableTypes()  {
      Collection<AttributeTypeId> taggableTypes = new ArrayList<>();
      for (AttributeType type : getAllTypes()) {
         if (type.isTaggable()) {
            taggableTypes.add(type);
         }
      }
      return taggableTypes;
   }

   public static Collection<AttributeTypeId> getSingleMultiplicityTypes()  {
      Collection<AttributeTypeId> types = new ArrayList<>();
      for (AttributeType type : getAllTypes()) {
         if (type.getMaxOccurrences() == 1) {
            types.add(type);
         }
      }
      return types;
   }

   public static boolean typeExists(String name)  {
      return !getCache().getByName(name).isEmpty();
   }

   /**
    * @return Returns the attribute type matching the guid
    * @param guid attribute type guid to match
    */
   public static AttributeType getTypeByGuid(Long guid)  {
      if (guid == null) {
         throw new OseeArgumentException("[%s] is not a valid guid", guid);
      }
      AttributeType attributeType = getCache().getByGuid(guid);
      if (attributeType == null) {
         getCacheService().reloadTypes();
         attributeType = getCache().getByGuid(guid);
         if (attributeType == null) {
            throw new OseeTypeDoesNotExist("Attribute Type [%s] is not available.", guid);
         }
      }
      return attributeType;
   }

   public static AttributeType getType(AttributeTypeId type)  {
      return getTypeByGuid(type.getId());
   }

   /**
    * @return the attribute type with the given name or throws an OseeTypeDoesNotExist if it does not exist.
    */
   public static AttributeType getType(String name)  {
      AttributeType attributeType = getCache().getUniqueByName(name);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute Type with name [%s] does not exist.", name);
      }
      return attributeType;
   }

   private static Set<String> getEnumerationValues(AttributeType attributeType)  {
      if (attributeType.getOseeEnumType() != null) {
         return attributeType.getOseeEnumType().valuesAsOrderedStringSet();
      }
      return Collections.emptySet();
   }

   public static Set<String> getEnumerationValues(AttributeTypeId attributeType)  {
      return getEnumerationValues(getType(attributeType));
   }

   public static Map<String, String> getEnumerationValueDescriptions(AttributeTypeId attributeType)  {
      Map<String, String> values = new HashMap<>();
      for (OseeEnumEntry entry : AttributeTypeManager.getType(attributeType).getOseeEnumType().values()) {
         values.put(entry.getName(), entry.getDescription());
      }
      return values;
   }

   public static int getMinOccurrences(AttributeTypeId attributeType)  {
      return getType(attributeType).getMinOccurrences();
   }

   public static int getMaxOccurrences(AttributeTypeId attributeType)  {
      return getType(attributeType).getMaxOccurrences();
   }

   public static Set<String> getEnumerationValues(String attributeName)  {
      AttributeType type = getType(attributeName);
      Conditions.checkNotNull(type, "Attribute Type");
      return getEnumerationValues(type);
   }

   @SuppressWarnings("rawtypes")
   public static boolean isBaseTypeCompatible(Class<? extends Attribute> baseType, AttributeTypeId attributeType)  {
      return baseType.isAssignableFrom(getAttributeBaseClass(attributeType));
   }

   public static Class<? extends Attribute<?>> getAttributeBaseClass(AttributeTypeId attributeType)  {
      return AttributeExtensionManager.getAttributeClassFor(getType(attributeType).getBaseAttributeTypeId());
   }

   public static Class<? extends IAttributeDataProvider> getAttributeProviderClass(AttributeType attributeType)  {
      return AttributeExtensionManager.getAttributeProviderClassFor(attributeType.getAttributeProviderId());
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