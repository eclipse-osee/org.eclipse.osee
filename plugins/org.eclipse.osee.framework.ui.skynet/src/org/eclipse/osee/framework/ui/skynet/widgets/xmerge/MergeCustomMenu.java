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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.Commands;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.ConflictHandlingOperation.ConflictOperationEnum;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * @author Roberto E. Escobar
 */
public class MergeCustomMenu extends XViewerCustomMenu {

   private boolean isInitialized = false;

   private IWorkbenchPartSite getSite() {
      return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();
   }

   @Override
   protected void setupMenuForTable() {
      MenuManager menuManager = xViewer.getMenuManager();

      if (!isInitialized) {
         isInitialized = true;
         registerCommandHandlers(xViewer);
      } else {
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
      }
      super.setupMenuForTable();
   }

   private void registerCommandHandlers(XViewer xviewer) {
      MenuManager menuManager = xViewer.getMenuManager();
      IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);

      createEditArtifactMenuItem(menuManager, handlerService);
      createMergeMenuItem(menuManager, handlerService);
      menuManager.add(new Separator());

      createMarkResolvedMenuItem(menuManager, handlerService);
      createMarkUnResolvedMenuItem(menuManager, handlerService);
      menuManager.add(new Separator());

      createSourceAsMergeMenuItem(menuManager, handlerService);
      createDestinationAsMergeMenuItem(menuManager, handlerService);
      menuManager.add(new Separator());

      createResetConflictMenuItem(menuManager, handlerService);
      menuManager.add(new Separator());

      createPreviewMenuItem(menuManager, handlerService);
      createDiffMenuItem(menuManager, handlerService);
      menuManager.add(new Separator());

      createSourceResourceHistoryMenuItem(menuManager, handlerService);
      createSourceRevealMenuItem(menuManager, handlerService);
      menuManager.add(new Separator());

      createDestinationResourceHistoryMenuItem(menuManager, handlerService);
      createDestinationRevealMenuItem(menuManager, handlerService);
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

   private MergeXWidget getMergeXWiget() {
      return ((MergeXViewer) xViewer).getXUserRoleViewer();
   }

   private void createDiffMenuItem(MenuManager menuManager, IHandlerService handlerService) {
      MenuManager subMenuManager = new MenuManager("Differences", "diffTransaction");
      menuManager.add(subMenuManager);

      MergeXWidget mergeXWidget = getMergeXWiget();

      createDiffItems(subMenuManager, handlerService, new DiffHandler(menuManager, 1, mergeXWidget),
         "Show Source Branch Differences");
      createDiffItems(subMenuManager, handlerService, new DiffHandler(menuManager, 2, mergeXWidget),
         "Show Destination Branch Differences");
      createDiffItems(subMenuManager, handlerService, new DiffHandler(menuManager, 3, mergeXWidget),
         "Show Source/Destination Differences");
      createDiffItems(subMenuManager, handlerService, new DiffHandler(menuManager, 4, mergeXWidget),
         "Show Source/Merge Differences");
      createDiffItems(subMenuManager, handlerService, new DiffHandler(menuManager, 5, mergeXWidget),
         "Show Destination/Merge Differences");
   }

   private void createDiffItems(MenuManager subMenuManager, IHandlerService handlerService, DiffHandler handler, String command) {
      handlerService.activateHandler(addDiffItems(subMenuManager, command), handler);
   }

   private void createPreviewMenuItem(MenuManager menuManager, IHandlerService handlerService) {
      MenuManager subMenuManager = new MenuManager("Preview", "previewTransaction");
      menuManager.add(subMenuManager);
      createPreviewItems(subMenuManager, handlerService, new PreviewHandler(menuManager, 1), "Preview Source Artifact");
      createPreviewItems(subMenuManager, handlerService, new PreviewHandler(menuManager, 2),
         "Preview Destination Artifact");
      createPreviewItems(subMenuManager, handlerService, new PreviewHandler(menuManager, 3), "Preview Merge Artifact");
   }

   private void createPreviewItems(MenuManager subMenuManager, IHandlerService handlerService, PreviewHandler handler, String command) {
      handlerService.activateHandler(addPreviewItems(subMenuManager, command), handler);
   }

