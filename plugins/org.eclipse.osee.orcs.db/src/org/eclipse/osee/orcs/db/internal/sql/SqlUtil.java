/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.sql;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class SqlUtil {

   private SqlUtil() {
      // Utility class
   }

   public static AliasEntry newAlias(final String name, final String prefix) {
      return new AliasEntry() {
         @Override
         public String getPrefix() {
            return prefix;
         }

         @Override
         public String getName() {
            return name;
         }
      };
   }

   public static WithClause newSimpleWithClause(String name, String body) {
      return newWithClause(false, name, "", body);
   };

   public static WithClause newRecursiveWithClause(String name, String parameters, String body) {
      return newWithClause(true, name, parameters, body);
   };

   public static WithClause newWithClause(final boolean isRecursive, final String name, final String parameters, final String body) {
      return new WithClause() {

         @Override
         public String getName() {
            return name;
         }

         @Override
         public String getParameters() {
            return parameters;
         }

         @Override
         public boolean isRecursive() {
            return isRecursive;
         }

         @Override
         public String getBody() {
            return body;
         }

         @Override
         public boolean hasParameters() {
            return Strings.isValid(parameters);
         }

      };
   }
}
