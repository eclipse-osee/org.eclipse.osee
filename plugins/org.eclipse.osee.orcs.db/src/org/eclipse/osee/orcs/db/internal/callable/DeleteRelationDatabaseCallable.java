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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.Triplet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.accessor.UpdatePreviousTxCurrent;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

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

   private final SqlJoinFactory joinFactory;
   private final IdentityManager idManager;
   private final BranchCache branchCache;
   private final IOseeBranch branchToken;

   private final int artUserId;
   private final IRelationTypeSide relationType;
   private final int aArtId;
   private final int bArtId;
   private final String comment;

   public DeleteRelationDatabaseCallable(Log logger, OrcsSession session, JdbcClient jdbcClient, SqlJoinFactory joinFactory, IdentityManager idManager, BranchCache branchCache, IOseeBranch branchToken, IRelationTypeSide relationType, int aArtId, int bArtId, int artUserId, String comment) {
      super(logger, session, jdbcClient);
      this.joinFactory = joinFactory;
      this.idManager = idManager;
      this.branchCache = branchCache;
      this.branchToken = branchToken;

      this.relationType = relationType;
      this.aArtId = aArtId;
      this.bArtId = bArtId;
      this.artUserId = artUserId;
      this.comment = comment;

   }

   @Override
   protected Branch handleTxWork(JdbcConnection connection) throws OseeCoreException {
      Branch branch = branchCache.get(branchToken);
      Triplet<Integer, Integer, Integer> relIdModTypeGammaId =
         getRelationModAndGammaTxData(branch, relationType, aArtId, bArtId);

      int modType = relIdModTypeGammaId.getSecond();
      if (modType != ModificationType.ARTIFACT_DELETED.getValue() && modType != ModificationType.DELETED.getValue()) {
         UpdatePreviousTxCurrent txc =
            new UpdatePreviousTxCurrent(getJdbcClient(), joinFactory, connection, branch.getUuid());
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
      long relationTypeId = relationType.getGuid();
      JdbcStatement chStmt = getJdbcClient().getStatement();
      try {
         chStmt.runPreparedQuery(1, SELECT_RELATION_LINK, relationTypeId, aArtId, bArtId, commonBranch.getUuid(),
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

   private void createNewTxAddressing(JdbcConnection connection, Branch commonBranch, String comment, int userId, int currentGammaId) throws OseeCoreException {
      int transactionId = idManager.getNextTransactionId();

      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      int txType = TransactionDetailsType.NonBaselined.getId();
      getJdbcClient().runPreparedUpdate(connection, INSERT_INTO_TX_DETAILS, commonBranch.getUuid(), transactionId,
         comment, timestamp, userId, txType);
      getJdbcClient().runPreparedUpdate(connection, INSERT_INTO_TXS, ModificationType.DELETED.getValue(),
         TxChange.DELETED.getValue(), transactionId, currentGammaId, commonBranch.getUuid());
   }
}