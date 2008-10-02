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
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;

/**
 * @author Roberto E. Escobar
 */
public class TranslatedIdMap {
   private static final String INSERT_INTO_IMPORT_INDEX_MAP =
         "INSERT INTO osee_import_index_map (import_id, original_id, mapped_id) VALUES (?, ?, ?, ?)";

   private static final String SELECT_IDS_BY_DB_SOURCE_AND_SEQ_NAME =
         "SELECT original_id, mapped_id FROM osee_import_map oim, osee_import_index_map oiim WHERE oim.import_id = oiim.import_id AND oim.db_source_guid = ? AND oim.sequence_name = ?";

   private final String sequenceName;
   private final Set<String> aliases;
   private final Map<Long, Long> originalToMapped;
   private final List<Long> newIds;

   TranslatedIdMap(String sequenceName, String... aliases) {
      this.sequenceName = sequenceName;
      this.aliases = new HashSet<String>();
      this.originalToMapped = new HashMap<Long, Long>();
      this.newIds = new ArrayList<Long>();
      if (aliases != null && aliases.length > 0) {
         for (String alias : aliases) {
            this.aliases.add(alias.toLowerCase());
         }
      }
   }

   public Object getId(Object original) throws Exception {
      Long originalLong = null;
      if (original instanceof Double) {
         originalLong = ((Double) original).longValue();
      } else if (original instanceof Integer) {
         originalLong = ((Integer) original).longValue();
      } else if (original instanceof Long) {
         originalLong = ((Long) original).longValue();
      } else {
         System.out.println("Error here: " + original.getClass().getName());
      }
      Long newVersion = transalateId(originalLong);
      Object toReturn = newVersion;
      if (original instanceof Double) {
         toReturn = Double.valueOf((double) newVersion);
      } else if (original instanceof Integer) {
         toReturn = newVersion.intValue();
      } else if (original instanceof Long) {
         toReturn = newVersion;
      } else {
         System.out.println("Error here: " + original.getClass().getName());
      }
      return toReturn;
   }

   private Long transalateId(Long original) throws SQLException {
      Long newVersion = null;
      if (original <= 0L) {
         newVersion = original;
      } else {
         newVersion = this.originalToMapped.get(original);
         if (newVersion == null) {
            newVersion = SequenceManager.getNextSequence(getSequence());
            addToCache(original, newVersion);
         }
      }
      return newVersion;
   }

   public String getSequence() {
      return this.sequenceName;
   }

   public boolean hasAliases() {
      return this.aliases.size() > 0;
   }

   public Set<String> getAliases() {
      return this.aliases;
   }

   public void addToCache(Long original, Long newValue) {
      originalToMapped.put(original, newValue);
      newIds.add(original);
   }

   public Long getFromCache(Long original) {
      Long newVersion = null;
      if (original <= 0L) {
         newVersion = original;
      } else {
         newVersion = this.originalToMapped.get(original);
      }
      return newVersion;
   }

   public void load(Connection connection, String sourceDatabaseId) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         originalToMapped.clear();
         chStmt =
               ConnectionHandler.runPreparedQuery(connection, SELECT_IDS_BY_DB_SOURCE_AND_SEQ_NAME, sourceDatabaseId,
                     getSequence());
         if (chStmt.next()) {
            originalToMapped.put(chStmt.getRset().getLong(1), chStmt.getRset().getLong(2));
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public void store(Connection connection, int importId) throws SQLException {
      if (!newIds.isEmpty()) {
         List<Object[]> data = new ArrayList<Object[]>();
         for (Long original : newIds) {
            Long mapped = originalToMapped.get(original);
            data.add(new Object[] {importId, original, mapped});
         }
         ConnectionHandler.runPreparedUpdate(connection, INSERT_INTO_IMPORT_INDEX_MAP, data);
      }
   }
}
