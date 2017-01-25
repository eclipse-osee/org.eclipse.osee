/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.sql;

/**
 * @author Roberto E. Escobar
 */
public enum TableEnum implements AliasEntry {
   BRANCH_TABLE("osee_branch", "br"),
   TX_DETAILS_TABLE("osee_tx_details", "txd"),
   TXS_TABLE("osee_txs", "txs"),
   ARTIFACT_TABLE("osee_artifact", "art"),
   ATTRIBUTE_TABLE("osee_attribute", "att"),
   RELATION_TABLE("osee_relation_link", "rel"),
   CHAR_JOIN_TABLE("osee_join_char_id", "jch"),
   ID_JOIN_TABLE("osee_join_id", "jid"),
   SEARCH_TAGS_TABLE("osee_search_tags", "tag"),
   JOIN_ID4_TABLE("osee_join_id4", "jart"),
   MERGE_TABLE("osee_merge", "mbr");

   private final String tableName;
   private final String aliasPrefix;

   private TableEnum(String tableName, String aliasPrefix) {
      this.tableName = tableName;
      this.aliasPrefix = aliasPrefix;
   }

   @Override
   public String getName() {
      return tableName;
   }

   @Override
   public String getPrefix() {
      return aliasPrefix;
   }

}