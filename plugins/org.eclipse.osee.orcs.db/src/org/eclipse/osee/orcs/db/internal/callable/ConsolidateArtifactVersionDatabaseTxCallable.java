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

import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.INTRODUCED;
import static org.eclipse.osee.framework.core.enums.ModificationType.MERGED;
import static org.eclipse.osee.framework.core.enums.ModificationType.MODIFIED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.ExportImportJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.operation.Address;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Ryan D. Brooks
 */
public class ConsolidateArtifactVersionDatabaseTxCallable extends AbstractDatastoreTxCallable<Object> {
   private static final String SELECT_ARTIFACT_VERSIONS = "select * from osee_artifact order by art_id, gamma_id";

   private static final String SELECT_ADDRESSING =
      "select txs.*, idj.id1 as net_gamma_id from osee_join_export_import idj, osee_txs%s txs where idj.query_id = ? and idj.id2 = txs.gamma_id order by net_gamma_id, branch_id, transaction_id, gamma_id desc";

   private static final String UPDATE_CONFLICTS =
      "update osee_conflict set %s = (select gamma_id from osee_artifact where conflict_id = art_id) where conflict_type = 3";

   private static final String UPDATE_TXS_GAMMAS =
      "update osee_txs%s set gamma_id = ?, mod_type = ? where transaction_id = ? and gamma_id = ?";

   private static final String DELETE_TXS = "delete from osee_txs%s where transaction_id = ? and gamma_id = ?";

   private static final String DELETE_ARTIFACT_VERSIONS = "delete from osee_artifact where gamma_id = ?";

   private static final String POPULATE_DUPLICATE_ARTID =
      "SELECT ART1.art_id as art_id, t1.branch_id as branch_id, art2.art_id as art_id_1, t2.branch_id as branch_id_1 FROM OSEE.OSEE_ARTIFACT ART1, OSEE.OSEE_ARTIFACT ART2, osee.osee_txs t1, osee.osee_txs t2 where t1.gamma_id = ART1.gamma_id and t2.gamma_id = ART2.gamma_id and  ART1.gamma_id <> ART2.gamma_id AND ART1.ART_ID = ART2.ART_ID order by art1.art_id";

   private static final String FIND_ARTIFACT_MODS =
      "select * from osee_join_artifact jn1, osee_artifact art, osee_txs txs where art.gamma_id = txs.gamma_id and txs.branch_id = jn1.branch_id and art.art_id = jn1.art_id and jn1.query_id = ? order by art.art_id, txs.branch_id, txs.transaction_id";

   private static final String UPDATE_TXS_MOD_CURRENT =
      "update osee_txs%s set mod_type = ?, tx_current = ? where transaction_id = ? and gamma_id = ?";

   private List<Long[]> deleteArtifactVersionData;
   private final List<Long> obsoleteGammas = new ArrayList<Long>();
   private final List<Object[]> addressingToDelete = new ArrayList<Object[]>(13000);
   private final List<Object[]> updateAddressingData = new ArrayList<Object[]>(5000);
   private final List<Object[]> updateTxsCurrentModData = new ArrayList<Object[]>(5000);
   private ExportImportJoinQuery gammaJoin;
   private OseeConnection connection;
   private int previousArtifactId;
   private long netGamma;
   private IOseeStatement chStmt;
   private long previousNetGammaId;
   private int previousBranchId;
   private int previuosTransactionId;
   private int updateTxsCounter;
   private int deleteTxsCounter;

   private final Console console;

