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

import java.util.HashMap;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Ryan D. Brooks
 */
public class SequenceManager {
   private static final String QUERY_SEQUENCE = "SELECT last_sequence FROM osee_sequence WHERE sequence_name = ?";
   private static final String INSERT_SEQUENCE =
         "INSERT INTO osee_sequence (last_sequence, sequence_name) VALUES (?,?)";
   private static final String UPDATE_SEQUENCE =
         "UPDATE osee_sequence SET last_sequence = ? WHERE sequence_name = ? AND last_sequence = ?";

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
   public static final String IMPORT_ID_SEQ = "SKYNET_IMPORT_ID_SEQ";
   public static final String IMPORT_MAPPED_INDEX_SEQ = "SKYNET_IMPORT_MAPPED_INDEX_SEQ";
   public static final String TTE_SESSION_SEQ = "TTE_SESSION_SEQ";

   public static final String[] sequenceNames =
         new String[] {ART_ID_SEQ, ART_TYPE_ID_SEQ, ATTR_BASE_TYPE_ID_SEQ, ATTR_PROVIDER_TYPE_ID_SEQ, ATTR_ID_SEQ,
               ATTR_TYPE_ID_SEQ, FACTORY_ID_SEQ, BRANCH_ID_SEQ, REL_LINK_TYPE_ID_SEQ, REL_LINK_ID_SEQ, GAMMA_ID_SEQ,
               TRANSACTION_ID_SEQ, IMPORT_ID_SEQ, IMPORT_MAPPED_INDEX_SEQ, TTE_SESSION_SEQ};

   private final static HashMap<String, SequenceRange> sequences = new HashMap<String, SequenceRange>(30);

   private SequenceManager() {
   }

   private static SequenceRange getRange(String sequenceName) {
      SequenceRange range = sequences.get(sequenceName);
      if (range == null) {
         // do this to keep transaction id's sequential in the face of concurrent transaction by multiple users
         range = new SequenceRange(!sequenceName.equals(TRANSACTION_ID_SEQ));
         sequences.put(sequenceName, range);
      }
      return range;
   }

   public static synchronized long getNextSequence(String sequenceName) throws OseeDataStoreException {
      SequenceRange range = getRange(sequenceName);
      if (range.lastAvailable == 0) {
         long lastValue = -1;
         boolean gotSequence = false;
         OseeConnection connection = OseeDbConnection.getConnection();
         try {
            while (!gotSequence) {
               lastValue = ConnectionHandler.runPreparedQueryFetchLong(connection, -1, QUERY_SEQUENCE, sequenceName);
               if (lastValue == -1) {
                  throw new OseeDataStoreException("Sequence name [" + sequenceName + "] was not found");
               }
               gotSequence =
                     ConnectionHandler.runPreparedUpdate(connection, UPDATE_SEQUENCE, lastValue + range.prefetchSize,
                           sequenceName, lastValue) == 1;
            }
            range.updateRange(lastValue);
         } finally {
            connection.close();
         }
      }
      range.currentValue++;
      if (range.currentValue == range.lastAvailable) {
         range.lastAvailable = 0;
      }
      return range.currentValue;
   }

   public static void internalInitializeSequence(String sequenceName) throws OseeDataStoreException {
      SequenceRange range = getRange(sequenceName);
      range.lastAvailable = 0;
      ConnectionHandler.runPreparedUpdate(INSERT_SEQUENCE, 0, sequenceName);
   }

   public static int getNextSessionId() throws OseeDataStoreException {
      return (int) getNextSequence(TTE_SESSION_SEQ);
   }

   public static int getNextTransactionId() throws OseeDataStoreException {
      return (int) getNextSequence(TRANSACTION_ID_SEQ);
   }

   public static int getNextArtifactId() throws OseeDataStoreException {
      return (int) getNextSequence(ART_ID_SEQ);
   }

   public static int getNextGammaId() throws OseeDataStoreException {
      return (int) getNextSequence(GAMMA_ID_SEQ);
   }

   public static int getNextArtifactTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(ART_TYPE_ID_SEQ);
   }

   public static int getNextAttributeBaseTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(ATTR_BASE_TYPE_ID_SEQ);
   }

   public static int getNextAttributeProviderTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(ATTR_PROVIDER_TYPE_ID_SEQ);
   }

   public static int getNextAttributeId() throws OseeDataStoreException {
      return (int) getNextSequence(ATTR_ID_SEQ);
   }

   public static int getNextAttributeTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(ATTR_TYPE_ID_SEQ);
   }

   public static int getNextFactoryId() throws OseeDataStoreException {
      return (int) getNextSequence(FACTORY_ID_SEQ);
   }

   public static int getNextBranchId() throws OseeDataStoreException {
      return (int) getNextSequence(BRANCH_ID_SEQ);
   }

   public static int getNextRelationTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(REL_LINK_TYPE_ID_SEQ);
   }

   public static int getNextRelationId() throws OseeDataStoreException {
      return (int) getNextSequence(REL_LINK_ID_SEQ);
   }

   public static int getNextImportId() throws OseeDataStoreException {
      return (int) getNextSequence(IMPORT_ID_SEQ);
   }

   public static int getNextImportMappedIndexId() throws OseeDataStoreException {
      return (int) getNextSequence(IMPORT_MAPPED_INDEX_SEQ);
   }

   private static final class SequenceRange {
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