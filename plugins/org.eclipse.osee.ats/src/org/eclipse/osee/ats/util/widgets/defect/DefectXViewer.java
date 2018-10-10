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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem;
import org.eclipse.osee.ats.workflow.review.defect.ReviewDefectManager;
import org.eclipse.osee.ats.workflow.review.defect.ReviewDefectValidator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class DefectXViewer extends XViewer {

   private DefectXViewerMenu defectMenu;
   private final IAtsPeerToPeerReview review;

   public DefectXViewer(Composite parent, int style, IOseeTreeReportProvider reportProvider, IAtsPeerToPeerReview review) {
      this(parent, style, review, new DefectXViewerFactory(reportProvider));
   }

   public DefectXViewer(Composite parent, int style, IAtsPeerToPeerReview review, IXViewerFactory xViewerFactory) {
      super(parent, style, xViewerFactory);
      this.review = review;
   }

   public ReviewDefectManager getDefectManager() {
      return new ReviewDefectManager(AtsClientService.get().getQueryServiceClient().getArtifact(review));
   }

   public void loadTable(DefectData data) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            set(data.getDefectItems());
         }
      });
   }

   /**
    * Loads defect table in background thread.
    *
    * @param listener informed when loading is completed
    */
   public void loadTable(DefectRefreshListener listener) {
      final DefectData data = new DefectData();
      if (Displays.isDisplayThread()) {
         Jobs.startJob(new Job("Loading Defects") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  loadDefectData(data);
                  listener.refreshCompleted(data);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
               return Status.OK_STATUS;
            }

         }, true);
      } else {
         loadDefectData(data);
         listener.refreshCompleted(data);
      }
   }

   /**
    * Loads data in current thread
    */
   private void loadDefectData(final DefectData data) {
      data.defectItems = getDefectManager().getDefectItems();
      data.error = ReviewDefectValidator.isValid(AtsClientService.get().getQueryServiceClient().getArtifact(review));
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      parent.addDisposeListener(new DisposeListener() {
         @Override
         public void widgetDisposed(DisposeEvent e) {
            ((DefectContentProvider) getContentProvider()).clear();
         }
      });
   }

   DefectXViewerMenu getDefectMenu() {
      if (defectMenu == null) {
         defectMenu = new DefectXViewerMenu(this, review);
         defectMenu.createMenuActions();
      }
      return defectMenu;
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();
      getDefectMenu().updateEditMenuActions();
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public void add(Collection<ReviewDefectItem> defectItems) {
      if ((DefectContentProvider) getContentProvider() != null) {
         ((DefectContentProvider) getContentProvider()).add(defectItems);
      }
   }

   public void set(Collection<? extends ReviewDefectItem> defectItems) {
      if ((DefectContentProvider) getContentProvider() != null) {
         ((DefectContentProvider) getContentProvider()).set(defectItems);
      }
   }

   public void clear() {
      if ((DefectContentProvider) getContentProvider() != null) {
         ((DefectContentProvider) getContentProvider()).clear();
      }
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its resources
      getLabelProvider().dispose();
   }

   public List<ReviewDefectItem> getSelectedDefectItems() {
      List<ReviewDefectItem> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((ReviewDefectItem) item.getData());
         }
      }
      return arts;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!isEditable()) {
         return;
      }
      ArrayList<ReviewDefectItem> defectItems = new ArrayList<>();
      for (TreeItem item : treeItems) {
         defectItems.add((ReviewDefectItem) item.getData());
      }
      try {
         getDefectMenu().promptChangeData((XViewerColumn) treeColumn.getData(), defectItems,
            isColumnMultiEditEnabled());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      if (xCol.equals(DefectXViewerFactory.User_Col) || xCol.equals(
         DefectXViewerFactory.Disposition_Col) || xCol.equals(
            DefectXViewerFactory.Injection_Activity_Col) || xCol.equals(
               DefectXViewerFactory.Closed_Col) || xCol.equals(DefectXViewerFactory.Severity_Col)) {
         return handleAltLeftClick(treeColumn, treeItem);
      }
      return false;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (!isEditable()) {
         return false;
      }
      try {
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         ReviewDefectItem defectItem = (ReviewDefectItem) treeItem.getData();
         List<ReviewDefectItem> defectItems = new ArrayList<>();
         defectItems.add(defectItem);
         if (xCol.equals(DefectXViewerFactory.Severity_Col) || xCol.equals(
            DefectXViewerFactory.Disposition_Col) || xCol.equals(DefectXViewerFactory.Created_Date_Col) || xCol.equals(
               DefectXViewerFactory.Closed_Col) || xCol.equals(DefectXViewerFactory.Description_Col) || xCol.equals(
                  DefectXViewerFactory.Resolution_Col) || xCol.equals(DefectXViewerFactory.Location_Col) || xCol.equals(
                     DefectXViewerFactory.User_Col) || xCol.equals(DefectXViewerFactory.Injection_Activity_Col)) {
            return getDefectMenu().promptChangeData(xCol, defectItems, false);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean isEditable() {
      return review.isInWork();
   }

}
