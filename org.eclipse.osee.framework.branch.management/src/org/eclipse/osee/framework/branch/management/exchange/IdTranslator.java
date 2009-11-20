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
package org.eclipse.osee.framework.branch.management.exchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.SequenceManager;

/**
 * @author Roberto E. Escobar
 */
public class IdTranslator {
   private static final String INSERT_INTO_IMPORT_INDEX_MAP =
         "INSERT INTO osee_import_index_map (sequence_id, original_id, mapped_id) VALUES (?, ?, ?)";

   private static final String SELECT_IDS_BY_DB_SOURCE_AND_SEQ_NAME =
         "SELECT original_id, mapped_id FROM osee_import_source ois, osee_import_map oim, osee_import_index_map oiim WHERE ois.import_id = oim.import_id AND oim.sequence_id = oiim.sequence_id AND oiim.sequence_id = oiim.sequence_id AND ois.db_source_guid = ?  AND oim.sequence_name = ?";

   private final String sequenceName;
   private final Map<Long, Long> originalToMapped;
   private final List<Long> newIds;
   private final Set<String> aliases;

   IdTranslator(String sequenceName, String... aliases) {
      this.sequenceName = sequenceName;
      this.originalToMapped = new HashMap<Long, Long>();
      this.newIds = new ArrayList<Long>();
      this.aliases = new HashSet<String>();
      if (aliases != null && aliases.length > 0) {
         for (String alias : aliases) {
            this.aliases.add(alias.toLowerCase());
         }
      }
   }

   public boolean hasAliases() {
      return this.aliases.size() > 0;
   }

   public Set<String> getAliases() {
      return this.aliases;
   }

   public Object getId(Object original) throws OseeDataStoreException {
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
         toReturn = Double.valueOf(newVersion);
      } else if (original instanceof Integer) {
         toReturn = newVersion.intValue();
      } else if (original instanceof Long) {
         toReturn = newVersion;
      } else {
         System.out.println("Error here: " + original.getClass().getName());
      }
      return toReturn;
   }

   private Long transalateId(Long original) throws OseeDataStoreException {
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

   public void load(String sourceDatabaseId) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         originalToMapped.clear();
         chStmt.runPreparedQuery(SELECT_IDS_BY_DB_SOURCE_AND_SEQ_NAME, sourceDatabaseId, getSequence());
         while (chStmt.next()) {
            originalToMapped.put(chStmt.getLong("original_id"), chStmt.getLong("mapped_id"));
         }
      } finally {
         chStmt.close();
      }
   }

   public boolean hasItemsToStore() {
      return !newIds.isEmpty();
   }

   public void store(OseeConnection connection, int sequenceId) throws OseeDataStoreException {
      if (hasItemsToStore()) {
         List<Object[]> data = new ArrayList<Object[]>();
         for (Long original : newIds) {
            Long mapped = originalToMapped.get(original);
            data.add(new Object[] {sequenceId, original, mapped});
         }
         ConnectionHandler.runBatchUpdate(connection, INSERT_INTO_IMPORT_INDEX_MAP, data);
      }
   }
}
