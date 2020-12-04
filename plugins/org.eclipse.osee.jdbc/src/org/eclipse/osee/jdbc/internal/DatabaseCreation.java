/*********************************************************************
* Copyright (c) 2020 Boeing
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Boeing - initial API and implementation
**********************************************************************/

package org.eclipse.osee.jdbc.internal;

import java.sql.JDBCType;
import java.util.logging.Level;

import org.eclipse.osee.framework.core.enums.SqlColumn;
import org.eclipse.osee.framework.core.enums.SqlTable;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcDbType;

/**
 * @author Ryan D. Brooks
 */
public final class DatabaseCreation {

   private final JdbcClient jdbcClient;

   public DatabaseCreation(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   private void dropConstraint(SqlTable table, String constraint) {
      try {
         jdbcClient.runPreparedUpdate("ALTER TABLE " + table.getName() + " DROP CONSTRAINT " + constraint);
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.INFO, ex);
      }
   }

   public void createDataStore() {
      dropTables();

      createTable(SqlTable.ARTIFACT_TABLE);
      createTable(SqlTable.ATTRIBUTE_TABLE);
      createTable(SqlTable.RELATION_TABLE);
      createTable(SqlTable.BRANCH_TABLE);
      createTable(SqlTable.TXS_TABLE);
      createTable(SqlTable.TXS_ARCHIVED_TABLE);
      createTable(SqlTable.TX_DETAILS_TABLE);
      createTable(SqlTable.OSEE_PERMISSION_TABLE);
      createTable(SqlTable.OSEE_ARTIFACT_ACL_TABLE);
      createTable(SqlTable.OSEE_BRANCH_ACL_TABLE);
      createTable(SqlTable.OSEE_SEARCH_TAGS_TABLE);
      createTable(SqlTable.OSEE_TAG_GAMMA_QUEUE_TABLE);
      createTable(SqlTable.OSEE_SEQUENCE_TABLE);
      createTable(SqlTable.OSEE_INFO_TABLE);
      createTable(SqlTable.OSEE_MERGE_TABLE);
      createTable(SqlTable.OSEE_CONFLICT_TABLE);
      createTable(SqlTable.OSEE_JOIN_EXPORT_IMPORT_TABLE);
      createTable(SqlTable.OSEE_IMPORT_SOURCE_TABLE);
      createTable(SqlTable.OSEE_IMPORT_SAVE_POINT_TABLE);
      createTable(SqlTable.OSEE_IMPORT_MAP_TABLE);
      createTable(SqlTable.OSEE_IMPORT_INDEX_MAP_TABLE);
      createTable(SqlTable.OSEE_JOIN_ARTIFACT_TABLE);
      createTable(SqlTable.OSEE_JOIN_ID_TABLE);
      createTable(SqlTable.OSEE_JOIN_CLEANUP_TABLE);
      createTable(SqlTable.OSEE_JOIN_CHAR_ID_TABLE);
      createTable(SqlTable.OSEE_JOIN_TRANSACTION_TABLE);
      createTable(SqlTable.OSEE_BRANCH_GROUP_TABLE);
      createTable(SqlTable.LDAP_DETAILS_TABLE);
      createTable(SqlTable.TUPLE2);
      createTable(SqlTable.TUPLE3);
      createTable(SqlTable.TUPLE4);
      createTable(SqlTable.OSEE_KEY_VALUE_TABLE);
      createTable(SqlTable.OSEE_JOIN_ID4_TABLE);
      createTable(SqlTable.OSEE_SERVER_LOOKUP_TABLE);
      createTable(SqlTable.OSEE_SESSION_TABLE);
      createTable(SqlTable.OSEE_ACTIVITY_TYPE_TABLE);
      createTable(SqlTable.OSEE_ACTIVITY_TABLE);
      createTable(SqlTable.OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE);
      createTable(SqlTable.OSEE_OAUTH_AUTHORIZATION_TABLE);
      createTable(SqlTable.OSEE_OAUTH_TOKEN_TABLE);
   }

