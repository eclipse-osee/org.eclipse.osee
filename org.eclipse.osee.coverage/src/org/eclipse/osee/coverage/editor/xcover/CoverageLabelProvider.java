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
package org.eclipse.osee.coverage.editor.xcover;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.swt.graphics.Image;

public class CoverageLabelProvider extends XViewerLabelProvider {

   private final CoverageXViewer xViewer;

   public CoverageLabelProvider(CoverageXViewer xViewer) {
      super(xViewer);
      this.xViewer = xViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn dCol, int columnIndex) throws OseeCoreException {
      CoverageItem coverageItem = (CoverageItem) element;
      if (dCol.equals(CoverageXViewerFactory.Promoted_Col)) {
         return coverageItem.isPromoted() ? ImageManager.getImage(FrameworkImage.CHECKBOX_ENABLED) : ImageManager.getImage(FrameworkImage.CHECKBOX_DISABLED);
      } else if (dCol.equals(CoverageXViewerFactory.User_Col)) {
         return ImageManager.getImage(coverageItem.getUser());
      } else if (dCol.equals(CoverageXViewerFactory.Eng_Build_Id_Col)) {
      } else if (dCol.equals(CoverageXViewerFactory.Plan_CM_Build_Id_Col)) {
      } else if (dCol.equals(CoverageXViewerFactory.CM_Build_Col)) {
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn aCol, int columnIndex) throws OseeCoreException {
      CoverageItem coverageItem = (CoverageItem) element;
      if (aCol.equals(CoverageXViewerFactory.User_Col))
         return coverageItem.getUser().getName();
      else if (aCol.equals(CoverageXViewerFactory.Promoted_Col))
         return String.valueOf(coverageItem.isPromoted());
      else if (aCol.equals(CoverageXViewerFactory.Eng_Build_Id_Col)) {
         return "here";
      } else if (aCol.equals(CoverageXViewerFactory.Plan_CM_Build_Id_Col)) {
         return "here";
      } else if (aCol.equals(CoverageXViewerFactory.Date_Col))
         return coverageItem.getDateStr(XDate.MMDDYYHHMM);
      else if (aCol.equals(CoverageXViewerFactory.Promoted_Date_Col))
         return coverageItem.getPromotedDateStr(XDate.MMDDYYHHMM);
      else if (aCol.equals(CoverageXViewerFactory.View_Compare_Col))
         return coverageItem.getViewComparison();
      else if (aCol.equals(CoverageXViewerFactory.Notes_Col))
         return coverageItem.getNotes();
      else if (aCol.equals(CoverageXViewerFactory.Sub_System_Col))
         return coverageItem.getSubSystem();
      else if (aCol.equals(CoverageXViewerFactory.Code_Workflow_Id_Col))
         return "here";
      else if (aCol.equals(CoverageXViewerFactory.Code_Workflow_Pcr_Col))
         return "here";
      else if (aCol.equals(CoverageXViewerFactory.Code_Workflow_Title_Col))
         return "here";
      else if (aCol.equals(CoverageXViewerFactory.CM_Build_Col))
         return "here";
      else if (aCol.equals(CoverageXViewerFactory.View_Comp_Groups_Col)) return coverageItem.getViewComparisonGroup();
      return "Unhandled Column";
   }

   public void dispose() {
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public CoverageXViewer getTreeViewer() {
      return xViewer;
   }
}
