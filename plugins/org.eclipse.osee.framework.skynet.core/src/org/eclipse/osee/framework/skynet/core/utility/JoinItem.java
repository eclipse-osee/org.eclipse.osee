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
package org.eclipse.osee.framework.skynet.core.utility;

/**
 * @author Roberto E. Escobar
 */
public enum JoinItem {

   TRANSACTION("osee_join_transaction", "INSERT INTO osee_join_transaction (query_id, gamma_id, transaction_id, branch_id) VALUES (?, ?, ?, ?)"),
   ID("osee_join_id", "INSERT INTO osee_join_id (query_id, id) VALUES (?, ?)"),
   CHAR_ID("osee_join_char_id", "INSERT INTO osee_join_char_id (query_id, id) VALUES (?, ?)"),
   ID4("osee_join_id4", "INSERT INTO osee_join_id4 (query_id, id1, id2, id3, id4) VALUES (?, ?, ?, ?, ?)");

   private final String tableName;
   private final String deleteSql;
   private final String insertSql;

   JoinItem(String tableName, String insertSql) {
      this.tableName = tableName;
      this.deleteSql = String.format("DELETE FROM %s WHERE query_id = ?", tableName);
      this.insertSql = insertSql;
   }

   public String getDeleteSql() {
      return deleteSql;
   }

   public String getInsertSql() {
      return insertSql;
   }

   public String getJoinTableName() {
      return tableName;
   }
}