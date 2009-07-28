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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.resource.management.Options;

public class TranslationManager {
   private static final String INSERT_INTO_IMPORT_MAP =
         "INSERT INTO osee_import_map (import_id, sequence_id, sequence_name) VALUES (?, ?, ?)";

   private final List<IdTranslator> translators;
   private final Map<String, IdTranslator> translatorMap;

   private boolean useOriginalIds;

   public TranslationManager() {
      this.useOriginalIds = true;
      this.translators = ExchangeDb.createTranslators();
      this.translatorMap = new HashMap<String, IdTranslator>();
      for (IdTranslator translator : translators) {
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

   public void loadTranslators(String sourceDatabaseId) throws OseeDataStoreException {
      for (IdTranslator translator : translators) {
         translator.load(sourceDatabaseId);
      }
   }

   public List<String> getSequenceNames() {
      List<String> toReturn = new ArrayList<String>();
      for (IdTranslator translatedIdMap : translators) {
         toReturn.add(translatedIdMap.getSequence());
      }
      return toReturn;
   }

   public void store(OseeConnection connection, int importIdIndex) throws OseeDataStoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      for (IdTranslator translatedIdMap : translators) {
         if (translatedIdMap.hasItemsToStore()) {
            int importSeqId = SequenceManager.getNextImportMappedIndexId();
            data.add(new Object[] {importIdIndex, importSeqId, translatedIdMap.getSequence()});
            translatedIdMap.store(connection, importSeqId);
         }
      }
      ConnectionHandler.runBatchUpdate(connection, INSERT_INTO_IMPORT_MAP, data);
   }

   public boolean isTranslatable(String name) {
      return translatorMap.containsKey(name.toLowerCase());
   }

   public Object translate(String name, Object original) throws OseeDataStoreException {
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
         if (data == null || data != newValue) {
            translator.addToCache(original, newValue);
         }
      }
   }
}
