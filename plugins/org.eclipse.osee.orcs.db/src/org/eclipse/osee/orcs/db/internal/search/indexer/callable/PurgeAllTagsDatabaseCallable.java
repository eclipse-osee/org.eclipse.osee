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
package org.eclipse.osee.orcs.db.internal.search.indexer.callable;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreCallable;

/**
 * @author Roberto E. Escobar
 */
public final class PurgeAllTagsDatabaseCallable extends AbstractDatastoreCallable<Integer> {

   private static final String SEARCH_TAG_TABLE_NAME = "osee_search_tags";

   public PurgeAllTagsDatabaseCallable(Log logger, OrcsSession session, JdbcClient jdbcClient) {
      super(logger, session, jdbcClient);
   }

   @Override
   public Integer call() throws Exception {
      getLogger().warn("Purging all search tags");
      String cmd = isTruncateSupported() ? "TRUNCATE TABLE" : "DELETE FROM";
      String deleteSql = String.format("%s %s", cmd, SEARCH_TAG_TABLE_NAME);
      try {
         return getJdbcClient().runPreparedUpdate(deleteSql);
      } catch (OseeCoreException ex) {
         getLogger().info(ex, "Error clearing: %s", deleteSql);
         throw ex;
      }
   }

   private boolean isTruncateSupported() throws OseeCoreException {
      boolean isTruncateSupported = false;
      JdbcConnection connection = getJdbcClient().getConnection();
      try {
         DatabaseMetaData metaData = connection.getMetaData();
         ResultSet resultSet = null;
         try {
            resultSet = metaData.getTablePrivileges(null, null, SEARCH_TAG_TABLE_NAME);
            while (resultSet.next()) {
               String value = resultSet.getString("PRIVILEGE");
               if ("TRUNCATE".equalsIgnoreCase(value)) {
                  isTruncateSupported = true;
                  break;
               }
            }
         } catch (SQLException ex1) {
            getLogger().info(ex1, "Error determining truncate support");
         } finally {
            if (resultSet != null) {
               try {
                  resultSet.close();
               } catch (SQLException ex) {
                  // Do Nothing
               }
            }
         }
      } finally {
         connection.close();
      }
      return isTruncateSupported;
   }

}
