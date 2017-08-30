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

package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class MergeView extends GenericViewPart implements IBranchEventListener, IArtifactEventListener {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView";

   private MergeXWidget mergeXWidget;
   private IOseeBranch sourceBranch;
   private IOseeBranch destBranch;
   private TransactionToken transactionId;
   private TransactionToken commitTrans;
   private boolean showConflicts;
   private BranchId mergeBranch;

   public static void openView(final BranchId sourceBranch, final BranchId destBranch, final TransactionRecord tranId) {
      if (Conditions.allNull(sourceBranch, destBranch, tranId)) {
         throw new IllegalArgumentException("Branch's and Transaction ID can't be null");
      }
      openViewUpon(sourceBranch, destBranch, tranId, null, true);
   }

   public static void openView(final TransactionRecord commitTrans) throws OseeCoreException {
      Conditions.checkNotNull(commitTrans, "Commit Transaction ID");
      openViewUpon(null, null, null, commitTrans, true);
   }

   private static void openViewUpon(final BranchId sourceBranch, final BranchId destBranch, final TransactionToken tranId, final TransactionToken commitTrans, final boolean showConflicts) {
      Job job = new Job("Open Merge View") {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  try {
                     IWorkbenchPage page = AWorkbench.getActivePage();
                     IViewPart viewPart = page.showView(MergeView.VIEW_ID,
                        String.valueOf(
                           sourceBranch != null ? sourceBranch.getUuid() * 100000 + destBranch.getUuid() : commitTrans.getId()),
                        IWorkbenchPage.VIEW_ACTIVATE);
                     if (viewPart instanceof MergeView) {
                        MergeView mergeView = (MergeView) viewPart;
                        mergeView.showConflicts = showConflicts;
                        mergeView.explore(sourceBranch, destBranch, tranId, commitTrans, showConflicts);
                     }
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            });

            monitor.done();
            return Status.OK_STATUS;
         }
      };

      Jobs.startJob(job);
   }

   private Conflict[] getConflicts() {
      return mergeXWidget != null ? mergeXWidget.getConflicts() : MergeXViewer.EMPTY_CONFLICTS;
   }

   @Override
   public void dispose() {
      super.dispose();
      OseeEventManager.removeListener(this);
      OseeEventManager.removeListener(this);
   }

   @Override
   public void createPartControl(Composite parent) {
      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));
      mergeXWidget = new MergeXWidget();
      mergeXWidget.setDisplayLabel(false);
      mergeXWidget.createWidgets(parent, 1);

      OseeStatusContributionItemFactory.addTo(this, true);
      //      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView", menuManager,
      //         mergeXWidget.getXViewer());

      getSite().setSelectionProvider(mergeXWidget.getXViewer());
      HelpUtil.setHelp(parent, OseeHelpContext.MERGE_MANAGER);

      OseeEventManager.addListener(this);

      setFocusWidget(mergeXWidget.getControl());
   }

   public void explore(final BranchId sourceBranch, final BranchId destBranch, final TransactionToken transactionId, final TransactionToken commitTrans, boolean showConflicts) {
      this.sourceBranch = BranchManager.getBranchToken(sourceBranch);
      this.destBranch = BranchManager.getBranchToken(destBranch);
      this.transactionId = transactionId;
      this.commitTrans = commitTrans;
      try {
         mergeBranch = BranchManager.getMergeBranch(sourceBranch, destBranch);
         if (mergeBranch == null) {
            close();
         } else {
            mergeXWidget.setInputData(this.sourceBranch, this.destBranch, transactionId, this, commitTrans,
               showConflicts);
            if (sourceBranch != null) {
               setPartName(
                  "Merge Manager: " + this.sourceBranch.getShortName() + " <=> " + this.destBranch.getShortName());
            } else if (commitTrans != null) {
               setPartName("Merge Manager: " + commitTrans.getId());
            } else {
               setPartName("Merge Manager");
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         Long sourceBranchId = null;
         Long destBranchId = null;

         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               String commitTx = memento.getString(COMMIT_NUMBER);
               if (commitTx != null) {
                  openViewUpon(null, null, null, TransactionManager.getTransaction(Long.parseLong(commitTx)), false);
                  return;
               }
               sourceBranchId = Long.parseLong(memento.getString(SOURCE_BRANCH_ID));
               final BranchId sourceBranch = BranchId.valueOf(sourceBranchId);
               if (sourceBranch == null) {
                  OseeLog.log(Activator.class, Level.WARNING,
                     "Merge View can't init due to invalid source branch uuid " + sourceBranchId);
                  mergeXWidget.setLabel("Could not restore this Merge View");
                  return;
               }
               destBranchId = Long.parseLong(memento.getString(DEST_BRANCH_ID));
               final BranchId destBranch = BranchId.valueOf(destBranchId);
               if (destBranch == null) {
                  OseeLog.log(Activator.class, Level.WARNING,
                     "Merge View can't init due to invalid destination branch uuid " + sourceBranchId);
                  mergeXWidget.setLabel("Could not restore this Merge View");
                  return;
               }
               try {
                  TransactionToken transactionId =
                     TransactionManager.getTransaction(Long.parseLong(memento.getString(TRANSACTION_NUMBER)));
                  openViewUpon(sourceBranch, destBranch, transactionId, null, false);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.WARNING,
                     "Merge View can't init due to invalid transaction id " + transactionId);
                  mergeXWidget.setLabel(
                     "Could not restore this Merge View due to invalid transaction id " + transactionId);
                  return;
               }
            } else {
               SkynetViews.closeView(VIEW_ID, getViewSite().getSecondaryId());
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.WARNING, "Merge View error on init", ex);
      }
   }

   private static final String INPUT = "input";
   private static final String SOURCE_BRANCH_ID = "sourceBranchId";
   private static final String DEST_BRANCH_ID = "destBranchId";
   private static final String TRANSACTION_NUMBER = "transactionNumber";
   private static final String COMMIT_NUMBER = "commitTransactionNumber";

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      if (commitTrans != null || sourceBranch != null || destBranch != null || transactionId != null) {
         try {
            IMemento childMemento = memento.createChild(INPUT);
            if (commitTrans != null) {
               childMemento.putString(COMMIT_NUMBER, commitTrans.getIdString());
            }
            if (sourceBranch != null) {
               childMemento.putString(SOURCE_BRANCH_ID, sourceBranch.getIdString());
            }
            if (destBranch != null) {
               childMemento.putString(DEST_BRANCH_ID, destBranch.getIdString());
            }
            if (transactionId != null) {
               childMemento.putString(TRANSACTION_NUMBER, transactionId.getIdString());
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, "Merge View save state error", ex);
         }
      }
   }

   protected void showConflicts(boolean show) {
      showConflicts = show;
   }

   private boolean isApplicableEvent(BranchId branch, BranchId mergeBranch) {
      return mergeBranch.equals(branch) || isApplicableSourceOrDestEvent(branch);
   }

   private boolean isApplicableSourceOrDestEvent(Id branch) {
      return branch.matches(sourceBranch, destBranch);
   }

   protected BranchId getMergeBranchForView() {
      return mergeBranch;
   }

   @Override
   public void handleBranchEvent(final Sender sender, final BranchEvent branchEvent) {

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            switch (branchEvent.getEventType()) {
               case Deleting:
               case Purging:
               case Committing:
               case Deleted:
                  break;
               case Purged:
                  if (mergeBranch.equals(branchEvent.getSourceBranch())) {
                     close();
                  }
               case Committed:
                  if (isApplicableSourceOrDestEvent(branchEvent.getSourceBranch())) {
                     getSite().getPage().hideView(MergeView.this);
                  }
                  break;
               default:
                  if (isApplicableSourceOrDestEvent(
                     branchEvent.getSourceBranch()) && mergeXWidget != null && Widgets.isAccessible(
                        mergeXWidget.getXViewer().getTree())) {
                     mergeXWidget.refresh();
                  }
                  break;
            }
         }
      });
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   private void close() {
      dispose();
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      page.hideView(this);
   }

   private boolean isDisposed() {
      return Conditions.anyNull(mergeXWidget.getXViewer(),
         mergeXWidget.getXViewer().getTree()) || mergeXWidget.getXViewer().getTree().isDisposed();
   }

   private boolean conflictInvovlesArtifact(Artifact artifact, Conflict conflict) {
      if (artifact.equals(conflict.getArtId())) {
         BranchId branch = artifact.getBranch();
         return branch.equals(conflict.getSourceBranch()) || branch.equals(conflict.getDestBranch());
      }
      return false;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, final Sender sender) {
      if (isDisposed()) {
         OseeEventManager.removeListener(this);
         return;
      }
      BranchId mergeBranch = null;
      try {
         mergeBranch = BranchManager.getMergeBranch(sourceBranch, destBranch);
         if (mergeBranch == null || mergeBranch.notEqual(artifactEvent.getBranch())) {
            return;
         }
         if (!isApplicableEvent(artifactEvent.getBranch(), mergeBranch)) {
            return;
         }
      } catch (OseeCoreException ex1) {
         // Do nothing here
      }
      final Collection<Artifact> modifiedArts =
         artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Reloaded);
      final Collection<EventBasicGuidArtifact> deletedPurgedArts = artifactEvent.get(EventModType.Deleted);
      if (modifiedArts.isEmpty() && deletedPurgedArts.isEmpty()) {
         return;
      }
      final MergeView mergeView = this;
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (isDisposed()) {
               return;
            }
            for (Artifact artifact : modifiedArts) {
               try {
                  BranchId branch = artifact.getBranch();
                  if (showConflicts) {
                     Conflict[] conflicts = getConflicts();
                     for (Conflict conflict : conflicts) {
                        if (conflictInvovlesArtifact(artifact, conflict)) {
                           mergeXWidget.setInputData(sourceBranch, destBranch, transactionId, mergeView, commitTrans,
                              "Source Artifact Changed", showConflicts);
                           if (artifact.equals(conflict.getSourceArtifact()) && sender.isLocal()) {
                              new MessageDialog(Displays.getActiveShell().getShell(),
                                 "Modifying Source artifact while merging", null,
                                 "Typically changes done while merging should be done on the merge branch.  You should not normally merge on the source branch.",
                                 2, new String[] {"OK"}, 1).open();
                           }
                           return;
                        } else if (artifact.equals(conflict.getArtifact())) {
                           conflict.computeEqualsValues();
                           mergeXWidget.refresh();
                        }
                     }
                     if (conflicts.length > 0 && (branch.equals(conflicts[0].getSourceBranch()) || branch.equals(
                        conflicts[0].getDestBranch()))) {
                        mergeXWidget.setInputData(sourceBranch, destBranch, transactionId, mergeView, commitTrans,
                           branch.equals(
                              conflicts[0].getSourceBranch()) ? "Source Branch Changed" : "Destination Branch Changed",
                           showConflicts);
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            if (!deletedPurgedArts.isEmpty()) {
               try {
                  EventBasicGuidArtifact artifact = deletedPurgedArts.iterator().next();
                  Conflict[] conflicts = getConflicts();
                  boolean isOnSource = artifact.isOnBranch(conflicts[0].getSourceBranch());
                  if (conflicts.length > 0 && (isOnSource || artifact.isOnBranch(conflicts[0].getDestBranch()))) {
                     mergeXWidget.setInputData(sourceBranch, destBranch, transactionId, mergeView, commitTrans,
                        isOnSource ? "Source Branch Changed" : "Destination Branch Changed", showConflicts);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }

      });

   }

}
