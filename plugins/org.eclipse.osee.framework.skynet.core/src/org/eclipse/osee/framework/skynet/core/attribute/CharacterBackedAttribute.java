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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
      Type persistentClass = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
      if (!persistentClass.getTypeName().equals(value.getClass().getName())) {
         throw new ClassCastException(
            persistentClass + " attribute subClassSetValue called with type " + value.getClass());
      }

      return getAttributeDataProvider().setValue(value);
   }
}
