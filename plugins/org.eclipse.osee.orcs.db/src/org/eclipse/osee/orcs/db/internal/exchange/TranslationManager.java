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
package org.eclipse.osee.orcs.db.internal.exchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.orcs.ImportOptions;

public class TranslationManager {
   private static final String IMPORT_MAPPED_INDEX_SEQ = "SKYNET_IMPORT_MAPPED_INDEX_SEQ";

   private static final String INSERT_INTO_IMPORT_MAP =
      "INSERT INTO osee_import_map (import_id, sequence_id, sequence_name) VALUES (?, ?, ?)";

   private final List<IdTranslator> translators;
   private final Map<String, IdTranslator> translatorMap;
   private final JdbcClient jdbcClient;

   private boolean useOriginalIds;

   public TranslationManager(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
      this.useOriginalIds = true;
      this.translators = ExchangeDb.createTranslators(jdbcClient);
      this.translatorMap = new HashMap<>();
      for (IdTranslator translator : translators) {
         for (String alias : translator.getAliases()) {
            translatorMap.put(alias, translator);
         }
      }
   }

   public void configure(PropertyStore options) {
      if (options != null) {
         useOriginalIds = options.getBoolean(ImportOptions.USE_IDS_FROM_IMPORT_FILE.name());
      }
   }

   public void loadTranslators(String sourceDatabaseId) {
      for (IdTranslator translator : translators) {
         translator.load(sourceDatabaseId);
      }
   }

   public List<String> getSequenceNames() {
      List<String> toReturn = new ArrayList<>();
      for (IdTranslator translatedIdMap : translators) {
         toReturn.add(translatedIdMap.getSequenceName());
      }
      return toReturn;
   }

   public void store(JdbcConnection connection, int importIdIndex) {
      List<Object[]> data = new ArrayList<>();
      for (IdTranslator translatedIdMap : translators) {
         if (translatedIdMap.hasItemsToStore()) {
            int importSeqId = (int) jdbcClient.getNextSequence(IMPORT_MAPPED_INDEX_SEQ, true);
            data.add(new Object[] {importIdIndex, importSeqId, translatedIdMap.getSequenceName()});
            translatedIdMap.store(connection, importSeqId);
         }
      }
      jdbcClient.runBatchUpdate(connection, INSERT_INTO_IMPORT_MAP, data);
   }

   public boolean isTranslatable(String name) {
      return translatorMap.containsKey(name.toLowerCase());
   }

   public Object translate(String name, Object original) {
      Object toReturn = original;
      if (original != null && !useOriginalIds) {
         IdTranslator translator = translatorMap.get(name.toLowerCase());
         if (translator != null) {
            toReturn = translator.getId(original);
         }
      }
      return toReturn;
   }

   public void checkIdMapping(String name, Long original, Long newValue) {
      IdTranslator translator = translatorMap.get(name.toLowerCase());
      if (translator != null) {
         Long data = translator.getFromCache(original);
         if (data == null || !data.equals(newValue)) {
            translator.addToCache(original, newValue);
         }
      }
   }
}
