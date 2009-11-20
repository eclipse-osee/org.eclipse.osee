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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.search.engine.data.AttributeVersion;
import org.eclipse.osee.framework.search.engine.data.IAttributeLocator;
import org.eclipse.osee.framework.search.engine.data.SearchTag;

/**
 * @author Roberto E. Escobar
 */
public class SearchTagDataStore {

   private static String INSERT_SEARCH_TAG_BODY = "insert into osee_search_tags (gamma_id, coded_tag_id) values (?, ?)";

   private static final String DELETE_SEARCH_TAGS = "delete from osee_search_tags where gamma_id = ?";

   private static final String SELECT_TOTAL_TAGS = "select count(1) from osee_search_tags";

   private static final String SELECT_TOTAL_QUERY_IDS_IN_QUEUE =
         "select count(DISTINCT query_id) from osee_tag_gamma_queue";

   private static final String SELECT_SEARCH_TAGS =
         "select ost1.gamma_id from osee_search_tags ost1 where ost1.coded_tag_id = ?";

   public static long getTotalQueryIdsInQueue() throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchLong(-1L, SELECT_TOTAL_QUERY_IDS_IN_QUEUE);
   }

   public static long getTotalTags() throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchLong(-1L, SELECT_TOTAL_TAGS);
   }

   public static int deleteTags(OseeConnection connection, int queryId) throws OseeDataStoreException {
      int numberDeleted = 0;
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      try {
         chStmt.runPreparedQuery("select gamma_id from osee_join_transaction where query_id = ?", queryId);
         List<Object[]> datas = new ArrayList<Object[]>();
         while (chStmt.next()) {
            datas.add(new Object[] {chStmt.getLong("gamma_id")});
         }
         if (!datas.isEmpty()) {
            numberDeleted = ConnectionHandler.runBatchUpdate(connection, DELETE_SEARCH_TAGS, datas);
         }
      } finally {
         chStmt.close();
      }
      return numberDeleted;
   }

   public static int deleteTags(OseeConnection connection, Collection<IAttributeLocator> locators) throws OseeDataStoreException {
      return deleteTags(connection, locators.toArray(new IAttributeLocator[locators.size()]));
   }

   public static int deleteTags(OseeConnection connection, IAttributeLocator... locators) throws OseeDataStoreException {
      int numberDeleted = 0;
      if (locators.length > 0) {
         List<Object[]> datas = new ArrayList<Object[]>();
         for (IAttributeLocator locator : locators) {
            datas.add(new Object[] {locator.getGammaId()});
         }
         numberDeleted = ConnectionHandler.runBatchUpdate(connection, DELETE_SEARCH_TAGS, datas);
      }
      return numberDeleted;
   }

   public static int storeTags(OseeConnection connection, Collection<SearchTag> searchTags) throws OseeDataStoreException {
      return storeTags(connection, searchTags.toArray(new SearchTag[searchTags.size()]));
   }

   public static int storeTags(OseeConnection connection, SearchTag... searchTags) throws OseeDataStoreException {
      int updated = 0;
      if (searchTags != null && searchTags.length > 0) {
         for (SearchTag searchTag : searchTags) {
            List<Object[]> data = new ArrayList<Object[]>();
            for (Long codedTag : searchTag.getTags()) {
               data.add(new Object[] {searchTag.getGammaId(), codedTag});
            }
            updated += ConnectionHandler.runBatchUpdate(connection, INSERT_SEARCH_TAG_BODY, data);
         }
      }
      return updated;
   }

   public static Set<IAttributeLocator> fetchTagEntries(OseeConnection connection, Collection<Long> codedTags) throws Exception {
      return fetchTagEntries(connection, codedTags.toArray(new Long[codedTags.size()]));
   }

   public static Set<IAttributeLocator> fetchTagEntries(OseeConnection connection, Long... codedTags) throws OseeDataStoreException {
      final Set<IAttributeLocator> toReturn = new HashSet<IAttributeLocator>();

      for (Long codedTag : codedTags) {
         IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
         try {
            chStmt.runPreparedQuery(SELECT_SEARCH_TAGS, codedTag);
            while (chStmt.next()) {
               toReturn.add(new AttributeVersion(chStmt.getLong("gamma_id")));
            }
         } finally {
            chStmt.close();
         }
      }

      return toReturn;
   }
}
