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

import java.util.logging.Level;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.admin.AdminPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.cellEditor.EnumeratedValue;
import org.eclipse.osee.framework.ui.skynet.widgets.cellEditor.StringValue;
import org.eclipse.swt.widgets.TableItem;

/**
 * This class implements an ICellModifier An ICellModifier is called when the user modifies a cell in the tableViewer
 * 
 * @author Jeff C. Phillips
 */

public class DbCellModifier implements ICellModifier {
   private final DbTableViewer dbTableViewer;
   private final EnumeratedValue enumeratedValue;
   private final StringValue stringValue;
   private final DbItem dbItem;

   /**
    * Constructor
    * 
    * @param dbTableViewer - a DbTableViewer an instance of a TableViewerExample.
    * @param dbItem - a DbItem.
    */
   public DbCellModifier(DbTableViewer dbTableViewer, DbItem dbItem) {
      super();
      this.dbTableViewer = dbTableViewer;
      this.dbItem = dbItem;
      this.enumeratedValue = new EnumeratedValue();
      this.stringValue = new StringValue();
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
    */
   public boolean canModify(Object element, String property) {
      return true;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
    */
   public Object getValue(Object element, String property) {

      int columnIndex = dbTableViewer.getColumnNames().indexOf(property);
      DbModel model = (DbModel) element;
      Object obj = model.getColumn(columnIndex);
      if (obj instanceof Long) {
         stringValue.setValue(((Long) obj).toString());
         return stringValue;
      } else if (dbItem.isBems(property)) {
         try {
            enumeratedValue.setChocies(UserManager.getUserNames());
            User u = null;
            u = UserManager.getUserByUserId((String) obj);
            if (u != null) enumeratedValue.setValue(u.getName());
         } catch (Exception ex) {
            OseeLog.log(AdminPlugin.class, Level.SEVERE, ex);
         }
         return enumeratedValue;
      } else if (obj instanceof String) {
         stringValue.setValue(((String) obj));
         return stringValue;
      } else if (obj == null) {
         stringValue.setValue((""));
         return stringValue;
      }
      return null;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
    */
   public void modify(Object element, String property, Object value) {

      // Find the index of the column
      int columnIndex = dbTableViewer.getColumnNames().indexOf(property);
      TableItem item = (TableItem) element;
      DbModel dbModel = (DbModel) item.getData();
      Object wasObj = dbModel.getColumn(columnIndex);
      if (wasObj instanceof Long) {
         Long newLong = new Long((String) value);
         Long wasLong = (Long) wasObj;
         if (newLong != null && !wasLong.equals(newLong)) {
            dbModel.setColumn(columnIndex, newLong);
            dbModel.setNeedSave(true);
            dbModel.setColumnChanged(property);
         }
      } else if (dbItem.isBems(property)) {
         try {
            String newName = (String) value;
            User newUser = UserManager.getUserByName(newName);
            String oldBems = (String) wasObj;
            if (!newUser.getUserId().equals(oldBems)) {
               dbModel.setColumn(columnIndex, newUser.getUserId());
               dbModel.setNeedSave(true);
               dbModel.setColumnChanged(property);
            }
         } catch (Exception ex) {
            OseeLog.log(AdminPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else if (wasObj instanceof String) {
         if (!((String) wasObj).equals(value)) {
            dbModel.setColumn(columnIndex, value);
            dbModel.setNeedSave(true);
            dbModel.setColumnChanged(property);
         }
      } else if (wasObj == null) {
         dbModel.setColumn(columnIndex, value);
         dbModel.setNeedSave(true);
         dbModel.setColumnChanged(property);
      }

      if (dbModel.isNeedSave()) {
         dbTableViewer.setSaveNeeded(true);
         dbTableViewer.getTaskList().taskChanged(dbModel);
      }
   }
}
