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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TXD_COMMENT;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.branch.management.internal.BranchCreation.CreateBranchTx;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Jeff C. Phillips
 */
public class CreateBranchWithFiltering extends CreateBranchTx {
   private static final LocalAliasTable TRANSACTIONS_ALIAS_1 = new LocalAliasTable(TRANSACTIONS_TABLE, "t5");
   private static final LocalAliasTable TRANSACTIONS_ALIAS_2 = new LocalAliasTable(TRANSACTIONS_TABLE, "t6");
   private static final LocalAliasTable ARTIFACT_ALIAS_1 = new LocalAliasTable(ARTIFACT_TABLE, "t11");
   private static final LocalAliasTable ARTIFACT_ALIAS_2 = new LocalAliasTable(ARTIFACT_TABLE, "t12");
   private static final LocalAliasTable ARTIFACT_VERSION_ALIAS_13 = new LocalAliasTable(ARTIFACT_VERSION_TABLE, "t13");
   private static final LocalAliasTable ARTIFACT_VERSION_ALIAS_1 = new LocalAliasTable(ARTIFACT_VERSION_TABLE, "t1");
   private static final LocalAliasTable ARTIFACT_VERSION_ALIAS_2 = new LocalAliasTable(ARTIFACT_VERSION_TABLE, "t2");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_1 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t3");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_2 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "t4");
   private static final LocalAliasTable TRANSACTIONS_ALIAS_3 = new LocalAliasTable(TRANSACTIONS_TABLE, "t7");
   private static final LocalAliasTable TRANSACTIONS_ALIAS_4 = new LocalAliasTable(TRANSACTIONS_TABLE, "t8");
   private static final LocalAliasTable LINK_ALIAS_1 = new LocalAliasTable(RELATION_LINK_VERSION_TABLE, "t9");
   private static final LocalAliasTable LINK_ALIAS_2 = new LocalAliasTable(RELATION_LINK_VERSION_TABLE, "t10");