   public ConsolidateArtifactVersionDatabaseTxCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, Console console) {
      super(logger, session, databaseService, "Consolidate Artifact Versions");
      this.console = console;
   }

   private void init() throws OseeCoreException {
      deleteArtifactVersionData = new ArrayList<Long[]>(14000);
      obsoleteGammas.clear();
      updateAddressingData.clear();
      addressingToDelete.clear();
      previousArtifactId = -1;
      previousNetGammaId = -1;
      previousBranchId = -1;
      previuosTransactionId = -1;
      updateTxsCounter = 0;
      deleteTxsCounter = 0;
      chStmt = getDatabaseService().getStatement(connection);
      gammaJoin = JoinUtility.createExportImportJoinQuery();
   }

   private ArtifactJoinQuery populateJoinTableWithArtifacts() throws OseeCoreException {
      ArtifactJoinQuery idJoinQuery = JoinUtility.createArtifactJoinQuery();
      chStmt.runPreparedQuery(POPULATE_DUPLICATE_ARTID);

      while (chStmt.next()) {
         idJoinQuery.add(chStmt.getInt("art_id"), chStmt.getInt("branch_id"), -1);
         idJoinQuery.add(chStmt.getInt("art_id_1"), chStmt.getInt("branch_id_1"), -1);
      }
      idJoinQuery.store(connection);
      return idJoinQuery;
   }

   private void findArtifactMods() throws OseeCoreException {
      ArtifactJoinQuery artifactJoinQuery = populateJoinTableWithArtifacts();
      List<Address> mods = new ArrayList<Address>();
      try {
         chStmt.runPreparedQuery(10000, FIND_ARTIFACT_MODS, artifactJoinQuery.getQueryId());
         while (chStmt.next()) {
            int artifactId = chStmt.getInt("art_id");
            int branchId = chStmt.getInt("branch_id");

            if (previousArtifactId != artifactId || previousBranchId != branchId) {
               if (!mods.isEmpty()) {
                  consolidateMods(mods);
                  mods.clear();
               }
               previousArtifactId = artifactId;
               previousBranchId = branchId;
            }
            mods.add(new Address(false, branchId, artifactId, chStmt.getInt("transaction_id"),
               chStmt.getInt("gamma_id"), ModificationType.getMod(chStmt.getInt("mod_type")),
               TxChange.getChangeType(chStmt.getInt("tx_current"))));
         }
      } finally {
         chStmt.close();
         artifactJoinQuery.delete(connection);
      }
   }

   private void markForUpdate(Address address, ModificationType updatedMod, TxChange updatedTxChange) {
      updateTxsCurrentModData.add(new Object[] {
         (updatedMod == null ? address.getModType() : updatedMod).getValue(),
         (updatedTxChange == null ? address.getTxCurrent() : updatedTxChange).getValue(),
         address.getTransactionId(),
         address.getGammaId()});
   }

   private boolean ifAllNew(List<Address> addresses) {
      for (Address address : addresses) {
         if (address.getModType() != NEW) {
            return false;
         }
      }

      markForUpdate(addresses.get(0), null, TxChange.CURRENT);
      for (int i = 1; i < addresses.size(); i++) {
         addressingToDelete.add(new Object[] {addresses.get(i).getTransactionId(), addresses.get(i).getGammaId()});
      }

      return true;
   }

   private void consolidateMods(List<Address> mods) {
      Address address0 = mods.get(0);
      ModificationType mod0 = address0.getModType();
      boolean knownCase = ifAllNew(mods);
      if (mods.size() == 1) {
         knownCase = true;
         if (mod0 == MODIFIED) {
            markForUpdate(address0, NEW, null);
         }
      } else {
         Address address1 = mods.get(1);
         ModificationType mod1 = address1.getModType();
         if (mod0.matches(NEW, INTRODUCED, MERGED)) {
            if (mods.size() == 2 && mod1.matches(DELETED, MERGED)) {
               knownCase = true;
            } else if (mods.size() == 3) {
               Address address2 = mods.get(2);
               ModificationType mod2 = address2.getModType();
               if (mod1 == DELETED && mod2 == DELETED) {
                  knownCase = true;
                  // must purge most recent delete and set previous one to current
                  markForUpdate(address1, null, TxChange.DELETED);
                  addressingToDelete.add(new Object[] {address2.getTransactionId(), address2.getGammaId()});
               } else if (mod1 == MERGED && mod2 == DELETED) {
                  knownCase = true;
               }
            }
         }
      }
      if (!knownCase) {
         console.writeln("unknown case: artifact id: %d branch_id: %d", previousArtifactId, previousBranchId);
      }
   }

   @Override
   protected Object handleTxWork(OseeConnection connection) throws OseeCoreException {

      this.connection = connection;
      init();

      findArtifactMods();

      console.writeln("updateTxsCurrentModData size: %d", updateTxsCurrentModData.size());
      console.writeln("addressingToDelete size: %d", addressingToDelete.size());

      getDatabaseService().runBatchUpdate(connection, prepareSql(UPDATE_TXS_MOD_CURRENT, false),
         updateTxsCurrentModData);
      getDatabaseService().runBatchUpdate(connection, prepareSql(DELETE_TXS, false), addressingToDelete);

      findObsoleteGammas();
      console.writeln("gamma join size: %d", gammaJoin.size());

      console.writeln("Number of artifact version rows deleted: %d",
         getDatabaseService().runBatchUpdate(connection, DELETE_ARTIFACT_VERSIONS, deleteArtifactVersionData));
      deleteArtifactVersionData = null;

      gammaJoin.store(connection);

      updataConflicts("source_gamma_id");
      updataConflicts("dest_gamma_id");

      determineAffectedAddressingAndFix(false);
      determineAffectedAddressingAndFix(true);

      gammaJoin.delete(connection);

      return null;
   }

   private void updataConflicts(String columnName) throws OseeCoreException {
      int count = getDatabaseService().runPreparedUpdate(connection, String.format(UPDATE_CONFLICTS, columnName));
      console.writeln("updated %s in %d rows", columnName, count);
   }

   private void findObsoleteGammas() throws OseeCoreException {
      try {
         chStmt.runPreparedQuery(10000, SELECT_ARTIFACT_VERSIONS);
         while (chStmt.next()) {
            int artifactId = chStmt.getInt("art_id");

            if (isNextConceptualArtifact(artifactId)) {
               consolidate();
               initNextConceptualArtifact(artifactId);
            } else {
               obsoleteGammas.add(chStmt.getLong("gamma_id"));
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void consolidate() {
      if (obsoleteGammas.size() > 0) {
         gammaJoin.add(netGamma, netGamma);
         for (Long obsoleteGamma : obsoleteGammas) {
            gammaJoin.add(netGamma, obsoleteGamma);
            deleteArtifactVersionData.add(new Long[] {obsoleteGamma});
         }
      }
   }

   private void determineAffectedAddressingAndFix(boolean archived) throws OseeCoreException {
      try {
         console.writeln("query id: %d", gammaJoin.getQueryId());
         chStmt.runPreparedQuery(10000, String.format(SELECT_ADDRESSING, archived ? "_archived" : ""),
            gammaJoin.getQueryId());

         while (chStmt.next()) {
            long obsoleteGammaId = chStmt.getLong("gamma_id");
            int transactionId = chStmt.getInt("transaction_id");
            int branchId = chStmt.getInt("branch_id");
            long netGammaId = chStmt.getLong("net_gamma_id");
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
            TxChange.getChangeType(chStmt.getInt("tx_current"));

            if (isNextArtifactGroup(netGammaId, branchId)) {
               writeAddressingChanges(archived, false);
               if (modType == MODIFIED) {
                  addToUpdateAddresssing(NEW, netGammaId, modType, transactionId, obsoleteGammaId);
               } else if (modType.matches(NEW, INTRODUCED, DELETED, MERGED)) {
                  addToUpdateAddresssing(modType, netGammaId, modType, transactionId, obsoleteGammaId);
               } else {
                  throw new OseeStateException("unexpected mod type [%s]", modType);
               }
            } else {
               if (modType.matches(NEW, INTRODUCED, MODIFIED)) {
                  addressingToDelete.add(new Object[] {transactionId, obsoleteGammaId});
               } else if (modType == DELETED || modType == MERGED) {
                  if (previuosTransactionId != transactionId) { // can't use a gamma (netGammaId) more than once in a transaction
                     addToUpdateAddresssing(modType, netGammaId, modType, transactionId, obsoleteGammaId);
                  }
               } else {
                  throw new OseeStateException("unexpected mod type [%s]", modType);
               }
            }

            previousNetGammaId = netGammaId;
            previousBranchId = branchId;
            previuosTransactionId = transactionId;
         }
      } finally {
         chStmt.close();
      }

      writeAddressingChanges(archived, true);
   }

   private void addToUpdateAddresssing(ModificationType netModType, long netGammaId, ModificationType modType, int transactionId, long obsoleteGammaId) {
      if (obsoleteGammaId != netGammaId || modType != netModType) {
         updateAddressingData.add(new Object[] {netGammaId, netModType.getValue(), transactionId, obsoleteGammaId});
      }
   }

   private boolean isNextArtifactGroup(long netGammaId, int branchId) {
      return previousNetGammaId != netGammaId || previousBranchId != branchId;
   }

   private String prepareSql(String sql, boolean archived) {
      return String.format(sql, archived ? "_archived" : "");
   }

   private void writeAddressingChanges(boolean archived, boolean force) throws OseeCoreException {
      String archivedStr = archived ? "_archived" : "";
      if (addressingToDelete.size() > 99960 || force) {
         deleteTxsCounter +=
            getDatabaseService().runBatchUpdate(connection, prepareSql(DELETE_TXS, archived), addressingToDelete);
         console.writeln("Number of txs%s rows deleted: %d", archivedStr, deleteTxsCounter);
         addressingToDelete.clear();
      }

      if (updateAddressingData.size() > 99960 || force) {
         updateTxsCounter +=
            getDatabaseService().runBatchUpdate(connection, prepareSql(UPDATE_TXS_GAMMAS, archived),
               updateAddressingData);
         console.writeln("Number of txs%s rows updated: %d", archivedStr, updateTxsCounter);

         updateAddressingData.clear();
      }
   }

   private boolean isNextConceptualArtifact(int artifactId) {
      return previousArtifactId != artifactId;
   }

   private void initNextConceptualArtifact(int artifactId) throws OseeCoreException {
      obsoleteGammas.clear();
      previousArtifactId = artifactId;
      netGamma = chStmt.getInt("gamma_id");
   }

}