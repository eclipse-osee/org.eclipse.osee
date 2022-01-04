/*********************************************************************
 * Copyright (c) 2021 Boeing
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
 * @author Stephen J Molaro
 */
public class ComputedCharacteristicEnum<T extends EnumToken> extends ComputedCharacteristic<T> {
   private List<T> enumTokens;

   public ComputedCharacteristicEnum(Long id, String name, TaggerTypeToken taggerType, NamespaceToken namespace, String description, List<AttributeTypeGeneric<T>> typesToCompute, int enumCount) {
      super(id, name, taggerType, namespace, description, typesToCompute);
      this.enumTokens = new ArrayList<T>(enumCount);
   }

   public ComputedCharacteristicEnum<EnumToken> getAsEnumToken() {
      return (ComputedCharacteristicEnum<EnumToken>) this;
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

   protected void addEnum(T enumeration) {
      //Considered for switch to Long but don't believe this enum stores an Id, instead stores a enum value into a Array
      int ordinal = enumeration.getIdIntValue();

      while (enumTokens.size() <= ordinal) {
         enumTokens.add(null);
      }
      enumTokens.set(ordinal, enumeration);
   }

   <E extends T> void replaceEnumValues(ComputedCharacteristicEnum<E> attributeType) {
      enumTokens = org.eclipse.osee.framework.jdk.core.util.Collections.cast(attributeType.enumTokens);
   }

   @Override
   public boolean isMultiplicityValid(ArtifactTypeToken artifactType) {
      return false;
   }

   @Override
   public T calculate(List<T> computingValues) {
      return null;
   }
}