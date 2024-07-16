/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.access.AccessControlData;
import org.eclipse.osee.framework.core.access.AccessTopicEventPayload;
import org.eclipse.osee.framework.core.client.AccessTopicEvent;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.event.EventUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * TopicHandler for {@link AccessTopicEvent.ACCESS_BRANCH_MODIFIED}
 *
 * @author Shawn F. Cook
 */
public class XWorkingBranchButtonLock extends XWorkingBranchButtonAbstract implements EventHandler {

   public static String WIDGET_NAME = "XWorkingBranchButtonLock";

   @Override
   protected void initButton(final Button button) {
      button.setToolTipText("Toggle Working Branch Access Control");
      button.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            toggleWorkingBranchLock(button);
         }
      });
   }

   @Override
   protected void refreshEnablement(Button button) {
      button.setEnabled(
         !disableAll && isWorkingBranchInWork() && !isCommittedBranchExists() && isWidgetAllowedInCurrentState());
      refreshLockImage(button);
   }

   private void refreshLockImage(Button button) {
      boolean noBranch = false, someAccessControlSet = false;
      BranchId branch = BranchId.SENTINEL;
      try {
         branch = getTeamArt().getWorkingBranch();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      // just show normal icon if no branch yet
      if (branch.isInvalid()) {
         noBranch = true;
      } else {
         someAccessControlSet =
            !ServiceUtil.getOseeClient().getAccessControlService().getAccessControlList(branch).isEmpty();
      }
      button.setImage(ImageManager.getImage(
         noBranch || someAccessControlSet ? FrameworkImage.LOCK_LOCKED : FrameworkImage.LOCK_UNLOCKED));
      button.redraw();
      button.getParent().redraw();
   }

   private void toggleWorkingBranchLock(Button button) {
      try {
         BranchId branch = getTeamArt().getWorkingBranch();
         if (branch.isInvalid()) {
            AWorkbench.popup("Working branch doesn't exist");
            return;
         }
         boolean isLocked = false, manuallyLocked = false;
         Collection<AccessControlData> datas =
            AtsApiService.get().getAccessControlService().getAccessControlList(branch);
         if (datas.size() > 1) {
            manuallyLocked = true;
         } else if (datas.isEmpty()) {
            isLocked = false;
         } else {
            AccessControlData data = datas.iterator().next();
            if (data.getSubject().equals(CoreUserGroups.Everyone) && data.getBranchPermission().equals(
               PermissionEnum.READ)) {
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
               AtsApiService.get().getAccessControlService().removeAccessControlDataIf(true, datas.iterator().next());
            } else {
               IUserGroup everyoneGroup = AtsApiService.get().userService().getUserGroup(CoreUserGroups.Everyone);
               Conditions.assertTrue(everyoneGroup.getArtifact() instanceof Artifact, "Must be Artifact");
               AtsApiService.get().getAccessControlService().setPermission(everyoneGroup.getArtifact(), branch,
                  PermissionEnum.READ);
            }
            AWorkbench.popup(String.format("Branch set to [%s]", !isLocked ? "Locked" : "NOT Locked"));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void handleEvent(org.osgi.service.event.Event event) {
      BranchId branch = getTeamArt().getWorkingBranch();
      if (branch.isValid()) {
         AccessTopicEventPayload accessEvent = EventUtil.getTopicJson(event, AccessTopicEventPayload.class);
         if (branch.equals(accessEvent.getBranch())) {
            refreshWorkingBranchWidget();
         }
      }
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      BundleContext context = AtsApiService.get().getEventService().getBundleContext(Activator.PLUGIN_ID);
      context.registerService(EventHandler.class.getName(), this,
         AtsUtil.hashTable(EventConstants.EVENT_TOPIC, AccessTopicEvent.ACCESS_BRANCH_MODIFIED.getTopic()));

   }

   @Override
   protected boolean isWidgetAllowedInCurrentState() {
      return isWidgetInState(WIDGET_NAME);
   }

}
