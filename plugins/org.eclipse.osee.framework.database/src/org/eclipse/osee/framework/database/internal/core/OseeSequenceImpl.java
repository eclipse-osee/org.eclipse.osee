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
import org.eclipse.osee.framework.database.DatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public class OseeSequenceImpl {
   private static final String TRANSACTION_ID_SEQ = "SKYNET_TRANSACTION_ID_SEQ";

   private static final String QUERY_SEQUENCE = "SELECT last_sequence FROM osee_sequence WHERE sequence_name = ?";
   private static final String INSERT_SEQUENCE =
      "INSERT INTO osee_sequence (last_sequence, sequence_name) VALUES (?,?)";
   private static final String UPDATE_SEQUENCE =
      "UPDATE osee_sequence SET last_sequence = ? WHERE sequence_name = ? AND last_sequence = ?";

   private final HashMap<String, SequenceRange> sequences = new HashMap<String, SequenceRange>(30);

   private final DatabaseService service;

   public OseeSequenceImpl(DatabaseService service) {
      this.service = service;
   }

   private DatabaseService getDatabase() {
      return service;
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

   public synchronized void clear() {
      sequences.clear();
   }

   @SuppressWarnings("unchecked")
   public synchronized long getNextSequence(String sequenceName) throws OseeCoreException {
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
   private void internalInitializeSequence(String sequenceName) throws OseeCoreException {
      SequenceRange range = getRange(sequenceName);
      range.lastAvailable = 0;
      getDatabase().runPreparedUpdate(INSERT_SEQUENCE, 0, sequenceName);
   }

   private static final class SequenceRange {
      private long currentValue;
      private long lastAvailable;
      private int prefetchSize;
      private final boolean aggressiveFetch;

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