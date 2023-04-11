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

import static org.eclipse.osee.orcs.OseeDb.ARTIFACT_TABLE;
import static org.eclipse.osee.orcs.OseeDb.ATTRIBUTE_TABLE;
import static org.eclipse.osee.orcs.OseeDb.BRANCH_BASELINE_TRANSACTION_ID;
import static org.eclipse.osee.orcs.OseeDb.BRANCH_CATEGORY;
import static org.eclipse.osee.orcs.OseeDb.BRANCH_PARENT_TRANSACTION_ID;
import static org.eclipse.osee.orcs.OseeDb.BRANCH_TABLE;
import static org.eclipse.osee.orcs.OseeDb.LDAP_DETAILS_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_ACTIVITY_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_ACTIVITY_TYPE_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_ARTIFACT_ACL_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_BRANCH_ACL_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_CONFLICT_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_IMPORT_INDEX_MAP_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_IMPORT_MAP_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_IMPORT_SAVE_POINT_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_IMPORT_SOURCE_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_INFO_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_JOIN_ARTIFACT_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_JOIN_CHAR_ID_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_JOIN_CLEANUP_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_JOIN_EXPORT_IMPORT_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_JOIN_ID4_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_JOIN_ID_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_JOIN_TRANSACTION_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_KEY_VALUE_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_MERGE_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_OAUTH_AUTHORIZATION_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_OAUTH_TOKEN_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_PERMISSION_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_SEARCH_TAGS_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_SEQUENCE_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_SERVER_LOOKUP_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_SESSION_TABLE;
import static org.eclipse.osee.orcs.OseeDb.OSEE_TAG_GAMMA_QUEUE_TABLE;
import static org.eclipse.osee.orcs.OseeDb.RELATION_TABLE;
import static org.eclipse.osee.orcs.OseeDb.RELATION_TABLE2;
import static org.eclipse.osee.orcs.OseeDb.TUPLE2;
import static org.eclipse.osee.orcs.OseeDb.TUPLE3;
import static org.eclipse.osee.orcs.OseeDb.TUPLE4;
import static org.eclipse.osee.orcs.OseeDb.TXS_ARCHIVED_TABLE;
import static org.eclipse.osee.orcs.OseeDb.TXS_TABLE;
import static org.eclipse.osee.orcs.OseeDb.TX_DETAILS_TABLE;
import static org.eclipse.osee.orcs.OseeDb.TX_DETAILS_TRANSACTION_ID;
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

   public void createDataStore() {
      dropTables();
      if (jdbcClient.getDbType().equals(JdbcDbType.hsql)) {
         jdbcClient.runPreparedUpdate("SET DATABASE SQL SYNTAX ORA TRUE;");
      }
      jdbcClient.createTable(ARTIFACT_TABLE);
      jdbcClient.createTable(ATTRIBUTE_TABLE);
      jdbcClient.createTable(RELATION_TABLE);
      jdbcClient.createTable(RELATION_TABLE2);
      jdbcClient.createTable(BRANCH_TABLE);
      jdbcClient.createTable(TXS_TABLE);
      jdbcClient.createTable(TXS_ARCHIVED_TABLE);
      jdbcClient.createTable(TX_DETAILS_TABLE);

      jdbcClient.alterForeignKeyConstraint("PARENT_TX_ID_FK1", BRANCH_TABLE, BRANCH_PARENT_TRANSACTION_ID,
         TX_DETAILS_TABLE, TX_DETAILS_TRANSACTION_ID, "");
      jdbcClient.deferredForeignKeyConstraint("BASELINE_TX_ID_FK1", BRANCH_TABLE, BRANCH_BASELINE_TRANSACTION_ID,
         TX_DETAILS_TABLE, TX_DETAILS_TRANSACTION_ID);

      jdbcClient.createTable(OSEE_PERMISSION_TABLE);
      jdbcClient.createTable(OSEE_ARTIFACT_ACL_TABLE);
      jdbcClient.createTable(OSEE_BRANCH_ACL_TABLE);
      jdbcClient.createTable(OSEE_SEARCH_TAGS_TABLE);
      jdbcClient.createTable(OSEE_TAG_GAMMA_QUEUE_TABLE);
      jdbcClient.createTable(OSEE_SEQUENCE_TABLE);
      jdbcClient.createTable(OSEE_INFO_TABLE);
      jdbcClient.createTable(OSEE_MERGE_TABLE);
      jdbcClient.createTable(OSEE_CONFLICT_TABLE);
      jdbcClient.createTable(OSEE_JOIN_EXPORT_IMPORT_TABLE);
      jdbcClient.createTable(OSEE_IMPORT_SOURCE_TABLE);
      jdbcClient.createTable(OSEE_IMPORT_SAVE_POINT_TABLE);
      jdbcClient.createTable(OSEE_IMPORT_MAP_TABLE);
      jdbcClient.createTable(OSEE_IMPORT_INDEX_MAP_TABLE);
      jdbcClient.createTable(OSEE_JOIN_ARTIFACT_TABLE);
      jdbcClient.createTable(OSEE_JOIN_ID_TABLE);
      jdbcClient.createTable(OSEE_JOIN_CLEANUP_TABLE);
      jdbcClient.createTable(OSEE_JOIN_CHAR_ID_TABLE);
      jdbcClient.createTable(OSEE_JOIN_TRANSACTION_TABLE);
      jdbcClient.createTable(LDAP_DETAILS_TABLE);
      jdbcClient.createTable(TUPLE2);
      jdbcClient.createTable(TUPLE3);
      jdbcClient.createTable(TUPLE4);
      jdbcClient.createTable(BRANCH_CATEGORY);
      jdbcClient.createTable(OSEE_KEY_VALUE_TABLE);
      jdbcClient.createTable(OSEE_JOIN_ID4_TABLE);
      jdbcClient.createTable(OSEE_SERVER_LOOKUP_TABLE);
      jdbcClient.createTable(OSEE_SESSION_TABLE);
      jdbcClient.createTable(OSEE_ACTIVITY_TYPE_TABLE);
      jdbcClient.createTable(OSEE_ACTIVITY_TABLE);
      jdbcClient.createTable(OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE);
      jdbcClient.createTable(OSEE_OAUTH_AUTHORIZATION_TABLE);
      jdbcClient.createTable(OSEE_OAUTH_TOKEN_TABLE);
   }

   private void dropTables() {
      jdbcClient.dropConstraint(TX_DETAILS_TABLE, "BRANCH_ID_FK1");
      jdbcClient.dropConstraint(OSEE_ARTIFACT_ACL_TABLE, "ARTIFACT_ACL_PERM_FK");
      jdbcClient.dropConstraint(OSEE_BRANCH_ACL_TABLE, "BRANCH_ACL_PERM_FK");
      jdbcClient.dropConstraint(OSEE_PERMISSION_TABLE, OSEE_PERMISSION_TABLE.getName() + "_PK");
      jdbcClient.dropConstraint(OSEE_MERGE_TABLE, "OSEE_MERGE__MBI_FK");
      jdbcClient.dropConstraint(OSEE_MERGE_TABLE, "OSEE_MERGE__DBI_FK");
      jdbcClient.dropConstraint(OSEE_BRANCH_ACL_TABLE, "BRANCH_ACL_FK");
      jdbcClient.dropConstraint(BRANCH_TABLE, BRANCH_TABLE.getName() + "_PK");
      jdbcClient.dropConstraint(TUPLE2, TUPLE2.getName() + "_PK");
      jdbcClient.dropConstraint(OSEE_IMPORT_SAVE_POINT_TABLE, "OSEE_IMP_SAVE_POINT_II_FK");
      jdbcClient.dropConstraint(OSEE_IMPORT_MAP_TABLE, "OSEE_IMPORT_MAP_II_FK");
      jdbcClient.dropConstraint(OSEE_IMPORT_INDEX_MAP_TABLE, "OSEE_IMPORT_INDEX_MAP_II_FK");
      jdbcClient.dropConstraint(OSEE_OAUTH_AUTHORIZATION_TABLE, "OSEE_OAUTH_AUTH__CI_FK");
      jdbcClient.dropConstraint(OSEE_OAUTH_TOKEN_TABLE, "OSEE_OAUTH_TOKEN__CI_FK");

      jdbcClient.dropTable(OSEE_ARTIFACT_ACL_TABLE);
      jdbcClient.dropTable(OSEE_BRANCH_ACL_TABLE);
      jdbcClient.dropTable(OSEE_CONFLICT_TABLE);
      jdbcClient.dropTable(OSEE_MERGE_TABLE);
      jdbcClient.dropTable(ARTIFACT_TABLE);
      jdbcClient.dropTable(ATTRIBUTE_TABLE);
      jdbcClient.dropTable(RELATION_TABLE);
      jdbcClient.dropTable(RELATION_TABLE2);
      jdbcClient.dropTable(BRANCH_TABLE);
      jdbcClient.dropTable(TXS_TABLE);
      jdbcClient.dropTable(TXS_ARCHIVED_TABLE);
      jdbcClient.dropTable(TX_DETAILS_TABLE);
      jdbcClient.dropTable(OSEE_PERMISSION_TABLE);
      jdbcClient.dropTable(OSEE_SEARCH_TAGS_TABLE);
      jdbcClient.dropTable(OSEE_TAG_GAMMA_QUEUE_TABLE);
      jdbcClient.dropTable(OSEE_SEQUENCE_TABLE);
      jdbcClient.dropTable(OSEE_INFO_TABLE);
      jdbcClient.dropTable(OSEE_JOIN_EXPORT_IMPORT_TABLE);
      jdbcClient.dropTable(OSEE_IMPORT_SOURCE_TABLE);
      jdbcClient.dropTable(OSEE_IMPORT_SAVE_POINT_TABLE);
      jdbcClient.dropTable(OSEE_IMPORT_MAP_TABLE);
      jdbcClient.dropTable(OSEE_IMPORT_INDEX_MAP_TABLE);
      jdbcClient.dropTable(OSEE_JOIN_ARTIFACT_TABLE);
      jdbcClient.dropTable(OSEE_JOIN_ID_TABLE);
      jdbcClient.dropTable(OSEE_JOIN_CLEANUP_TABLE);
      jdbcClient.dropTable(OSEE_JOIN_CHAR_ID_TABLE);
      jdbcClient.dropTable(OSEE_JOIN_TRANSACTION_TABLE);
      jdbcClient.dropTable(LDAP_DETAILS_TABLE);
      jdbcClient.dropTable(TUPLE2);
      jdbcClient.dropTable(TUPLE3);
      jdbcClient.dropTable(TUPLE4);
      jdbcClient.dropTable(BRANCH_CATEGORY);
      jdbcClient.dropTable(OSEE_KEY_VALUE_TABLE);
      jdbcClient.dropTable(OSEE_JOIN_ID4_TABLE);
      jdbcClient.dropTable(OSEE_SERVER_LOOKUP_TABLE);
      jdbcClient.dropTable(OSEE_SESSION_TABLE);
      jdbcClient.dropTable(OSEE_ACTIVITY_TYPE_TABLE);
      jdbcClient.dropTable(OSEE_ACTIVITY_TABLE);
      jdbcClient.dropTable(OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE);
      jdbcClient.dropTable(OSEE_OAUTH_AUTHORIZATION_TABLE);
      jdbcClient.dropTable(OSEE_OAUTH_TOKEN_TABLE);
   }
}