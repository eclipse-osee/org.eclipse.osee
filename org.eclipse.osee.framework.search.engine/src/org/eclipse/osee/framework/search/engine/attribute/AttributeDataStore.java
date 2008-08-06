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
package org.eclipse.osee.framework.search.engine.attribute;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.search.engine.Options;
import org.eclipse.osee.framework.search.engine.utility.DatabaseUtil;
import org.eclipse.osee.framework.search.engine.utility.IRowProcessor;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataStore {

   private static final String LOAD_ATTRIBUTE =
         "SELECT attr1.gamma_id, attr1.VALUE, attr1.uri, attrtype.tagger_id FROM osee_define_attribute attr1, osee_define_attribute_type attrtype, osee_tag_gamma_queue tgq1 WHERE attrtype.attr_type_id = attr1.attr_type_id AND attr1.gamma_id = tgq1.gamma_id AND tgq1.query_id = ?";

   private static final String RESTRICT_BRANCH = " AND txd1.branch_id = ?";

   private static final String SELECT_ATTRIBUTE_BY_TAG =
         "select attr1.art_id, attr1.gamma_id, attr1.VALUE, attr1.uri, attrtype.tagger_id, txd1.branch_id from osee_define_attribute attr1, osee_define_attribute_type attrtype, osee_search_tags ost1, osee_define_txs txs1, osee_define_tx_details txd1, osee_define_artifact_version artv1, osee_define_txs txs2, osee_define_tx_details txd2, osee.osee_join_search_tags jq1 where attrtype.attr_type_id = attr1.attr_type_id AND ost1.gamma_id = attr1.gamma_id AND ost1.gamma_id = txs1.gamma_id AND txd1.transaction_id = txs1.transaction_id AND txs2.gamma_id = artv1.gamma_id AND artv1.art_id = attr1.art_id AND txd2.transaction_id = txs2.transaction_id AND txd1.branch_id = txd2.branch_id AND ost1.coded_tag_id = jq1.coded_tag_id AND jq1.query_id = ?";

   private static final String GET_TAGGABLE_SQL_BODY =
         " FROM osee_define_attribute attr1, osee_define_attribute_type type1,  osee_define_txs txs1, osee_define_tx_details txd1, osee_define_branch br1 WHERE txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = attr1.gamma_id AND txd1.branch_id = br1.branch_id AND br1.archived <> 1 AND attr1.attr_type_id = type1.attr_type_id AND type1.tagger_id IS NOT NULL";

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES = "SELECT DISTINCT attr1.gamma_id" + GET_TAGGABLE_SQL_BODY;
   private static final String COUNT_TAGGABLE_ATTRIBUTES =
         "SELECT count(DISTINCT attr1.gamma_id)" + GET_TAGGABLE_SQL_BODY;

   private static final String POSTGRESQL_CHECK = " AND type1.tagger_id <> ''";
   private static final String RESTRICT_BY_BRANCH = " AND txd1.branch_id = ?";

   private AttributeDataStore() {
   }

   public static Collection<AttributeData> getAttribute(final int tagQueueQueryId) throws Exception {
      final Collection<AttributeData> attributeData = new ArrayList<AttributeData>();
      DatabaseUtil.executeQuery(LOAD_ATTRIBUTE, new IRowProcessor() {
         @Override
         public void processRow(ResultSet resultSet) throws Exception {
            attributeData.add(new AttributeData(resultSet.getLong("gamma_id"), resultSet.getString("value"),
                  resultSet.getString("uri"), resultSet.getString("tagger_id")));
         }
      }, SQL3DataType.INTEGER, tagQueueQueryId);
      return attributeData;
   }

   private static String getQuery(int branchId, Options options) {
      StringBuilder toReturn = new StringBuilder(SELECT_ATTRIBUTE_BY_TAG);
      if (branchId > -1) {
         toReturn.append(RESTRICT_BRANCH);
      }
      // txs1 is for attributes, txs2 is for artifact
      if (options.getBoolean("include deleted")) {
         toReturn.append(" AND txs1.tx_current = 1 AND txs2.tx_current <> 0");
      } else {
         toReturn.append(" AND txs1.tx_current = 1 AND txs2.tx_current = 1");
      }
      return toReturn.toString();
   }

   public static Set<AttributeData> getAttributesByTags(int branchId, Options options, int queryId) throws Exception {
      final Set<AttributeData> toReturn = new HashSet<AttributeData>();
      String sqlQuery = getQuery(branchId, options);
      int dataSize = branchId > -1 ? 4 : 2;
      Object[] data = new Object[dataSize];
      data[0] = SQL3DataType.INTEGER;
      data[1] = queryId;
      if (branchId > -1) {
         data[2] = SQL3DataType.INTEGER;
         data[3] = branchId;
      }
      DatabaseUtil.executeQuery(sqlQuery, new IRowProcessor() {
         @Override
         public void processRow(ResultSet resultSet) throws Exception {
            toReturn.add(new AttributeData(resultSet.getInt("art_id"), resultSet.getLong("gamma_id"),
                  resultSet.getInt("branch_id"), resultSet.getString("value"), resultSet.getString("uri"),
                  resultSet.getString("tagger_id")));
         }
      }, data);
      return toReturn;
   }

   public static String getAllTaggableGammasByBranchQuery(int branchId) throws SQLException {
      return getBranchTaggingQueries(branchId, false);
   }

   public static Object[] getAllTaggableGammasByBranchQueryData(int branchId) {
      return branchId > -1 ? new Object[] {SQL3DataType.INTEGER, branchId} : new Object[0];
   }

   private static String getBranchTaggingQueries(int branchId, boolean isCountQuery) throws SQLException {
      StringBuilder builder = new StringBuilder();
      builder.append(isCountQuery ? COUNT_TAGGABLE_ATTRIBUTES : FIND_ALL_TAGGABLE_ATTRIBUTES);
      Connection connection = null;
      try {
         connection = OseeDbConnection.getConnection();
         if (connection.getMetaData().getDatabaseProductName().toLowerCase().contains("gresql")) {
            builder.append(POSTGRESQL_CHECK);
         }
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
      if (branchId > -1) {
         builder.append(RESTRICT_BY_BRANCH);
      }
      return builder.toString();
   }

   public static int getTotalTaggableItems(int branchId) throws Exception {
      final MutableInteger total = new MutableInteger(-1);
      DatabaseUtil.executeQuery(getBranchTaggingQueries(branchId, true), new IRowProcessor() {
         @Override
         public void processRow(ResultSet resultSet) throws Exception {
            total.setValue(resultSet.getInt(1));
         }
      }, getAllTaggableGammasByBranchQueryData(branchId));
      return total.getValue();
   }
}
