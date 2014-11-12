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
package org.eclipse.osee.jdbc.internal;

import java.util.HashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcTransaction;

/**
 * This should be modified to use native database sequences
 * 
 * @author Ryan D. Brooks
 */
public class JdbcSequenceProvider {

   private final HashMap<String, SequenceRange> sequences = new HashMap<String, SequenceRange>(30);

   public synchronized void invalidate() {
      sequences.clear();
   }

   public synchronized long getNextSequence(JdbcClient client, String sequenceName) throws OseeCoreException {
      JdbcSequenceTx sequenceTx = new JdbcSequenceTx(client, sequenceName);
      client.runTransaction(sequenceTx);
      return sequenceTx.getNextSequence();
   }

   private final class JdbcSequenceTx extends JdbcTransaction {

      private static final String TRANSACTION_ID_SEQ = "SKYNET_TRANSACTION_ID_SEQ";
      private static final String QUERY_SEQUENCE = "SELECT last_sequence FROM osee_sequence WHERE sequence_name = ?";
      private static final String INSERT_SEQUENCE =
         "INSERT INTO osee_sequence (last_sequence, sequence_name) VALUES (?,?)";
      private static final String UPDATE_SEQUENCE =
         "UPDATE osee_sequence SET last_sequence = ? WHERE sequence_name = ? AND last_sequence = ?";

      private final JdbcClient client;
      private final String sequenceName;
      private long nextSequence;

      public JdbcSequenceTx(JdbcClient client, String sequenceName) {
         super();
         this.client = client;
         this.sequenceName = sequenceName;
      }

      public long getNextSequence() {
         return nextSequence;
      }

      private SequenceRange getRange(String sequenceName) {
         SequenceRange range = sequences.get(sequenceName);
         if (range == null) {
            // do this to keep transaction id's sequential in the face of concurrent transaction by multiple users
            boolean aggressiveFetch = !sequenceName.equals(TRANSACTION_ID_SEQ);

            range = new SequenceRange(aggressiveFetch);
            sequences.put(sequenceName, range);
         }
         return range;
      }

      @Override
      public void handleTxWork(JdbcConnection connection) {
         SequenceRange range = getRange(sequenceName);
         if (range.lastAvailable == 0) {
            long lastValue = -1L;
            boolean gotSequence = false;
            while (!gotSequence) {
               long currentValue =
                  client.runPreparedQueryFetchObject(connection, lastValue, QUERY_SEQUENCE, sequenceName);
               if (currentValue == lastValue) {
                  internalInitializeSequence(connection, sequenceName);
                  lastValue = 0;
               } else {
                  lastValue = currentValue;
               }
               gotSequence =
                  client.runPreparedUpdate(connection, UPDATE_SEQUENCE, lastValue + range.prefetchSize, sequenceName,
                     lastValue) == 1;
            }
            range.updateRange(lastValue);
         }
         range.currentValue++;
         if (range.currentValue == range.lastAvailable) {
            range.lastAvailable = 0;
         }
         nextSequence = range.currentValue;
      }

      private void internalInitializeSequence(JdbcConnection connection, String sequenceName) throws OseeCoreException {
         SequenceRange range = getRange(sequenceName);
         range.lastAvailable = 0;
         client.runPreparedUpdate(connection, INSERT_SEQUENCE, 0, sequenceName);
      }
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