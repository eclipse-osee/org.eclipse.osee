/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.rest.model.transaction;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author David W. Miller
 */
public enum TransferOpType {
   PREV_TX(1),
   ADD(2),
   PURGE(3),
   EMPTY(4);
   private final int value;

   TransferOpType(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public static TransferOpType valueOf(int value) {
      for (TransferOpType type : values()) {
         if (type.value == value) {
            return type;
         }
      }
      throw new OseeArgumentException("%d is not a valid Transfer Operation", value);
   }
}
