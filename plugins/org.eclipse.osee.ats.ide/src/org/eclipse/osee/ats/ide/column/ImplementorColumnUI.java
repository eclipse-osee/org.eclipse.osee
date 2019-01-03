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
package org.eclipse.osee.ats.ide.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class ImplementorColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static ImplementorColumnUI instance = new ImplementorColumnUI();

   public static ImplementorColumnUI getInstance() {
      return instance;
   }

   private ImplementorColumnUI() {
      super(AtsColumnId.Implementers.name(), "Implementer", 80, XViewerAlign.Left, false, SortDataType.String, false,
         "User assigned to the Implementation of the changes.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ImplementorColumnUI copy() {
      ImplementorColumnUI newXCol = new ImplementorColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      if (element instanceof IAtsObject) {
         try {
            result =
               AtsClientService.get().getColumnService().getColumnText(AtsColumnId.Implementers, (IAtsObject) element);
         } catch (OseeCoreException ex) {
            result = LogUtil.getCellExceptionString(ex);
         }
      }
      return result;
   }
}
