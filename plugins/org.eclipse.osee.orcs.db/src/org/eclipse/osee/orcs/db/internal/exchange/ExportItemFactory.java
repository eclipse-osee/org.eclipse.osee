/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ExportOptions;
import org.eclipse.osee.orcs.SystemProperties;
import org.eclipse.osee.orcs.db.internal.exchange.export.AbstractExportItem;
import org.eclipse.osee.orcs.db.internal.exchange.export.DbTableSqlExportItem;
import org.eclipse.osee.orcs.db.internal.exchange.export.DbTableXmlExportItem;
import org.eclipse.osee.orcs.db.internal.exchange.export.ManifestExportItem;
import org.eclipse.osee.orcs.db.internal.exchange.export.MetadataExportItem;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

public class ExportItemFactory {
   private static final String GET_MAX_TX =
      "SELECT last_sequence FROM osee_sequence WHERE sequence_name = '" + OseeData.TRANSACTION_ID_SEQ + "'";

   private static final String BRANCH_TABLE_QUERY =
      "SELECT br.* FROM osee_join_id, osee_branch br WHERE query_id=? AND id=br.branch_id ORDER BY br.branch_id";

   private static final String TX_DETAILS_TABLE_QUERY =
      "SELECT txd.* FROM osee_join_id, osee_tx_details txd WHERE query_id=? AND id=txd.branch_id";

   private static final String TXS_TABLE_QUERY =
      "SELECT txs.* FROM osee_join_id, osee_txs txs WHERE query_id=? AND id=txs.branch_id";

   private static final String TXS_ARCHIVE_TABLE_QUERY = TXS_TABLE_QUERY.replace("osee_txs", "osee_txs_archived");

   private static final String ARTIFACT_TABLE_QUERY =
      "SELECT item.* FROM osee_join_id, osee_artifact item WHERE query_id = ? AND id = item.gamma_id";

   private static final String ATTRIBUTE_TABLE_QUERY = ARTIFACT_TABLE_QUERY.replace("osee_artifact", "osee_attribute");

   private static final String RELATION_LINK_TABLE_QUERY =
      ARTIFACT_TABLE_QUERY.replace("osee_artifact", "osee_relation_link");

   private static final String MERGE_TABLE_QUERY =
      "SELECT om.* FROM osee_join_id, osee_merge om WHERE query_id=? AND id=om.merge_branch_id ORDER BY om.merge_branch_id";

   private static final String CONFLICT_TABLE_QUERY =
      "SELECT oc.* FROM osee_join_id, osee_merge om, osee_conflict oc WHERE query_id=? AND id=om.merge_branch_id AND om.merge_branch_id=oc.merge_branch_id";

   private static final String ARTIFACT_ACL_QUERY =
      "SELECT aac.* FROM osee_join_id, osee_artifact_acl aac WHERE query_id=? AND id=aac.branch_id ORDER BY aac.branch_id";

   private static final String BRANCH_ACL_QUERY =
      "SELECT bac.* FROM osee_join_id, osee_branch_acl bac WHERE query_id=? AND id=bac.branch_id ORDER BY bac.branch_id";

   private final Log logger;
   private final SystemProperties preferences;
   private final JdbcClient jdbcClient;
   private final IResourceManager resourceManager;

   public ExportItemFactory(Log logger, SystemProperties preferences, JdbcClient jdbcClient, IResourceManager resourceManager) {
      this.logger = logger;
      this.preferences = preferences;
      this.jdbcClient = jdbcClient;
      this.resourceManager = resourceManager;
   }

   public Log getLogger() {
      return logger;
   }

   public JdbcClient getDbService() {
      return jdbcClient;
   }

   public IResourceManager getResourceManager() {
      return resourceManager;
   }

   public List<AbstractExportItem> createTaskList(Long branchJoinId, PropertyStore options) {
      List<AbstractExportItem> items = new ArrayList<>();

      processTxOptions(options);
      // perform sql export for now
      options.put("sql_export", true);

      int gammaJoinId = createGammaJoin(getDbService(), branchJoinId, options);

      items.add(new ManifestExportItem(logger, preferences, items, options));
      items.add(new MetadataExportItem(logger, items, getDbService()));

      if (options.getBoolean("sql_export")) {
         addSqlItem(items, branchJoinId, options, gammaJoinId, ExportItem.OSEE_BRANCH_DATA, BRANCH_TABLE_QUERY);
         addSqlItem(items, branchJoinId, options, gammaJoinId, ExportItem.OSEE_TX_DETAILS_DATA, TX_DETAILS_TABLE_QUERY);
         addSqlItem(items, branchJoinId, options, gammaJoinId, ExportItem.OSEE_TXS_DATA, TXS_TABLE_QUERY);
         addSqlItem(items, branchJoinId, options, gammaJoinId, ExportItem.OSEE_ARTIFACT_DATA, ARTIFACT_TABLE_QUERY);
         addSqlItem(items, branchJoinId, options, gammaJoinId, ExportItem.OSEE_ATTRIBUTE_DATA, ATTRIBUTE_TABLE_QUERY);
         addSqlItem(items, branchJoinId, options, gammaJoinId, ExportItem.OSEE_RELATION_LINK_DATA,
            RELATION_LINK_TABLE_QUERY);
      }
      return items;
   }

