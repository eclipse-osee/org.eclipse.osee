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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeEnum<T extends EnumToken> extends AttributeTypeGeneric<T> {
   private final List<T> enumTokens;
   private final List<T> dbLoadedEnumTokens = new CopyOnWriteArrayList<>();

   public AttributeTypeEnum(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, int enumCount, DisplayHint... hints) {
      super(id, namespace, name, mediaType, description, taggerType, "", null, null, hints);
      this.enumTokens = new CopyOnWriteArrayList<T>();
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
      return findByName(enumTokens, name).isPresent();
   }

   @SuppressWarnings("unchecked")
   public AttributeTypeEnum<EnumToken> getAsEnumToken() {
      return (AttributeTypeEnum<EnumToken>) this;
   }

   public Optional<T> getEnum(String name) {
      return findByName(this.enumTokens, name);
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

   /**
    * @return Enum values as Strings in ordinal order
    */
   public List<String> getEnumStrValues() {
      List<String> enumStringValues = new ArrayList<String>();
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
      return findByName(enumTokens, enumName).isPresent();
   }

   /**
    * Promotes a runtime-discovered value into the VALID enum set (used for pick lists and
    * validation). Idempotent by name and identity-preserving:
    * <ul>
    * <li>If the name is already a valid enum, its existing token is returned and the valid set is
    * unchanged.</li>
    * <li>If the name was previously minted by {@link #valueFromStorageString} into
    * {@code dbLoadedEnumTokens}, that same token is moved into the valid set (its ordinal is
    * preserved) so a stored value and its promoted valid value remain {@code ==}/{@code equals}
    * equal and no duplicate name or colliding ordinal is created.</li>
    * <li>Otherwise a new token is minted with the next ordinal using the same scheme as
    * {@link #valueFromStorageString}.</li>
    * </ul>
    * Synchronized on this instance; combined with the synchronization on
    * {@link #valueFromStorageString}, concurrent callers cannot produce duplicate names or colliding
    * ordinals.
    */
   public synchronized T addDbLoadedValidEnum(String name) {
      if (name == null || name.isBlank()) {
         throw new OseeArgumentException("name cannot be null or blank for [%s]", this);
      }
      if (enumTokens.isEmpty()) {
         throw new OseeArgumentException("no seed enum token available to clone for [%s]", this);
      }
      Optional<T> existingValid = getEnum(name);
      if (existingValid.isPresent()) {
         return existingValid.get();
      }
      Optional<T> alreadyLoaded = findByName(dbLoadedEnumTokens, name);
      if (alreadyLoaded.isPresent()) {
         T promoted = alreadyLoaded.get();
         dbLoadedEnumTokens.remove(promoted);
         enumTokens.add(promoted);
         return promoted;
      }
      T enumeration = cloneWithNextOrdinal(name);
      enumTokens.add(enumeration);
      return enumeration;
   }

   @Override
   public synchronized T valueFromStorageString(String storedValue) {
      Optional<T> eTok = findByName(enumTokens, storedValue);
      if (eTok.isPresent()) {
         return eTok.get();
      }
      eTok = findByName(dbLoadedEnumTokens, storedValue);
      if (eTok.isPresent()) {
         return eTok.get();
      }
      /**
       * Create a new EnumToken with next id so it will work with == and equals and other operations against
       * enumerations. Use a different storage so any calls to get currently valid Enums do not get database loaded
       * ones.
       */
      T enumeration = cloneWithNextOrdinal(storedValue);
      /**
       * New enumerations are created if a loaded enum value isn't in the original enumTokens list. Need to keep track
       * of these so each loaded token gets the next id, but do not want to add to the enumTokens list or they will be
       * available as valid values.
       */
      dbLoadedEnumTokens.add(enumeration);
      return enumeration;
   }

   private T cloneWithNextOrdinal(String name) {
      T enumeration = enumTokens.get(0).clone(Long.valueOf(enumTokens.size() + dbLoadedEnumTokens.size()));
      enumeration.setName(name);
      return enumeration;
   }

   private Optional<T> findByName(List<T> tokens, String name) {
      for (T enumToken : tokens) {
         if (enumToken.getName().equals(name)) {
            return Optional.of(enumToken);
         }
      }
      return Optional.empty();
   }

}