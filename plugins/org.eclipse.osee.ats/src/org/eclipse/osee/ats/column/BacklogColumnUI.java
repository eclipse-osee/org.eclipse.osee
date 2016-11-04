/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.core.column.BacklogColumn;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class BacklogColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static BacklogColumnUI instance = new BacklogColumnUI();

   public static BacklogColumnUI getInstance() {
      return instance;
   }

   private BacklogColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".backlog", "Backlog", 100, XViewerAlign.Left, false, SortDataType.String,
         true,
         "Backlog that this item belongs to.  (BL) if Agile Backlog, else Goal (which are sometimes used as backlogs)");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public BacklogColumnUI copy() {
      BacklogColumnUI newXCol = new BacklogColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      try {
         result = BacklogColumn.getColumnText(element, AtsClientService.get().getRelationResolver());
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return result;
   }

}
