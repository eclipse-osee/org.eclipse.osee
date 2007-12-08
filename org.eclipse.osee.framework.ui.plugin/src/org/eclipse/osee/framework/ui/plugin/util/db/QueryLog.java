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

import java.util.ArrayList;

/**
 * Configurable log to track query characteristics.
 * 
 * @author Robert A. Fisher
 */
public class QueryLog {
   private static QueryLog reference = null;
   private ArrayList<QueryRecord> records;
   private int maxRecords;

   public static synchronized QueryLog getInstance() {
      if (reference == null) {
         reference = new QueryLog();
      }

      return reference;
   }

   private QueryLog() {
      this.maxRecords = 200;
      this.records = new ArrayList<QueryRecord>(maxRecords);
   }

   protected synchronized void add(QueryRecord record) {
      if (record == null) throw new IllegalArgumentException("record can not be null");

      if (records.size() < maxRecords) {
         records.add(record);
      }
   }

   /**
    * @return the maxRecords
    */
   public int getMaxRecords() {
      return maxRecords;
   }

   /**
    * @param maxRecords the maxRecords to set
    */
   public void setMaxRecords(int maxRecords) {
      if (maxRecords < 0) throw new IllegalArgumentException("maxRecords can not be negative");

      if (maxRecords < this.maxRecords && records.size() > maxRecords) {
         records.subList(maxRecords, records.size()).clear();
         records.trimToSize();
      }

      this.maxRecords = maxRecords;
   }

   /**
    * @return the records
    */
   public ArrayList<QueryRecord> getRecords() {
      return records;
   }

   public boolean isFull() {
      return records.size() >= maxRecords;
   }

   public void clear() {
      records.clear();
   }
}
