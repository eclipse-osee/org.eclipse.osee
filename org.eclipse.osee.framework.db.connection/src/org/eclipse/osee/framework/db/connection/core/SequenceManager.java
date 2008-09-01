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
package org.eclipse.osee.framework.db.connection.core;

import java.sql.SQLException;
import java.util.HashMap;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;

/**
 * @author Ryan D. Brooks
 */
public class SequenceManager {
   private static final String LAST_SEQUENCE = "LAST_SEQUENCE";
   private static final String QUERY_SEQUENCE =
         "SELECT last_sequence FROM osee_define_sequence WHERE sequence_name = ?";
   private static final String INSERT_SEQUENCE =
         "INSERT INTO osee_define_sequence (last_sequence, sequence_name) VALUES (?,?)";
   private static final String UPDATE_SEQUENCE =
         "UPDATE osee_define_sequence SET last_sequence = ? WHERE sequence_name = ? AND last_sequence = ?";

   private HashMap<String, SequenceRange> sequences;

   public static final String ART_ID_SEQ = "SKYNET_ART_ID_SEQ";
   public static final String ART_TYPE_ID_SEQ = "SKYNET_ART_TYPE_ID_SEQ";
   public static final String ATTR_BASE_TYPE_ID_SEQ = "SKYNET_ATTR_BASE_TYPE_ID_SEQ";
   public static final String ATTR_PROVIDER_TYPE_ID_SEQ = "SKYNET_ATTR_PROVIDER_TYPE_ID_SEQ";
   public static final String ATTR_ID_SEQ = "SKYNET_ATTR_ID_SEQ";
   public static final String ATTR_TYPE_ID_SEQ = "SKYNET_ATTR_TYPE_ID_SEQ";
   public static final String FACTORY_ID_SEQ = "SKYNET_FACTORY_ID_SEQ";
   public static final String BRANCH_ID_SEQ = "SKYNET_BRANCH_ID_SEQ";
   public static final String REL_LINK_TYPE_ID_SEQ = "SKYNET_REL_LINK_TYPE_ID_SEQ";
   public static final String REL_LINK_ID_SEQ = "SKYNET_REL_LINK_ID_SEQ";
   public static final String GAMMA_ID_SEQ = "SKYNET_GAMMA_ID_SEQ";
   public static final String TRANSACTION_ID_SEQ = "SKYNET_TRANSACTION_ID_SEQ";
   public static final String TTE_SESSION_SEQ = "TTE_SESSION_SEQ";

   public static final String[] sequenceNames =
         new String[] {ART_ID_SEQ, ART_TYPE_ID_SEQ, ATTR_BASE_TYPE_ID_SEQ, ATTR_PROVIDER_TYPE_ID_SEQ, ATTR_ID_SEQ,
               ATTR_TYPE_ID_SEQ, FACTORY_ID_SEQ, BRANCH_ID_SEQ, REL_LINK_TYPE_ID_SEQ, REL_LINK_ID_SEQ, GAMMA_ID_SEQ,
               TRANSACTION_ID_SEQ, TTE_SESSION_SEQ};

   private static final SequenceManager instance = new SequenceManager();

   private SequenceManager() {
      sequences = new HashMap<String, SequenceRange>(30);
   }

   public static SequenceManager getInstance() {
      return instance;
   }

   private SequenceRange getRange(String sequenceName) {
      SequenceRange range = sequences.get(sequenceName);
      if (range == null) {
         // do this to keep transaction id's sequential in the face of concurrent transaction by multiple users
         range = new SequenceRange(!sequenceName.equals(TRANSACTION_ID_SEQ));
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
            ConnectionHandler.runPreparedUpdateReturnStmt(true, UPDATE_SEQUENCE, value, sequenceName, lastValue);
      return modifySequenceValue(chStmt);
   }

   private boolean insertSequenceValue(String sequenceName, long value) throws SQLException {
      ConnectionHandlerStatement chStmt =
            ConnectionHandler.runPreparedUpdateReturnStmt(true, INSERT_SEQUENCE, value, sequenceName);
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
      long toReturn = -1;
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(true, QUERY_SEQUENCE, sequenceName);
         if (chStmt.next()) {
            toReturn = chStmt.getRset().getLong(LAST_SEQUENCE);
         } else {
            throw new SQLException("Sequence name [" + sequenceName + "] was not found");
         }
      } finally {
         DbUtil.close(chStmt);
      }
      return toReturn;
   }

   public static synchronized long getNextSequence(String sequenceName) throws SQLException {
      SequenceRange range = instance.getRange(sequenceName);
      if (range.lastAvailable == 0) {
         instance.prefetch(sequenceName);
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

   public static int getNextSessionId() throws SQLException {
      return (int) getNextSequence(TTE_SESSION_SEQ);
   }

   public static int getNextTransactionId() throws SQLException {
      return (int) getNextSequence(TRANSACTION_ID_SEQ);
   }

   public static int getNextArtifactId() throws SQLException {
      return (int) getNextSequence(ART_ID_SEQ);
   }

   public static int getNextGammaId() throws SQLException {
      return (int) getNextSequence(GAMMA_ID_SEQ);
   }

   public static int getNextArtifactTypeId() throws SQLException {
      return (int) getNextSequence(ART_TYPE_ID_SEQ);
   }

   public static int getNextAttributeBaseTypeId() throws SQLException {
      return (int) getNextSequence(ATTR_BASE_TYPE_ID_SEQ);
   }

   public static int getNextAttributeProviderTypeId() throws SQLException {
      return (int) getNextSequence(ATTR_PROVIDER_TYPE_ID_SEQ);
   }

   public static int getNextAttributeId() throws SQLException {
      return (int) getNextSequence(ATTR_ID_SEQ);
   }

   public static int getNextAttributeTypeId() throws SQLException {
      return (int) getNextSequence(ATTR_TYPE_ID_SEQ);
   }

   public static int getNextFactoryId() throws SQLException {
      return (int) getNextSequence(FACTORY_ID_SEQ);
   }

   public static int getNextBranchId() throws SQLException {
      return (int) getNextSequence(BRANCH_ID_SEQ);
   }

   public static int getNextRelationTypeId() throws SQLException {
      return (int) getNextSequence(REL_LINK_TYPE_ID_SEQ);
   }

   public static int getNextRelationId() throws SQLException {
      return (int) getNextSequence(REL_LINK_ID_SEQ);
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