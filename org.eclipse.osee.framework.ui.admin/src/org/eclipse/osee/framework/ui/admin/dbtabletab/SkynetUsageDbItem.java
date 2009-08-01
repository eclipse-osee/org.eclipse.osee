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


/**
 * @author Roberto E. Escobar
 */
public class SkynetUsageDbItem extends DbItem {

   public SkynetUsageDbItem() {
      super("OSEE_USAGE");
   }

   @Override
   public DbModel createNewRow(DbModel example) {
      DbModel dbModel = new DbModel();
      for (int x = 0; x < example.getValues().length; x++) {
         dbModel.addColumn(x, "");
      }
      dbModel.setColumn(0, "NEW");
      dbModel.setColumnChanged("KEY");
      return dbModel;
   }

   @Override
   public int getColumnWidth(String columnName) {
      return 100;
   }

   @Override
   public boolean isBems(String columnName) {
      return false;
   }

   @Override
   public boolean isWriteable(String columnName) {
      return false;
   }

   @Override
   public void save(DbDescribe describe, DbModel model) {
   }

}
