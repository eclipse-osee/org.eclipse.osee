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
package org.eclipse.osee.framework.branch.management.internal;

import java.sql.Timestamp;
import org.eclipse.osee.framework.branch.management.IBranchCreation;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;

/**
 * @author Andrew M. Finkbeiner
 */
public class BranchCreation implements IBranchCreation {

   private static final String COPY_BRANCH_ADDRESSING =
         "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, gamma_id, mod_type, tx_current FROM osee_txs txs1, osee_tx_details txd1 WHERE txs1.tx_current = 1 AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ?";

   private static final String BRANCH_TABLE_INSERT =
         "INSERT INTO osee_branch (branch_id, short_name, branch_name, parent_branch_id, parent_transaction_id, archived, associated_art_id, branch_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

   private static final String SELECT_BRANCH_BY_NAME = "SELECT count(1) FROM osee_branch WHERE branch_name = ?";

   private static final String INSERT_DEFAULT_BRANCH_NAMES =
         "INSERT INTO OSEE_BRANCH_DEFINITIONS (static_branch_name, mapped_branch_id) VALUES (?, ?)";

   private static final String INSERT_TX_DETAILS =
         "INSERT INTO osee_TX_DETAILS ( branch_id, transaction_id, OSEE_COMMENT, time, author, tx_type ) VALUES ( ?, ?, ?, ?, ?, ?)";

   public int createTopLevelBranch(int parentTransactionId, int parentBranchId, String childBranchShortName, String childBranchName, String creationComment, int associatedArtifactId, int authorId, String staticBranchName, boolean systemRootBranch) throws Exception {
      CreateTopLevelBranchTx createRootBranchTx =
            new CreateTopLevelBranchTx(parentTransactionId, parentBranchId, childBranchShortName, childBranchName,
                  creationComment, associatedArtifactId, authorId, staticBranchName, systemRootBranch);
      createRootBranchTx.execute();
      return createRootBranchTx.getNewBranchId();
   }

   public int createChildBranch(int parentTransactionId, int parentBranchId, String childBranchShortName, String childBranchName, String creationComment, int associatedArtifactId, int authorId, boolean branchWithFiltering, String[] compressArtTypeIds, String[] preserveArtTypeIds) throws Exception {
      int branchId;

      if (branchWithFiltering) {
         CreateBranchWithFiltering createBranchWithFiltering =
               new CreateBranchWithFiltering(parentTransactionId, parentBranchId, childBranchShortName,
                     childBranchName, creationComment, associatedArtifactId, authorId, compressArtTypeIds,
                     preserveArtTypeIds);
         createBranchWithFiltering.execute();
         branchId = createBranchWithFiltering.getNewBranchId();
      } else {
         CreateChildBranchTx createChildBranchTx =
               new CreateChildBranchTx(parentTransactionId, parentBranchId, childBranchShortName, childBranchName,
                     creationComment, associatedArtifactId, authorId);
         createChildBranchTx.execute();
         branchId = createChildBranchTx.getNewBranchId();
      }
      return branchId;
   }

   public static abstract class CreateBranchTx extends DbTransaction {
      protected String childBranchShortName;
      protected String childBranchName;
      protected int parentBranchId;
      protected int associatedArtifactId;
      protected boolean success = false;
      protected int branchId;
      protected int authorId;
      protected String creationComment;
      private BranchType branchType;
      private int parentTransactionId;

      public CreateBranchTx(int parentTransactionId, int parentBranchId, String childBranchShortName, String childBranchName, String creationComment, int associatedArtifactId, int authorId, BranchType branchType) throws OseeCoreException {
         this.parentBranchId = parentBranchId;
         this.childBranchShortName = childBranchShortName;
         this.childBranchName = childBranchName;
         this.associatedArtifactId = associatedArtifactId;
         this.authorId = authorId;
         this.creationComment = creationComment;
         this.branchType = branchType;
         this.parentTransactionId = parentTransactionId;
      }

      public int getNewBranchId() {
         return branchId;
      }

      protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
         branchId =
               initializeBranch(connection, childBranchShortName, childBranchName, parentBranchId, parentTransactionId,
                     authorId, timestamp, creationComment, associatedArtifactId, branchType);
         int newTransactionNumber = SequenceManager.getNextTransactionId();
         ConnectionHandler.runPreparedUpdate(connection, INSERT_TX_DETAILS, branchId, newTransactionNumber,
               creationComment, timestamp, authorId, 1);

         specializedBranchOperations(branchId, newTransactionNumber, connection);

         success = true;
      }

      private int initializeBranch(OseeConnection connection, String branchShortName, String branchName, int parentBranchId, int parentTransactionId, int authorId, Timestamp creationDate, String creationComment, int associatedArtifactId, BranchType branchType) throws OseeDataStoreException, OseeArgumentException {
         if (checkAlreadyHasBranchName(branchName)) {
            throw new OseeArgumentException("A branch with the name " + branchName + " already exists");
         }
         int branchId = SequenceManager.getNextBranchId();

         ConnectionHandler.runPreparedUpdate(connection, BRANCH_TABLE_INSERT, branchId, branchShortName, branchName,
               parentBranchId, parentTransactionId, 0, associatedArtifactId, branchType.getValue());

         return branchId;
      }

      private boolean checkAlreadyHasBranchName(String branchName) throws OseeDataStoreException {
         return ConnectionHandler.runPreparedQueryFetchInt(0, SELECT_BRANCH_BY_NAME, branchName) > 0;
      }

      public abstract void specializedBranchOperations(int newBranchId, int newTransactionNumber, OseeConnection connection) throws OseeDataStoreException;

      /**
       * @return the parentBranchId
       */
      public int getParentBranchId() {
         return parentBranchId;
      }

   }

   private final class CreateTopLevelBranchTx extends CreateBranchTx {

      private String staticBranchName;

      public CreateTopLevelBranchTx(int parentTransactionId, int parentBranchId, String childBranchShortName, String childBranchName, String creationComment, int associatedArtifactId, int authorId, String staticBranchName, boolean systemRootBranch) throws OseeCoreException {
         super(parentTransactionId, parentBranchId, childBranchShortName, childBranchName, creationComment,
               associatedArtifactId, authorId, systemRootBranch ? BranchType.SYSTEM_ROOT : BranchType.TOP_LEVEL);
         this.staticBranchName = staticBranchName;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.core.transaction.AbstractDbTxTemplate#handleTxWork()
       */
      @Override
      public void specializedBranchOperations(int newBranchId, int newTransactionNumber, OseeConnection connection) throws OseeDataStoreException {
         if (staticBranchName != null) {
            ConnectionHandler.runPreparedUpdate(connection, INSERT_DEFAULT_BRANCH_NAMES, staticBranchName, branchId);
         }

      }
   }

   private final class CreateChildBranchTx extends CreateBranchTx {

      public CreateChildBranchTx(int parentTransactionId, int parentBranchId, String childBranchShortName, String childBranchName, String creationComment, int associatedArtifactId, int authorId) throws OseeCoreException {
         super(parentTransactionId, parentBranchId, childBranchShortName, childBranchName, creationComment,
               associatedArtifactId, authorId, BranchType.WORKING);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.core.transaction.AbstractDbTxTemplate#handleTxWork()
       */
      @Override
      public void specializedBranchOperations(int newBranchId, int newTransactionNumber, OseeConnection connection) throws OseeDataStoreException {
         int updates =
               ConnectionHandler.runPreparedUpdate(connection, COPY_BRANCH_ADDRESSING, newTransactionNumber,
                     parentBranchId);
         System.out.println(String.format("Create child branch - updated [%d] records", updates));
      }
   }

}
