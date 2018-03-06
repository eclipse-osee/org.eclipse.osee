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
 * @author John R. Misinco
 */
public class WithClause {
   private final boolean isRecursive;
   private final String name;
   private final String parameters;
   private final String body;

   public WithClause(String name, String body) {
      this(false, name, "", body);
   };

   public WithClause(String name, String parameters, String body) {
      this(true, name, parameters, body);
   };

   public WithClause(boolean isRecursive, String name, String parameters, String body) {
      this.isRecursive = isRecursive;
      this.name = name;
      this.parameters = parameters;
      this.body = body;
   }

   public String getName() {
      return name;
   }

   public String getParameters() {
      return parameters;
   }

   public boolean isRecursive() {
      return isRecursive;
   }

   public String getBody() {
      return body;
   }

   public boolean hasParameters() {
      return Strings.isValid(parameters);
   }
}