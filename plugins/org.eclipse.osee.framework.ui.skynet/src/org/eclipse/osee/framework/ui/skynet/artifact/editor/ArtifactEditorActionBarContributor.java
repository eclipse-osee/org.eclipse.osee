/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.access.AccessControlUtil;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ISelectedArtifacts;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OpenContributionItem;
import org.eclipse.osee.framework.ui.skynet.access.AccessControlDetails;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.action.RevealInExplorerAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.action.CopyArtifactURLAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.action.OpenArtifactInBrowserAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.action.OpenHistoryAction;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XResultDataDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditorActionBarContributor implements IActionContributor, ISelectedArtifacts {

   private final Artifact artifact;

   public ArtifactEditorActionBarContributor(Artifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public void contributeToToolBar(IToolBarManager manager) {
      addOpenWithContributionItem(manager);
      manager.add(new DeleteArtifactAction());
      manager.add(new Separator());
      manager.add(new OpenOutlineAction());
      manager.add(new OpenHistoryAction(artifact));
      manager.add(new RevealInExplorerAction(artifact));
      manager.add(new RevealBranchAction());
      manager.add(new Separator());
      manager.add(new AccessControlAction());
      if (AccessControlUtil.isDebugOn()) {
         manager.add(new AccessControlDetails(artifact));
      }
      manager.add(new Separator());
      manager.add(new CopyArtifactURLAction(artifact));
      manager.add(new OpenArtifactInBrowserAction(artifact));
      manager.add(new Separator());
      if (ServiceUtil.accessControlService().isOseeAdmin()) {
         manager.add(new DirtyReportAction(artifact));
      }
   }

   private void addOpenWithContributionItem(IToolBarManager manager) {
      OpenContributionItem contributionItem = new OpenContributionItem(getClass().getSimpleName() + ".open", this);
      contributionItem.setVisible(true);
      manager.add(contributionItem);
   }

   private final class RevealBranchAction extends Action {
      public RevealBranchAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BRANCH));
         setToolTipText("Reveal the branch this artifact is on in the Branch Manager");
      }

      @Override
      public void run() {
         try {
            BranchView.revealBranch(artifact.getBranch());
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }

   }

   private final class DeleteArtifactAction extends Action {

      public DeleteArtifactAction() {
         super("&Delete Artifact\tDelete", IAction.AS_PUSH_BUTTON);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DELETE));
      }

      @Override
      public void run() {
         try {
            XResultData rd = ServiceUtil.accessControlService().hasArtifactPermission(Collections.singleton(artifact),
               PermissionEnum.WRITE,
               AccessControlArtifactUtil.getXResultAccessHeader("Delete Artifact Failed", artifact));
            if (rd.isErrors()) {
               XResultDataDialog.open(rd, "Delete Artifact Failed", "You do not have valid access to delete %s",
                  artifact.toStringWithId());
               return;
            }

            MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Confirm Artifact Deletion", null,
               String.format(
                  "Are you sure you want to delete the artifact\n\n%s\n\nand all of the default hierarchy children?",
                  artifact.toStringWithId()),
               MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
            if (dialog.open() == Window.OK) {
               artifact.deleteAndPersist(getClass().getSimpleName());
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final class AccessControlAction extends Action {
      public AccessControlAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.AUTHENTICATED));
         setToolTipText("&Access Control");
         setText("&Access Control");
      }

      @Override
      public void run() {
         try {
            PolicyDialog pd = PolicyDialog.createPolicyDialog(Displays.getActiveShell(), artifact);
            pd.open();
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final class OpenOutlineAction extends Action {
      public OpenOutlineAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.OUTLINE));
         setToolTipText("Open Outline");
      }

      @Override
      public void run() {
         try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
               "org.eclipse.ui.views.ContentOutline");
         } catch (PartInitException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Unable to open outline", ex);
         }
      }
   }

   @Override
   public Collection<Artifact> getSelectedArtifacts() {
      return Collections.singleton(artifact);
   }

}
