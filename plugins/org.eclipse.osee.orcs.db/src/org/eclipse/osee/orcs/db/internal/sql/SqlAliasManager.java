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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class SqlAliasManager {

   private final HashMap<TableEnum, Alias> aliases = new HashMap<TableEnum, Alias>();
   private final Map<TableEnum, List<String>> usedAliases = new HashMap<TableEnum, List<String>>();

   public SqlAliasManager() {
      for (TableEnum table : TableEnum.values()) {
         aliases.put(table, new Alias(table.getAliasPrefix()));
      }
   }

   public boolean hasAlias(TableEnum table) {
      return usedAliases.get(table) != null;
   }

   public String getFirstAlias(TableEnum table) {
      return getAliases(table).get(0);
   }

   public List<String> getAliases(TableEnum table) {
      List<String> values = usedAliases.get(table);
      return values != null ? values : Collections.<String> emptyList();
   }

   public int getCount(TableEnum table) {
      List<String> list = usedAliases.get(table);
      return list == null ? 0 : list.size();
   }

   public String getNextAlias(TableEnum table) throws OseeCoreException {
      Alias alias = aliases.get(table);
      Conditions.checkNotNull(alias, "alias", "Unable to find alias for [%s]", table);
      String toReturn = alias.next();
      putUsedAlias(table, toReturn);
      return toReturn;
   }

   private void putUsedAlias(TableEnum table, String alias) {
      List<String> values = usedAliases.get(table);
      if (values == null) {
         values = new LinkedList<String>();
         usedAliases.put(table, values);
      }
      values.add(alias);
   }

   public void reset() {
      for (Alias alias : aliases.values()) {
         alias.reset();
      }
      usedAliases.clear();
   }

   private class Alias {
      private final String aliasPrefix;
      private int aliasSuffix;

      public Alias(String aliasPrefix) {
         this.aliasPrefix = aliasPrefix;
         reset();
      }

      public String next() {
         return getName() + aliasSuffix++;
      }

      public String getName() {
         return aliasPrefix;
      }

      public void reset() {
         aliasSuffix = 1;
      }
   }
}
