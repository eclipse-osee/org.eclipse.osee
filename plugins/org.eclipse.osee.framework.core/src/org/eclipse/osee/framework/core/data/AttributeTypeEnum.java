/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Ryan D. Brooks
 */
public class AttributeTypeEnum<T extends EnumToken> extends AttributeTypeGeneric<T> {
   private final List<T> enumTokens;

   public AttributeTypeEnum(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, int enumCount) {
      super(id, namespace, name, mediaType, description, taggerType);
      this.enumTokens = new ArrayList<T>(enumCount);
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
      int ordinal = enumeration.getIdIntValue();

      while (enumTokens.size() <= ordinal) {
         enumTokens.add(null);
      }
      enumTokens.set(ordinal, enumeration);
   }
}