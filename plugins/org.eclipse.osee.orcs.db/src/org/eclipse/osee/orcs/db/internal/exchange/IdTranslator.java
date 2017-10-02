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
package org.eclipse.osee.orcs.db.internal.exchange;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;

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
   private final JdbcClient jdbcClient;

   IdTranslator(JdbcClient jdbcClient, String sequenceName, String... aliases) {
      this.jdbcClient = jdbcClient;
      this.sequenceName = sequenceName;
      this.originalToMapped = new HashMap<>();
      this.newIds = new ArrayList<>();
      this.aliases = new HashSet<>();
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

   public Object getId(Object original) {
      Long originalLong = null;
      if (original instanceof Double) {
         originalLong = ((Double) original).longValue();
      } else if (original instanceof Integer) {
         originalLong = ((Integer) original).longValue();
      } else if (original instanceof Long) {
         originalLong = ((Long) original).longValue();
      } else if (original instanceof BigInteger) {
         originalLong = ((BigInteger) original).longValue();
      } else if (original instanceof BigDecimal) {
         originalLong = ((BigDecimal) original).longValue();
      } else {
         throw new OseeCoreException("Undefined Type [%s]",
            original != null ? original.getClass().getSimpleName() : original);
      }
      Long newVersion = transalateId(originalLong);
      Object toReturn = newVersion;
      if (original instanceof Double) {
         toReturn = Double.valueOf(newVersion);
      } else if (original instanceof Integer) {
         toReturn = newVersion.intValue();
      } else if (original instanceof Long) {
         toReturn = newVersion;
      } else if (original instanceof BigInteger) {
         toReturn = BigInteger.valueOf(newVersion);
      } else if (original instanceof BigDecimal) {
         toReturn = BigDecimal.valueOf(newVersion);
      } else {
         throw new OseeCoreException("Undefined Type [%s]", original.getClass().getSimpleName());
      }
      return toReturn;
   }

   private Long transalateId(Long original) {
      Long newVersion = null;
      if (original <= 0L) {
         newVersion = original;
      } else {
         newVersion = this.originalToMapped.get(original);
         if (newVersion == null) {
            newVersion = jdbcClient.getNextSequence(getSequenceName(), true);
            addToCache(original, newVersion);
         }
      }
      return newVersion;
   }

   public String getSequenceName() {
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

   public void load(String sourceDatabaseId) {
      originalToMapped.clear();
      jdbcClient.runQuery(stmt -> originalToMapped.put(stmt.getLong("original_id"), stmt.getLong("mapped_id")),
         SELECT_IDS_BY_DB_SOURCE_AND_SEQ_NAME, sourceDatabaseId, getSequenceName());
   }

   public boolean hasItemsToStore() {
      return !newIds.isEmpty();
   }

   public void store(JdbcConnection connection, int sequenceId) {
      if (hasItemsToStore()) {
         List<Object[]> data = new ArrayList<>();
         for (Long original : newIds) {
            Long mapped = originalToMapped.get(original);
            data.add(new Object[] {sequenceId, original, mapped});
         }
         jdbcClient.runBatchUpdate(connection, INSERT_INTO_IMPORT_INDEX_MAP, data);
      }
   }
}
