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
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.BRANCH_ID_SEQ;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.BRANCH_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_ID_SEQ;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TXD_COMMENT;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.db.connection.core.transaction.AbstractDbTxTemplate;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Branch.BranchType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionDetailsType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;

/**
 * @author Ryan D. Brooks
 */
public class BranchCreator {
   static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchPersistenceManager.class);

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
   private static final String SELECT_BRANCH_BY_NAME = "SELECT * FROM osee_define_branch WHERE branch_name = ?";

   /* TODO: DISTINCT */
   private static final String INSERT_LINK_GAMMAS =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, mod_type, tx_current) " + "SELECT ?, " + LINK_ALIAS_1.column("gamma_id") + ", " + TRANSACTIONS_ALIAS_1.column("mod_type") + ", ? FROM " + TRANSACTIONS_ALIAS_1 + "," + ARTIFACT_VERSION_ALIAS_1 + "," + TRANSACTIONS_ALIAS_2 + "," + ARTIFACT_VERSION_ALIAS_2 + "," + LINK_ALIAS_1 + "," + TRANSACTIONS_ALIAS_3 + " WHERE " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " = " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + " AND " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " = " + LINK_ALIAS_1.column("a_art_id") + " AND " + TRANSACTIONS_ALIAS_2.column("transaction_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " = " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + " = " + LINK_ALIAS_1.column("b_art_id") + " AND " + LINK_ALIAS_1.column("modification_id") + " <> " + ModificationType.DELETED.getValue() + " AND " + LINK_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_3.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_3.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + LINK_ALIAS_2 + "," + TRANSACTIONS_ALIAS_4 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + LINK_ALIAS_2.column("rel_link_id") + "=" + LINK_ALIAS_1.column("rel_link_id") + " AND " + LINK_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_4.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_4.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   private static final String INSERT_ATTRIBUTES_GAMMAS =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, mod_type, tx_current) " + "SELECT ?, " + ATTRIBUTE_ALIAS_1.columns("gamma_id") + ", " + TRANSACTIONS_ALIAS_1.column("mod_type") + ", ? FROM " + TRANSACTIONS_ALIAS_1 + "," + ARTIFACT_VERSION_TABLE + "," + ATTRIBUTE_ALIAS_1 + "," + TRANSACTIONS_ALIAS_2 + " WHERE " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " =? AND " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " = " + ARTIFACT_VERSION_TABLE.column("gamma_id") + " AND " + ARTIFACT_VERSION_TABLE.column("art_id") + " = " + ATTRIBUTE_ALIAS_1.column("art_id") + " AND " + ATTRIBUTE_ALIAS_1.column("modification_id") + " <> " + ModificationType.DELETED.getValue() + " AND " + ATTRIBUTE_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_2.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_2.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + ATTRIBUTE_ALIAS_2 + "," + TRANSACTIONS_ALIAS_3 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_ALIAS_1.column("attr_id") + "=" + ATTRIBUTE_ALIAS_2.column("attr_id") + " AND " + ATTRIBUTE_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_3.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_3.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " <= ?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   // insert the artifact versions and attributes for all non-deleted artifact of a given type on a
   // given branch
   private static final String SELECTIVELY_BRANCH_ARTIFACTS_COMPRESSED =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, mod_type, tx_current) " + "SELECT ?, " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + ", " + TRANSACTIONS_TABLE.column("mod_type") + ", ? FROM " + ARTIFACT_TABLE + " , " + ARTIFACT_VERSION_ALIAS_1 + "," + TRANSACTIONS_TABLE + " WHERE " + ARTIFACT_TABLE.column("art_type_id") + " =? AND " + ARTIFACT_TABLE.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_1.column("modification_id") + " <> " + ModificationType.DELETED.getValue() + " AND " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + ARTIFACT_VERSION_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   private static final String SELECT_ARTIFACT_HISTORY =
         "SELECT " + TRANSACTIONS_ALIAS_1.columns("transaction_id", "gamma_id") + " AS art_gamma_id, " + TRANSACTIONS_ALIAS_1.column("mod_type") + " AS art_mod_type, " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " AS attr_gamma_id" + TRANSACTIONS_ALIAS_2.column("mod_type") + " AS attr_mod_type FROM " + ARTIFACT_TABLE + " , " + ARTIFACT_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + "," + ATTRIBUTE_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_2 + " WHERE " + ARTIFACT_TABLE.column("art_type_id") + " =?" + " AND " + ARTIFACT_TABLE.column("art_id") + " = " + ARTIFACT_VERSION_TABLE.column("art_id") + " AND " + ARTIFACT_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " = " + TRANSACTIONS_ALIAS_2.column("transaction_id") + " AND " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " = " + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + " AND " + ATTRIBUTE_VERSION_TABLE.column("art_id") + " = " + ARTIFACT_TABLE.column("art_id");

   private static final String INSERT_TX_DETAILS_FOR_HISTORY =
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE + " (branch_id, transaction_id, " + TXD_COMMENT + ", time, author)" + " SELECT ?, ?, " + TRANSACTION_DETAIL_TABLE.columns(
               TXD_COMMENT, "time", "author") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " = ?";

   private static final String INSERT_TX_FOR_HISTORY =
         "INSERT INTO " + TRANSACTIONS_TABLE.columnsForInsert("transaction_id", "gamma_id", "mod_type", "tx_current");

   //   private static final String INSERT_DEFAULT_BRANCH_NAMES =
   //         "INSERT INTO " + BRANCH_DEFINITIONS.columnsForInsert("static_branch_name", "mapped_branch_id");

   private static final String MERGE_BRANCH_INSERT =
         "INSERT INTO osee_define_merge " + "(source_branch_id, dest_branch_id, merge_branch_id)  VALUES( ? , ? , ?)";

   private static final BranchCreator instance = new BranchCreator();

   private BranchCreator() {

   }

   public static BranchCreator getInstance() {
      return instance;
   }

   private Pair<Branch, Integer> createMergeBranchWithBaselineTransactionNumber(Artifact associatedArtifact, TransactionId sourceTransactionId, String childBranchShortName, String childBranchName, BranchType branchType, Branch destBranch) throws OseeCoreException, SQLException {
      User userToBlame = SkynetAuthentication.getUser();
      Branch parentBranch = sourceTransactionId.getBranch();
      int userId =
            (userToBlame == null) ? SkynetAuthentication.getUser(UserEnum.NoOne).getArtId() : userToBlame.getArtId();
      String comment =
            NEW_MERGE_BRANCH_COMMENT + parentBranch.getBranchName() + "(" + sourceTransactionId.getTransactionNumber() + ") and " + destBranch.getBranchName();
      Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
      Branch childBranch =
            initializeBranch(childBranchShortName, childBranchName, null, userId, timestamp, comment,
                  associatedArtifact, branchType);

      // insert the new transaction data first.
      int newTransactionNumber = Query.getNextSeqVal(TRANSACTION_ID_SEQ);
      String query =
            "INSERT INTO " + TRANSACTION_DETAIL_TABLE.columnsForInsert("branch_id", "transaction_id", TXD_COMMENT,
                  "time", "author", "tx_type");
      ConnectionHandler.runPreparedUpdate(query, SQL3DataType.INTEGER, childBranch.getBranchId(), SQL3DataType.INTEGER,
            newTransactionNumber, SQL3DataType.VARCHAR, childBranch.getCreationComment(), SQL3DataType.TIMESTAMP,
            childBranch.getCreationDate(), SQL3DataType.INTEGER, childBranch.getAuthorId(), SQL3DataType.INTEGER,
            TransactionDetailsType.Baselined.getId());

      return new Pair<Branch, Integer>(childBranch, newTransactionNumber);
   }

   private void copyBranchAddressingFromTransaction(Branch newBranch, int newTransactionNumber, TransactionId parentTransactionId, Collection<ArtifactType> compressArtTypes, Collection<ArtifactType> preserveArtTypes) throws SQLException {
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
    * @throws SQLException
    */
   public static void branchWithHistory(Branch newBranch, TransactionId parentTransactionId, Collection<ArtifactType> compressArtTypes, Collection<ArtifactType> preserveArtTypes) throws SQLException {

      HashCollection<Integer, Pair<Integer, ModificationType>> historyMap =
            new HashCollection<Integer, Pair<Integer, ModificationType>>(false, HashSet.class);
      ConnectionHandlerStatement chStmt = null;
      try {
         for (ArtifactType artifactType : preserveArtTypes) {

            chStmt =
                  ConnectionHandler.runPreparedQuery(SELECT_ARTIFACT_HISTORY, SQL3DataType.INTEGER,
                        artifactType.getArtTypeId(), SQL3DataType.INTEGER, parentTransactionId.getTransactionNumber(),
                        SQL3DataType.INTEGER, parentTransactionId.getBranch().getBranchId());

            ResultSet rSet = chStmt.getRset();
            while (chStmt.next()) {
               historyMap.put(rSet.getInt("transaction_id"), new Pair<Integer, ModificationType>(
                     rSet.getInt("art_gamma_id"), ModificationType.getMod(rSet.getInt("art_mod_type"))));
               historyMap.put(rSet.getInt("transaction_id"), new Pair<Integer, ModificationType>(
                     rSet.getInt("attr_gamma_id"), ModificationType.getMod(rSet.getInt("attr_mod_type"))));
            }
         }
         initSelectLinkHistory(compressArtTypes, preserveArtTypes, parentTransactionId, historyMap);
      } finally {
         DbUtil.close(chStmt);
      }

      Set<Integer> transactions = new TreeSet<Integer>(historyMap.keySet()); // the tree set is to in ascending order
      List<Object[]> txAddressData = new LinkedList<Object[]>();
      for (Integer parentTransactionNumber : transactions) {
         int nextTransactionNumber = Query.getNextSeqVal(TRANSACTION_ID_SEQ);

         ConnectionHandler.runPreparedUpdate(INSERT_TX_DETAILS_FOR_HISTORY, SQL3DataType.INTEGER,
               newBranch.getBranchId(), SQL3DataType.INTEGER, nextTransactionNumber, SQL3DataType.INTEGER,
               parentTransactionNumber.intValue());

         Collection<Pair<Integer, ModificationType>> gammasAndMods = historyMap.getValues(parentTransactionNumber);
         for (Pair<Integer, ModificationType> gammaAndMod : gammasAndMods) {
            ModificationType modType = gammaAndMod.getValue();
            txAddressData.add(new Object[] {SQL3DataType.INTEGER, nextTransactionNumber, SQL3DataType.INTEGER,
                  gammaAndMod.getKey(), SQL3DataType.INTEGER, modType.getValue(), SQL3DataType.INTEGER,
                  modType.getCurrentValue()});
         }
         ConnectionHandler.runPreparedUpdateBatch(INSERT_TX_FOR_HISTORY, txAddressData);
         txAddressData.clear();
      }
   }

   /**
    * artifact a | artifact b | select link ===========|============|============ I | I | false I | C | false I | P |
    * false C | I | false P | I | false C | C | false C | P | true P | C | true P | P | true
    * 
    * @param compressArtTypes
    * @param preserveArtTypes
    * @throws SQLException
    */
   private static void initSelectLinkHistory(Collection<ArtifactType> compressArtTypes, Collection<ArtifactType> preserveArtTypes, TransactionId parentTransactionId, HashCollection<Integer, Pair<Integer, ModificationType>> historyMap) throws SQLException {
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

   private static void populateHistoryMapWithRelations(HashCollection<Integer, Pair<Integer, ModificationType>> historyMap, String sql, TransactionId parentTransactionId) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(sql, SQL3DataType.INTEGER,
                     parentTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                     parentTransactionId.getBranch().getBranchId());
         ResultSet rSet = chStmt.getRset();
         while (chStmt.next()) {
            historyMap.put(rSet.getInt("transaction_id"), new Pair<Integer, ModificationType>(
                  rSet.getInt("link_gamma_id"), ModificationType.getMod(rSet.getInt("mod_type"))));
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   private void createBaselineTransaction(int newTransactionNumber, TransactionId parentTransactionId, Collection<ArtifactType> compressArtTypes) throws SQLException {
      for (ArtifactType artifactType : compressArtTypes) {
         int count =
               ConnectionHandler.runPreparedUpdateReturnCount(SELECTIVELY_BRANCH_ARTIFACTS_COMPRESSED,
                     SQL3DataType.INTEGER, newTransactionNumber, SQL3DataType.INTEGER, TxChange.CURRENT.getValue(),
                     SQL3DataType.INTEGER, artifactType.getArtTypeId(), SQL3DataType.INTEGER,
                     parentTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                     parentTransactionId.getBranch().getBranchId());
         if (count > 0) logger.log(Level.INFO, "inserted " + count + " " + artifactType.getName() + " artifacts");
      }

      int count =
            ConnectionHandler.runPreparedUpdateReturnCount(INSERT_ATTRIBUTES_GAMMAS, SQL3DataType.INTEGER,
                  newTransactionNumber, SQL3DataType.INTEGER, TxChange.CURRENT.getValue(), SQL3DataType.INTEGER,
                  newTransactionNumber, SQL3DataType.INTEGER, parentTransactionId.getTransactionNumber(),
                  SQL3DataType.INTEGER, parentTransactionId.getBranch().getBranchId());
      logger.log(Level.INFO, "inserted " + count + " attributes");

      count =
            ConnectionHandler.runPreparedUpdateReturnCount(INSERT_LINK_GAMMAS, SQL3DataType.INTEGER,
                  newTransactionNumber, SQL3DataType.INTEGER, TxChange.CURRENT.getValue(), SQL3DataType.INTEGER,
                  newTransactionNumber, SQL3DataType.INTEGER, newTransactionNumber, SQL3DataType.INTEGER,
                  parentTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                  parentTransactionId.getBranch().getBranchId());
      logger.log(Level.INFO, "inserted " + count + " relations");
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
    * Creates a new root branch. Should NOT be used outside BranchPersistenceManager. If programatic access is
    * necessary, setting the staticBranchName will add a key for this branch and allow access to the branch through
    * getKeyedBranch(staticBranchName).
    * 
    * @param shortBranchName
    * @param branchName
    * @param staticBranchName null if no static key is desired
    * @return branch object
    * @throws SQLException
    * @throws OseeCoreException
    * @see BranchPersistenceManager#createRootBranch(String, String, int)
    * @see BranchPersistenceManager#getKeyedBranch(String)
    */
   public Branch createRootBranch(String shortBranchName, String branchName, String staticBranchName) throws SQLException, OseeCoreException {
      return HttpBranchCreation.createRootBranch(shortBranchName, branchName, staticBranchName);
   }

   /**
    * adds a new branch to the database
    * 
    * @param branchName caller is responsible for ensuring no branch has already been given this name
    * @param parentBranchId the id of the parent branch or NULL_PARENT_BRANCH_ID if this branch has no parent
    * @return branch object that represents the newly created branch
    * @throws SQLException
    * @throws UserNotInDatabase
    * @throws MultipleArtifactsExist
    */
   private Branch initializeBranch(String branchShortName, String branchName, TransactionId parentBranchId, int authorId, Timestamp creationDate, String creationComment, Artifact associatedArtifact, BranchType branchType) throws OseeCoreException, SQLException {
      branchShortName = StringFormat.truncate(branchShortName != null ? branchShortName : branchName, 25);

      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_BRANCH_BY_NAME, SQL3DataType.VARCHAR, branchName);
         ResultSet rset = chStmt.getRset();
         if (rset.next()) {
            throw new IllegalArgumentException("A branch with the name " + branchName + " already exists");
         }
      } finally {
         DbUtil.close(chStmt);
      }

      int branchId = Query.getNextSeqVal(BRANCH_ID_SEQ);
      int parentBranchNumber =
            parentBranchId == null ? Branch.NULL_PARENT_BRANCH_ID : parentBranchId.getBranch().getBranchId();
      int associatedArtifactId = -1;

      if (associatedArtifact == null && !SkynetDbInit.isDbInit()) {
         associatedArtifact = SkynetAuthentication.getUser(UserEnum.NoOne);
      }

      if (associatedArtifact != null) {
         associatedArtifactId = associatedArtifact.getArtId();
      }

      ConnectionHandler.runPreparedUpdate(BRANCH_TABLE_INSERT, SQL3DataType.INTEGER, branchId, SQL3DataType.VARCHAR,
            branchShortName, SQL3DataType.VARCHAR, branchName, SQL3DataType.INTEGER, parentBranchNumber,
            SQL3DataType.INTEGER, 0, SQL3DataType.INTEGER, associatedArtifactId, SQL3DataType.INTEGER,
            branchType.getValue());

      // this needs to be after the insert in case there is an exception on insert
      Branch branch;
      if (associatedArtifact == null) {
         branch =
               new Branch(branchShortName, branchName, branchId, parentBranchNumber, false, authorId, creationDate,
                     creationComment, associatedArtifactId, branchType);
      } else {
         branch =
               new Branch(branchShortName, branchName, branchId, parentBranchNumber, false, authorId, creationDate,
                     creationComment, associatedArtifact, branchType);
      }

      return branch;
   }

   /**
    * Creates a new Branch based on the transaction number selected and the parent branch.
    * 
    * @param parentTransactionId
    * @param childBranchName
    * @throws SQLException
    */
   public Branch createChildBranch(final TransactionId parentTransactionId, final String childBranchShortName, final String childBranchName, final Artifact associatedArtifact, boolean preserveMetaData, Collection<ArtifactType> compressArtTypes, Collection<ArtifactType> preserveArtTypes) throws OseeCoreException, SQLException {
      return HttpBranchCreation.createChildBranch(parentTransactionId, childBranchShortName, childBranchName,
            associatedArtifact, preserveMetaData, compressArtTypes, preserveArtTypes);
   }

   /**
    * Creates a new merge branch based on the artifacts from the source branch
    */
   public Branch createMergeBranch(Branch sourceBranch, Branch destBranch, Collection<Integer> artIds) throws OseeCoreException, SQLException {
      try {
         CreateMergeBranchTx createMergeBranchTx = new CreateMergeBranchTx(sourceBranch, destBranch, artIds);
         createMergeBranchTx.execute();
         return createMergeBranchTx.getMergeBranch();
      } catch (SQLException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public void addArtifactsToBranch(Branch sourceBranch, Branch destBranch, Branch mergeBranch, Collection<Integer> artIds) throws OseeCoreException, SQLException {
      try {
         CreateMergeBranchTx createMergeBranchTx =
               new CreateMergeBranchTx(sourceBranch, destBranch, artIds, mergeBranch);
         createMergeBranchTx.execute();
      } catch (SQLException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   private final class CreateMergeBranchTx extends AbstractDbTxTemplate {
      private Branch sourceBranch;
      private Branch destBranch;
      private Collection<Integer> artIds;
      private Branch mergeBranch;

      /**
       * @param sourceBranch
       * @param destBranch
       * @param artIds
       */
      public CreateMergeBranchTx(Branch sourceBranch, Branch destBranch, Collection<Integer> artIds) throws SQLException {
         this(sourceBranch, destBranch, artIds, null);
      }

      /**
       * @param sourceBranch
       * @param destBranch
       * @param artIds
       */
      public CreateMergeBranchTx(Branch sourceBranch, Branch destBranch, Collection<Integer> artIds, Branch mergeBranch) throws SQLException {
         super();
         this.sourceBranch = sourceBranch;
         this.destBranch = destBranch;
         this.artIds = artIds;
         this.mergeBranch = mergeBranch;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws OseeCoreException, SQLException {
         boolean createBranch = (mergeBranch == null);

         if (artIds == null || artIds.isEmpty()) {
            throw new IllegalArgumentException("Artifact IDs can not be null or empty");
         }

         Pair<Branch, Integer> branchWithTransactionNumber;
         if (createBranch) {
            branchWithTransactionNumber =
                  createMergeBranchWithBaselineTransactionNumber(SkynetAuthentication.getUser(),
                        TransactionIdManager.getInstance().getStartEndPoint(sourceBranch).getKey(),
                        "Merge " + sourceBranch.getDisplayName(), "Merge " + sourceBranch.getDisplayName(),
                        BranchType.MERGE, destBranch);
         } else {
            TransactionId startTransactionId =
                  TransactionIdManager.getInstance().getStartEndPoint(mergeBranch).getKey();
            branchWithTransactionNumber =
                  new Pair<Branch, Integer>(mergeBranch, startTransactionId.getTransactionNumber());
         }

         List<Object[]> datas = new LinkedList<Object[]>();
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         for (int artId : artIds) {
            datas.add(new Object[] {SQL3DataType.INTEGER, queryId, SQL3DataType.TIMESTAMP, insertTime,
                  SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER, sourceBranch.getBranchId()});
         }
         try {
            ArtifactLoader.selectArtifacts(datas);
            String attributeGammas =
                  "INSERT INTO OSEE_DEFINE_TXS (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, atr1.gamma_id, txs1.mod_type, ? FROM osee_define_attribute atr1, osee_define_txs txs1, osee_define_tx_details txd1, osee_join_artifact ald1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = atr1.gamma_id AND atr1.art_id = ald1.art_id and ald1.query_id = ?";
            String artifactVersionGammas =
                  "INSERT INTO OSEE_DEFINE_TXS (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, arv1.gamma_id, txs1.mod_type, ? FROM osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1, osee_join_artifact ald1 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (1,2) AND txs1.gamma_id = arv1.gamma_id AND arv1.art_id = ald1.art_id and ald1.query_id = ?";

            insertGammas(attributeGammas, branchWithTransactionNumber.getValue(), queryId);
            insertGammas(artifactVersionGammas, branchWithTransactionNumber.getValue(), queryId);
         } finally {
            ArtifactLoader.clearQuery(queryId);
         }

         mergeBranch = branchWithTransactionNumber.getKey();

         ConnectionHandlerStatement chStmt = null;

         if (createBranch) {
            try {
               ConnectionHandler.runPreparedUpdate(MERGE_BRANCH_INSERT, SQL3DataType.INTEGER,
                     sourceBranch.getBranchId(), SQL3DataType.INTEGER, destBranch.getBranchId(), SQL3DataType.INTEGER,
                     mergeBranch.getBranchId());
            } finally {
               DbUtil.close(chStmt);
            }
         }
      }

      public Branch getMergeBranch() {
         return mergeBranch;
      }

      private void insertGammas(String sql, int baselineTransactionNumber, int queryId) throws SQLException {
         ConnectionHandler.runPreparedUpdate(sql, SQL3DataType.INTEGER, baselineTransactionNumber,
               SQL3DataType.INTEGER, TxChange.CURRENT.getValue(), SQL3DataType.INTEGER, sourceBranch.getBranchId(),
               SQL3DataType.INTEGER, queryId);
      }
   }
}