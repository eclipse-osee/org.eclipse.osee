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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

/**
 * @author Roberto E. Escobar
 */
public final class ExchangeDb {

   private ExchangeDb() {
      // Utility class;
   }

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

   private static final String[] BRANCH_ID_REG_ALIASES =
      new String[] {"mapped_branch_id", "source_branch_id", "merge_branch_id", "dest_branch_id"};

   private static final String[] ARTIFACT_ID_NEG_ONE_ALIASES =
      new String[] {"commit_art_id", "associated_art_id", "author"};

   private static final String[] ARTIFACT_ID_REG_ALIASES = new String[] {"a_art_id", "b_art_id", "privilege_entity_id"};

   private static final String[] GAMMA_ID_REG_ALIASES = new String[] {"source_gamma_id", "dest_gamma_id"};

   private static final String[] TRANSACTION_ID_REG_ALIASES =
      new String[] {"baseline_transaction_id", "parent_transaction_id"};

   private static final String[] TRANSACTION_ID_NEG_ONE_ALIASES = new String[] {"commit_transaction_id"};

   private static final String[] ARTIFACT_ID_ALIASES;
   private static final String[] BRANCH_ID_ALIASES;
   private static final String[] GAMMA_ID_ALIASES;
   private static final String[] TRANSACTION_ID_ALIASES;

   static {
      Set<String> artIdAliases = new HashSet<>();
      artIdAliases.add(ARTIFACT_ID);
      artIdAliases.addAll(Arrays.asList(ARTIFACT_ID_REG_ALIASES));
      artIdAliases.addAll(Arrays.asList(ARTIFACT_ID_NEG_ONE_ALIASES));
      ARTIFACT_ID_ALIASES = artIdAliases.toArray(new String[artIdAliases.size()]);

      Set<String> branchUuidAliases = new HashSet<>();
      branchUuidAliases.add(BRANCH_ID);
      branchUuidAliases.addAll(Arrays.asList(BRANCH_ID_REG_ALIASES));
      branchUuidAliases.addAll(Arrays.asList(BRANCH_ID_NEG_ONE_ALIASES));
      BRANCH_ID_ALIASES = branchUuidAliases.toArray(new String[branchUuidAliases.size()]);

      Set<String> gammaIdAliases = new HashSet<>();
      gammaIdAliases.add(GAMMA_ID);
      gammaIdAliases.addAll(Arrays.asList(GAMMA_ID_REG_ALIASES));
      GAMMA_ID_ALIASES = gammaIdAliases.toArray(new String[gammaIdAliases.size()]);

      Set<String> txIdAliases = new HashSet<>();
      txIdAliases.add(TRANSACTION_ID);
      txIdAliases.addAll(Arrays.asList(TRANSACTION_ID_REG_ALIASES));
      txIdAliases.addAll(Arrays.asList(TRANSACTION_ID_NEG_ONE_ALIASES));
      TRANSACTION_ID_ALIASES = txIdAliases.toArray(new String[txIdAliases.size()]);
   }

   static List<IndexCollector> createCheckList() {
      List<IndexCollector> items = new ArrayList<>();
      items.add(new IndexCollector(ExportItem.OSEE_TXS_DATA, GAMMA_ID, GAMMA_ID_REG_ALIASES));
      items.add(new IndexCollector(ExportItem.OSEE_TXS_ARCHIVED_DATA, GAMMA_ID, GAMMA_ID_REG_ALIASES));
      items.add(new IndexCollector(ExportItem.OSEE_TX_DETAILS_DATA, TRANSACTION_ID, TRANSACTION_ID_REG_ALIASES,
         TRANSACTION_ID_NEG_ONE_ALIASES));
      items.add(new IndexCollector(ExportItem.OSEE_ARTIFACT_DATA, ARTIFACT_ID, ARTIFACT_ID_REG_ALIASES,
         ARTIFACT_ID_NEG_ONE_ALIASES));
      items.add(new IndexCollector(ExportItem.OSEE_ATTRIBUTE_DATA, ATTRIBUTE_ID));
      items.add(new IndexCollector(ExportItem.OSEE_RELATION_LINK_DATA, RELATION_ID));
      items.add(
         new IndexCollector(ExportItem.OSEE_BRANCH_DATA, BRANCH_ID, BRANCH_ID_REG_ALIASES, BRANCH_ID_NEG_ONE_ALIASES));
      return items;
   }

   static List<IdTranslator> createTranslators(JdbcClient service) {
      List<IdTranslator> translators = new ArrayList<>();
      translators.add(new IdTranslator(service, OseeData.GAMMA_ID_SEQ, GAMMA_ID_ALIASES));
      translators.add(new IdTranslator(service, OseeData.TRANSACTION_ID_SEQ, TRANSACTION_ID_ALIASES));
      translators.add(new IdTranslator(service, OseeData.BRANCH_ID_SEQ, BRANCH_ID_ALIASES));
      translators.add(new IdTranslator(service, OseeData.ART_ID_SEQ, ARTIFACT_ID_ALIASES));
      translators.add(new IdTranslator(service, OseeData.ATTR_ID_SEQ, ATTRIBUTE_ID));
      translators.add(new IdTranslator(service, OseeData.REL_LINK_ID_SEQ, RELATION_ID));
      return translators;
   }
}