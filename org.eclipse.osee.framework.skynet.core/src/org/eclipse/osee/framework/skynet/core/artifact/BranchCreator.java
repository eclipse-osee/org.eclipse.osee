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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.BRANCH_DEFINITIONS;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.BRANCH_ID_SEQ;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.BRANCH_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_TYPE_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_ID_SEQ;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TXD_COMMENT;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.VALID_RELATIONS_TABLE;
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
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkNewBranchEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.PersistenceManager;
import org.eclipse.osee.framework.skynet.core.PersistenceManagerInit;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.LocalNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.remoteEvent.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.LocalAliasTable;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.Table;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType;

/**
 * @author Ryan D. Brooks
 */
public class BranchCreator implements PersistenceManager {
   static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchPersistenceManager.class);

   private static final LocalAliasTable RELATION_LINK_TYPE_ALIAS_1 =
         new LocalAliasTable(RELATION_LINK_TYPE_TABLE, "t1");
   private static final LocalAliasTable RELATION_LINK_TYPE_ALIAS_2 =
         new LocalAliasTable(RELATION_LINK_TYPE_TABLE, "t2");

   private static final LocalAliasTable VALID_RELATIONS_ALIAS_1 = new LocalAliasTable(VALID_RELATIONS_TABLE, "t1");
   private static final LocalAliasTable VALID_RELATIONS_ALIAS_2 = new LocalAliasTable(VALID_RELATIONS_TABLE, "t2");

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
               "archived", "associated_art_id");
   private static final String SELECT_BRANCH_BY_NAME = "SELECT * FROM " + BRANCH_TABLE + " WHERE branch_name = ?";

   /* TODO: DISTINCT */
   private static final String INSERT_LINK_GAMMAS =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, tx_type) " + "SELECT ?, " + LINK_ALIAS_1.columns("gamma_id") + ", ?" + " FROM " + TRANSACTIONS_ALIAS_1 + "," + ARTIFACT_VERSION_ALIAS_1 + "," + TRANSACTIONS_ALIAS_2 + "," + ARTIFACT_VERSION_ALIAS_2 + "," + LINK_ALIAS_1 + "," + TRANSACTIONS_ALIAS_3 + " WHERE " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " = " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + " AND " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " = " + LINK_ALIAS_1.column("a_art_id") + " AND " + TRANSACTIONS_ALIAS_2.column("transaction_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " = " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + " = " + LINK_ALIAS_1.column("b_art_id") + " AND " + LINK_ALIAS_1.column("modification_id") + " <> " + ModificationType.DELETE.getValue() + " AND " + LINK_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_3.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_3.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + LINK_ALIAS_2 + "," + TRANSACTIONS_ALIAS_4 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + LINK_ALIAS_2.column("rel_link_id") + "=" + LINK_ALIAS_1.column("rel_link_id") + " AND " + LINK_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_4.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_4.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   private static final String INSERT_ATTRIBUTES_GAMMAS =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, tx_type) " + "SELECT ?, " + ATTRIBUTE_ALIAS_1.columns("gamma_id") + ", ?" + " FROM " + TRANSACTIONS_ALIAS_1 + "," + ARTIFACT_VERSION_TABLE + "," + ATTRIBUTE_ALIAS_1 + "," + TRANSACTIONS_ALIAS_2 + " WHERE " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " =? AND " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " = " + ARTIFACT_VERSION_TABLE.column("gamma_id") + " AND " + ARTIFACT_VERSION_TABLE.column("art_id") + " = " + ATTRIBUTE_ALIAS_1.column("art_id") + " AND " + ATTRIBUTE_ALIAS_1.column("modification_id") + " <> " + ModificationType.DELETE.getValue() + " AND " + ATTRIBUTE_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_2.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_2.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + ATTRIBUTE_ALIAS_2 + "," + TRANSACTIONS_ALIAS_3 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_ALIAS_1.column("attr_id") + "=" + ATTRIBUTE_ALIAS_2.column("attr_id") + " AND " + ATTRIBUTE_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_3.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_3.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " <= ?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   // insert the artifact versions and attributes for all non-deleted artifact of a given type on a
   // given branch
   private static final String SELECTIVELY_BRANCH_ARTIFACTS_COMPRESSED =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, tx_type) " + "SELECT ?, " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + ", ?" + " FROM " + ARTIFACT_TABLE + " , " + ARTIFACT_VERSION_ALIAS_1 + "," + TRANSACTIONS_TABLE + " WHERE " + ARTIFACT_TABLE.column("art_type_id") + " =? AND " + ARTIFACT_TABLE.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_1.column("modification_id") + " <> " + ModificationType.DELETE.getValue() + " AND " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + ARTIFACT_VERSION_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   private static final String SELECT_ARTIFACT_HISTORY =
         "SELECT " + TRANSACTIONS_ALIAS_1.columns("transaction_id", "gamma_id") + " AS art_gamma_id, " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " AS attr_gamma_id" + " FROM " + ARTIFACT_TABLE + " , " + ARTIFACT_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + "," + ATTRIBUTE_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_2 + " WHERE " + ARTIFACT_TABLE.column("art_type_id") + " =?" + " AND " + ARTIFACT_TABLE.column("art_id") + " = " + ARTIFACT_VERSION_TABLE.column("art_id") + " AND " + ARTIFACT_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " = " + TRANSACTIONS_ALIAS_2.column("transaction_id") + " AND " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " = " + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + " AND " + ATTRIBUTE_VERSION_TABLE.column("art_id") + " = " + ARTIFACT_TABLE.column("art_id");

   private static final String INSERT_TX_DETAILS_FOR_HISTORY =
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE + " (branch_id, transaction_id, " + TXD_COMMENT + ", time, author)" + " SELECT ?, ?, " + TRANSACTION_DETAIL_TABLE.columns(
               TXD_COMMENT, "time", "author") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " = ?";

   private static final String INSERT_TX_FOR_HISTORY =
         "INSERT INTO " + TRANSACTIONS_TABLE.columnsForInsert("transaction_id", "gamma_id", "tx_type");

   private static final String INSERT_DEFAULT_BRANCH_NAMES =
         "INSERT INTO " + BRANCH_DEFINITIONS.columnsForInsert("static_branch_name", "mapped_branch_id");
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final RemoteEventManager remoteEventManager = RemoteEventManager.getInstance();
   private SkynetAuthentication skynetAuth;
   private static final BranchCreator instance = new BranchCreator();

   private BranchCreator() {

   }

   public static BranchCreator getInstance() {
      PersistenceManagerInit.initManagerWeb(instance);
      return instance;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.PersistenceManager#setRelatedManagers()
    */
   public void onManagerWebInit() throws Exception {
      skynetAuth = SkynetAuthentication.getInstance();
   }

   private void copyBranchAddressing(Branch newBranch, int newTransactionNumber, TransactionId parentTransactionId, Collection<ArtifactSubtypeDescriptor> compressArtTypes, Collection<ArtifactSubtypeDescriptor> preserveArtTypes) throws SQLException {
      if (compressArtTypes != null && preserveArtTypes != null) {
         Set<ArtifactSubtypeDescriptor> intersection = new HashSet<ArtifactSubtypeDescriptor>(compressArtTypes);
         intersection.retainAll(preserveArtTypes);
         if (intersection.size() > 0) {
            throw new IllegalArgumentException("The following artifact types are in both sets: " + intersection);
         }
      }

      copyTypeConfigurationAddressing(newTransactionNumber, parentTransactionId);

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
   public static void branchWithHistory(Branch newBranch, TransactionId parentTransactionId, Collection<ArtifactSubtypeDescriptor> compressArtTypes, Collection<ArtifactSubtypeDescriptor> preserveArtTypes) throws SQLException {
      HashCollection<Integer, Integer> historyMap =
            new HashCollection<Integer /*
                                                                                                                                                                                                                                                                                                                                       * parent
                                                                                                                                                                                                                                                                                                                                       * transactoin_id
                                                                                                                                                                                                                                                                                                                                       */, Integer /* gamma_id */>(
                  false, HashSet.class);
      ConnectionHandlerStatement chStmt = null;
      try {
         for (ArtifactSubtypeDescriptor artifactType : preserveArtTypes) {

            chStmt =
                  ConnectionHandler.runPreparedQuery(SELECT_ARTIFACT_HISTORY, SQL3DataType.INTEGER,
                        artifactType.getArtTypeId(), SQL3DataType.INTEGER, parentTransactionId.getTransactionNumber(),
                        SQL3DataType.INTEGER, parentTransactionId.getBranch().getBranchId());

            ResultSet rSet = chStmt.getRset();
            while (chStmt.next()) {
               historyMap.put(rSet.getInt("transaction_id"), rSet.getInt("art_gamma_id"));
               historyMap.put(rSet.getInt("transaction_id"), rSet.getInt("attr_gamma_id"));
            }
         }
         historyMap = initSelectLinkHistory(compressArtTypes, preserveArtTypes, parentTransactionId, historyMap);
      } finally {
         DbUtil.close(chStmt);
      }

      Set<Integer> transactions = new TreeSet<Integer>(historyMap.keySet()); // the tree set is to
      // sort the
      // transaction numbers
      // in ascending order
      // List<Object[]>txDetailData = new LinkedList<Object[]>();
      List<Object[]> txAddressData = new LinkedList<Object[]>();
      for (Integer parentTransactionNumber : transactions) {
         int nextTransactionNumber = Query.getNextSeqVal(null, TRANSACTION_ID_SEQ);

         ConnectionHandler.runPreparedUpdate(INSERT_TX_DETAILS_FOR_HISTORY, SQL3DataType.INTEGER,
               newBranch.getBranchId(), SQL3DataType.INTEGER, nextTransactionNumber, SQL3DataType.INTEGER,
               parentTransactionNumber.intValue());
         // txDetailData.add(new Object[]{
         // SQL3DataType.INTEGER, newBranch.getBranchId(),
         // SQL3DataType.INTEGER, nextTransactionNumber,
         // SQL3DataType.INTEGER, parentTransactionNumber.intValue()});

         Collection<Integer> gammas = historyMap.getValues(parentTransactionNumber);
         for (Integer gamma : gammas) {
            txAddressData.add(new Object[] {SQL3DataType.INTEGER, nextTransactionNumber, SQL3DataType.INTEGER, gamma,
                  SQL3DataType.INTEGER, TransactionType.BRANCHED.getId()});
         }
         ConnectionHandler.runBatchablePreparedUpdate(INSERT_TX_FOR_HISTORY, true, txAddressData);
         txAddressData.clear();
      }
      // ConnectionHandler.runBatchablePreparedUpdate(INSERT_TX_DETAILS_FOR_HISTORY, true,
      // txDetailData);
   }

   /**
    * artifact a | artifact b | select link ===========|============|============ I | I | false I | C | false I | P |
    * false C | I | false P | I | false C | C | false C | P | true P | C | true P | P | true
    * 
    * @param compressArtTypes
    * @param preserveArtTypes
    * @return
    */
   private static HashCollection<Integer, Integer> initSelectLinkHistory(Collection<ArtifactSubtypeDescriptor> compressArtTypes, Collection<ArtifactSubtypeDescriptor> preserveArtTypes, TransactionId parentTransactionId, HashCollection<Integer, Integer> historyMap) {
      String preservedTypeSet = makeArtTypeSet(null, preserveArtTypes);
      String compressTypeSet = makeArtTypeSet(compressArtTypes, null);

      if (compressArtTypes != null && compressArtTypes.size() > 0) {
         // Handles the case C | P | true
         String cpSql =
               "SELECT " + TRANSACTIONS_ALIAS_1.columns("transaction_id", "gamma_id") + " AS link_gamma_id" + " FROM " + ARTIFACT_ALIAS_1 + "," + ARTIFACT_ALIAS_2 + "," + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + "," + ARTIFACT_VERSION_ALIAS_13 + " WHERE " + ARTIFACT_ALIAS_1.column("art_type_id") + " IN " + compressTypeSet + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("a_art_id") + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_13.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_13.column("modification_id") + " <> " + ModificationType.DELETE

               + " AND " + ARTIFACT_ALIAS_2.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_2.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("b_art_id")

               + " AND " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?";

         historyMap = populateHistoryMapWithRelations(historyMap, cpSql, parentTransactionId);

         // Handles the case P | C | true
         String pcSql =
               "SELECT " + TRANSACTIONS_ALIAS_1.columns("transaction_id", "gamma_id") + " AS link_gamma_id" + " FROM " + ARTIFACT_ALIAS_1 + "," + ARTIFACT_ALIAS_2 + "," + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + "," + ARTIFACT_VERSION_ALIAS_13 + " WHERE " + ARTIFACT_ALIAS_1.column("art_type_id") + " IN " + compressTypeSet + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("b_art_id") + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_13.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_13.column("modification_id") + " <> " + ModificationType.DELETE

               + " AND " + ARTIFACT_ALIAS_2.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_2.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("a_art_id")

               + " AND " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?";

         historyMap = populateHistoryMapWithRelations(historyMap, pcSql, parentTransactionId);
      }

      // Handles the case P | P | true
      String ppSql =
            "SELECT " + TRANSACTIONS_ALIAS_1.columns("transaction_id", "gamma_id") + " AS link_gamma_id" + " FROM " + ARTIFACT_ALIAS_1 + "," + ARTIFACT_ALIAS_2 + "," + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_ALIAS_1.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("b_art_id")

            + " AND " + ARTIFACT_ALIAS_2.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_2.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("a_art_id")

            + " AND " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?";

      historyMap = populateHistoryMapWithRelations(historyMap, ppSql, parentTransactionId);

      return historyMap;
   }

   private static HashCollection<Integer, Integer> populateHistoryMapWithRelations(HashCollection<Integer, Integer> historyMap, String sql, TransactionId parentTransactionId) {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(sql, SQL3DataType.INTEGER,
                     parentTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                     parentTransactionId.getBranch().getBranchId());
         ResultSet rSet = chStmt.getRset();
         while (chStmt.next()) {
            historyMap.put(rSet.getInt("transaction_id"), rSet.getInt("link_gamma_id"));
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } finally {
         DbUtil.close(chStmt);
      }
      return historyMap;
   }

   private void copyTypeConfigurationAddressing(int newTransactionNumber, TransactionId parentTransactionId) throws SQLException {
      insertGamms(newTransactionNumber, parentTransactionId, RELATION_LINK_TYPE_ALIAS_1, RELATION_LINK_TYPE_ALIAS_2,
            "rel_link_type_id");
      insertGamms(newTransactionNumber, parentTransactionId, VALID_RELATIONS_ALIAS_1, VALID_RELATIONS_ALIAS_2,
            "rel_link_type_id", "art_type_id");
   }

   private void createBaselineTransaction(int newTransactionNumber, TransactionId parentTransactionId, Collection<ArtifactSubtypeDescriptor> compressArtTypes) throws SQLException {
      for (ArtifactSubtypeDescriptor artifactType : compressArtTypes) {
         int count =
               ConnectionHandler.runPreparedUpdateReturnCount(SELECTIVELY_BRANCH_ARTIFACTS_COMPRESSED,
                     SQL3DataType.INTEGER, newTransactionNumber, SQL3DataType.INTEGER,
                     TransactionType.BRANCHED.getId(), SQL3DataType.INTEGER, artifactType.getArtTypeId(),
                     SQL3DataType.INTEGER, parentTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                     parentTransactionId.getBranch().getBranchId());
         if (count > 0) logger.log(Level.INFO, "inserted " + count + " " + artifactType.getName() + " artifacts");
      }

      int count =
            ConnectionHandler.runPreparedUpdateReturnCount(INSERT_ATTRIBUTES_GAMMAS, SQL3DataType.INTEGER,
                  newTransactionNumber, SQL3DataType.INTEGER, TransactionType.BRANCHED.getId(), SQL3DataType.INTEGER,
                  newTransactionNumber, SQL3DataType.INTEGER, parentTransactionId.getTransactionNumber(),
                  SQL3DataType.INTEGER, parentTransactionId.getBranch().getBranchId());
      if (count > 0) logger.log(Level.INFO, "inserted " + count + " attributes");

      count =
            ConnectionHandler.runPreparedUpdateReturnCount(INSERT_LINK_GAMMAS, SQL3DataType.INTEGER,
                  newTransactionNumber, SQL3DataType.INTEGER, TransactionType.BRANCHED.getId(), SQL3DataType.INTEGER,
                  newTransactionNumber, SQL3DataType.INTEGER, newTransactionNumber, SQL3DataType.INTEGER,
                  parentTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                  parentTransactionId.getBranch().getBranchId());
      if (count > 0) logger.log(Level.INFO, "inserted " + count + " relations");
   }

   private void insertGamms(int newTransactionNumber, TransactionId parentTransactionId, Table table1, Table table2, String... columns) throws SQLException {
      StringBuilder joinParallelColumns = new StringBuilder(300);
      for (String column : columns) {
         joinParallelColumns.append(table1.join(table2, column));
         joinParallelColumns.append(" AND ");
      }

      String insert =
            "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, tx_type) " + "SELECT ?, " + table1.column("gamma_id") + ", ?" + " FROM " + table1 + ", " + TRANSACTIONS_ALIAS_1 + " WHERE " + table1.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + "(SELECT " + TRANSACTION_DETAIL_TABLE.max("transaction_id") + " FROM " + table2 + "," + TRANSACTIONS_ALIAS_2 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + joinParallelColumns + table2.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_2.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_2.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=?" + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=" + "?)";

      int count =
            ConnectionHandler.runPreparedUpdateReturnCount(insert, SQL3DataType.INTEGER, newTransactionNumber,
                  SQL3DataType.INTEGER, TransactionType.BRANCHED.getId(), SQL3DataType.INTEGER,
                  parentTransactionId.getTransactionNumber(), SQL3DataType.INTEGER,
                  parentTransactionId.getBranch().getBranchId());
      logger.log(Level.INFO, "inserted row count for " + table1 + ": " + count);
   }

   private static String makeArtTypeSet(Collection<ArtifactSubtypeDescriptor> compressArtTypes, Collection<ArtifactSubtypeDescriptor> preserveArtTypes) {
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

   private static boolean addTypes(StringBuilder typeStrB, Collection<ArtifactSubtypeDescriptor> artifactTypes, boolean firstItem) {
      for (ArtifactSubtypeDescriptor artifactType : artifactTypes) {
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
    * @throws IllegalArgumentException
    * @see BranchPersistenceManager#createRootBranch(String, String, int)
    * @see BranchPersistenceManager#getKeyedBranch(String)
    */
   public Branch createRootBranch(String shortBranchName, String branchName, String staticBranchName) throws SQLException, IllegalArgumentException {
      Branch branch =
            initializeBranch(shortBranchName, branchName, null, -1, GlobalTime.GreenwichMeanTimestamp(), "", null);
      if (staticBranchName != null) ConnectionHandler.runPreparedUpdate(INSERT_DEFAULT_BRANCH_NAMES,
            SQL3DataType.VARCHAR, staticBranchName, SQL3DataType.INTEGER, branch.getBranchId());
      return branch;
   }

   /**
    * adds a new branch to the database
    * 
    * @param branchName caller is responsible for ensuring no branch has already been given this name
    * @param parentBranchId the id of the parent branch or NULL_PARENT_BRANCH_ID if this branch has no parent
    * @return branch object that represents the newly created branch
    * @throws SQLException
    */
   private Branch initializeBranch(String branchShortName, String branchName, TransactionId parentBranchId, int authorId, Timestamp creationDate, String creationComment, Artifact associatedArtifact) throws SQLException, IllegalArgumentException {
      ConnectionHandlerStatement chStmt =
            ConnectionHandler.runPreparedQuery(SELECT_BRANCH_BY_NAME, SQL3DataType.VARCHAR, branchName);
      ResultSet rset = chStmt.getRset();
      branchShortName = StringFormat.truncate(branchShortName != null ? branchShortName : branchName, 25);

      try {
         if (rset.next()) {
            throw new IllegalArgumentException("A branch with the name " + branchName + " already exists");
         }
      } finally {
         DbUtil.close(chStmt);
      }

      int branchId = Query.getNextSeqVal(null, BRANCH_ID_SEQ);
      int parentBranchNumber =
            parentBranchId == null ? Branch.NULL_PARENT_BRANCH_ID : parentBranchId.getBranch().getBranchId();
      int associatedArtifactId = -1;

      if (associatedArtifact == null && !SkynetDbInit.isDbInit()) {
         associatedArtifact = skynetAuth.getUser(UserEnum.NoOne);
      }

      if (associatedArtifact != null) {
         associatedArtifactId = associatedArtifact.getArtId();
      }

      ConnectionHandler.runPreparedUpdate(BRANCH_TABLE_INSERT, SQL3DataType.INTEGER, branchId, SQL3DataType.VARCHAR,
            branchShortName, SQL3DataType.VARCHAR, branchName, SQL3DataType.INTEGER, parentBranchNumber,
            SQL3DataType.INTEGER, 0, SQL3DataType.INTEGER, associatedArtifactId);

      // this needs to be after the insert in case there is an exception on insert
      Branch branch;
      if (associatedArtifact == null) {
         branch =
               new Branch(branchShortName, branchName, branchId, parentBranchNumber, false, authorId, creationDate,
                     creationComment, associatedArtifactId);
      } else {
         branch =
               new Branch(branchShortName, branchName, branchId, parentBranchNumber, false, authorId, creationDate,
                     creationComment, associatedArtifact);
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
   public Branch createChildBranch(final TransactionId parentTransactionId, final String childBranchShortName, final String childBranchName, final Artifact associatedArtifact, boolean preserveMetaData, Collection<ArtifactSubtypeDescriptor> compressArtTypes, Collection<ArtifactSubtypeDescriptor> preserveArtTypes) throws Exception {

      CreateChildBranchTx createChildBranchTx =
            new CreateChildBranchTx(parentTransactionId, childBranchShortName, childBranchName, associatedArtifact,
                  compressArtTypes, preserveArtTypes);
      createChildBranchTx.execute();
      return createChildBranchTx.getChildBranch();
   }

   private final class CreateChildBranchTx extends AbstractDbTxTemplate {
      private Branch childBranch;
      private String childBranchShortName;
      private String childBranchName;
      private TransactionId parentTransactionId;
      private Artifact associatedArtifact;
      private Collection<ArtifactSubtypeDescriptor> compressArtTypes;
      private Collection<ArtifactSubtypeDescriptor> preserveArtTypes;
      private boolean success = false;

      public CreateChildBranchTx(TransactionId parentTransactionId, String childBranchShortName, String childBranchName, Artifact associatedArtifact, Collection<ArtifactSubtypeDescriptor> compressArtTypes, Collection<ArtifactSubtypeDescriptor> preserveArtTypes) {
         this.childBranch = null;
         this.parentTransactionId = parentTransactionId;
         this.childBranchShortName = childBranchShortName;
         this.childBranchName = childBranchName;
         this.associatedArtifact = associatedArtifact;
         this.compressArtTypes = compressArtTypes;
         this.preserveArtTypes = preserveArtTypes;
      }

      @Override
      protected void handleTxWork() throws Exception {
         User userToBlame = skynetAuth.getAuthenticatedUser();
         Branch parentBranch = parentTransactionId.getBranch();
         int userId = (userToBlame == null) ? skynetAuth.getUser(UserEnum.NoOne).getArtId() : userToBlame.getArtId();
         String comment =
               BranchPersistenceManager.NEW_BRANCH_COMMENT + parentBranch.getBranchName() + "(" + parentTransactionId.getTransactionNumber() + ")";
         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
         childBranch =
               initializeBranch(childBranchShortName, childBranchName, parentTransactionId, userId, timestamp, comment,
                     associatedArtifact);

         // insert the new transaction data first.
         int newTransactionNumber = Query.getNextSeqVal(null, TRANSACTION_ID_SEQ);
         String query =
               "INSERT INTO " + TRANSACTION_DETAIL_TABLE.columnsForInsert("branch_id", "transaction_id", TXD_COMMENT,
                     "time", "author");
         ConnectionHandler.runPreparedUpdate(query, SQL3DataType.INTEGER, childBranch.getBranchId(),
               SQL3DataType.INTEGER, newTransactionNumber, SQL3DataType.VARCHAR, childBranch.getCreationComment(),
               SQL3DataType.TIMESTAMP, childBranch.getCreationDate(), SQL3DataType.INTEGER, childBranch.getAuthorId());

         copyBranchAddressing(childBranch, newTransactionNumber, parentTransactionId, compressArtTypes,
               preserveArtTypes);

         success = true;

      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxException(java.lang.Exception)
       */
      @Override
      protected void handleTxException(Exception ex) throws Exception {
         super.handleTxException(ex);
         success = false;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxFinally()
       */
      @Override
      protected void handleTxFinally() throws Exception {
         super.handleTxFinally();
         if (success) {
            eventManager.kick(new LocalNewBranchEvent(this, childBranch.getBranchId()));
            remoteEventManager.kick(new NetworkNewBranchEvent(childBranch.getBranchId(),
                  SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId()));
         }
      }

      public Branch getChildBranch() {
         return childBranch;
      }
   }
}