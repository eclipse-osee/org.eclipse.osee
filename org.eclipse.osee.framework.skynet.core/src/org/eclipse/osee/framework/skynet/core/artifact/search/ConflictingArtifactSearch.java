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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Robert A. Fisher
 */
public class ConflictingArtifactSearch implements ISearchPrimitive {
   private static final String TOKEN = ";";
   private static final String CONFLICT_TABLE =
         "((SELECT art_id FROM osee_artifact_version t1, osee_txs t2, osee_tx_details t3 WHERE t3.branch_id = ? AND t3.transaction_id > ? AND t3.transaction_id <= ? AND t3.transaction_id = t2.transaction_id AND t2.gamma_id = t1.gamma_id union all " +

         "SELECT a_art_id as art_id FROM osee_relation_link t1, osee_txs t2, osee_tx_details t3 WHERE t3.branch_id = ? AND t3.transaction_id > ?  AND t3.transaction_id <= ?  AND t3.transaction_id = t2.transaction_id  AND t2.gamma_id = t1.gamma_id union all " +

         "SELECT b_art_id as art_id FROM osee_relation_link t1, osee_txs t2, osee_tx_details t3 WHERE t3.branch_id = ? AND t3.transaction_id > ?  AND t3.transaction_id <= ?  AND t3.transaction_id = t2.transaction_id  AND t2.gamma_id = t1.gamma_id) " +

         " intersect " +

         "(SELECT art_id FROM osee_artifact_version t1, osee_txs t2, osee_tx_details t3 WHERE t3.branch_id = ?  AND t3.transaction_id > ?  AND t3.transaction_id <= ?  AND t3.transaction_id = t2.transaction_id  AND t2.gamma_id = t1.gamma_id union all " +

         "SELECT a_art_id as art_id FROM osee_relation_link t1, osee_txs t2, osee_tx_details t3 WHERE t3.branch_id = ?  AND t3.transaction_id > ?  AND t3.transaction_id <= ?  AND t3.transaction_id = t2.transaction_id  AND t2.gamma_id = t1.gamma_id union all " +

         "SELECT b_art_id as art_id FROM osee_relation_link t1, osee_txs t2, osee_tx_details t3 WHERE t3.branch_id = ?  AND t3.transaction_id > ?  AND t3.transaction_id <= ?  AND t3.transaction_id = t2.transaction_id  AND t2.gamma_id = t1.gamma_id)) tcd1";

   private final int parentBranchId;
   private final int parentBaseTransactionNumber;
   private final int parentHeadTransactionNumber;
   private final int childBranchId;
   private final int childBaseTransactionNumber;
   private final int childHeadTransactionNumber;

   /**
    * @param parentBranchId
    * @param parentBaseTramsactionNumber
    * @param parentHeadTransactionNumber
    * @param childBranchId
    * @param childBaseTransactionNumber
    * @param childHeadTransactionNumber
    */
   public ConflictingArtifactSearch(int parentBranchId, int parentBaseTramsactionNumber, int parentHeadTransactionNumber, int childBranchId, int childBaseTransactionNumber, int childHeadTransactionNumber) {
      super();
      this.parentBranchId = parentBranchId;
      this.parentBaseTransactionNumber = parentBaseTramsactionNumber;
      this.parentHeadTransactionNumber = parentHeadTransactionNumber;
      this.childBranchId = childBranchId;
      this.childBaseTransactionNumber = childBaseTransactionNumber;
      this.childHeadTransactionNumber = childHeadTransactionNumber;
   }

   public String getArtIdColName() {
      return "art_id";
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      return "";
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      dataList.add(parentBranchId);
      dataList.add(parentBaseTransactionNumber);
      dataList.add(parentHeadTransactionNumber);
      dataList.add(parentBranchId);
      dataList.add(parentBaseTransactionNumber);
      dataList.add(parentHeadTransactionNumber);
      dataList.add(parentBranchId);
      dataList.add(parentBaseTransactionNumber);
      dataList.add(parentHeadTransactionNumber);
      dataList.add(childBranchId);
      dataList.add(childBaseTransactionNumber);
      dataList.add(childHeadTransactionNumber);
      dataList.add(childBranchId);
      dataList.add(childBaseTransactionNumber);
      dataList.add(childHeadTransactionNumber);
      dataList.add(childBranchId);
      dataList.add(childBaseTransactionNumber);
      dataList.add(childHeadTransactionNumber);

      return CONFLICT_TABLE;
   }

   @Override
   public String toString() {
      String parentBranch;
      String childBranch;

      try {
         parentBranch = BranchManager.getBranch(parentBranchId).getName();
         childBranch = BranchManager.getBranch(childBranchId).getName();
      } catch (Exception ex) {
         parentBranch = Integer.toString(parentBranchId);
         childBranch = Integer.toString(childBranchId);
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      return "Parent Branch:" + parentBranch + " transactions " + parentBaseTransactionNumber + " to " + parentHeadTransactionNumber + "\nChild Branch:" + childBranch + " transactions " + childBaseTransactionNumber + " to " + childHeadTransactionNumber;

   }

   public String getStorageString() {
      return parentBranchId + TOKEN + parentBaseTransactionNumber + TOKEN + parentHeadTransactionNumber + TOKEN + childBranchId + TOKEN + childBaseTransactionNumber + TOKEN + childHeadTransactionNumber;
   }

   public static ConflictingArtifactSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length != 6) {
         throw new IllegalStateException(
               "Value for " + ConflictingArtifactSearch.class.getSimpleName() + " not parsable");
      }

      return new ConflictingArtifactSearch(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
            Integer.parseInt(values[2]), Integer.parseInt(values[3]), Integer.parseInt(values[4]),
            Integer.parseInt(values[5]));
   }
}
