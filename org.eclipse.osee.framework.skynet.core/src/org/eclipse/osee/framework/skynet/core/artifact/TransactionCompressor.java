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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

/**
 * Compresses gammaIds from osee_define_txs table so earlier versions to not cause conflicts. This is needed for
 * branching and committing because many changes are assigned to a single transaction.
 * 
 * @author Jeff C. Phillips
 */
public final class TransactionCompressor extends AbstractDbTxTemplate {
   private static final String TABLE_TOKEN = "<table>";
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(TransactionCompressor.class);

   private static final String GET_ARTIFACTS_TO_COMPRESS =
         "select t1.transaction_id, t1.gamma_id, t1.tx_type" + " from " + SkynetDatabase.TRANSACTIONS_TABLE + " t1, " + SkynetDatabase.ARTIFACT_VERSION_TABLE + " t2" + " WHERE t1.gamma_id = t2.gamma_id" + " and t1.transaction_id = ?" + " and exists (" + " select 'x' from " + SkynetDatabase.TRANSACTIONS_TABLE + " t3, " + SkynetDatabase.ARTIFACT_VERSION_TABLE + " t4" + " where t1.transaction_id = t3.transaction_id" + " and t3.gamma_id = t4.gamma_id" + " and t2.art_id = t4.art_id" + " and t1.gamma_id < t3.gamma_id )";

   private static final String GET_RELATIONS_TO_COMPRESS =
         "select t1.transaction_id, t1.gamma_id, t1.tx_type" + " from " + SkynetDatabase.TRANSACTIONS_TABLE + " t1, " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t2" + " WHERE t1.gamma_id = t2.gamma_id" + " and t1.transaction_id = ?" + " and exists (" + " select 'x' from " + SkynetDatabase.TRANSACTIONS_TABLE + " t3, " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t4" + " where t1.transaction_id = t3.transaction_id" + " and t3.gamma_id = t4.gamma_id" + " and t2.rel_link_id = t4.rel_link_id" + " and t1.gamma_id < t3.gamma_id )";

   private static final String GET_ATTRIBUTES_TO_COMPRESS =
         "select t1.transaction_id, t1.gamma_id, t1.tx_type" + " from " + SkynetDatabase.TRANSACTIONS_TABLE + " t1, " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + " t2" + " WHERE t1.gamma_id = t2.gamma_id" + " and t1.transaction_id = ?" + " and exists (" + " select 'x' from " + SkynetDatabase.TRANSACTIONS_TABLE + " t3, " + SkynetDatabase.ATTRIBUTE_VERSION_TABLE + " t4" + " where t1.transaction_id = t3.transaction_id" + " and t3.gamma_id = t4.gamma_id" + " and t2.attr_id = t4.attr_id" + " and t1.gamma_id < t3.gamma_id )";

   private static String DELETE_COMPRESSED_DATA =
         "DELETE FROM " + SkynetDatabase.TRANSACTIONS_TABLE + " t1 WHERE t1.transaction_id = ? AND EXISTS(SELECT 'x' FROM " + TABLE_TOKEN + " t2 WHERE t1.gamma_id = t2.gamma_id)";

   private boolean isDeleteEnabled;
   private int[] transactionIds;

   /**
    * Compresses gammaIds from osee_define_txs.
    * 
    * @param transactionIds - The transaction to be compressed.
    * @param delete - true to delete the compressed data from the artifact, relation and attribute tables.
    */
   public TransactionCompressor(boolean isDeleteEnabled, int... transactionIds) {
      this.isDeleteEnabled = isDeleteEnabled;
      this.transactionIds = transactionIds;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.plugin.util.db.AbstractDbTxTemplate#handleTxWork()
    */
   @Override
   protected void handleTxWork() throws Exception {
      TempTable artifactTempTable = null;
      TempTable attributeTempTable = null;
      TempTable relationTempTable = null;

      // Without a transaction being in progress, the temporary tables will be cleared automatically
      // before we can use the data

      for (int transactionId : transactionIds) {

         artifactTempTable = new TempTable("ARTIFACT_" + transactionId);
         attributeTempTable = new TempTable("ATTRIBUTE_" + transactionId);
         relationTempTable = new TempTable("RELATION_" + transactionId);

         // Load transactions and gammas to temporary tables to save time for
         // deleting from the attribute, relation and artifact_version tables.
         int insertCount =
               ConnectionHandler.runPreparedUpdateReturnCount(
                     "INSERT INTO " + attributeTempTable + " (transaction_id, gamma_id, tx_type) " + GET_ATTRIBUTES_TO_COMPRESS,
                     SQL3DataType.INTEGER, transactionId);
         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(
                     "INSERT INTO " + artifactTempTable + " (transaction_id, gamma_id, tx_type) " + GET_ARTIFACTS_TO_COMPRESS,
                     SQL3DataType.INTEGER, transactionId);
         insertCount +=
               ConnectionHandler.runPreparedUpdateReturnCount(
                     "INSERT INTO " + relationTempTable + " (transaction_id, gamma_id, tx_type) " + GET_RELATIONS_TO_COMPRESS,
                     SQL3DataType.INTEGER, transactionId);

         if (insertCount > 0) {
            ConnectionHandler.runPreparedUpdateReturnCount(DELETE_COMPRESSED_DATA.replace(TABLE_TOKEN,
                  artifactTempTable.toString()), SQL3DataType.INTEGER, transactionId);
            ConnectionHandler.runPreparedUpdateReturnCount(DELETE_COMPRESSED_DATA.replace(TABLE_TOKEN,
                  attributeTempTable.toString()), SQL3DataType.INTEGER, transactionId);
            ConnectionHandler.runPreparedUpdateReturnCount(DELETE_COMPRESSED_DATA.replace(TABLE_TOKEN,
                  relationTempTable.toString()), SQL3DataType.INTEGER, transactionId);

            if (isDeleteEnabled) {
               // Nothing being done here right now since we are archiving branches off until
               // absolutely sure no data loss is occurring
               // delete compressed links from the attribute, artifact and relation table.
            }

            try {
               if (artifactTempTable != null) artifactTempTable.drop();
               if (attributeTempTable != null) attributeTempTable.drop();
               if (relationTempTable != null) relationTempTable.drop();
            } catch (SQLException ex) {
               logger.log(Level.WARNING, ex.toString(), ex);
            }
         }
      }
   }
}