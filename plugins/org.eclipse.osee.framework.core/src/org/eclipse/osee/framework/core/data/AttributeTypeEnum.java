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
import java.util.Optional;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeEnum<T extends EnumToken> extends AttributeTypeGeneric<T> {
   private final List<T> enumTokens;

   public AttributeTypeEnum(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, int enumCount) {
      super(id, namespace, name, mediaType, description, taggerType, "", null);
      this.enumTokens = new ArrayList<T>(enumCount);
   }

   protected void addEnum(T enumeration) {
      enumTokens.add(enumeration);
   }

   <E extends T> void appendEnumValues(AttributeTypeEnum<E> attributeType) {
      List<E> newEnumTokens = attributeType.enumTokens;

      for (E enumValue : newEnumTokens) {
         // Add only unique enums
         if (!containsEnumWithName(enumValue.getName())) {
            enumTokens.add(enumValue);
         }
      }
   }

   private boolean containsEnumWithName(String name) {
      for (T enumValue : enumTokens) {
         if (enumValue.getName().equals(name)) {
            return true;
         }
      }
      return false;
   }

   @SuppressWarnings("unchecked")
   public AttributeTypeEnum<EnumToken> getAsEnumToken() {
      return (AttributeTypeEnum<EnumToken>) this;
   }

   public Optional<T> getEnum(String name) {
      for (var enumToken : this.enumTokens) {
         if (enumToken.getName().equals(name)) {
            return Optional.of(enumToken);
         }
      }
      return Optional.empty();
   }

   public Optional<T> getEnum(int ordinal) {
      for (var enumToken : this.enumTokens) {
         if (enumToken.getIdIntValue() == ordinal) {
            return Optional.of(enumToken);
         }
      }
      return Optional.empty();
   }

   public Long getEnumOrdinal(String enumName) {
      for (T enumToken : enumTokens) {
         if (enumToken.getName().equals(enumName)) {
            return enumToken.getId();
         }
      }
      throw new OseeArgumentException("[%s] is not a valid enum name for [%s]", enumName, this);
   }

   public Set<String> getEnumStrValues() {
      Set<String> enumStringValues = new HashSet<String>();
      for (T enumToken : enumTokens) {
         enumStringValues.add(enumToken.getName());
      }
      return enumStringValues;
   }

   public Collection<T> getEnumValues() {
      return Collections.unmodifiableCollection(enumTokens);
   }

   public Collection<T> getEnumValuesByNamespace(NamespaceToken namespace) {
      ArrayList<T> enumValues = new ArrayList<T>();
      // do NOT include enums that have specified that they are NOT in the same namespace as the artifact
      for (T enumToken : enumTokens) {
         for (NamespaceToken currentEnumNamespace : enumToken.getNamespaces()) {
            // if the namespace is specified (and matching the artifact namespace), sentinel, or osee, then add to list of valid enum values
            if (currentEnumNamespace.equals(namespace) || currentEnumNamespace.equals(
               NamespaceToken.SENTINEL) || currentEnumNamespace.equals(NamespaceToken.OSEE)) {
               enumValues.add(enumToken);
            }
         }
      }
      return Collections.unmodifiableCollection(enumValues);
   }

   @Override
   public boolean isEnumerated() {
      return true;
   }

   public boolean isValidEnum(ArtifactTypeToken artTypeToken, String enumName) {
      for (EnumToken enumToken : artTypeToken.getValidEnumValues(this)) {
         if (enumToken.getName().equals(enumName)) {
            return true;
         }
      }
      return false;
   }

   public boolean isValidEnum(String enumName) {
      for (T enumToken : enumTokens) {
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
}