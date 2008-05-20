/*
 * Created on May 20, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.artifact.servlet;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactUtil {

   private static final int WORD_FORMATTED_CONTENT = 19;

   private static String URI_BY_GUID_AND_BRANCH_ID =
         "SELECT uri FROM OSEE_DEFINE_TX_DETAILS txd1, OSEE_DEFINE_TXS txs1, OSEE_DEFINE_ARTIFACT art1, OSEE_DEFINE_ATTRIBUTE attr1 WHERE art1.guid = ? AND attr1.attr_type_id = ? AND txd1.branch_id = ? AND txs1.gamma_id = attr1.gamma_id AND attr1.art_id = art1.art_id AND txs1.transaction_id = txd1.transaction_id AND txs1.tx_current = 1";

   private static String URI_BY_GUID_AND_BRANCH_NAME =
         "SELECT uri FROM OSEE_DEFINE_TX_DETAILS txd1, OSEE_DEFINE_TXS txs1, OSEE_DEFINE_ARTIFACT art1, OSEE_DEFINE_ATTRIBUTE attr1, OSEE_DEFINE_BRANCH branch1 WHERE art1.guid = ? AND attr1.attr_type_id = ? AND txs1.gamma_id = attr1.gamma_id AND attr1.art_id = art1.art_id AND txs1.transaction_id = txd1.transaction_id AND txs1.tx_current = 1 AND txd1.branch_id = branch1.branch_id AND branch1.branch_name = ?";

   public static String getUri(String guid, int branchId) throws SQLException {
      return getUri(URI_BY_GUID_AND_BRANCH_ID, SQL3DataType.VARCHAR, guid, SQL3DataType.INTEGER,
            WORD_FORMATTED_CONTENT, SQL3DataType.INTEGER, branchId);
   }

   public static String getUri(String guid, String branchName) throws SQLException {
      return getUri(URI_BY_GUID_AND_BRANCH_NAME, SQL3DataType.VARCHAR, guid, SQL3DataType.INTEGER,
            WORD_FORMATTED_CONTENT, SQL3DataType.VARCHAR, branchName);
   }

   private static String getUri(String query, Object... dataBindings) throws SQLException {
      String uriValue = null;
      Connection connection = null;
      ConnectionHandlerStatement handlerStatement = null;
      try {
         connection = OseeDbConnection.getConnection();
         handlerStatement = ConnectionHandler.runPreparedQuery(connection, query, dataBindings);
         handlerStatement.next();
         uriValue = handlerStatement.getRset().getString("uri");
      } finally {
         if (handlerStatement != null) {
            handlerStatement.close();
         }
         if (connection != null) {
            connection.close();
         }
      }
      return uriValue;
   }
}
