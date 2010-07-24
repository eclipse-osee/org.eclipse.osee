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

import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public enum StorageState {
   LOADED(0), CREATED(1), MODIFIED(2), PURGED(3), //
   DELETED(4); // TODO remove after release

   private final int value;

   private StorageState(int value) {
      this.value = value;
   }

   public int value() {
      return value;
   }

   public static StorageState valueOf(int value) throws OseeCoreException {
      for (StorageState type : values()) {
         if (type.value == value) {
            return type;
         }
      }
      throw new OseeArgumentException(String.format("[%s] is not a valid StorageState"));
   }
}
