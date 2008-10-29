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

import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Donald G. Dunne
 */
public class OseeInfo {
   private static final String GET_VALUE_SQL = "Select OSEE_VALUE FROM osee_info where OSEE_KEY = ?";
   private static final String INSERT_KEY_VALUE_SQL = "INSERT INTO osee_info (OSEE_KEY, OSEE_VALUE) VALUES (?, ?)";
   private static final String DELETE_KEY_SQL = "DELETE FROM osee_info WHERE OSEE_KEY = ?";
   public static final String SAVE_OUTFILE_IN_DB = "SAVE_OUTFILE_IN_DB";

   public static String getValue(String key) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchString("", GET_VALUE_SQL, key);
   }

   public static void putValue(String key, String value) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(DELETE_KEY_SQL, key);
      ConnectionHandler.runPreparedUpdate(INSERT_KEY_VALUE_SQL, key, value);
   }
}