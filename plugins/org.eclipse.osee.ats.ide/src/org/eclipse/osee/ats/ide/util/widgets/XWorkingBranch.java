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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.branch.AtsBranchManager;
import org.eclipse.osee.ats.ide.branch.AtsBranchUtil;
import org.eclipse.osee.ats.ide.editor.header.WfeTargetedVersionHeader;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEventPayload;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.util.RebaselineInProgressHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * TopicHandler for {@link AccessTopicEvent.ACCESS_BRANCH_MODIFIED}
 *
 * @author Megumi Telles
 * @author Donald G. Dunne
 */
public class XWorkingBranch extends GenericXWidget implements IArtifactWidget, IArtifactEventListener, IBranchEventListener, EventHandler {

   private TeamWorkFlowArtifact teamArt;
   private Button createBranchButton;
   private Button showArtifactExplorer;
   private Button showChangeReport;
   private Button updateWorkingBranch;
   private Button deleteBranchButton;
   private Button favoriteBranchButton;
   private Button lockBranchButton;
   private Button abandonMergeButton;
   private XWorkingBranchEnablement enablement;
   public static String NAME = "Working Branch";
   public static String WIDGET_NAME = "XWorkingBranch";

   private Composite buttonComp;

   public XWorkingBranch() {
      super(NAME);
      OseeEventManager.addListener(this);
   }

   @Override
   public TeamWorkFlowArtifact getArtifact() {
      return teamArt;
   }
   private static final class UserConflictResolver extends ConflictResolverOperation {

      public UserConflictResolver() {
         super("Launch Merge Manager", Activator.PLUGIN_ID);
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         // do nothing
      }
   }

   private boolean mergeInProgress() {
      if (BranchManager.hasMergeBranches(teamArt.getWorkingBranch())) {
         return true;
      } else {
         return false;
      }
   }

   public void resetAbandon() {
      abandonMergeButton.setText("");
      abandonMergeButton.getParent().layout();
      enablement.refresh();
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }
      Composite mainComp = new Composite(parent, SWT.NONE);
      mainComp.setLayout(new GridLayout(1, false));
      mainComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (toolkit != null) {
         toolkit.adapt(mainComp);
      }
      if (!getLabel().equals("")) {
         labelWidget = new Label(mainComp, SWT.NONE);
         labelWidget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      }

      buttonComp = new Composite(mainComp, SWT.NONE);
      buttonComp.setLayout(new GridLayout(8, false));
      buttonComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (toolkit != null) {
         toolkit.adapt(buttonComp);
      }

