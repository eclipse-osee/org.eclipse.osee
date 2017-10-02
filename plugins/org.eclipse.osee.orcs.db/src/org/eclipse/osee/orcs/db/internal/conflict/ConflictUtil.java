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
package org.eclipse.osee.orcs.db.internal.conflict;

import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public final class ConflictUtil {

   private ConflictUtil() {
      // Utility Class
   }

   public static Conflict createConflict(MergeBranch mergeBranch, ChangeItem changeItem, ConflictStatus conflictStatus)  {
      ConflictType conflictType = ConflictUtil.toConflictType(changeItem);
      return new Conflict(StorageState.CREATED, changeItem.getItemId(), conflictType, mergeBranch, conflictStatus,
         changeItem.getCurrentVersion().getGammaId(), changeItem.getDestinationVersion().getGammaId());
   }

   public static boolean areGammasEqual(Conflict object1, Conflict object2) {
      boolean result = false;
      if (object1 == null && object2 == null) {
         result = true;
      } else if (object1 != null && object2 != null) {
         result = object1.getSourceGammaId().equals(
            object2.getSourceGammaId()) && object1.getDestinationGammaId().equals(object2.getDestinationGammaId());
      }
      return result;
   }

   public static ConflictStatus computeNetStatus(Conflict newConflict, Conflict storedConflict) {
      ConflictStatus netStatus = newConflict.getStatus();

      ConflictStatus storedStatus = storedConflict.getStatus();

      if (storedConflict.getStatus().isIgnoreable() || newConflict.getStatus().isIgnoreable()) {
         netStatus = newConflict.getStatus();
      } else if (!areGammasEqual(newConflict, storedConflict) && !storedStatus.isCommitted()) {
         if (storedStatus.isResolved() || storedStatus.isPreviousMergeSuccessfullyApplied()) {
            netStatus = ConflictStatus.OUT_OF_DATE_RESOLVED;
         }
         if (storedStatus.isEdited() || storedStatus.isPreviousMergeAppliedWithCaution()) {
            netStatus = ConflictStatus.OUT_OF_DATE;
         }
         //         ConnectionHandler.runPreparedUpdate(MERGE_UPDATE_GAMMAS, sourceGamma, destGamma, intStatus, branchID,
         //               objectID, conflictType);
         if (ConflictType.ATTRIBUTE == newConflict.getType()) {
            //            ConnectionHandler.runPreparedUpdate(MERGE_BRANCH_GAMMAS, sourceGamma, transactionId, objectID);
         }
      }
      return netStatus;
   }

   public static ConflictType toConflictType(ChangeItem item)  {
      ConflictType type = null;
      switch (item.getChangeType()) {
         case ARTIFACT_CHANGE:
            type = ConflictType.ARTIFACT;
            break;
         case ATTRIBUTE_CHANGE:
            type = ConflictType.ATTRIBUTE;
            break;
         case RELATION_CHANGE:
            type = ConflictType.RELATION;
            break;
         default:
            throw new OseeArgumentException("Unable to convert change item [%s] to conflict type", item);
      }
      return type;
   }

   // private static final String MERGE_BRANCH_GAMMAS = //
   //      "UPDATE osee_txs SET gamma_id = ? where (transaction_id, gamma_id) = " + //
   //      "(SELECT tx.transaction_id, tx.gamma_id FROM osee_txs tx, osee_attribute atr " + //
   //      "WHERE tx.transaction_id = ? AND atr.gamma_id = tx.gamma_id AND atr.attr_id = ? )";

   //   public static ConflictStatus computeStatus(int sourceGamma, int destGamma, int branchID, int objectID, int conflictType, ConflictStatus passedStatus, int transactionId) throws OseeDataStoreException {
   //      //Check for a value in the table, if there is not one in there then
   //      //add it with an unedited setting and return unedited
   //      //If gammas are out of date, update the gammas and down grade markedMerged to Edited
   //
   //      JdbcStatement chStmt = ConnectionHandler.getStatement();
   //      try {
   //         chStmt.runPreparedQuery(MERGE_ATTRIBUTE_STATUS, branchID, objectID, conflictType);
   //
   //         if (chStmt.next()) {
   //            //There was an entry so lets check it and update it.
   //            int intStatus = chStmt.getInt("status");
   //            if ((chStmt.getInt("source_gamma_id") != sourceGamma || chStmt.getInt("dest_gamma_id") != destGamma) && intStatus != ConflictStatus.COMMITTED.getValue()) {
   //               if (intStatus == ConflictStatus.RESOLVED.getValue() || intStatus == ConflictStatus.PREVIOUS_MERGE_APPLIED_SUCCESS.getValue()) {
   //                  intStatus = ConflictStatus.OUT_OF_DATE_RESOLVED.getValue();
   //               }
   //               if (intStatus == ConflictStatus.EDITED.getValue() || intStatus == ConflictStatus.PREVIOUS_MERGE_APPLIED_CAUTION.getValue()) {
   //                  intStatus = ConflictStatus.OUT_OF_DATE.getValue();
   //               }
   //               ConnectionHandler.runPreparedUpdate(MERGE_UPDATE_GAMMAS, sourceGamma, destGamma, intStatus, branchID,
   //                     objectID, conflictType);
   //               if (conflictType == ConflictType.ATTRIBUTE.getValue()) {
   //                  ConnectionHandler.runPreparedUpdate(MERGE_BRANCH_GAMMAS, sourceGamma, transactionId, objectID);
   //               }
   //            }
   //            if (intStatus == ConflictStatus.NOT_RESOLVABLE.getValue() || intStatus == ConflictStatus.INFORMATIONAL.getValue() || passedStatus == ConflictStatus.NOT_RESOLVABLE || passedStatus == ConflictStatus.INFORMATIONAL) {
   //               intStatus = passedStatus.getValue();
   //            }
   //            return ConflictStatus.getStatus(intStatus);
   //         }
   //         // add the entry to the table and set as UNTOUCHED
   //      } finally {
   //         chStmt.close();
   //      }
   //      ConnectionHandler.runPreparedUpdate(MERGE_INSERT_STATUS, objectID, branchID, sourceGamma, destGamma,
   //            passedStatus.getValue(), conflictType);
   //
   //      return passedStatus;
   //   }

}
