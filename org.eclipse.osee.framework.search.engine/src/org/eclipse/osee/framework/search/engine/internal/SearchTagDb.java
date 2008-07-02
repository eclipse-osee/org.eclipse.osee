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
package org.eclipse.osee.framework.search.engine.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.search.engine.Options;
import org.eclipse.osee.framework.search.engine.utility.IRowProcessor;

/**
 * @author Roberto E. Escobar
 */
public class SearchTagDb {

   private static final String INSERT_SEARCH_TAG =
         "insert into osee_search_tags ost1 (ost1.attr_id, ost1.gamma_id, ost1.coded_tag_id) values (?,?,?)";

   private static final String DELETE_SEARCH_TAGS =
         "delete from osee_search_tags ost1 where ost1.attr_id = ? and ost1.gamma_id = ?";

   private static final String SELECT_SEARCH_TAGS =
         "select ost1.attr_id, ost1.gamma_id from osee_search_tags ost1 where ost1.coded_tag_id = ?";

   public static String getQuery(Options options) {
      return SELECT_SEARCH_TAGS;
   }

   public static int deleteTags(AttributeVersion... attributeVersions) throws SQLException {
      int updated = 0;
      Connection connection = null;
      updated = -1;
      try {
         connection = OseeDbConnection.getConnection();
         List<Object[]> datas = new ArrayList<Object[]>();
         for (AttributeVersion attributeVersion : attributeVersions) {
            datas.add(attributeVersion.toArray());
         }
         updated = ConnectionHandler.runPreparedUpdate(connection, DELETE_SEARCH_TAGS, datas);
      } finally {
         if (connection != null && connection.isClosed() != true) {
            connection.close();
         }
      }
      return updated;
   }

   public static int storeTags(SearchTag searchTag) throws SQLException {
      int updated = 0;
      if (searchTag.size() > 0) {
         Connection connection = null;
         updated = -1;
         try {
            connection = OseeDbConnection.getConnection();
            updated = ConnectionHandler.runPreparedUpdate(connection, INSERT_SEARCH_TAG, searchTag.toList());
         } finally {
            if (connection != null && connection.isClosed() != true) {
               connection.close();
            }
         }
      }
      return updated;
   }

   public static void executeQuery(String sql, List<Object[]> datas, IRowProcessor processor) throws Exception {
      Connection connection = null;
      ConnectionHandlerStatement chStmt = null;
      try {
         connection = OseeDbConnection.getConnection();
         chStmt = ConnectionHandler.runPreparedQuery(connection, sql, datas);
         while (chStmt.next()) {
            processor.processRow(chStmt.getRset());
         }
      } finally {
         DbUtil.close(chStmt);
         if (connection != null && connection.isClosed() != true) {
            connection.close();
         }
      }
   }
}
