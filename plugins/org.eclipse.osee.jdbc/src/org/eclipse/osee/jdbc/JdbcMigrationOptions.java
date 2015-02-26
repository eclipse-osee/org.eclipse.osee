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
package org.eclipse.osee.jdbc;

/**
 * @author Roberto E. Escobar
 */
public class JdbcMigrationOptions {

   private final boolean clean;
   private final boolean baselineOnMigration;

   public JdbcMigrationOptions(boolean clean, boolean baselineOnMigration) {
      super();
      this.clean = clean;
      this.baselineOnMigration = baselineOnMigration;
   }

   public boolean isClean() {
      return clean;
   }

   public boolean isBaselineOnMigration() {
      return baselineOnMigration;
   }
}
