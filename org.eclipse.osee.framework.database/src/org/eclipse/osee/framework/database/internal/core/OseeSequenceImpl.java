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
package org.eclipse.osee.framework.database.internal.core;

import java.util.HashMap;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IOseeSequence;
import org.eclipse.osee.framework.database.core.OseeConnection;

/**
 * @author Ryan D. Brooks
 */
public class OseeSequenceImpl implements IOseeSequence {
   private static final String QUERY_SEQUENCE = "SELECT last_sequence FROM osee_sequence WHERE sequence_name = ?";
   private static final String INSERT_SEQUENCE =
         "INSERT INTO osee_sequence (last_sequence, sequence_name) VALUES (?,?)";
   private static final String UPDATE_SEQUENCE =
         "UPDATE osee_sequence SET last_sequence = ? WHERE sequence_name = ? AND last_sequence = ?";

   private final static HashMap<String, SequenceRange> sequences = new HashMap<String, SequenceRange>(30);

   private final IOseeDatabaseServiceProvider serviceProvider;

   public OseeSequenceImpl(IOseeDatabaseServiceProvider serviceProvider) {
      this.serviceProvider = serviceProvider;
   }

   private IOseeDatabaseService getDatabase() throws OseeDataStoreException {
      return serviceProvider.getOseeDatabaseService();
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

   @SuppressWarnings("unchecked")
   public synchronized long getNextSequence(String sequenceName) throws OseeDataStoreException {
      SequenceRange range = getRange(sequenceName);
      if (range.lastAvailable == 0) {
         long lastValue = -1L;
         boolean gotSequence = false;
         OseeConnection connection = getDatabase().getConnection();
         try {
            while (!gotSequence) {
               long currentValue =
                     getDatabase().runPreparedQueryFetchObject(connection, lastValue, QUERY_SEQUENCE, sequenceName);
               if (currentValue == lastValue) {
                  internalInitializeSequence(sequenceName);
                  lastValue = 0;
               } else {
                  lastValue = currentValue;
               }
               gotSequence =
                     getDatabase().runPreparedUpdate(connection, UPDATE_SEQUENCE, lastValue + range.prefetchSize,
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

   @SuppressWarnings("unchecked")
   private void internalInitializeSequence(String sequenceName) throws OseeDataStoreException {
      SequenceRange range = getRange(sequenceName);
      range.lastAvailable = 0;
      getDatabase().runPreparedUpdate(INSERT_SEQUENCE, 0, sequenceName);
   }

   public int getNextSessionId() throws OseeDataStoreException {
      return (int) getNextSequence(TTE_SESSION_SEQ);
   }

   public int getNextTransactionId() throws OseeDataStoreException {
      return (int) getNextSequence(TRANSACTION_ID_SEQ);
   }

   public int getNextArtifactId() throws OseeDataStoreException {
      return (int) getNextSequence(ART_ID_SEQ);
   }

   public int getNextOseeEnumTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(ENUM_TYPE_ID_SEQ);
   }

   public int getNextGammaId() throws OseeDataStoreException {
      return (int) getNextSequence(GAMMA_ID_SEQ);
   }

   public int getNextArtifactTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(ART_TYPE_ID_SEQ);
   }

   public int getNextAttributeBaseTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(ATTR_BASE_TYPE_ID_SEQ);
   }

   public int getNextAttributeProviderTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(ATTR_PROVIDER_TYPE_ID_SEQ);
   }

   public int getNextAttributeId() throws OseeDataStoreException {
      return (int) getNextSequence(ATTR_ID_SEQ);
   }

   public int getNextAttributeTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(ATTR_TYPE_ID_SEQ);
   }

   public int getNextFactoryId() throws OseeDataStoreException {
      return (int) getNextSequence(FACTORY_ID_SEQ);
   }

   public int getNextBranchId() throws OseeDataStoreException {
      return (int) getNextSequence(BRANCH_ID_SEQ);
   }

   public int getNextRelationTypeId() throws OseeDataStoreException {
      return (int) getNextSequence(REL_LINK_TYPE_ID_SEQ);
   }

   public int getNextRelationId() throws OseeDataStoreException {
      return (int) getNextSequence(REL_LINK_ID_SEQ);
   }

   public int getNextImportId() throws OseeDataStoreException {
      return (int) getNextSequence(IMPORT_ID_SEQ);
   }

   public int getNextImportMappedIndexId() throws OseeDataStoreException {
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