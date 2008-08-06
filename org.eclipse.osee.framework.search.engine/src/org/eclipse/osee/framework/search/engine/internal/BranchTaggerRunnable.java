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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TagQueueJoinQuery;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.ITagListener;
import org.eclipse.osee.framework.search.engine.attribute.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.utility.DatabaseUtil;
import org.eclipse.osee.framework.search.engine.utility.IRowProcessor;

/**
 * @author Roberto E. Escobar
 */
public class BranchTaggerRunnable implements Runnable {
   private static int CACHE_LIMIT = 1000;
   private ISearchEngineTagger tagger;
   private int branchId;
   private ITagListener listener;

   BranchTaggerRunnable(ISearchEngineTagger tagger, ITagListener listener, int branchId) {
      this.tagger = tagger;
      this.branchId = branchId;
      this.listener = listener;
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      Connection connection = null;
      TagQueryDispatcher dispatcher = null;
      try {
         connection = OseeDbConnection.getConnection();
         if (listener != null) {
            int totalAttributes = AttributeDataStore.getTotalTaggableItems(branchId);
            int remainder = totalAttributes % 1000;
            int totalQueries = totalAttributes / 1000 + (remainder > 0 ? 1 : 0);
            listener.onTagExpectedQueryIdSubmits(totalQueries);
         }
         dispatcher = new TagQueryDispatcher(connection);
         DatabaseUtil.executeQuery(AttributeDataStore.getAllTaggableGammasByBranchQuery(branchId), dispatcher,
               AttributeDataStore.getAllTaggableGammasByBranchQueryData(branchId));
         dispatcher.dispatchQueryId(connection);
      } catch (Exception ex) {
         if (listener != null) {
            int queryId = -2;
            if (dispatcher != null) {
               queryId = dispatcher.getCurrentJoinQuery();
            }
            listener.onTagError(queryId, ex);
         }
         OseeLog.log(BranchTaggerRunnable.class, Level.SEVERE, ex);
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (SQLException ex) {
               OseeLog.log(BranchTaggerRunnable.class, Level.SEVERE, ex);
            }
         }
      }
   }

   private final class TagQueryDispatcher implements IRowProcessor {
      private TagQueueJoinQuery joinQuery;
      private Connection connection;

      public TagQueryDispatcher(Connection connection) {
         this.joinQuery = null;
         this.connection = connection;
      }

      public int getCurrentJoinQuery() {
         return joinQuery != null ? joinQuery.getQueryId() : -1;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.search.engine.utility.IRowProcessor#processRow(java.sql.ResultSet)
       */
      @Override
      public void processRow(ResultSet resultSet) throws Exception {
         long gammaId = resultSet.getLong("gamma_id");
         if (joinQuery == null) {
            joinQuery = JoinUtility.createTagQueueJoinQuery();
         }
         joinQuery.add(gammaId);
         if (joinQuery.size() >= CACHE_LIMIT) {
            dispatchQueryId(connection);
            joinQuery = null;
         }
      }

      private void dispatchQueryId(Connection connection) throws SQLException {
         if (joinQuery != null && joinQuery.size() > 0) {
            joinQuery.store(connection);
            tagger.tagByQueueQueryId(listener, joinQuery.getQueryId());
         }
      }
   }
}
