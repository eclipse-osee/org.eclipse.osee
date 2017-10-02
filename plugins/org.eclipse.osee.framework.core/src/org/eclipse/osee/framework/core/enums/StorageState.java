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

   public static StorageState valueOf(int value)  {
      for (StorageState type : values()) {
         if (type.value == value) {
            return type;
         }
      }
      throw new OseeArgumentException("%d is not a valid StorageState", value);
   }
}
