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

package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.BRANCH_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TXD_COMMENT;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Ryan D. Brooks
 */
public class BranchCreator {
   public static final String NEW_MERGE_BRANCH_COMMENT = "New Merge Branch from ";

   private static final String BRANCH_TABLE_INSERT =
         "INSERT INTO " + BRANCH_TABLE.columnsForInsert("branch_id", "short_name", "branch_name", "parent_branch_id",
               "parent_transaction_id", "archived", "associated_art_id", "branch_type");
   private static final String SELECT_BRANCH_BY_NAME = "SELECT count(1) FROM osee_branch WHERE branch_name = ?";

   private static final String MERGE_BRANCH_INSERT =
         "INSERT INTO osee_merge (source_branch_id, dest_branch_id, merge_branch_id, commit_transaction_id) VALUES(?,?,?,?)";

   private static final BranchCreator instance = new BranchCreator();

   private BranchCreator() {

   }

   public static BranchCreator getInstance() {
      return instance;
   }

   private Pair<Branch, Integer> createMergeBranchWithBaselineTransactionNumber(Connection connection, Artifact associatedArtifact, TransactionId sourceTransactionId, String childBranchShortName, String childBranchName, BranchType branchType, Branch destBranch) throws OseeCoreException {
      User userToBlame = UserManager.getUser();
      Branch parentBranch = sourceTransactionId.getBranch();
      int userId = (userToBlame == null) ? UserManager.getUser(SystemUser.NoOne).getArtId() : userToBlame.getArtId();
      String comment =
            NEW_MERGE_BRANCH_COMMENT + parentBranch.getBranchName() + "(" + sourceTransactionId.getTransactionNumber() + ") and " + destBranch.getBranchName();
      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      Branch childBranch =
            initializeBranch(connection, sourceTransactionId, childBranchShortName, childBranchName, userId, timestamp,
                  comment, associatedArtifact, branchType);

      // insert the new transaction data first.
      int newTransactionNumber = SequenceManager.getNextTransactionId();
      String query =
            "INSERT INTO " + TRANSACTION_DETAIL_TABLE.columnsForInsert("branch_id", "transaction_id", TXD_COMMENT,
                  "time", "author", "tx_type");
      ConnectionHandler.runPreparedUpdate(connection, query, childBranch.getBranchId(), newTransactionNumber,
            childBranch.getCreationComment(), childBranch.getCreationDate(), childBranch.getAuthorId(),
            TransactionDetailsType.Baselined.getId());

      return new Pair<Branch, Integer>(childBranch, newTransactionNumber);
   }

   /**
    * adds a new branch to the database
    * 
    * @param connection
    * @param branchShortName
    * @param branchName
    * @param authorId
    * @param creationDate
    * @param creationComment
    * @param associatedArtifact
    * @param branchType
    * @return branch object that represents the newly created branch
    * @throws OseeCoreException
    */
   private Branch initializeBranch(Connection connection, TransactionId sourceTransactionId, String branchShortName, String branchName, int authorId, Timestamp creationDate, String creationComment, Artifact associatedArtifact, BranchType branchType) throws OseeCoreException {
      branchShortName = StringFormat.truncate(branchShortName != null ? branchShortName : branchName, 25);

      if (ConnectionHandler.runPreparedQueryFetchInt(connection, 0, SELECT_BRANCH_BY_NAME, branchName) > 0) {
         throw new OseeArgumentException("A branch with the name " + branchName + " already exists");
      }

      int branchId = SequenceManager.getNextBranchId();
      int parentBranchNumber = sourceTransactionId.getBranchId();
      int parentTransactionId = sourceTransactionId.getTransactionNumber();
      int associatedArtifactId = -1;

      if (associatedArtifact == null && !SkynetDbInit.isDbInit()) {
         associatedArtifact = UserManager.getUser(SystemUser.NoOne);
      }

      if (associatedArtifact != null) {
         associatedArtifactId = associatedArtifact.getArtId();
      }

      ConnectionHandler.runPreparedUpdate(connection, BRANCH_TABLE_INSERT, branchId, branchShortName, branchName,
            parentBranchNumber, parentTransactionId, 0, associatedArtifactId, branchType.getValue());

      // this needs to be after the insert in case there is an exception on insert
      Branch branch =
            BranchManager.createBranchObject(branchShortName, branchName, branchId, parentBranchNumber,
                  parentTransactionId, false, authorId, creationDate, creationComment, associatedArtifactId, branchType);
      if (associatedArtifact != null) {
         branch.setAssociatedArtifact(associatedArtifact);
      }

      return branch;
   }

   /**
    * Creates a new Branch based on the transaction number selected and the parent branch.
    * 
    * @param parentTransactionId
    * @param childBranchName
    */
   public Branch createChildBranch(final TransactionId parentTransactionId, final String childBranchShortName, final String childBranchName, final Artifact associatedArtifact, boolean preserveMetaData, Collection<Integer> compressArtTypeIds, Collection<Integer> preserveArtTypeIds) throws OseeCoreException {
      return HttpBranchCreation.createChildBranch(parentTransactionId, childBranchShortName, childBranchName,
            associatedArtifact, preserveMetaData, compressArtTypeIds, preserveArtTypeIds);
   }

