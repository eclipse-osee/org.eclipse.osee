/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.widgets.defect;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class DefectLabelProvider extends XViewerLabelProvider {

   public DefectLabelProvider(DefectXViewer xViewer) {
      super(xViewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn dCol, int columnIndex) {
      ReviewDefectItem defectItem = (ReviewDefectItem) element;
      if (dCol.equals(PeerReviewDefectXViewerColumns.Severity_Col)) {
         return DefectSeverityToImage.getImage(defectItem.getSeverity());
      } else if (dCol.equals(PeerReviewDefectXViewerColumns.Injection_Activity_Col)) {
         return ImageManager.getImage(FrameworkImage.INFO_SM);
      } else if (dCol.equals(PeerReviewDefectXViewerColumns.Disposition_Col)) {
         return DefectDispositionToImage.getImage(defectItem.getDisposition());
      } else if (dCol.equals(PeerReviewDefectXViewerColumns.Closed_Col)) {
         return ImageManager.getImage(
            defectItem.isClosed() ? PluginUiImage.CHECKBOX_ENABLED : PluginUiImage.CHECKBOX_DISABLED);
      } else if (dCol.equals(PeerReviewDefectXViewerColumns.User_Col)) {
         try {
            User user = UserManager.getUserByUserId(defectItem.getUserId());
            return ArtifactImageManager.getImage(user);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else if (dCol.equals(PeerReviewDefectXViewerColumns.Closed_By_Col)) {
         try {
            if (Strings.isValid(defectItem.getClosedUserId())) {
               User user = UserManager.getUserByUserId(defectItem.getClosedUserId());
               return ArtifactImageManager.getImage(user);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn aCol, int columnIndex) {
      ReviewDefectItem defectItem = (ReviewDefectItem) element;
      if (aCol.equals(PeerReviewDefectXViewerColumns.User_Col)) {
         String name;
         try {
            AtsUser atsUser = AtsApiService.get().getUserService().getUserByUserId(defectItem.getUserId());
            name = atsUser.getName();
         } catch (OseeCoreException ex) {
            name = String.format("Erroring getting user name: [%s]", ex.getLocalizedMessage());
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         return name;
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.Closed_By_Col)) {
         String name = "";
         if (Strings.isValid(defectItem.getClosedUserId())) {
            try {
               AtsUser atsUser = AtsApiService.get().getUserService().getUserByUserId(defectItem.getClosedUserId());
               name = atsUser.getName();
            } catch (OseeCoreException ex) {
               name = String.format("Erroring getting user name: [%s]", ex.getLocalizedMessage());
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         return name;
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.Closed_Col)) {
         return String.valueOf(defectItem.isClosed());
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.Created_Date_Col)) {
         return DateUtil.getMMDDYYHHMM(defectItem.getDate());
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.Description_Col)) {
         return defectItem.getDescription();
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.Resolution_Col)) {
         return defectItem.getResolution();
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.DefectId_Col)) {
         return String.valueOf(defectItem.getId());
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.Location_Col)) {
         return defectItem.getLocation();
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.Severity_Col)) {
         return defectItem.getSeverity().equals(Severity.None) ? "" : defectItem.getSeverity().name();
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.Disposition_Col)) {
         return defectItem.getDisposition().equals(Disposition.None) ? "" : defectItem.getDisposition().name();
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.Injection_Activity_Col)) {
         return defectItem.getInjectionActivity() == InjectionActivity.None ? "" : defectItem.getInjectionActivity().name();
      } else if (aCol.equals(PeerReviewDefectXViewerColumns.Notes_Col)) {
         return defectItem.getNotes();
      }
      return "Unhandled Column";
   }

   /**
    * Provides the XViewerSorter the actual Date object to sort instead of having to convert the text back to Date (and
    * loose the precision)
    */
   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      if (!(element instanceof ReviewDefectItem)) {
         return "";
      }
      ReviewDefectItem item = (ReviewDefectItem) element;
      if (xCol.getId().equals(PeerReviewDefectXViewerColumns.Created_Date_Col.getId())) {
         return item.getDate();
      }
      return super.getBackingData(element, xCol, columnIndex);
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
