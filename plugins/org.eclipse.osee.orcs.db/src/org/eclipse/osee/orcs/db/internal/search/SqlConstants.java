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
package org.eclipse.osee.orcs.db.internal.search;

/**
 * @author Roberto E. Escobar
 */
public final class SqlConstants {

   private SqlConstants() {
      // Constants
   }

   public static enum TableEnum {
      TXS_TABLE("osee_txs", "txs"),
      ARTIFACT_TABLE("osee_artifact", "art"),
      ATTRIBUTE_TABLE("osee_attribute", "att"),
      RELATION_TABLE("osee_relation_link", "rel"),
      CHAR_JOIN_TABLE("osee_join_char_id", "jch"),
      ID_JOIN_TABLE("osee_join_id", "jid"),
      SEARCH_TAGS_TABLE("osee_search_tags", "tag");

      private String tableName;
      private String aliasPrefix;

      private TableEnum(String tableName, String aliasPrefix) {
         this.tableName = tableName;
         this.aliasPrefix = aliasPrefix;
      }

      public String getName() {
         return tableName;
      }

      public String getAliasPrefix() {
         return aliasPrefix;
      }

   }

   public static enum CriteriaPriority {
      ARTIFACT_ID,
      ARTIFACT_GUID,
      ARTIFACT_HRID,
      ATTRIBUTE_VALUE,
      ATTRIBUTE_TOKENIZED_VALUE,
      ARTIFACT_TYPE,
      ATTRIBUTE_TYPE_EXISTS,
      RELATION_TYPE_EXISTS;
   }

}
