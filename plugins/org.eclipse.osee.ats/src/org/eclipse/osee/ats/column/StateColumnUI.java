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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.workdef.StateColorToSwtColor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Donald G. Dunne
 */
public class StateColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static StateColumnUI instance = new StateColumnUI();

   public static StateColumnUI getInstance() {
      return instance;
   }

   private StateColumnUI() {
      super(AtsColumnId.State.name(), "State", 75, SWT.LEFT, true, SortDataType.String, false, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public StateColumnUI copy() {
      StateColumnUI newXCol = new StateColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsObject) {
            return AtsClientService.get().getColumnService().getColumnText(AtsColumnId.State, (IAtsObject) element);
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof AbstractWorkflowArtifact) {
         return Displays.getSystemColor(
            StateColorToSwtColor.convert(((AbstractWorkflowArtifact) element).getStateDefinition().getColor()));
      }
      return null;
   }

}
