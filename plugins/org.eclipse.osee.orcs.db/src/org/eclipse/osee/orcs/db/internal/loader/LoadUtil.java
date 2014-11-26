/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.jdbc.JdbcConstants;

/**
 * @author Roberto E. Escobar
 */
public final class LoadUtil {

   private static final int MINIMUM_FETCH_SIZE = 10;
   private static final int APPROXIMATE_NUMBER_OF_SUB_ITEMS = 20;

   private LoadUtil() {
      // Utility class
   }

   public static int computeFetchSize(int initialSize) {
      int fetchSize = initialSize;
      if (fetchSize < MINIMUM_FETCH_SIZE) {
         fetchSize = MINIMUM_FETCH_SIZE;
      }

      // Account for attribute and relation loading
      fetchSize *= APPROXIMATE_NUMBER_OF_SUB_ITEMS;

      if (fetchSize < 0 || fetchSize > JdbcConstants.JDBC__MAX_FETCH_SIZE) {
         fetchSize = JdbcConstants.JDBC__MAX_FETCH_SIZE;
      }
      return fetchSize;
   }

}
