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

import java.sql.DatabaseMetaData;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeSequence;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * Handles connection recovery in the event of database connection being lost
 * 
 * @author Jeff C. Phillips
 */
public final class ConnectionHandler {

   protected static IOseeDatabaseService getDatabase() throws OseeDataStoreException {
      return ServiceUtil.getOseeDatabaseService();
   }

   public static IOseeSequence getSequence() throws OseeDataStoreException {
      return getDatabase().getSequence();
   }

   public static IOseeStatement getStatement() throws OseeDataStoreException {
      return getDatabase().getStatement();
   }

   public static IOseeStatement getStatement(OseeConnection connection) throws OseeDataStoreException {
      return getDatabase().getStatement(connection);
   }

   /**
    * This method should only be used when not contained in a DB transaction
    * 
    * @return number of records updated
    */
   public static int runPreparedUpdate(String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedUpdate(query, data);
   }

   /**
    * This method should only be used when not contained in a DB transaction
    * 
    * @return number of records updated
    */
   public static <O extends Object> int runBatchUpdate(String query, List<O[]> dataList) throws OseeCoreException {
      return getDatabase().runBatchUpdate(query, dataList);
   }

   /**
    * This method should only be used when contained in a DB transaction
    * 
    * @return number of records updated
    */
   public static int runPreparedUpdate(OseeConnection connection, String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedUpdate(connection, query, data);
   }

   public static int runPreparedQueryFetchInt(int defaultValue, String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedQueryFetchObject(defaultValue, query, data);
   }

   public static String runPreparedQueryFetchString(String defaultValue, String query, Object... data) throws OseeCoreException {
      return getDatabase().runPreparedQueryFetchObject(defaultValue, query, data);
   }

   public static DatabaseMetaData getMetaData() throws OseeCoreException {
      OseeConnection connection = getDatabase().getConnection();
      try {
         return connection.getMetaData();
      } finally {
         connection.close();
      }
   }

}