      createBranchButton = createNewButton(buttonComp);
      createBranchButton.setToolTipText("Create Working Branch");
      createBranchButton.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            if (!Widgets.isAccessible(createBranchButton)) {
               return;
            }
            try {
               enablement.disableAll();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            refreshEnablement();
            // Create working branch
            Result result = AtsBranchUtil.createWorkingBranch_Validate(teamArt);
            boolean appropriate = selectTargetedVersionIfAppropriate(result);
            if (appropriate) {
               return;
            }
            if (result.isFalse()) {
               AWorkbench.popup(result);
               enablement.refresh();
               refreshEnablement();
               return;
            }
            try {
               String parentBranchName = AtsClientService.get().getBranchService().getBranchName(teamArt);
               // Retrieve parent branch to create working branch from
               if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Create Working Branch",
                  "Create a working branch from parent branch\n\n\"" + parentBranchName + "\"?\n\n" + "NOTE: Working branches are necessary when OSEE Artifact changes " + "are made during implementation.")) {
                  enablement.refresh();
                  refreshEnablement();
                  return;
               }
               if (!Widgets.isAccessible(createBranchButton)) {
                  return;
               }
               createBranchButton.setText("Creating Branch...");
               createBranchButton.getParent().layout();
               AtsBranchUtil.createWorkingBranch_Create(teamArt, false);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               enablement.refresh();
               refreshEnablement();
            }
         }

         private boolean selectTargetedVersionIfAppropriate(Result result) {
            boolean returnVal = false;
            if (result.getText().equals(AtsBranchUtil.PARENT_BRANCH_CAN_NOT_BE_DETERMINED)) {
               returnVal = true;
               IAtsVersion version = AtsClientService.get().getVersionService().getTargetedVersion(teamArt);
               if (version == null) {
                  MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Create Working Branch", null,
                     AtsBranchUtil.PARENT_BRANCH_CAN_NOT_BE_DETERMINED, MessageDialog.ERROR,
                     new String[] {"Select Targeted Version", "Cancel"}, 0);
                  if (dialog.open() == 0) {
                     if (!WfeTargetedVersionHeader.chooseVersion(teamArt)) {
                        enablement.refresh();
                        refreshEnablement();
                     }
                  } else {
                     enablement.refresh();
                     refreshEnablement();
                  }
               }
            }
            return returnVal;
         }
      });

      updateWorkingBranch = createNewButton(buttonComp);
      updateWorkingBranch.setToolTipText("Update Working Branch from targeted version or team configured branch");
      updateWorkingBranch.setImage(ImageManager.getImage(FrameworkImage.BRANCH_SYNCH));

      updateWorkingBranch.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            try {
               IOseeBranch branchToUpdate = teamArt.getWorkingBranch();
               if (branchToUpdate != null) {
                  Artifact associatedArtifact = BranchManager.getAssociatedArtifact(branchToUpdate);
                  IAtsWorkItem workItem = AtsClientService.get().getWorkItemService().getWorkItem(associatedArtifact);
                  if (workItem == null || !workItem.isTeamWorkflow()) {
                     AWorkbench.popup("Working Branch must have associated Team Workflow");
                     return;
                  }
                  IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
                  BranchId targetedBranch =
                     AtsClientService.get().getBranchService().getConfiguredBranchForWorkflow(teamWf);
                  if (BranchManager.isUpdatable(branchToUpdate)) {
                     if (BranchManager.getState(branchToUpdate).isRebaselineInProgress()) {
                        RebaselineInProgressHandler.handleRebaselineInProgress(branchToUpdate);
                     } else {

                        boolean isUserSure = MessageDialog.openQuestion(
                           PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Update Branch",
                           String.format(
                              "Are you sure you want to update [%s]\n branch from Targeted Version or Team Configured branch [%s]?",
                              branchToUpdate.getName(), BranchManager.getBranch(targetedBranch).getName()));
                        if (isUserSure) {
                           BranchManager.updateBranch(branchToUpdate, targetedBranch, new UserConflictResolver());
                        }
                     }
                  } else {
                     MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Can't Update Branch",
                        String.format(
                           "Couldn't update [%s] because it currently has merge branches from commits.  " //
                              + "To perform an update please delete all the merge branches for this branch.",
                           branchToUpdate.getName()));
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });

      showArtifactExplorer = createNewButton(buttonComp);
      showArtifactExplorer.setToolTipText("Show Artifact Explorer");
      showArtifactExplorer.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            try {
               ArtifactExplorer.exploreBranch(teamArt.getWorkingBranch());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

      showChangeReport = createNewButton(buttonComp);
      showChangeReport.setToolTipText("Show Change Report");
      showChangeReport.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            AtsBranchManager.showChangeReport(teamArt);
         }
      });

      abandonMergeButton = createNewButton(buttonComp);
      abandonMergeButton.setToolTipText("Delete Merge Branch(es)");
      abandonMergeButton.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            if (!Widgets.isAccessible(abandonMergeButton)) {
               return;
            }
            try {
               enablement.disableAll();

               refreshEnablement();
               abandonMergeButton.setText("Delete Merge Branch(es)...");
               abandonMergeButton.getParent().layout();

               boolean hasMergeBranch = BranchManager.hasMergeBranches(teamArt.getWorkingBranch());
               if (hasMergeBranch) {
                  List<MergeBranch> br = BranchManager.getMergeBranches(teamArt.getWorkingBranch());
                  if (br.size() > 1) {
                     AWorkbench.popup(
                        "The Working Branch: " + teamArt.getWorkingBranch() + " has more than 1 merge branch. Manually open the merge manager to cancel merge");
                  } else {
                     BranchManager.deleteBranch(br.get(0));
                     AWorkbench.popup("Merge Branch(es) Deleted");
                  }
                  resetAbandon();
               }

            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               resetAbandon();
            }
         }
      });

      deleteBranchButton = createNewButton(buttonComp);
      deleteBranchButton.setToolTipText("Delete Working Branch");
      deleteBranchButton.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            if (!Widgets.isAccessible(deleteBranchButton)) {
               return;
            }
            try {
               enablement.disableAll();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            refreshEnablement();
            deleteBranchButton.setText("Deleting Branch...");
            deleteBranchButton.getParent().layout();
            boolean deleted = AtsBranchManager.deleteWorkingBranch(teamArt, true, false);
            if (!deleted) {
               deleteBranchButton.setText("");
               deleteBranchButton.getParent().layout();
               enablement.refresh();
               refreshEnablement();
            }
         }
      });

      favoriteBranchButton = createNewButton(buttonComp);
      favoriteBranchButton.setToolTipText("Toggle Working Branch as Favorite");
      favoriteBranchButton.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            markWorkingBranchAsFavorite();
         }
      });

      lockBranchButton = createNewButton(buttonComp);
      lockBranchButton.setToolTipText("Toggle Working Branch Access Control");
      lockBranchButton.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            toggleWorkingBranchLock();
         }
      });

      if (!Widgets.isAccessible(createBranchButton)) {
         return;
      }
      createBranchButton.setImage(ImageManager.getImage(FrameworkImage.BRANCH));
      deleteBranchButton.setImage(ImageManager.getImage(FrameworkImage.TRASH));
      abandonMergeButton.setImage(ImageManager.getImage(FrameworkImage.DELETE_MERGE_BRANCHES));
      favoriteBranchButton.setImage(ImageManager.getImage(AtsImage.FAVORITE));
      showArtifactExplorer.setImage(ImageManager.getImage(FrameworkImage.ARTIFACT_EXPLORER));
      showChangeReport.setImage(ImageManager.getImage(FrameworkImage.BRANCH_CHANGE));
      refreshLockImage();
      refreshLabel();
      refreshEnablement();

      BundleContext context = AtsClientService.get().getEventService().getBundleContext(Activator.PLUGIN_ID);
      context.registerService(EventHandler.class.getName(), this,
         AtsUtil.hashTable(EventConstants.EVENT_TOPIC, AccessTopicEvent.ACCESS_BRANCH_MODIFIED.getTopic()));

   }

   private void refreshLockImage() {
      boolean noBranch = false, someAccessControlSet = false;
      BranchId branch = BranchId.SENTINEL;
      try {
         branch = teamArt.getWorkingBranch();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      // just show normal icon if no branch yet
      if (branch.isInvalid()) {
         noBranch = true;
      } else {
         someAccessControlSet = !AccessControlManager.getAccessControlList(branch).isEmpty();
      }
      lockBranchButton.setImage(ImageManager.getImage(
         noBranch || someAccessControlSet ? FrameworkImage.LOCK_LOCKED : FrameworkImage.LOCK_UNLOCKED));
      lockBranchButton.redraw();
      lockBranchButton.getParent().redraw();
   }

   private void markWorkingBranchAsFavorite() {
      try {
         User user = AtsClientService.get().getUserServiceClient().getOseeUser(
            AtsClientService.get().getUserService().getCurrentUser());
         // Make sure we have latest artifact
         user.reloadAttributesAndRelations();
         if (user.isSystemUser()) {
            AWorkbench.popup("Can't set preference as System User = " + user);
            return;
         }
         BranchId branch = teamArt.getWorkingBranch();
         if (branch.isInvalid()) {
            AWorkbench.popup("Working branch doesn't exist");
            return;
         }
         boolean isFavorite = user.isFavoriteBranch(branch);
         String message = String.format("Working branch is currently [%s]\n\nToggle favorite?",
            isFavorite ? "Favorite" : "NOT Favorite");
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Toggle Branch as Favorite", message)) {
            user.toggleFavoriteBranch(branch);
            OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.FavoritesUpdated, branch));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void toggleWorkingBranchLock() {
      try {
         BranchId branch = teamArt.getWorkingBranch();
         if (branch.isInvalid()) {
            AWorkbench.popup("Working branch doesn't exist");
            return;
         }
         boolean isLocked = false, manuallyLocked = false;
         Collection<AccessControlData> datas = AccessControlManager.getAccessControlList(branch);
         if (datas.size() > 1) {
            manuallyLocked = true;
         } else if (datas.isEmpty()) {
            isLocked = false;
         } else {
            AccessControlData data = datas.iterator().next();
            if (data.getSubject().equals(AtsClientService.get().getUserGroupService().getUserGroup(
               CoreUserGroups.Everyone).getArtifact()) && data.getBranchPermission() == PermissionEnum.READ) {
               isLocked = true;
            } else {
               manuallyLocked = true;
            }
         }
         if (manuallyLocked) {
            AWorkbench.popup(
               "Manual access control applied to branch.  Can't override.\n\nUse Access Control option of Branch Manager");
            return;
         }
         String message = String.format("Working branch is currently [%s]\n\n%s the Branch?",
            isLocked ? "Locked" : "NOT Locked", isLocked ? "UnLock" : "Lock");
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Toggle Branch Lock", message)) {
            if (isLocked) {
               AccessControlManager.removeAccessControlDataIf(true, datas.iterator().next());
            } else {
               IUserGroup everyoneGroup =
                  AtsClientService.get().getUserGroupService().getUserGroup(CoreUserGroups.Everyone);
               Conditions.assertTrue(everyoneGroup.getArtifact() instanceof Artifact, "Must be Artifact");
               AccessControlManager.setPermission((Artifact) everyoneGroup.getArtifact(), branch, PermissionEnum.READ);
            }
            AWorkbench.popup(String.format("Branch set to [%s]", !isLocked ? "Locked" : "NOT Locked"));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public Button createNewButton(Composite comp) {
      if (toolkit != null) {
         return toolkit.createButton(comp, null, SWT.PUSH);
      }
      return new Button(comp, SWT.PUSH);
   }

   public void refreshLabel() {
      if (labelWidget != null && Widgets.isAccessible(labelWidget) && !getLabel().equals("")) {
         try {
            IOseeBranch workBranch = enablement.getWorkingBranch();
            String labelStr =
               getLabel() + ": " + enablement.getStatus().getDisplayName() + (workBranch != null && workBranch.isValid() ? " - " + workBranch.getShortName() : "");
            labelWidget.setText(labelStr);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
         labelWidget.getParent().redraw();
         if (getManagedForm() != null) {
            getManagedForm().reflow(true);
         }
      }
   }

   public void refreshEnablement() {
      createBranchButton.setEnabled(enablement.isCreateBranchButtonEnabled());
      updateWorkingBranch.setEnabled(enablement.isUpdateWorkingBranchButtonEnabled());
      showArtifactExplorer.setEnabled(enablement.isShowArtifactExplorerButtonEnabled());
      abandonMergeButton.setEnabled(mergeInProgress());
      showChangeReport.setEnabled(enablement.isShowChangeReportButtonEnabled());
      if (Strings.isValid(deleteBranchButton.getText())) {
         deleteBranchButton.setText("");
         deleteBranchButton.getParent().layout();
      }
      deleteBranchButton.setEnabled(enablement.isDeleteBranchButtonEnabled());
      favoriteBranchButton.setEnabled(enablement.isFavoriteBranchButtonEnabled());
      lockBranchButton.setEnabled(enablement.isDeleteBranchButtonEnabled());
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
   }

   @Override
   public Control getControl() {
      return labelWidget;
   }

   @Override
   public IStatus isValid() {
      // Need this cause it removes all error items of this namespace
      return new Status(IStatus.OK, getClass().getSimpleName(), "");
   }

   public void refreshOnBranchEvent() {
      if (teamArt == null || labelWidget == null || labelWidget.isDisposed()) {
         return;
      }
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            try {
               enablement.refresh();
               enablement.getStatus();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (Widgets.isAccessible(createBranchButton)) {
                     refreshEnablement();
                     refreshLabel();
                     refreshLockImage();
                  }
               }
            });
         }
      };
      Thread thread = new Thread(runnable);
      thread.start();
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         this.teamArt = (TeamWorkFlowArtifact) artifact;
      }
      enablement = new XWorkingBranchEnablement(teamArt);
   }

   @Override
   public String toString() {
      return String.format("%s", getLabel());
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      refreshOnBranchEvent();
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      refreshOnBranchEvent();
   }

   @Override
   public void handleEvent(org.osgi.service.event.Event event) {
      BranchId branch = teamArt.getBranch();
      if (branch != null) {
         AccessTopicEventPayload accessEvent = EventUtil.getTopicJson(event, AccessTopicEventPayload.class);
         if (branch.equals(accessEvent.getBranch())) {
            refreshOnBranchEvent();
         }
      }
   }

}
