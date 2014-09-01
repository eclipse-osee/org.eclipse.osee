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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class SqlAliasManager {

   private final HashMap<String, Alias> aliasCounter = new HashMap<String, Alias>();
   private final List<Map<String, LinkedList<String>>> usedAliases = new ArrayList<Map<String, LinkedList<String>>>();

   private int level = 0;

   private int getLevel() {
      return level;
   }

   private Map<String, LinkedList<String>> getAliasByLevel(int level) {
      if (level < usedAliases.size()) {
         return usedAliases.get(level);
      } else {
         return Collections.emptyMap();
      }
   }

   public boolean hasAlias(AliasEntry table) {
      int level = getLevel();
      return hasAlias(table, level);
   }

   public boolean hasAlias(AliasEntry table, int level) {
      return !getAliases(table, level).isEmpty();
   }

   public List<String> getAliases(AliasEntry table, int level) {
      List<String> linkedList = getAliasByLevel(level).get(table.getPrefix());
      return linkedList != null ? linkedList : Collections.<String> emptyList();
   }

   public String getFirstAlias(TableEnum table, int level) {
      return getAliases(table, level).get(0);
   }

   public String getLastAlias(AliasEntry table) {
      int currentLevel = getLevel();
      LinkedList<String> values = getAliasByLevel(currentLevel).get(table.getPrefix());
      String toReturn = null;
      if (values != null) {
         toReturn = values.getLast();
      }
      return toReturn;
   }

   public String getNextAlias(AliasEntry table) {
      String prefix = table.getPrefix();

      Alias alias = aliasCounter.get(prefix);
      if (alias == null) {
         alias = new Alias(prefix);
         aliasCounter.put(prefix, alias);
      }
      String aliasValue = alias.next();

      int level = getLevel();
      putAlias(level, prefix, aliasValue);
      return aliasValue;
   }

   private void putAlias(int level, String prefix, String alias) {
      Map<String, LinkedList<String>> map = null;
      if (level < usedAliases.size()) {
         map = usedAliases.get(level);
      }
      if (map == null) {
         map = new HashMap<String, LinkedList<String>>();
         usedAliases.add(level, map);
      }

      LinkedList<String> values = map.get(prefix);
      if (values == null) {
         values = new LinkedList<String>();
         map.put(prefix, values);
      }
      values.add(alias);
   }

   public void nextLevel() {
      level++;
   }

   public void reset() {
      level = 0;
      for (Alias alias : aliasCounter.values()) {
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
