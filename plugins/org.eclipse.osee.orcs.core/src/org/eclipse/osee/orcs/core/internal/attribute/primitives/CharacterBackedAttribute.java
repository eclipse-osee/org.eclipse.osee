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

package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;

/**
 * @author Roberto E. Escobar
 */
public abstract class CharacterBackedAttribute<T> extends AttributeImpl<T> {

   public CharacterBackedAttribute(Long id) {
      super(id);
   }

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