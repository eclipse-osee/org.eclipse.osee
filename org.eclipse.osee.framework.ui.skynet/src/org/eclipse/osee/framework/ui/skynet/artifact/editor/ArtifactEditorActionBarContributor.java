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

import java.net.URL;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactURL;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.OpenWithContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorSite;
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

   public void contributeToToolBar(IToolBarManager manager) {
      manager.add(createAtsBugAction());
      manager.add(new OpenOutlineAction());
      manager.add(new OpenHistoryAction());
      manager.add(new RevealInExplorerAction());
      manager.add(new Separator());
      addOpenWithContributionItem(manager);
      manager.add(new DeleteArtifactAction());
      manager.add(new Separator());
      manager.add(new RevealBranchAction());
      manager.add(new Separator());
      manager.add(new AccessControlAction());
      manager.add(new Separator());
      manager.add(new CopyArtifactURLAction());
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
      OpenWithContributionItem contributionItem = new OpenWithContributionItem();
      contributionItem.setVisible(true);
      manager.add(contributionItem);
   }

   private final Action createAtsBugAction() {
      IEditorSite site = editor.getEditorSite();
      return OseeAts.createBugAction(SkynetGuiPlugin.getInstance(), editor, site.getId(), site.getRegisteredName());
   }

   private final class RevealBranchAction extends Action {
      public RevealBranchAction() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("branch.gif"));
         setToolTipText("Reveal the branch this artifact is on in the Branch Manager");
      }

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
         super("&Delete Artifact\tDelete", Action.AS_PUSH_BUTTON);
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("delete.gif"));
      }

      @Override
      public void run() {
         try {
            MessageDialog dialog =
                  new MessageDialog(Display.getCurrent().getActiveShell(), "Confirm Artifact Deletion", null,
                        " Are you sure you want to delete this artifact and all of the default hierarchy children?",
                        MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
            if (dialog.open() == Window.OK) {
               getSelectedArtifact().delete();
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final class OpenHistoryAction extends Action {

      public OpenHistoryAction() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("DBiconBlueEdit.GIF"));
         setToolTipText("Show this artifact in the Resource History");
      }

      public void run() {
         try {
            HistoryView.open(getSelectedArtifact());
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final class RevealInExplorerAction extends Action {
      public RevealInExplorerAction() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("magnify.gif"));
         setToolTipText("Reveal this artifact in the Artifact Explorer");
      }

      public void run() {
         try {
            ArtifactExplorer.revealArtifact(getSelectedArtifact());
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final class AccessControlAction extends Action {
      public AccessControlAction() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("authenticated.gif"));
         setToolTipText("Access Control");
      }

      public void run() {
         try {
            PolicyDialog pd = new PolicyDialog(Display.getCurrent().getActiveShell(), getSelectedArtifact());
            pd.open();
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final class CopyArtifactURLAction extends Action {
      public CopyArtifactURLAction() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("copyToClipboard.gif"));
         setToolTipText("Copy artifact url link to clipboard. NOTE: This is a link pointing to the latest version of the artifact.");
      }

      public void run() {
         if (getSelectedArtifact() != null) {
            Clipboard clipboard = null;
            try {
               URL url = ArtifactURL.getExternalArtifactLink(getSelectedArtifact());
               clipboard = new Clipboard(null);
               clipboard.setContents(new Object[] {url.toString()}, new Transfer[] {TextTransfer.getInstance()});
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, String.format(
                     "Error obtaining url for - guid: [%s] branch:[%s]", getSelectedArtifact().getGuid(),
                     getSelectedArtifact().getBranch()), ex);
            } finally {
               if (clipboard != null && !clipboard.isDisposed()) {
                  clipboard.dispose();
                  clipboard = null;
               }
            }
         }
      }
   }

   private final class OpenOutlineAction extends Action {
      public OpenOutlineAction() {
         super();
         setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("outline_co.gif"));
         setToolTipText("Open Outline");
      }

      public void run() {
         try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                  "org.eclipse.ui.views.ContentOutline");
         } catch (PartInitException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Unable to open outline", ex);
         }
      }
   }

}
