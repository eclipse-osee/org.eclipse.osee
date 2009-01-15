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
package org.eclipse.osee.ats.util.widgets.role;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class UserRoleLabelProvider extends XViewerLabelProvider {
   Font font = null;

   private final UserRoleXViewer xViewer;

   public UserRoleLabelProvider(UserRoleXViewer xViewer) {
      super(xViewer);
      this.xViewer = xViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn dCol, int columnIndex) throws OseeCoreException {
      UserRole roleItem = (UserRole) element;
      if (dCol.equals(UserRoleXViewerFactory.User_Col)) {
         if (roleItem.getUser().equals(UserManager.getUser()))
            return SkynetGuiPlugin.getInstance().getImage("red_user_sm.gif");
         else
            return SkynetGuiPlugin.getInstance().getImage("user_sm.gif");
      } else if (dCol.equals(UserRoleXViewerFactory.Role_Col)) {
         return AtsPlugin.getInstance().getImage("role.gif");
      } else if (dCol.equals(UserRoleXViewerFactory.Hours_Spent_Col)) {
         return SkynetGuiPlugin.getInstance().getImage("clock.gif");
      } else if (dCol.equals(UserRoleXViewerFactory.Completed_Col)) {
         return roleItem.isCompleted() ? SkynetGuiPlugin.getInstance().getImage("chkbox_enabled.gif") : SkynetGuiPlugin.getInstance().getImage(
               "chkbox_disabled.gif");
      } else if (dCol.equals(UserRoleXViewerFactory.Num_Major_Col)) {
         return Severity.getImage(Severity.Major);
      } else if (dCol.equals(UserRoleXViewerFactory.Num_Minor_Col)) {
         return Severity.getImage(Severity.Minor);
      } else if (dCol.equals(UserRoleXViewerFactory.Num_Issues_Col)) {
         return Severity.getImage(Severity.Issue);
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn aCol, int columnIndex) throws OseeCoreException {

      UserRole defectItem = ((UserRole) element);
      if (aCol.equals(UserRoleXViewerFactory.User_Col))
         return defectItem.getUser().getName();
      else if (aCol.equals(UserRoleXViewerFactory.Hours_Spent_Col))
         return defectItem.getHoursSpent() == null ? "" : AtsLib.doubleToStrString(defectItem.getHoursSpent(), false);
      else if (aCol.equals(UserRoleXViewerFactory.Role_Col))
         return defectItem.getRole().name();
      else if (aCol.equals(UserRoleXViewerFactory.Completed_Col))
         return String.valueOf(defectItem.isCompleted());
      else if (aCol.equals(UserRoleXViewerFactory.Num_Major_Col))
         return xViewer.getXUserRoleViewer().getReviewArt().getUserRoleManager().getNumMajor(defectItem.getUser()) + "";
      else if (aCol.equals(UserRoleXViewerFactory.Num_Minor_Col))
         return xViewer.getXUserRoleViewer().getReviewArt().getUserRoleManager().getNumMinor(defectItem.getUser()) + "";
      else if (aCol.equals(UserRoleXViewerFactory.Num_Issues_Col)) return xViewer.getXUserRoleViewer().getReviewArt().getUserRoleManager().getNumIssues(
            defectItem.getUser()) + "";
      return "unhandled column";
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

   public UserRoleXViewer getTreeViewer() {
      return xViewer;
   }

}
