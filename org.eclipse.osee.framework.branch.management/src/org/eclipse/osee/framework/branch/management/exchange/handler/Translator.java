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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.branch.management.exchange.ExchangeDb;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.resource.management.Options;

public class Translator {
   private static final String INSERT_INTO_IMPORT_MAP =
         "INSERT INTO osee_import_map (import_id, sequence_id, sequence_name) VALUES (?, ?, ?)";

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
      translators.add(new TranslatedIdMap(SequenceManager.GAMMA_ID_SEQ, ExchangeDb.GAMMA_ID));
      translators.add(new TranslatedIdMap(SequenceManager.TRANSACTION_ID_SEQ, ExchangeDb.TRANSACTION_ID));
      translators.add(new TranslatedIdMap(SequenceManager.ART_ID_SEQ, ExchangeDb.ARTIFACT_ID_ALIASES));
      translators.add(new TranslatedIdMap(SequenceManager.ATTR_ID_SEQ, ExchangeDb.ATTRIBUTE_ID));
      translators.add(new TranslatedIdMap(SequenceManager.REL_LINK_ID_SEQ, ExchangeDb.RELATION_ID));
      translators.add(new TranslatedIdMap(SequenceManager.BRANCH_ID_SEQ, ExchangeDb.BRANCH_ID_ALIASES));
      translators.add(new TranslatedIdMap(SequenceManager.ART_TYPE_ID_SEQ, ExchangeDb.ARTIFACT_TYPE_ID));
      translators.add(new TranslatedIdMap(SequenceManager.ATTR_TYPE_ID_SEQ, ExchangeDb.ATTRIBUTE_TYPE_ID));
      translators.add(new TranslatedIdMap(SequenceManager.REL_LINK_TYPE_ID_SEQ, ExchangeDb.RELATION_TYPE_ID));
      return translators;
   }

   public void loadTranslators(String sourceDatabaseId) throws OseeDataStoreException {
      OseeConnection connection = null;
      try {
         connection = OseeDbConnection.getConnection();
         for (TranslatedIdMap translator : translators) {
            translator.load(connection, sourceDatabaseId);
         }
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
   }

   public List<String> getSequenceNames() {
      List<String> toReturn = new ArrayList<String>();
      for (TranslatedIdMap translatedIdMap : translators) {
         toReturn.add(translatedIdMap.getSequence());
      }
      return toReturn;
   }

   public void store(Connection connection, int importIdIndex) throws OseeDataStoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      for (TranslatedIdMap translatedIdMap : translators) {
         if (translatedIdMap.hasItemsToStore()) {
            int importSeqId = SequenceManager.getNextImportMappedIndexId();
            data.add(new Object[] {importIdIndex, importSeqId, translatedIdMap.getSequence()});
            translatedIdMap.store(connection, importSeqId);
         }
      }
      ConnectionHandler.runPreparedUpdate(connection, INSERT_INTO_IMPORT_MAP, data);
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
