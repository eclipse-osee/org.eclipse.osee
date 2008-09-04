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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.type.MutableDouble;
import org.eclipse.osee.framework.search.engine.data.AttributeVersion;
import org.eclipse.osee.framework.search.engine.data.IAttributeLocator;
import org.eclipse.osee.framework.search.engine.data.SearchTag;

/**
 * @author Roberto E. Escobar
 */
public class SearchTagDataStore {

   private static String INSERT_SEARCH_TAG_BODY =
         "insert into osee_search_tags (gamma_id, coded_tag_id) select ?, ? %s where not exists (select 1 from osee_search_tags ost1 where ost1.gamma_id = ? and ost1.coded_tag_id = ?)";

   private static final String DELETE_SEARCH_TAGS = "delete from osee_search_tags where gamma_id = ?";

   private static final String DELETE_SEARCH_TAGS_BY_JOIN =
         "delete from osee_search_tags ost1 where EXISTS (select 1 from osee_join_transaction ojt1 where ost1.gamma_id = ojt1.gamma_id AND ojt1.query_id = ?)";

   private static final String SELECT_TOTAL_TAGS = "select count(1) from osee_search_tags";

   private static final String SELECT_TOTAL_QUERY_IDS_IN_QUEUE =
         "select count(DISTINCT query_id) from osee_tag_gamma_queue";

   private static final String SELECT_SEARCH_TAGS =
         "select ost1.gamma_id from osee_search_tags ost1 where ost1.coded_tag_id = ?";

   public static long getTotalQueryIdsInQueue() {
      final MutableDouble toReturn = new MutableDouble(-1);
      try {
         DatabaseUtil.executeQueryInternalConnection(SELECT_TOTAL_QUERY_IDS_IN_QUEUE, new IRowProcessor() {

            @Override
            public void processRow(ResultSet resultSet) throws Exception {
               toReturn.setValue(resultSet.getLong(1));
            }
         });
      } catch (Exception ex) {
         // Do Nothing
      }
      return (long) toReturn.getValue();
   }

   public static long getTotalTags() {
      final MutableDouble toReturn = new MutableDouble(-1);
      try {
         DatabaseUtil.executeQueryInternalConnection(SELECT_TOTAL_TAGS, new IRowProcessor() {

            @Override
            public void processRow(ResultSet resultSet) throws Exception {
               toReturn.setValue(resultSet.getLong(1));
            }

         });
      } catch (Exception ex) {
         // Do Nothing
      }
      return (long) toReturn.getValue();
   }

   private static String getInsertSQL(Connection connection) throws SQLException {
      String dummyTable = "";
      String dbName = connection.getMetaData().getDatabaseProductName().toLowerCase();
      if (dbName.contains("oracle") || dbName.contains("mysql")) {
         dummyTable = "FROM DUAL";
      } else if (dbName.contains("derby")) {
         dummyTable = "FROM sysibm.sysdummy1"; // 
      }
      return String.format(INSERT_SEARCH_TAG_BODY, dummyTable);
   }

   public static int deleteTags(Connection connection, Collection<IAttributeLocator> locators) throws Exception {
      return deleteTags(connection, locators.toArray(new IAttributeLocator[locators.size()]));
   }

   public static int deleteTags(Connection connection, IAttributeLocator... locators) throws SQLException {
      List<Object[]> datas = new ArrayList<Object[]>();
      for (IAttributeLocator locator : locators) {
         datas.add(new Object[] {locator.getGammaId()});
      }
      return ConnectionHandler.runPreparedUpdate(connection, DELETE_SEARCH_TAGS, datas);
   }

   public static int storeTags(Connection connection, Collection<SearchTag> searchTags) throws SQLException {
      return storeTags(connection, searchTags.toArray(new SearchTag[searchTags.size()]));
   }

   public static int storeTags(Connection connection, SearchTag... searchTags) throws SQLException {
      int updated = 0;
      if (searchTags != null && searchTags.length > 0) {
         for (SearchTag searchTag : searchTags) {
            List<Object[]> data = new ArrayList<Object[]>();
            for (Long codedTag : searchTag.getTags()) {
               data.add(new Object[] {searchTag.getGammaId(), codedTag, searchTag.getGammaId(), codedTag});
            }
            updated += ConnectionHandler.runPreparedUpdate(connection, getInsertSQL(connection), data);
         }
      }
      return updated;
   }

   public static Set<IAttributeLocator> fetchTagEntries(Connection connection, Collection<Long> codedTags) throws Exception {
      return fetchTagEntries(connection, codedTags.toArray(new Long[codedTags.size()]));
   }

   public static Set<IAttributeLocator> fetchTagEntries(Connection connection, Long... codedTags) throws Exception {
      final Set<IAttributeLocator> toReturn = new HashSet<IAttributeLocator>();
      for (Long codedTag : codedTags) {
         DatabaseUtil.executeQuery(connection, SELECT_SEARCH_TAGS, new IRowProcessor() {
            @Override
            public void processRow(ResultSet resultSet) throws Exception {
               toReturn.add(new AttributeVersion(resultSet.getLong("gamma_id")));
            }
         }, codedTag);
      }
      return toReturn;
   }

   public static int deleteTags(Connection connection, int joinQueryId) throws SQLException {
      return ConnectionHandler.runPreparedUpdate(connection, DELETE_SEARCH_TAGS_BY_JOIN, joinQueryId);
   }
}
