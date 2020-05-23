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

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Theron Virgin
 */
public enum ConflictType {

   ATTRIBUTE(1),
   RELATION(2),
   ARTIFACT(3);
   private final int value;

   ConflictType(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public static ConflictType valueOf(int value) {
      for (ConflictType type : values()) {
         if (type.value == value) {
            return type;
         }
      }
      throw new OseeArgumentException("%d is not a valid ConflictType", value);
   }
}
