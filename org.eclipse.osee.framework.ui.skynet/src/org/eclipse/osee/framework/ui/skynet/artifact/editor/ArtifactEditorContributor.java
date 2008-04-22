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
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.SelectionCountChangeListener;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.StatusLineContributionItem;

/**
 * Manages the installation/deinstallation of global actions for multi-page editors. Responsible for the redirection of
 * global actions to the active editor. Multi-page contributor replaces the contributors for the individual editors in
 * the multi-page editor.
 */
public class ArtifactEditorContributor extends MultiPageEditorActionBarContributor {

   private StatusLineContributionItem typeStatusItem;
   private ShowInExplorerAction showInExplorerAction;

   /**
    * Creates a multi-page contributor.
    */
   public ArtifactEditorContributor() {
      super();

      createActions();
   }

   public void setActiveEditor(IEditorPart part) {
      super.setActiveEditor(part);
      if (part instanceof ArtifactEditor) {
         ArtifactEditor artifactEditor = (ArtifactEditor) part;
         Artifact artifact = artifactEditor.getEditorInput().getArtifact();
         typeStatusItem.setText(artifact.getArtifactType().getName());
         typeStatusItem.setImage(artifact.getArtifactType().getImage());
         showInExplorerAction.setArtifact(artifact);

         artifactEditor.getRelationsComposite().getTreeViewer().addSelectionChangedListener(
               new SelectionCountChangeListener(this.getActionBars().getStatusLineManager()));
      }
   }

   public void setActivePage(IEditorPart part) {
   }

   private void createActions() {
      typeStatusItem = new StatusLineContributionItem("skynet.artifactType", true, 25);
      typeStatusItem.setToolTipText("The type of the artifact being edited.");

      showInExplorerAction = new ShowInExplorerAction();
   }

   @Override
   public void contributeToStatusLine(IStatusLineManager statusLineManager) {
      statusLineManager.add(typeStatusItem);
      SkynetContributionItem.addTo(statusLineManager);
   }

   @Override
   public void contributeToCoolBar(ICoolBarManager coolBarManager) {
      coolBarManager.add(showInExplorerAction);
   }

   private static class ShowInExplorerAction extends Action {
      private Artifact artifact;

      public ShowInExplorerAction() {
         setText("Show in Artifact Explorer");
         setToolTipText("Show the Artifact being edited in the Artifact Explorer");
      }

      public void setArtifact(Artifact artifact) {
         this.artifact = artifact;
      }

      @Override
      public void run() {
         ArtifactExplorer.revealArtifact(artifact);
      }
   }
}
