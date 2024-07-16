/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.sql;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.jdbc.SqlTable;

/**
 * @author Roberto E. Escobar
 */
public class SqlAliasManager {
   private final HashMap<String, Alias> aliasCounter = new HashMap<>();
   private final List<AliasSet> usedAliases = new ArrayList<>();
   private int level = 0;

   private AliasSet getAliasByLevel(int level) {
      AliasSet dataSet = null;
      if (level < usedAliases.size()) {
         dataSet = usedAliases.get(level);
      }
      return dataSet;
   }

   public boolean hasAlias(int level, SqlTable table, ObjectType objectType) {
      return !getAliases(level, table, objectType).isEmpty();
   }

   public List<String> getAliases(int level, SqlTable table, ObjectType objectType) {
      List<String> toReturn;
      AliasSet dataSet = getAliasByLevel(level);
      if (dataSet != null) {
         if (objectType == null || ObjectType.UNKNOWN == objectType) {
            toReturn = dataSet.getAliases(table.getPrefix());
         } else {
            toReturn = dataSet.getAliases(table.getPrefix(), objectType);
         }
      } else {
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }

   public String getFirstAlias(int level, SqlTable table, ObjectType objectType) {
      Collection<String> aliases = getAliases(level, table, objectType);
      return Iterables.getFirst(aliases, null);
   }

   public String getLastAlias(SqlTable table, ObjectType objectType) {
      Collection<String> aliases = getAliases(level, table, objectType);
      return Iterables.getLast(aliases, null);
   }

   public String getNextAlias(String prefix, ObjectType type) {
      Alias alias = aliasCounter.get(prefix);
      if (alias == null) {
         alias = new Alias(prefix);
         aliasCounter.put(prefix, alias);
      }
      String aliasValue = alias.next();
      putAlias(prefix, type, aliasValue);
      return aliasValue;
   }

   public String getPreviousAlias(String prefix) {
      AliasSet aliasByLevel = getAliasByLevel(0);
      if (aliasByLevel == null) {
         return "";
      }
      List<String> aliases = aliasByLevel.getAliases(prefix);
      if (aliases.isEmpty() || aliases.size() == 1) {
         return "";
      } else {
         return aliases.get(aliases.size() - 2);
      }
   }

   public String getLastAlias(String prefix) {
      AliasSet aliasByLevel = getAliasByLevel(0);

      if (aliasByLevel == null) {
         return "";
      }

      List<String> aliases = aliasByLevel.getAliases(prefix);
      if (aliases.isEmpty() || aliases.size() == 1) {
         return "";
      } else {
         return aliases.get(aliases.size() - 1);
      }
   }

   public void putAlias(SqlTable table, ObjectType type, String alias) {
      String prefix = table.getPrefix();
      putAlias(prefix, type, alias);
   }

   private void putAlias(String key, ObjectType type, String alias) {
      AliasSet dataSet = null;
      if (level < usedAliases.size()) {
         dataSet = usedAliases.get(level);
      }
      if (dataSet == null) {
         dataSet = new AliasSet();
         usedAliases.add(level, dataSet);
      }
      dataSet.putAlias(key, type, alias);
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

   private static final class AliasSet {

      private final Map<String, ListMultimap<ObjectType, String>> used = new HashMap<>();

      public List<String> getAliases(String key, ObjectType type) {
         ListMultimap<ObjectType, String> data = used.get(key);
         return data != null ? data.get(type) : Collections.<String> emptyList();
      }

      public List<String> getAliases(String key) {
         Multimap<ObjectType, String> data = used.get(key);
         return data != null ? Lists.newArrayList(data.values()) : Collections.<String> emptyList();
      }

      public void putAlias(String key, ObjectType type, String alias) {
         ListMultimap<ObjectType, String> multimap = used.get(key);
         if (multimap == null) {
            multimap = newListMultimap();
            used.put(key, multimap);
         }
         multimap.put(type, alias);
      }

      private static <K, V> ListMultimap<K, V> newListMultimap() {
         Map<K, Collection<V>> map = Maps.newLinkedHashMap();
         return Multimaps.newListMultimap(map, new Supplier<List<V>>() {
            @Override
            public List<V> get() {
               return Lists.newArrayList();
            }
         });
      }
   }

   private final class Alias {
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
