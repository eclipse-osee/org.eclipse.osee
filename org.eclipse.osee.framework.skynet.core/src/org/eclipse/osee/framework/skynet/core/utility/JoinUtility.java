/*
 * Created on Jun 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.utility;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;

/**
 * @author Roberto E. Escobar
 */
public class JoinUtility {

   private static final String INSERT_INTO_JOIN_ARTIFACT =
         "INSERT INTO osee_join_artifact (query_id, insert_time, art_id, branch_id) VALUES (?, ?, ?, ?)";

   public static final String INSERT_INTO_JOIN_ATTRIBUTE =
         "INSERT INTO osee_join_attribute (attr_query_id, insert_time, value) VALUES (?, ?, ?)";

   public static final String INSERT_INTO_JOIN_TRANSACTION =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) VALUES (?, ?, ?, ?)";

   private static final String DELETE_FROM_JOIN_TRANSACTION = "DELETE FROM osee_join_transaction WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_ARTIFACT = "DELETE FROM osee_join_artifact WHERE query_id = ?";
   private static final String DELETE_FROM_JOIN_ATTRIBUTE = "DELETE FROM osee_join_attribute WHERE attr_query_id = ?";

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

   private static abstract class JoinQueryEntry {
      private String deleteSql;
      private String insertSql;
      private int queryId;
      protected List<IJoinRow> entries;

      public JoinQueryEntry(String insertSql, String deleteSql) {
         this.deleteSql = deleteSql;
         this.insertSql = insertSql;
         this.queryId = getNewQueryId();
         this.entries = new ArrayList<IJoinRow>();
      }

      public boolean isEmpty() {
         return entries.isEmpty();
      }

      public int size() {
         return entries.size();
      }

      public int getQueryId() {
         return queryId;
      }

      public void store(Connection connection) throws SQLException {
         List<Object[]> data = new ArrayList<Object[]>();
         for (IJoinRow joinArray : entries) {
            data.add(joinArray.toArray());
         }
         ConnectionHandler.runPreparedUpdate(connection, insertSql, data);
      }

      public int delete(Connection connection) throws SQLException {
         int updated = 0;
         if (queryId != -1) {
            updated = ConnectionHandler.runPreparedUpdate(connection, deleteSql, SQL3DataType.INTEGER, queryId);
         }
         entries.clear();
         return updated;
      }

      public void store() throws SQLException {
         store(ConnectionHandler.getConnection());
      }

      public int delete() throws SQLException {
         return delete(ConnectionHandler.getConnection());
      }
   }

   private interface IJoinRow {
      public Object[] toArray();
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
            Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
            return new Object[] {SQL3DataType.INTEGER, getQueryId(), SQL3DataType.TIMESTAMP, insertTime,
                  SQL3DataType.INTEGER, gammaId, SQL3DataType.INTEGER, transactionId};
         }
      }

      private TransactionJoinQuery() {
         super(INSERT_INTO_JOIN_TRANSACTION, DELETE_FROM_JOIN_TRANSACTION);
      }

      public void add(int gammaId, int transactionId) {
         entries.add(new TempTransactionEntry(gammaId, transactionId));
      }
   }

   public static final class ArtifactJoinQuery extends JoinQueryEntry {

      private final class Entry implements IJoinRow {
         private int art_id;
         private int branch_id;

         private Entry(int art_id, int branch_id) {
            this.art_id = art_id;
            this.branch_id = branch_id;
         }

         public Object[] toArray() {
            Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
            return new Object[] {SQL3DataType.INTEGER, getQueryId(), SQL3DataType.TIMESTAMP, insertTime,
                  SQL3DataType.INTEGER, art_id, SQL3DataType.INTEGER, branch_id};
         }
      }

      private ArtifactJoinQuery() {
         super(INSERT_INTO_JOIN_ARTIFACT, DELETE_FROM_JOIN_ARTIFACT);
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
            Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
            return new Object[] {SQL3DataType.INTEGER, getQueryId(), SQL3DataType.TIMESTAMP, insertTime,
                  SQL3DataType.VARCHAR, value};
         }
      }

      private AttributeJoinQuery() {
         super(INSERT_INTO_JOIN_ATTRIBUTE, DELETE_FROM_JOIN_ATTRIBUTE);
      }

      public void add(String value) {
         entries.add(new Entry(value));
      }
   }

}
