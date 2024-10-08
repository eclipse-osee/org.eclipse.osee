/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.conflict;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Theron Virgin
 */
public class ConflictStatusManager {

   private static final String MERGE_UPDATE_STATUS =
      "UPDATE osee_conflict SET status = ? WHERE source_gamma_id = ? AND dest_gamma_id = ? AND merge_branch_id = ?";
   private static final String MERGE_INSERT_STATUS =
      "insert into osee_conflict ( conflict_id, merge_branch_id, source_gamma_id, dest_gamma_id, status, conflict_type) VALUES ( ?, ?, ?, ?, ?, ?)";

   private static final String MERGE_ATTRIBUTE_STATUS =
      "SELECT source_gamma_id, dest_gamma_id, status FROM osee_conflict WHERE merge_branch_id = ? AND conflict_id = ? AND conflict_type = ?";
   private static final String MERGE_UPDATE_GAMMAS =
      "UPDATE osee_conflict SET source_gamma_id = ?, dest_gamma_id = ?, status = ? WHERE merge_branch_id = ? AND conflict_id = ? AND conflict_type = ?";
   private static final String MERGE_BRANCH_GAMMAS =
      "UPDATE osee_txs SET gamma_id = ? where (transaction_id, gamma_id) = (SELECT tx.transaction_id, tx.gamma_id FROM osee_txs tx, osee_attribute atr WHERE tx.branch_id = ? AND tx.transaction_id = ? AND atr.gamma_id = tx.gamma_id AND atr.attr_id = ? )";

   public static void setStatus(ConflictStatus status, GammaId sourceGamma, GammaId destGamma, BranchId mergeBranch) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      //Gammas should be up to date so you can use them to get entry just update the status field.
      try {
         ConnectionHandler.runPreparedUpdate(MERGE_UPDATE_STATUS, status.getValue(), sourceGamma, destGamma,
            mergeBranch);
      } finally {
         chStmt.close();
      }
   }

   public static ConflictStatus computeStatus(GammaId sourceGamma, GammaId destGamma, BranchId branch, Id objectID,
      int conflictType, ConflictStatus passedStatus, TransactionId transactionId) {
      //Check for a value in the table, if there is not one in there then
      //add it with an unedited setting and return unedited
      //If gammas are out of date, update the gammas and down grade markedMerged to Edited

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(MERGE_ATTRIBUTE_STATUS, branch, objectID, conflictType);

         if (chStmt.next()) {
            //There was an entry so lets check it and update it.
            int intStatus = chStmt.getInt("status");
            if (sourceGamma.notEqual(chStmt.getLong("source_gamma_id")) || destGamma.notEqual(
               chStmt.getLong("dest_gamma_id")) && intStatus != ConflictStatus.COMMITTED.getValue()) {
               if (intStatus == ConflictStatus.RESOLVED.getValue() || intStatus == ConflictStatus.PREVIOUS_MERGE_APPLIED_SUCCESS.getValue()) {
                  intStatus = ConflictStatus.OUT_OF_DATE_RESOLVED.getValue();
               }
               if (intStatus == ConflictStatus.EDITED.getValue() || intStatus == ConflictStatus.PREVIOUS_MERGE_APPLIED_CAUTION.getValue()) {
                  intStatus = ConflictStatus.OUT_OF_DATE.getValue();
               }
               ConnectionHandler.runPreparedUpdate(MERGE_UPDATE_GAMMAS, sourceGamma, destGamma, intStatus, branch,
                  objectID, conflictType);
               if (conflictType == ConflictType.ATTRIBUTE.getValue()) {
                  ConnectionHandler.runPreparedUpdate(MERGE_BRANCH_GAMMAS, sourceGamma, branch, transactionId,
                     objectID);
               }
            }
            if (intStatus == ConflictStatus.INFORMATIONAL.getValue() || passedStatus == ConflictStatus.INFORMATIONAL) {
               intStatus = passedStatus.getValue();
            }
            return ConflictStatus.valueOf(intStatus);
         }
         // add the entry to the table and set as UNTOUCHED
      } finally {
         chStmt.close();
      }
      ConnectionHandler.runPreparedUpdate(MERGE_INSERT_STATUS, objectID, branch, sourceGamma, destGamma,
         passedStatus.getValue(), conflictType);

      return passedStatus;
   }

}
