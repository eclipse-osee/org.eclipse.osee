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
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.search.engine.utility.SearchTagQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public final class AttributeDataStore {

   private static final String LOAD_ATTRIBUTE =
      "SELECT attr1.gamma_id, attr1.VALUE, attr1.uri, attr1.attr_type_id FROM osee_attribute attr1, osee_tag_gamma_queue tgq1 WHERE attr1.gamma_id = tgq1.gamma_id AND tgq1.query_id = ?";

   private static final String GET_TAGGABLE_SQL_BODY =
      " FROM osee_attribute attr1, osee_attribute_type type1, osee_txs txs1 WHERE txs1.gamma_id = attr1.gamma_id AND attr1.attr_type_id = type1.attr_type_id AND type1.tagger_id IS NOT NULL";

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES =
      "SELECT DISTINCT attr1.gamma_id, type1.tagger_id" + GET_TAGGABLE_SQL_BODY;
   private static final String COUNT_TAGGABLE_ATTRIBUTES =
      "SELECT count(DISTINCT attr1.gamma_id)" + GET_TAGGABLE_SQL_BODY;

   private static final String RESTRICT_BY_BRANCH = " AND txs1.branch_id = ?";

   private static final SearchTagQueryBuilder searchTagQueryBuilder = new SearchTagQueryBuilder();

   private AttributeDataStore() {
      // Utility Class
   }

   public static Collection<AttributeData> getAttribute(final OseeConnection connection, final int tagQueueQueryId) throws OseeDataStoreException {
      final Collection<AttributeData> attributeData = new ArrayList<AttributeData>();

      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      try {
         chStmt.runPreparedQuery(LOAD_ATTRIBUTE, tagQueueQueryId);
         while (chStmt.next()) {
            attributeData.add(new AttributeData(chStmt.getLong("gamma_id"), chStmt.getString("value"),
               chStmt.getString("uri"), chStmt.getInt("attr_type_id")));
         }
      } finally {
         chStmt.close();
      }

      return attributeData;
   }

   public static Set<AttributeData> getAttributesByTags(int branchId, DeletionFlag deletionFlag, final Collection<Long> tagData, final AttributeType... attributeTypes) throws OseeCoreException {
      final Set<AttributeData> toReturn = new HashSet<AttributeData>();

      IdJoinQuery oseeIdJoin = null;
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      boolean useAttrTypeJoin = attributeTypes.length > 1;

      try {
         String sqlQuery =
            searchTagQueryBuilder.getQuery(tagData.size(), useAttrTypeJoin, branchId, deletionFlag.areDeletedAllowed());
         List<Object> params = new ArrayList<Object>();
         params.addAll(tagData);

         if (attributeTypes.length == 1) {
            sqlQuery += " and attr1.attr_type_id = ?";
         } else if (useAttrTypeJoin) {
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
            toReturn.add(new AttributeData(chStmt.getInt("art_id"), chStmt.getLong("gamma_id"),
               chStmt.getInt("branch_id"), chStmt.getString("value"), chStmt.getString("uri"),
               chStmt.getInt("attr_type_id")));
         }
      } finally {
         chStmt.close();
         if (useAttrTypeJoin) {
            oseeIdJoin.delete();
         }
      }
      return toReturn;
   }

   public static String getAllTaggableGammasByBranchQuery(final int branchId) {
      return getBranchTaggingQueries(branchId, false);
   }

   public static Object[] getAllTaggableGammasByBranchQueryData(final int branchId) {
      return branchId > -1 ? new Object[] {branchId} : new Object[0];
   }

   private static String getBranchTaggingQueries(final int branchId, final boolean isCountQuery) {
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