   private void createEditArtifactMenuItem(MenuManager menuManager, IHandlerService handlerService) {

      handlerService.activateHandler(addEditArtifactMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) throws OseeCoreException {
            if (attributeConflict != null) {
               if (MergeUtility.okToOverwriteEditedValue(attributeConflict, Displays.getActiveShell().getShell(), false)) {
                  RendererManager.openInJob(attributeConflict.getArtifact(), PresentationType.SPECIALIZED_EDIT);

                  attributeConflict.markStatusToReflectEdit();
               }
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
            List<Conflict> conflicts = getMergeXWiget().getSelectedConflicts();
            attributeConflict = null;
            if (conflicts.size() == 1) {
               Conflict conflict = conflicts.iterator().next();
               if (conflict.getStatus().isEditable()) {
                  if (conflict instanceof AttributeConflict) {
                     attributeConflict = (AttributeConflict) conflict;
                  }
               }
            }
            return attributeConflict != null;
         }
      });
   }

   private void createSourceResourceHistoryMenuItem(MenuManager menuManager, IHandlerService handlerService) {

      handlerService.activateHandler(addSourceResourceHistoryMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) throws OseeCoreException {
            if (attributeConflict != null) {
               HistoryView.open(attributeConflict.getSourceArtifact());
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
            List<Conflict> conflicts = getMergeXWiget().getSelectedConflicts();
            attributeConflict = null;
            if (conflicts == null || conflicts.size() != 1) {
               return false;
            }
            attributeConflict = (AttributeConflict) conflicts.get(0);
            return true;
         }
      });
   }

   private void createDestinationResourceHistoryMenuItem(MenuManager menuManager, IHandlerService handlerService) {

      handlerService.activateHandler(addDestResourceHistoryMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) throws OseeCoreException {
            if (attributeConflict != null) {
               HistoryView.open(attributeConflict.getDestArtifact());
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
            List<Conflict> conflicts = getMergeXWiget().getSelectedConflicts();
            attributeConflict = null;
            if (conflicts == null || conflicts.size() != 1) {
               return false;
            }
            attributeConflict = (AttributeConflict) conflicts.get(0);
            return true;
         }

      });
   }

   private void createSourceRevealMenuItem(MenuManager menuManager, IHandlerService handlerService) {

      handlerService.activateHandler(addSourceRevealMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) throws OseeCoreException {
            if (attributeConflict != null) {
               ArtifactExplorer.revealArtifact(attributeConflict.getSourceArtifact());
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
            List<Conflict> conflicts = getMergeXWiget().getSelectedConflicts();
            attributeConflict = null;
            if (conflicts == null || conflicts.size() != 1) {
               return false;
            }
            attributeConflict = (AttributeConflict) conflicts.get(0);
            return true;
         }
      });
   }

   private void createDestinationRevealMenuItem(MenuManager menuManager, IHandlerService handlerService) {

      handlerService.activateHandler(addDestRevealMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) throws OseeCoreException {
            if (attributeConflict != null) {
               ArtifactExplorer.revealArtifact(attributeConflict.getDestArtifact());
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
            List<Conflict> conflicts = getMergeXWiget().getSelectedConflicts();
            attributeConflict = null;
            if (conflicts == null || conflicts.size() != 1) {
               return false;
            }
            attributeConflict = (AttributeConflict) conflicts.get(0);
            return true;
         }
      });
   }

   private void createMarkResolvedMenuItem(MenuManager menuManager, IHandlerService handlerService) {
      String commandId = addMarkAsResolvedMenuItem(menuManager);
      IHandler handler =
         new MergeManagerConflictHandler(menuManager,
            "Are you sure you want to Mark the selected [%s] conflict(s) as Resolved?",
            ConflictOperationEnum.MARK_RESOLVED);
      handlerService.activateHandler(commandId, handler);
   }

   private void createMarkUnResolvedMenuItem(MenuManager menuManager, IHandlerService handlerService) {
      String commandId = addMarkAsUnResolvedMenuItem(menuManager);
      IHandler handler =
         new MergeManagerConflictHandler(menuManager,
            "Are you sure you want to Mark the selected [%s] conflict(s) as UnResolved?",
            ConflictOperationEnum.MARK_UNRESOLVED);
      handlerService.activateHandler(commandId, handler);
   }

   private void createSourceAsMergeMenuItem(MenuManager menuManager, IHandlerService handlerService) {
      String commandId = addSourceAsMergeValueMenuItem(menuManager);
      IHandler handler =
         new MergeManagerConflictHandler(menuManager,
            "Are you sure you want to set the Merge value to the Source value for the selected [%s] conflict(s)?",
            ConflictOperationEnum.SET_SRC_AND_RESOLVE);
      handlerService.activateHandler(commandId, handler);
   }

   private void createDestinationAsMergeMenuItem(MenuManager menuManager, IHandlerService handlerService) {
      String commandId = addDestinationAsMergeValueMenuItem(menuManager);
      IHandler handler =
         new MergeManagerConflictHandler(menuManager,
            "Are you sure you want to set the Merge value to the Destination value for the selected [%s] conflict(s)?",
            ConflictOperationEnum.SET_DST_AND_RESOLVE);
      handlerService.activateHandler(commandId, handler);
   }

   private void createResetConflictMenuItem(MenuManager menuManager, IHandlerService handlerService) {
      String commandId = addResetConflictMenuItem(menuManager);
      IHandler handler =
         new MergeManagerConflictHandler(menuManager, "Are you sure you want to reset %s conflict(s)?",
            ConflictOperationEnum.RESET);
      handlerService.activateHandler(commandId, handler);
   }

   private void createMergeMenuItem(MenuManager menuManager, IHandlerService handlerService) {

      handlerService.activateHandler(addMergeMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
            if (attributeConflict != null) {
               MergeUtility.launchMerge(attributeConflict, Displays.getActiveShell().getShell());
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
            List<Conflict> conflicts = getMergeXWiget().getSelectedConflicts();
            attributeConflict = null;
            if (conflicts.size() == 1) {
               Conflict conflict = conflicts.iterator().next();
               if (conflict.getStatus().isEditable()) {
                  if (conflict instanceof AttributeConflict) {
                     attributeConflict = (AttributeConflict) conflict;
                  }
               }
            }
            return attributeConflict != null && attributeConflict.isWordAttribute();
         }
      });
   }

   private final class MergeManagerConflictHandler extends AbstractSelectionEnabledHandler {
      private final String dialogString;
      private final ConflictOperationEnum kindOfOperation;
      private List<Conflict> conflicts;

      public MergeManagerConflictHandler(MenuManager menuManager, String dialogString, ConflictOperationEnum kindOfOperation) {
         super(menuManager);
         this.dialogString = dialogString;
         this.kindOfOperation = kindOfOperation;
      }

      @Override
      public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
         boolean confirm =
            MessageDialog.openConfirm(Displays.getActiveShell().getShell(), "Confirm",
               String.format(dialogString, conflicts.size()));
         if (confirm) {
            IOperation operation = new ConflictHandlingOperation(kindOfOperation, conflicts);
            Operations.executeAsJob(operation, true, Job.SHORT, new JobChangeAdapter() {
               @Override
               public void done(IJobChangeEvent event) {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        xViewer.refresh();
                     }
                  });
               }
            });
         }
         return null;
      }

      @Override
      public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
         conflicts = getMergeXWiget().getSelectedConflicts();
         return !conflicts.isEmpty();
      }
   }

   private class PreviewHandler extends AbstractSelectionEnabledHandler {
      private final int partToPreview;
      private List<Artifact> artifacts;

      public PreviewHandler(MenuManager menuManager, int partToPreview) {
         super(menuManager);
         this.partToPreview = partToPreview;
      }

      @Override
      public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
         RendererManager.openInJob(artifacts, PresentationType.PREVIEW);
         return null;
      }

      @Override
      public boolean isEnabledWithException(IStructuredSelection structuredSelection) throws OseeCoreException {
         artifacts = new LinkedList<Artifact>();
         List<Conflict> conflicts = getMergeXWiget().getSelectedConflicts();
         for (Conflict conflict : conflicts) {

            try {
               switch (partToPreview) {
                  case 1:
                     if (conflict.getSourceArtifact() != null) {
                        artifacts.add(conflict.getSourceArtifact());
                     }
                     break;
                  case 2:
                     if (conflict.getDestArtifact() != null) {
                        artifacts.add(conflict.getDestArtifact());
                     }
                     break;
                  case 3:
                     ConflictStatus status = conflict.getStatus();
                     if (status.isInformational()) {
                        return false;
                     }
                     if (conflict.getArtifact() != null) {
                        artifacts.add(conflict.getArtifact());
                     }
                     break;
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

         return AccessControlManager.hasPermission(artifacts, PermissionEnum.READ);
      }
   }
}
