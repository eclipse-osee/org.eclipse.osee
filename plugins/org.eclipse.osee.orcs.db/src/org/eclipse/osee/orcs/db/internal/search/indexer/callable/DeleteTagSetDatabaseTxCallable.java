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
package org.eclipse.osee.orcs.db.internal.search.indexer.callable;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;

/**
 * @author Roberto E. Escobar
 */
public final class DeleteTagSetDatabaseTxCallable extends AbstractDatastoreTxCallable<Integer> {

   private static final String DELETE_SEARCH_TAGS = "delete from osee_search_tags where gamma_id = ?";

   private static final String SELECT_GAMMAS_FROM_TX_JOIN =
      "select gamma_id from osee_join_transaction where query_id = ?";

   private final int queryId;

   public DeleteTagSetDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, int queryId) {
      super(logger, session, jdbcClient);
      this.queryId = queryId;
   }

   @Override
   protected Integer handleTxWork(JdbcConnection connection)  {
      int numberDeleted = 0;
      JdbcStatement chStmt = getJdbcClient().getStatement(connection);
      try {
         chStmt.runPreparedQuery(SELECT_GAMMAS_FROM_TX_JOIN, queryId);
         List<Object[]> datas = new ArrayList<>();
         while (chStmt.next()) {
            datas.add(new Object[] {chStmt.getLong("gamma_id")});
         }
         if (!datas.isEmpty()) {
            numberDeleted = getJdbcClient().runBatchUpdate(connection, DELETE_SEARCH_TAGS, datas);
         }
      } finally {
         chStmt.close();
      }
      return numberDeleted;
   }
}