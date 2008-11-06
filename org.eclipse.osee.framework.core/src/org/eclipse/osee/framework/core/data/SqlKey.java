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
package org.eclipse.osee.framework.core.data;

import java.util.Properties;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public class SqlKey {
   private static Boolean areHintsAllowed = null;

   public static final String SELECT_HISTORICAL_ARTIFACTS = "SELECT_HISTORICAL_ARTIFACTS";
   public static final String SELECT_HISTORICAL_ATTRIBUTES = "SELECT_HISTORICAL_ATTRIBUTES";

   public static final String SELECT_HISTORICAL_ARTIFACTS_DEFINITION =
         "SELECT%s al1.art_id, txs1.gamma_id, mod_type, txd1.*, art_type_id, guid, human_readable_id, al1.transaction_id as stripe_transaction_id FROM osee_join_artifact al1, osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= al1.transaction_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by al1.branch_id, art1.art_id, txs1.transaction_id desc";

   public static final String SELECT_HISTORICAL_ATTRIBUTES_DEFINITION =
         "SELECT%s att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.branch_id, txs1.mod_type, txd1.transaction_id, al1.transaction_id as stripe_transaction_id FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= al1.transaction_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by txd1.branch_id, att1.art_id, att1.attr_id, txd1.transaction_id desc";

   public static final String ORDERED_HINT = " /*+ ordered */";

   private SqlKey() {
   }

   public static Properties getSqlProperties() throws OseeDataStoreException {
      Properties sqlProperties = new Properties();
      sqlProperties.put(SqlKey.SELECT_HISTORICAL_ARTIFACTS,
            getFormattedSql(SqlKey.SELECT_HISTORICAL_ARTIFACTS_DEFINITION));
      sqlProperties.put(SqlKey.SELECT_HISTORICAL_ATTRIBUTES,
            getFormattedSql(SqlKey.SELECT_HISTORICAL_ATTRIBUTES_DEFINITION));
      return sqlProperties;
   }

   private static String getFormattedSql(String sql) throws OseeDataStoreException {
      if (areHintsAllowed == null) {
         areHintsAllowed = ConnectionHandler.areHintsSupported();
      }
      return String.format(sql, areHintsAllowed ? SqlKey.ORDERED_HINT : "");
   }
}
