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

import java.util.ArrayList;

/**
 * @author Donald G. Dunne
 */
public class DbModel {

   private ArrayList<Object> columns = new ArrayList<Object>();
   private boolean needSave = false;
   private ArrayList<String> changedColumns = new ArrayList<String>();

   public DbModel() {
   }

   public Object getColumn(int num) {
      if (columns.size() == 0 || num > columns.size())
         return "";
      else
         return columns.get(num);
   }

   public void addColumn(int column, Object obj) {
      columns.add(column, obj);
   }

   public void setColumn(int column, Object obj) {
      columns.set(column, obj);
   }

   public boolean isNeedSave() {
      return needSave;
   }

   public void setNeedSave(boolean needSave) {
      this.needSave = needSave;
   }

   public void setColumnChanged(String column) {
      if (column == null) changedColumns.clear();
      changedColumns.add(column);
   }

   public boolean isColumnChanged(String column) {
      return changedColumns.contains(column);
   }

   public String[] getValues() {
      String[] values = new String[columns.size()];
      int x = 0;
      for (Object o : columns) {
         if (o instanceof String)
            values[x] = (String) o;
         else if (o instanceof Long)
            values[x] = ((Long) o).toString();
         else if (o == null) {
            values[x] = "";
         } else {
            System.err.println("Invalid value type");
         }
         x++;
      }
      return values;
   }

}