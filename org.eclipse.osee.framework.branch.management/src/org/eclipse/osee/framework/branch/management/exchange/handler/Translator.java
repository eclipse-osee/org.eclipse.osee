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
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.resource.management.Options;

public class Translator {
   private static final String INSERT_INTO_IMPORT_MAP =
         "INSERT INTO osee_import_map (import_id, sequence_name, db_source_guid, insert_time) VALUES (?, ?, ?, ?)";

   private static final String[] ARTIFACT_ID_ALIASES =
         new String[] {"art_id", "associated_art_id", "a_order", "b_order", "a_order_value", "b_order_value",
               "a_art_id", "b_art_id", "commit_art_id", "author"};

   private static final String[] BRANCH_ID_ALIASES =
         new String[] {"branch_id", "parent_branch_id", "a_branch_id", "b_branch_id", "mapped_branch_id"};

   private final List<TranslatedIdMap> translators;
   private final Map<String, TranslatedIdMap> translatorMap;
   private boolean useOriginalIds;

   public Translator() {
      this.useOriginalIds = true;
      this.translators = createTranslators();
      this.translatorMap = new HashMap<String, TranslatedIdMap>();
      for (TranslatedIdMap translator : translators) {
         for (String alias : translator.getAliases()) {
            translatorMap.put(alias, translator);
         }
      }
   }

   public void configure(Options options) {
      if (options != null) {
         useOriginalIds = options.getBoolean(ImportOptions.USE_IDS_FROM_IMPORT_FILE.name());
      }
   }

   private List<TranslatedIdMap> createTranslators() {
      List<TranslatedIdMap> translators = new ArrayList<TranslatedIdMap>();
      translators.add(new TranslatedIdMap(SequenceManager.GAMMA_ID_SEQ, "gamma_id"));
      translators.add(new TranslatedIdMap(SequenceManager.TRANSACTION_ID_SEQ, "transaction_id"));
      translators.add(new TranslatedIdMap(SequenceManager.ART_ID_SEQ, ARTIFACT_ID_ALIASES));
      translators.add(new TranslatedIdMap(SequenceManager.ATTR_ID_SEQ, "attr_id"));
      translators.add(new TranslatedIdMap(SequenceManager.REL_LINK_ID_SEQ, "rel_link_id"));
      translators.add(new TranslatedIdMap(SequenceManager.BRANCH_ID_SEQ, BRANCH_ID_ALIASES));
      translators.add(new TranslatedIdMap(SequenceManager.ART_TYPE_ID_SEQ, "art_type_id"));
      translators.add(new TranslatedIdMap(SequenceManager.ATTR_TYPE_ID_SEQ, "attr_type_id"));
      translators.add(new TranslatedIdMap(SequenceManager.REL_LINK_TYPE_ID_SEQ, "rel_link_type_id"));
      return translators;
   }

   public void loadTranslators(Connection connection, String sourceDatabaseId) throws SQLException {
      for (TranslatedIdMap translator : translators) {
         translator.load(connection, sourceDatabaseId);
      }
   }

   public void storeImport(Connection connection, String sourceDatabaseId, Date sourceExportDate) throws SQLException {
      Timestamp timeStamp = new Timestamp(sourceExportDate.getTime());

      Map<Integer, TranslatedIdMap> importIdIndex = new HashMap<Integer, TranslatedIdMap>();
      List<Object[]> data = new ArrayList<Object[]>();
      for (TranslatedIdMap entry : translators) {
         int importId = SequenceManager.getNextImportId();
         String sequence = entry.getSequence();
         importIdIndex.put(importId, entry);
         data.add(new Object[] {importId, sequence, sourceDatabaseId, timeStamp});
      }
      ConnectionHandler.runPreparedUpdate(connection, INSERT_INTO_IMPORT_MAP, data);

      for (Integer importIndex : importIdIndex.keySet()) {
         TranslatedIdMap translatedIdMap = importIdIndex.get(importIndex);
         translatedIdMap.store(connection, importIndex);
      }
   }

   public boolean isTranslatable(String name) {
      return translatorMap.containsKey(name.toLowerCase());
   }

   public Object translate(String name, Object original) throws Exception {
      Object toReturn = original;
      if (original != null && !useOriginalIds) {
         TranslatedIdMap translator = translatorMap.get(name.toLowerCase());
         if (translator != null) {
            toReturn = translator.getId(original);
         }
      }
      return toReturn;
   }

   public void checkIdMapping(String name, Long original, Long newValue) {
      TranslatedIdMap translator = translatorMap.get(name.toLowerCase());
      if (translator != null) {
         Long data = translator.getFromCache(original);
         if (data == null || data != newValue) {
            translator.addToCache(original, newValue);
         }
      }
   }
}