   private void addItem(List<AbstractExportItem> items, Long branchJoinId, PropertyStore options, int gammaJoinId,
      ExportItem exportItem, String query) {
      StringBuilder modifiedQuery = new StringBuilder(query);
      Object[] bindData = prepareQuery(exportItem, modifiedQuery, options, branchJoinId, gammaJoinId);
      items.add(new DbTableXmlExportItem(getLogger(), getDbService(), getResourceManager(), exportItem,
         modifiedQuery.toString(), bindData));
   }

   private void addSqlItem(List<AbstractExportItem> items, Long branchJoinId, PropertyStore options, int gammaJoinId,
      ExportItem exportItem, String query) {
      StringBuilder modifiedQuery = new StringBuilder(query);
      Object[] bindData = prepareQuery(exportItem, modifiedQuery, options, branchJoinId, gammaJoinId);
      items.add(new DbTableSqlExportItem(getLogger(), getDbService(), getResourceManager(), exportItem,
         modifiedQuery.toString(), bindData));
   }

   private void processTxOptions(PropertyStore options) {
      long maxTx = getDbService().fetch(-1L, GET_MAX_TX);
      long userMaxTx = getMaxTransaction(options);
      if (userMaxTx == Long.MIN_VALUE || userMaxTx > maxTx) {
         options.put(ExportOptions.MAX_TXS.name(), Long.toString(maxTx));
      }
   }

   @SuppressWarnings("java:S2245") //This random doesn't need to be truly random as it is not sensitive
   private int createGammaJoin(JdbcClient jdbcClient, Long branchJoinId, PropertyStore options) {
      List<Object> bindList = new ArrayList<>();
      Random rand = new Random();
      int gammaJoinId = rand.nextInt();
      StringBuilder sql = new StringBuilder(
         "INSERT INTO osee_join_id (id, query_id) SELECT DISTINCT(gamma_id), %s FROM osee_join_id, osee_txs txs WHERE query_id=? AND id = txs.branch_id");
      bindList.add(branchJoinId);
      addMaxMinFilter(sql, bindList, options);

      sql.append(
         " UNION SELECT DISTINCT(gamma_id), %s FROM osee_join_id, osee_txs_archived txs WHERE query_id = ? AND id = txs.branch_id");
      bindList.add(branchJoinId);
      addMaxMinFilter(sql, bindList, options);

      Object[] bindData = bindList.toArray(new Object[bindList.size()]);
      String insert = String.format(sql.toString(), gammaJoinId, gammaJoinId);
      int itemsInserted = jdbcClient.runPreparedUpdate(insert, bindData);

      getLogger().info("Export join rows: [%s]", itemsInserted);

      return gammaJoinId;
   }

   private static Object[] prepareQuery(ExportItem exportItem, StringBuilder query, PropertyStore options,
      Long branchJoinId, int gammaJionId) {
      List<Object> bindData = new ArrayList<>();

      if (exportItem.matches(ExportItem.OSEE_ARTIFACT_DATA, ExportItem.OSEE_ATTRIBUTE_DATA,
         ExportItem.OSEE_RELATION_LINK_DATA)) {
         bindData.add(gammaJionId);
      } else {
         bindData.add(branchJoinId);
      }

      if (exportItem.matches(ExportItem.OSEE_TX_DETAILS_DATA, ExportItem.OSEE_TXS_DATA,
         ExportItem.OSEE_TXS_ARCHIVED_DATA)) {
         // this can not be accurately applied to osee_merge and osee_conflict because the best you can do is filter a the merge_branch level
         addMaxMinFilter(query, bindData, options);
      }

      if (exportItem.matches(ExportItem.OSEE_TX_DETAILS_DATA)) {
         // tx_details needs to be ordered so transactions are sequenced properly
         query.append(" ORDER BY transaction_id ASC");
      }
      return bindData.toArray(new Object[bindData.size()]);
   }

   private static void addMaxMinFilter(StringBuilder query, List<Object> bindData, PropertyStore options) {
      long minTxs = getMinTransaction(options);
      long maxTxs = getMaxTransaction(options);

      if (minTxs > maxTxs) {
         throw new OseeArgumentException("Invalid transaction range: min - %d >  max - %d", minTxs, maxTxs);
      }

      if (minTxs != Long.MIN_VALUE) {
         query.append(" AND transaction_id >= ?");
         bindData.add(minTxs);
      }
      if (maxTxs != Long.MIN_VALUE) {
         query.append(" AND transaction_id <= ?");
         bindData.add(maxTxs);
      }
   }

   private static Long getMaxTransaction(PropertyStore options) {
      return getTransactionNumber(options, ExportOptions.MAX_TXS.name());
   }

   private static Long getMinTransaction(PropertyStore options) {
      return getTransactionNumber(options, ExportOptions.MIN_TXS.name());
   }

   private static Long getTransactionNumber(PropertyStore options, String exportOption) {
      String transactionNumber = options.get(exportOption);
      long toReturn = Long.MIN_VALUE;
      if (Strings.isValid(transactionNumber)) {
         toReturn = Long.valueOf(transactionNumber);
      }
      return toReturn;
   }
}
