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
package org.eclipse.osee.framework.core.data;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;

/**
 * @author Roberto E. Escobar
 */
public class JoinUtility {

   private static final String INSERT_INTO_JOIN_ARTIFACT =
         "INSERT INTO osee_join_artifact (query_id, insert_time, art_id, branch_id) VALUES (?, ?, ?, ?)";

   private static final String INSERT_INTO_JOIN_ATTRIBUTE =
         "INSERT INTO osee_join_attribute (attr_query_id, insert_time, value) VALUES (?, ?, ?)";

   private static final String INSERT_INTO_JOIN_TRANSACTION =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) VALUES (?, ?, ?, ?)";

   private static final String INSERT_INTO_JOIN_SEARCH_TAGS =
         "INSERT INTO osee_join_search_tags (query_id, insert_time, coded_tag_id) VALUES (?, ?, ?)";

   private static final String INSERT_INTO_TAG_GAMMA_QUEUE =
         "INSERT INTO osee_tag_gamma_queue (query_id, insert_time, gamma_id) VALUES (?, ?, ?)";

   private static final String INSERT_INTO_JOIN_EXPORT_IMPORT =
         "INSERT INTO osee_join_export_import (query_id, insert_time, id1, id2) VALUES (?, ?, ?, ?)";

   private static final String DELETE_FROM_JOIN_TRANSACTION = "DELETE FROM osee_join_transaction WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_ARTIFACT = "DELETE FROM osee_join_artifact WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_ATTRIBUTE = "DELETE FROM osee_join_attribute WHERE attr_query_id = ?";
   private static final String DELETE_FROM_JOIN_SEARCH_TAGS = "DELETE FROM osee_join_search_tags WHERE query_id = ?";
   private static final String DELETE_FROM_TAG_GAMMA_QUEUE = "DELETE FROM osee_tag_gamma_queue WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_EXPORT_IMPORT = "DELETE FROM osee_join_export_import WHERE query_id =?";

   private static final String SELECT_TAG_GAMMA_QUEUE_QUERIES = "select DISTINCT query_id from osee_tag_gamma_queue";

   public enum JoinItem {
      TRANSACTION(INSERT_INTO_JOIN_TRANSACTION, DELETE_FROM_JOIN_TRANSACTION),
      ARTIFACT(INSERT_INTO_JOIN_ARTIFACT, DELETE_FROM_JOIN_ARTIFACT),
      ATTRIBUTE(INSERT_INTO_JOIN_ATTRIBUTE, DELETE_FROM_JOIN_ATTRIBUTE),
      SEARCH_TAGS(INSERT_INTO_JOIN_SEARCH_TAGS, DELETE_FROM_JOIN_SEARCH_TAGS),
      TAG_GAMMA_QUEUE(INSERT_INTO_TAG_GAMMA_QUEUE, DELETE_FROM_TAG_GAMMA_QUEUE),
      EXPORT_IMPORT(INSERT_INTO_JOIN_EXPORT_IMPORT, DELETE_FROM_JOIN_EXPORT_IMPORT);

      private final String deleteSql;
      private final String insertSql;

      JoinItem(String insertSql, String deleteSql) {
         this.deleteSql = deleteSql;
         this.insertSql = insertSql;
      }

      String getDeleteSql() {
         return deleteSql;
      }

      String getInsertSql() {
         return insertSql;
      }
   }

   private JoinUtility() {
   }

   public static int getNewQueryId() {
      return (int) (Math.random() * Integer.MAX_VALUE);
   }

   public static TransactionJoinQuery createTransactionJoinQuery() {
      return new TransactionJoinQuery();
   }

   public static ArtifactJoinQuery createArtifactJoinQuery() {
      return new ArtifactJoinQuery();
   }

   public static AttributeJoinQuery createAttributeJoinQuery() {
      return new AttributeJoinQuery();
   }

   public static SearchTagJoinQuery createSearchTagJoinQuery() {
      return new SearchTagJoinQuery();
   }

   public static TagQueueJoinQuery createTagQueueJoinQuery() {
      return new TagQueueJoinQuery();
   }

   public static ExportImportJoinQuery createExportImportJoinQuery() {
      return new ExportImportJoinQuery();
   }

   public static List<Integer> getAllTagQueueQueryIds() throws OseeDataStoreException {
      List<Integer> queryIds = new ArrayList<Integer>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_TAG_GAMMA_QUEUE_QUERIES);
         while (chStmt.next()) {
            queryIds.add(chStmt.getInt("query_id"));
         }
      } finally {
         chStmt.close();
      }
      return queryIds;
   }

   private static abstract class JoinQueryEntry {
      public final JoinItem joinItem;
      private final int queryId;
      private final Timestamp insertTime;
      protected Set<IJoinRow> entries;
      private boolean wasStored;
      private int storedSize;

      public JoinQueryEntry(JoinItem joinItem) {
         this.wasStored = false;
         this.joinItem = joinItem;
         this.queryId = getNewQueryId();
         this.insertTime = GlobalTime.GreenwichMeanTimestamp();
         this.entries = new HashSet<IJoinRow>();
         this.storedSize = -1;
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

      public void store(Connection connection) throws OseeDataStoreException {
         if (this.wasStored != true) {
            List<Object[]> data = new ArrayList<Object[]>();
            for (IJoinRow joinArray : entries) {
               data.add(joinArray.toArray());
            }
            ConnectionHandler.runBatchUpdate(connection, joinItem.getInsertSql(), data);
            this.storedSize = this.entries.size();
            this.wasStored = true;
            this.entries.clear();
         } else {
            throw new OseeDataStoreException("Cannot store query id twice");
         }
      }

      public int delete(Connection connection) throws OseeDataStoreException {
         int updated = 0;
         if (queryId != -1) {
            updated = ConnectionHandler.runPreparedUpdate(connection, joinItem.getDeleteSql(), queryId);
         }
         return updated;
      }

      public void store() throws OseeDataStoreException {
         store(null);
      }

      public int delete() throws OseeDataStoreException {
         return delete(null);
      }

      public String toString() {
         return String.format("id: [%s] entrySize: [%d]", getQueryId(), size());
      }
   }

   public static void deleteQuery(Connection connection, JoinItem item, int queryId) throws Exception {
      if (item != null) {
         ConnectionHandler.runPreparedUpdate(connection, item.getDeleteSql(), queryId);
      }
   }

   public static void deleteQuery(JoinItem item, int queryId) throws Exception {
      if (item != null) {
         ConnectionHandler.runPreparedUpdate(item.getDeleteSql(), queryId);
      }
   }

   private interface IJoinRow {
      public Object[] toArray();

      public String toString();
   }

   public static final class TransactionJoinQuery extends JoinQueryEntry {

      private final class TempTransactionEntry implements IJoinRow {
         private int gammaId;
         private int transactionId;

         private TempTransactionEntry(int gammaId, int transactionId) {
            this.gammaId = gammaId;
            this.transactionId = transactionId;
         }

         public Object[] toArray() {
            return new Object[] {getQueryId(), getInsertTime(), gammaId, transactionId};
         }

         /* (non-Javadoc)
          * @see java.lang.Object#equals(java.lang.Object)
          */
         @Override
         public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof TempTransactionEntry)) return false;
            TempTransactionEntry other = (TempTransactionEntry) obj;
            return other.gammaId == this.gammaId && other.transactionId == this.transactionId;
         }

         /* (non-Javadoc)
          * @see java.lang.Object#hashCode()
          */
         @Override
         public int hashCode() {
            return 37 * gammaId * transactionId;
         }

         public String toString() {
            return String.format("gamma_id=%d, tx_id=%d", gammaId, transactionId);
         }
      }

      private TransactionJoinQuery() {
         super(JoinItem.TRANSACTION);
      }

      public void add(int gammaId, int transactionId) {
         entries.add(new TempTransactionEntry(gammaId, transactionId));
      }
   }

   public static final class ArtifactJoinQuery extends JoinQueryEntry {

      private final class Entry implements IJoinRow {
         private int artId;
         private int branchId;

         private Entry(int artId, int branchId) {
            this.artId = artId;
            this.branchId = branchId;
         }

         public Object[] toArray() {
            return new Object[] {getQueryId(), getInsertTime(), artId, branchId};
         }

         public String toString() {
            return String.format("art_id=%d, branch_id=%d", artId, branchId);
         }

         /* (non-Javadoc)
          * @see java.lang.Object#equals(java.lang.Object)
          */
         @Override
         public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof Entry)) return false;
            Entry other = (Entry) obj;
            return other.artId == this.artId && other.branchId == this.branchId;
         }

         /* (non-Javadoc)
          * @see java.lang.Object#hashCode()
          */
         @Override
         public int hashCode() {
            return 37 * artId * branchId;
         }
      }

      private ArtifactJoinQuery() {
         super(JoinItem.ARTIFACT);
      }

      public void add(int art_id, int branch_id) {
         entries.add(new Entry(art_id, branch_id));
      }
   }

   public static final class AttributeJoinQuery extends JoinQueryEntry {

      private final class Entry implements IJoinRow {
         private String value;

         private Entry(String value) {
            this.value = value;
         }

         public Object[] toArray() {
            return new Object[] {getQueryId(), getInsertTime(), value != null ? value : SQL3DataType.VARCHAR};
         }

         /* (non-Javadoc)
          * @see java.lang.Object#equals(java.lang.Object)
          */
         @Override
         public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof Entry)) return false;
            Entry other = (Entry) obj;
            return other.value == null && this.value == null || (other.value != null && this.value != null && this.value.equals(other.value));
         }

         /* (non-Javadoc)
          * @see java.lang.Object#hashCode()
          */
         @Override
         public int hashCode() {
            return 37 * (value != null ? value.hashCode() : -1);
         }

         public String toString() {
            return String.format("attr_value=%s", value);
         }
      }

      private AttributeJoinQuery() {
         super(JoinItem.ATTRIBUTE);
      }

      public void add(String value) {
         entries.add(new Entry(value));
      }
   }

   public static final class SearchTagJoinQuery extends JoinQueryEntry {

      private final class TagEntry implements IJoinRow {
         private long value;

         private TagEntry(long value) {
            this.value = value;
         }

         public Object[] toArray() {
            return new Object[] {getQueryId(), getInsertTime(), value};
         }

         /* (non-Javadoc)
          * @see java.lang.Object#equals(java.lang.Object)
          */
         @Override
         public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof TagEntry)) return false;
            TagEntry other = (TagEntry) obj;
            return this.value == other.value;
         }

         /* (non-Javadoc)
          * @see java.lang.Object#hashCode()
          */
         @Override
         public int hashCode() {
            return (int) (37 * value);
         }

         public String toString() {
            return String.format("tag=%d", value);
         }
      }

      private SearchTagJoinQuery() {
         super(JoinItem.SEARCH_TAGS);
      }

      public void add(long tag) {
         entries.add(new TagEntry(tag));
      }
   }

   public static final class TagQueueJoinQuery extends JoinQueryEntry {

      private final class GammaEntry implements IJoinRow {
         private long gammaId;

         private GammaEntry(long gammaId) {
            this.gammaId = gammaId;
         }

         public Object[] toArray() {
            return new Object[] {getQueryId(), getInsertTime(), gammaId};
         }

         /* (non-Javadoc)
          * @see java.lang.Object#equals(java.lang.Object)
          */
         @Override
         public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof GammaEntry)) return false;
            GammaEntry other = (GammaEntry) obj;
            return this.gammaId == other.gammaId;
         }

         /* (non-Javadoc)
          * @see java.lang.Object#hashCode()
          */
         @Override
         public int hashCode() {
            return (int) (37 * gammaId);
         }

         public String toString() {
            return String.format("gammaId=%d", gammaId);
         }
      }

      private TagQueueJoinQuery() {
         super(JoinItem.TAG_GAMMA_QUEUE);
      }

      public void add(long gammaId) {
         entries.add(new GammaEntry(gammaId));
      }
   }

   public static final class ExportImportJoinQuery extends JoinQueryEntry {

      private final class ExportImportEntry implements IJoinRow {
         private long id1;
         private long id2;

         private ExportImportEntry(long id1, long id2) {
            this.id1 = id1;
            this.id2 = id2;
         }

         public Object[] toArray() {
            return new Object[] {getQueryId(), getInsertTime(), id1, id2};
         }

         /* (non-Javadoc)
          * @see java.lang.Object#equals(java.lang.Object)
          */
         @Override
         public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof ExportImportEntry)) return false;
            ExportImportEntry other = (ExportImportEntry) obj;
            return this.id1 == other.id1 && this.id2 == other.id2;
         }

         /* (non-Javadoc)
          * @see java.lang.Object#hashCode()
          */
         @Override
         public int hashCode() {
            return (int) (37 * id1 * id2);
         }

         public String toString() {
            return String.format("id1=%d id2=%d", id1, id2);
         }
      }

      private ExportImportJoinQuery() {
         super(JoinItem.EXPORT_IMPORT);
      }

      public void add(long id1, long id2) {
         entries.add(new ExportImportEntry(id1, id2));
      }
   }
}
