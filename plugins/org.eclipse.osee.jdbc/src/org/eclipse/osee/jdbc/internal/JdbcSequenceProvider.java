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
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Ryan D. Brooks
 */
public class JdbcSequenceProvider {
   private static final String QUERY_SEQUENCE = "SELECT last_sequence FROM osee_sequence WHERE sequence_name = ?";
   public static final String INSERT_SEQUENCE = "INSERT INTO osee_sequence (last_sequence, sequence_name) VALUES (?,?)";
   private static final String UPDATE_SEQUENCE =
      "UPDATE osee_sequence SET last_sequence = ? WHERE sequence_name = ? AND last_sequence = ?";

   private final HashMap<String, SequenceRange> sequences = new HashMap<>(30);

   public synchronized void invalidate() {
      sequences.clear();
   }

   public synchronized long getNextSequence(JdbcClient client, String sequenceName, boolean aggressiveFetch) {
      SequenceRange range = getRange(sequenceName);
      if (range.lastAvailable == 0) {
         long lastValue = -1L;
         boolean gotSequence = false;
         while (!gotSequence) {
            long currentValue = client.fetch(lastValue, QUERY_SEQUENCE, sequenceName);
            if (currentValue == lastValue) {
               internalInitializeSequence(client, sequenceName);
               lastValue = 0;
            } else {
               lastValue = currentValue;
            }
            gotSequence =
               client.runPreparedUpdate(UPDATE_SEQUENCE, lastValue + range.prefetchSize, sequenceName, lastValue) == 1;
         }
         range.updateRange(lastValue, aggressiveFetch);
      }
      range.currentValue++;
      if (range.currentValue == range.lastAvailable) {
         range.lastAvailable = 0;
      }
      return range.currentValue;
   }

   private SequenceRange getRange(String sequenceName) {
      SequenceRange range = sequences.get(sequenceName);
      if (range == null) {
         range = new SequenceRange();
         sequences.put(sequenceName, range);
      }
      return range;
   }

   private void internalInitializeSequence(JdbcClient client, String sequenceName) {
      SequenceRange range = getRange(sequenceName);
      range.lastAvailable = 0;
      int initalValue = 0;
      if (sequenceName.equals(OseeData.ART_ID_SEQ)) {
         initalValue = 200000;
      }
      client.runPreparedUpdate(INSERT_SEQUENCE, initalValue, sequenceName);
   }

   private static final class SequenceRange {
      private long currentValue;
      private long lastAvailable;
      private int prefetchSize;

      public SequenceRange() {
         this.prefetchSize = 1;
      }

      public void updateRange(long lastValue, boolean aggressiveFetch) {
         currentValue = lastValue;
         lastAvailable = lastValue + prefetchSize;

         if (aggressiveFetch) {
            prefetchSize *= 2; // next time grab twice as many
         }
      }
   }
}