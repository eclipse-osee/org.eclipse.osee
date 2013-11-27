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
package org.eclipse.osee.orcs.db.internal.transaction;

import java.sql.Timestamp;
import org.eclipse.osee.database.schema.DatabaseTxCallable;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.internal.accessor.UpdatePreviousTxCurrent;

public final class UnsubscribeTransaction extends DatabaseTxCallable<String> {
   private final static String SELECT_RELATION_LINK =
      "select txs.gamma_id, rel.rel_link_id, txs.mod_type from osee_relation_link rel, osee_txs txs where rel.a_art_id = ? and rel.b_art_id = ? and rel.rel_link_type_id = ? and rel.gamma_id=txs.gamma_id and txs.branch_id = ? and txs.tx_current <> ? order by txs.tx_current";
   private final static String INSERT_INTO_TX_DETAILS =
      "insert into osee_tx_details (branch_id, transaction_id, osee_comment, time, author, tx_type) values (?,?,?,?,?,?)";
   private final static String INSERT_INTO_TXS =
      "insert into osee_txs (mod_type, tx_current, transaction_id, gamma_id, branch_id) values (?, ?, ?, ?, ?)";

   private int relationId;
   private int currentGammaId;
   private final ArtifactReadable groupArtifact;
   private final ArtifactReadable userArtifact;
   private String completionMethod;

   public UnsubscribeTransaction(Log logger, IOseeDatabaseService databaseService, ArtifactReadable userArtifact, ArtifactReadable groupArtifact) {
      super(logger, databaseService, "Delete Relation");
      this.groupArtifact = groupArtifact;
      this.userArtifact = userArtifact;
   }

   private ArtifactReadable getGroup() {
      return groupArtifact;
   }

   private ArtifactReadable getUser() {
      return userArtifact;
   }

   @Override
   protected String handleTxWork(OseeConnection connection) throws OseeCoreException {
      int branchId =
         getDatabaseService().runPreparedQueryFetchObject(-2,
            "select branch_id from osee_branch where branch_guid = ?", CoreBranches.COMMON.getGuid());
      Conditions.checkExpressionFailOnTrue(branchId == -2, "Common branch was not found");

      if (getRelationTxData(branchId)) {
         UpdatePreviousTxCurrent txc = new UpdatePreviousTxCurrent(getDatabaseService(), connection, branchId);
         txc.addRelation(relationId);
         txc.updateTxNotCurrents();

         createNewTxAddressing(connection, branchId);
      }
      return completionMethod;
   }

   private boolean getRelationTxData(long branchId) throws OseeCoreException {
      long relationTypeId = CoreRelationTypes.Users_Artifact.getGuid();
      IOseeStatement chStmt = getDatabaseService().getStatement();

      try {
         chStmt.runPreparedQuery(1, SELECT_RELATION_LINK, getGroup().getLocalId(), getUser().getLocalId(),
            relationTypeId, branchId, TxChange.NOT_CURRENT.getValue());
         if (chStmt.next()) {
            currentGammaId = chStmt.getInt("gamma_id");
            relationId = chStmt.getInt("rel_link_id");
            int modType = chStmt.getInt("mod_type");
            return ensureNotAlreadyDeleted(modType);
         } else {
            throw new OseeStateException(
               "No existing relation (deleted or otherwise) was found for group [%s] and user [%s].",
               getGroup().getLocalId(), getGroup().getLocalId());
         }
      } finally {
         Lib.close(chStmt);
      }
   }

   private boolean ensureNotAlreadyDeleted(int modType) {
      if (modType == ModificationType.ARTIFACT_DELETED.getValue() || modType == ModificationType.DELETED.getValue()) {
         completionMethod =
            String.format("<br/>You have already been removed from the group.<br/>  group [%s] user [%s]", getGroup(),
               getUser());
         return false;
      } else {
         completionMethod = String.format("<br/>You have been successfully unsubscribed.");
         return true;
      }
   }

   @SuppressWarnings("unchecked")
   private void createNewTxAddressing(OseeConnection connection, int branchId) throws OseeCoreException {
      int transactionId = getDatabaseService().getSequence().getNextTransactionId();
      String comment =
         String.format("User %s requested unsubscribe from group %s", getUser().getLocalId(), getGroup().getLocalId());
      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      int txType = TransactionDetailsType.NonBaselined.getId();

      getDatabaseService().runPreparedUpdate(connection, INSERT_INTO_TX_DETAILS, branchId, transactionId, comment,
         timestamp, getUser().getLocalId(), txType);
      getDatabaseService().runPreparedUpdate(connection, INSERT_INTO_TXS, ModificationType.DELETED.getValue(),
         TxChange.DELETED.getValue(), transactionId, currentGammaId, branchId);
   }

}