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
 * @author Ryan T. Baldwin
 */
public enum MaterialColors {
   NONE(""),
   BLUE("primary"),
   GREEN("success"),
   RED("warning"),
   YELLOW("accent");

   String value;

   MaterialColors(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
