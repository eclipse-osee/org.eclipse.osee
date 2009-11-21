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
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public enum CacheOperation {

   UPDATE,
   STORE;

   public static CacheOperation fromString(String value) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(value, "enum string");
      for (CacheOperation op : CacheOperation.values()) {
         if (op.name().equalsIgnoreCase(value)) {
            return op;
         }
      }
      throw new OseeArgumentException(String.format("Unable to find cache operation matching [%s]", value));
   }
}
