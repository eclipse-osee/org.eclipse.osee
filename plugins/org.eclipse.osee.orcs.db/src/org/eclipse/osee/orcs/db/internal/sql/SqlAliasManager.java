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

/**
 * @author Roberto E. Escobar
 */
public class SqlAliasManager {

   private final HashMap<String, Alias> aliases = new HashMap<String, Alias>();
   private final Map<AliasEntry, List<String>> usedAliases = new HashMap<AliasEntry, List<String>>();

   public boolean hasAlias(AliasEntry table) {
      return usedAliases.get(table) != null;
   }

   public String getFirstAlias(AliasEntry table) {
      return getAliases(table).get(0);
   }

   public List<String> getAliases(AliasEntry table) {
      List<String> values = usedAliases.get(table);
      return values != null ? values : Collections.<String> emptyList();
   }

   public String getNextAlias(AliasEntry table) {
      Alias alias = aliases.get(table.getAliasPrefix());
      if (alias == null) {
         alias = new Alias(table.getAliasPrefix());
         aliases.put(table.getAliasPrefix(), alias);
      }
      String toReturn = alias.next();
      putUsedAlias(table, toReturn);
      return toReturn;
   }

   private void putUsedAlias(AliasEntry table, String alias) {
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
