/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.orcs.db.internal.branch;

import java.util.function.Consumer;
import org.apache.commons.lang.mutable.MutableLong;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.KeyValueStore;

/**
 * @author Angel Avila
 */
public class KeyValueModule {

   private final JdbcClient jdbcClient;

   private static final String SELECT_KEY_WITH_KEY = "select * from osee_key_value where key = ?";
   private static final String SELECT_KEY_WITH_VALUE = "select * from osee_key_value where value = ?";

   public KeyValueModule(JdbcClient jdbcClient) {
      super();
      this.jdbcClient = jdbcClient;
   }

   public KeyValueStore createKeyValueStore() {
      return new KeyValueStore() {

         @Override
         public Long putIfAbsent(String value) {
            Long key = getByValue(value);
            if (key.equals(0L)) {
               key = Lib.generateUuid();
               jdbcClient.runPreparedUpdate(OseeDb.OSEE_KEY_VALUE_TABLE.getInsertSql(), key, value);
            }

            return key;
         }

         @Override
         public String getByKey(Long key) {
            final MutableString toReturn = new MutableString();

            jdbcClient.runQuery(new Consumer<JdbcStatement>() {

               @Override
               public void accept(JdbcStatement chStmt) {
                  toReturn.setValue(chStmt.getString("value"));
               }

            }, SELECT_KEY_WITH_KEY, key);
            return toReturn.getValue();
         }

         @Override
         public Long getByValue(String value) {
            final MutableLong toReturn = new MutableLong(0);
            jdbcClient.runQuery(new Consumer<JdbcStatement>() {

               @Override
               public void accept(JdbcStatement chStmt) {
                  toReturn.setValue(chStmt.getLong("key"));
               }

            }, SELECT_KEY_WITH_VALUE, value);
            return (Long) toReturn.getValue();
         }

         @Override
         public boolean putByKey(Long key, String value) {
            String existingValue = getByKey(key);
            if (!Strings.isValid(existingValue)) {
               jdbcClient.runPreparedUpdate(OseeDb.OSEE_KEY_VALUE_TABLE.getInsertSql(), key, value);
               return true;
            }

            return false;
         }
      };

   }

   private class MutableString {

      private String value;

      public String getValue() {
         return value;
      }

      public void setValue(String value) {
         this.value = value;
      }
   }
}
