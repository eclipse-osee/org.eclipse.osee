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
package org.eclipse.osee.framework.server.admin.management;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.JoinUtility.ExportImportJoinQuery;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Ryan D. Brooks
 */
public class ConsolidateArtifactVersionTxOperation extends AbstractDbTxOperation {
   private static final String SELECT_ARTIFACT_VERSIONS =
         "select * from osee_artifact_version order by art_id, gamma_id";

   private static final String SELECT_ADDRESSING =
         "select txs.*, idj.id1 as net_gamma_id from osee_join_export_import idj, osee_txs%s txs where idj.query_id = ? and idj.id2 = txs.gamma_id order by net_gamma_id, branch_id, transaction_id, gamma_id desc";

   private static final String SELECT_CONFLICTS =
         "select con.merge_branch_id, con.source_gamma_id, con.%s as obsolete_gamma_id, idj.id1 as net_gamma_id from osee_join_export_import idj, osee_conflict con where idj.query_id = ? and idj.id2 = con.obsolete_gamma_id";

   private static final String UPDATE_CONFLICTS =
         "update osee_conflict set %s = ? where merge_branch_id = ?, con.source_gamma_id = ?";

   private static final String UPDATE_TXS_GAMMAS =
         "update osee_txs%s set gamma_id = ?, mod_type = ? where transaction_id = ? and gamma_id = ?";

   private static final String DELETE_TXS = "delete from osee_txs%s where transaction_id = ? and gamma_id = ?";

   private static final String DELETE_ARTIFACT_VERSIONS = "delete from osee_artifact_version where gamma_id = ?";

   private List<Long[]> deleteArtifactVersionData;
   private final List<Long> obsoleteGammas = new ArrayList<Long>();
   private final List<Object[]> addressingToDelete = new ArrayList<Object[]>(13000);
   private final List<Object[]> updateAddressingData = new ArrayList<Object[]>(5000);
   private final List<Object[]> updateConflictsData = new ArrayList<Object[]>(5000);
   private ExportImportJoinQuery gammaJoin;
   private OseeConnection connection;
   private int previousArtifactId;
   private long netGamma;
   private IOseeStatement chStmt;
   private long previousNetGammaId;
   private int previousBranchId;
   private int previuosTransactionId;
   private final boolean includeArchived;
   private final CommandInterpreter ci;
   private int updateTxsCounter;
   private int deleteTxsCounter;

   public ConsolidateArtifactVersionTxOperation(IOseeDatabaseServiceProvider provider, CommandInterpreter ci) {
      super(provider, "Consolidate Artifact Versions", Activator.PLUGIN_ID);
      this.ci = ci;
      this.includeArchived = Boolean.parseBoolean(ci.nextArgument());
   }

   private void init() throws OseeCoreException {
      deleteArtifactVersionData = new ArrayList<Long[]>(14000);
      obsoleteGammas.clear();
      updateAddressingData.clear();
      addressingToDelete.clear();
      previousNetGammaId = -1;
      previousBranchId = -1;
      previuosTransactionId = -1;
      updateTxsCounter = 0;
      deleteTxsCounter = 0;
      chStmt = getDatabaseService().getStatement(connection);
      gammaJoin = JoinUtility.createExportImportJoinQuery();
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      this.connection = connection;
      // also osee_conflict
      init();

      findObsoleteGammas();
      ci.println("gamma join size: " + gammaJoin.size());

      ci.println("Number of artifact version rows deleted: " + getDatabaseService().runBatchUpdate(connection,
            DELETE_ARTIFACT_VERSIONS, deleteArtifactVersionData));
      deleteArtifactVersionData = null;

      updataConflicts("source_gamma_id");
      updataConflicts("dest_gamma_id");

      gammaJoin.store(connection);
      determineAffectedAddressing(false);
      if (includeArchived) {
         determineAffectedAddressing(true);
      }

      gammaJoin.delete(connection);

   }

   private void updataConflicts(String columnName) throws OseeCoreException {
      updateConflictsData.clear();
      try {
         chStmt.runPreparedQuery(10000, String.format(SELECT_CONFLICTS, columnName), gammaJoin.getQueryId());
         while (chStmt.next()) {
            chStmt.getLong("obsolete_gamma_id");
            updateConflictsData.add(new Object[] {chStmt.getLong("net_gamma_id"), chStmt.getInt("merge_branch_id"),
                  chStmt.getLong("source_gamma_id")});
         }
      } finally {
         if (chStmt != null) {
            chStmt.close();
         }
      }
      getDatabaseService().runBatchUpdate(connection, String.format(UPDATE_CONFLICTS, columnName), updateConflictsData);
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
         if (chStmt != null) {
            chStmt.close();
         }
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

   private void determineAffectedAddressing(boolean archived) throws OseeCoreException {
      try {
         ci.println("query id: " + gammaJoin.getQueryId());
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
               if (modType == ModificationType.MODIFIED) {
                  addToUpdateAddresssing(ModificationType.NEW, netGammaId, modType, transactionId, obsoleteGammaId);
               } else if (modType == ModificationType.NEW || modType == ModificationType.INTRODUCED || modType == ModificationType.DELETED || modType == ModificationType.MERGED) {
                  addToUpdateAddresssing(modType, netGammaId, modType, transactionId, obsoleteGammaId);
               } else {
                  throw new OseeStateException("unexpected mod type: " + modType);
               }
            } else {
               if (modType == ModificationType.MODIFIED || modType == ModificationType.NEW || modType == ModificationType.INTRODUCED) {
                  addressingToDelete.add(new Object[] {transactionId, obsoleteGammaId});
               } else if (modType == ModificationType.DELETED || modType == ModificationType.MERGED) {
                  if (previuosTransactionId != transactionId) { // can't use a gamma (netGammaId) more than once in a transaction
                     addToUpdateAddresssing(modType, netGammaId, modType, transactionId, obsoleteGammaId);
                  }
               } else {
                  throw new OseeStateException("unexpected mod type: " + modType);
               }
            }

            previousNetGammaId = netGammaId;
            previousBranchId = branchId;
            previuosTransactionId = transactionId;
         }
      } finally {
         if (chStmt != null) {
            chStmt.close();
         }
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

   private void writeAddressingChanges(boolean archived, boolean force) throws OseeDataStoreException {
      String archivedStr = archived ? "_archived" : "";
      if (addressingToDelete.size() > 99960 || force) {
         deleteTxsCounter +=
               getDatabaseService().runBatchUpdate(connection, String.format(DELETE_TXS, archivedStr),
                     addressingToDelete);
         ci.println("Number of txs" + archivedStr + " rows deleted: " + deleteTxsCounter);
         addressingToDelete.clear();
      }

      if (updateAddressingData.size() > 99960 || force) {
         updateTxsCounter +=
               getDatabaseService().runBatchUpdate(connection, String.format(UPDATE_TXS_GAMMAS, archivedStr),
                     updateAddressingData);
         ci.println("Number of txs" + archivedStr + " rows updated: " + updateTxsCounter);

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