/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange.transform;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

public class DbSchemaRuleAddColumn extends Rule {
   private static final Pattern tablePattern =
      Pattern.compile("\\s+<table name=\"([^\"]+)\"(.*?)</table>", Pattern.DOTALL);

   private final HashCollection<String, String> tableToColumns;

   public DbSchemaRuleAddColumn(HashCollection<String, String> tableToColumns) {
      super(null);
      this.tableToColumns = tableToColumns;
   }

   public DbSchemaRuleAddColumn(String tableName, String column) {
      this(wrap(tableName, column));
   }

   private static HashCollection<String, String> wrap(String tableName, String column) {
      HashCollection<String, String> tableToColumns = new HashCollection<>();
      tableToColumns.put(tableName, column);
      return tableToColumns;
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);

      Matcher tableMatcher = tablePattern.matcher(seq);
      while (tableMatcher.find()) {
         Collection<String> columns = tableToColumns.getValues(tableMatcher.group(1));
         if (columns != null) {
            for (String column : columns) {
               if (!tableMatcher.group(2).contains(column)) {
                  changeSet.insertBefore(tableMatcher.end(2), column);
                  ruleWasApplicable = true;
               }
            }
         }
      }
      return changeSet;
   }
}