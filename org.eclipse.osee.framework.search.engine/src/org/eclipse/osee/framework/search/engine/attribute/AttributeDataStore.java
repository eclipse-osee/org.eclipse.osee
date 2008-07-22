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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.search.engine.Options;
import org.eclipse.osee.framework.search.engine.utility.DatabaseUtil;
import org.eclipse.osee.framework.search.engine.utility.IRowProcessor;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataStore {

   private static final String SELECT_ATTRIBUTE =
         "SELECT attr1.art_id, attr1.gamma_id, attr1.value, attr1.uri, attrtype.tagger_id, txd1.branch_id FROM osee_define_attribute attr1, osee_define_attribute_type attrtype, osee_define_txs txs1, osee_define_tx_details txd1 WHERE attr1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND attrtype.attr_type_id = attr1.attr_type_id AND attr1.gamma_id = ?";

   private static final String RESTRICT_BRANCH = " AND txd1.branch_id = ?";

   private static final String SELECT_ATTRIBUTE_BY_TAG =
         "select attr1.art_id, attr1.gamma_id, attr1.VALUE, attr1.uri, attrtype.tagger_id, txd1.branch_id from osee_define_attribute attr1, osee_define_attribute_type attrtype, osee_search_tags ost1, osee_define_txs txs1, osee_define_tx_details txd1, osee_define_artifact_version artv1, osee_define_txs txs2, osee_define_tx_details txd2 where attrtype.attr_type_id = attr1.attr_type_id AND ost1.gamma_id = attr1.gamma_id AND ost1.gamma_id = txs1.gamma_id AND txd1.transaction_id = txs1.transaction_id AND txs2.gamma_id = artv1.gamma_id AND artv1.art_id = attr1.art_id AND txd2.transaction_id = txs2.transaction_id AND txd1.branch_id = txd2.branch_id AND 1 = (SELECT count(1) FROM osee.osee_join_search_tags jq1 WHERE jq1.query_id = ? AND ost1.coded_tag_id = jq1.coded_tag_id)";

   private AttributeDataStore() {
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

   public static AttributeData getAttribute(final long gammaId) throws Exception {
      final List<AttributeData> attributeData = new ArrayList<AttributeData>();
      DatabaseUtil.executeQuery(SELECT_ATTRIBUTE, new IRowProcessor() {
         @Override
         public void processRow(ResultSet resultSet) throws Exception {
            attributeData.add(new AttributeData(resultSet.getInt("art_id"), resultSet.getLong("gamma_id"),
                  resultSet.getInt("branch_id"), resultSet.getString("value"), resultSet.getString("uri"),
                  resultSet.getString("tagger_id")));
         }
      }, SQL3DataType.BIGINT, gammaId);
      return attributeData.isEmpty() != true ? attributeData.get(0) : null;
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
}
