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
package org.eclipse.osee.framework.ui.plugin.util.db;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.SEQUENCE_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

public class OseeSequenceManager {
   private static final String SEQUENCE_NAME = "SEQUENCE_NAME";
   private static final String LAST_SEQUENCE = "LAST_SEQUENCE";
   private static final String QUERY_SEQUENCE =
         "SELECT " + LAST_SEQUENCE + " FROM " + SEQUENCE_TABLE + " WHERE " + SEQUENCE_NAME + " = ? ";
   private static final String INSERT_SEQUENCE =
         "INSERT INTO " + SEQUENCE_TABLE + " ( " + LAST_SEQUENCE + ", " + SEQUENCE_NAME + ") VALUES (?,?)";
   private static final String UPDATE_SEQUENCE =
         "UPDATE " + SEQUENCE_TABLE + " SET " + LAST_SEQUENCE + " = ? WHERE " + SEQUENCE_NAME + " = ? AND " + LAST_SEQUENCE + " = ?";

   private HashMap<String, SequenceRange> sequences;

   private static final OseeSequenceManager instance = new OseeSequenceManager();

   private OseeSequenceManager() {
      sequences = new HashMap<String, SequenceRange>(30);
   }

   public static OseeSequenceManager getInstance() {
      return instance;
   }

   private SequenceRange getRange(String sequenceName) {
      SequenceRange range = sequences.get(sequenceName);
      if (range == null) {
         // do this to keep transaction id's sequential in the face of concurrent transaction by multiple users
         range = new SequenceRange(!sequenceName.equals(SkynetDatabase.TRANSACTION_ID_SEQ));
         sequences.put(sequenceName, range);
      }
      return range;
   }

   private void prefetch(String sequenceName) throws SQLException {
      SequenceRange range = getRange(sequenceName);

      long lastValue = -1;
      boolean gotSequence = false;
      while (!gotSequence) {
         lastValue = getSequence(sequenceName);
         gotSequence = updateSequenceValue(sequenceName, lastValue + range.prefetchSize, lastValue);
      }
      range.updateRange(lastValue);
   }

   private boolean updateSequenceValue(String sequenceName, long value, long lastValue) throws SQLException {
      ConnectionHandlerStatement chStmt =
            ConnectionHandler.runPreparedUpdateReturnStmt(true, UPDATE_SEQUENCE, SQL3DataType.BIGINT, value,
                  SQL3DataType.VARCHAR, sequenceName, SQL3DataType.BIGINT, lastValue);
      return modifySequenceValue(chStmt);
   }

   private boolean insertSequenceValue(String sequenceName, long value) throws SQLException {
      ConnectionHandlerStatement chStmt =
            ConnectionHandler.runPreparedUpdateReturnStmt(true, INSERT_SEQUENCE, SQL3DataType.BIGINT, value,
                  SQL3DataType.VARCHAR, sequenceName);
      return modifySequenceValue(chStmt);
   }

   private boolean modifySequenceValue(ConnectionHandlerStatement chStmt) throws SQLException {
      boolean updated = false;
      try {
         updated = chStmt.getStatement().getUpdateCount() == 1;
      } finally {
         DbUtil.close(chStmt);
      }
      return updated;
   }

   private long getSequence(String sequenceName) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(true, QUERY_SEQUENCE, SQL3DataType.VARCHAR, sequenceName);
         ResultSet rSet = chStmt.getRset();
         if (rSet.next()) {
            return rSet.getLong(LAST_SEQUENCE);
         } else {
            throw new SQLException("Sequence name [" + sequenceName + "] not found in " + SEQUENCE_TABLE);
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public synchronized long getNextSequence(String sequenceName) throws SQLException {
      SequenceRange range = getRange(sequenceName);
      if (range.lastAvailable == 0) {
         prefetch(sequenceName);
      }

      range.currentValue++;
      if (range.currentValue == range.lastAvailable) {
         range.lastAvailable = 0;
      }
      return range.currentValue;
   }

   public void initializeSequence(String sequenceName) throws SQLException {
      SequenceRange range = getRange(sequenceName);
      range.lastAvailable = 0;
      insertSequenceValue(sequenceName, 0);
   }

   private class SequenceRange {
      private long currentValue;
      private long lastAvailable;
      private int prefetchSize;
      private final boolean aggressiveFetch;

      /**
       * @param aggressiveFetch
       */
      public SequenceRange(boolean aggressiveFetch) {
         super();
         this.prefetchSize = 1;
         this.aggressiveFetch = aggressiveFetch;
      }

      public void updateRange(long lastValue) {
         currentValue = lastValue;
         lastAvailable = lastValue + prefetchSize;

         if (aggressiveFetch) {
            prefetchSize *= 2; // next time grab twice as many
         }
      }
   }
}