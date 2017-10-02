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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.sql.join.ExportImportJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Ryan D. Brooks
 */
public class ConsolidateRelationsDatabaseTxCallable extends AbstractDatastoreTxCallable<Object> {
   private static final String SELECT_RELATIONS =
      "select * from osee_relation_link order by rel_link_type_id, a_art_id, b_art_id, gamma_id";

   private static final String SELECT_RELATION_ADDRESSING =
      "select txs.*, idj.id1 as net_gamma_id from osee_join_export_import idj, osee_txs txs where idj.query_id = ? and idj.id2 = txs.gamma_id order by net_gamma_id, transaction_id, id2";

   private static final String UPDATE_TXS_GAMMAS =
      "update osee_txs set gamma_id = ?, mod_type = ?, tx_current = ? where branch_id = ? and transaction_id = ? and gamma_id = ?";

   private static final String DELETE_TXS =
      "delete from osee_txs where branch_id = ? and transaction_id = ? and gamma_id = ?";

   private static final String DELETE_RELATIONS = "delete from osee_relation_link where gamma_id = ?";

   private final List<Long> obsoleteGammas = new ArrayList<>();
   private final StringBuilder addressingBackup = new StringBuilder(100000);
   private OseePreparedStatement deleteAddressing;
   private OseePreparedStatement updateAddressing;
   private OseePreparedStatement deleteRelations;
   private ExportImportJoinQuery gammaJoin;
   private JdbcConnection connection;
   private long previousRelationTypeId;
   private int previousArtifactAId;
   private int previousArtiafctBId;
   private long netGamma;
   private String netRationale;
   boolean materiallyDifferent;
   boolean updatedAddressing;
   private int counter;

   private final SqlJoinFactory joinFactory;
   private final Console console;

   long previousNetGammaId;
   long previousObsoleteGammaId;
   long previousTransactionId;
   long previousBranchId;
   ModificationType netModType;
   TxChange netTxCurrent;

   public ConsolidateRelationsDatabaseTxCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, SqlJoinFactory joinFactory, Console console) {
      super(logger, session, jdbcClient);
      this.joinFactory = joinFactory;
      this.console = console;
   }

   private void init()  {
      previousRelationTypeId = -1;
      previousArtifactAId = -1;
      previousArtiafctBId = -1;
      materiallyDifferent = true;
      obsoleteGammas.clear();

      updateAddressing = getJdbcClient().getBatchStatement(connection, UPDATE_TXS_GAMMAS);
      deleteAddressing = getJdbcClient().getBatchStatement(connection, DELETE_TXS);
      deleteRelations = getJdbcClient().getBatchStatement(connection, DELETE_RELATIONS);
      addressingBackup.delete(0, 999999999);
      updatedAddressing = false;

      previousNetGammaId = -1;
      previousTransactionId = -1;
      previousBranchId = -1;
      gammaJoin = joinFactory.createExportImportJoinQuery();

      counter = 0;
   }

   @Override
   protected Object handleTxWork(JdbcConnection connection)  {
      this.connection = connection;
      console.writeln("Consolidating relations:");
      init();
      try {
         getJdbcClient().runQuery(this::findObsoleteRelations, JdbcConstants.JDBC__MAX_FETCH_SIZE, SELECT_RELATIONS);

         console.writeln("gamma join size: [%s]", gammaJoin.size());

         determineAffectedAddressing();
      } finally {
         gammaJoin.close();
      }

      updateGammas();
      console.writeln("...done.");
      return null;
   }

   private void findObsoleteRelations(JdbcStatement stmt) {
      long relationTypeId = stmt.getLong("rel_link_type_id");
      int artifactAId = stmt.getInt("a_art_id");
      int artiafctBId = stmt.getInt("b_art_id");

      if (isNextConceptualRelation(relationTypeId, artifactAId, artiafctBId)) {
         consolidate();
         initNextConceptualRelation(relationTypeId, artifactAId, artiafctBId, stmt.getLong("gamma_id"),
            stmt.getString("rationale"));
      } else {
         obsoleteGammas.add(stmt.getLong("gamma_id"));
         relationMateriallyDiffers(stmt.getString("rationale"));
      }
   }

   private void consolidate() {
      if (!materiallyDifferent && obsoleteGammas.size() > 0) {
         gammaJoin.add(netGamma, netGamma);
         for (Long obsoleteGamma : obsoleteGammas) {
            gammaJoin.add(netGamma, obsoleteGamma);
            deleteRelations.addToBatch(obsoleteGamma);
         }
      }
      if (materiallyDifferent) {
         counter++;
         console.writeln("rel_type:[%s] a_art_id:[%s]  b_art_id:[%s]", previousRelationTypeId, previousArtifactAId,
            previousArtiafctBId);
      }
   }

   private void determineAffectedAddressing()  {
      gammaJoin.store();

      console.writeln("counter: [%s]", counter);
      console.writeln("query id: [%s]", gammaJoin.getQueryId());
      getJdbcClient().runQuery(this::determineAffectedAddressing, JdbcConstants.JDBC__MAX_FETCH_SIZE,
         SELECT_RELATION_ADDRESSING, gammaJoin.getQueryId());
   }

   private void determineAffectedAddressing(JdbcStatement stmt) {
      long obsoleteGammaId = stmt.getLong("gamma_id");
      long transactionId = stmt.getLong("transaction_id");
      long netGammaId = stmt.getLong("net_gamma_id");
      int modType = stmt.getInt("mod_type");
      TxChange txCurrent = TxChange.valueOf(stmt.getInt("tx_current"));
      long branchId = stmt.getLong("branch_id");

      if (isNextAddressing(netGammaId, transactionId)) {
         if (updatedAddressing) {
            updateAddressing.addToBatch(previousNetGammaId, netModType, netTxCurrent, previousBranchId,
               previousTransactionId, previousObsoleteGammaId);
         }
         updatedAddressing = obsoleteGammaId != netGammaId;
         previousNetGammaId = netGammaId;
         previousObsoleteGammaId = obsoleteGammaId;
         previousTransactionId = transactionId;
         previousBranchId = branchId;
         netModType = ModificationType.valueOf(modType);
         netTxCurrent = txCurrent;
      } else {
         deleteAddressing.addToBatch(branchId, transactionId, obsoleteGammaId);
         computeNetAddressing(ModificationType.valueOf(modType), txCurrent);
      }

      writeAddressingBackup(obsoleteGammaId, transactionId, netGammaId, modType, txCurrent);
   }

   private boolean isNextAddressing(long netGammaId, long transactionId) {
      return previousNetGammaId != netGammaId || previousTransactionId != transactionId;
   }

   private void computeNetAddressing(ModificationType modificationType, TxChange txCurrent)  {
      if (netTxCurrentNeedsUpdate(txCurrent)) {
         netTxCurrent = txCurrent;
         updatedAddressing = true;
      }

      if (netModTypeNeedsUpdate(modificationType)) {
         netModType = modificationType;
         updatedAddressing = true;
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
      ignore |= netModType == ModificationType.INTRODUCED && modificationType == ModificationType.NEW;
      return ignore || netModType == ModificationType.ARTIFACT_DELETED && modificationType == ModificationType.DELETED;
   }

   private void updateGammas()  {
      console.writeln("Number of txs rows deleted: [%s]", deleteAddressing.execute());
      console.writeln("Number of relation rows deleted: [%s]", deleteRelations.execute());
      console.writeln("Number of txs rows updated: [%s]", updateAddressing.execute());
   }

   private void writeAddressingBackup(long obsoleteGammaId, long transactionId, long netGammaId, int modType, TxChange txCurrent) {
      StringBuilder strB = new StringBuilder(30);

      strB.append(obsoleteGammaId);
      strB.append(",");
      strB.append(transactionId);
      strB.append(",");
      strB.append(netGammaId);
      strB.append(",");
      strB.append(modType);
      strB.append(",");
      strB.append(txCurrent);
      strB.append("\n");
      console.writeln(strB.toString());
   }

   private boolean isNextConceptualRelation(long relationTypeId, int artifactAId, int artiafctBId) {
      return previousRelationTypeId != relationTypeId || previousArtifactAId != artifactAId || previousArtiafctBId != artiafctBId;
   }

   private void relationMateriallyDiffers(String currentRationale)  {
      if (!materiallyDifferent) {
         materiallyDifferent |= Strings.isValid(currentRationale) && !currentRationale.equals(netRationale);
      }
   }

   private void initNextConceptualRelation(long relationTypeId, int artifactAId, int artiafctBId, long gammaId, String rationale)  {
      obsoleteGammas.clear();
      previousRelationTypeId = relationTypeId;
      previousArtifactAId = artifactAId;
      previousArtiafctBId = artiafctBId;
      netGamma = gammaId;
      netRationale = rationale;
      materiallyDifferent = false;
   }
}