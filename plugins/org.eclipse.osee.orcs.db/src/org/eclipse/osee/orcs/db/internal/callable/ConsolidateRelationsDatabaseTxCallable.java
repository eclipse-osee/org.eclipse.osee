/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import static org.eclipse.osee.framework.database.core.IOseeStatement.MAX_FETCH;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.sql.join.ExportImportJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.JoinUtility;

/**
 * @author Ryan D. Brooks
 */
public class ConsolidateRelationsDatabaseTxCallable extends AbstractDatastoreTxCallable<Object> {
   private static final String SELECT_RELATIONS =
      "select * from osee_relation_link order by rel_link_type_id, a_art_id, b_art_id, gamma_id";

   private static final String SELECT_RELATION_ADDRESSING =
      "select txs.*, idj.id1 as net_gamma_id from osee_join_export_import idj, osee_txs txs where idj.query_id = ? and idj.id2 = txs.gamma_id order by net_gamma_id, transaction_id, id2";

   private static final String UPDATE_TXS_GAMMAS =
      "update osee_txs set gamma_id = ?, mod_type = ?, tx_current = ? where transaction_id = ? and gamma_id = ?";

   private static final String DELETE_TXS =
      "delete from osee_txs where branch_id = ? and transaction_id = ? and gamma_id = ?";

   private static final String DELETE_RELATIONS = "delete from osee_relation_link where gamma_id = ?";

   private final List<Long[]> relationDeleteData = new ArrayList<Long[]>(14000);
   private final List<Long> obsoleteGammas = new ArrayList<Long>();
   private final StringBuilder addressingBackup = new StringBuilder(100000);
   private final List<Object[]> addressingToDelete = new ArrayList<Object[]>(13000);
   private final List<Object[]> updateAddressingData = new ArrayList<Object[]>(5000);
   private ExportImportJoinQuery gammaJoin;
   private OseeConnection connection;
   private long previousRelationTypeId;
   private int previousArtifactAId;
   private int previousArtiafctBId;
   private long netGamma;
   private String netRationale;
   boolean materiallyDifferent;
   boolean updateAddressing;
   private int counter;
   private IOseeStatement chStmt;

   private final Console console;

   long previousNetGammaId;
   long previousObsoleteGammaId;
   int previousTransactionId;
   ModificationType netModType;
   TxChange netTxCurrent;

   public ConsolidateRelationsDatabaseTxCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, Console console) {
      super(logger, session, databaseService, "Consolidate Relations");
      this.console = console;
   }

   private void init() throws OseeCoreException {
      previousRelationTypeId = -1;
      previousArtifactAId = -1;
      previousArtiafctBId = -1;
      materiallyDifferent = true;
      relationDeleteData.clear();
      obsoleteGammas.clear();
      updateAddressingData.clear();
      addressingToDelete.clear();
      addressingBackup.delete(0, 999999999);
      updateAddressing = false;

      previousNetGammaId = -1;
      previousTransactionId = -1;
      chStmt = getDatabaseService().getStatement();
      gammaJoin = JoinUtility.createExportImportJoinQuery(getDatabaseService());

      counter = 0;
   }

   @Override
   protected Object handleTxWork(OseeConnection connection) throws OseeCoreException {
      this.connection = connection;
      console.writeln("Consolidating relations:");
      init();

      findObsoleteRelations();

      console.writeln("gamma join size: [%s]", gammaJoin.size());

      determineAffectedAddressing();

      updateGammas();
      console.writeln("...done.");
      return null;
   }

   private void findObsoleteRelations() throws OseeCoreException {
      try {
         chStmt.runPreparedQuery(MAX_FETCH, SELECT_RELATIONS);
         while (chStmt.next()) {
            long relationTypeId = chStmt.getLong("rel_link_type_id");
            int artifactAId = chStmt.getInt("a_art_id");
            int artiafctBId = chStmt.getInt("b_art_id");

            if (isNextConceptualRelation(relationTypeId, artifactAId, artiafctBId)) {
               consolidate();
               initNextConceptualRelation(relationTypeId, artifactAId, artiafctBId);
            } else {
               obsoleteGammas.add(chStmt.getLong("gamma_id"));
               relationMateriallyDiffers(chStmt);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void consolidate() {
      if (!materiallyDifferent && obsoleteGammas.size() > 0) {
         gammaJoin.add(netGamma, netGamma);
         for (Long obsoleteGamma : obsoleteGammas) {
            gammaJoin.add(netGamma, obsoleteGamma);
            relationDeleteData.add(new Long[] {obsoleteGamma});
         }
      }
      if (materiallyDifferent) {
         counter++;
         console.writeln("rel_type:[%s] a_art_id:[%s]  b_art_id:[%s]", previousRelationTypeId, previousArtifactAId,
            previousArtiafctBId);
      }
   }

   private void determineAffectedAddressing() throws OseeCoreException {
      gammaJoin.store();

      try {
         console.writeln("counter: [%s]", counter);
         console.writeln("query id: [%s]", gammaJoin.getQueryId());
         chStmt.runPreparedQuery(MAX_FETCH, SELECT_RELATION_ADDRESSING, gammaJoin.getQueryId());

         while (chStmt.next()) {
            long obsoleteGammaId = chStmt.getLong("gamma_id");
            int transactionId = chStmt.getInt("transaction_id");
            long netGammaId = chStmt.getLong("net_gamma_id");
            int modType = chStmt.getInt("mod_type");
            TxChange txCurrent = TxChange.getChangeType(chStmt.getInt("tx_current"));

            if (isNextAddressing(netGammaId, transactionId)) {
               if (updateAddressing) {
                  updateAddressingData.add(new Object[] {
                     previousNetGammaId,
                     netModType.getValue(),
                     netTxCurrent.getValue(),
                     previousTransactionId,
                     previousObsoleteGammaId});
               }
               updateAddressing = obsoleteGammaId != netGammaId;
               previousNetGammaId = netGammaId;
               previousObsoleteGammaId = obsoleteGammaId;
               previousTransactionId = transactionId;
               netModType = ModificationType.getMod(modType);
               netTxCurrent = txCurrent;
            } else {
               addressingToDelete.add(new Object[] {chStmt.getLong("branch_id"), transactionId, obsoleteGammaId});
               computeNetAddressing(ModificationType.getMod(modType), txCurrent);
            }

            writeAddressingBackup(obsoleteGammaId, transactionId, netGammaId, modType, txCurrent);
         }
      } finally {
         chStmt.close();
      }
      gammaJoin.delete();
   }

   private boolean isNextAddressing(long netGammaId, int transactionId) {
      return previousNetGammaId != netGammaId || previousTransactionId != transactionId;
   }

   private void computeNetAddressing(ModificationType modificationType, TxChange txCurrent) throws OseeStateException {
      if (netTxCurrentNeedsUpdate(txCurrent)) {
         netTxCurrent = txCurrent;
         updateAddressing = true;
      }

      if (netModTypeNeedsUpdate(modificationType)) {
         netModType = modificationType;
         updateAddressing = true;
      } else if (!ignoreNetModType(modificationType)) {
         throw new OseeStateException("    modType [%s] != [%s]", modificationType, netModType);
      }
   }

   private boolean netTxCurrentNeedsUpdate(TxChange txCurrent) {
      if (txCurrent == netTxCurrent) {
         return false;
      }
      boolean needsUpdate = txCurrent == TxChange.NOT_CURRENT;
      needsUpdate |= txCurrent == TxChange.CURRENT && netTxCurrent.isDeleted();
      return needsUpdate || netTxCurrent == TxChange.DELETED && txCurrent == TxChange.ARTIFACT_DELETED;
   }

   private boolean netModTypeNeedsUpdate(ModificationType modificationType) {
      boolean needsUpdate = !modificationType.isDeleted() && netModType.isDeleted();
      needsUpdate |= netModType == ModificationType.NEW && modificationType == ModificationType.MODIFIED;
      return needsUpdate || netModType == ModificationType.DELETED && modificationType == ModificationType.ARTIFACT_DELETED;
   }

   private boolean ignoreNetModType(ModificationType modificationType) {
      boolean ignore = !netModType.isDeleted() && modificationType.isDeleted();
      ignore |= netModType == modificationType;
      ignore |= netModType == ModificationType.MODIFIED && modificationType == ModificationType.NEW;
      return ignore || netModType == ModificationType.ARTIFACT_DELETED && modificationType == ModificationType.DELETED;
   }

   private void updateGammas() throws OseeCoreException {
      console.writeln("Number of txs rows deleted: [%s]",
         getDatabaseService().runBatchUpdate(connection, DELETE_TXS, addressingToDelete));

      console.writeln("Number of relation rows deleted: [%s]",
         getDatabaseService().runBatchUpdate(connection, DELETE_RELATIONS, relationDeleteData));

      console.writeln("Number of txs rows updated: [%s]",
         getDatabaseService().runBatchUpdate(connection, UPDATE_TXS_GAMMAS, updateAddressingData));
   }

   private void writeAddressingBackup(long obsoleteGammaId, int transactionId, long netGammaId, int modType, TxChange txCurrent) {
      StringBuilder strB = new StringBuilder(30);

      strB.append(obsoleteGammaId);
      strB.append(",");
      strB.append(transactionId);
      strB.append(",");
      strB.append(netGammaId);
      strB.append(",");
      strB.append(modType);
      strB.append(",");
      strB.append(txCurrent.getValue());
      strB.append("\n");
      console.writeln(strB.toString());
   }

   private boolean isNextConceptualRelation(long relationTypeId, int artifactAId, int artiafctBId) {
      return previousRelationTypeId != relationTypeId || previousArtifactAId != artifactAId || previousArtiafctBId != artiafctBId;
   }

   private void relationMateriallyDiffers(IOseeStatement chStmt) throws OseeCoreException {
      if (!materiallyDifferent) {
         String currentRationale = chStmt.getString("rationale");
         materiallyDifferent |= Strings.isValid(currentRationale) && !currentRationale.equals(netRationale);
      }
   }

   private void initNextConceptualRelation(long relationTypeId, int artifactAId, int artiafctBId) throws OseeCoreException {
      obsoleteGammas.clear();
      previousRelationTypeId = relationTypeId;
      previousArtifactAId = artifactAId;
      previousArtiafctBId = artiafctBId;
      netGamma = chStmt.getInt("gamma_id");
      netRationale = chStmt.getString("rationale");
      materiallyDifferent = false;
   }
}