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
package org.eclipse.osee.ats.util.widgets;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
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

/**
 * @author Megumi Telles
 * @author Donald G. Dunne
 */
public class XWorkingBranch extends XWidget implements IArtifactWidget, IAccessControlEventListener, IArtifactEventListener, IBranchEventListener {

   private TeamWorkFlowArtifact teamArt;
   private Button createBranchButton;
   private Button showArtifactExplorer;
   private Button showChangeReport;
   private Button deleteBranchButton;
   private Button favoriteBranchButton;
   private Button lockBranchButton;
   private XWorkingBranchEnablement enablement;

   public static enum BranchStatus {
      Not_Started,
      Changes_InProgress,
      Changes_NotPermitted
   }
   public final static String WIDGET_ID = ATSAttributes.WORKING_BRANCH_WIDGET.getWorkItemId();

   public XWorkingBranch() {
      super("Working Branch", "");
      OseeEventManager.addListener(this);
   }

   @Override
   public TeamWorkFlowArtifact getArtifact() {
      return teamArt;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }
      if (!getLabel().equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
      }

      Composite bComp = new Composite(parent, SWT.NONE);
      bComp.setLayout(new GridLayout(6, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (toolkit != null) {
         toolkit.adapt(bComp);
      }

      createBranchButton = createNewButton(bComp);
      createBranchButton.setToolTipText("Create Working Branch");
      createBranchButton.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            teamArt.getBranchMgr().createWorkingBranch(null, true);
         }
      });

      showArtifactExplorer = createNewButton(bComp);
      showArtifactExplorer.setToolTipText("Show Artifact Explorer");
      showArtifactExplorer.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            try {
               ArtifactExplorer.exploreBranch(teamArt.getWorkingBranch());
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

      showChangeReport = createNewButton(bComp);
      showChangeReport.setToolTipText("Show Change Report");
      showChangeReport.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            teamArt.getBranchMgr().showChangeReport();
         }
      });

      deleteBranchButton = createNewButton(bComp);
      deleteBranchButton.setToolTipText("Delete Working Branch");
      deleteBranchButton.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            teamArt.getBranchMgr().deleteWorkingBranch(true);
            refresh();
         }
      });

      favoriteBranchButton = createNewButton(bComp);
      favoriteBranchButton.setToolTipText("Toggle Working Branch as Favorite");
      favoriteBranchButton.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            markWorkingBranchAsFavorite();
         }
      });

      lockBranchButton = createNewButton(bComp);
      lockBranchButton.setToolTipText("Toggle Working Branch Access Control");
      lockBranchButton.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            toggleWorkingBranchLock();
         }
      });

      if (SkynetGuiPlugin.getInstance().getOseeCmService() != null) {
         createBranchButton.setImage(ImageManager.getImage(FrameworkImage.BRANCH));
         deleteBranchButton.setImage(ImageManager.getImage(FrameworkImage.TRASH));
         favoriteBranchButton.setImage(ImageManager.getImage(AtsImage.FAVORITE));
      }
      if (SkynetGuiPlugin.getInstance() != null) {
         showArtifactExplorer.setImage(ImageManager.getImage(FrameworkImage.ARTIFACT_EXPLORER));
         showChangeReport.setImage(ImageManager.getImage(FrameworkImage.BRANCH_CHANGE));
      }
      refreshLockImage();
      refreshLabel();
      refreshEnablement();
   }

   private void refreshLockImage() {
      if (SkynetGuiPlugin.getInstance() != null) {
         boolean noBranch = false, someAccessControlSet = false;
         Branch branch = null;
         try {
            branch = teamArt.getWorkingBranch();
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
         // just show normal icon if no branch yet
         if (branch == null) {
            noBranch = true;
         } else {
            someAccessControlSet = !AccessControlManager.getAccessControlList(branch).isEmpty();
         }
         lockBranchButton.setImage(ImageManager.getImage((noBranch || someAccessControlSet) ? FrameworkImage.LOCK_LOCKED : FrameworkImage.LOCK_UNLOCKED));
         lockBranchButton.redraw();
         lockBranchButton.getParent().redraw();
      }
   }

   private void markWorkingBranchAsFavorite() {
      try {
         User user = UserManager.getUser();
         if (user.isSystemUser()) {
            AWorkbench.popup("Can't set preference as System User = " + user);
            return;
         }
         Branch branch = teamArt.getWorkingBranch();
         if (branch == null) {
            AWorkbench.popup("Working branch doesn't exist");
            return;
         }
         boolean isFavorite = user.isFavoriteBranch(branch);
         String message =
            String.format("Working branch is currently [%s]\n\nToggle favorite?",
               isFavorite ? "Favorite" : "NOT Favorite");
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Toggle Branch as Favorite", message)) {
            user.toggleFavoriteBranch(branch);
            OseeEventManager.kickBranchEvent(this, new BranchEvent(BranchEventType.FavoritesUpdated, branch.getGuid()),
               branch.getId());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void toggleWorkingBranchLock() {
      try {
         Branch branch = teamArt.getWorkingBranch();
         if (branch == null) {
            AWorkbench.popup("Working branch doesn't exist");
            return;
         }
         boolean isLocked = false, manuallyLocked = false;
         Collection<AccessControlData> datas = AccessControlManager.getAccessControlList(branch);
         if (datas.size() > 1) {
            manuallyLocked = true;
         } else if (datas.size() == 0) {
            isLocked = false;
         } else {
            AccessControlData data = datas.iterator().next();
            if (data.getSubject().equals(SystemGroup.Everyone.getArtifact()) && data.getBranchPermission() == PermissionEnum.READ) {
               isLocked = true;
            } else {
               manuallyLocked = true;
            }
         }
         if (manuallyLocked) {
            AWorkbench.popup("Manual access control applied to branch.  Can't override.\n\nUse Access Control option of Branch Manager");
            return;
         }
         String message =
            String.format("Working branch is currently [%s]\n\n%s the Branch?", isLocked ? "Locked" : "NOT Locked",
               isLocked ? "UnLock" : "Lock");
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Toggle Branch Lock", message)) {
            if (isLocked) {
               AccessControlManager.removeAccessControlDataIf(true, datas.iterator().next());
            } else {
               AccessControlManager.setPermission(SystemGroup.Everyone.getArtifact(), branch, PermissionEnum.READ);
            }
            AccessControlEvent event = new AccessControlEvent();
            event.setEventType(AccessControlEventType.BranchAccessControlModified);
            OseeEventManager.kickAccessControlArtifactsEvent(this, event);
            AWorkbench.popup(String.format("Branch set to [%s]", !isLocked ? "Locked" : "NOT Locked"));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public Button createNewButton(Composite comp) {
      if (toolkit != null) {
         return toolkit.createButton(comp, null, SWT.PUSH);
      }
      return new Button(comp, SWT.PUSH);
   }

   public void refreshLabel() {
      if (!getLabel().equals("")) {
         try {
            labelWidget.setText(getLabel() + ": " + (enablement.getWorkingBranch() != null ? enablement.getWorkingBranch().getShortName() : "") + " " + enablement.getStatus().name());
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }
   }

   public void refreshEnablement() {
      createBranchButton.setEnabled(enablement.isCreateBranchButtonEnabled());
      showArtifactExplorer.setEnabled(enablement.isShowArtifactExplorerButtonEnabled());
      showChangeReport.setEnabled(enablement.isShowChangeReportButtonEnabled());
      deleteBranchButton.setEnabled(enablement.isDeleteBranchButtonEnabled());
      favoriteBranchButton.setEnabled(enablement.isFavoriteBranchButtonEnabled());
      lockBranchButton.setEnabled(enablement.isDeleteBranchButtonEnabled());
   }

   public static boolean isPurgeBranchButtonEnabled(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return teamArt.getBranchMgr().isWorkingBranchInWork();
   }

   @Override
   public void setFocus() {
      // do nothing
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
   public Object getData() {
      return null;
   }

   @Override
   public String getReportData() {
      return null;
   }

   @Override
   public String getXmlData() {
      return null;
   }

   @Override
   public IStatus isValid() {
      // Need this cause it removes all error items of this namespace
      return new Status(IStatus.OK, getClass().getSimpleName(), "");
   }

   @Override
   public void refresh() {
      // don't do anything here cause to expensive to check for branch conditions during every refresh
   }

   public void refreshOnBranchEvent() {
      if (teamArt == null || teamArt.getBranchMgr() == null || labelWidget == null || labelWidget.isDisposed()) {
         return;
      }
      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            enablement.refresh();
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (Widgets.isAccessible(createBranchButton)) {
                     refreshLabel();
                     refreshEnablement();
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
   public void setXmlData(String str) {
      // do nothing
   }

   @Override
   public String toHTML(String labelFont) {
      return "";
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
      if (artifact instanceof TeamWorkFlowArtifact) {
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
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEvent accessControlEvent) {
      if (accessControlEvent.getEventType() == AccessControlEventType.BranchAccessControlModified) {
         refreshOnBranchEvent();
      }
   }

}
