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
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.search.engine.Options;
import org.eclipse.osee.framework.search.engine.data.AttributeVersion;
import org.eclipse.osee.framework.search.engine.data.IAttributeLocator;
import org.eclipse.osee.framework.search.engine.data.SearchTag;

/**
 * @author Roberto E. Escobar
 */
public class SearchTagDataStore {

   private static final String BASE_INSERT_FOR_SEARCH_TAG =
         "INSERT INTO osee.osee_search_tags (attr_id, gamma_id, coded_tag_id) select ?, ?, ? %s where 0 = (select count(1) from osee.osee_search_tags where attr_id=? and gamma_id=? and coded_tag_id=?)";

   // Initialized at runtime based on connection
   private static String INSERT_SEARCH_TAG = null;

   private static final String DELETE_SEARCH_TAGS = "delete from osee_search_tags where attr_id = ? and gamma_id = ?";

   private static final String SELECT_SEARCH_TAGS =
         "select ost1.attr_id, ost1.gamma_id from osee_search_tags ost1 where ost1.coded_tag_id = ?";

   private static String getQuery(Options options) {
      // TODO different query based on options
      return SELECT_SEARCH_TAGS;
   }

   public static int deleteTags(Collection<IAttributeLocator> locators) throws Exception {
      return deleteTags(locators.toArray(new IAttributeLocator[locators.size()]));
   }

   public static int deleteTags(IAttributeLocator... locators) throws SQLException {
      int updated = 0;
      Connection connection = null;
      updated = -1;
      try {
         connection = OseeDbConnection.getConnection();
         List<Object[]> datas = new ArrayList<Object[]>();
         for (IAttributeLocator locator : locators) {
            datas.add(new Object[] {SQL3DataType.INTEGER, locator.getAttrId(), SQL3DataType.BIGINT,
                  locator.getGammaId()});
         }
         updated = ConnectionHandler.runPreparedUpdate(connection, DELETE_SEARCH_TAGS, datas);
      } finally {
         if (connection != null && connection.isClosed() != true) {
            connection.close();
         }
      }
      return updated;
   }

   public static int storeTags(Collection<SearchTag> searchTags) throws Exception {
      return storeTags(searchTags.toArray(new SearchTag[searchTags.size()]));
   }

   private static void checkSQLInitialized(Connection connection) throws SQLException {
      if (INSERT_SEARCH_TAG == null) {
         String value = "";
         String dbName = connection.getMetaData().getDatabaseProductName().toLowerCase();
         if (dbName.contains("mysql") || dbName.contains("oracle")) {
            value = "FROM DUAL";
         } else if (dbName.contains("derby")) {
            value = "FROM SYSIBM.SYSDUMMY1";
         }
         // Postgres doesn't require dummy table
         INSERT_SEARCH_TAG = String.format(BASE_INSERT_FOR_SEARCH_TAG, value);
      }
   }

   public static int storeTags(SearchTag... searchTags) throws SQLException {
      int updated = 0;
      if (searchTags != null && searchTags.length > 0) {
         Connection connection = null;
         updated = -1;
         try {
            List<Object[]> data = new ArrayList<Object[]>();
            for (SearchTag searchTag : searchTags) {
               for (Long codedTag : searchTag.getTags()) {
                  data.add(new Object[] {SQL3DataType.INTEGER, searchTag.getAttrId(), SQL3DataType.BIGINT,
                        searchTag.getGammaId(), SQL3DataType.BIGINT, codedTag, SQL3DataType.INTEGER,
                        searchTag.getAttrId(), SQL3DataType.BIGINT, searchTag.getGammaId(), SQL3DataType.BIGINT,
                        codedTag});
               }
            }
            connection = OseeDbConnection.getConnection();
            checkSQLInitialized(connection);
            updated = ConnectionHandler.runPreparedUpdate(connection, INSERT_SEARCH_TAG, data);
         } finally {
            if (connection != null && connection.isClosed() != true) {
               connection.close();
            }
         }
      }
      return updated;
   }

   public static Set<IAttributeLocator> fetchTagEntries(Options options, Collection<Long> codedTags) throws Exception {
      return fetchTagEntries(options, codedTags.toArray(new Long[codedTags.size()]));
   }

   public static Set<IAttributeLocator> fetchTagEntries(Options options, Long... codedTags) throws Exception {
      final Set<IAttributeLocator> toReturn = new HashSet<IAttributeLocator>();
      for (Long codedTag : codedTags) {
         DatabaseUtil.executeQuery(getQuery(options), new IRowProcessor() {
            @Override
            public void processRow(ResultSet resultSet) throws Exception {
               toReturn.add(new AttributeVersion(resultSet.getInt("attr_id"), resultSet.getLong("gamma_id")));
            }
         }, SQL3DataType.BIGINT, codedTag);
      }
      return toReturn;
   }
}
