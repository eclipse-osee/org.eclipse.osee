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
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Donald G. Dunne
 */
public class DbUtil {

   public static void getTableRowCounts(Map<String, Integer> tableCount, Collection<String> tableNames) throws OseeDataStoreException {
      for (String tableName : tableNames) {
         tableCount.put(tableName, getTableRowCount(tableName));
      }
   }

   public static int getTableRowCount(String tableName) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(0, "SELECT count(1) FROM " + tableName);
   }

}
