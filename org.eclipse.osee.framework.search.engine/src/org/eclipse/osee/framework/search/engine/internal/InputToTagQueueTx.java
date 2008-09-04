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
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TagQueueJoinQuery;
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.ITagListener;

/**
 * @author Roberto E. Escobar
 */
public abstract class InputToTagQueueTx extends DbTransaction {

   private final ITagListener listener;
   private final int cacheLimit;
   private final boolean isCacheAll;
   private final List<Integer> queryIds;
   private final ISearchEngineTagger tagger;
   private TagQueueJoinQuery currentJoinQuery;
   private boolean isOkToDispatch;

   InputToTagQueueTx(ISearchEngineTagger tagger, ITagListener listener, boolean isCacheAll, int cacheLimit) {
      this.tagger = tagger;
      this.listener = listener;
      this.cacheLimit = cacheLimit;
      this.isCacheAll = isCacheAll;
      this.queryIds = new ArrayList<Integer>();
      this.isOkToDispatch = false;
      this.currentJoinQuery = null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction#handleTxWork(java.sql.Connection)
    */
   @Override
   protected void handleTxWork(Connection connection) throws Exception {
      convertInput(connection);
      storeQueryIds(connection);
      if (listener != null) {
         listener.onTagExpectedQueryIdSubmits(this.queryIds.size());
      }
      this.isOkToDispatch = true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction#handleTxException(java.lang.Exception)
    */
   @Override
   protected void handleTxException(Exception ex) throws Exception {
      this.isOkToDispatch = false;
      if (listener != null) {
         if (queryIds.isEmpty()) {
            listener.onTagError(-2, ex);
         } else {
            for (Integer queryId : queryIds) {
               listener.onTagError(queryId, ex);
            }
         }
      }
      throw new Exception(String.format("Error during [%s] - ", this.getClass()), ex);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.DbTransaction#handleTxFinally()
    */
   @Override
   protected void handleTxFinally() throws Exception {
      super.handleTxFinally();
      if (this.isOkToDispatch) {
         for (int queryId : queryIds) {
            tagger.tagByQueueQueryId(listener, queryId);
         }
      }
   }

   protected void addEntry(Connection connection, long gammaId) throws SQLException {
      if (currentJoinQuery == null) {
         currentJoinQuery = JoinUtility.createTagQueueJoinQuery();
      }
      currentJoinQuery.add(gammaId);
      if (isStorageNeeded()) {
         storeQueryIds(connection);
      }
   }

   private boolean isStorageNeeded() {
      return this.isCacheAll != true && this.currentJoinQuery != null && this.currentJoinQuery.size() > this.cacheLimit;
   }

   private void storeQueryIds(Connection connection) throws SQLException {
      if (currentJoinQuery != null && !currentJoinQuery.isEmpty()) {
         currentJoinQuery.store(connection);
         queryIds.add(currentJoinQuery.getQueryId());
      }
      currentJoinQuery = null;
   }

   abstract protected void convertInput(Connection connection) throws Exception;
}
