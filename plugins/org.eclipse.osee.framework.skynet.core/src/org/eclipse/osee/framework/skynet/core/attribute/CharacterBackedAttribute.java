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

import java.lang.reflect.ParameterizedType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider;

/**
 * @author Roberto E. Escobar
 */
public abstract class CharacterBackedAttribute<T> extends Attribute<T> {
   @Override
   public ICharacterAttributeDataProvider getAttributeDataProvider() {
      // this cast is always safe since the the data provider passed in the constructor to
      // the super class is of type  ICharacterAttributeDataProvider
      return (ICharacterAttributeDataProvider) super.getAttributeDataProvider();
   }

   @Override
   protected boolean subClassSetValue(T value) {
      Class<?> clazz = getClass();
      String superclassName = clazz.getSuperclass().getSimpleName();
      while (!superclassName.equals("CharacterBackedAttribute") && !superclassName.equals("BinaryBackedAttribute")) {
         clazz = clazz.getSuperclass();
         superclassName = clazz.getSuperclass().getSimpleName();
      }
      Class<?> parameterclazz =
         (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

      if (!parameterclazz.isInstance(value)) {
         if (value.getClass().getSuperclass().getSimpleName().equals("EnumToken")) {
            EnumToken enumToken = (EnumToken) value;
            String stringValue = enumToken.getName();
            return getAttributeDataProvider().setValue(stringValue);
         }
         throw new ClassCastException(
            parameterclazz + " attribute subClassSetValue called with type " + value.getClass());
      }
      if (this.isOfType(CoreAttributeTypes.WordTemplateContent) && value instanceof String) {
         return getAttributeDataProvider().setValue(modifyWordValue(value));
      }

      return getAttributeDataProvider().setValue(value);
   }

   protected String modifyWordValue(T value) {
      return (String) value;
   }
}