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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ExportIntegrityCheck {
   // Load base Id from source table;
   // Check other tables and ensure all found;

   // Base ID & source Table;
   // Aliases;

   private final class CheckIdSaxHandler {
      private final String primaryKey;
      private final String source;
      private final Set<String> aliases;
      private final Set<Long> ids;
      private final List<Long> notFound;

      private CheckIdSaxHandler(String primaryKey, String source, String... aliases) {
         this.primaryKey = primaryKey;
         this.source = source;
         this.ids = new HashSet<Long>();
         this.aliases = new HashSet<String>();
         if (aliases != null && aliases.length > 0) {
            for (String alias : aliases) {
               this.aliases.add(alias.toLowerCase());
            }
         }
         this.notFound = new ArrayList<Long>();
      }

      protected void processData(Map<String, String> fieldMap) throws Exception {
         if (fieldMap.containsKey(primaryKey)) {
            String value = fieldMap.get(primaryKey);
            if (Strings.isValid(value)) {
               ids.add(Long.valueOf(value));
            }
         }
         List<String> intersect = Collections.setIntersection(aliases, fieldMap.keySet());
         for (String key : intersect) {
            String value = fieldMap.get(key);
            if (Strings.isValid(value)) {
               long toCheck = Long.valueOf(value);
               if (!ids.contains(toCheck) && !notFound.contains(toCheck)) {
                  notFound.add(toCheck);
               }
            }
         }
      }

      public boolean hasErrors() {
         return !notFound.isEmpty();
      }

      public List<Long> getItemsNotFound() {
         return notFound;
      }
   }
}
