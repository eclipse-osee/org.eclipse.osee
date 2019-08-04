/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets.defect;

import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.defect.ReviewDefectManager;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class DefectUtil {

   private final DefectXViewer defectXViewer;
   private final IAtsPeerToPeerReview review;
   private final IRefreshActionHandler refreshActionHandler;

   public DefectUtil(DefectXViewer defectXViewer, IAtsPeerToPeerReview review, IRefreshActionHandler refreshActionHandler) {
      this.defectXViewer = defectXViewer;
      this.review = review;
      this.refreshActionHandler = refreshActionHandler;
   }

   public void handleNewDefect() {
      NewDefectDialog ed = new NewDefectDialog();
      ed.setFillVertically(true);
      if (ed.open() == 0) {
         try {
            SkynetTransaction transaction =
               TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Add Review Defect");
            ReviewDefectItem item = new ReviewDefectItem(AtsClientService.get());
            item.setDescription(ed.getEntry());
            if (ed.getSeverity() != null) {
               item.setSeverity(ed.getSeverity());
            }
            if (Strings.isValid(ed.getEntry2())) {
               item.setLocation(ed.getEntry2());
            }
            ReviewDefectManager defectManager = getDefectManager();
            defectManager.addOrUpdateDefectItem(item);
            defectManager.saveToArtifact(getArtifact());
            transaction.addArtifact(getArtifact());
            transaction.execute();
            refreshActionHandler.refreshActionHandler();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private ReviewDefectManager getDefectManager() {
      return new ReviewDefectManager((PeerToPeerReviewArtifact) review.getStoreObject());
   }

   private Artifact getArtifact() {
      return AtsClientService.get().getQueryServiceClient().getArtifact(review);
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
            SkynetTransaction transaction =
               TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Delete Review Defects");
            ReviewDefectManager defectManager = getDefectManager();
            for (ReviewDefectItem defectItem : items) {
               defectManager.removeDefectItem(defectItem, persist, transaction);
               defectManager.saveToArtifact(getArtifact());
            }
            transaction.addArtifact(getArtifact());
            transaction.execute();
            refreshActionHandler.refreshActionHandler();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public void handleImportDefectsViaList() {
      try {
         EntryDialog ed = new EntryDialog(Displays.getActiveShell(), "Create Defects", null,
            "Enter task titles, one per line.", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
         ed.setFillVertically(true);
         if (ed.open() == 0) {
            SkynetTransaction transaction =
               TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Import Review Defects");
            ReviewDefectManager defectManager = getDefectManager();
            for (String str : ed.getEntry().split("\n")) {
               str = str.replaceAll("\r", "");
               if (Strings.isValid(str)) {
                  defectManager.addDefectItem(str, false, transaction);
               }
            }
            transaction.addArtifact(getArtifact());
            transaction.execute();
            refreshActionHandler.refreshActionHandler();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
