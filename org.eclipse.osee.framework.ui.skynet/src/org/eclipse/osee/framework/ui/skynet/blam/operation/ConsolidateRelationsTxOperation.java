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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.JoinUtility.ExportImportJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public class ConsolidateRelationsTxOperation extends AbstractDbTxOperation {
   private static final String SELECT_RELATIONS =
         "select * from osee_relation_link order by rel_link_type_id, a_art_id, b_art_id, gamma_id";

   private static final String SELECT_RELATION_ADDRESSING =
         "select txs.*, idj.id1 as net_gamma_id from osee_join_export_import idj, osee_txs txs where idj.query_id = ? and idj.id2 = txs.gamma_id order by net_gamma_id, transaction_id, id2";

   private static final String UPDATE_TXS_GAMMAS =
         "update osee_txs set gamma_id = ?, mod_type = ?, tx_current = ? where transaction_id = ? and gamma_id = ?";

   private static final String DELETE_TXS = "delete from osee_txs where transaction_id = ? and gamma_id = ?";

   private static final String DELETE_RELATIONS = "delete from osee_relation_link where gamma_id = ?";

   private final ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
   private final List<Long[]> relationDeleteData = new ArrayList<Long[]>(14000);
   private final List<Long> obsoleteGammas = new ArrayList<Long>();
   private final StringBuilder addressingBackup = new StringBuilder(100000);
   private final List<Object[]> addressingToDelete = new ArrayList<Object[]>(13000);
   private final List<Object[]> updateAddressingData = new ArrayList<Object[]>(5000);
   private Writer backupWriter;
   private ExportImportJoinQuery gammaJoin;
   private OseeConnection connection;
   private int previousRelationTypeId;
   private int previousArtifactAId;
   private int previousArtiafctBId;
   private long netGamma;
   private int netOrderA;
   private int netOrderB;
   private String netRationale;
   boolean materiallyDifferent;
   boolean updateAddressing;
   private int counter;

   long previousNetGammaId;
   long previousObsoleteGammaId;
   int previousTransactionId;
   ModificationType netModType;
   TxChange netTxCurrent;

   public ConsolidateRelationsTxOperation() {
      super("Consolidate Relations", SkynetGuiPlugin.PLUGIN_ID);
   }

   private void init() throws OseeWrappedException, IOException {
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
      gammaJoin = JoinUtility.createExportImportJoinQuery();

      File iFile = OseeData.getFile("consolidateRelations_" + Lib.getDateTimeString() + ".csv");
      backupWriter = new BufferedWriter(new FileWriter(iFile));

      counter = 0;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      this.connection = connection;
      try {
         init();

         findObsoleteRelatins();

         System.out.println("gamma join size: " + gammaJoin.size());

         determineAffectedAddressing();

         updateGammas();
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private void findObsoleteRelatins() throws OseeCoreException {
      try {
         chStmt.runPreparedQuery(10000, SELECT_RELATIONS);
         while (chStmt.next()) {
            int relationTypeId = chStmt.getInt("rel_link_type_id");
            int artifactAId = chStmt.getInt("a_art_id");
            int artiafctBId = chStmt.getInt("b_art_id");

            if (isNextConceptualRelation(relationTypeId, artifactAId, artiafctBId)) {
               consolidate();
               initNextConceptualRelation(relationTypeId, artifactAId, artiafctBId);
            } else {
               obsoleteGammas.add(chStmt.getLong("gamma_id"));
               relationMateriallyDifferes(chStmt);
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
         System.out.println("rel type: " + previousRelationTypeId + "   A art id: " + previousArtifactAId + "   B art id: " + previousArtiafctBId);
      }
   }

   private void determineAffectedAddressing() throws OseeCoreException, IOException {
      gammaJoin.store();

      try {
         System.out.println("counter: " + counter);
         System.out.println("query id: " + gammaJoin.getQueryId());
         chStmt.runPreparedQuery(10000, SELECT_RELATION_ADDRESSING, gammaJoin.getQueryId());

         while (chStmt.next()) {
            long obsoleteGammaId = chStmt.getLong("gamma_id");
            int transactionId = chStmt.getInt("transaction_id");
            long netGammaId = chStmt.getLong("net_gamma_id");
            int modType = chStmt.getInt("mod_type");
            TxChange txCurrent = TxChange.getChangeType(chStmt.getInt("tx_current"));

            if (isNextAddressing(netGammaId, transactionId)) {
               if (updateAddressing) {
                  updateAddressingData.add(new Object[] {previousNetGammaId, netModType.getValue(),
                        netTxCurrent.getValue(), previousTransactionId, previousObsoleteGammaId});
               }
               updateAddressing = obsoleteGammaId != netGammaId;
               previousNetGammaId = netGammaId;
               previousObsoleteGammaId = obsoleteGammaId;
               previousTransactionId = transactionId;
               netModType = ModificationType.getMod(modType);
               netTxCurrent = txCurrent;
            } else {
               addressingToDelete.add(new Object[] {transactionId, obsoleteGammaId});
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
         throw new OseeStateException("    modType: " + modificationType + " != " + netModType);
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

   private void updateGammas() throws OseeCoreException, IOException {
      backupWriter.close();

      System.out.println("Number of txs rows deleted: " + ConnectionHandler.runBatchUpdate(connection, DELETE_TXS,
            addressingToDelete));

      System.out.println("Number of relation rows deleted: " + ConnectionHandler.runBatchUpdate(connection,
            DELETE_RELATIONS, relationDeleteData));

      System.out.println("Number of txs rows updated: " + ConnectionHandler.runBatchUpdate(connection,
            UPDATE_TXS_GAMMAS, updateAddressingData));
   }

   private void writeAddressingBackup(long obsoleteGammaId, int transactionId, long netGammaId, int modType, TxChange txCurrent) throws IOException {
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
      backupWriter.append(strB);
   }

   private boolean isNextConceptualRelation(int relationTypeId, int artifactAId, int artiafctBId) {
      return previousRelationTypeId != relationTypeId || previousArtifactAId != artifactAId || previousArtiafctBId != artiafctBId;
   }

   private void relationMateriallyDifferes(ConnectionHandlerStatement chStmt) throws OseeCoreException {
      if (!materiallyDifferent) {
         String currentRationale = chStmt.getString("rationale");
         materiallyDifferent |= Strings.isValid(currentRationale) && !currentRationale.equals(netRationale);
         if (RelationTypeManager.getType(chStmt.getInt("rel_link_type_id")).isOrdered()) {
            materiallyDifferent |= chStmt.getInt("a_order") != 0 && netOrderA != chStmt.getInt("a_order");
            materiallyDifferent |= chStmt.getInt("b_order") != 0 && netOrderB != chStmt.getInt("b_order");
         }
      }
   }

   private void initNextConceptualRelation(int relationTypeId, int artifactAId, int artiafctBId) throws OseeCoreException {
      obsoleteGammas.clear();
      previousRelationTypeId = relationTypeId;
      previousArtifactAId = artifactAId;
      previousArtiafctBId = artiafctBId;
      netGamma = chStmt.getInt("gamma_id");
      netOrderA = chStmt.getInt("a_order");
      netOrderB = chStmt.getInt("b_order");
      netRationale = chStmt.getString("rationale");
      materiallyDifferent = false;
   }
}