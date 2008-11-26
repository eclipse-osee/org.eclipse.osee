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
package org.eclipse.osee.ats.util.widgets.defect;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Disposition;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.InjectionActivity;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class DefectLabelProvider extends XViewerLabelProvider {
   Font font = null;

   private final DefectXViewer xViewer;

   public DefectLabelProvider(DefectXViewer xViewer) {
      super(xViewer);
      this.xViewer = xViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn dCol, int columnIndex) throws OseeCoreException {
      DefectItem defectItem = (DefectItem) element;
      if (dCol.equals(DefectXViewerFactory.Severity_Col))
         return Severity.getImage(defectItem.getSeverity());
      else if (dCol.equals(DefectXViewerFactory.Injection_Activity_Col))
         return AtsPlugin.getInstance().getImage("info.gif");
      else if (dCol.equals(DefectXViewerFactory.Disposition_Col))
         return Disposition.getImage(defectItem.getDisposition());
      else if (dCol.equals(DefectXViewerFactory.Closed_Col)) {
         return defectItem.isClosed() ? SkynetGuiPlugin.getInstance().getImage("chkbox_enabled.gif") : SkynetGuiPlugin.getInstance().getImage(
               "chkbox_disabled.gif");
      } else if (dCol.equals(DefectXViewerFactory.User_Col)) {
         if (defectItem.getUser().equals(UserManager.getUser()))
            return SkynetGuiPlugin.getInstance().getImage("red_user_sm.gif");
         else
            return ArtifactTypeManager.getType("User").getImage();
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn aCol, int columnIndex) throws OseeCoreException {
      DefectItem defectItem = (DefectItem) element;
      if (aCol.equals(DefectXViewerFactory.User_Col))
         return defectItem.getUser().getName();
      else if (aCol.equals(DefectXViewerFactory.Closed_Col))
         return String.valueOf(defectItem.isClosed());
      else if (aCol.equals(DefectXViewerFactory.Created_Date_Col))
         return defectItem.getCreatedDate(XDate.MMDDYYHHMM);
      else if (aCol.equals(DefectXViewerFactory.Description_Col))
         return defectItem.getDescription();
      else if (aCol.equals(DefectXViewerFactory.Resolution_Col))
         return defectItem.getResolution();
      else if (aCol.equals(DefectXViewerFactory.Location_Col))
         return defectItem.getLocation();
      else if (aCol.equals(DefectXViewerFactory.Severity_Col))
         return defectItem.getSeverity().equals(Severity.None) ? "" : defectItem.getSeverity().name();
      else if (aCol.equals(DefectXViewerFactory.Disposition_Col))
         return defectItem.getDisposition().equals(Disposition.None) ? "" : defectItem.getDisposition().name();
      else if (aCol.equals(DefectXViewerFactory.Injection_Activity_Col)) return defectItem.getInjectionActivity() == InjectionActivity.None ? "" : defectItem.getInjectionActivity().name();

      return "Unhandled Column";
   }

   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public DefectXViewer getTreeViewer() {
      return xViewer;
   }
}