   private static final String SELECT_ARTIFACT_HISTORY =
         "SELECT " + TRANSACTIONS_ALIAS_1.columns("transaction_id", "gamma_id") + " AS art_gamma_id, " + TRANSACTIONS_ALIAS_1.column("mod_type") + " AS art_mod_type, " + TRANSACTIONS_ALIAS_1.column("tx_current") + " AS art_current, " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " AS attr_gamma_id, " + TRANSACTIONS_ALIAS_2.column("tx_current") + " AS attr_current, " + TRANSACTIONS_ALIAS_2.column("mod_type") + " AS attr_mod_type FROM " + ARTIFACT_TABLE + " , " + ARTIFACT_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + "," + ATTRIBUTE_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_2 + " WHERE " + ARTIFACT_TABLE.column("art_type_id") + " =?" + " AND " + ARTIFACT_TABLE.column("art_id") + " = " + ARTIFACT_VERSION_TABLE.column("art_id") + " AND " + ARTIFACT_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " = " + TRANSACTIONS_ALIAS_2.column("transaction_id") + " AND " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " = " + ATTRIBUTE_VERSION_TABLE.column("gamma_id") + " AND " + ATTRIBUTE_VERSION_TABLE.column("art_id") + " = " + ARTIFACT_TABLE.column("art_id");
   private static final String INSERT_TX_DETAILS_FOR_HISTORY =
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE + " (branch_id, transaction_id, tx_type, " + TXD_COMMENT + ", time, author, commit_art_id)" + " SELECT ?, ?, ?, " + TRANSACTION_DETAIL_TABLE.columns(
               TXD_COMMENT, "time", "author", "commit_art_id") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " = ?";
   private static final String INSERT_LINK_GAMMAS =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, mod_type, tx_current) " + "SELECT ?, " + LINK_ALIAS_1.column("gamma_id") + ", " + TRANSACTIONS_ALIAS_1.column("mod_type") + ", ? FROM " + TRANSACTIONS_ALIAS_1 + "," + ARTIFACT_VERSION_ALIAS_1 + "," + TRANSACTIONS_ALIAS_2 + "," + ARTIFACT_VERSION_ALIAS_2 + "," + LINK_ALIAS_1 + "," + TRANSACTIONS_ALIAS_3 + " WHERE " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " = " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + " AND " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " = " + LINK_ALIAS_1.column("a_art_id") + " AND " + TRANSACTIONS_ALIAS_2.column("transaction_id") + " = ?" + " AND " + TRANSACTIONS_ALIAS_2.column("gamma_id") + " = " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + " = " + LINK_ALIAS_1.column("b_art_id") + " AND " + LINK_ALIAS_1.column("modification_id") + " <> 3 AND " + LINK_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_3.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_3.column("transaction_id") + "=" + "(SELECT max(transaction_id) FROM " + LINK_ALIAS_2 + "," + TRANSACTIONS_ALIAS_4 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + LINK_ALIAS_2.column("rel_link_id") + "=" + LINK_ALIAS_1.column("rel_link_id") + " AND " + LINK_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_4.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_4.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";
   private static final String INSERT_ATTRIBUTES_GAMMAS =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, mod_type, tx_current) " + "SELECT ?, " + ATTRIBUTE_ALIAS_1.columns("gamma_id") + ", " + TRANSACTIONS_ALIAS_1.column("mod_type") + ", ? FROM " + TRANSACTIONS_ALIAS_1 + "," + ARTIFACT_VERSION_TABLE + "," + ATTRIBUTE_ALIAS_1 + "," + TRANSACTIONS_ALIAS_2 + " WHERE " + TRANSACTIONS_ALIAS_1.column("transaction_id") + " =? AND " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " = " + ARTIFACT_VERSION_TABLE.column("gamma_id") + " AND " + ARTIFACT_VERSION_TABLE.column("art_id") + " = " + ATTRIBUTE_ALIAS_1.column("art_id") + " AND " + ATTRIBUTE_ALIAS_1.column("modification_id") + " <> 3 AND " + ATTRIBUTE_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_2.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_2.column("transaction_id") + "=" + "(SELECT max(transaction_id) FROM " + ATTRIBUTE_ALIAS_2 + "," + TRANSACTIONS_ALIAS_3 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_ALIAS_1.column("attr_id") + "=" + ATTRIBUTE_ALIAS_2.column("attr_id") + " AND " + ATTRIBUTE_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_ALIAS_3.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_3.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";
   private static final String INSERT_TX_FOR_HISTORY =
         "INSERT INTO " + TRANSACTIONS_TABLE.columnsForInsert("transaction_id", "gamma_id", "mod_type", "tx_current");
   private static final String SELECTIVELY_BRANCH_ARTIFACTS_COMPRESSED =
         "INSERT INTO " + TRANSACTIONS_TABLE + "(transaction_id, gamma_id, mod_type, tx_current) " + "SELECT ?, " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + ", " + TRANSACTIONS_TABLE.column("mod_type") + ", ? FROM " + ARTIFACT_TABLE + " , " + ARTIFACT_VERSION_ALIAS_1 + "," + TRANSACTIONS_TABLE + " WHERE " + ARTIFACT_TABLE.column("art_type_id") + " =? AND " + ARTIFACT_TABLE.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_1.column("modification_id") + " <> 3 AND " + ARTIFACT_VERSION_ALIAS_1.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT max(transaction_id) FROM " + ARTIFACT_VERSION_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_VERSION_ALIAS_1.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_2.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_2.column("gamma_id") + " = " + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

   private String[] compressArtTypeIds;
   private String[] preserveArtTypeIds;
   private Map<Integer, Integer> gammasToCurrent = new HashMap<Integer, Integer>();

   public CreateBranchWithFiltering(int parentTransactionId, int parentBranchId, String childBranchShortName, String childBranchName, String creationComment, int associatedArtifactId, int authorId, String[] compressArtTypeIds, String[] preserveArtTypeIds) throws OseeCoreException {
      super(parentTransactionId, parentBranchId, childBranchShortName, childBranchName, creationComment,
            associatedArtifactId, authorId, BranchType.WORKING);
      this.compressArtTypeIds = compressArtTypeIds;
      this.preserveArtTypeIds = preserveArtTypeIds;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.AbstractDbTxTemplate#handleTxWork()
    */
   @Override
   public void specializedBranchOperations(int newBranchId, int newTransactionNumber, OseeConnection connection) throws OseeDataStoreException {
      gammasToCurrent.clear();

      if (compressArtTypeIds != null && compressArtTypeIds.length > 0) {
         createBaselineTransaction(newTransactionNumber, compressArtTypeIds, connection);
      }

      HashCollection<Integer, Pair<Integer, Integer>> historyMap =
            new HashCollection<Integer, Pair<Integer, Integer>>(false, HashSet.class);
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      try {
         for (String preserveArtTypeId : preserveArtTypeIds) {

            chStmt.runPreparedQuery(SELECT_ARTIFACT_HISTORY, preserveArtTypeId, getParentBranchId());

            while (chStmt.next()) {
               int artGamma = chStmt.getInt("art_gamma_id");
               int artCurrent = chStmt.getInt("art_current");
               int attrGamma = chStmt.getInt("attr_gamma_id");
               int attrCurrent = chStmt.getInt("attr_current");

               historyMap.put(chStmt.getInt("transaction_id"), new Pair<Integer, Integer>(artGamma,
                     chStmt.getInt("art_mod_type")));

               historyMap.put(chStmt.getInt("transaction_id"), new Pair<Integer, Integer>(attrGamma,
                     chStmt.getInt("attr_mod_type")));

               gammasToCurrent.put(artGamma, artCurrent);
               gammasToCurrent.put(attrGamma, attrCurrent);
            }
         }
         initSelectLinkHistory(compressArtTypeIds, preserveArtTypeIds, historyMap, connection);
      } finally {
         chStmt.close();
      }

      Set<Integer> transactions = new TreeSet<Integer>(historyMap.keySet()); // the tree set is to in ascending order
      for (Integer parentTransactionNumber : transactions) {
         int nextTransactionNumber = SequenceManager.getNextTransactionId();

         ConnectionHandler.runPreparedUpdate(connection, INSERT_TX_DETAILS_FOR_HISTORY, getNewBranchId(),
               nextTransactionNumber, 0, parentTransactionNumber.intValue());

         Set<Integer> gammas = new HashSet<Integer>();
         for (Pair<Integer, Integer> gammaAndMod : historyMap.getValues(parentTransactionNumber)) {
            Integer modType = gammaAndMod.getValue();

            if (!gammas.contains(gammaAndMod.getKey())) {
               ConnectionHandler.runPreparedUpdate(connection, INSERT_TX_FOR_HISTORY, nextTransactionNumber,
                     gammaAndMod.getKey(), modType, gammasToCurrent.get(gammaAndMod.getKey()));
               gammas.add(gammaAndMod.getKey());
            }
         }
      }
   }

   private void createBaselineTransaction(int newTransactionNumber, String[] compressArtTypes, OseeConnection connection) throws OseeDataStoreException {
      for (String artifactTypeId : compressArtTypes) {
         ConnectionHandler.runPreparedUpdate(connection, SELECTIVELY_BRANCH_ARTIFACTS_COMPRESSED, newTransactionNumber,
               1, artifactTypeId, getParentBranchId());
      }
      ConnectionHandler.runPreparedUpdate(connection, INSERT_ATTRIBUTES_GAMMAS, newTransactionNumber, 1,
            newTransactionNumber, getParentBranchId());

      ConnectionHandler.runPreparedUpdate(connection, INSERT_LINK_GAMMAS, newTransactionNumber, 1,
            newTransactionNumber, newTransactionNumber, getParentBranchId());
   }

   /**
    * artifact a | artifact b | select link ===========|============|============ I | I | false I | C | false I | P |
    * false C | I | false P | I | false C | C | false C | P | true P | C | true P | P | true
    * 
    * @param compressArtTypes
    * @param preserveArtTypes
    */
   private void initSelectLinkHistory(String[] compressArtTypeIds, String[] preserveArtTypeIds, HashCollection<Integer, Pair<Integer, Integer>> historyMap, OseeConnection connection) throws OseeDataStoreException {
      String preservedTypeSet = makeArtTypeSet(null, preserveArtTypeIds);
      String compressTypeSet = makeArtTypeSet(compressArtTypeIds, null);

      if (compressArtTypeIds != null && compressArtTypeIds.length > 0) {
         // Handles the case C | P | true
         String cpSql =
               "SELECT " + TRANSACTIONS_ALIAS_1.columns("mod_type", "transaction_id", "tx_current", "gamma_id") + " AS link_gamma_id  FROM " + ARTIFACT_ALIAS_1 + "," + ARTIFACT_ALIAS_2 + "," + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + "," + ARTIFACT_VERSION_ALIAS_13 + " WHERE " + ARTIFACT_ALIAS_1.column("art_type_id") + " IN " + compressTypeSet + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("a_art_id") + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_13.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_13.column("modification_id") + " <> 3 "

               + " AND " + ARTIFACT_ALIAS_2.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_2.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("b_art_id")

               + " AND " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?";

         populateHistoryMapWithRelations(historyMap, cpSql, connection);

         // Handles the case P | C | true
         String pcSql =
               "SELECT " + TRANSACTIONS_ALIAS_1.columns("mod_type", "transaction_id", "tx_current", "gamma_id") + " AS link_gamma_id" + " FROM " + ARTIFACT_ALIAS_1 + "," + ARTIFACT_ALIAS_2 + "," + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + "," + ARTIFACT_VERSION_ALIAS_13 + " WHERE " + ARTIFACT_ALIAS_1.column("art_type_id") + " IN " + compressTypeSet + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("b_art_id") + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + ARTIFACT_VERSION_ALIAS_13.column("art_id") + " AND " + ARTIFACT_VERSION_ALIAS_13.column("modification_id") + " <> 3 "

               + " AND " + ARTIFACT_ALIAS_2.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_2.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("a_art_id")

               + " AND " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?";

         populateHistoryMapWithRelations(historyMap, pcSql, connection);
      }

      // Handles the case P | P | true
      String ppSql =
            "SELECT " + TRANSACTIONS_ALIAS_1.columns("mod_type", "transaction_id", "tx_current", "gamma_id") + " AS link_gamma_id" + " FROM " + ARTIFACT_ALIAS_1 + "," + ARTIFACT_ALIAS_2 + "," + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_ALIAS_1 + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ARTIFACT_ALIAS_1.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_1.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("b_art_id")

            + " AND " + ARTIFACT_ALIAS_2.column("art_type_id") + " IN " + preservedTypeSet + " AND " + ARTIFACT_ALIAS_2.column("art_id") + " = " + RELATION_LINK_VERSION_TABLE.column("a_art_id")

            + " AND " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + " = " + TRANSACTIONS_ALIAS_1.column("gamma_id") + " AND " + TRANSACTIONS_ALIAS_1.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?";

      populateHistoryMapWithRelations(historyMap, ppSql, connection);
   }

   private void populateHistoryMapWithRelations(HashCollection<Integer, Pair<Integer, Integer>> historyMap, String sql, OseeConnection connection) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      try {
         chStmt.runPreparedQuery(sql, getParentBranchId());
         while (chStmt.next()) {
            int linkGammaId = chStmt.getInt("link_gamma_id");
            int txCurrent = chStmt.getInt("tx_current");

            historyMap.put(chStmt.getInt("transaction_id"), new Pair<Integer, Integer>(linkGammaId,
                  chStmt.getInt("mod_type")));

            gammasToCurrent.put(linkGammaId, txCurrent);
         }
      } finally {
         chStmt.close();
      }
   }

   private String makeArtTypeSet(String[] compressArtTypes, String[] preserveArtTypes) {
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

   private boolean addTypes(StringBuilder typeStrB, String[] artifactTypes, boolean firstItem) {
      for (String artifactTypeId : artifactTypes) {
         if (firstItem) {
            firstItem = false;
         } else {
            typeStrB.append(",");
         }
         typeStrB.append(artifactTypeId);
      }
      return firstItem;
   }
}
