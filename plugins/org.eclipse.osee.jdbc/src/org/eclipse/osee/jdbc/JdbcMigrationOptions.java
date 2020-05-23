/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
