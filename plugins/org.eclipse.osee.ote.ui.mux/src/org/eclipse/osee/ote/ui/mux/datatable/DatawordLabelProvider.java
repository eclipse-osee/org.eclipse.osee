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
package org.eclipse.osee.ote.ui.mux.datatable;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ote.ui.mux.msgtable.MessageNode;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ky Komadino
 *
 */
public class DatawordLabelProvider extends LabelProvider implements ITableLabelProvider {
   public String getColumnText(Object obj, int index) {
      if (obj != null && obj instanceof RowNode) {
         if (index >= 0 && index <= 7)
            return String.valueOf(((RowNode)obj).getDataword(index));
         else
            return "";
      }
      else
         return "";
   }

   public Image getColumnImage(Object obj, int index) {
      return getImage(obj);
   }

   public String getText(Object obj) {
      return ((MessageNode)obj).getName();
   }

}
