/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.EnumType;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypesImpl implements AttributeTypes {

   public static interface AttributeTypeIndexProvider {
      AttributeTypeIndex getAttributeTypeIndex();
   }

   public static interface EnumTypeIndexProvider {
      EnumTypeIndex getEnumTypeIndex();
   }

   private static final String ATTRIBUTE_OCCURRENCE_UNLIMITED = "unlimited";

   private final AttributeTypeIndexProvider provider;
   private final EnumTypeIndexProvider enumTypeIndexProvider;

   public AttributeTypesImpl(AttributeTypeIndexProvider provider, EnumTypeIndexProvider enumTypeIndexProvider) {
      this.provider = provider;
      this.enumTypeIndexProvider = enumTypeIndexProvider;
   }

   private XAttributeType getType(AttributeTypeId attributeType) {
      return provider.getAttributeTypeIndex().getDslTypeByToken(attributeType);
   }

   @Override
   public Collection<AttributeTypeToken> getAll() {
      return provider.getAttributeTypeIndex().getAllTokens();
   }

   @Override
   public AttributeTypeToken get(Id id) {
      return provider.getAttributeTypeIndex().get(id);
   }

   @Override
   public AttributeTypeToken get(Long id) {
      return provider.getAttributeTypeIndex().get(id);
   }

   @Override
   public String getAttributeProviderId(AttributeTypeId attrType) {
      return getType(attrType).getDataProvider();
   }

   @Override
   public String getDefaultValue(AttributeTypeId attrType) {
      XAttributeType type = getType(attrType);
      return type.getDefaultValue();
   }

   @Override
   public int getMaxOccurrences(AttributeTypeId attrType) {
      XAttributeType type = getType(attrType);
      String maxValue = type.getMax();
      int max = Integer.MAX_VALUE;
      if (!ATTRIBUTE_OCCURRENCE_UNLIMITED.equals(maxValue)) {
         if (Strings.isValid(maxValue)) {
            max = Integer.parseInt(maxValue);
         }
      }
      return max;
   }

   @Override
   public int getMinOccurrences(AttributeTypeId attrType) {
      XAttributeType type = getType(attrType);
      String minValue = type.getMin();
      int min = 0;
      if (Strings.isValid(minValue)) {
         min = Integer.parseInt(minValue);
      }
      return min;
   }

   @Override
   public String getFileTypeExtension(AttributeTypeId attrType) {
      XAttributeType type = getType(attrType);
      String value = type.getFileExtension();
      return Strings.isValid(value) ? value : Strings.emptyString();
   }

   @Override
   public EnumType getEnumType(AttributeTypeId attrType) {
      EnumType toReturn = null;
      XAttributeType type = getType(attrType);
      XOseeEnumType enumType = type.getEnumType();
      if (enumType != null) {
         toReturn = enumTypeIndexProvider.getEnumTypeIndex().getTokenByDslType(enumType);
      }
      return toReturn;
   }

   @Override
   public boolean isEmpty() {
      return provider.getAttributeTypeIndex().isEmpty();
   }

   @Override
   public int size() {
      return provider.getAttributeTypeIndex().size();
   }

   @Override
   public boolean exists(Id id) {
      return provider.getAttributeTypeIndex().exists(id);
   }

   @Override
   public AttributeTypeToken getByNameOrSentinel(String attrTypeName) {
      for (AttributeTypeToken type : getAll()) {
         if (type.getName().equals(attrTypeName)) {
            return type;
         }
      }
      return AttributeTypeToken.SENTINEL;
   }

   @Override
   public AttributeTypeToken getByName(String attrTypeName) {
      for (AttributeTypeToken type : getAll()) {
         if (type.getName().equals(attrTypeName)) {
            return type;
         }
      }
      throw new OseeTypeDoesNotExist("AttributeTypeToken does not exist: %s", attrTypeName);
   }

   @Override
   public boolean typeExists(String attrTypeName) {
      for (AttributeTypeToken type : getAll()) {
         if (type.getName().equals(attrTypeName)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean typeExists(AttributeTypeId attrTypeId) {
      for (AttributeTypeToken type : getAll()) {
         if (type.equals(attrTypeId)) {
            return true;
         }
      }
      return false;
   }

}