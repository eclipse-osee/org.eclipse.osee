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
package org.eclipse.osee.framework.branch.management.change;

import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ArtifactChangeItem;
import org.eclipse.osee.framework.core.message.AttributeChangeItem;
import org.eclipse.osee.framework.core.message.ChangeItem;
import org.eclipse.osee.framework.core.message.RelationChangeItem;
import org.eclipse.osee.framework.core.model.MergeBranch;

/**
 * @author Roberto E. Escobar
 */
public final class ConflictUtil {

   private ConflictUtil() {
   }

   public static Conflict createConflict(MergeBranch mergeBranch, ChangeItem changeItem, ConflictStatus conflictStatus) throws OseeCoreException {
      ConflictType conflictType = ConflictUtil.toConflictType(changeItem);
      return new Conflict(StorageState.CREATED, changeItem.getItemId(), conflictType, mergeBranch, conflictStatus,
            changeItem.getCurrentVersion().getGammaId(), changeItem.getDestinationVersion().getGammaId());
   }

   public static boolean areGammasEqual(Conflict object1, Conflict object2) {
      boolean result = false;
      if (object1 == null && object2 == null) {
         result = true;
      } else if (object1 != null && object2 != null) {
         result =
               object1.getSourceGammaId().equals(object2.getSourceGammaId()) && object1.getDestinationGammaId().equals(
                     object2.getDestinationGammaId());
      }
      return result;
   }

   public static ConflictStatus computeNetStatus(Conflict newConflict, Conflict storedConflict) {
      ConflictStatus netStatus = newConflict.getStatus();

      ConflictStatus storedStatus = storedConflict.getStatus();

      if (storedConflict.getStatus().isIgnoreable() || newConflict.getStatus().isIgnoreable()) {
         netStatus = newConflict.getStatus();
      } else if (!areGammasEqual(newConflict, storedConflict) && !storedStatus.isCommitted()) {
         if (storedStatus.isResolved() || storedStatus.wasPreviousMergeSuccessfullyApplied()) {
            netStatus = ConflictStatus.OUT_OF_DATE_RESOLVED;
         }
         if (storedStatus.hasBeenEdited() || storedStatus.wasPreviousMergeAppliedWithCaution()) {
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

   public static ConflictType toConflictType(ChangeItem item) throws OseeCoreException {
      ConflictType type = null;
      if (item instanceof ArtifactChangeItem) {
         type = ConflictType.ARTIFACT;
      } else if (item instanceof AttributeChangeItem) {
         type = ConflictType.ATTRIBUTE;
      } else if (item instanceof RelationChangeItem) {
         type = ConflictType.RELATION;
      } else {
         throw new OseeArgumentException(String.format("Unable to convert change item [%s] to conflict type", item));
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
   //      IOseeStatement chStmt = ConnectionHandler.getStatement();
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
