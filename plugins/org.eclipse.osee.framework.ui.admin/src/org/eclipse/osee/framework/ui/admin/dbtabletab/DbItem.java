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
package org.eclipse.osee.framework.ui.admin.dbtabletab;


public abstract class DbItem {

   private final String tableName;

   public DbItem(String tableName) {
      this.tableName = tableName;
   }

   public boolean isWriteAccess() {
	   return true;
   }

   public String getTableName() {
      return tableName;
   }

   public abstract boolean isWriteable(String columnName);

   public abstract boolean isBems(String columnName);

   public abstract int getColumnWidth(String columnName);

   public abstract void save(DbDescribe descibre, DbModel model);

   public abstract DbModel createNewRow(DbModel example);

}
