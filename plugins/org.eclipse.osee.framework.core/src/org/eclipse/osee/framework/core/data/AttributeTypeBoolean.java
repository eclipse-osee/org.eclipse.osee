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

/**
 * @author Ryan D. Brooks
 */
public final class AttributeTypeBoolean extends AttributeTypeGeneric<Boolean> {
   public AttributeTypeBoolean(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      super(id, namespace, name, mediaType, description, taggerType, "", Boolean.FALSE, null);
   }

   public AttributeTypeBoolean(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, DisplayHint... displayHints) {
      super(id, namespace, name, mediaType, description, taggerType, "", Boolean.FALSE, null, displayHints);
   }

   @Override
   public boolean isBoolean() {
      return true;
   }

   @Override
   public Boolean valueFromStorageString(String storedValue) {
      return Boolean.valueOf(storedValue);
   }
}