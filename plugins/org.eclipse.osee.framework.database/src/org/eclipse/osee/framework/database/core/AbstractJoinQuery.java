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
package org.eclipse.osee.framework.database.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.DatabaseJoinAccessor.JoinItem;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractJoinQuery {

   protected static interface IJoinRow {
      Object[] toArray();

      @Override
      String toString();
   }

   private final IJoinAccessor joinAccessor;
   private final JoinItem joinItem;
   private final int queryId;

   private final Timestamp insertTime;
   protected final Set<IJoinRow> entries = new HashSet<IJoinRow>();

   private boolean wasStored;
   private int storedSize;

   protected AbstractJoinQuery(IJoinAccessor joinAccessor, JoinItem joinItem, int queryId) {
      this.joinAccessor = joinAccessor;
      this.joinItem = joinItem;
      this.queryId = queryId;
      this.insertTime = GlobalTime.GreenwichMeanTimestamp();
      this.storedSize = -1;
      this.wasStored = false;
   }

   public boolean isEmpty() {
      return this.wasStored != true ? entries.isEmpty() : this.storedSize > 0;
   }

   public int size() {
      return this.wasStored != true ? entries.size() : this.storedSize;
   }

   public int getQueryId() {
      return queryId;
   }

   public Timestamp getInsertTime() {
      return insertTime;
   }

   public String getJoinTableName() {
      return joinItem.getJoinTableName();
   }

   public boolean wasStored() {
      return wasStored;
   }

   public void store(OseeConnection connection) throws OseeCoreException {
      if (this.wasStored != true) {
         List<Object[]> data = new ArrayList<Object[]>();
         for (IJoinRow joinArray : entries) {
            data.add(joinArray.toArray());
         }
         joinAccessor.store(connection, joinItem, getQueryId(), data);
         this.storedSize = this.entries.size();
         this.wasStored = true;
         this.entries.clear();
      } else {
         throw new OseeDataStoreException("Cannot store query id twice");
      }
   }

   public int delete(OseeConnection connection) throws OseeCoreException {
      return joinAccessor.delete(connection, joinItem, getQueryId());
   }

   public void store() throws OseeCoreException {
      store(null);
   }

   public int delete() throws OseeCoreException {
      return delete(null);
   }

   public Collection<Integer> getAllQueryIds(OseeConnection connection) throws OseeCoreException {
      return joinAccessor.getAllQueryIds(connection, joinItem);
   }

   public Collection<Integer> getAllQueryIds() throws OseeCoreException {
      return joinAccessor.getAllQueryIds(null, joinItem);
   }

   @Override
   public String toString() {
      return String.format("id: [%s] entrySize: [%d]", getQueryId(), size());
   }
}