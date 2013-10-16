/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;

/**
 * @author Angel Avila
 */
public final class PurgeAttributeTypeDatabaseTxCallable extends AbstractDatastoreTxCallable<Void> {
   private static final String RETRIEVE_GAMMAS_OF_ATTR_TYPE =
      "select gamma_id from osee_attribute where attr_type_id = ?";

   private static final String RETRIEVE_GAMMAS_OF_ATTR_MULT_TYPES =
      "select gamma_id from osee_attribute attr, osee_join_id jid where attr.attr_type_id = jid.id and jid.query_id = ?";

   private static final String RETRIEVE_GAMMAS_WITH_BRANCH_IDS =
      "select txs.branch_id, txs.gamma_id from osee_attribute attr, osee_txs txs where attr.attr_type_id = ? and txs.gamma_id = attr.gamma_id";

   private static final String RETRIEVE_GAMMAS_WITH_BRANCH_IDS_MULT_TYPES =
      "select txs.branch_id, txs.gamma_id from osee_attribute attr, osee_txs txs, osee_join_id jid where attr.attr_type_id = jid.id and jid.query_id = ? and txs.gamma_id = attr.gamma_id";

   private static final String DELETE_BY_GAMMAS_AND_BRANCH = "DELETE FROM %s WHERE gamma_id = ? AND branch_id = ?";
   private static final String DELETE_BY_GAMMAS = "DELETE FROM osee_attribute WHERE gamma_id = ?";
   private static final String DELETE_FROM_CONFLICT_TABLE_SOURCE_SIDE =
      "DELETE FROM osee_conflict WHERE source_gamma_id = ?";
   private static final String DELETE_FROM_CONFLICT_TABLE_DEST_SIDE =
      "DELETE FROM osee_conflict WHERE dest_gamma_id = ?";

   private final Collection<? extends IAttributeType> typesToPurge;
   private final IdentityLocator identityService;

   public PurgeAttributeTypeDatabaseTxCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, IdentityLocator identityService, Collection<? extends IAttributeType> typesToPurge) {
      super(logger, session, databaseService, "Purge Attribute Type");
      this.identityService = identityService;
      this.typesToPurge = typesToPurge;
   }

   @Override
   protected Void handleTxWork(OseeConnection connection) throws OseeCoreException {
      List<Integer[]> gammasAndBranchIds = retrieveBranchAndGammaIds(connection, typesToPurge);
      List<Integer[]> gammas = retrieveGammaIds(connection, typesToPurge);
      processDeletes(connection, gammasAndBranchIds, gammas);
      return null;
   }

   private List<Integer[]> retrieveGammaIds(OseeConnection connection, Collection<? extends IAttributeType> types) throws OseeCoreException {
      List<Integer[]> gammas = new LinkedList<Integer[]>();
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      IdJoinQuery joinQuery = JoinUtility.createIdJoinQuery();
      try {
         if (types.size() == 1) {
            chStmt.runPreparedQuery(RETRIEVE_GAMMAS_OF_ATTR_TYPE, identityService.getLocalId(types.iterator().next()));
         } else {
            for (IAttributeType type : types) {
               joinQuery.add(identityService.getLocalId(type));
            }
            joinQuery.store(connection);
            chStmt.runPreparedQuery(RETRIEVE_GAMMAS_OF_ATTR_MULT_TYPES, joinQuery.getQueryId());
         }
         while (chStmt.next()) {
            gammas.add(new Integer[] {chStmt.getInt("gamma_id")});
         }
      } finally {
         joinQuery.delete(connection);
         chStmt.close();
      }
      return gammas;
   }

   private List<Integer[]> retrieveBranchAndGammaIds(OseeConnection connection, Collection<? extends IAttributeType> types) throws OseeCoreException {
      List<Integer[]> gammasAndBranchIds = new LinkedList<Integer[]>();
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      IdJoinQuery joinQuery = JoinUtility.createIdJoinQuery();
      try {
         if (types.size() == 1) {
            chStmt.runPreparedQuery(RETRIEVE_GAMMAS_WITH_BRANCH_IDS,
               identityService.getLocalId(types.iterator().next()));
         } else {
            for (IAttributeType type : types) {
               joinQuery.add(identityService.getLocalId(type));
            }
            chStmt.runPreparedQuery(RETRIEVE_GAMMAS_WITH_BRANCH_IDS_MULT_TYPES, joinQuery.getQueryId());
         }
         while (chStmt.next()) {
            gammasAndBranchIds.add(new Integer[] {chStmt.getInt("gamma_id"), chStmt.getInt("branch_id")});
         }
      } finally {
         chStmt.close();
      }

      return gammasAndBranchIds;
   }

   private void processDeletes(OseeConnection connection, List<Integer[]> gammasAndBranchIds, List<Integer[]> gammas) throws OseeCoreException {
      getDatabaseService().runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS_AND_BRANCH, "osee_txs"),
         gammasAndBranchIds);
      getDatabaseService().runBatchUpdate(connection, String.format(DELETE_BY_GAMMAS_AND_BRANCH, "osee_txs_archived"),
         gammasAndBranchIds);

      getDatabaseService().runBatchUpdate(connection, DELETE_BY_GAMMAS, gammas);
      getDatabaseService().runBatchUpdate(connection, DELETE_FROM_CONFLICT_TABLE_SOURCE_SIDE, gammas);
      getDatabaseService().runBatchUpdate(connection, DELETE_FROM_CONFLICT_TABLE_DEST_SIDE, gammas);
   }
}