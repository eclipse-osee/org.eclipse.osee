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

import java.util.logging.Level;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Commands;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * @author Roberto E. Escobar
 */
public class MergeCustomMenu extends XViewerCustomMenu {

   private IWorkbenchPartSite getSite() {
      return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();
   }

   @Override
   protected void setupMenuForTable() {
      MenuManager menuManager = xViewer.getMenuManager();

      addEditArtifactMenuItem(menuManager);
      addMergeMenuItem(menuManager);
      menuManager.add(new Separator());
      addMarkAsResolvedMenuItem(menuManager);
      addMarkAsUnResolvedMenuItem(menuManager);
      menuManager.add(new Separator());
      addSourceAsMergeValueMenuItem(menuManager);
      addDestinationAsMergeValueMenuItem(menuManager);
      menuManager.add(new Separator());
      addResetConflictMenuItem(menuManager);
      menuManager.add(new Separator());
      addPreviewMenuItem(menuManager);
      addDiffMenuItem(menuManager);
      menuManager.add(new Separator());
      addSourceResourceHistoryMenuItem(menuManager);
      addSourceRevealMenuItem(menuManager);
      menuManager.add(new Separator());
      addDestResourceHistoryMenuItem(menuManager);
      addDestRevealMenuItem(menuManager);

      try {
         if (AccessControlManager.isOseeAdmin()) {
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            addRevertUnresolvableConflictsMenuItem(menuManager);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      super.setupMenuForTable();
   }

   private String addPreviewItems(MenuManager subMenuManager, String command) {
      CommandContributionItem previewCommand =
         Commands.getLocalCommandContribution(getSite(), subMenuManager.getId() + command, command, null, null,
            ImageManager.getImageDescriptor(FrameworkImage.PREVIEW_ARTIFACT), null, null, null);
      subMenuManager.add(previewCommand);
      return previewCommand.getId();
   }

   private void addPreviewMenuItem(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Preview", "previewTransaction");
      menuManager.add(subMenuManager);
      addPreviewItems(subMenuManager, "Preview Source Artifact");
      addPreviewItems(subMenuManager, "Preview Destination Artifact");
      addPreviewItems(subMenuManager, "Preview Merge Artifact");
   }

   private void addDiffMenuItem(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Differences", "diffTransaction");
      menuManager.add(subMenuManager);
      addDiffItems(subMenuManager, "Show Source Branch Differences");
      addDiffItems(subMenuManager, "Show Destination Branch Differences");
      addDiffItems(subMenuManager, "Show Source/Destination Differences");
      addDiffItems(subMenuManager, "Show Source/Merge Differences");
      addDiffItems(subMenuManager, "Show Destination/Merge Differences");
   }

   private String addDiffItems(MenuManager subMenuManager, String command) {
      CommandContributionItem diffCommand =
         Commands.getLocalCommandContribution(getSite(), subMenuManager.getId() + command, command, null, null, null,
            null, null, null);
      subMenuManager.add(diffCommand);
      return diffCommand.getId();
   }

   private String addEditArtifactMenuItem(MenuManager menuManager) {
      CommandContributionItem editArtifactCommand =
         Commands.getLocalCommandContribution(getSite(), "editArtifactCommand", "Edit Merge Artifact", null, null,
            null, "E", null, "edit_Merge_Artifact");
      menuManager.add(editArtifactCommand);
      return editArtifactCommand.getId();
   }

   private String addSourceResourceHistoryMenuItem(MenuManager menuManager) {
      CommandContributionItem sourecResourceCommand =
         Commands.getLocalCommandContribution(getSite(), "sourceResourceHistory",
            "Show Source Artifact Resource History", null, null,
            ImageManager.getImageDescriptor(FrameworkImage.DB_ICON_BLUE_EDIT), null, null, "source_Resource_History");
      menuManager.add(sourecResourceCommand);
      return sourecResourceCommand.getId();
   }

   private String addDestResourceHistoryMenuItem(MenuManager menuManager) {
      CommandContributionItem sourecResourceCommand =
         Commands.getLocalCommandContribution(getSite(), "destResourceHistory", "Show Dest Artifact Resource History",
            null, null, ImageManager.getImageDescriptor(FrameworkImage.DB_ICON_BLUE_EDIT), null, null,
            "dest_Resource_History");
      menuManager.add(sourecResourceCommand);
      return sourecResourceCommand.getId();
   }

   private String addSourceRevealMenuItem(MenuManager menuManager) {
      CommandContributionItem sourceReveal =
         Commands.getLocalCommandContribution(getSite(), "sourceRevealArtifactExplorer",
            "Reveal Source Artifact in Artifact Explorer", null, null,
            ImageManager.getImageDescriptor(FrameworkImage.MAGNIFY), null, null, "source_Reveal");
      menuManager.add(sourceReveal);
      return sourceReveal.getId();
   }

   private String addDestRevealMenuItem(MenuManager menuManager) {
      CommandContributionItem destReveal =
         Commands.getLocalCommandContribution(getSite(), "destRevealArtifactExplorer",
            "Reveal Dest Artifact in Artifact Explorer", null, null,
            ImageManager.getImageDescriptor(FrameworkImage.MAGNIFY), null, null, "dest_Reveal");
      menuManager.add(destReveal);
      return destReveal.getId();
   }

   private String addRevertUnresolvableConflictsMenuItem(MenuManager menuManager) {
      CommandContributionItem revertSelected =
         Commands.getLocalCommandContribution(getSite(), "revertSelected",
            "Revert Source Artifacts for Unresolvable Conflicts", null, null, null, null, null, null);
      menuManager.add(revertSelected);
      return revertSelected.getId();
   }

   private String addMarkAsResolvedMenuItem(MenuManager menuManager) {
      CommandContributionItem markAsResolvedSelected =
         Commands.getLocalCommandContribution(getSite(), "markAsResolvedSelected", "Mark as Resolved", null, null,
            null, null, null, null);
      menuManager.add(markAsResolvedSelected);
      return markAsResolvedSelected.getId();
   }

   private String addMarkAsUnResolvedMenuItem(MenuManager menuManager) {
      CommandContributionItem markAsUnResolvedSelected =
         Commands.getLocalCommandContribution(getSite(), "markAsUnResolvedSelected", "Mark as Unresolved", null, null,
            null, null, null, null);
      menuManager.add(markAsUnResolvedSelected);
      return markAsUnResolvedSelected.getId();
   }

   private String addSourceAsMergeValueMenuItem(MenuManager menuManager) {
      CommandContributionItem mergeValueSourcePickerSelected =
         Commands.getLocalCommandContribution(getSite(), "mergeValueSourcePickerSelected",
            "Resolve using Source Value", null, null, null, null, null, null);
      menuManager.add(mergeValueSourcePickerSelected);
      return mergeValueSourcePickerSelected.getId();
   }

   private String addDestinationAsMergeValueMenuItem(MenuManager menuManager) {
      CommandContributionItem mergeValueDestinationPickerSelected =
         Commands.getLocalCommandContribution(getSite(), "mergeValueDestinationPickerSelected",
            "Resolve using Destination Value", null, null, null, null, null, null);
      menuManager.add(mergeValueDestinationPickerSelected);
      return mergeValueDestinationPickerSelected.getId();
   }

   private String addResetConflictMenuItem(MenuManager menuManager) {
      CommandContributionItem resetConflictMenuItem =
         Commands.getLocalCommandContribution(getSite(), "resetConflictMenuItem", "Reset Conflict", null, null, null,
            null, null, null);
      menuManager.add(resetConflictMenuItem);
      return resetConflictMenuItem.getId();
   }

   private String addMergeMenuItem(MenuManager menuManager) {
      CommandContributionItem mergeArtifactCommand =
         Commands.getLocalCommandContribution(getSite(), "mergeArtifactCommand",
            "Generate Three Way Merge (Developmental)", null, null, null, "E", null,
            "Merge_Source_Destination_Artifact");
      menuManager.add(mergeArtifactCommand);
      return mergeArtifactCommand.getId();
   }
}
