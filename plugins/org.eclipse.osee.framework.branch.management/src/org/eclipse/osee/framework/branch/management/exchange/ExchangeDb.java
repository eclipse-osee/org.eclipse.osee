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
package org.eclipse.osee.framework.branch.management.exchange;

import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_ARTIFACT_ACL_DATA;
import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_ARTIFACT_DATA;
import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_ATTRIBUTE_DATA;
import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_BRANCH_ACL_DATA;
import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_BRANCH_DATA;
import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_CONFLICT_DATA;
import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_MERGE_DATA;
import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_RELATION_LINK_DATA;
import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_TXS_ARCHIVED_DATA;
import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_TXS_DATA;
import static org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem.OSEE_TX_DETAILS_DATA;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.branch.management.exchange.export.AbstractExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.DbTableExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.ManifestExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.MetadataExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.OseeTypeModelExportItem;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeSequence;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class ExchangeDb {

   public static final String GAMMA_ID = "gamma_id";
   public static final String TRANSACTION_ID = "transaction_id";
   public static final String ATTRIBUTE_ID = "attr_id";
   public static final String ARTIFACT_ID = "art_id";
   public static final String RELATION_ID = "rel_link_id";
   public static final String BRANCH_ID = "branch_id";
   public static final String CONFLICT_ID = "conflict_id";
   public static final String CONFLICT_TYPE = "conflict_type";
   public static final String TYPE_GUID = "type_guid";

   private static final String[] BRANCH_ID_NEG_ONE_ALIASES = new String[] {"parent_branch_id"};

   private static final String[] BRANCH_ID_REG_ALIASES = new String[] {
      "mapped_branch_id",
      "source_branch_id",
      "merge_branch_id",
      "dest_branch_id"};

   private static final String[] ARTIFACT_ID_NEG_ONE_ALIASES = new String[] {
      "commit_art_id",
      "associated_art_id",
      "author"};

   private static final String[] ARTIFACT_ID_REG_ALIASES = new String[] {"a_art_id", "b_art_id", "privilege_entity_id"};

   private static final String[] GAMMA_ID_REG_ALIASES = new String[] {"source_gamma_id", "dest_gamma_id"};

   private static final String[] TRANSACTION_ID_REG_ALIASES = new String[] {
      "baseline_transaction_id",
      "parent_transaction_id"};

   private static final String[] TRANSACTION_ID_NEG_ONE_ALIASES = new String[] {"commit_transaction_id"};

   private static final String[] ARTIFACT_ID_ALIASES;
   private static final String[] BRANCH_ID_ALIASES;
   private static final String[] GAMMA_ID_ALIASES;
   private static final String[] TRANSACTION_ID_ALIASES;
   static {
      Set<String> artIdAliases = new HashSet<String>();
      artIdAliases.add(ARTIFACT_ID);
      artIdAliases.addAll(Arrays.asList(ARTIFACT_ID_REG_ALIASES));
      artIdAliases.addAll(Arrays.asList(ARTIFACT_ID_NEG_ONE_ALIASES));
      ARTIFACT_ID_ALIASES = artIdAliases.toArray(new String[artIdAliases.size()]);

      Set<String> branchIdAliases = new HashSet<String>();
      branchIdAliases.add(BRANCH_ID);
      branchIdAliases.addAll(Arrays.asList(BRANCH_ID_REG_ALIASES));
      branchIdAliases.addAll(Arrays.asList(BRANCH_ID_NEG_ONE_ALIASES));
      BRANCH_ID_ALIASES = branchIdAliases.toArray(new String[branchIdAliases.size()]);

      Set<String> gammaIdAliases = new HashSet<String>();
      gammaIdAliases.add(GAMMA_ID);
      gammaIdAliases.addAll(Arrays.asList(GAMMA_ID_REG_ALIASES));
      GAMMA_ID_ALIASES = gammaIdAliases.toArray(new String[gammaIdAliases.size()]);

      Set<String> txIdAliases = new HashSet<String>();
      txIdAliases.add(TRANSACTION_ID);
      txIdAliases.addAll(Arrays.asList(TRANSACTION_ID_REG_ALIASES));
      txIdAliases.addAll(Arrays.asList(TRANSACTION_ID_NEG_ONE_ALIASES));
      TRANSACTION_ID_ALIASES = txIdAliases.toArray(new String[txIdAliases.size()]);
   }

   public static final String GET_MAX_TX =
      "SELECT last_sequence FROM osee_sequence WHERE sequence_name = '" + IOseeSequence.TRANSACTION_ID_SEQ + "'";

   private static final String BRANCH_TABLE_QUERY =
      "SELECT br.* FROM osee_join_export_import jex, osee_branch br WHERE jex.query_id=? AND jex.id1=br.branch_id ORDER BY br.branch_id";

   private static final String TX_DETAILS_TABLE_QUERY =
      "SELECT txd.* FROM osee_join_export_import jex, osee_tx_details txd WHERE jex.query_id=? AND jex.id1=txd.branch_id";

   private static final String TXS_TABLE_QUERY =
      "SELECT txs.* FROM osee_join_export_import jex, osee_txs txs WHERE jex.query_id=? AND jex.id1=txs.branch_id";

   private static final String TXS_ARCHIVE_TABLE_QUERY = TXS_TABLE_QUERY.replace("osee_txs", "osee_txs_archived");

   private static final String ARTIFACT_TABLE_QUERY =
      "SELECT item.* FROM osee_join_id oji, osee_artifact item WHERE oji.query_id = ? AND oji.id = item.gamma_id";

   private static final String ATTRIBUTE_TABLE_QUERY = ARTIFACT_TABLE_QUERY.replace("osee_artifact", "osee_attribute");

   private static final String RELATION_LINK_TABLE_QUERY = ARTIFACT_TABLE_QUERY.replace("osee_artifact",
      "osee_relation_link");

   private static final String MERGE_TABLE_QUERY =
      "SELECT om.* FROM osee_join_export_import jex, osee_merge om WHERE jex.query_id=? AND jex.id1=om.merge_branch_id ORDER BY om.merge_branch_id";

   private static final String CONFLICT_TABLE_QUERY =
      "SELECT oc.* FROM osee_join_export_import jex, osee_merge om, osee_conflict oc WHERE jex.query_id=? AND jex.id1=om.merge_branch_id AND om.merge_branch_id=oc.merge_branch_id";

   private static final String ARTIFACT_ACL_QUERY =
      "SELECT aac.* FROM osee_join_export_import jex, osee_artifact_acl aac WHERE jex.query_id=? AND jex.id1=aac.branch_id ORDER BY aac.branch_id";

   private static final String BRANCH_ACL_QUERY =
      "SELECT bac.* FROM osee_join_export_import jex, osee_branch_acl bac WHERE jex.query_id=? AND jex.id1=bac.branch_id ORDER BY bac.branch_id";

   private final List<AbstractExportItem> items = new ArrayList<AbstractExportItem>();
   private final OseeServices services;
   private final PropertyStore options;
   private final int exportJoinId;
   private int gammaJoinId;

   public ExchangeDb(OseeServices services, PropertyStore options, int exportJoinId) {
      this.services = services;
      this.options = options;
      this.exportJoinId = exportJoinId;
   }

   private DatabaseMetaData getMetaData() throws OseeCoreException {
      OseeConnection connection = services.getDatabaseService().getConnection();
      try {
         return connection.getMetaData();
      } finally {
         connection.close();
      }
   }

   List<AbstractExportItem> createTaskList() throws OseeCoreException {
      this.gammaJoinId = setupGammaJoin();

      items.add(new ManifestExportItem(items, options));
      items.add(new MetadataExportItem(items, getMetaData()));
      items.add(new OseeTypeModelExportItem(services.getModelingService()));
      addExportItem(OSEE_BRANCH_DATA, BRANCH_TABLE_QUERY);
      addExportItem(OSEE_TX_DETAILS_DATA, TX_DETAILS_TABLE_QUERY);
      addExportItem(OSEE_TXS_DATA, TXS_TABLE_QUERY);
      addExportItem(OSEE_TXS_ARCHIVED_DATA, TXS_ARCHIVE_TABLE_QUERY);
      addExportItem(OSEE_ARTIFACT_DATA, ARTIFACT_TABLE_QUERY);
      addExportItem(OSEE_ATTRIBUTE_DATA, ATTRIBUTE_TABLE_QUERY);
      addExportItem(OSEE_RELATION_LINK_DATA, RELATION_LINK_TABLE_QUERY);
      addExportItem(OSEE_MERGE_DATA, MERGE_TABLE_QUERY);
      addExportItem(OSEE_CONFLICT_DATA, CONFLICT_TABLE_QUERY);
      addExportItem(OSEE_BRANCH_ACL_DATA, BRANCH_ACL_QUERY);
      addExportItem(OSEE_ARTIFACT_ACL_DATA, ARTIFACT_ACL_QUERY);
      return items;
   }

   private void addExportItem(ExportItem exportItem, String query) throws OseeCoreException {
      StringBuilder modifiedQuery = new StringBuilder(query);
      Object[] bindData = prepareQuery(exportItem, modifiedQuery, options, exportJoinId, gammaJoinId);
      items.add(new DbTableExportItem(services, exportItem, modifiedQuery.toString(), bindData));
   }

   static List<IndexCollector> createCheckList() {
      List<IndexCollector> items = new ArrayList<IndexCollector>();
      items.add(new IndexCollector(ExportItem.OSEE_TXS_DATA, GAMMA_ID, GAMMA_ID_REG_ALIASES));
      items.add(new IndexCollector(ExportItem.OSEE_TXS_ARCHIVED_DATA, GAMMA_ID, GAMMA_ID_REG_ALIASES));
      items.add(new IndexCollector(ExportItem.OSEE_TX_DETAILS_DATA, TRANSACTION_ID, TRANSACTION_ID_REG_ALIASES,
         TRANSACTION_ID_NEG_ONE_ALIASES));
      items.add(new IndexCollector(ExportItem.OSEE_ARTIFACT_DATA, ARTIFACT_ID, ARTIFACT_ID_REG_ALIASES,
         ARTIFACT_ID_NEG_ONE_ALIASES));
      items.add(new IndexCollector(ExportItem.OSEE_ATTRIBUTE_DATA, ATTRIBUTE_ID));
      items.add(new IndexCollector(ExportItem.OSEE_RELATION_LINK_DATA, RELATION_ID));
      items.add(new IndexCollector(ExportItem.OSEE_BRANCH_DATA, BRANCH_ID, BRANCH_ID_REG_ALIASES,
         BRANCH_ID_NEG_ONE_ALIASES));
      return items;
   }

   static List<IdTranslator> createTranslators(IOseeDatabaseService service) {
      List<IdTranslator> translators = new ArrayList<IdTranslator>();
      translators.add(new IdTranslator(service, IOseeSequence.GAMMA_ID_SEQ, GAMMA_ID_ALIASES));
      translators.add(new IdTranslator(service, IOseeSequence.TRANSACTION_ID_SEQ, TRANSACTION_ID_ALIASES));
      translators.add(new IdTranslator(service, IOseeSequence.BRANCH_ID_SEQ, BRANCH_ID_ALIASES));
      translators.add(new IdTranslator(service, IOseeSequence.ART_ID_SEQ, ARTIFACT_ID_ALIASES));
      translators.add(new IdTranslator(service, IOseeSequence.ATTR_ID_SEQ, ATTRIBUTE_ID));
      translators.add(new IdTranslator(service, IOseeSequence.REL_LINK_ID_SEQ, RELATION_ID));
      return translators;
   }

   private static void addMaxMinFilter(StringBuilder query, List<Object> bindData, PropertyStore options) throws OseeCoreException {
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

   private int setupGammaJoin() throws OseeCoreException {
      List<Object> bindList = new ArrayList<Object>();
      int gammaJoinId = new Random().nextInt();
      StringBuilder sql =
         new StringBuilder(
            "INSERT INTO osee_join_id (id, query_id) SELECT DISTINCT(gamma_id), %s FROM osee_join_export_import jex, osee_txs txs WHERE jex.query_id=? AND jex.id1 = txs.branch_id");
      bindList.add(exportJoinId);
      addMaxMinFilter(sql, bindList, options);

      sql.append(" UNION SELECT DISTINCT(gamma_id), %s FROM osee_join_export_import jex, osee_txs_archived txs WHERE jex.query_id=? AND jex.id1 = txs.branch_id");
      bindList.add(exportJoinId);
      addMaxMinFilter(sql, bindList, options);

      IOseeDatabaseService databaseService = services.getDatabaseService();
      Object[] bindData = bindList.toArray(new Object[bindList.size()]);

      String insert = String.format(sql.toString(), gammaJoinId, gammaJoinId);
      System.out.println(databaseService.runPreparedUpdate(insert, bindData));
      return gammaJoinId;
   }

   public static Object[] prepareQuery(ExportItem exportItem, StringBuilder query, PropertyStore options, int exportJoinId, int gammaJionId) throws OseeCoreException {
      List<Object> bindData = new ArrayList<Object>();

      if (exportItem.matches(OSEE_ARTIFACT_DATA, OSEE_ATTRIBUTE_DATA, OSEE_RELATION_LINK_DATA)) {
         bindData.add(gammaJionId);
      } else {
         bindData.add(exportJoinId);
      }

      if (exportItem.matches(OSEE_TX_DETAILS_DATA, OSEE_TXS_DATA, OSEE_TXS_ARCHIVED_DATA)) {
         // this can not be accurately applied to osee_merge and osee_conflict because the best you can do is filter a the merge_branch level
         addMaxMinFilter(query, bindData, options);
      }

      if (exportItem.matches(OSEE_TX_DETAILS_DATA)) {
         // tx_details needs to be ordered so transactions are sequenced properly
         query.append(" ORDER BY transaction_id ASC");
      }
      return bindData.toArray(new Object[bindData.size()]);
   }

   static Long getMaxTransaction(PropertyStore options) {
      return getTransactionNumber(options, ExportOptions.MAX_TXS.name());
   }

   static Long getMinTransaction(PropertyStore options) {
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