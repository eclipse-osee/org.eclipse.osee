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
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public enum CacheOperation {

   UPDATE,
   STORE;

   public static CacheOperation fromString(String value) {
      Conditions.checkNotNullOrEmpty(value, "enum string");
      for (CacheOperation op : CacheOperation.values()) {
         if (op.name().equalsIgnoreCase(value)) {
            return op;
         }
      }
      throw new OseeArgumentException("Unable to find cache operation matching [%s]", value);
   }
}