   /**
    * Creates a new merge branch based on the artifacts from the source branch
    */
   public Branch createMergeBranch(Branch sourceBranch, Branch destBranch, Collection<Integer> artIds) throws OseeCoreException {
      CreateMergeBranchTx createMergeBranchTx = new CreateMergeBranchTx(sourceBranch, destBranch, artIds);
      createMergeBranchTx.execute();
      return createMergeBranchTx.getMergeBranch();
   }

   private final static String attributeGammas =
         "INSERT INTO OSEE_TXS (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, atr1.gamma_id, txs1.mod_type, ? FROM osee_attribute atr1, osee_txs txs1, osee_tx_details txd1, osee_join_artifact ald1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = atr1.gamma_id AND atr1.art_id = ald1.art_id and ald1.query_id = ?";
   private final static String artifactVersionGammas =
         "INSERT INTO OSEE_TXS (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, arv1.gamma_id, txs1.mod_type, ? FROM osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1, osee_join_artifact ald1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = arv1.gamma_id AND arv1.art_id = ald1.art_id and ald1.query_id = ?";

   private final class CreateMergeBranchTx extends DbTransaction {
      private Branch sourceBranch;
      private Branch destBranch;
      private Collection<Integer> artIds;
      private Branch mergeBranch;

      /**
       * @param sourceBranch
       * @param destBranch
       * @param artIds
       * @throws OseeStateException
       */
      public CreateMergeBranchTx(Branch sourceBranch, Branch destBranch, Collection<Integer> artIds) throws OseeCoreException {
         this(sourceBranch, destBranch, artIds, null);
      }

      /**
       * @param sourceBranch
       * @param destBranch
       * @param artIds
       * @throws OseeStateException
       */
      public CreateMergeBranchTx(Branch sourceBranch, Branch destBranch, Collection<Integer> artIds, Branch mergeBranch) throws OseeCoreException {
         this.sourceBranch = sourceBranch;
         this.destBranch = destBranch;
         this.artIds = artIds;
         this.mergeBranch = mergeBranch;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork(Connection connection) throws OseeCoreException {

         if (artIds == null || artIds.isEmpty()) {
            throw new IllegalArgumentException("Artifact IDs can not be null or empty");
         }

         Pair<Branch, Integer> branchWithTransactionNumber;
         branchWithTransactionNumber =
               createMergeBranchWithBaselineTransactionNumber(connection, UserManager.getUser(),
                     TransactionIdManager.getStartEndPoint(sourceBranch).getKey(),
                     "Merge " + sourceBranch.getDisplayName() + " <=> " + destBranch.getBranchShortName(),
                     "Merge " + sourceBranch.getDisplayName() + " <=> " + destBranch.getBranchShortName(),
                     BranchType.MERGE, destBranch);

         List<Object[]> datas = new LinkedList<Object[]>();
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         for (int artId : artIds) {
            datas.add(new Object[] {queryId, insertTime, artId, sourceBranch.getBranchId(), SQL3DataType.INTEGER});
         }
         try {
            ArtifactLoader.selectArtifacts(datas);
            insertGammas(connection, attributeGammas, branchWithTransactionNumber.getValue(), queryId, sourceBranch);
            insertGammas(connection, artifactVersionGammas, branchWithTransactionNumber.getValue(), queryId,
                  sourceBranch);
         } finally {
            ArtifactLoader.clearQuery(connection, queryId);
         }

         mergeBranch = branchWithTransactionNumber.getKey();

         ConnectionHandler.runPreparedUpdate(connection, MERGE_BRANCH_INSERT, sourceBranch.getBranchId(),
               destBranch.getBranchId(), mergeBranch.getBranchId(), -1);
      }

      public Branch getMergeBranch() {
         return mergeBranch;
      }
   }

   public void addArtifactsToBranch(Connection connection, Branch sourceBranch, Branch destBranch, Branch mergeBranch, Collection<Integer> artIds) throws OseeCoreException {
      if (artIds == null || artIds.isEmpty()) {
         throw new IllegalArgumentException("Artifact IDs can not be null or empty");
      }

      Pair<Branch, Integer> branchWithTransactionNumber;
      TransactionId startTransactionId = TransactionIdManager.getStartEndPoint(mergeBranch).getKey();
      branchWithTransactionNumber = new Pair<Branch, Integer>(mergeBranch, startTransactionId.getTransactionNumber());

      List<Object[]> datas = new LinkedList<Object[]>();
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      for (int artId : artIds) {
         datas.add(new Object[] {queryId, insertTime, artId, sourceBranch.getBranchId(), SQL3DataType.INTEGER});
      }
      try {
         ArtifactLoader.selectArtifacts(datas);
         insertGammas(connection, attributeGammas, branchWithTransactionNumber.getValue(), queryId, sourceBranch);
         insertGammas(connection, artifactVersionGammas, branchWithTransactionNumber.getValue(), queryId, sourceBranch);
      } finally {
         ArtifactLoader.clearQuery(connection, queryId);
      }
      mergeBranch = branchWithTransactionNumber.getKey();
   }

   private static void insertGammas(Connection connection, String sql, int baselineTransactionNumber, int queryId, Branch sourceBranch) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(connection, sql, baselineTransactionNumber, TxChange.CURRENT.getValue(),
            sourceBranch.getBranchId(), queryId);
   }
}