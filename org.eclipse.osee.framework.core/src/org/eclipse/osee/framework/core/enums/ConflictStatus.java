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
package org.eclipse.osee.framework.core.enums;

/**
 * @author Theron Virgin
 */
public enum ConflictStatus {

   UNTOUCHED(1),
   EDITED(2),
   RESOLVED(3),
   OUT_OF_DATE_COMMITTED(4),
   NOT_RESOLVABLE(5),
   COMMITTED(6),
   INFORMATIONAL(7),
   OUT_OF_DATE(8);
   private final int value;

   ConflictStatus(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public static ConflictStatus getStatus(int value) {
      for (ConflictStatus status : values()) {
         if (status.value == value) return status;
      }
      return null;
   }
}
