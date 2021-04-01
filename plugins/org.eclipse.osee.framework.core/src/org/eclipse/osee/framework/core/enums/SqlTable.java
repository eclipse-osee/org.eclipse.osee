/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.jdk.core.type.ChainingArrayList;
import org.eclipse.osee.framework.jdk.core.type.NamedBase;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Roberto E. Escobar
 */
public class SqlTable extends NamedBase {

   public static final SqlTable ARTIFACT_TABLE = new SqlTable("osee_artifact", "art", ObjectType.ARTIFACT);
   public static final SqlColumn ARTIFACT_GUID = ARTIFACT_TABLE.addVarCharColumn("GUID", 22, false);
   public static final SqlColumn ARTIFACT_ART_ID = ARTIFACT_TABLE.addColumn("ART_ID", JDBCType.BIGINT);
   public static final SqlColumn ARTIFACT_ART_TYPE_ID = ARTIFACT_TABLE.addColumn("ART_TYPE_ID", JDBCType.BIGINT);
   public static final SqlColumn ARTIFACT_GAMMA_ID = ARTIFACT_TABLE.addColumn("GAMMA_ID", JDBCType.BIGINT);
   static {
      ARTIFACT_TABLE.setPrimaryKeyConstraint(ARTIFACT_ART_ID, ARTIFACT_GAMMA_ID);
      ARTIFACT_TABLE.createIndex("OSEE_ART__ART_ID_IDX", true, ARTIFACT_ART_ID.getName());
      ARTIFACT_TABLE.createIndex("OSEE_ART__GUID_IDX", true, ARTIFACT_GUID.getName());
      ARTIFACT_TABLE.createIndex("OSEE_ART__ART_TYPE_ID_IDX", true, ARTIFACT_ART_TYPE_ID.getName());

   }

   public static final SqlTable ATTRIBUTE_TABLE = new SqlTable("osee_attribute", "att", ObjectType.ATTRIBUTE);
   public static final SqlColumn ATTRIBUTE_ATTR_TYPE_ID = ATTRIBUTE_TABLE.addColumn("ATTR_TYPE_ID", JDBCType.BIGINT);
   public static final SqlColumn ATTRIBUTE_ART_ID = ATTRIBUTE_TABLE.addColumn("ART_ID", JDBCType.BIGINT);
   public static final SqlColumn ATTRIBUTE_VALUE = ATTRIBUTE_TABLE.addVarCharColumn("VALUE", 4000);
   public static final SqlColumn ATTRIBUTE_ATRR_ID = ATTRIBUTE_TABLE.addColumn("ATTR_ID", JDBCType.BIGINT);
   public static final SqlColumn ATTRIBUTE_GAMMA_ID = ATTRIBUTE_TABLE.addColumn("GAMMA_ID", JDBCType.BIGINT);
   public static final SqlColumn ATTRIBUTE_URI = ATTRIBUTE_TABLE.addVarCharColumn("URI", 200);
   static {
      ATTRIBUTE_TABLE.setPrimaryKeyConstraint(ATTRIBUTE_ATRR_ID, ATTRIBUTE_GAMMA_ID);
      ATTRIBUTE_TABLE.createIndex("OSEE_ATTRIBUTE_AR_G_IDX", true, ATTRIBUTE_ART_ID.getName(),
         ATTRIBUTE_GAMMA_ID.getName());
      ATTRIBUTE_TABLE.createIndex("OSEE_ATTRIBUTE_G_AT_IDX", true, ATTRIBUTE_GAMMA_ID.getName(),
         ATTRIBUTE_ATRR_ID.getName());

   }
   public static final SqlTable RELATION_TABLE = new SqlTable("osee_relation_link", "rel", ObjectType.RELATION);

   public static final SqlColumn RELATION_LINK_REL_LINK_ID = RELATION_TABLE.addColumn("REL_LINK_ID", JDBCType.BIGINT);
   public static final SqlColumn RELATION_LINK_REL_LINK_TYPE_ID =
      RELATION_TABLE.addColumn("REL_LINK_TYPE_ID", JDBCType.BIGINT);
   public static final SqlColumn RELATION_LINK_A_ART_ID = RELATION_TABLE.addColumn("A_ART_ID", JDBCType.BIGINT);
   public static final SqlColumn RELATION_LINK_B_ART_ID = RELATION_TABLE.addColumn("B_ART_ID", JDBCType.BIGINT);
   public static final SqlColumn RELATION_LINK_RATIONALE = RELATION_TABLE.addVarCharColumn("RATIONALE", 4000);
   public static final SqlColumn RELATION_LINK_GAMMA_ID = RELATION_TABLE.addColumn("GAMMA_ID", JDBCType.BIGINT);

   static {
      RELATION_TABLE.setPrimaryKeyConstraint(RELATION_LINK_GAMMA_ID);
      RELATION_TABLE.createIndex("OSEE_RELATION__R_G_IDX", true, RELATION_LINK_REL_LINK_ID.getName(),
         RELATION_LINK_GAMMA_ID.getName());
      RELATION_TABLE.createIndex("OSEE_RELATION__A_IDX", true, RELATION_LINK_A_ART_ID.getName());
      RELATION_TABLE.createIndex("OSEE_RELATION__B_IDX", true, RELATION_LINK_B_ART_ID.getName());

   }

