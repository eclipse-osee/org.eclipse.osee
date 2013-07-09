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
package org.eclipse.osee.orcs.db.internal.console;

import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ExportImportJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;

/**
 * @author Roberto E. Escobar
 */
public class FixDuplicateAttributesCommand extends AbstractDatastoreConsoleCommand {

   private OrcsApi orcsApi;
   private IdentityService identityService;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   @Override
   public String getName() {
      return "db_fix_duplicate_attributes";
   }

   @Override
   public String getDescription() {
      return "Detect and fix duplicate attributes";
   }

   @Override
   public String getUsage() {
      return "No Parameters";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new DuplicateAttributesDatabaseTxCallable(getLogger(), getSession(), getDatabaseService(), console);
   }

   private final class DuplicateAttributesDatabaseTxCallable extends AbstractDatastoreTxCallable<Object> {
      private static final String SELECT_ATTRIBUTES =
         "select att1.gamma_id as gamma1, att2.gamma_id as gamma2 from osee_join_id oji, osee_attribute att1, osee_attribute att2 where oji.query_id = ? AND oji.id = att1.attr_type_id and att1.art_id = att2.art_id and att1.attr_type_id = att2.attr_type_id and att1.attr_id <> att2.attr_id";
      private static final String SELECT_DUPLICATES =
         "select txs1.branch_id, txs1.gamma_id as gamma1, txs2.gamma_id as gamma2  from osee_join_export_import idj, osee_txs txs1, osee_txs txs2 where idj.query_id = ? and idj.id1 = txs1.gamma_id and idj.id2 = txs2.gamma_id and txs1.branch_id = txs2.branch_id and txs1.tx_current = ? and  txs2.tx_current = ?";

      private final Console console;

      public DuplicateAttributesDatabaseTxCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, Console console) {
         super(logger, session, databaseService, "Duplicate Attributes");
         this.console = console;
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
         AttributeTypes types = orcsApi.getOrcsTypes(null).getAttributeTypes();
         for (IAttributeType attributeType : types.getAll()) {
            if (types.getMaxOccurrences(attributeType) == 1) {
               Integer localId = identityService.getLocalId(attributeType.getGuid());
               typeJoin.add(localId);
            }
         }
         typeJoin.store();
      }
   }
}
