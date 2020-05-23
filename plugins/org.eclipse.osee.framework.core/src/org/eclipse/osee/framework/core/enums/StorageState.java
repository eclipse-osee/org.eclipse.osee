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
 * @author Roberto E. Escobar
 */
public enum StorageState {
   LOADED(0),
   CREATED(1),
   MODIFIED(2),
   PURGED(3);

   private final int value;

   private StorageState(int value) {
      this.value = value;
   }

   public int value() {
      return value;
   }

   public static StorageState valueOf(int value) {
      for (StorageState type : values()) {
         if (type.value == value) {
            return type;
         }
      }
      throw new OseeArgumentException("%d is not a valid StorageState", value);
   }
}
