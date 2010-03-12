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
package org.eclipse.osee.framework.branch.management.exchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class IndexCollector {
   private final String source;
   private final String primaryKey;
   private final Set<String> aliases;
   private final Set<String> negativeOneAliases;
   private final Set<Long> ids;
   private final Map<String, Set<Long>> notFound;

   IndexCollector(String source, String primaryKey, String[] aliases, String[] negativeOneAliases) {
      this.source = source;
      this.primaryKey = primaryKey.toLowerCase();
      this.ids = java.util.Collections.synchronizedSet(new HashSet<Long>());
      this.aliases = new HashSet<String>();
      if (aliases != null && aliases.length > 0) {
         for (String alias : aliases) {
            this.aliases.add(alias.toLowerCase());
         }
      }
      this.aliases.add(this.primaryKey);

      this.negativeOneAliases = new HashSet<String>();
      if (negativeOneAliases != null && negativeOneAliases.length > 0) {
         for (String alias : negativeOneAliases) {
            this.negativeOneAliases.add(alias.toLowerCase());
         }
      }
      this.aliases.addAll(this.negativeOneAliases);
      this.notFound = java.util.Collections.synchronizedMap(new HashMap<String, Set<Long>>());
   }

   IndexCollector(String source, String primaryKey, String[] aliases) {
      this(source, primaryKey, aliases, null);
   }

   IndexCollector(String source, String primaryKey) {
      this(source, primaryKey, new String[] {primaryKey}, null);
   }

   public String getSource() {
      return source;
   }

   protected void processData(String source, Map<String, String> fieldMap) throws Exception {
      if (this.source.equals(source)) {
         if (fieldMap.containsKey(primaryKey)) {
            String value = fieldMap.get(primaryKey);
            if (Strings.isValid(value)) {
               ids.add(Long.valueOf(value));
            }
         }
      }
      List<String> intersect = Collections.setIntersection(aliases, fieldMap.keySet());
      for (String key : intersect) {
         String value = fieldMap.get(key);
         if (Strings.isValid(value)) {
            long toCheck = Long.valueOf(value);
            if (!ids.contains(toCheck)) {
               if (!negativeOneAliases.contains(key) && !(toCheck == -1L)) {
                  addValue(key, toCheck);
               }
            }
         }
      }
   }

   private void addValue(String key, Long value) {
      Set<Long> values = notFound.get(key);
      if (values == null) {
         values = java.util.Collections.synchronizedSet(new HashSet<Long>());
         notFound.put(key, values);
      }
      values.add(value);
   }

   protected void removeFalsePositives() {
      if (!notFound.isEmpty()) {
         List<String> keysToRemove = new ArrayList<String>();
         for (String key : notFound.keySet()) {
            Collection<Long> values = notFound.get(key);
            if (values != null && !values.isEmpty()) {
               values.removeAll(ids);
               if (negativeOneAliases.contains(key)) {
                  values.remove(-1L);
               }
            }
            if (values.isEmpty()) {
               keysToRemove.add(key);
            }
         }
         for (String key : keysToRemove) {
            notFound.remove(key);
         }
      }
   }

   public boolean hasErrors() {
      return !notFound.isEmpty();
   }

   public Map<String, Set<Long>> getItemsNotFound() {
      return notFound;
   }
}
