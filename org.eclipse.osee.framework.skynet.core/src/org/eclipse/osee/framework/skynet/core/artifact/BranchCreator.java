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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.BRANCH_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TXD_COMMENT;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionDetailsType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Ryan D. Brooks
 */
public class BranchCreator {
   public static final String NEW_MERGE_BRANCH_COMMENT = "New Merge Branch from ";

   private static final LocalAliasTable ARTIFACT_VERSION_ALIAS_1 = new LocalAliasTable(ARTIFACT_VERSION_TABLE, "t1");
   private static final LocalAliasTable ARTIFACT_VERSION_ALIAS_2 = new LocalAliasTable(ARTIFACT_VERSION_TABLE, "t2");

   private static final LocalAliasTable ATTRIBUTE_ALIAS_1 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t3");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_2 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t4");

   private static final LocalAliasTable TRANSACTIONS_ALIAS_1 = new LocalAliasTable(TRANSACTIONS_TABLE, "t5");
   private static final LocalAliasTable TRANSACTIONS_ALIAS_2 = new LocalAliasTable(TRANSACTIONS_TABLE, "t6");
   private static final LocalAliasTable TRANSACTIONS_ALIAS_3 = new LocalAliasTable(TRANSACTIONS_TABLE, "t7");
   private static final LocalAliasTable TRANSACTIONS_ALIAS_4 = new LocalAliasTable(TRANSACTIONS_TABLE, "t8");

   private static final LocalAliasTable LINK_ALIAS_1 = new LocalAliasTable(RELATION_LINK_VERSION_TABLE, "t9");
   private static final LocalAliasTable LINK_ALIAS_2 = new LocalAliasTable(RELATION_LINK_VERSION_TABLE, "t10");

   private static final LocalAliasTable ARTIFACT_ALIAS_1 = new LocalAliasTable(ARTIFACT_TABLE, "t11");
   private static final LocalAliasTable ARTIFACT_ALIAS_2 = new LocalAliasTable(ARTIFACT_TABLE, "t12");

   private static final LocalAliasTable ARTIFACT_VERSION_ALIAS_13 = new LocalAliasTable(ARTIFACT_VERSION_TABLE, "t13");

   private static final String BRANCH_TABLE_INSERT =
         "INSERT INTO " + BRANCH_TABLE.columnsForInsert("branch_id", "short_name", "branch_name", "parent_branch_id",
               "archived", "associated_art_id", "branch_type");
   private static final String SELECT_BRANCH_BY_NAME = "SELECT count(1) FROM osee_branch WHERE branch_name = ?";

   /* TODO: DISTINCT */
   private static final String INSERT_LINK_GAMMAS =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, mod_type, tx_current) " + "SELECT ?, " + LINK_ALIAS_1.column("gamma_id") + ", " + TRANSACTIONS_ALIAS_1.column("mod_type") + ", ? FROM " + TRANSACTIONS_ALIAS_1 + "," + ARTIFACT_VERSION_ALIAS_1 + "," + TRANSACTIONS_ALIAS_2 + "," + ARTIFACT_VERSION_ALIAS_2 + "," + LINK_ALIAS_1 + "," + TRANSACTIONS_ALIAS_3 + " WHERE " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " = " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + " AND " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " = " + LINK_ALIAS_1.column("a_art_id") + " AND " + TRANSACTIONS_ALIAS_2.column("transaction_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " = " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + " = " + LINK_ALIAS_1.column("b_art_id") + " AND " + LINK_ALIAS_1.column("modification_id") + " <> " + ModificationType.DELETED.getValue() + " AND " + LINK_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_3.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_3.column("transaction_id") + "=" + "(SELECT max(osee_tx_details.transaction_id) FROM " + LINK_ALIAS_2 + "," + TRANSACTIONS_ALIAS_4 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + LINK_ALIAS_2.column("rel_link_id") + "=" + LINK_ALIAS_1.column("rel_link_id") + " AND " + LINK_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_4.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_4.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   private static final String INSERT_ATTRIBUTES_GAMMAS =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, mod_type, tx_current) " + "SELECT ?, " + ATTRIBUTE_ALIAS_1.columns("gamma_id") + ", " + TRANSACTIONS_ALIAS_1.column("mod_type") + ", ? FROM " + TRANSACTIONS_ALIAS_1 + "," + ARTIFACT_VERSION_TABLE + "," + ATTRIBUTE_ALIAS_1 + "," + TRANSACTIONS_ALIAS_2 + " WHERE " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " =? AND " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " = " + ARTIFACT_VERSION_TABLE.column("gamma_id") + " AND " + ARTIFACT_VERSION_TABLE.column("art_id") + " = " + ATTRIBUTE_ALIAS_1.column("art_id") + " AND " + ATTRIBUTE_ALIAS_1.column("modification_id") + " <> " + ModificationType.DELETED.getValue() + " AND " + ATTRIBUTE_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_2.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_2.column("transaction_id") + "=" + "(SELECT max(osee_txs.transaction_id) FROM " + ATTRIBUTE_ALIAS_2 + "," + TRANSACTIONS_ALIAS_3 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_ALIAS_1.column("attr_id") + "=" + ATTRIBUTE_ALIAS_2.column("attr_id") + " AND " + ATTRIBUTE_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_3.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_3.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " <= ?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   // insert the artifact versions and attributes for all non-deleted artifact of a given type on a
   // given branch
   private static final String SELECTIVELY_BRANCH_ARTIFACTS_COMPRESSED =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, mod_type, tx_current) " + "SELECT ?, " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + ", " + TRANSACTIONS_TABLE.column("mod_type") + ", ? FROM " + ARTIFACT_TABLE + " , " + ARTIFACT_VERSION_ALIAS_1 + "," + TRANSACTIONS_TABLE + " WHERE " + ARTIFACT_TABLE.column("art_type_id") + " =? AND " + ARTIFACT_TABLE.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_1.column("modification_id") + " <> " + ModificationType.DELETED.getValue() + " AND " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT max(osee_txs.transaction_id FROM " + ARTIFACT_VERSION_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   private static final String SELECT_ARTIFACT_HISTORY =
         "SELECT " + TRANSACTIONS_ALIAS_1.columns("transaction_id", "gamma_id") + " AS art_gamma_id, " + TRANSACTIONS_ALIAS_1.column("mod_type") + " AS art_mod_type, " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " AS attr_gamma_id" + TRANSACTIONS_ALIAS_2.column("mod_type") + " AS attr_mod_type FROM " + ARTIFACT_TABLE + " , " + ARTIFACT_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + "," + ATTRIBUTE_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_2 + " WHERE " + ARTIFACT_TABLE.column("art_type_id") + " =?" + " AND " + ARTIFACT_TABLE.column("art_id") + " = " + ARTIFACT_VERSION_TABLE.column("art_id") + " AND " + ARTIFACT_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " = " + TRANSACTIONS_ALIAS_2.column("transaction_id") + " AND " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " = " + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + " AND " + ATTRIBUTE_VERSION_TABLE.column("art_id") + " = " + ARTIFACT_TABLE.column("art_id");

   private static final String INSERT_TX_DETAILS_FOR_HISTORY =
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE + " (branch_id, transaction_id, " + TXD_COMMENT + ", time, author)" + " SELECT ?, ?, " + TRANSACTION_DETAIL_TABLE.columns(
               TXD_COMMENT, "time", "author") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " = ?";

   private static final String INSERT_TX_FOR_HISTORY =
         "INSERT INTO " + TRANSACTIONS_TABLE.columnsForInsert("transaction_id", "gamma_id", "mod_type", "tx_current");

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
            initializeBranch(connection, childBranchShortName, childBranchName, userId, timestamp, comment,
                  associatedArtifact, branchType);

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

   private void copyBranchAddressingFromTransaction(Branch newBranch, int newTransactionNumber, TransactionId parentTransactionId, Collection<ArtifactType> compressArtTypes, Collection<ArtifactType> preserveArtTypes) throws OseeDataStoreException {
      if (compressArtTypes != null && preserveArtTypes != null) {
         Set<ArtifactType> intersection = new HashSet<ArtifactType>(compressArtTypes);
         intersection.retainAll(preserveArtTypes);
         if (intersection.size() > 0) {
            throw new IllegalArgumentException("The following artifact types are in both sets: " + intersection);
         }
      }

      if (compressArtTypes != null && compressArtTypes.size() > 0) {
         createBaselineTransaction(newTransactionNumber, parentTransactionId, compressArtTypes);
      }
      if (preserveArtTypes != null && preserveArtTypes.size() > 0) {
         branchWithHistory(newBranch, parentTransactionId, compressArtTypes, preserveArtTypes);
      }
   }

   /**
    * expects that preserveArtTypes is not null (may be empty)
    * 
    * @param newBranch
    * @param parentTransactionId
    * @param compressArtTypes
    * @param preserveArtTypes
    * @param newTransactionNumber
    */
   public static void branchWithHistory(Branch newBranch, TransactionId parentTransactionId, Collection<ArtifactType> compressArtTypes, Collection<ArtifactType> preserveArtTypes) throws OseeDataStoreException {

      HashCollection<Integer, Pair<Integer, ModificationType>> historyMap =
            new HashCollection<Integer, Pair<Integer, ModificationType>>(false, HashSet.class);
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         for (ArtifactType artifactType : preserveArtTypes) {

            chStmt.runPreparedQuery(SELECT_ARTIFACT_HISTORY, artifactType.getArtTypeId(),
                  parentTransactionId.getTransactionNumber(), parentTransactionId.getBranchId());

            while (chStmt.next()) {
               historyMap.put(chStmt.getInt("transaction_id"), new Pair<Integer, ModificationType>(
                     chStmt.getInt("art_gamma_id"), ModificationType.getMod(chStmt.getInt("art_mod_type"))));
               historyMap.put(chStmt.getInt("transaction_id"), new Pair<Integer, ModificationType>(
                     chStmt.getInt("attr_gamma_id"), ModificationType.getMod(chStmt.getInt("attr_mod_type"))));
            }
         }
         initSelectLinkHistory(compressArtTypes, preserveArtTypes, parentTransactionId, historyMap);
      } finally {
         chStmt.close();
      }

      Set<Integer> transactions = new TreeSet<Integer>(historyMap.keySet()); // the tree set is to in ascending order
      List<Object[]> txAddressData = new LinkedList<Object[]>();
      for (Integer parentTransactionNumber : transactions) {
         int nextTransactionNumber = SequenceManager.getNextTransactionId();

         ConnectionHandler.runPreparedUpdate(INSERT_TX_DETAILS_FOR_HISTORY, newBranch.getBranchId(),
               nextTransactionNumber, parentTransactionNumber.intValue());

         Collection<Pair<Integer, ModificationType>> gammasAndMods = historyMap.getValues(parentTransactionNumber);
         for (Pair<Integer, ModificationType> gammaAndMod : gammasAndMods) {
            ModificationType modType = gammaAndMod.getValue();
            txAddressData.add(new Object[] {nextTransactionNumber, gammaAndMod.getKey(), modType.getValue(),
                  TxChange.CURRENT.getValue()});
         }
         ConnectionHandler.runBatchUpdate(INSERT_TX_FOR_HISTORY, txAddressData);
         txAddressData.clear();
      }
   }

   /**
    * artifact a | artifact b | select link ===========|============|============ I | I | false I | C | false I | P |
    * false C | I | false P | I | false C | C | false C | P | true P | C | true P | P | true
    * 
    * @param compressArtTypes
    * @param preserveArtTypes
    */
   private static void initSelectLinkHistory(Collection<ArtifactType> compressArtTypes, Collection<ArtifactType> preserveArtTypes, TransactionId parentTransactionId, HashCollection<Integer, Pair<Integer, ModificationType>> historyMap) throws OseeDataStoreException {
      String preservedTypeSet = makeArtTypeSet(null, preserveArtTypes);
      String compressTypeSet = makeArtTypeSet(compressArtTypes, null);

      if (compressArtTypes != null && compressArtTypes.size() > 0) {
         // Handles the case C | P | true
         String cpSql =
               "SELECT " + TRANSACTIONS_ALIAS_1.columns("mode_type", "transaction_id", "gamma_id") + " AS link_gamma_id  FROM " + ARTIFACT_ALIAS_1 + "," + ARTIFACT_ALIAS_2 + "," + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + "," + ARTIFACT_VERSION_ALIAS_13 + " WHERE " + ARTIFACT_ALIAS_1.column("art_type_id") + " IN " + compressTypeSet + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("a_art_id") + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_13.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_13.column("modification_id") + " <> " + ModificationType.DELETED

               + " AND " + ARTIFACT_ALIAS_2.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_2.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("b_art_id")

               + " AND " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?";

         populateHistoryMapWithRelations(historyMap, cpSql, parentTransactionId);

         // Handles the case P | C | true
         String pcSql =
               "SELECT " + TRANSACTIONS_ALIAS_1.columns("mode_type", "transaction_id", "gamma_id") + " AS link_gamma_id" + " FROM " + ARTIFACT_ALIAS_1 + "," + ARTIFACT_ALIAS_2 + "," + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + "," + ARTIFACT_VERSION_ALIAS_13 + " WHERE " + ARTIFACT_ALIAS_1.column("art_type_id") + " IN " + compressTypeSet + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("b_art_id") + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_13.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_13.column("modification_id") + " <> " + ModificationType.DELETED

               + " AND " + ARTIFACT_ALIAS_2.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_2.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("a_art_id")

               + " AND " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?";

         populateHistoryMapWithRelations(historyMap, pcSql, parentTransactionId);
      }

      // Handles the case P | P | true
      String ppSql =
            "SELECT " + TRANSACTIONS_ALIAS_1.columns("mode_type", "transaction_id", "gamma_id") + " AS link_gamma_id" + " FROM " + ARTIFACT_ALIAS_1 + "," + ARTIFACT_ALIAS_2 + "," + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_ALIAS_1.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("b_art_id")

            + " AND " + ARTIFACT_ALIAS_2.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_2.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("a_art_id")

            + " AND " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?";

      populateHistoryMapWithRelations(historyMap, ppSql, parentTransactionId);
   }

   private static void populateHistoryMapWithRelations(HashCollection<Integer, Pair<Integer, ModificationType>> historyMap, String sql, TransactionId parentTransactionId) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(sql, parentTransactionId.getTransactionNumber(), parentTransactionId.getBranchId());
         while (chStmt.next()) {
            historyMap.put(chStmt.getInt("transaction_id"), new Pair<Integer, ModificationType>(
                  chStmt.getInt("link_gamma_id"), ModificationType.getMod(chStmt.getInt("mod_type"))));
         }
      } finally {
         chStmt.close();
      }
   }

   private void createBaselineTransaction(int newTransactionNumber, TransactionId parentTransactionId, Collection<ArtifactType> compressArtTypes) throws OseeDataStoreException {
      for (ArtifactType artifactType : compressArtTypes) {
         int count =
               ConnectionHandler.runPreparedUpdate(SELECTIVELY_BRANCH_ARTIFACTS_COMPRESSED, newTransactionNumber,
                     TxChange.CURRENT.getValue(), artifactType.getArtTypeId(),
                     parentTransactionId.getTransactionNumber(), parentTransactionId.getBranchId());
         if (count > 0) {
            OseeLog.log(SkynetActivator.class, Level.INFO,
                  "inserted " + count + " " + artifactType.getName() + " artifacts");
         }
      }

      int count =
            ConnectionHandler.runPreparedUpdate(INSERT_ATTRIBUTES_GAMMAS, newTransactionNumber,
                  TxChange.CURRENT.getValue(), newTransactionNumber, parentTransactionId.getTransactionNumber(),
                  parentTransactionId.getBranchId());
      OseeLog.log(SkynetActivator.class, Level.INFO, "inserted " + count + " attributes");

      count =
            ConnectionHandler.runPreparedUpdate(INSERT_LINK_GAMMAS, newTransactionNumber, TxChange.CURRENT.getValue(),
                  newTransactionNumber, newTransactionNumber, parentTransactionId.getTransactionNumber(),
                  parentTransactionId.getBranchId());
      OseeLog.log(SkynetActivator.class, Level.INFO, "inserted " + count + " relations");
   }

   private static String makeArtTypeSet(Collection<ArtifactType> compressArtTypes, Collection<ArtifactType> preserveArtTypes) {
      StringBuilder typeStrB = new StringBuilder();
      typeStrB.append("(");
      boolean firstItem = true;
      if (compressArtTypes != null) {
         firstItem = addTypes(typeStrB, compressArtTypes, firstItem);
      }
      if (preserveArtTypes != null) {
         addTypes(typeStrB, preserveArtTypes, firstItem);
      }
      typeStrB.append(")");
      return typeStrB.toString();
   }

   private static boolean addTypes(StringBuilder typeStrB, Collection<ArtifactType> artifactTypes, boolean firstItem) {
      for (ArtifactType artifactType : artifactTypes) {
         if (firstItem) {
            firstItem = false;
         } else {
            typeStrB.append(",");
         }
         typeStrB.append(artifactType.getArtTypeId());
      }
      return firstItem;
   }

   /**
    * Creates a new root branch. Should NOT be used outside BranchManager. If programatic access is necessary, setting
    * the staticBranchName will add a key for this branch and allow access to the branch through
    * getKeyedBranch(staticBranchName).
    * 
    * @param shortBranchName
    * @param branchName
    * @param staticBranchName null if no static key is desired
    * @param parentBranchId TODO
    * @return branch object
    * @throws OseeCoreException
    * @see BranchManager#createRootBranch(String, String, int)
    * @see BranchManager#getKeyedBranch(String)
    */
   public Branch createRootBranch(String shortBranchName, String branchName, String staticBranchName, int parentBranchId, boolean systemRootBranch) throws OseeCoreException {
      return HttpBranchCreation.createRootBranch(shortBranchName, branchName, staticBranchName, parentBranchId,
            systemRootBranch);
   }

   /**
    * adds a new branch to the database
    * 
    * @param branchName caller is responsible for ensuring no branch has already been given this name
    * @param parentTransactionId the id of the parent branch or null if this branch has no parent
    * @return branch object that represents the newly created branch
    * @throws OseeCoreException
    */
   private Branch initializeBranch(Connection connection, String branchShortName, String branchName, int authorId, Timestamp creationDate, String creationComment, Artifact associatedArtifact, BranchType branchType) throws OseeCoreException {
      branchShortName = StringFormat.truncate(branchShortName != null ? branchShortName : branchName, 25);

      if (ConnectionHandler.runPreparedQueryFetchInt(connection, 0, SELECT_BRANCH_BY_NAME, branchName) > 0) {
         throw new OseeArgumentException("A branch with the name " + branchName + " already exists");
      }

      int branchId = SequenceManager.getNextBranchId();
      int parentBranchNumber = BranchManager.getSystemRootBranch().getBranchId();
      int associatedArtifactId = -1;

      if (associatedArtifact == null && !SkynetDbInit.isDbInit()) {
         associatedArtifact = UserManager.getUser(SystemUser.NoOne);
      }

      if (associatedArtifact != null) {
         associatedArtifactId = associatedArtifact.getArtId();
      }

      ConnectionHandler.runPreparedUpdate(connection, BRANCH_TABLE_INSERT, branchId, branchShortName, branchName,
            parentBranchNumber, 0, associatedArtifactId, branchType.getValue());

      // this needs to be after the insert in case there is an exception on insert
      Branch branch =
            BranchManager.createBranchObject(branchShortName, branchName, branchId, parentBranchNumber, false,
                  authorId, creationDate, creationComment, associatedArtifactId, branchType);
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

   public void addArtifactsToBranch(Branch sourceBranch, Branch destBranch, Branch mergeBranch, Collection<Integer> artIds) throws OseeCoreException {
      CreateMergeBranchTx createMergeBranchTx = new CreateMergeBranchTx(sourceBranch, destBranch, artIds, mergeBranch);
      createMergeBranchTx.execute();
   }

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
         boolean createBranch = (mergeBranch == null);

         if (artIds == null || artIds.isEmpty()) {
            throw new IllegalArgumentException("Artifact IDs can not be null or empty");
         }

         Pair<Branch, Integer> branchWithTransactionNumber;
         if (createBranch) {
            branchWithTransactionNumber =
                  createMergeBranchWithBaselineTransactionNumber(connection, UserManager.getUser(),
                        TransactionIdManager.getStartEndPoint(sourceBranch).getKey(),
                        "Merge " + sourceBranch.getDisplayName() + " <=> " + destBranch.getBranchShortName(),
                        "Merge " + sourceBranch.getDisplayName() + " <=> " + destBranch.getBranchShortName(),
                        BranchType.MERGE, destBranch);
         } else {
            TransactionId startTransactionId = TransactionIdManager.getStartEndPoint(mergeBranch).getKey();
            branchWithTransactionNumber =
                  new Pair<Branch, Integer>(mergeBranch, startTransactionId.getTransactionNumber());
         }

         List<Object[]> datas = new LinkedList<Object[]>();
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         for (int artId : artIds) {
            datas.add(new Object[] {queryId, insertTime, artId, sourceBranch.getBranchId(), SQL3DataType.INTEGER});
         }
         try {
            ArtifactLoader.selectArtifacts(datas);
            String attributeGammas =
                  "INSERT INTO OSEE_TXS (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, atr1.gamma_id, txs1.mod_type, ? FROM osee_attribute atr1, osee_txs txs1, osee_tx_details txd1, osee_join_artifact ald1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = atr1.gamma_id AND atr1.art_id = ald1.art_id and ald1.query_id = ?";
            String artifactVersionGammas =
                  "INSERT INTO OSEE_TXS (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, arv1.gamma_id, txs1.mod_type, ? FROM osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1, osee_join_artifact ald1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = arv1.gamma_id AND arv1.art_id = ald1.art_id and ald1.query_id = ?";

            insertGammas(connection, attributeGammas, branchWithTransactionNumber.getValue(), queryId);
            insertGammas(connection, artifactVersionGammas, branchWithTransactionNumber.getValue(), queryId);
         } finally {
            ArtifactLoader.clearQuery(connection, queryId);
         }

         mergeBranch = branchWithTransactionNumber.getKey();

         if (createBranch) {
            ConnectionHandler.runPreparedUpdate(connection, MERGE_BRANCH_INSERT, sourceBranch.getBranchId(),
                  destBranch.getBranchId(), mergeBranch.getBranchId(), -1);
         }
      }

      public Branch getMergeBranch() {
         return mergeBranch;
      }

      private void insertGammas(Connection connection, String sql, int baselineTransactionNumber, int queryId) throws OseeDataStoreException {
         ConnectionHandler.runPreparedUpdate(connection, sql, baselineTransactionNumber, TxChange.CURRENT.getValue(),
               sourceBranch.getBranchId(), queryId);
      }
   }
}