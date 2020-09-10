/*********************************************************************
 * Copyright (c) 2013 Boeing
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

/**
 * @author Donald G. Dunne
 */
public class LongAttribute extends CharacterBackedAttribute<Long> {

   @Override
   public Long getValue() {
      return (Long) getAttributeDataProvider().getValue();
   }

   @Override
   public Long convertStringToValue(String value) {
      return Long.valueOf(value);
   }
}