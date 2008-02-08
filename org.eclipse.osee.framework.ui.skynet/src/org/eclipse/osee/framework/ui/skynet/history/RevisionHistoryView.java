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
package org.eclipse.osee.framework.ui.skynet.history;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.CacheArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.TransactionArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.event.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalDeletedBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteDeletedBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactDiffMenu;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactPreviewMenu;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays an artifacts revision history specific to a branch.
 * 
 * @author Jeff C. Phillips
 */
public class RevisionHistoryView extends ViewPart implements IActionable, IEventReceiver {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView";
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(RevisionHistoryView.class);
   private static final String[] columnNames = {"Revision", "Time Stamp", "Author", "Comment"};
   private static final String ARTIFACT_GUID = "GUID";
   private TreeViewer treeViewer;
   private Artifact artifact;

   /**
    * 
    */
   public RevisionHistoryView() {
      super();

      SkynetEventManager.getInstance().unRegisterAll(this);
      SkynetEventManager.getInstance().register(LocalCommitBranchEvent.class, this);
      SkynetEventManager.getInstance().register(RemoteCommitBranchEvent.class, this);
      SkynetEventManager.getInstance().register(LocalDeletedBranchEvent.class, this);
      SkynetEventManager.getInstance().register(RemoteDeletedBranchEvent.class, this);
      SkynetEventManager.getInstance().register(CacheArtifactModifiedEvent.class, this);
      SkynetEventManager.getInstance().register(TransactionArtifactModifiedEvent.class, this);
      SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);

   }

   public static void open(Artifact artifact) {
      IWorkbenchPage page = AWorkbench.getActivePage();
      try {
         RevisionHistoryView revisionHistoryView =
               (RevisionHistoryView) page.showView(RevisionHistoryView.VIEW_ID, artifact.getGuid(),
                     IWorkbenchPage.VIEW_ACTIVATE);
         revisionHistoryView.explore(artifact);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   @Override
   public void createPartControl(Composite parent) {
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

      GridData gridData = new GridData();
      gridData.verticalAlignment = GridData.FILL;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.grabExcessHorizontalSpace = true;

      parent.setLayoutData(gridData);

      treeViewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI);
      treeViewer.setContentProvider(new RevisionHistoryContentProvider());
      treeViewer.setLabelProvider(new RevisionHistoryLabelProvider());

      createColumns();
      createTreeExpandListener();
      treeViewer.addDoubleClickListener(new Transaction2ClickListener());

      Menu popupMenu = new Menu(parent);
      ArtifactPreviewMenu.createPreviewMenuItem(popupMenu, treeViewer);
      ArtifactDiffMenu.createDiffMenuItem(popupMenu, treeViewer, "Compare two Artifacts", null);
      treeViewer.getTree().setMenu(popupMenu);

      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Revision History");
      SkynetContributionItem.addTo(this, true);

      explore(artifact);
   }

   private void createTreeExpandListener() {
      treeViewer.addTreeListener(new ITreeViewerListener() {

         public void treeCollapsed(TreeExpansionEvent event) {
            Display.getCurrent().asyncExec(new Runnable() {
               public void run() {
                  packColumnData();
               }
            });
         }

         public void treeExpanded(TreeExpansionEvent event) {
            Display.getCurrent().asyncExec(new Runnable() {
               public void run() {
                  packColumnData();
               }
            });
         }
      });
   }

   private void createColumns() {
      Tree tree = treeViewer.getTree();

      tree.setHeaderVisible(true);
      TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
      column1.setWidth(100);
      column1.setText(columnNames[0]);

      TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
      column2.setWidth(200);
      column2.setText(columnNames[1]);

      TreeColumn column3 = new TreeColumn(tree, SWT.LEFT);
      column3.setWidth(150);
      column3.setText(columnNames[2]);

      TreeColumn column4 = new TreeColumn(tree, SWT.LEFT);
      column4.setWidth(250);
      column4.setText(columnNames[3]);

      setHelpContexts();
   }

   @Override
   public void setFocus() {
      treeViewer.getControl().setFocus();
   }

   /**
    * Explores an artifacts history.
    * 
    * @param artifact
    */
   public void explore(Artifact artifact) {
      if (treeViewer != null && artifact != null) {
         this.artifact = artifact;
         //         Pair<TransactionId, TransactionId> points = transactionIdManager.getStartEndPoint(artifact.getBranch());
         //         historyTable.setInput(new ArtifactChange(ChangeType.OUTGOING, ModificationType.CHANGE, artifact, null, null, points.getKey(), points.getValue(),0));
         treeViewer.setInput(artifact);
         setContentDescription("Artifact: " + artifact.getDescriptiveName());
         packColumnData();
      }
   }

   public String getActionDescription() {
      return "";
   }

   private void packColumnData() {
      TreeColumn[] columns = treeViewer.getTree().getColumns();
      for (TreeColumn column : columns) {
         column.pack();
      }
   }

   private void setHelpContexts() {
      SkynetGuiPlugin.getInstance().setHelp(treeViewer.getControl(), "revision_history_tree_viewer");
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
    */
   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);

      try {
         if (memento != null) {
            Artifact previousArtifact =
                  ArtifactPersistenceManager.getInstance().getArtifact(memento.getString(ARTIFACT_GUID),
                        BranchPersistenceManager.getInstance().getDefaultBranch());
            if (previousArtifact != null) {
               artifact = previousArtifact;
               return;
            }
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "Falling back to the root artifact: " + ex.getLocalizedMessage(), ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      if (artifact != null) {
         memento.putString(ARTIFACT_GUID, artifact.getGuid());
      }
      super.saveState(memento);
   }

   public void onEvent(Event event) {
      boolean closeView = false;

      if (event instanceof TransactionEvent) {
         ((TransactionEvent) event).fireSingleEvent(this);
      }

      if (event instanceof ArtifactModifiedEvent) {
         ArtifactModifiedEvent artModEvent = (ArtifactModifiedEvent) event;
         closeView =
               artifact != null && artModEvent.getType() == ModType.Deleted && artModEvent.getGuid().equals(
                     artifact.getGuid());
      }

      if ((event instanceof LocalCommitBranchEvent) || (event instanceof RemoteCommitBranchEvent) || (event instanceof LocalDeletedBranchEvent) || (event instanceof RemoteDeletedBranchEvent)) {
         closeView = artifact != null && ((BranchEvent) event).getBranchId() == artifact.getBranch().getBranchId();
      }

      if (closeView) {
         getViewSite().getPage().hideView(getViewSite().getPage().findViewReference(VIEW_ID, artifact.getGuid()));
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.WorkbenchPart#dispose()
    */
   @Override
   public void dispose() {
      SkynetEventManager.getInstance().unRegisterAll(this);

      super.dispose();
   }
}
