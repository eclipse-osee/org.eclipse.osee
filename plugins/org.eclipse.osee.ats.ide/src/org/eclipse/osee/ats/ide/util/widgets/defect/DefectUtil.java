/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewDefectManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class DefectUtil {

   private final DefectXViewer defectXViewer;
   private final IAtsPeerToPeerReview review;

   public DefectUtil(DefectXViewer defectXViewer, IAtsPeerToPeerReview review) {
      this.defectXViewer = defectXViewer;
      this.review = review;
   }

   public void handleNewDefect() {
      NewDefectDialog ed = new NewDefectDialog();
      ed.setFillVertically(true);
      if (ed.open() == 0) {
         try {

            IAtsChangeSet changes = AtsApiService.get().createChangeSet("Add Review Defect");
            ReviewDefectItem item = new ReviewDefectItem();
            item.setUserId(AtsApiService.get().getUserService().getCurrentUserId());
            item.setDescription(ed.getEntry());
            if (ed.getSeverity() != null) {
               item.setSeverity(ed.getSeverity());
            }
            if (Strings.isValid(ed.getEntry2())) {
               item.setLocation(ed.getEntry2());
            }
            IAtsPeerReviewDefectManager defectManager = review.getDefectManager();
            defectManager.addOrUpdateDefectItem(item);
            defectManager.saveToArtifact(review, changes);
            changes.execute();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public void handleDeleteDefect(boolean persist) {
      final List<ReviewDefectItem> items = defectXViewer.getSelectedDefectItems();
      if (items.isEmpty()) {
         AWorkbench.popup("ERROR", "No Defects Selected");
         return;
      }
      StringBuilder builder = new StringBuilder();
      for (ReviewDefectItem defectItem : items) {
         builder.append("\"" + defectItem.getDescription() + "\"\n");
      }

      boolean delete = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
         "Delete Defects", "Are You Sure You Wish to Delete the Defects(s):\n\n" + builder.toString());
      if (delete) {
         try {
            IAtsChangeSet changes = AtsApiService.get().createChangeSet("Delete Review Defects");
            IAtsPeerReviewDefectManager defectManager = review.getDefectManager();
            for (ReviewDefectItem defectItem : items) {
               defectManager.removeDefectItem(defectItem);
               defectManager.saveToArtifact(review, changes);
            }
            changes.executeIfNeeded();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public void handleImportDefectsViaList() {
      try {
         EntryDialog ed = new EntryDialog(Displays.getActiveShell(), "Create Defects", null,
            "Enter Defect descriptions, one per line.", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
         ed.setFillVertically(true);
         if (ed.open() == 0) {
            IAtsChangeSet changes = AtsApiService.get().createChangeSet("Import Review Defects");
            IAtsPeerReviewDefectManager defectManager = review.getDefectManager();
            for (String str : ed.getEntry().split("\n")) {
               str = str.replaceAll("\r", "");
               if (Strings.isValid(str)) {
                  defectManager.addDefectItem(str);
               }
            }
            defectManager.saveToArtifact(review, changes);
            changes.execute();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
