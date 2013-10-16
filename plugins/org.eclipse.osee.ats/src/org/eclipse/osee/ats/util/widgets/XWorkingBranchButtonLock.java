/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SystemGroup;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Shawn F. Cook
 */
public class XWorkingBranchButtonLock extends XWorkingBranchButtonAbstract implements IAccessControlEventListener {

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
      button.setEnabled(!disableAll && isWorkingBranchInWork() && !isCommittedBranchExists());
      refreshLockImage(button);
   }

   private void refreshLockImage(Button button) {
      boolean noBranch = false, someAccessControlSet = false;
      Branch branch = null;
      try {
         branch = getTeamArt().getWorkingBranch();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      // just show normal icon if no branch yet
      if (branch == null) {
         noBranch = true;
      } else {
         someAccessControlSet = !AccessControlManager.getAccessControlList(branch).isEmpty();
      }
      button.setImage(ImageManager.getImage((noBranch || someAccessControlSet) ? FrameworkImage.LOCK_LOCKED : FrameworkImage.LOCK_UNLOCKED));
      button.redraw();
      button.getParent().redraw();
   }

   private void toggleWorkingBranchLock(Button button) {
      try {
         Branch branch = getTeamArt().getWorkingBranch();
         if (branch == null) {
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
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEvent accessControlEvent) {
      if (accessControlEvent.getEventType() == AccessControlEventType.BranchAccessControlModified) {
         refreshWorkingBranchWidget();
      }
   }

}
