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

package org.eclipse.osee.framework.ui.skynet.widgets.xcommit;

import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @see ViewPart
 * @author Donald G. Dunne
 */
public class CommitManagerView extends ViewPart implements IActionable, IBranchEventListener, IFrameworkTransactionEventListener {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xcommit.CommitManagerView";
   private static String HELP_CONTEXT_ID = "CommitManagerView";
   private XCommitViewer xCommitViewer;
   private IBranchArtifact branchArtifact;
   private static final String INPUT = "CommitManagerViewInput";

   /**
    * @author Donald G. Dunne
    */
   public CommitManagerView() {
   }

   public static void openViewUpon(final IBranchArtifact branchArtifact) {
      Job job = new Job("Open Change Manager") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  try {
                     IWorkbenchPage page = AWorkbench.getActivePage();
                     CommitManagerView commitManagerView =
                           (CommitManagerView) page.showView(VIEW_ID,
                                 String.valueOf(branchArtifact.getWorkingBranch().getBranchId()),
                                 IWorkbenchPage.VIEW_ACTIVATE);
                     commitManagerView.explore(branchArtifact);

                  } catch (Exception ex) {
                     OSEELog.logException(SkynetGuiPlugin.class, ex, true);
                  }
               }
            });

            monitor.done();
            return Status.OK_STATUS;
         }
      };

      Jobs.startJob(job);
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   @Override
   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      /*
       * Create a grid layout object so the text and treeviewer are layed out the way I want.
       */
      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      xCommitViewer = new XCommitViewer();
      xCommitViewer.setDisplayLabel(false);
      xCommitViewer.createWidgets(parent, 1);
      try {
         if (branchArtifact != null) xCommitViewer.setArtifact(branchArtifact.getArtifact(), "");
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

      SkynetGuiPlugin.getInstance().setHelp(parent, HELP_CONTEXT_ID);

      OseeEventManager.addListener(this);
   }

   public void explore(IBranchArtifact branchArtifact) {
      this.branchArtifact = branchArtifact;
      try {
         if (xCommitViewer != null && branchArtifact != null) xCommitViewer.setArtifact(branchArtifact.getArtifact(),
               "");
         setPartName("Commit Manager: " + branchArtifact.getWorkingBranch().getBranchShortestName());
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   public String getActionDescription() {
      return "";
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);

      try {
         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               int artId = memento.getInteger("artId");
               if (artId > 0) {
                  int branchId = memento.getInteger("branchId");
                  if (branchId > 0) {
                     Branch branch = BranchPersistenceManager.getBranch(branchId);
                     Artifact artifact = ArtifactQuery.getArtifactFromId(artId, branch);
                     explore((IBranchArtifact) artifact);
                  }
               }
            }
         }
      } catch (Exception ex) {
         OSEELog.logWarning(SkynetActivator.class, "Commit Manager error on init: " + ex.getLocalizedMessage(), false);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      try {
         memento = memento.createChild(INPUT);
         memento.putInteger("artId", branchArtifact.getArtifact().getArtId());
         memento.putInteger("branchId", branchArtifact.getArtifact().getBranch().getBranchId());
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (branchArtifact.getArtifact().getBranch().getBranchId() == branchId) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               if (xCommitViewer.getXViewer() == null || xCommitViewer.getXViewer().getTree() == null || xCommitViewer.getXViewer().getTree().isDisposed()) return;
               xCommitViewer.refresh();
            }
         });
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(final Sender sender, final FrameworkTransactionData transData) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            if (xCommitViewer.getXViewer() == null || xCommitViewer.getXViewer().getTree() == null || xCommitViewer.getXViewer().getTree().isDisposed()) return;
            xCommitViewer.refresh();
         }
      });

   }

}