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

package org.eclipse.osee.orcs.db.internal;

import static org.eclipse.osee.framework.core.enums.SqlTable.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.ATTRIBUTE_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.BRANCH_BASELINE_TRANSACTION_ID;
import static org.eclipse.osee.framework.core.enums.SqlTable.BRANCH_PARENT_TRANSACTION_ID;
import static org.eclipse.osee.framework.core.enums.SqlTable.BRANCH_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.LDAP_DETAILS_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_ACTIVITY_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_ACTIVITY_TYPE_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_ARTIFACT_ACL_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_BRANCH_ACL_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_BRANCH_GROUP_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_CONFLICT_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_IMPORT_INDEX_MAP_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_IMPORT_MAP_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_IMPORT_SAVE_POINT_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_IMPORT_SOURCE_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_INFO_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_JOIN_ARTIFACT_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_JOIN_CHAR_ID_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_JOIN_CLEANUP_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_JOIN_EXPORT_IMPORT_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_JOIN_ID4_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_JOIN_ID_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_JOIN_TRANSACTION_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_KEY_VALUE_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_MERGE_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_OAUTH_AUTHORIZATION_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_OAUTH_TOKEN_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_PERMISSION_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_SEARCH_TAGS_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_SEQUENCE_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_SERVER_LOOKUP_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_SESSION_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.OSEE_TAG_GAMMA_QUEUE_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.RELATION_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.TUPLE2;
import static org.eclipse.osee.framework.core.enums.SqlTable.TUPLE3;
import static org.eclipse.osee.framework.core.enums.SqlTable.TUPLE4;
import static org.eclipse.osee.framework.core.enums.SqlTable.TXS_ARCHIVED_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.TXS_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.TX_DETAILS_TABLE;
import static org.eclipse.osee.framework.core.enums.SqlTable.TX_DETAILS_TRANSACTION_ID;
import java.sql.JDBCType;
import org.eclipse.osee.framework.core.enums.SqlColumn;
import org.eclipse.osee.framework.core.enums.SqlTable;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.logger.Log;

/**
 * @author Ryan D. Brooks
 */
public final class DatabaseCreation {

   private final JdbcClient jdbcClient;
   private final Log logger;

   public DatabaseCreation(JdbcClient jdbcClient, Log logger) {
      this.jdbcClient = jdbcClient;
      this.logger = logger;
   }

   private void dropConstraint(SqlTable table, String constraint) {
      try {
         jdbcClient.runPreparedUpdate("ALTER TABLE " + table.getName() + " DROP CONSTRAINT " + constraint);
      } catch (Exception ex) {
         logger.info(ex, "dropConstraint failed");
      }
   }

   public void createDataStore() {
      dropTables();

      createTable(ARTIFACT_TABLE);
      createTable(ATTRIBUTE_TABLE);
      createTable(RELATION_TABLE);
      createTable(BRANCH_TABLE);
      createTable(TXS_TABLE);
      createTable(TXS_ARCHIVED_TABLE);
      createTable(TX_DETAILS_TABLE);

      alterForeignKeyConstraint("PARENT_TX_ID_FK1", BRANCH_TABLE, BRANCH_PARENT_TRANSACTION_ID, TX_DETAILS_TABLE,
         TX_DETAILS_TRANSACTION_ID, "");
      deferredForeignKeyConstraint("BASELINE_TX_ID_FK1", BRANCH_TABLE, BRANCH_BASELINE_TRANSACTION_ID, TX_DETAILS_TABLE,
         TX_DETAILS_TRANSACTION_ID);

      createTable(OSEE_PERMISSION_TABLE);
      createTable(OSEE_ARTIFACT_ACL_TABLE);
      createTable(OSEE_BRANCH_ACL_TABLE);
      createTable(OSEE_SEARCH_TAGS_TABLE);
      createTable(OSEE_TAG_GAMMA_QUEUE_TABLE);
      createTable(OSEE_SEQUENCE_TABLE);
      createTable(OSEE_INFO_TABLE);
      createTable(OSEE_MERGE_TABLE);
      createTable(OSEE_CONFLICT_TABLE);
      createTable(OSEE_JOIN_EXPORT_IMPORT_TABLE);
      createTable(OSEE_IMPORT_SOURCE_TABLE);
      createTable(OSEE_IMPORT_SAVE_POINT_TABLE);
      createTable(OSEE_IMPORT_MAP_TABLE);
      createTable(OSEE_IMPORT_INDEX_MAP_TABLE);
      createTable(OSEE_JOIN_ARTIFACT_TABLE);
      createTable(OSEE_JOIN_ID_TABLE);
      createTable(OSEE_JOIN_CLEANUP_TABLE);
      createTable(OSEE_JOIN_CHAR_ID_TABLE);
      createTable(OSEE_JOIN_TRANSACTION_TABLE);
      createTable(OSEE_BRANCH_GROUP_TABLE);
      createTable(LDAP_DETAILS_TABLE);
      createTable(TUPLE2);
      createTable(TUPLE3);
      createTable(TUPLE4);
      createTable(OSEE_KEY_VALUE_TABLE);
      createTable(OSEE_JOIN_ID4_TABLE);
      createTable(OSEE_SERVER_LOOKUP_TABLE);
      createTable(OSEE_SESSION_TABLE);
      createTable(OSEE_ACTIVITY_TYPE_TABLE);
      createTable(OSEE_ACTIVITY_TABLE);
      createTable(OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE);
      createTable(OSEE_OAUTH_AUTHORIZATION_TABLE);
      createTable(OSEE_OAUTH_TOKEN_TABLE);
   }

