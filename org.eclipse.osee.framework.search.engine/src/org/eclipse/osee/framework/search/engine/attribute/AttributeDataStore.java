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
         "SELECT attr1.art_id, attr1.gamma_id, attr1.VALUE, attr1.uri, attrtype.tagger_id, txd1.branch_id FROM osee.osee_join_search_tags jq1, osee_search_tags ost1, osee_define_attribute attr1, osee_define_txs txs1, osee_define_tx_details txd1, osee_define_attribute_type attrtype WHERE jq1.query_id = ? AND jq1.coded_tag_id = ost1.coded_tag_id AND ost1.gamma_id = attr1.gamma_id AND attr1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND attr1.attr_type_id = attrtype.attr_type_id";

   private static final String GET_TAGGABLE_SQL_BODY =
         " FROM osee_define_attribute attr1, osee_define_attribute_type type1,  osee_define_txs txs1, osee_define_tx_details txd1, osee_define_branch br1 WHERE txs1.transaction_id = txd1.transaction_id AND txs1.gamma_id = attr1.gamma_id AND txd1.branch_id = br1.branch_id AND br1.archived <> 1 AND attr1.attr_type_id = type1.attr_type_id AND type1.tagger_id IS NOT NULL";

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES = "SELECT DISTINCT attr1.gamma_id" + GET_TAGGABLE_SQL_BODY;
   private static final String COUNT_TAGGABLE_ATTRIBUTES =
         "SELECT count(DISTINCT attr1.gamma_id)" + GET_TAGGABLE_SQL_BODY;

   private static final String POSTGRESQL_CHECK = " AND type1.tagger_id <> ''";
   private static final String RESTRICT_BY_BRANCH = " AND txd1.branch_id = ?";

   private AttributeDataStore() {
   }

   public static Collection<AttributeData> getAttribute(final Connection connection, final int tagQueueQueryId) throws Exception {
      final Collection<AttributeData> attributeData = new ArrayList<AttributeData>();
      DatabaseUtil.executeQuery(connection, LOAD_ATTRIBUTE, new IRowProcessor() {
         @Override
         public void processRow(ResultSet resultSet) throws Exception {
            attributeData.add(new AttributeData(resultSet.getLong("gamma_id"), resultSet.getString("value"),
                  resultSet.getString("uri"), resultSet.getString("tagger_id")));
         }
      }, tagQueueQueryId);
      return attributeData;
   }

   private static String getQuery(final int branchId, final Options options) {
      StringBuilder toReturn = new StringBuilder(SELECT_ATTRIBUTE_BY_TAG);
      if (branchId > -1) {
         toReturn.append(RESTRICT_BRANCH);
      }
      // txs1 is for attributes, txs2 is for artifact
      if (options.getBoolean("include deleted")) {
         toReturn.append(" AND txs1.tx_current IN (1,3)");
      } else {
         toReturn.append(" AND txs1.tx_current = 1");
      }

      if (options.getBoolean("name only")) {
         toReturn.append(" AND attrtype.name = 'Name'");
      }

      return toReturn.toString();
   }

   public static Set<AttributeData> getAttributesByTags(final Connection connection, final int branchId, final Options options, final int queryId) throws Exception {
      final Set<AttributeData> toReturn = new HashSet<AttributeData>();
      String sqlQuery = getQuery(branchId, options);
      int dataSize = branchId > -1 ? 2 : 1;
      Object[] data = new Object[dataSize];
      data[0] = queryId;
      if (branchId > -1) {
         data[1] = branchId;
      }
      DatabaseUtil.executeQuery(connection, sqlQuery, new IRowProcessor() {
         @Override
         public void processRow(ResultSet resultSet) throws Exception {
            toReturn.add(new AttributeData(resultSet.getInt("art_id"), resultSet.getLong("gamma_id"),
                  resultSet.getInt("branch_id"), resultSet.getString("value"), resultSet.getString("uri"),
                  resultSet.getString("tagger_id")));
         }
      }, data);
      return toReturn;
   }

   public static String getAllTaggableGammasByBranchQuery(final Connection connection, final int branchId) throws SQLException {
      return getBranchTaggingQueries(connection, branchId, false);
   }

   public static Object[] getAllTaggableGammasByBranchQueryData(final int branchId) {
      return branchId > -1 ? new Object[] {branchId} : new Object[0];
   }

   private static String getBranchTaggingQueries(final Connection connection, final int branchId, final boolean isCountQuery) throws SQLException {
      StringBuilder builder = new StringBuilder();
      builder.append(isCountQuery ? COUNT_TAGGABLE_ATTRIBUTES : FIND_ALL_TAGGABLE_ATTRIBUTES);
      if (connection.getMetaData().getDatabaseProductName().toLowerCase().contains("gresql")) {
         builder.append(POSTGRESQL_CHECK);
      }
      if (branchId > -1) {
         builder.append(RESTRICT_BY_BRANCH);
      }
      return builder.toString();
   }

   public static int getTotalTaggableItems(final Connection connection, final int branchId) throws Exception {
      final MutableInteger total = new MutableInteger(-1);
      DatabaseUtil.executeQuery(connection, getBranchTaggingQueries(connection, branchId, true), new IRowProcessor() {
         @Override
         public void processRow(ResultSet resultSet) throws Exception {
            total.setValue(resultSet.getInt(1));
         }
      }, getAllTaggableGammasByBranchQueryData(branchId));
      return total.getValue();
   }
}
