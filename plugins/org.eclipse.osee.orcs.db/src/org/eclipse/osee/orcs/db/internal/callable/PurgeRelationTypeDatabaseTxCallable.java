/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * Purges given relation types.<br/>
 * <p>
 * Tables involved:
 * <li>osee_txs</li>
 * <li>osee_txs_archived</li>
 * <li>osee_relation_link</li>
 * </p>
 * <br/>
 *
 * @author Karol M. Wilk
 */
public final class PurgeRelationTypeDatabaseTxCallable extends AbstractDatastoreTxCallable<Void> {
   private static final String RETRIEVE_GAMMAS_OF_REL_LINK_TXS =
      "SELECT rel_link.gamma_id FROM osee_relation_link rel_link WHERE rel_link.rel_link_type_id = ?";

   private static final String DELETE_BY_GAMMAS = "DELETE FROM %s WHERE gamma_id = ?";
   private static final String DELETE_FROM_CONFLICT_TABLE_SOURCE_SIDE =
      "DELETE FROM osee_conflict WHERE source_gamma_id = ?";
   private static final String DELETE_FROM_CONFLICT_TABLE_DEST_SIDE =
      "DELETE FROM osee_conflict WHERE dest_gamma_id = ?";

   private final Collection<? extends IRelationType> typesToPurge;

   public PurgeRelationTypeDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, Collection<? extends IRelationType> typesToPurge) {
      super(logger, session, jdbcClient);
      this.typesToPurge = typesToPurge;
   }

   @Override
   protected Void handleTxWork(JdbcConnection connection) {
      List<Object[]> gammaIds = retrieveGammaIds(connection, typesToPurge);
      processDeletes(connection, gammaIds);
      return null;
   }

   private List<Object[]> retrieveGammaIds(JdbcConnection connection, Collection<? extends IRelationType> types) {
      List<Object[]> gammas = new ArrayList<>(50000);
      JdbcStatement chStmt = getJdbcClient().getStatement(connection);
      try {
         for (IRelationType type : types) {
            chStmt.runPreparedQuery(RETRIEVE_GAMMAS_OF_REL_LINK_TXS, type);
            while (chStmt.next()) {
               gammas.add(new Long[] {chStmt.getLong("gamma_id")});
            }
         }
      } finally {
         chStmt.close();
      }

      return gammas;
   }

   private void processDeletes(JdbcConnection connection, List<Object[]> gammas) {
      getJdbcClient().runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS, "osee_txs"), gammas);
      getJdbcClient().runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS, "osee_txs_archived"), gammas);
      getJdbcClient().runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS, "osee_relation_link"), gammas);
      getJdbcClient().runBatchUpdate(connection, String.format(DELETE_FROM_CONFLICT_TABLE_SOURCE_SIDE), gammas);
      getJdbcClient().runBatchUpdate(connection, String.format(DELETE_FROM_CONFLICT_TABLE_DEST_SIDE), gammas);
   }
}