   private void dropTable(SqlTable table) {
      try {
         jdbcClient.runPreparedUpdate("DROP TABLE " + table.getName());
      } catch (Exception ex) {
         logger.info(ex, "dropTable failed");
      }
   }

   private void dropTables() {
      dropConstraint(TX_DETAILS_TABLE, "BRANCH_ID_FK1");
      dropConstraint(OSEE_ARTIFACT_ACL_TABLE, "ARTIFACT_ACL_PERM_FK");
      dropConstraint(OSEE_BRANCH_ACL_TABLE, "BRANCH_ACL_PERM_FK");
      dropConstraint(OSEE_PERMISSION_TABLE, OSEE_PERMISSION_TABLE.getName() + "_PK");
      dropConstraint(OSEE_MERGE_TABLE, "OSEE_MERGE__MBI_FK");
      dropConstraint(OSEE_MERGE_TABLE, "OSEE_MERGE__DBI_FK");
      dropConstraint(OSEE_BRANCH_ACL_TABLE, "BRANCH_ACL_FK");
      dropConstraint(BRANCH_TABLE, BRANCH_TABLE.getName() + "_PK");
      dropConstraint(TUPLE2, TUPLE2.getName() + "_PK");
      dropConstraint(OSEE_IMPORT_SAVE_POINT_TABLE, "OSEE_IMP_SAVE_POINT_II_FK");
      dropConstraint(OSEE_IMPORT_MAP_TABLE, "OSEE_IMPORT_MAP_II_FK");
      dropConstraint(OSEE_IMPORT_INDEX_MAP_TABLE, "OSEE_IMPORT_INDEX_MAP_II_FK");
      dropConstraint(OSEE_OAUTH_AUTHORIZATION_TABLE, "OSEE_OAUTH_AUTH__CI_FK");
      dropConstraint(OSEE_OAUTH_TOKEN_TABLE, "OSEE_OAUTH_TOKEN__CI_FK");

      dropTable(OSEE_ARTIFACT_ACL_TABLE);
      dropTable(OSEE_BRANCH_ACL_TABLE);
      dropTable(OSEE_CONFLICT_TABLE);
      dropTable(OSEE_MERGE_TABLE);
      dropTable(ARTIFACT_TABLE);
      dropTable(ATTRIBUTE_TABLE);
      dropTable(RELATION_TABLE);
      dropTable(BRANCH_TABLE);
      dropTable(TXS_TABLE);
      dropTable(TXS_ARCHIVED_TABLE);
      dropTable(TX_DETAILS_TABLE);
      dropTable(OSEE_PERMISSION_TABLE);
      dropTable(OSEE_SEARCH_TAGS_TABLE);
      dropTable(OSEE_TAG_GAMMA_QUEUE_TABLE);
      dropTable(OSEE_SEQUENCE_TABLE);
      dropTable(OSEE_INFO_TABLE);
      dropTable(OSEE_JOIN_EXPORT_IMPORT_TABLE);
      dropTable(OSEE_IMPORT_SOURCE_TABLE);
      dropTable(OSEE_IMPORT_SAVE_POINT_TABLE);
      dropTable(OSEE_IMPORT_MAP_TABLE);
      dropTable(OSEE_IMPORT_INDEX_MAP_TABLE);
      dropTable(OSEE_JOIN_ARTIFACT_TABLE);
      dropTable(OSEE_JOIN_ID_TABLE);
      dropTable(OSEE_JOIN_CLEANUP_TABLE);
      dropTable(OSEE_JOIN_CHAR_ID_TABLE);
      dropTable(OSEE_JOIN_TRANSACTION_TABLE);
      dropTable(OSEE_BRANCH_GROUP_TABLE);
      dropTable(LDAP_DETAILS_TABLE);
      dropTable(TUPLE2);
      dropTable(TUPLE3);
      dropTable(TUPLE4);
      dropTable(OSEE_KEY_VALUE_TABLE);
      dropTable(OSEE_JOIN_ID4_TABLE);
      dropTable(OSEE_SERVER_LOOKUP_TABLE);
      dropTable(OSEE_SESSION_TABLE);
      dropTable(OSEE_ACTIVITY_TYPE_TABLE);
      dropTable(OSEE_ACTIVITY_TABLE);
      dropTable(OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE);
      dropTable(OSEE_OAUTH_AUTHORIZATION_TABLE);
      dropTable(OSEE_OAUTH_TOKEN_TABLE);
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

      JdbcDbType dbType = jdbcClient.getDbType();
      if (dbType.equals(JdbcDbType.oracle)) {
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
      jdbcClient.runPreparedUpdate(sql.toString());

      for (String statement : table.getStatements()) {
         if (statement.contains("CREATE INDEX") && dbType.equals(JdbcDbType.oracle)) {
            statement += " TABLESPACE osee_index";
         }
         jdbcClient.runPreparedUpdate(statement);
      }
   }

   private void deferredForeignKeyConstraint(String constraintName, SqlTable table, SqlColumn column, SqlTable refTable, SqlColumn refColumn) {
      String defered = jdbcClient.getDbType().matches(JdbcDbType.oracle,
         JdbcDbType.postgresql) ? " DEFERRABLE INITIALLY DEFERRED" : "";
      alterForeignKeyConstraint(constraintName, table, column, refTable, refColumn, defered);
   }

   private void alterForeignKeyConstraint(String constraintName, SqlTable table, SqlColumn column, SqlTable refTable, SqlColumn refColumn, String defered) {
      String statement = String.format("ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY(%s) REFERENCES %s(%s)%s", table,
         constraintName, column, refTable, refColumn, defered);
      jdbcClient.runPreparedUpdate(statement);
   }
}