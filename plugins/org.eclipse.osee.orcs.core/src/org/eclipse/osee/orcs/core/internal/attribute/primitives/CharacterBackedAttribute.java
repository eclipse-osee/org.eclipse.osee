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
package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;

/**
 * @author Roberto E. Escobar
 */
public abstract class CharacterBackedAttribute<T> extends AttributeImpl<T> {
   @Override
   public CharacterDataProxy<T> getDataProxy() {
      // this cast is always safe since the the data provider passed in the constructor to
      // the super class is of type  ICharacterAttributeDataProvider
      return (CharacterDataProxy) super.getDataProxy();
   }

   @Override
   public T getValue() {
      return getDataProxy().getValue();
   }

   @Override
   public boolean subClassSetValue(T value) {
      Conditions.checkNotNull(value, "Attribute value", "attribute id [%s]", getId());
      return getDataProxy().setValue(value);
   }
}