   public static final SqlTable BRANCH_TABLE = new SqlTable("osee_branch", "br", ObjectType.BRANCH);
   public static final SqlColumn BRANCH_NAME = BRANCH_TABLE.addVarCharColumn("BRANCH_NAME", 200, false);
   public static final SqlColumn BRANCH_TYPE = BRANCH_TABLE.addColumn("BRANCH_TYPE", JDBCType.SMALLINT);
   public static final SqlColumn BRANCH_BASELINE_TRANSACTION_ID =
      BRANCH_TABLE.addColumn("BASELINE_TRANSACTION_ID", JDBCType.BIGINT);
   public static final SqlColumn BRANCH_ASSOCIATED_ART_ID =
      BRANCH_TABLE.addColumn("ASSOCIATED_ART_ID", JDBCType.BIGINT);
   public static final SqlColumn BRANCH_ARCHIVED = BRANCH_TABLE.addColumn("ARCHIVED", JDBCType.SMALLINT);
   public static final SqlColumn BRANCH_ID = BRANCH_TABLE.addColumn("BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn BRANCH_STATE = BRANCH_TABLE.addColumn("BRANCH_STATE", JDBCType.SMALLINT);
   public static final SqlColumn BRANCH_PARENT_BRANCH_ID = BRANCH_TABLE.addColumn("PARENT_BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn BRANCH_PARENT_TRANSACTION_ID =
      BRANCH_TABLE.addColumn("PARENT_TRANSACTION_ID", JDBCType.BIGINT);
   public static final SqlColumn BRANCH_INHERIT_ACCESS_CONTROL =
      BRANCH_TABLE.addColumn("INHERIT_ACCESS_CONTROL", JDBCType.SMALLINT);
   static {
      BRANCH_TABLE.setPrimaryKeyConstraint(BRANCH_ID);
      BRANCH_TABLE.createIndex("OSEE_BRANCH_A_IDX", true, BRANCH_ARCHIVED.getName());
      BRANCH_TABLE.addStatement("INSERT INTO OSEE_BRANCH (" + Collections.toString(", ",
         BRANCH_TABLE.columns) + ") VALUES ('" + CoreBranches.SYSTEM_ROOT.getName() + "'," + BranchType.SYSTEM_ROOT.getIdString() + ",1,-1,0," + SYSTEM_ROOT.getIdString() + "," + BranchState.MODIFIED.getIdString() + ",-1,1,0)");
   }

   public static final SqlTable TXS_TABLE = new SqlTable("osee_txs", "txs", 1);

   public static final SqlColumn TXS_BRANCH_ID = TXS_TABLE.addColumn("BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn TXS_GAMMA_ID = TXS_TABLE.addColumn("GAMMA_ID", JDBCType.BIGINT);
   public static final SqlColumn TXS_TRANSACTION_ID = TXS_TABLE.addColumn("TRANSACTION_ID", JDBCType.BIGINT);
   public static final SqlColumn TXS_TX_CURRENT = TXS_TABLE.addColumn("TX_CURRENT", JDBCType.SMALLINT);
   public static final SqlColumn TXS_MOD_TYPE = TXS_TABLE.addColumn("MOD_TYPE", JDBCType.SMALLINT);
   public static final SqlColumn TXS_APP_ID = TXS_TABLE.addColumn("APP_ID", JDBCType.BIGINT);
   static {
      TXS_TABLE.setPrimaryKeyConstraint(TXS_BRANCH_ID, TXS_GAMMA_ID, TXS_TRANSACTION_ID);
   }

   public static final SqlTable TXS_ARCHIVED_TABLE = new SqlTable("osee_txs_archived", "txs_arc", 1);
   public static final SqlColumn TXS_ARCHIVED_BRANCH_ID = TXS_ARCHIVED_TABLE.addColumn("BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn TXS_ARCHIVED_GAMMA_ID = TXS_ARCHIVED_TABLE.addColumn("GAMMA_ID", JDBCType.BIGINT);
   public static final SqlColumn TXS_ARCHIVED_TRANSACTION_ID =
      TXS_ARCHIVED_TABLE.addColumn("TRANSACTION_ID", JDBCType.BIGINT);
   public static final SqlColumn TXS_ARCHIVED_TX_CURRENT =
      TXS_ARCHIVED_TABLE.addColumn("TX_CURRENT", JDBCType.SMALLINT);
   public static final SqlColumn TXS_ARCHIVED_MOD_TYPE = TXS_ARCHIVED_TABLE.addColumn("MOD_TYPE", JDBCType.SMALLINT);
   public static final SqlColumn TXS_ARCHIVED_APP_ID = TXS_ARCHIVED_TABLE.addColumn("APP_ID", JDBCType.BIGINT);
   static {
      TXS_ARCHIVED_TABLE.setPrimaryKeyConstraint(TXS_ARCHIVED_BRANCH_ID, TXS_ARCHIVED_GAMMA_ID,
         TXS_ARCHIVED_TRANSACTION_ID);
      TXS_ARCHIVED_TABLE.setTableExtras("TABLESPACE osee_archived");

   }

   public static final SqlTable TX_DETAILS_TABLE = new SqlTable("osee_tx_details", "txd", ObjectType.TX);
   public static final SqlColumn TX_DETAILS_TX_BRANCH_ID = TX_DETAILS_TABLE.addColumn("BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn TX_DETAILS_TRANSACTION_ID =
      TX_DETAILS_TABLE.addColumn("TRANSACTION_ID", JDBCType.BIGINT);

   public static final SqlColumn TX_DETAILS_AUTHOR = TX_DETAILS_TABLE.addColumn("AUTHOR", JDBCType.BIGINT);
   public static final SqlColumn TX_DETAILS_TIME = TX_DETAILS_TABLE.addColumn("TIME", JDBCType.TIMESTAMP);
   public static final SqlColumn TX_DETAILS_OSEE_COMMENT = TX_DETAILS_TABLE.addVarCharColumn("OSEE_COMMENT", 1000);
   public static final SqlColumn TX_DETAILS_TX_TYPE = TX_DETAILS_TABLE.addColumn("TX_TYPE", JDBCType.SMALLINT);
   public static final SqlColumn TX_DETAILS_TX_COMMIT_ART_ID =
      TX_DETAILS_TABLE.addColumn("COMMIT_ART_ID", JDBCType.BIGINT, true);
   public static final SqlColumn TX_DETAILS_BUILD_ID = TX_DETAILS_TABLE.addColumn("BUILD_ID", JDBCType.BIGINT);
   static {
      TX_DETAILS_TABLE.setPrimaryKeyConstraint(TX_DETAILS_TRANSACTION_ID);
      TX_DETAILS_TABLE.setForeignKeyConstraint("BRANCH_ID_FK1", TX_DETAILS_TX_BRANCH_ID, BRANCH_TABLE, BRANCH_ID);
      TX_DETAILS_TABLE.createIndex("OSEE_TX_DETAILS_B_TX_IDX", true, TX_DETAILS_TX_BRANCH_ID.getName(),
         TX_DETAILS_TRANSACTION_ID.getName());
      TX_DETAILS_TABLE.addStatement("INSERT INTO OSEE_TX_DETAILS (" + Collections.toString(",",
         (TX_DETAILS_TABLE.columns)) + ") VALUES (1,1,-1,CURRENT_TIMESTAMP,'" + CoreBranches.SYSTEM_ROOT.getName() + " Creation',1,NULL," + OseeCodeVersion.getVersionId() + ")");
   }

   public static final SqlTable OSEE_PERMISSION_TABLE = new SqlTable("osee_permission", "per");
   public static final SqlColumn OSEE_PERMISSION_PERMISSION_NAME =
      OSEE_PERMISSION_TABLE.addVarCharColumn("PERMISSION_NAME", 50, false);
   public static final SqlColumn OSEE_PERMISSION_PERMISSION_ID =
      OSEE_PERMISSION_TABLE.addColumn("PERMISSION_ID", JDBCType.INTEGER);
   static {
      OSEE_PERMISSION_TABLE.setPrimaryKeyConstraint(OSEE_PERMISSION_PERMISSION_ID);
   }

   public static final SqlTable OSEE_ARTIFACT_ACL_TABLE = new SqlTable("osee_artifact_acl", "art_acl");
   public static final SqlColumn OSEE_ARTIFACT_ACL_PRIVILEGE_ENTITY_ID =
      OSEE_ARTIFACT_ACL_TABLE.addColumn("PRIVILEGE_ENTITY_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ARTIFACT_ACL_ART_ID =
      OSEE_ARTIFACT_ACL_TABLE.addColumn("ART_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ARTIFACT_ACL_BRANCH_ID =
      OSEE_ARTIFACT_ACL_TABLE.addColumn("BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ARTIFACT_ACL_PERMISSION_ID =
      OSEE_ARTIFACT_ACL_TABLE.addColumn("PERMISSION_ID", JDBCType.INTEGER);
   static {
      OSEE_ARTIFACT_ACL_TABLE.setPrimaryKeyConstraint(OSEE_ARTIFACT_ACL_ART_ID, OSEE_ARTIFACT_ACL_PRIVILEGE_ENTITY_ID,
         OSEE_ARTIFACT_ACL_BRANCH_ID);
      OSEE_ARTIFACT_ACL_TABLE.setForeignKeyConstraint("ARTIFACT_ACL_PERM_FK", OSEE_ARTIFACT_ACL_PERMISSION_ID,
         OSEE_PERMISSION_TABLE, OSEE_PERMISSION_PERMISSION_ID);

   }

   public static final SqlTable OSEE_BRANCH_ACL_TABLE = new SqlTable("osee_branch_acl", "br_acl");
   public static final SqlColumn OSEE_BRANCH_ACL_PRIVILEGE_ENTITY_ID =
      OSEE_BRANCH_ACL_TABLE.addColumn("PRIVILEGE_ENTITY_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_BRANCH_ACL_BRANCH_ID =
      OSEE_BRANCH_ACL_TABLE.addColumn("BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_BRANCH_ACL_PERMISSION_ID =
      OSEE_BRANCH_ACL_TABLE.addColumn("PERMISSION_ID", JDBCType.INTEGER);
   static {
      OSEE_BRANCH_ACL_TABLE.setPrimaryKeyConstraint(OSEE_BRANCH_ACL_BRANCH_ID, OSEE_BRANCH_ACL_PRIVILEGE_ENTITY_ID);
      OSEE_BRANCH_ACL_TABLE.setForeignKeyConstraintCascadeDelete("BRANCH_ACL_FK", OSEE_BRANCH_ACL_BRANCH_ID,
         BRANCH_TABLE, BRANCH_ID);
      OSEE_BRANCH_ACL_TABLE.setForeignKeyConstraint("BRANCH_ACL_PERM_FK", OSEE_BRANCH_ACL_PERMISSION_ID,
         OSEE_PERMISSION_TABLE, OSEE_PERMISSION_PERMISSION_ID);

   }

   public static final SqlTable OSEE_SEARCH_TAGS_TABLE = new SqlTable("osee_search_tags", "srch_tgs");
   public static final SqlColumn OSEE_SEARCH_TAGS_CODED_TAG_ID =
      OSEE_SEARCH_TAGS_TABLE.addColumn("CODED_TAG_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_SEARCH_TAGS_GAMMA_ID =
      OSEE_SEARCH_TAGS_TABLE.addColumn("GAMMA_ID", JDBCType.BIGINT);
   static {
      OSEE_SEARCH_TAGS_TABLE.setPrimaryKeyConstraint(OSEE_SEARCH_TAGS_CODED_TAG_ID, OSEE_SEARCH_TAGS_GAMMA_ID);
      OSEE_SEARCH_TAGS_TABLE.createIndex("OSEE_SEARCH_TAGS_C_IDX", true, OSEE_SEARCH_TAGS_CODED_TAG_ID.getName());
      OSEE_SEARCH_TAGS_TABLE.createIndex("OSEE_SEARCH_TAGS_G_IDX", true, OSEE_SEARCH_TAGS_GAMMA_ID.getName());

   }

   public static final SqlTable OSEE_TAG_GAMMA_QUEUE_TABLE = new SqlTable("osee_tag_gamma_queue", "tg_gm_que");
   public static final SqlColumn OSEE_TAG_GAMMA_QUEUE_QUERY_ID =
      OSEE_TAG_GAMMA_QUEUE_TABLE.addColumn("QUERY_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_TAG_GAMMA_QUEUE_GAMMA_ID =
      OSEE_TAG_GAMMA_QUEUE_TABLE.addColumn("GAMMA_ID", JDBCType.BIGINT);
   static {
      OSEE_TAG_GAMMA_QUEUE_TABLE.setPrimaryKeyConstraint(OSEE_TAG_GAMMA_QUEUE_QUERY_ID, OSEE_TAG_GAMMA_QUEUE_GAMMA_ID);
   }

   public static final SqlTable OSEE_SEQUENCE_TABLE = new SqlTable("osee_sequence", "seq");
   public static final SqlColumn OSEE_SEQUENCE_SEQUENCE_NAME =
      OSEE_SEQUENCE_TABLE.addVarCharColumn("SEQUENCE_NAME", 128, false);
   public static final SqlColumn OSEE_SEQUENCE_LAST_SEQUENCE =
      OSEE_SEQUENCE_TABLE.addColumn("LAST_SEQUENCE", JDBCType.BIGINT);
   static {
      OSEE_SEQUENCE_TABLE.setUniqueKeyConstraint("SEQUENCE_ID_UN", OSEE_SEQUENCE_SEQUENCE_NAME.getName());
      OSEE_SEQUENCE_TABLE.addStatement("INSERT INTO OSEE_SEQUENCE (" + Collections.toString(",",
         OSEE_SEQUENCE_TABLE.columns) + ") VALUES ('SKYNET_TRANSACTION_ID_SEQ', 1)");

   }

   public static final SqlTable OSEE_INFO_TABLE = new SqlTable("osee_info", "inf");
   public static final SqlColumn OSEE_INFO_OSEE_VALUE = OSEE_INFO_TABLE.addVarCharColumn("OSEE_VALUE", 1000, false);
   public static final SqlColumn OSEE_INFO_OSEE_KEY = OSEE_INFO_TABLE.addVarCharColumn("OSEE_KEY", 50, false);
   static {
      OSEE_INFO_TABLE.setUniqueKeyConstraint("OSEE_INFO_KEY_UN_IDX", OSEE_INFO_OSEE_KEY.getName());
   }

   public static final SqlTable OSEE_MERGE_TABLE = new SqlTable("osee_merge", "mrg");
   public static final SqlColumn OSEE_MERGE_SOURCE_BRANCH_ID =
      OSEE_MERGE_TABLE.addColumn("SOURCE_BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_MERGE_MERGE_BRANCH_ID =
      OSEE_MERGE_TABLE.addColumn("MERGE_BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_MERGE_COMMIT_TRANSACTION_ID =
      OSEE_MERGE_TABLE.addColumn("COMMIT_TRANSACTION_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_MERGE_DEST_BRANCH_ID =
      OSEE_MERGE_TABLE.addColumn("DEST_BRANCH_ID", JDBCType.BIGINT);
   static {
      OSEE_MERGE_TABLE.setPrimaryKeyConstraint(OSEE_MERGE_MERGE_BRANCH_ID);
      OSEE_MERGE_TABLE.setForeignKeyConstraint("OSEE_MERGE__MBI_FK", OSEE_MERGE_MERGE_BRANCH_ID, BRANCH_TABLE,
         BRANCH_ID);
      OSEE_MERGE_TABLE.setForeignKeyConstraint("OSEE_MERGE__DBI_FK", OSEE_MERGE_DEST_BRANCH_ID, BRANCH_TABLE,
         BRANCH_ID);

   }

   public static final SqlTable OSEE_CONFLICT_TABLE = new SqlTable("osee_conflict", "conf");
   public static final SqlColumn OSEE_CONFLICT_SOURCE_GAMMA_ID =
      OSEE_CONFLICT_TABLE.addColumn("SOURCE_GAMMA_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_CONFLICT_MERGE_BRANCH_ID =
      OSEE_CONFLICT_TABLE.addColumn("MERGE_BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_CONFLICT_CONFLICT_ID =
      OSEE_CONFLICT_TABLE.addColumn("CONFLICT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_CONFLICT_DEST_GAMMA_ID =
      OSEE_CONFLICT_TABLE.addColumn("DEST_GAMMA_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_CONFLICT_CONFLICT_TYPE =
      OSEE_CONFLICT_TABLE.addColumn("CONFLICT_TYPE", JDBCType.SMALLINT);
   public static final SqlColumn OSEE_CONFLICT_STATUS = OSEE_CONFLICT_TABLE.addColumn("STATUS", JDBCType.SMALLINT);
   static {
      OSEE_CONFLICT_TABLE.setPrimaryKeyConstraint(OSEE_CONFLICT_MERGE_BRANCH_ID, OSEE_CONFLICT_SOURCE_GAMMA_ID);
      OSEE_CONFLICT_TABLE.setForeignKeyConstraint("OSEE_CONFLICT__MBI_FK", OSEE_CONFLICT_MERGE_BRANCH_ID,
         OSEE_MERGE_TABLE, OSEE_MERGE_MERGE_BRANCH_ID);
   }

   public static final SqlTable OSEE_JOIN_EXPORT_IMPORT_TABLE = new SqlTable("osee_join_export_import", "jn_ex_im");
   public static final SqlColumn OSEE_JOIN_EXPORT_IMPORT_ID2 =
      OSEE_JOIN_EXPORT_IMPORT_TABLE.addColumn("ID2", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_EXPORT_IMPORT_ID1 =
      OSEE_JOIN_EXPORT_IMPORT_TABLE.addColumn("ID1", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_EXPORT_IMPORT_TABLE_QUERY_ID =
      OSEE_JOIN_EXPORT_IMPORT_TABLE.addColumn("QUERY_ID", JDBCType.BIGINT);
   static {
      OSEE_JOIN_EXPORT_IMPORT_TABLE.setTableExtras("TABLESPACE osee_join");
   }

   public static final SqlTable OSEE_IMPORT_SOURCE_TABLE = new SqlTable("osee_import_source", "imp_src");
   public static final SqlColumn OSEE_IMPORT_SOURCE_IMPORT_ID =
      OSEE_IMPORT_SOURCE_TABLE.addColumn("IMPORT_ID", JDBCType.INTEGER);
   public static final SqlColumn OSEE_IMPORT_SOURCE_SOURCE_EXPORT_DATE =
      OSEE_IMPORT_SOURCE_TABLE.addColumn("SOURCE_EXPORT_DATE", JDBCType.TIMESTAMP);
   public static final SqlColumn OSEE_IMPORT_SOURCE_DB_SOURCE_GUID =
      OSEE_IMPORT_SOURCE_TABLE.addVarCharColumn("DB_SOURCE_GUID", 28, false);
   public static final SqlColumn OSEE_IMPORT_SOURCE_DATA_IMPORTED =
      OSEE_IMPORT_SOURCE_TABLE.addColumn("DATE_IMPORTED", JDBCType.TIMESTAMP);
   static {
      OSEE_IMPORT_SOURCE_TABLE.setPrimaryKeyConstraint(OSEE_IMPORT_SOURCE_IMPORT_ID);

   }

   public static final SqlTable OSEE_IMPORT_SAVE_POINT_TABLE = new SqlTable("osee_import_save_point", "imp_sv_pt");
   public static final SqlColumn OSEE_IMPORT_SAVE_POINT_IMPORT_ID =
      OSEE_IMPORT_SAVE_POINT_TABLE.addColumn("IMPORT_ID", JDBCType.INTEGER);
   public static final SqlColumn OSEE_IMPORT_SAVE_POINT_STATE_ERROR =
      OSEE_IMPORT_SAVE_POINT_TABLE.addVarCharColumn("STATE_ERROR", 4000);
   public static final SqlColumn OSEE_IMPORT_SAVE_POINT_STATUS =
      OSEE_IMPORT_SAVE_POINT_TABLE.addColumn("STATUS", JDBCType.INTEGER);
   public static final SqlColumn OSEE_IMPORT_SAVE_POINT_SAVE_POINT_NAME =
      OSEE_IMPORT_SAVE_POINT_TABLE.addVarCharColumn("SAVE_POINT_NAME", 128, false);

   static {
      OSEE_IMPORT_SAVE_POINT_TABLE.setPrimaryKeyConstraint(OSEE_IMPORT_SAVE_POINT_IMPORT_ID,
         OSEE_IMPORT_SAVE_POINT_SAVE_POINT_NAME);
      OSEE_IMPORT_SAVE_POINT_TABLE.setForeignKeyConstraint("OSEE_IMP_SAVE_POINT_II_FK",
         OSEE_IMPORT_SAVE_POINT_IMPORT_ID, OSEE_IMPORT_SOURCE_TABLE, OSEE_IMPORT_SOURCE_IMPORT_ID);
   }

   public static final SqlTable OSEE_IMPORT_MAP_TABLE = new SqlTable("osee_import_map", "imp_mp");
   public static final SqlColumn OSEE_IMPORT_MAP_IMPORT_ID =
      OSEE_IMPORT_MAP_TABLE.addColumn("IMPORT_ID", JDBCType.INTEGER);
   public static final SqlColumn OSEE_IMPORT_MAP_SEQUENCE_NAME =
      OSEE_IMPORT_MAP_TABLE.addVarCharColumn("SEQUENCE_NAME", 128, false);
   public static final SqlColumn OSEE_IMPORT_MAP_SEQUENCE_ID =
      OSEE_IMPORT_MAP_TABLE.addColumn("SEQUENCE_ID", JDBCType.INTEGER);
   static {
      OSEE_IMPORT_MAP_TABLE.setPrimaryKeyConstraint(OSEE_IMPORT_MAP_SEQUENCE_ID);
      OSEE_IMPORT_MAP_TABLE.setForeignKeyConstraint("OSEE_IMPORT_MAP_II_FK", OSEE_IMPORT_MAP_IMPORT_ID,
         OSEE_IMPORT_SOURCE_TABLE, OSEE_IMPORT_SOURCE_IMPORT_ID);
   }

   public static final SqlTable OSEE_IMPORT_INDEX_MAP_TABLE = new SqlTable("osee_import_index_map", "imp_mp");
   public static final SqlColumn OSEE_IMPORT_INDEX_MAP_MAPPED_ID =
      OSEE_IMPORT_INDEX_MAP_TABLE.addColumn("MAPPED_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_IMPORT_INDEX_MAP_SEQUENCE_ID =
      OSEE_IMPORT_INDEX_MAP_TABLE.addColumn("SEQUENCE_ID", JDBCType.INTEGER);
   public static final SqlColumn OSEE_IMPORT_INDEX_MAP_ORIGINAL_ID =
      OSEE_IMPORT_INDEX_MAP_TABLE.addColumn("ORIGINAL_ID", JDBCType.BIGINT);
   static {
      OSEE_IMPORT_INDEX_MAP_TABLE.setPrimaryKeyConstraint(OSEE_IMPORT_INDEX_MAP_SEQUENCE_ID,
         OSEE_IMPORT_INDEX_MAP_ORIGINAL_ID, OSEE_IMPORT_INDEX_MAP_MAPPED_ID);
      OSEE_IMPORT_INDEX_MAP_TABLE.setForeignKeyConstraint("OSEE_IMPORT_INDEX_MAP_II_FK",
         OSEE_IMPORT_INDEX_MAP_SEQUENCE_ID, OSEE_IMPORT_MAP_TABLE, OSEE_IMPORT_MAP_SEQUENCE_ID);

      OSEE_IMPORT_INDEX_MAP_TABLE.createIndex("OSEE_IMPORT_INDEX_MAP_IO_IDX", true,
         OSEE_IMPORT_INDEX_MAP_SEQUENCE_ID.getName(), OSEE_IMPORT_INDEX_MAP_ORIGINAL_ID.getName());
      OSEE_IMPORT_INDEX_MAP_TABLE.createIndex("OSEE_IMPORT_INDEX_MAP_IM_IDX", true,
         OSEE_IMPORT_INDEX_MAP_SEQUENCE_ID.getName(), OSEE_IMPORT_INDEX_MAP_MAPPED_ID.getName());

   }

   public static final SqlTable OSEE_JOIN_ARTIFACT_TABLE = new SqlTable("osee_join_artifact", "jn_art");
   public static final SqlColumn OSEE_JOIN_ARTIFACT_TABLE_ART_ID =
      OSEE_JOIN_ARTIFACT_TABLE.addColumn("ART_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_ARTIFACT_TABLE_BRANCH_ID =
      OSEE_JOIN_ARTIFACT_TABLE.addColumn("BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_ARTIFACT_TABLE_TRANSACTION_ID =
      OSEE_JOIN_ARTIFACT_TABLE.addColumn("TRANSACTION_ID", JDBCType.BIGINT, true);
   public static final SqlColumn OSEE_JOIN_ARTIFACT_TABLE_QUERY_ID =
      OSEE_JOIN_ARTIFACT_TABLE.addColumn("QUERY_ID", JDBCType.BIGINT);

   static {
      OSEE_JOIN_ARTIFACT_TABLE.createIndex("OSEE_JOIN_ART__Q_A_IDX", true, OSEE_JOIN_ARTIFACT_TABLE_QUERY_ID.getName(),
         OSEE_JOIN_ARTIFACT_TABLE_ART_ID.getName());
      OSEE_JOIN_ARTIFACT_TABLE.setTableExtras("TABLESPACE osee_join");
   }

   public static final SqlTable OSEE_JOIN_ID_TABLE = new SqlTable("osee_join_id", "jn_id");
   public static final SqlColumn OSEE_JOIN_ID_TABLE_ID = OSEE_JOIN_ID_TABLE.addColumn("ID", JDBCType.BIGINT, true);
   public static final SqlColumn OSEE_JOIN_ID_TABLE_QUERY_ID =
      OSEE_JOIN_ID_TABLE.addColumn("QUERY_ID", JDBCType.BIGINT);
   static {
      OSEE_JOIN_ID_TABLE.createIndex("OSEE_JOIN_ID__Q_I_IDX", true, OSEE_JOIN_ID_TABLE_QUERY_ID.getName(),
         OSEE_JOIN_ID_TABLE_ID.getName());
      OSEE_JOIN_ID_TABLE.setTableExtras("TABLESPACE osee_join");

   }

   public static final SqlTable OSEE_JOIN_CLEANUP_TABLE = new SqlTable("osee_join_cleanup", "jn_clup");
   public static final SqlColumn OSEE_JOIN_CLEANUP_TABLE_NAME =
      OSEE_JOIN_CLEANUP_TABLE.addVarCharColumn("TABLE_NAME", 28, false);
   public static final SqlColumn OSEE_JOIN_CLEANUP_EXPIRES_IN =
      OSEE_JOIN_CLEANUP_TABLE.addColumn("EXPIRES_IN", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_CLEANUP_ISSUED_AT =
      OSEE_JOIN_CLEANUP_TABLE.addColumn("ISSUED_AT", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_CLEANUP_QUERY_ID =
      OSEE_JOIN_CLEANUP_TABLE.addColumn("QUERY_ID", JDBCType.BIGINT);
   static {
      OSEE_JOIN_CLEANUP_TABLE.setPrimaryKeyConstraint(OSEE_JOIN_CLEANUP_QUERY_ID);
      OSEE_JOIN_CLEANUP_TABLE.setTableExtras("TABLESPACE osee_join");

   }

   public static final SqlTable OSEE_JOIN_CHAR_ID_TABLE = new SqlTable("osee_join_char_id", "jn_chr_id");
   public static final SqlColumn OSEE_JOIN_CHAR_ID_ID = OSEE_JOIN_CHAR_ID_TABLE.addVarCharColumn("ID", 4000, false);
   public static final SqlColumn OSEE_JOIN_CHAR_ID_QUERY_ID =
      OSEE_JOIN_CHAR_ID_TABLE.addColumn("QUERY_ID", JDBCType.BIGINT);
   static {
      OSEE_JOIN_CHAR_ID_TABLE.createIndex("OSEE_JOIN_CHAR__Q_IDX", true, OSEE_JOIN_CHAR_ID_QUERY_ID.getName());
      OSEE_JOIN_CHAR_ID_TABLE.setTableExtras("TABLESPACE osee_join");

   }

   public static final SqlTable OSEE_JOIN_TRANSACTION_TABLE = new SqlTable("osee_join_transaction", "jn_trns");
   public static final SqlColumn OSEE_JOIN_TRANSACTION_BRANCH_ID =
      OSEE_JOIN_TRANSACTION_TABLE.addColumn("BRANCH_ID", JDBCType.BIGINT, true);
   public static final SqlColumn OSEE_JOIN_TRANSACTION_TRANSACTION_ID =
      OSEE_JOIN_TRANSACTION_TABLE.addColumn("TRANSACTION_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_TRANSACTION_QUERY_ID =
      OSEE_JOIN_TRANSACTION_TABLE.addColumn("QUERY_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_TRANSACTION_GAMMA_ID =
      OSEE_JOIN_TRANSACTION_TABLE.addColumn("GAMMA_ID", JDBCType.BIGINT);
   static {
      OSEE_JOIN_TRANSACTION_TABLE.createIndex("OSEE_JOIN_TRANSACTION_Q_IDX", true,
         OSEE_JOIN_TRANSACTION_QUERY_ID.getName());
      OSEE_JOIN_TRANSACTION_TABLE.setTableExtras("TABLESPACE osee_join");

   }

   public static final SqlTable OSEE_BRANCH_GROUP_TABLE = new SqlTable("osee_branch_group", "br_grp", 2);
   public static final SqlColumn OSEE_BRANCH_GROUP_GROUP_TYPE =
      OSEE_BRANCH_GROUP_TABLE.addColumn("GROUP_TYPE", JDBCType.BIGINT);
   public static final SqlColumn OSEE_BRANCH_GROUP_GROUP_ID =
      OSEE_BRANCH_GROUP_TABLE.addColumn("GROUP_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_BRANCH_GROUP_BRANCH_ID =
      OSEE_BRANCH_GROUP_TABLE.addColumn("BRANCH_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_BRANCH_GROUP_GAMMA_ID =
      OSEE_BRANCH_GROUP_TABLE.addColumn("GAMMA_ID", JDBCType.BIGINT);
   static {
      OSEE_BRANCH_GROUP_TABLE.setPrimaryKeyConstraint(OSEE_BRANCH_GROUP_GROUP_TYPE, OSEE_BRANCH_GROUP_GROUP_ID,
         OSEE_BRANCH_GROUP_BRANCH_ID);
      OSEE_BRANCH_GROUP_TABLE.createIndex("OSEE_BRANCH_GROUP__G_IDX", false, OSEE_BRANCH_GROUP_GAMMA_ID.getName());

   }

   public static final SqlTable LDAP_DETAILS_TABLE = new SqlTable("ldap_details", "ldap_det");
   public static final SqlColumn LDAP_DETAILS_USER_NAME = LDAP_DETAILS_TABLE.addVarCharColumn("USER_NAME", 1000, false);
   public static final SqlColumn LDAP_DETAILS_PASSWORD = LDAP_DETAILS_TABLE.addVarCharColumn("PASSWORD", 1000, false);
   public static final SqlColumn LDAP_DETAILS_SERVER_NAME =
      LDAP_DETAILS_TABLE.addVarCharColumn("SERVER_NAME", 1000, false);
   public static final SqlColumn LDAP_DETAILS_PORT = LDAP_DETAILS_TABLE.addColumn("PORT", JDBCType.INTEGER);
   public static final SqlColumn LDAP_DETAILS_SEARCH_BASE =
      LDAP_DETAILS_TABLE.addVarCharColumn("SEARCH_BASE", 1000, false);
   public static final SqlColumn LDAP_DETAILS_GROUP_SEARCH_FILTER =
      LDAP_DETAILS_TABLE.addVarCharColumn("GROUP_SEARCH_FILTER", 1000, false);

   public static final SqlTable TUPLE2 = new SqlTable("osee_tuple2", "tp2", ObjectType.TUPLE, 2);
   public static final SqlColumn TUPLE2_TUPLE_TYPE = TUPLE2.addColumn("TUPLE_TYPE", JDBCType.BIGINT);
   public static final SqlColumn TUPLE2_E1 = TUPLE2.addColumn("E1", JDBCType.BIGINT);
   public static final SqlColumn TUPLE2_E2 = TUPLE2.addColumn("E2", JDBCType.BIGINT);
   public static final SqlColumn TUPLE2_GAMMA_ID = TUPLE2.addColumn("GAMMA_ID", JDBCType.BIGINT);
   static {
      TUPLE2.setPrimaryKeyConstraint(TUPLE2_TUPLE_TYPE, TUPLE2_E1, TUPLE2_E2);
      TUPLE2.createIndex("OSEE_TUPLE2__G_IDX", false, TUPLE2_GAMMA_ID.getName());
   }

   public static final SqlTable TUPLE3 = new SqlTable("osee_tuple3", "tp3", ObjectType.TUPLE, 2);
   public static final SqlColumn TUPLE3_TUPLE_TYPE = TUPLE3.addColumn("TUPLE_TYPE", JDBCType.BIGINT);
   public static final SqlColumn TUPLE3_E1 = TUPLE3.addColumn("E1", JDBCType.BIGINT);
   public static final SqlColumn TUPLE3_E2 = TUPLE3.addColumn("E2", JDBCType.BIGINT);
   public static final SqlColumn TUPLE3_E3 = TUPLE3.addColumn("E3", JDBCType.BIGINT);

   public static final SqlColumn TUPLE3_GAMMA_ID = TUPLE3.addColumn("GAMMA_ID", JDBCType.BIGINT);
   static {
      TUPLE3.setPrimaryKeyConstraint(TUPLE3_TUPLE_TYPE, TUPLE3_E1, TUPLE3_E2, TUPLE3_E3);
      TUPLE3.createIndex("OSEE_TUPLE3__G_IDX", false, TUPLE3_GAMMA_ID.getName());

   }

   public static final SqlTable TUPLE4 = new SqlTable("osee_tuple4", "tp4", ObjectType.TUPLE, 3);
   public static final SqlColumn TUPLE4_TUPLE_TYPE = TUPLE4.addColumn("TUPLE_TYPE", JDBCType.BIGINT);
   public static final SqlColumn TUPLE4_E1 = TUPLE4.addColumn("E1", JDBCType.BIGINT);
   public static final SqlColumn TUPLE4_E2 = TUPLE4.addColumn("E2", JDBCType.BIGINT);
   public static final SqlColumn TUPLE4_E3 = TUPLE4.addColumn("E3", JDBCType.BIGINT);
   public static final SqlColumn TUPLE4_E4 = TUPLE4.addColumn("E4", JDBCType.BIGINT);
   public static final SqlColumn TUPLE4_GAMMA_ID = TUPLE4.addColumn("GAMMA_ID", JDBCType.BIGINT);
   static {
      TUPLE4.setPrimaryKeyConstraint(TUPLE4_TUPLE_TYPE, TUPLE4_E1, TUPLE4_E2, TUPLE4_E3, TUPLE4_E4);
      TUPLE4.createIndex("OSEE_TUPLE4__G_IDX", false, TUPLE4_GAMMA_ID.getName());

   }

   public static final SqlTable OSEE_KEY_VALUE_TABLE = new SqlTable("osee_key_value", "key_val", 0);
   public static final SqlColumn OSEE_KEY_VALUE_KEY = OSEE_KEY_VALUE_TABLE.addColumn("KEY", JDBCType.BIGINT);
   public static final SqlColumn OSEE_KEY_VALUE_VALUE = OSEE_KEY_VALUE_TABLE.addVarCharColumn("VALUE", 4000);
   static {
      OSEE_KEY_VALUE_TABLE.setPrimaryKeyConstraint(OSEE_KEY_VALUE_KEY);
      OSEE_KEY_VALUE_TABLE.createIndex("OSEE_KEY_VALUE__V_IDX", false, OSEE_KEY_VALUE_VALUE.getName());
      OSEE_KEY_VALUE_TABLE.setTableExtras("TABLESPACE osee_data\n\tPCTTHRESHOLD 20\n\tOVERFLOW TABLESPACE osee_data");
   }

   public static final SqlTable OSEE_JOIN_ID4_TABLE = new SqlTable("osee_join_id4", "jn_id4", 2);
   public static final SqlColumn OSEE_JOIN_ID4_QUERY_ID = OSEE_JOIN_ID4_TABLE.addColumn("QUERY_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_ID4_ID1 = OSEE_JOIN_ID4_TABLE.addColumn("ID1", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_ID4_ID2 = OSEE_JOIN_ID4_TABLE.addColumn("ID2", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_ID4_ID3 = OSEE_JOIN_ID4_TABLE.addColumn("ID3", JDBCType.BIGINT);
   public static final SqlColumn OSEE_JOIN_ID4_ID4 = OSEE_JOIN_ID4_TABLE.addColumn("ID4", JDBCType.BIGINT);
   static {
      OSEE_JOIN_ID4_TABLE.setPrimaryKeyConstraint(OSEE_JOIN_ID4_QUERY_ID, OSEE_JOIN_ID4_ID1, OSEE_JOIN_ID4_ID2,
         OSEE_JOIN_ID4_ID3, OSEE_JOIN_ID4_ID4);
      OSEE_JOIN_ID4_TABLE.createIndex("OSEE_JOIN_ID4__Q_IDX", false, OSEE_JOIN_ID4_QUERY_ID.getName());
      OSEE_JOIN_ID4_TABLE.setTableExtras("TABLESPACE osee_join");
   }

   public static final SqlTable OSEE_SERVER_LOOKUP_TABLE = new SqlTable("osee_server_lookup", "srvr_lkup");
   public static final SqlColumn OSEE_SERVER_LOOKUP_ACCEPTS_REQUESTS =
      OSEE_SERVER_LOOKUP_TABLE.addColumn("ACCEPTS_REQUESTS", JDBCType.SMALLINT);
   public static final SqlColumn OSEE_SERVER_LOOKUP_VERSION_ID =
      OSEE_SERVER_LOOKUP_TABLE.addVarCharColumn("VERSION_ID", 100, false);
   public static final SqlColumn OSEE_SERVER_LOOKUP_SERVER_ID =
      OSEE_SERVER_LOOKUP_TABLE.addVarCharColumn("SERVER_ID", 40, false);
   public static final SqlColumn OSEE_SERVER_LOOKUP_START_TIME =
      OSEE_SERVER_LOOKUP_TABLE.addColumn("START_TIME", JDBCType.TIMESTAMP);
   public static final SqlColumn OSEE_SERVER_LOOKUP_SERVER_URI =
      OSEE_SERVER_LOOKUP_TABLE.addVarCharColumn("SERVER_URI", 255, false);
   static {
      OSEE_SERVER_LOOKUP_TABLE.setPrimaryKeyConstraint(OSEE_SERVER_LOOKUP_SERVER_URI, OSEE_SERVER_LOOKUP_VERSION_ID);
   }

   public static final SqlTable OSEE_SESSION_TABLE = new SqlTable("osee_session", "sess");
   public static final SqlColumn OSEE_SESSION_CLIENT_ADDRESS =
      OSEE_SESSION_TABLE.addVarCharColumn("CLIENT_ADDRESS", 255, false);
   public static final SqlColumn OSEE_SESSION_USER_ID = OSEE_SESSION_TABLE.addVarCharColumn("USER_ID", 100, false);
   public static final SqlColumn OSEE_SESSION_CREATED_ON =
      OSEE_SESSION_TABLE.addColumn("CREATED_ON", JDBCType.TIMESTAMP);
   public static final SqlColumn OSEE_SESSION_CLIENT_PORT =
      OSEE_SESSION_TABLE.addColumn("CLIENT_PORT", JDBCType.INTEGER);
   public static final SqlColumn OSEE_SESSION_CLIENT_VERSION =
      OSEE_SESSION_TABLE.addVarCharColumn("CLIENT_VERSION", 100, false);
   public static final SqlColumn OSEE_SESSION_SESSION_ID = OSEE_SESSION_TABLE.addVarCharColumn("SESSION_ID", 28, false);
   public static final SqlColumn OSEE_SESSION_CLIENT_MACHINE_NAME =
      OSEE_SESSION_TABLE.addVarCharColumn("CLIENT_MACHINE_NAME", 100, false);
   static {
      OSEE_SESSION_TABLE.setPrimaryKeyConstraint(OSEE_SESSION_SESSION_ID);
   }

   public static final SqlTable OSEE_ACCOUNT_SESSION_TABLE = new SqlTable("osee_account_session", "acc_sess");
   public static final SqlColumn OSEE_ACCOUNT_SESSION_CREATED_ON =
      OSEE_ACCOUNT_SESSION_TABLE.addColumn("CREATED_ON", JDBCType.TIMESTAMP);
   public static final SqlColumn OSEE_ACCOUNT_SESSION_ACCOUNT_ID =
      OSEE_ACCOUNT_SESSION_TABLE.addColumn("ACCOUNT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ACCOUNT_SESSION_ACCESSED_FROM =
      OSEE_ACCOUNT_SESSION_TABLE.addVarCharColumn("ACCESSED_FROM", 255, false);
   public static final SqlColumn OSEE_ACCOUNT_SESSION_ACCESS_DETAILS =
      OSEE_ACCOUNT_SESSION_TABLE.addVarCharColumn("ACCESS_DETAILS", 255, false);
   public static final SqlColumn OSEE_ACCOUNT_SESSION_LAST_ACCESSED_ON =
      OSEE_ACCOUNT_SESSION_TABLE.addColumn("LAST_ACCESSED_ON", JDBCType.TIMESTAMP);
   public static final SqlColumn OSEE_ACCOUNT_SESSION_SESSION_TOKEN =
      OSEE_ACCOUNT_SESSION_TABLE.addVarCharColumn("SESSION_TOKEN", 255, false);
   static {
      OSEE_ACCOUNT_SESSION_TABLE.setPrimaryKeyConstraint(OSEE_ACCOUNT_SESSION_ACCOUNT_ID,
         OSEE_ACCOUNT_SESSION_SESSION_TOKEN);
      OSEE_ACCOUNT_SESSION_TABLE.createIndex("OSEE_ACCOUNT_SESSION_T_IDX", false,
         OSEE_ACCOUNT_SESSION_SESSION_TOKEN.getName());
   }

   public static final SqlTable OSEE_ACTIVITY_TYPE_TABLE = new SqlTable("osee_activity_type", "acc_sess");
   public static final SqlColumn OSEE_ACTIVITY_TYPE_TYPE_ID =
      OSEE_ACTIVITY_TYPE_TABLE.addColumn("TYPE_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ACTIVITY_TYPE_MODULE =
      OSEE_ACTIVITY_TYPE_TABLE.addVarCharColumn("MODULE", 4000, true);
   public static final SqlColumn OSEE_ACTIVITY_TYPE_MSG_FORMAT =
      OSEE_ACTIVITY_TYPE_TABLE.addVarCharColumn("MSG_FORMAT", 4000, true);
   public static final SqlColumn OSEE_ACTIVITY_TYPE_LOG_LEVEL =
      OSEE_ACTIVITY_TYPE_TABLE.addColumn("CREATED_ON", JDBCType.TIMESTAMP);
   static {
      OSEE_ACTIVITY_TYPE_TABLE.setPrimaryKeyConstraint(OSEE_ACTIVITY_TYPE_TYPE_ID);
   }

   public static final SqlTable OSEE_ACTIVITY_TABLE = new SqlTable("osee_activity", "act");
   public static final SqlColumn OSEE_ACTIVITY_ENTRY_ID = OSEE_ACTIVITY_TABLE.addColumn("ENTRY_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ACTIVITY_ACCOUNT_ID =
      OSEE_ACTIVITY_TABLE.addColumn("ACCOUNT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ACTIVITY_CLIENT_ID = OSEE_ACTIVITY_TABLE.addColumn("CLIENT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ACTIVITY_TYPE_ID = OSEE_ACTIVITY_TABLE.addColumn("TYPE_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ACTIVITY_SERVER_ID = OSEE_ACTIVITY_TABLE.addColumn("SERVER_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ACTIVITY_MSG_ARGS = OSEE_ACTIVITY_TABLE.addVarCharColumn("MSG_ARGS", 4000, true);
   public static final SqlColumn OSEE_ACTIVITY_START_TIME =
      OSEE_ACTIVITY_TABLE.addColumn("START_TIME", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ACTIVITY_STATUS = OSEE_ACTIVITY_TABLE.addColumn("STATUS", JDBCType.SMALLINT);
   public static final SqlColumn OSEE_ACTIVITY_PARENT_ID = OSEE_ACTIVITY_TABLE.addColumn("PARENT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_ACTIVITY_DURATION = OSEE_ACTIVITY_TABLE.addColumn("DURATION", JDBCType.BIGINT);
   static {
      OSEE_ACTIVITY_TABLE.setPrimaryKeyConstraint(OSEE_ACTIVITY_ENTRY_ID);
      OSEE_ACTIVITY_TABLE.createIndex("OSEE_ACTIVITY__P_E_IDX", false, OSEE_ACTIVITY_PARENT_ID.getName(),
         OSEE_ACTIVITY_ENTRY_ID.getName());
      OSEE_ACTIVITY_TABLE.createIndex("OSEE_ACTIVITY__ACCOUNT_IDX", false, OSEE_ACTIVITY_ACCOUNT_ID.getName());
      OSEE_ACTIVITY_TABLE.createIndex("OSEE_ACTIVITY__TYPE_IDX", false, OSEE_ACTIVITY_TYPE_ID.getName());

   }

   public static final SqlTable OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE =
      new SqlTable("osee_oauth_client_credential", "oauth_cli_cred");
   public static final SqlColumn OSEE_OAUTH_CLIENT_CREDENTIAL_CLIENT_ID =
      OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE.addColumn("CLIENT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_CLIENT_CREDENTIAL_APPLICATION_ID =
      OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE.addColumn("APPLICATION_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_CLIENT_CREDENTIAL_SUBJECT_ID =
      OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE.addColumn("SUBJECT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_CLIENT_CREDENTIAL_CLIENT_CERT =
      OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE.addVarCharColumn("CLIENT_CERT", 255, false);
   public static final SqlColumn OSEE_OAUTH_CLIENT_CREDENTIAL_CLIENT_SECRET =
      OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE.addVarCharColumn("CLIENT_SECRET", 255, false);
   public static final SqlColumn OSEE_OAUTH_CLIENT_CREDENTIAL_CLIENT_KEY =
      OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE.addVarCharColumn("CLIENT_KEY", 255, true);
   static {
      OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE.setPrimaryKeyConstraint("OSEE_OAUTH_CLIENT_CRED__U",
         OSEE_OAUTH_CLIENT_CREDENTIAL_CLIENT_ID);
      OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE.createIndex("OSEE_OAUTH_CLIENT_CRED__CK_IDX", false,
         OSEE_OAUTH_CLIENT_CREDENTIAL_CLIENT_KEY.getName());
      OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE.createIndex("OSEE_OAUTH_CLIENT_CRED__AI_IDX", false,
         OSEE_OAUTH_CLIENT_CREDENTIAL_APPLICATION_ID.getName());
   }

   public static final SqlTable OSEE_OAUTH_AUTHORIZATION_TABLE = new SqlTable("osee_oauth_authorization", "oauth_auth");
   public static final SqlColumn OSEE_OAUTH_AUTHORIZATION_AUDIENCE =
      OSEE_OAUTH_AUTHORIZATION_TABLE.addVarCharColumn("AUDIENCE", 512, true);
   public static final SqlColumn OSEE_OAUTH_AUTHORIZATION_APPROVED_SCOPES =
      OSEE_OAUTH_AUTHORIZATION_TABLE.addVarCharColumn("APPROVED_SCOPES", 2000, true);
   public static final SqlColumn OSEE_OAUTH_AUTHORIZATION_REDIRECT_URI =
      OSEE_OAUTH_AUTHORIZATION_TABLE.addVarCharColumn("REDIRECT_URI", 512, true);
   public static final SqlColumn OSEE_OAUTH_AUTHORIZATION_EXPIRES_IN =
      OSEE_OAUTH_AUTHORIZATION_TABLE.addColumn("EXPIRES_IN", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_AUTHORIZATION_ISSUED_AT =
      OSEE_OAUTH_AUTHORIZATION_TABLE.addColumn("ISSUED_AT", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_AUTHORIZATION_VERIFIER =
      OSEE_OAUTH_AUTHORIZATION_TABLE.addVarCharColumn("VERIFIER", 512, true);
   public static final SqlColumn OSEE_OAUTH_AUTHORIZATION_CLIENT_ID =
      OSEE_OAUTH_AUTHORIZATION_TABLE.addColumn("CLIENT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_AUTHORIZATION_ID =
      OSEE_OAUTH_AUTHORIZATION_TABLE.addColumn("ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_AUTHORIZATION_SUBJECT_ID =
      OSEE_OAUTH_AUTHORIZATION_TABLE.addColumn("SUBJECT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_AUTHORIZATION_CODE =
      OSEE_OAUTH_AUTHORIZATION_TABLE.addVarCharColumn("CODE", 512, true);
   static {
      OSEE_OAUTH_AUTHORIZATION_TABLE.setPrimaryKeyConstraint(OSEE_OAUTH_AUTHORIZATION_ID);
      OSEE_OAUTH_AUTHORIZATION_TABLE.setForeignKeyConstraintCascadeDelete("OSEE_OAUTH_AUTH__CI_FK",
         OSEE_OAUTH_AUTHORIZATION_CLIENT_ID, OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE,
         OSEE_OAUTH_CLIENT_CREDENTIAL_CLIENT_ID);
      OSEE_OAUTH_AUTHORIZATION_TABLE.createIndex("OSEE_OAUTH_AUTH__C_IDX", false,
         OSEE_OAUTH_AUTHORIZATION_CODE.getName());
   }

   public static final SqlTable OSEE_OAUTH_TOKEN_TABLE = new SqlTable("osee_oauth_token", "oauth_tk");
   public static final SqlColumn OSEE_OAUTH_TOKEN_AUDIENCE =
      OSEE_OAUTH_TOKEN_TABLE.addVarCharColumn("AUDIENCE", 512, true);
   public static final SqlColumn OSEE_OAUTH_TOKEN_EXPIRES_IN =
      OSEE_OAUTH_TOKEN_TABLE.addColumn("EXPIRES_IN", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_TOKEN_ISSUED_AT =
      OSEE_OAUTH_TOKEN_TABLE.addColumn("ISSUED_AT", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_TOKEN_TOKEN_KEY =
      OSEE_OAUTH_TOKEN_TABLE.addVarCharColumn("TOKEN_KEY", 512, true);
   public static final SqlColumn OSEE_OAUTH_TOKEN_PARENT_TOKEN_ID =
      OSEE_OAUTH_TOKEN_TABLE.addColumn("PARENT_TOKEN_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_TOKEN_CLIENT_ID =
      OSEE_OAUTH_TOKEN_TABLE.addColumn("CLIENT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_TOKEN_GRANT_TYPE =
      OSEE_OAUTH_TOKEN_TABLE.addVarCharColumn("GRANT_TYPE", 255, true);
   public static final SqlColumn OSEE_OAUTH_TOKEN_ID = OSEE_OAUTH_TOKEN_TABLE.addColumn("ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_TOKEN_SUBJECT_ID =
      OSEE_OAUTH_TOKEN_TABLE.addColumn("SUBJECT_ID", JDBCType.BIGINT);
   public static final SqlColumn OSEE_OAUTH_TOKEN_PARENT_TYPE_ID =
      OSEE_OAUTH_TOKEN_TABLE.addColumn("TYPE_ID", JDBCType.SMALLINT);
   public static final SqlColumn OSEE_OAUTH_TOKEN_TOKEN_TYPE =
      OSEE_OAUTH_TOKEN_TABLE.addVarCharColumn("TOKEN_TYPE", 255, true);
   static {
      OSEE_OAUTH_TOKEN_TABLE.setPrimaryKeyConstraint(OSEE_OAUTH_TOKEN_ID);
      OSEE_OAUTH_TOKEN_TABLE.setForeignKeyConstraintCascadeDelete("OSEE_OAUTH_TOKEN__CI_FK", OSEE_OAUTH_TOKEN_CLIENT_ID,
         OSEE_OAUTH_CLIENT_CREDENTIAL_TABLE, OSEE_OAUTH_CLIENT_CREDENTIAL_CLIENT_ID);
      OSEE_OAUTH_TOKEN_TABLE.createIndex("OSEE_OAUTH_TOKEN__TK_IDX", false, OSEE_OAUTH_TOKEN_TOKEN_KEY.getName());
   }

   private final String aliasPrefix;
   private final ObjectType objectType;
   private final ChainingArrayList<@NonNull SqlColumn> columns;
   private final ArrayList<String> constraints;
   private final ArrayList<String> statements;
   private final int indexLevel;
   private final boolean hasJoinTblSp;
   private String tableExtras;

   private SqlTable(String tableName, String aliasPrefix) {
      this(tableName, aliasPrefix, -1);
   }

   private SqlTable(String tableName, String aliasPrefix, int indexLevel) {
      this(tableName, aliasPrefix, ObjectType.UNKNOWN, indexLevel);
   }

   private SqlTable(String tableName, String aliasPrefix, ObjectType objectType) {
      this(tableName, aliasPrefix, objectType, -1);
   }

   private SqlTable(String tableName, String aliasPrefix, ObjectType objectType, int indexLevel) {
      this(tableName, aliasPrefix, objectType, indexLevel, false);
   }

   private SqlTable(String tableName, String aliasPrefix, ObjectType objectType, int indexLevel, boolean hasJoinTblSp) {
      super(tableName);
      this.aliasPrefix = aliasPrefix;
      this.objectType = objectType;
      columns = new ChainingArrayList<>();
      constraints = new ArrayList<>();
      statements = new ArrayList<>();
      this.indexLevel = indexLevel;
      this.hasJoinTblSp = hasJoinTblSp;
   }

   public String getPrefix() {
      return aliasPrefix;
   }

   public String getTableExtras() {
      return tableExtras;
   }

   public ObjectType getObjectType() {
      return objectType;
   }

   public List<SqlColumn> getColumns() {
      return columns;
   }

   public List<String> getConstraints() {
      return constraints;
   }

   public List<String> getStatements() {
      return statements;
   }

   public int getIndexLevel() {
      return indexLevel;
   }

   public SqlColumn addColumn(String name, JDBCType type) {
      return columns.addAndReturn(new SqlColumn(this, name, type));
   }

   public SqlColumn addColumn(String name, JDBCType type, boolean isNull) {
      return columns.addAndReturn(new SqlColumn(this, name, type, isNull));
   }

   public SqlColumn addVarCharColumn(String name, int length) {
      return columns.addAndReturn(new SqlColumn(this, name, JDBCType.VARCHAR, true, length));
   }

   public SqlColumn addVarCharColumn(String name, int length, boolean isNull) {
      return columns.addAndReturn(new SqlColumn(this, name, JDBCType.VARCHAR, false, length));
   }

   public void setPrimaryKeyConstraint(SqlColumn... columns) {
      constraints.add(
         "CONSTRAINT " + getName() + "_PK PRIMARY KEY (" + Collections.toString(",", Arrays.asList(columns)) + ")");
   }

   public void setPrimaryKeyConstraint(String key, SqlColumn... columns) {
      constraints.add(
         "CONSTRAINT " + key + "_PK PRIMARY KEY (" + Collections.toString(",", Arrays.asList(columns)) + ")");
   }

   public void setForeignKeyConstraint(String constraintName, SqlColumn column, SqlTable refTable, SqlColumn refColumn) {
      constraints.add(
         "CONSTRAINT " + constraintName + " FOREIGN KEY (" + column + ") REFERENCES " + refTable + " (" + refColumn + ")");
   }

   public void setForeignKeyConstraintCascadeDelete(String constraintName, SqlColumn column, SqlTable refTable, SqlColumn refColumn) {
      constraints.add(
         "CONSTRAINT " + constraintName + " FOREIGN KEY (" + column + ") REFERENCES " + refTable + " (" + refColumn + ") ON DELETE CASCADE");
   }

   public void setUniqueKeyConstraint(String constraintName, String columnName) {
      constraints.add("CONSTRAINT " + constraintName + " UNIQUE (" + columnName + ")");
   }

   public void createIndex(String indexName, boolean hasIndexTablespace, String... columns) {
      if (hasIndexTablespace) {
         addStatement("CREATE INDEX " + indexName + " ON " + getName() + " (" + Collections.toString(",",
            Arrays.asList(columns)) + ")");
      } else {
         addStatement("CREATE INDEX " + indexName + " ON " + getName() + " (" + Collections.toString(",",
            Arrays.asList(columns)) + ")");
      }
   }

   public void addStatement(String statement) {
      statements.add(statement);
   }

   private void setTableExtras(String extras) {
      tableExtras = extras;
   }

   public static SqlTable getTxsTable(boolean isArchived) {
      return isArchived ? TXS_ARCHIVED_TABLE : TXS_TABLE;
   }

}