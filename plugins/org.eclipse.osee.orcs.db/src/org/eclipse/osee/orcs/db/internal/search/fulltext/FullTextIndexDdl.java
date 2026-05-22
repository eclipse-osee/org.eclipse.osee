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

package org.eclipse.osee.orcs.db.internal.search.fulltext;

import org.eclipse.osee.jdbc.DatabaseType;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;

/**
 * Utility class to create full-text search indexes on the osee_attribute table. This should be run once during
 * migration to the hybrid search approach.
 *
 * @author Roberto E. Escobar
 */
public class FullTextIndexDdl {

   private static final String ORACLE_INDEX_EXISTS_CHECK =
      "SELECT COUNT(*) FROM user_indexes WHERE index_name = 'OSEE_ATTR_FTS_IDX'";

   private static final String POSTGRES_INDEX_EXISTS_CHECK =
      "SELECT COUNT(*) FROM pg_indexes WHERE indexname = 'osee_attr_fts_idx'";

   private final JdbcClient jdbcClient;
   private final Log logger;

   public FullTextIndexDdl(JdbcClient jdbcClient, Log logger) {
      this.jdbcClient = jdbcClient;
      this.logger = logger;
   }

   /**
    * Creates the full-text search index on osee_attribute.value if it does not already exist.
    *
    * @return true if the index was created, false if it already existed or the DB does not support FTS
    */
   public boolean createFullTextIndex() {
      DatabaseType dbType = jdbcClient.getDbType();
      if (!dbType.supportsFullTextSearch()) {
         logger.info("Database type does not support native full-text search. Skipping index creation.");
         return false;
      }

      if (indexExists(dbType)) {
         logger.info("Full-text search index already exists. Skipping creation.");
         return false;
      }

      String ddl = dbType.getFullTextIndexDdl("osee_attribute", "value", "osee_attr_fts_idx");
      if (ddl != null) {
         logger.info("Creating full-text search index: %s", ddl);
         jdbcClient.runPreparedUpdate(ddl);
         logger.info("Full-text search index created successfully.");
         return true;
      }
      return false;
   }

   private boolean indexExists(DatabaseType dbType) {
      String checkSql;
      if (dbType.equals(DatabaseType.oracle)) {
         checkSql = ORACLE_INDEX_EXISTS_CHECK;
      } else if (dbType.equals(DatabaseType.postgresql)) {
         checkSql = POSTGRES_INDEX_EXISTS_CHECK;
      } else {
         return false;
      }
      return jdbcClient.fetch(0, checkSql) > 0;
   }
}
