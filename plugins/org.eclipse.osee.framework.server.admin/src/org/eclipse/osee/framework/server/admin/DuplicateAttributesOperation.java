/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.server.admin;

/**
 * @author Ryan D. Brooks
 */
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ExportImportJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.server.admin.internal.Activator;

public final class DuplicateAttributesOperation extends AbstractDbTxOperation {
   private static final String SELECT_ATTRIBUTES =
      "select att1.gamma_id as gamma1, att2.gamma_id as gamma2 from osee_join_id oji, osee_attribute att1, osee_attribute att2 where oji.query_id = ? AND oji.id = att1.attr_type_id and att1.art_id = att2.art_id and att1.attr_type_id = att2.attr_type_id and att1.attr_id <> att2.attr_id";
   private static final String SELECT_DUPLICATES =
      "select txs1.branch_id, txs1.gamma_id as gamma1, txs2.gamma_id as gamma2  from osee_join_export_import idj, osee_txs txs1, osee_txs txs2 where idj.query_id = ? and idj.id1 = txs1.gamma_id and idj.id2 = txs2.gamma_id and txs1.branch_id = txs2.branch_id and txs1.tx_current = ? and  txs2.tx_current = ?";
   private final ExportImportJoinQuery gammaJoin;
   private final AttributeTypeCache attTypeCache;

   public DuplicateAttributesOperation(OperationLogger logger, AttributeTypeCache attTypeCache, IOseeDatabaseService databaseService) throws OseeDataStoreException {
      super(databaseService, "Duplicate Attributes", Activator.PLUGIN_ID, logger);
      gammaJoin = JoinUtility.createExportImportJoinQuery();
      this.attTypeCache = attTypeCache;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      try {
         selectAttributes(connection);
         gammaJoin.store(connection);
         selectDuplicates(connection);
      } catch (Exception ex) {
         log(ex);
      } finally {
         gammaJoin.delete(connection);
      }
   }

   private void selectAttributes(OseeConnection connection) throws OseeCoreException {
      IdJoinQuery typeJoin = JoinUtility.createIdJoinQuery();
      populateAttributeTypeJoin(typeJoin);

      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery(10000, SELECT_ATTRIBUTES, typeJoin.getQueryId());
         while (chStmt.next()) {
            gammaJoin.add(chStmt.getLong("gamma1"), chStmt.getLong("gamma2"));
         }
      } finally {
         chStmt.close();
         typeJoin.delete(connection);
      }
   }

   private void selectDuplicates(OseeConnection connection) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery(SELECT_DUPLICATES, gammaJoin.getQueryId(), TxChange.CURRENT.getValue(),
            TxChange.CURRENT.getValue());
         while (chStmt.next()) {
            log("branch: " + chStmt.getInt("branch_id"), "gamma1: " + chStmt.getLong("gamma1"),
               "gamma2: " + chStmt.getLong("gamma2"));
         }
      } finally {
         chStmt.close();
      }
   }

   private void populateAttributeTypeJoin(IdJoinQuery typeJoin) throws OseeCoreException {
      for (AttributeType attributeType : attTypeCache.getAll()) {
         if (attributeType.getMaxOccurrences() == 1) {
            typeJoin.add(attributeType.getId());
         }
      }
      typeJoin.store();
   }
}