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
package org.eclipse.osee.orcs.db.internal.callable;

/**
 * @author Ryan D. Brooks
 */
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.database.schema.DatabaseTxCallable;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ExportImportJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;

public final class DuplicateAttributesDatabaseTxCallable extends DatabaseTxCallable<Object> {
   private static final String SELECT_ATTRIBUTES =
      "select att1.gamma_id as gamma1, att2.gamma_id as gamma2 from osee_join_id oji, osee_attribute att1, osee_attribute att2 where oji.query_id = ? AND oji.id = att1.attr_type_id and att1.art_id = att2.art_id and att1.attr_type_id = att2.attr_type_id and att1.attr_id <> att2.attr_id";
   private static final String SELECT_DUPLICATES =
      "select txs1.branch_id, txs1.gamma_id as gamma1, txs2.gamma_id as gamma2  from osee_join_export_import idj, osee_txs txs1, osee_txs txs2 where idj.query_id = ? and idj.id1 = txs1.gamma_id and idj.id2 = txs2.gamma_id and txs1.branch_id = txs2.branch_id and txs1.tx_current = ? and  txs2.tx_current = ?";

   private final Console console;
   private final AttributeTypeCache attTypeCache;

   public DuplicateAttributesDatabaseTxCallable(Log logger, IOseeDatabaseService databaseService, Console console, AttributeTypeCache attTypeCache) {
      super(logger, databaseService, "Duplicate Attributes");
      this.console = console;
      this.attTypeCache = attTypeCache;
   }

   @Override
   protected Object handleTxWork(OseeConnection connection) throws OseeCoreException {
      ExportImportJoinQuery gammaJoin = JoinUtility.createExportImportJoinQuery();
      try {
         selectAttributes(gammaJoin, connection);
         gammaJoin.store(connection);
         selectDuplicates(gammaJoin, connection);
      } catch (Exception ex) {
         console.write(ex);
         getLogger().error(ex, "Error fixing duplicate attributes");
      } finally {
         gammaJoin.delete(connection);
      }
      return null;
   }

   private void selectAttributes(ExportImportJoinQuery gammaJoin, OseeConnection connection) throws OseeCoreException {
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

   private void selectDuplicates(ExportImportJoinQuery gammaJoin, OseeConnection connection) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery(SELECT_DUPLICATES, gammaJoin.getQueryId(), TxChange.CURRENT.getValue(),
            TxChange.CURRENT.getValue());
         while (chStmt.next()) {
            console.writeln("branch: " + chStmt.getInt("branch_id"), "gamma1: " + chStmt.getLong("gamma1"),
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