   private void dropTable(SqlTable table) {
      try {
         jdbcClient.runPreparedUpdate("DROP TABLE " + table.getName());
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.INFO, ex);
      }
   }

   private void dropTables() {
      dropConstraint(SqlTable.TX_DETAILS_TABLE, "BRANCH_ID_FK1");
      dropConstraint(SqlTable.OSEE_ARTIFACT_ACL_TABLE, "ARTIFACT_ACL_PERM_FK");
      dropConstraint(SqlTable.OSEE_BRANCH_ACL_TABLE, "BRANCH_ACL_PERM_FK");
      dropConstraint(SqlTable.OSEE_PERMISSION_TABLE, SqlTable.OSEE_PERMISSION_TABLE.getName() + "_PK");
      dropConstraint(SqlTable.OSEE_MERGE_TABLE, "OSEE_MERGE__MBI_FK");
      dropConstraint(SqlTable.OSEE_MERGE_TABLE, "OSEE_MERGE__DBI_FK");
      dropConstraint(SqlTable.OSEE_BRANCH_ACL_TABLE, "BRANCH_ACL_FK");
      dropConstraint(SqlTable.BRANCH_TABLE, SqlTable.BRANCH_TABLE.getName() + "_PK");
      dropConstraint(SqlTable.TUPLE2, SqlTable.TUPLE2.getName() + "_PK");
      dropConstraint(SqlTable.OSEE_IMPORT_SAVE_POINT_TABLE, "OSEE_IMP_SAVE_POINT_II_FK");
      dropConstraint(SqlTable.OSEE_IMPORT_MAP_TABLE, "OSEE_IMPORT_MAP_II_FK");
      dropConstraint(SqlTable.OSEE_IMPORT_INDEX_MAP_TABLE, "OSEE_IMPORT_INDEX_MAP_II_FK");
      dropConstraint(SqlTable.OSEE_OAUTH_AUTHORIZATION_TABLE, "OSEE_OAUTH_AUTH__CI_FK");
      dropConstraint(SqlTable.OSEE_OAUTH_TOKEN_TABLE, "OSEE_OAUTH_TOKEN__CI_FK");

      dropTable(SqlTable.OSEE_ARTIFACT_ACL_TABLE);
      dropTable(SqlTable.OSEE_BRANCH_ACL_TABLE);
      dropTable(SqlTable.OSEE_CONFLICT_TABLE);
      dropTable(SqlTable.OSEE_MERGE_TABLE);
      dropTable(SqlTable.ARTIFACT_TABLE);
      dropTable(SqlTable.ATTRIBUTE_TABLE);
      dropTable(SqlTable.RELATION_TABLE);
      dropTable(SqlTable.BRANCH_TABLE);
      dropTable(SqlTable.TXS_TABLE);
      dropTable(SqlTable.TXS_ARCHIVED_TABLE);
      dropTable(SqlTable.TX_DETAILS_TABLE);
      dropTable(SqlTable.OSEE_PERMISSION_TABLE);
      dropTable(SqlTable.OSEE_SEARCH_TAGS_TABLE);
      dropTable(SqlTable.OSEE_TAG_GAMMA_QUEUE_TABLE);
      dropTable(SqlTable.OSEE_SEQUENCE_TABLE);
      dropTable(SqlTable.OSEE_INFO_TABLE);
      dropTable(SqlTable.OSEE_JOIN_EXPORT_IMPORT_TABLE);
      dropTable(SqlTable.OSEE_IMPORT_SOURCE_TABLE);
      dropTable(SqlTable.OSEE_IMPORT_SAVE_POINT_TABLE);
      dropTable(SqlTable.OSEE_IMPORT_MAP_TABLE);
      dropTable(SqlTable.OSEE_IMPORT_INDEX_MAP_TABLE);
      dropTable(SqlTable.OSEE_JOIN_ARTIFACT_TABLE);
      dropTable(SqlTable.OSEE_JOIN_ID_TABLE);
      dropTable(SqlTable.OSEE_JOIN_CLEANUP_TABLE);
      dropTable(SqlTable.OSEE_JOIN_CHAR_ID_TABLE);
      dropTable(SqlTable.OSEE_JOIN_TRANSACTION_TABLE);
      dropTable(SqlTable.OSEE_BRANCH_GROUP_TABLE);
      dropTable(SqlTable.LDAP_DETAILS_TABLE);
      dropTable(SqlTable.TUPLE2);
      dropTable(SqlTable.TUPLE3);
      dropTable(SqlTable.TUPLE4);
      dropTable(SqlTable.OSEE_KEY_VALUE_TABLE);
      dropTable(SqlTable.OSEE_JOIN_ID4_TABLE);
      dropTable(SqlTable.OSEE_SERVER_LOOKUP_TABLE);
      dropTable(SqlTable.OSEE_SESSION_TABLE);
      dropTable(SqlTable.OSEE_ACTIVITY_TYPE_TABLE);
      dropTable(SqlTable.OSEE_ACTIVITY_TABLE);
      dropTable(SqlTable.OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE);
      dropTable(SqlTable.OSEE_OAUTH_AUTHORIZATION_TABLE);
      dropTable(SqlTable.OSEE_OAUTH_TOKEN_TABLE);
   }

   public String columnToSql(SqlColumn column) {
      StringBuilder strB = new StringBuilder(50);
      strB.append(column.getName());
      strB.append(" ");
      if (column.getType() == JDBCType.INTEGER) {
         strB.append("INT");
      } else if (column.getType() == JDBCType.BIGINT) {
         if (jdbcClient.getDbType().equals(JdbcDbType.oracle)) {
            strB.append("NUMBER (19, 0)");
         } else {
            strB.append("BIGINT");
         }
      }

      else if (jdbcClient.getDbType().equals(JdbcDbType.postgresql)) {
         if (column.getType() == JDBCType.BLOB) {
            strB.append("bytea");
         } else if (column.getType() == JDBCType.CLOB) {
            strB.append("text");
         } else {
            strB.append(column.getType());
         }
      } else {
         strB.append(column.getType());
      }
      if (column.getLength() > 0) {
         strB.append(" (");
         strB.append(column.getLength());
         strB.append(")");
      }
      if (column.getName().equals("BUILD_ID")) {
         strB.append(" DEFAULT 0");
      } else if (!column.isNull()) {
         strB.append(" NOT NULL");
      }
      return strB.toString();
   }

   public void createTable(SqlTable table) {
      StringBuilder sql = new StringBuilder(200);
      sql.append("CREATE TABLE ");
      sql.append(table.getName());
      sql.append(" (\n\t");
      for (int i = 0; i < table.getColumns().size(); i++) {
         sql.append(columnToSql(table.getColumns().get(i)));

         if (i != table.getColumns().size() - 1 || !table.getConstraints().isEmpty()) {
            sql.append(",\n\t");
         }
      }
      sql.append(Collections.toString(",\n\t", table.getConstraints()));
      sql.append("\n)");
      if (jdbcClient.getDbType().equals(JdbcDbType.oracle)) {
         if (table.getIndexLevel() != -1) {
            sql.append("\tORGANIZATION INDEX ");
            if (table.getIndexLevel() > 0) {
               sql.append("COMPRESS " + table.getIndexLevel());
            }
         }
         if (table.getTableExtras() != null) {
            sql.append("\n\t" + table.getTableExtras());
         }
      }
      sql.append("\n\n");
      for (int i = 0; i < table.getStatements().size(); i++) {
         if (table.getStatements().get(i).contains("BASELINE_TX_ID_FK1")) {
            if (jdbcClient.getDbType().equals(JdbcDbType.oracle) || jdbcClient.getDbType().equals(
               JdbcDbType.postgresql)) {
               table.getStatements().set(i, table.getStatements().get(i) + "DEFERRABLE INITIALLY DEFERRED");
            }
         }
      }

      sql.append("\n");
      jdbcClient.runPreparedUpdate(sql.toString());
      for (String statement : table.getStatements()) {
         if (statement.contains("CREATE INDEX")) {
            if (jdbcClient.getDbType().equals(JdbcDbType.oracle)) {
               statement += " TABLESPACE osee_index";
            }
         }
         jdbcClient.runPreparedUpdate(statement);
      }

   }
}
