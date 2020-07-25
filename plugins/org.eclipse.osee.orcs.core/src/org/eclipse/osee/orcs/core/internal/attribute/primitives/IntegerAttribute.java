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

import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

/**
 * @author Ryan D. Brooks
 */
@OseeAttribute("IntegerAttribute")
public class IntegerAttribute extends CharacterBackedAttribute<Integer> {

   public IntegerAttribute(Long id) {
      super(id);
   }

   @Override
   Integer subclassConvertStringToValue(String value) {
      return Integer.valueOf(value);
   }
}