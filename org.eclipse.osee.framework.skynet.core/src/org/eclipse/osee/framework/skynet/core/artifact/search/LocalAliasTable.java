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
package org.eclipse.osee.framework.skynet.core.artifact.search;

/**
 * @author Robert A. Fisher
 */
@Deprecated
class LocalAliasTable extends Table {
   private final String declarationName;

   public LocalAliasTable(String aliasedTableName, String aliasName) {
      super(aliasName);
      this.declarationName = String.format("%s %s", aliasedTableName, aliasName);
   }

   public LocalAliasTable(Table aliasedTable, String aliasName) {
      super(aliasName);
      this.declarationName = String.format("%s %s", aliasedTable.name, aliasName);
   }

   @Override
   public String toString() {
      return declarationName;
   }
}
