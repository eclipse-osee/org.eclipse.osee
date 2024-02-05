/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.mim.types;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ElementArrayIndexOrder {
   INNER_OUTER("Inner, Outer"),
   OUTER_INNER("Outer, Inner");

   final String formattedName;

   ElementArrayIndexOrder(String formattedName) {
      this.formattedName = formattedName;
   }

   public String getName() {
      return this.toString();
   }

   public String getFormattedName() {
      return formattedName;
   }
}
