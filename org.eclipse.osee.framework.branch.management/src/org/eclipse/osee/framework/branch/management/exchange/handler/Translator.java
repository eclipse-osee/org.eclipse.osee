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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.ExportImportJoinQuery;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.resource.management.Options;

public class Translator {
   private static final String[] ARTIFACT_ID_ALIASES =
         new String[] {"art_id", "associated_art_id", "a_order", "b_order", "a_order_value", "b_order_value",
               "a_art_id", "b_art_id"};

   private static final String[] BRANCH_ID_ALIASES =
         new String[] {"branch_id", "parent_branch_id", "a_branch_id", "b_branch_id"};

   private List<IdTranslator> translators;
   private Map<String, IdTranslator> translatorMap;
   private boolean useOriginalIds;

   public Translator() {
      this.useOriginalIds = true;
      this.translators = getTranslators();
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

   private List<IdTranslator> getTranslators() {
      List<IdTranslator> translators = new ArrayList<IdTranslator>();
      translators.add(new IdTranslator(SkynetDatabase.GAMMA_ID_SEQ, "gamma_id"));
      translators.add(new IdTranslator(SkynetDatabase.ART_ID_SEQ, ARTIFACT_ID_ALIASES));
      translators.add(new IdTranslator(SkynetDatabase.ART_TYPE_ID_SEQ, "art_type_id"));
      translators.add(new IdTranslator(SkynetDatabase.ATTR_ID_SEQ, "attr_id"));
      translators.add(new IdTranslator(SkynetDatabase.ATTR_TYPE_ID_SEQ, "attr_type_id"));
      translators.add(new IdTranslator(SkynetDatabase.BRANCH_ID_SEQ, BRANCH_ID_ALIASES));
      translators.add(new IdTranslator(SkynetDatabase.REL_LINK_ID_SEQ, "rel_link_id"));
      translators.add(new IdTranslator(SkynetDatabase.REL_LINK_TYPE_ID_SEQ, "rel_link_type_id"));
      return translators;
   }

   public void cleanUp() {
      for (IdTranslator translator : translators) {
         translator.cleanUp();
      }
   }

   public boolean isTranslatable(String name) {
      return translatorMap.containsKey(name.toLowerCase());
   }

   public Object translate(Connection connection, String name, Object original) throws Exception {
      Object toReturn = original;
      if (original != null && !useOriginalIds) {
         IdTranslator translator = translatorMap.get(name.toLowerCase());
         if (translator != null) {
            toReturn = translator.getId(connection, (Integer) original);
         }
      }
      return toReturn;
   }

   private final class IdTranslator {
      private String sequenceName;
      private Set<String> aliases;
      private ExportImportJoinQuery joinQuery;

      private IdTranslator(String sequenceName, String... aliases) {
         this.sequenceName = sequenceName;
         this.aliases = new HashSet<String>();
         this.joinQuery = JoinUtility.createExportImportJoinQuery();
         if (aliases != null && aliases.length > 0) {
            for (String alias : aliases) {
               this.aliases.add(alias.toLowerCase());
            }
         }
      }

      public Integer getId(Connection connection, int original) throws Exception {
         ExportImportJoinQuery joinQuery = JoinUtility.createExportImportJoinQuery();
         joinQuery.getQueryId();
         long newData = Query.getNextSeqVal(getSequence());
         joinQuery.add(original, newData);
         return original;
      }

      public String getSequence() {
         return this.sequenceName;
      }

      public boolean hasAliases() {
         return this.aliases.size() > 0;
      }

      public Set<String> getAliases() {
         return this.aliases;
      }

      public void cleanUp() {

      }
   }

}
