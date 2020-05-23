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

package org.eclipse.osee.framework.core.enums;

/**
 * @author Jeff C. Phillips
 */
public enum ChangeItemType {
   ARTIFACT,
   ATTRIBUTE,
   RELATION;

   public static ChangeItemType getType(String name) {
      for (ChangeItemType type : values()) {
         if (type.name().equalsIgnoreCase(name)) {
            return type;
         }
      }
      return null;
   }
}
