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
package org.eclipse.osee.framework.search.engine.utility;

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;

/**
 * @author Roberto E. Escobar
 */
public final class SearchTagQueryBuilder {
   private static final String RESTRICT_BY_BRANCH = " AND txs1.branch_id = ?";

   private static final CompositeKeyHashMap<Integer, Boolean, String> queryCache =
      new CompositeKeyHashMap<Integer, Boolean, String>();

   public SearchTagQueryBuilder() {
      super();
   }

   public String getQuery(int numberOfTags, boolean useAttrTypeJoin, final int branchId, final boolean includeDeleted) {
      String baseQuery = getTagQuery(numberOfTags, useAttrTypeJoin);

      StringBuilder toReturn = new StringBuilder();
      toReturn.append(baseQuery);
      if (branchId > -1) {
         toReturn.append(RESTRICT_BY_BRANCH);
      }
      // txs1 is for attributes, txs2 is for artifact
      if (includeDeleted) {
         toReturn.append(" AND txs1.tx_current IN (1,3)");
      } else {
         toReturn.append(" AND txs1.tx_current = 1");
      }
      return toReturn.toString();
   }

   private static synchronized String getTagQuery(int numberOfTags, boolean useAttrTypeJoin) {
      String query = queryCache.get(numberOfTags, useAttrTypeJoin);
      if (query == null) {
         query = createTagQuery(numberOfTags, useAttrTypeJoin);
         queryCache.put(numberOfTags, useAttrTypeJoin, query);
      }
      return query;
   }

   private static String createTagQuery(int numberOfTags, boolean useAttrTypeJoin) {
      StringBuilder codedTag = new StringBuilder();
      codedTag.append("SELECT  /*+ ordered FIRST_ROWS */ attr1.art_id, attr1.gamma_id, attr1.value, attr1.uri, attr1.attr_type_id, txs1.branch_id FROM \n");
      for (int index = 0; index < numberOfTags; index++) {
         codedTag.append(String.format("osee_search_tags ost%d, \n", index));
      }
      if (useAttrTypeJoin) {
         codedTag.append(" osee_join_id idj,");
      }
      codedTag.append(" osee_attribute attr1, osee_txs txs1 WHERE \n");

      for (int index = 0; index < numberOfTags; index++) {
         codedTag.append(String.format("ost%d.coded_tag_id = ? AND\n", index));
      }
      for (int index = 1; index < numberOfTags; index++) {
         codedTag.append(String.format("ost%d.gamma_id = ost%d.gamma_id AND \n", index - 1, index));
      }
      codedTag.append(String.format("ost%d.gamma_id = attr1.gamma_id AND\n attr1.gamma_id = txs1.gamma_id\n",
         numberOfTags - 1));

      if (useAttrTypeJoin) {
         codedTag.append(" AND attr1.attr_type_id = idj.id AND idj.query_id = ?\n");
      }
      return codedTag.toString();
   }
}
