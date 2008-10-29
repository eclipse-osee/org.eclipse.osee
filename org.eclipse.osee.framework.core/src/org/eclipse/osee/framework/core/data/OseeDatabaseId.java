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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * This is a unique identifier generated upon database initialization. The field should never be changed in the database
 * once it has been created.
 * 
 * @author Roberto E. Escobar
 */
public class OseeDatabaseId {

   private static final String DB_ID_KEY = "osee.db.guid";
   private static String databaseId = null;

   private OseeDatabaseId() {
   }

   public static String getKey() {
      return DB_ID_KEY;
   }

   public synchronized static String getGuid() throws OseeDataStoreException {
      if (databaseId == null) {
         databaseId = OseeInfo.getValue(DB_ID_KEY);
      }
      return databaseId;
   }
}
