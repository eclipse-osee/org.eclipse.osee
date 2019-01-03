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
package org.eclipse.osee.ats.ide.util.widgets.role;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.core.review.UserRoleManager;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.widgets.defect.DefectSeverityToImage;
import org.eclipse.osee.ats.ide.workflow.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.ide.workflow.review.defect.ReviewDefectManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class UserRoleLabelProvider extends XViewerLabelProvider {
   private final UserRoleXViewer xViewer;

   public UserRoleLabelProvider(UserRoleXViewer xViewer) {
      super(xViewer);
      this.xViewer = xViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn dCol, int columnIndex) {
      UserRole roleItem = (UserRole) element;
      try {
         IAtsUser user = UserRoleManager.getUser(roleItem, AtsClientService.get());
         if (dCol.equals(UserRoleXViewerFactory.User_Col)) {
            return ArtifactImageManager.getImage(AtsClientService.get().getUserServiceClient().getOseeUser(user));
         } else if (dCol.equals(UserRoleXViewerFactory.Role_Col)) {
            return ImageManager.getImage(AtsImage.ROLE);
         } else if (dCol.equals(UserRoleXViewerFactory.Hours_Spent_Col)) {
            return ImageManager.getImage(FrameworkImage.CLOCK);
         } else if (dCol.equals(UserRoleXViewerFactory.Completed_Col)) {
            return ImageManager.getImage(
               roleItem.isCompleted() ? PluginUiImage.CHECKBOX_ENABLED : PluginUiImage.CHECKBOX_DISABLED);
         } else if (dCol.equals(UserRoleXViewerFactory.Num_Major_Col)) {
            return DefectSeverityToImage.getImage(Severity.Major);
         } else if (dCol.equals(UserRoleXViewerFactory.Num_Minor_Col)) {
            return DefectSeverityToImage.getImage(Severity.Minor);
         } else if (dCol.equals(UserRoleXViewerFactory.Num_Issues_Col)) {
            return DefectSeverityToImage.getImage(Severity.Issue);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn aCol, int columnIndex) {

      UserRole userRole = (UserRole) element;
      IAtsUser user = UserRoleManager.getUser(userRole, AtsClientService.get());
      if (aCol.equals(UserRoleXViewerFactory.User_Col)) {
         return user.getName();
      } else if (aCol.equals(UserRoleXViewerFactory.Hours_Spent_Col)) {
         return userRole.getHoursSpent() == null ? "" : AtsUtil.doubleToI18nString(userRole.getHoursSpent(), false);
      } else if (aCol.equals(UserRoleXViewerFactory.Role_Col)) {
         return userRole.getRole().name();
      } else if (aCol.equals(UserRoleXViewerFactory.Completed_Col)) {
         return String.valueOf(userRole.isCompleted());
      } else if (aCol.equals(UserRoleXViewerFactory.Num_Major_Col)) {
         ReviewDefectManager defectMgr = new ReviewDefectManager(xViewer.getXUserRoleViewer().getReviewArt());
         return defectMgr.getNumMajor(user) + "";
      } else if (aCol.equals(UserRoleXViewerFactory.Num_Minor_Col)) {
         ReviewDefectManager defectMgr = new ReviewDefectManager(xViewer.getXUserRoleViewer().getReviewArt());
         return defectMgr.getNumMinor(user) + "";
      } else if (aCol.equals(UserRoleXViewerFactory.Num_Issues_Col)) {
         ReviewDefectManager defectMgr = new ReviewDefectManager(xViewer.getXUserRoleViewer().getReviewArt());
         return defectMgr.getNumIssues(user) + "";
      }
      return "unhandled column";
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

}
