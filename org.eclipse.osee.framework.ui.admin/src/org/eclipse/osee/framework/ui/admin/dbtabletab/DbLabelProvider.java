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

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Label provider for the TableViewerExample
 * 
 * @see org.eclipse.jface.viewers.LabelProvider
 * @author Jeff C. Phillips
 */
public class DbLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

   private final String[] columnNames;

   public DbLabelProvider(String[] columnNames) {
      this.columnNames = columnNames;
   };

   public Color getForeground(Object element, int columnIndex) {
      DbModel model = (DbModel) element;
      if (model.isColumnChanged(columnNames[columnIndex])) return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
      return null;
   }

   public Color getBackground(Object element, int columnIndex) {
      return null;
   }

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
    */
   public String getColumnText(Object element, int columnIndex) {
      DbModel dbModel = (DbModel) element;
      Object obj = dbModel.getColumn(columnIndex);
      if (obj == null)
         return "";
      else if (obj instanceof String)
         return (String) obj;
      else if (obj instanceof Long)
         return ((Long) obj).toString();
      else
         return "Unknown column type";
   }

   /**
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
    */
   public Image getColumnImage(Object element, int columnIndex) {
      return null;
   }

}