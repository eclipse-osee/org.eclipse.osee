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
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OpenContributionItem;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.action.RevealInExplorerAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.action.CopyArtifactURLAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.action.OpenArtifactInBrowserAction;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditorActionBarContributor implements IActionContributor {

   private final AbstractArtifactEditor editor;

   public ArtifactEditorActionBarContributor(AbstractArtifactEditor editor) {
      this.editor = editor;
   }

   @Override
   public void contributeToToolBar(IToolBarManager manager) {
      addOpenWithContributionItem(manager);
      manager.add(new DeleteArtifactAction());
      manager.add(new Separator());
      manager.add(new OpenOutlineAction());
      manager.add(new OpenHistoryAction());
      Artifact artifact = editor.getArtifactFromEditorInput();
      manager.add(new RevealInExplorerAction(artifact));
      manager.add(new RevealBranchAction());
      manager.add(new Separator());
      manager.add(new AccessControlAction());
      manager.add(new Separator());
      if (CopyArtifactURLAction.isApplicable(artifact)) {
         manager.add(new CopyArtifactURLAction(artifact));
      }
      manager.add(new OpenArtifactInBrowserAction(artifact));
      manager.add(new Separator());
      if (AccessControlManager.isOseeAdmin()) {
         manager.add(new DirtyReportAction(artifact));
      }
   }

   private Artifact getSelectedArtifact() {
      Artifact toReturn = null;

      ISelectionProvider provider = editor.getSite().getSelectionProvider();
      ISelection selection = provider.getSelection();
      if (!selection.isEmpty()) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selection;
         Object selectedObject = structuredSelection.getFirstElement();
         if (selectedObject instanceof Artifact) {
            toReturn = (Artifact) selectedObject;
         }
      }
      return toReturn;
   }

   private void addOpenWithContributionItem(IToolBarManager manager) {
      OpenContributionItem contributionItem = new OpenContributionItem(getClass().getSimpleName() + ".open");
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
            BranchView.revealBranch(getSelectedArtifact().getBranch());
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
            MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Confirm Artifact Deletion", null,
               " Are you sure you want to delete this artifact and all of the default hierarchy children?",
               MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
            if (dialog.open() == Window.OK) {
               getSelectedArtifact().deleteAndPersist();
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final class OpenHistoryAction extends Action {

      public OpenHistoryAction() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DB_ICON_BLUE));
         setToolTipText("Show this artifact in the Resource History");
      }

      @Override
      public void run() {
         try {
            HistoryView.open(getSelectedArtifact());
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
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
            PolicyDialog pd = new PolicyDialog(Displays.getActiveShell(), getSelectedArtifact());
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

}
