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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.search.engine.SearchOptions;
import org.eclipse.osee.framework.search.engine.SearchOptions.SearchOptionsEnum;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataStore {

   private static final String LOAD_ATTRIBUTE = "SELECT attr1.gamma_id, attr1.VALUE, attr1.uri, attr1.attr_type_id FROM osee_attribute attr1, osee_tag_gamma_queue tgq1 WHERE attr1.gamma_id = tgq1.gamma_id AND tgq1.query_id = ?";

   private static final String GET_TAGGABLE_SQL_BODY = " FROM osee_attribute attr1, osee_attribute_type type1, osee_txs txs1 WHERE txs1.gamma_id = attr1.gamma_id AND attr1.attr_type_id = type1.attr_type_id AND type1.tagger_id IS NOT NULL";

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES = "SELECT DISTINCT attr1.gamma_id, type1.tagger_id" + GET_TAGGABLE_SQL_BODY;
   private static final String COUNT_TAGGABLE_ATTRIBUTES = "SELECT count(DISTINCT attr1.gamma_id)" + GET_TAGGABLE_SQL_BODY;

   private static final String RESTRICT_BY_BRANCH = " AND txs1.branch_id = ?";

   private static final CompositeKeyHashMap<Integer, Boolean, String> queryCache = new CompositeKeyHashMap<Integer, Boolean, String>();

   private AttributeDataStore() {
   }

   public static Collection<AttributeData> getAttribute(final OseeConnection connection, final int tagQueueQueryId) throws OseeDataStoreException {
      final Collection<AttributeData> attributeData = new ArrayList<AttributeData>();

      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      try {
         chStmt.runPreparedQuery(LOAD_ATTRIBUTE, tagQueueQueryId);
         while (chStmt.next()) {
            attributeData.add(new AttributeData(chStmt.getLong("gamma_id"), chStmt.getString("value"), chStmt.getString("uri"),
                                                chStmt.getInt("attr_type_id")));
         }
      }
      finally {
         chStmt.close();
      }

      return attributeData;
   }

   private static String getAttributeTagQuery(int numberOfTags, boolean useAttrTypeJoin) {
      String query = queryCache.get(numberOfTags, useAttrTypeJoin);
      if (query == null) {
         StringBuilder codedTag = new StringBuilder();
         codedTag.append("SELECT  /*+ ordered FIRST_ROWS */ attr1.art_id, attr1.gamma_id, attr1.value, attr1.uri, attr1.attr_type_id, txs1.branch_id FROM \n");
         for (int index = 0; index < numberOfTags; index++) {
            codedTag.append(String.format("osee_search_tags ost%d, \n", index));
         }
         codedTag.append("osee_attribute attr1,");
         if (useAttrTypeJoin) {
            codedTag.append(" osee_join_id idj,");
         }
         codedTag.append(" osee_txs txs1 WHERE \n");

         for (int index = 0; index < numberOfTags; index++) {
            codedTag.append(String.format("ost%d.coded_tag_id = ? and\n", index));
         }
         for (int index = 1; index < numberOfTags; index++) {
            codedTag.append(String.format("ost%d.gamma_id = ost%d.gamma_id and \n", index - 1, index));
         }
         codedTag.append(String.format("ost%d.gamma_id = attr1.gamma_id and\n attr1.gamma_id = txs1.gamma_id \n", numberOfTags - 1));

         if (useAttrTypeJoin) {
            codedTag.append(" and attr1.attr_type_id = idj.id and idj.query_id = ? ");
         }
         query = codedTag.toString();
         queryCache.put(numberOfTags, useAttrTypeJoin, query);
      }
      return query;
   }

   private static String getQuery(final String baseQuery, final int branchId, final SearchOptions options) {
      StringBuilder toReturn = new StringBuilder(baseQuery);
      if (branchId > -1) {
         toReturn.append(RESTRICT_BY_BRANCH);
      }
      // txs1 is for attributes, txs2 is for artifact
      if (options.getBoolean(SearchOptionsEnum.include_deleted.asStringOption())) {
         toReturn.append(" AND txs1.tx_current IN (1,3)");
      }
      else {
         toReturn.append(" AND txs1.tx_current = 1");
      }
      return toReturn.toString();
   }

   public static void main(String[] args) {
      for (int index = 1; index < 13; index++) {
         System.out.println("\n------------------------------------------------------------");
         System.out.println(getQuery(getAttributeTagQuery(index, false), 2, new SearchOptions()));
         System.out.println("\n------------------------------------------------------------");
         System.out.println(getQuery(getAttributeTagQuery(index, true), 2, new SearchOptions()));
      }

      for (int index = 1; index < 13; index++) {
         System.out.println("\n------------------------------------------------------------");
         System.out.println(getQuery(getAttributeTagQuery(index, false), 2, new SearchOptions()));
         System.out.println("\n------------------------------------------------------------");
         System.out.println(getQuery(getAttributeTagQuery(index, true), 2, new SearchOptions()));
      }
   }

   public static Set<AttributeData> getAttributesByTags(final int branchId, final SearchOptions options, final Collection<Long> tagData,
         final AttributeType... attributeTypes) throws OseeCoreException {
      final Set<AttributeData> toReturn = new HashSet<AttributeData>();
      IdJoinQuery oseeIdJoin = null;
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      boolean useAttrTypeJoin = attributeTypes.length > 1;

      try {
         String sqlQuery = getQuery(getAttributeTagQuery(tagData.size(), useAttrTypeJoin), branchId, options);
         List<Object> params = new ArrayList<Object>();
         params.addAll(tagData);

         if (attributeTypes.length == 1) {
            sqlQuery += " and attr1.attr_type_id = ?";
         }
         else if (useAttrTypeJoin) {
            oseeIdJoin = JoinUtility.createIdJoinQuery();
            for (AttributeType attributeType : attributeTypes) {
               oseeIdJoin.add(attributeType.getId());
            }
            oseeIdJoin.store();
            params.add(oseeIdJoin.getQueryId());
         }

         if (branchId > -1) {
            params.add(branchId);
         }
         if (attributeTypes.length == 1) {
            params.add(attributeTypes[0].getId());
         }

         chStmt.runPreparedQuery(sqlQuery, params.toArray(new Object[params.size()]));
         while (chStmt.next()) {
            toReturn.add(new AttributeData(chStmt.getInt("art_id"), chStmt.getLong("gamma_id"), chStmt.getInt("branch_id"),
                                           chStmt.getString("value"), chStmt.getString("uri"), chStmt.getInt("attr_type_id")));
         }
      }
      finally {
         chStmt.close();
         if (useAttrTypeJoin) {
            oseeIdJoin.delete();
         }
      }
      return toReturn;
   }

   public static String getAllTaggableGammasByBranchQuery(final int branchId) throws OseeDataStoreException {
      return getBranchTaggingQueries(branchId, false);
   }

   public static Object[] getAllTaggableGammasByBranchQueryData(final int branchId) {
      return branchId > -1 ? new Object[] {branchId} : new Object[0];
   }

   private static String getBranchTaggingQueries(final int branchId, final boolean isCountQuery) throws OseeDataStoreException {
      StringBuilder builder = new StringBuilder();
      builder.append(isCountQuery ? COUNT_TAGGABLE_ATTRIBUTES : FIND_ALL_TAGGABLE_ATTRIBUTES);
      if (branchId > -1) {
         builder.append(RESTRICT_BY_BRANCH);
      }
      return builder.toString();
   }

   public static int getTotalTaggableItems(OseeConnection connection, final int branchId) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(connection, -1, getBranchTaggingQueries(branchId, true),
                                                        getAllTaggableGammasByBranchQueryData(branchId));
   }
}
