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
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.sql.join.ArtifactJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.ExportImportJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.internal.util.Address;

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

   private List<Object[]> deleteArtifactVersionData;
   private final List<Long> obsoleteGammas = new ArrayList<>();
   private final List<Object[]> addressingToDelete = new ArrayList<>(13000);
   private final List<Object[]> updateAddressingData = new ArrayList<>(5000);
   private final List<Object[]> updateTxsCurrentModData = new ArrayList<>(5000);
   private ExportImportJoinQuery gammaJoin;
   private JdbcConnection connection;
   private int previousArtifactId;
   private long netGamma;
   private JdbcStatement chStmt;
   private long previousNetGammaId;
   private long previousBranchId;
   private Long previuosTransactionId;
   private int updateTxsCounter;
   private int deleteTxsCounter;

   private final SqlJoinFactory joinFactory;
   private final Console console;

   public ConsolidateArtifactVersionDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, SqlJoinFactory joinFactory, Console console) {
      super(logger, session, jdbcClient);
      this.joinFactory = joinFactory;
      this.console = console;
   }

   private void init() throws OseeCoreException {
      deleteArtifactVersionData = new ArrayList<>(14000);
      obsoleteGammas.clear();
      updateAddressingData.clear();
      addressingToDelete.clear();
      previousArtifactId = -1;
      previousNetGammaId = -1;
      previousBranchId = -1;
      previuosTransactionId = Id.SENTINEL;
      updateTxsCounter = 0;
      deleteTxsCounter = 0;
      chStmt = getJdbcClient().getStatement(connection);
      gammaJoin = joinFactory.createExportImportJoinQuery();
   }

   private ArtifactJoinQuery populateJoinTableWithArtifacts() throws OseeCoreException {
      ArtifactJoinQuery idJoinQuery = joinFactory.createArtifactJoinQuery();
      chStmt.runPreparedQuery(POPULATE_DUPLICATE_ARTID);

      while (chStmt.next()) {
         idJoinQuery.add(chStmt.getInt("art_id"), BranchId.valueOf(chStmt.getLong("branch_id")),
            TransactionId.SENTINEL);
         idJoinQuery.add(chStmt.getInt("art_id_1"), BranchId.valueOf(chStmt.getLong("branch_id_1")),
            TransactionId.SENTINEL);
      }
      idJoinQuery.store(connection);
      return idJoinQuery;
   }

   private void findArtifactMods() throws OseeCoreException {
      ArtifactJoinQuery artifactJoinQuery = populateJoinTableWithArtifacts();
      List<Address> mods = new ArrayList<>();
      try {
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, FIND_ARTIFACT_MODS,
            artifactJoinQuery.getQueryId());
         while (chStmt.next()) {
            int artifactId = chStmt.getInt("art_id");
            long branchUuid = chStmt.getLong("branch_id");
            ApplicabilityId appId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));
            if (previousArtifactId != artifactId || previousBranchId != branchUuid) {
               if (!mods.isEmpty()) {
                  consolidateMods(mods);
                  mods.clear();
               }
               previousArtifactId = artifactId;
               previousBranchId = branchUuid;
            }
            mods.add(new Address(false, branchUuid, artifactId, chStmt.getLong("transaction_id"),
               chStmt.getInt("gamma_id"), ModificationType.getMod(chStmt.getInt("mod_type")), appId,
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
   protected Object handleTxWork(JdbcConnection connection) throws OseeCoreException {

      this.connection = connection;
      init();

      findArtifactMods();

      console.writeln("updateTxsCurrentModData size: %d", updateTxsCurrentModData.size());
      console.writeln("addressingToDelete size: %d", addressingToDelete.size());

      getJdbcClient().runBatchUpdate(connection, prepareSql(UPDATE_TXS_MOD_CURRENT, false), updateTxsCurrentModData);
      getJdbcClient().runBatchUpdate(connection, prepareSql(DELETE_TXS, false), addressingToDelete);

      findObsoleteGammas();
      console.writeln("gamma join size: %d", gammaJoin.size());

      console.writeln("Number of artifact version rows deleted: %d",
         getJdbcClient().runBatchUpdate(connection, DELETE_ARTIFACT_VERSIONS, deleteArtifactVersionData));
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
      int count = getJdbcClient().runPreparedUpdate(connection, String.format(UPDATE_CONFLICTS, columnName));
      console.writeln("updated %s in %d rows", columnName, count);
   }

   private void findObsoleteGammas() throws OseeCoreException {
      try {
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, SELECT_ARTIFACT_VERSIONS);
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
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE,
            String.format(SELECT_ADDRESSING, archived ? "_archived" : ""), gammaJoin.getQueryId());

         while (chStmt.next()) {
            long obsoleteGammaId = chStmt.getLong("gamma_id");
            Long transactionId = chStmt.getLong("transaction_id");
            long branchUuid = chStmt.getLong("branch_id");
            long netGammaId = chStmt.getLong("net_gamma_id");
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
            TxChange.getChangeType(chStmt.getInt("tx_current"));

            if (isNextArtifactGroup(netGammaId, branchUuid)) {
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
                  if (!previuosTransactionId.equals(transactionId)) { // can't use a gamma (netGammaId) more than once in a transaction
                     addToUpdateAddresssing(modType, netGammaId, modType, transactionId, obsoleteGammaId);
                  }
               } else {
                  throw new OseeStateException("unexpected mod type [%s]", modType);
               }
            }

            previousNetGammaId = netGammaId;
            previousBranchId = branchUuid;
            previuosTransactionId = transactionId;
         }
      } finally {
         chStmt.close();
      }

      writeAddressingChanges(archived, true);
   }

   private void addToUpdateAddresssing(ModificationType netModType, long netGammaId, ModificationType modType, Long transactionId, long obsoleteGammaId) {
      if (obsoleteGammaId != netGammaId || modType != netModType) {
         updateAddressingData.add(new Object[] {netGammaId, netModType.getValue(), transactionId, obsoleteGammaId});
      }
   }

   private boolean isNextArtifactGroup(long netGammaId, long branchUuid) {
      return previousNetGammaId != netGammaId || previousBranchId != branchUuid;
   }

   private String prepareSql(String sql, boolean archived) {
      return String.format(sql, archived ? "_archived" : "");
   }

   private void writeAddressingChanges(boolean archived, boolean force) throws OseeCoreException {
      String archivedStr = archived ? "_archived" : "";
      if (addressingToDelete.size() > 99960 || force) {
         deleteTxsCounter +=
            getJdbcClient().runBatchUpdate(connection, prepareSql(DELETE_TXS, archived), addressingToDelete);
         console.writeln("Number of txs%s rows deleted: %d", archivedStr, deleteTxsCounter);
         addressingToDelete.clear();
      }

      if (updateAddressingData.size() > 99960 || force) {
         updateTxsCounter +=
            getJdbcClient().runBatchUpdate(connection, prepareSql(UPDATE_TXS_GAMMAS, archived), updateAddressingData);
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