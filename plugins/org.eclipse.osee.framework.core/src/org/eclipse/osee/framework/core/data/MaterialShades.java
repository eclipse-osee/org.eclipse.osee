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
package org.eclipse.osee.framework.core.data;

/**
 * Possible shades for Angular Material colors. Smaller numbers are lighter shades, larger numbers are darker shades.
 * NONE should only be used when the color is set to NONE
 *
 * @author Ryan T. Baldwin
 */
public enum MaterialShades {
   NONE(""),
   S50("50"),
   S100("100"),
   S200("200"),
   S300("300"),
   S400("400"),
   S500("500"),
   S600("600"),
   S700("700"),
   S800("800"),
   S900("900");

   String value;

   MaterialShades(String value) {
      this.value = value;
   }

   String getValue() {
      return this.value;
   }
}
