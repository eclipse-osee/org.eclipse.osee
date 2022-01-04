/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeEnum<T extends EnumToken> extends AttributeTypeGeneric<T> {
   private List<T> enumTokens;

   public AttributeTypeEnum(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, int enumCount) {
      super(id, namespace, name, mediaType, description, taggerType, "", null);
      this.enumTokens = new ArrayList<T>(enumCount);
   }

   @Override
   public boolean isEnumerated() {
      return true;
   }

   public AttributeTypeEnum<EnumToken> getAsEnumToken() {
      return (AttributeTypeEnum<EnumToken>) this;
   }

   public Collection<T> getEnumValues() {
      return Collections.unmodifiableCollection(enumTokens);
   }

   public Set<String> getEnumStrValues() {
      Set<String> enumStringValues = new HashSet<String>();
      for (T enumToken : enumTokens) {
         enumStringValues.add(enumToken.getName());
      }
      return enumStringValues;
   }

   public boolean isValidEnum(String enumName) {
      for (T enumToken : enumTokens) {
         if (enumToken.getName().equals(enumName)) {
            return true;
         }
      }
      return false;
   }

   public Long getEnumOrdinal(String enumName) {
      for (T enumToken : enumTokens) {
         if (enumToken.getName().equals(enumName)) {
            return enumToken.getId();
         }
      }
      throw new OseeArgumentException("[%s] is not a valid enum name for [%s]", enumName, this);
   }

   public boolean isValidEnum(ArtifactTypeToken artTypeToken, String enumName) {
      for (EnumToken enumToken : artTypeToken.getValidEnumValues(this)) {
         if (enumToken.getName().equals(enumName)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public T valueFromStorageString(String storedValue) {
      for (T enumToken : enumTokens) {
         if (enumToken != null && enumToken.getName().equals(storedValue)) {
            return enumToken;
         }
      }
      T enumeration = enumTokens.get(0).clone(Long.valueOf(enumTokens.size()));
      enumeration.setName(storedValue);
      addEnum(enumeration);
      return enumeration;
   }

   protected void addEnum(T enumeration) {
      //Considered for switch to Long but don't believe this enum stores an Id, instead stores a enum value into a Array
      int ordinal = enumeration.getIdIntValue();

      while (enumTokens.size() <= ordinal) {
         enumTokens.add(null);
      }
      enumTokens.set(ordinal, enumeration);
   }

   <E extends T> void replaceEnumValues(AttributeTypeEnum<E> attributeType) {
      enumTokens = org.eclipse.osee.framework.jdk.core.util.Collections.cast(attributeType.enumTokens);
   }
}