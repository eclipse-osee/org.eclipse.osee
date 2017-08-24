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

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_FETCH_SIZE;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.sql.join.ExportImportJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class FixDuplicateAttributesCommand extends AbstractDatastoreConsoleCommand {

   private OrcsApi orcsApi;
   private SqlJoinFactory joinFactory;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setSqlJoinFactory(SqlJoinFactory joinFactory) {
      this.joinFactory = joinFactory;
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
      return new DuplicateAttributesDatabaseTxCallable(getLogger(), getSession(), getJdbcClient(), console);
   }

   private final class DuplicateAttributesDatabaseTxCallable extends AbstractDatastoreTxCallable<Object> {
      private static final String SELECT_ATTRIBUTES =
         "select att1.gamma_id as gamma1, att2.gamma_id as gamma2 from osee_join_id oji, osee_attribute att1, osee_attribute att2 where oji.query_id = ? AND oji.id = att1.attr_type_id and att1.art_id = att2.art_id and att1.attr_type_id = att2.attr_type_id and att1.attr_id <> att2.attr_id";
      private static final String SELECT_DUPLICATES =
         "select txs1.branch_id, txs1.gamma_id as gamma1, txs2.gamma_id as gamma2  from osee_join_export_import idj, osee_txs txs1, osee_txs txs2 where idj.query_id = ? and idj.id1 = txs1.gamma_id and idj.id2 = txs2.gamma_id and txs1.branch_id = txs2.branch_id and txs1.tx_current = ? and  txs2.tx_current = ?";

      private final Console console;

      public DuplicateAttributesDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, Console console) {
         super(logger, session, jdbcClient);
         this.console = console;
      }

      @Override
      protected Object handleTxWork(JdbcConnection connection) throws OseeCoreException {
         try (ExportImportJoinQuery gammaJoin = joinFactory.createExportImportJoinQuery(connection)) {
            selectAttributes(gammaJoin, connection);
            gammaJoin.store();
            selectDuplicates(gammaJoin, connection);
         } catch (Exception ex) {
            console.write(ex);
            getLogger().error(ex, "Error fixing duplicate attributes");
         }
         return null;
      }

      private void selectAttributes(ExportImportJoinQuery gammaJoin, JdbcConnection connection) throws OseeCoreException {
         try (IdJoinQuery typeJoin = joinFactory.createIdJoinQuery(connection)) {
            populateAttributeTypeJoin(typeJoin);
            getJdbcClient().runQuery(stmt -> gammaJoin.add(stmt.getLong("gamma1"), stmt.getLong("gamma2")),
               JDBC__MAX_FETCH_SIZE, SELECT_ATTRIBUTES, typeJoin.getQueryId());
         }
      }

      private void selectDuplicates(ExportImportJoinQuery gammaJoin, JdbcConnection connection) throws OseeCoreException {
         JdbcStatement chStmt = getJdbcClient().getStatement(connection);
         try {
            chStmt.runPreparedQuery(SELECT_DUPLICATES, gammaJoin.getQueryId(), TxChange.CURRENT, TxChange.CURRENT);
            while (chStmt.next()) {
               console.writeln("branch: " + chStmt.getLong("branch_id"), "gamma1: " + chStmt.getLong("gamma1"),
                  "gamma2: " + chStmt.getLong("gamma2"));
            }
         } finally {
            chStmt.close();
         }
      }

      private void populateAttributeTypeJoin(IdJoinQuery typeJoin) throws OseeCoreException {
         AttributeTypes types = orcsApi.getOrcsTypes().getAttributeTypes();
         for (AttributeTypeId attributeType : types.getAll()) {
            if (types.getMaxOccurrences(attributeType) == 1) {
               typeJoin.add(attributeType);
            }
         }
         typeJoin.store();
      }
   }
}
