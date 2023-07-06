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
public enum TransferDBType {

   SOURCE(1),
   DESTINATION(2);
   private final int value;

   TransferDBType(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public static TransferDBType valueOf(int value) {
      for (TransferDBType type : values()) {
         if (type.value == value) {
            return type;
         }
      }
      throw new OseeArgumentException("%d is not a valid Transfer DB Type", value);
   }
}
