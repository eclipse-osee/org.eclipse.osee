/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.Collection;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

public final class PurgeAttributesDatabaseTxCallable extends AbstractDatastoreTxCallable<Void> {
   private static final String SELECT_ATTRIBUTE_GAMMAS =
      "select gamma_id from osee_attribute, osee_join_id where attr_id = id and query_id = ?";

   private final SqlJoinFactory joinFactory;
   private final Collection<AttributeId> idsToPurge;
   private final Console console;

   public PurgeAttributesDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, SqlJoinFactory joinFactory, Collection<AttributeId> idsToPurge, Console console) {
      super(logger, session, jdbcClient);
      this.joinFactory = joinFactory;
      this.idsToPurge = idsToPurge;
      this.console = console;
   }

   @Override
   protected Void handleTxWork(JdbcConnection connection)  {
      try (IdJoinQuery idJoin = joinFactory.createIdJoinQuery(connection)) {
         OseePreparedStatement attrBatch =
            getJdbcClient().getBatchStatement(connection, "delete from osee_attribute where attr_id = ?");
         OseePreparedStatement txBatch =
            getJdbcClient().getBatchStatement(connection, "delete from osee_txs where gamma_id = ?");

         for (AttributeId id : idsToPurge) {
            idJoin.add(id);
            attrBatch.addToBatch(id);
         }
         idJoin.store();
         getJdbcClient().runQuery(stmt -> txBatch.addToBatch(stmt.getLong("gamma_id")), SELECT_ATTRIBUTE_GAMMAS,
            idJoin.getQueryId());

         writeToConsole("Deleting gammas from osee_txs...");
         int deleted = txBatch.execute();
         writeToConsole(deleted + " rows deleted.");

         // execute after txBatch
         writeToConsole("Deleting attributes from osee_attribute...");
         deleted = attrBatch.execute();
         writeToConsole(deleted + " rows deleted.");
         writeToConsole("Operation Finished");
      }
      return null;
   }

   private void writeToConsole(String s) {
      if (console != null) {
         console.writeln(s);
      }
   }
}