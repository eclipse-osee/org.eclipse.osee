/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.callable;

import java.sql.Timestamp;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.Triplet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.accessor.UpdatePreviousTxCurrent;

/**
 * @author Roberto E. Escobar
 */
public class DeleteRelationDatabaseCallable extends AbstractDatastoreTxCallable<Branch> {

   private final static String SELECT_RELATION_LINK =
      "select txs.gamma_id, rel.rel_link_id, txs.mod_type from osee_relation_link rel, osee_txs txs where rel.rel_link_type_id = ? and rel.a_art_id = ? and rel.b_art_id = ? and rel.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.tx_current <> ? order by txs.tx_current";

   private final static String INSERT_INTO_TX_DETAILS =
      "insert into osee_tx_details (branch_id, transaction_id, osee_comment, time, author, tx_type) values (?,?,?,?,?,?)";

   private final static String INSERT_INTO_TXS =
      "insert into osee_txs (mod_type, tx_current, transaction_id, gamma_id, branch_id) values (?, ?, ?, ?, ?)";

   private final IdentityLocator identityService;
   private final BranchCache branchCache;
   private final IOseeBranch branchToken;

   private final int artUserId;
   private final IRelationTypeSide relationType;
   private final int aArtId;
   private final int bArtId;
   private final String comment;

   public DeleteRelationDatabaseCallable(Log logger, OrcsSession session, IOseeDatabaseService databaseService, IdentityLocator identityService, BranchCache branchCache, IOseeBranch branchToken, IRelationTypeSide relationType, int aArtId, int bArtId, int artUserId, String comment) {
      super(logger, session, databaseService, "Delete Relation");
      this.identityService = identityService;
      this.branchCache = branchCache;
      this.branchToken = branchToken;

      this.relationType = relationType;
      this.aArtId = aArtId;
      this.bArtId = bArtId;
      this.artUserId = artUserId;
      this.comment = comment;

   }

   @Override
   protected Branch handleTxWork(OseeConnection connection) throws OseeCoreException {
      Branch branch = branchCache.get(branchToken);
      Triplet<Integer, Integer, Integer> relIdModTypeGammaId =
         getRelationModAndGammaTxData(branch, relationType, aArtId, bArtId);

      int modType = relIdModTypeGammaId.getSecond();
      if (modType != ModificationType.ARTIFACT_DELETED.getValue() && modType != ModificationType.DELETED.getValue()) {
         UpdatePreviousTxCurrent txc = new UpdatePreviousTxCurrent(getDatabaseService(), connection, branch.getId());
         txc.addRelation(relIdModTypeGammaId.getFirst());
         txc.updateTxNotCurrents();

         int currentGammaId = relIdModTypeGammaId.getThird();
         createNewTxAddressing(connection, branch, comment, artUserId, currentGammaId);
      } else {
         // Already deleted - Do NOTHING
      }
      return branch;
   }

   private Triplet<Integer, Integer, Integer> getRelationModAndGammaTxData(Branch commonBranch, IRelationTypeSide relationType, int aArtId, int bArtId) throws OseeCoreException {
      int relationTypeId = identityService.getLocalId(relationType);
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(1, SELECT_RELATION_LINK, relationTypeId, aArtId, bArtId, commonBranch.getId(),
            TxChange.NOT_CURRENT.getValue());
         if (chStmt.next()) {
            int relationId = chStmt.getInt("rel_link_id");
            int modType = chStmt.getInt("mod_type");
            int currentGammaId = chStmt.getInt("gamma_id");
            return new Triplet<Integer, Integer, Integer>(relationId, modType, currentGammaId);
         } else {
            throw new OseeStateException(
               "No existing relation (deleted or otherwise) was found for [%s] with aArtId:[%s] and bArtId:[%s].",
               relationType, aArtId, bArtId);
         }
      } finally {
         Lib.close(chStmt);
      }
   }

   @SuppressWarnings("unchecked")
   private void createNewTxAddressing(OseeConnection connection, Branch commonBranch, String comment, int userId, int currentGammaId) throws OseeCoreException {
      int transactionId = getDatabaseService().getSequence().getNextTransactionId();

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      int txType = TransactionDetailsType.NonBaselined.getId();
      getDatabaseService().runPreparedUpdate(connection, INSERT_INTO_TX_DETAILS, commonBranch.getId(), transactionId,
         comment, timestamp, userId, txType);
      getDatabaseService().runPreparedUpdate(connection, INSERT_INTO_TXS, ModificationType.DELETED.getValue(),
         TxChange.DELETED.getValue(), transactionId, currentGammaId, commonBranch.getId());
   }
}