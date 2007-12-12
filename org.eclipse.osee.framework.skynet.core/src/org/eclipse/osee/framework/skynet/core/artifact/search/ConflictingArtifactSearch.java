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

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

/**
 * @author Robert A. Fisher
 */
public class ConflictingArtifactSearch implements ISearchPrimitive {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ConflictingArtifactSearch.class);
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final String TOKEN = ";";
   private static final String CONFLICT_TABLE =
         "((SELECT art_id " + "FROM " + SkynetDatabase.ARTIFACT_VERSION_TABLE + " t1, " + "  " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + "  " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3 " + "WHERE t3.branch_id = ? " + " AND t3.transaction_id > ? " + " AND t3.transaction_id <= ? " + " AND t3.transaction_id = t2.transaction_id " + " AND t2.gamma_id = t1.gamma_id union all " +

         "SELECT a_art_id art_id " + "FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t1, " + "  " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + "  " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3 " + "WHERE t3.branch_id = ? " + " AND t3.transaction_id > ? " + " AND t3.transaction_id <= ? " + " AND t3.transaction_id = t2.transaction_id " + " AND t2.gamma_id = t1.gamma_id union all " +

         "SELECT b_art_id art_id " + "FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t1, " + "  " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + "  " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3 " + "WHERE t3.branch_id = ? " + " AND t3.transaction_id > ? " + " AND t3.transaction_id <= ? " + " AND t3.transaction_id = t2.transaction_id " + " AND t2.gamma_id = t1.gamma_id) " +

         " intersect " +

         "(SELECT art_id " + "FROM " + SkynetDatabase.ARTIFACT_VERSION_TABLE + " t1, " + "  " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + "  " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3 " + "WHERE t3.branch_id = ? " + " AND t3.transaction_id > ? " + " AND t3.transaction_id <= ? " + " AND t3.transaction_id = t2.transaction_id " + " AND t2.gamma_id = t1.gamma_id union all " +

         "SELECT a_art_id art_id " + "FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t1, " + "  " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + "  " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3 " + "WHERE t3.branch_id = ? " + " AND t3.transaction_id > ? " + " AND t3.transaction_id <= ? " + " AND t3.transaction_id = t2.transaction_id " + " AND t2.gamma_id = t1.gamma_id union all " +

         "SELECT b_art_id art_id " + "FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t1, " + "  " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + "  " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3 " + "WHERE t3.branch_id = ? " + " AND t3.transaction_id > ? " + " AND t3.transaction_id <= ? " + " AND t3.transaction_id = t2.transaction_id " + " AND t2.gamma_id = t1.gamma_id))";

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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "art_id";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.search.ISearchPrimitive#getSql()
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      return "";
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(parentBranchId);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(parentBaseTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(parentHeadTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(parentBranchId);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(parentBaseTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(parentHeadTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(parentBranchId);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(parentBaseTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(parentHeadTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(childBranchId);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(childBaseTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(childHeadTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(childBranchId);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(childBaseTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(childHeadTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(childBranchId);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(childBaseTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(childHeadTransactionNumber);

      return CONFLICT_TABLE;
   }

   @Override
   public String toString() {
      String parentBranch;
      String childBranch;

      try {
         parentBranch = branchManager.getBranch(parentBranchId).getBranchName();
         childBranch = branchManager.getBranch(childBranchId).getBranchName();
      } catch (SQLException ex) {
         parentBranch = Integer.toString(parentBranchId);
         childBranch = Integer.toString(childBranchId);
         logger.log(Level.SEVERE, ex.toString(), ex);
      }

      return "Parent Branch:" + parentBranch + " transactions " + parentBaseTransactionNumber + " to " + parentHeadTransactionNumber + "\n" + "Child Branch:" + childBranch + " transactions " + childBaseTransactionNumber + " to " + childHeadTransactionNumber;

   }

   public String getStorageString() {
      return parentBranchId + TOKEN + parentBaseTransactionNumber + TOKEN + parentHeadTransactionNumber + TOKEN + childBranchId + TOKEN + childBaseTransactionNumber + TOKEN + childHeadTransactionNumber;
   }

   public static ConflictingArtifactSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length != 6) throw new IllegalStateException(
            "Value for " + ConflictingArtifactSearch.class.getSimpleName() + " not parsable");

      return new ConflictingArtifactSearch(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
            Integer.parseInt(values[2]), Integer.parseInt(values[3]), Integer.parseInt(values[4]),
            Integer.parseInt(values[5]));
   }
}
