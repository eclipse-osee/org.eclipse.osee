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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.Commands;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.part.ViewPart;

/**
 * @see ViewPart
 * @author Donald G. Dunne
 */
public class MergeView extends ViewPart implements IActionable, IBranchEventListener, IFrameworkTransactionEventListener {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView";
   public static String HELP_CONTEXT_ID = "Merge_Manager_View";
   private XMergeViewer xMergeViewer;
   private Conflict[] conflicts;
   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet/debug/Merge"));
   private IHandlerService handlerService;
   private Branch sourceBranch;
   private Branch destBranch;
   private TransactionRecord transactionId;
   private TransactionRecord commitTrans;
   private boolean showConflicts;

   public static void openView(final Branch sourceBranch, final Branch destBranch, final TransactionRecord tranId) {
      if (sourceBranch == null && destBranch == null && tranId == null) {
         throw new IllegalArgumentException("Branch's and Transaction ID can't be null");
      }
      if (DEBUG && sourceBranch != null && destBranch != null) {
         System.out.println(String.format("Openeing Merge View with Source Branch: %s and Destination Branch: %s",
               sourceBranch.getName(), destBranch.getName()));
      }
      openViewUpon(sourceBranch, destBranch, tranId, null, true);
   }

   public static void openView(final TransactionRecord commitTrans) {
      if (commitTrans == null) {
         throw new IllegalArgumentException("Commit Transaction ID can't be null");
      }
      if (DEBUG) {
         System.out.println(String.format("Openeing Merge View with Transaction ID: %d ",
               commitTrans.getId()));
      }
      openViewUpon(null, null, null, commitTrans, true);
   }

   private static void openViewUpon(final Branch sourceBranch, final Branch destBranch, final TransactionRecord tranId, final TransactionRecord commitTrans, final boolean showConflicts) {
      Job job = new Job("Open Merge View") {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  try {
                     IWorkbenchPage page = AWorkbench.getActivePage();
                     IViewPart viewPart =
                           page.showView(
                                 MergeView.VIEW_ID,
                                 String.valueOf(sourceBranch != null ? sourceBranch.getId() * 100000 + destBranch.getId() : commitTrans.getId()),
                                 IWorkbenchPage.VIEW_ACTIVATE);
                     if (viewPart instanceof MergeView) {
                        MergeView mergeView = (MergeView) viewPart;
                        mergeView.showConflicts = showConflicts;
                        mergeView.explore(sourceBranch, destBranch, tranId, commitTrans, showConflicts);
                     }
                  } catch (Exception ex) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                  }
               }
            });

            monitor.done();
            return Status.OK_STATUS;
         }
      };

      Jobs.startJob(job);
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   @Override
   public void setFocus() {
      if (xMergeViewer != null) {
         xMergeViewer.setFocus();
      }
   }

   @Override
   public void createPartControl(Composite parent) {
      /*
       * Create a grid layout object so the text and treeviewer are laid out the way I want.
       */

      PlatformUI.getWorkbench().getService(IHandlerService.class);
      handlerService = (IHandlerService) getSite().getService(IHandlerService.class);

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));
      xMergeViewer = new XMergeViewer();
      xMergeViewer.setDisplayLabel(false);
      xMergeViewer.createWidgets(parent, 1);

      if (conflicts != null) {
         xMergeViewer.setConflicts(conflicts);
      }
      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            MenuManager menuManager = (MenuManager) manager;
            fillPopupMenu(menuManager);
         }

         private void fillPopupMenu(MenuManager menuManager) {
            addEditArtifactMenuItem(menuManager);
            addMergeMenuItem(menuManager);
            menuManager.add(new Separator());
            addPreviewMenuItem(menuManager);
            addDiffMenuItem(menuManager);
            menuManager.add(new Separator());
            addSourceResourceHistoryMenuItem(menuManager);
            addSourceRevealMenuItem(menuManager);
            menuManager.add(new Separator());
            addDestResourceHistoryMenuItem(menuManager);
            addDestRevealMenuItem(menuManager);
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      });

      xMergeViewer.getXViewer().getTree().setMenu(menuManager.createContextMenu(xMergeViewer.getXViewer().getTree()));

      createEditArtifactMenuItem(menuManager);
      createMergeMenuItem(menuManager);
      menuManager.add(new Separator());
      createPreviewMenuItem(menuManager);
      createDiffMenuItem(menuManager);
      menuManager.add(new Separator());
      createSourceResourceHistoryMenuItem(menuManager);
      createSourceRevealMenuItem(menuManager);
      menuManager.add(new Separator());
      createDestinationResourceHistoryMenuItem(menuManager);
      createDestinationRevealMenuItem(menuManager);
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

      OseeContributionItem.addTo(this, true);
      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynetd.widgets.xmerge.MergeView", menuManager,
            xMergeViewer.getXViewer());

      getSite().setSelectionProvider(xMergeViewer.getXViewer());
      SkynetGuiPlugin.getInstance().setHelp(parent, HELP_CONTEXT_ID, "org.eclipse.osee.framework.help.ui");

      OseeEventManager.addListener(this);
   }

   private void addPreviewMenuItem(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Preview", "previewTransaction");
      menuManager.add(subMenuManager);
      addPreviewItems(subMenuManager, "Preview Source Artifact");
      addPreviewItems(subMenuManager, "Preview Destination Artifact");
      addPreviewItems(subMenuManager, "Preview Merge Artifact");
   }

   private void createPreviewMenuItem(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Preview", "previewTransaction");
      menuManager.add(subMenuManager);
      createPreviewItems(subMenuManager, new PreviewHandler(menuManager, 1), "Preview Source Artifact");
      createPreviewItems(subMenuManager, new PreviewHandler(menuManager, 2), "Preview Destination Artifact");
      createPreviewItems(subMenuManager, new PreviewHandler(menuManager, 3), "Preview Merge Artifact");
   }

   private String addPreviewItems(MenuManager subMenuManager, String command) {
      CommandContributionItem previewCommand =
            Commands.getLocalCommandContribution(getSite(), subMenuManager.getId() + command, command, null, null,
                  ImageManager.getImageDescriptor(FrameworkImage.PREVIEW_ARTIFACT), null, null, null);
      subMenuManager.add(previewCommand);
      return previewCommand.getId();
   }

   private void createPreviewItems(MenuManager subMenuManager, PreviewHandler handler, String command) {
      handlerService.activateHandler(addPreviewItems(subMenuManager, command), handler);
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

   private void createDiffMenuItem(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Differences", "diffTransaction");
      menuManager.add(subMenuManager);
      createDiffItems(subMenuManager, new DiffHandler(menuManager, 1), "Show Source Branch Differences");
      createDiffItems(subMenuManager, new DiffHandler(menuManager, 2), "Show Destination Branch Differences");
      createDiffItems(subMenuManager, new DiffHandler(menuManager, 3), "Show Source/Destination Differences");
      createDiffItems(subMenuManager, new DiffHandler(menuManager, 4), "Show Source/Merge Differences");
      createDiffItems(subMenuManager, new DiffHandler(menuManager, 5), "Show Destination/Merge Differences");
   }

   private String addDiffItems(MenuManager subMenuManager, String command) {
      CommandContributionItem diffCommand =
            Commands.getLocalCommandContribution(getSite(), subMenuManager.getId() + command, command, null, null,
                  null, null, null, null);
      subMenuManager.add(diffCommand);
      return diffCommand.getId();
   }

   private void createDiffItems(MenuManager subMenuManager, DiffHandler handler, String command) {
      handlerService.activateHandler(addDiffItems(subMenuManager, command), handler);
   }

   private String addEditArtifactMenuItem(MenuManager menuManager) {
      CommandContributionItem editArtifactCommand;
      //RendererManager.getBestFileRenderer(PresentationType.SPECIALIZED_EDIT, attributeConflict.getArtifact()).getImage(attributeConflict.getArtifact());
      editArtifactCommand =
            Commands.getLocalCommandContribution(getSite(), "editArtifactCommand", "Edit Merge Artifact", null, null,
                  null, "E", null, "edit_Merge_Artifact");
      menuManager.add(editArtifactCommand);
      return editArtifactCommand.getId();
   }

   private String addSourceResourceHistoryMenuItem(MenuManager menuManager) {
      CommandContributionItem sourecResourceCommand;
      sourecResourceCommand =
            Commands.getLocalCommandContribution(getSite(), "sourceResourceHistory",
                  "Show Source Artifact Resource History", null, null,
                  ImageManager.getImageDescriptor(FrameworkImage.DB_ICON_BLUE_EDIT), null, null,
                  "source_Resource_History");
      menuManager.add(sourecResourceCommand);
      return sourecResourceCommand.getId();
   }

   private String addDestResourceHistoryMenuItem(MenuManager menuManager) {
      CommandContributionItem sourecResourceCommand;
      sourecResourceCommand =
            Commands.getLocalCommandContribution(getSite(), "destResourceHistory",
                  "Show Dest Artifact Resource History", null, null,
                  ImageManager.getImageDescriptor(FrameworkImage.DB_ICON_BLUE_EDIT), null, null,
                  "dest_Resource_History");
      menuManager.add(sourecResourceCommand);
      return sourecResourceCommand.getId();
   }

   private String addSourceRevealMenuItem(MenuManager menuManager) {
      CommandContributionItem sourceReveal;
      sourceReveal =
            Commands.getLocalCommandContribution(getSite(), "sourceRevealArtifactExplorer",
                  "Reveal Source Artifact in Artifact Explorer", null, null,
                  ImageManager.getImageDescriptor(FrameworkImage.MAGNIFY), null, null, "source_Reveal");
      menuManager.add(sourceReveal);
      return sourceReveal.getId();
   }

   private String addDestRevealMenuItem(MenuManager menuManager) {
      CommandContributionItem destReveal;
      destReveal =
            Commands.getLocalCommandContribution(getSite(), "destRevealArtifactExplorer",
                  "Reveal Dest Artifact in Artifact Explorer", null, null,
                  ImageManager.getImageDescriptor(FrameworkImage.MAGNIFY), null, null, "dest_Reveal");
      menuManager.add(destReveal);
      return destReveal.getId();
   }

   private void createEditArtifactMenuItem(MenuManager menuManager) {

      handlerService.activateHandler(addEditArtifactMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            if (attributeConflict != null) {
               try {
                  if (MergeUtility.okToOverwriteEditedValue(attributeConflict,
                        Display.getCurrent().getActiveShell().getShell(), false)) {
                     RendererManager.openInJob(attributeConflict.getArtifact(), PresentationType.SPECIALIZED_EDIT);

                     attributeConflict.markStatusToReflectEdit();
                  }
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            List<Conflict> conflicts = xMergeViewer.getSelectedConflicts();
            attributeConflict = null;
            if (conflicts == null || conflicts.size() != 1 || !(conflicts.get(0) instanceof AttributeConflict) || !conflicts.get(
                  0).statusEditable()) {
               return false;
            }
            attributeConflict = (AttributeConflict) conflicts.get(0);
            return true;
         }
      });
   }

   private void createSourceResourceHistoryMenuItem(MenuManager menuManager) {

      handlerService.activateHandler(addSourceResourceHistoryMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            if (attributeConflict != null) {
               try {
                  HistoryView.open(attributeConflict.getSourceArtifact());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            List<Conflict> conflicts = xMergeViewer.getSelectedConflicts();
            attributeConflict = null;
            if (conflicts == null || conflicts.size() != 1) {
               return false;
            }
            attributeConflict = (AttributeConflict) conflicts.get(0);
            return true;
         }
      });
   }

   private void createDestinationResourceHistoryMenuItem(MenuManager menuManager) {

      handlerService.activateHandler(addDestResourceHistoryMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            if (attributeConflict != null) {
               try {
                  HistoryView.open(attributeConflict.getDestArtifact());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            List<Conflict> conflicts = xMergeViewer.getSelectedConflicts();
            attributeConflict = null;
            if (conflicts == null || conflicts.size() != 1) {
               return false;
            }
            attributeConflict = (AttributeConflict) conflicts.get(0);
            return true;
         }
      });
   }

   private void createSourceRevealMenuItem(MenuManager menuManager) {

      handlerService.activateHandler(addSourceRevealMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            if (attributeConflict != null) {
               try {
                  ArtifactExplorer.revealArtifact(attributeConflict.getSourceArtifact());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            List<Conflict> conflicts = xMergeViewer.getSelectedConflicts();
            attributeConflict = null;
            if (conflicts == null || conflicts.size() != 1) {
               return false;
            }
            attributeConflict = (AttributeConflict) conflicts.get(0);
            return true;
         }
      });
   }

   private void createDestinationRevealMenuItem(MenuManager menuManager) {

      handlerService.activateHandler(addDestRevealMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            if (attributeConflict != null) {
               try {
                  ArtifactExplorer.revealArtifact(attributeConflict.getDestArtifact());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            List<Conflict> conflicts = xMergeViewer.getSelectedConflicts();
            attributeConflict = null;
            if (conflicts == null || conflicts.size() != 1) {
               return false;
            }
            attributeConflict = (AttributeConflict) conflicts.get(0);
            return true;
         }
      });
   }

   private String addMergeMenuItem(MenuManager menuManager) {
      CommandContributionItem mergeArtifactCommand;
      mergeArtifactCommand =
            Commands.getLocalCommandContribution(getSite(), "mergeArtifactCommand",
                  "Generate Three Way Merge (Developmental)", null, null, null, "E", null,
                  "Merge_Source_Destination_Artifact");
      menuManager.add(mergeArtifactCommand);
      return mergeArtifactCommand.getId();
   }

   private void createMergeMenuItem(MenuManager menuManager) {

      handlerService.activateHandler(addMergeMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         private AttributeConflict attributeConflict;

         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            if (attributeConflict != null) {
               MergeUtility.launchMerge(attributeConflict, Display.getCurrent().getActiveShell().getShell());
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            List<Conflict> conflicts = xMergeViewer.getSelectedConflicts();
            attributeConflict = null;
            if (conflicts == null || conflicts.size() != 1 || !(conflicts.get(0) instanceof AttributeConflict) || !conflicts.get(
                  0).statusEditable()) {
               return false;
            }
            attributeConflict = (AttributeConflict) conflicts.get(0);
            return attributeConflict.isWordAttribute();
         }
      });
   }

   public void explore(final Branch sourceBranch, final Branch destBranch, final TransactionRecord transactionId, final TransactionRecord commitTrans, boolean showConflicts) {
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
      this.transactionId = transactionId;
      this.commitTrans = commitTrans;
      try {
         xMergeViewer.setInputData(sourceBranch, destBranch, transactionId, this, commitTrans, showConflicts);
         if (sourceBranch != null) {
            setPartName("Merge Manager: " + sourceBranch.getShortName() + " <=> " + destBranch.getShortName());
         } else if (commitTrans != null) {
            setPartName("Merge Manager: " + commitTrans.getId());
         } else {
            setPartName("Merge Manager");
         }

      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void setConflicts(Conflict[] conflicts) {
      this.conflicts = conflicts;
   }

   public String getActionDescription() {
      return "";
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         Integer sourceBranchId = null;
         Integer destBranchId = null;

         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               if (SkynetViews.isSourceValid(memento)) {

                  Integer commitTransaction = memento.getInteger(COMMIT_NUMBER);
                  if (commitTransaction != null) {
                     openViewUpon(null, null, null, TransactionManager.getTransactionId(commitTransaction), false);
                     return;
                  }
                  sourceBranchId = memento.getInteger(SOURCE_BRANCH_ID);
                  final Branch sourceBranch = BranchManager.getBranch(sourceBranchId);
                  if (sourceBranch == null) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.WARNING,
                           "Merge View can't init due to invalid source branch id " + sourceBranchId);
                     xMergeViewer.setLabel("Could not restore this Merge View");
                     return;
                  }
                  destBranchId = memento.getInteger(DEST_BRANCH_ID);
                  final Branch destBranch = BranchManager.getBranch(destBranchId);
                  if (destBranch == null) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.WARNING,
                           "Merge View can't init due to invalid destination branch id " + sourceBranchId);
                     xMergeViewer.setLabel("Could not restore this Merge View");
                     return;
                  }
                  try {
                     TransactionRecord transactionId =
                           TransactionManager.getTransactionId(memento.getInteger(TRANSACTION_NUMBER));
                     openViewUpon(sourceBranch, destBranch, transactionId, null, false);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.WARNING,
                           "Merge View can't init due to invalid transaction id " + transactionId);
                     xMergeViewer.setLabel("Could not restore this Merge View due to invalid transaction id " + transactionId);
                     return;
                  }
               } else {
                  SkynetViews.closeView(VIEW_ID, getViewSite().getSecondaryId());
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Merge View error on init", ex);
      }
   }

   private static final String INPUT = "input";
   private static final String SOURCE_BRANCH_ID = "sourceBranchId";
   private static final String DEST_BRANCH_ID = "destBranchId";
   private static final String TRANSACTION_NUMBER = "transactionNumber";
   private static final String COMMIT_NUMBER = "commitTransactionNumber";

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento = memento.createChild(INPUT);
      if (sourceBranch != null) {
         memento.putInteger(SOURCE_BRANCH_ID, sourceBranch.getId());
         memento.putInteger(DEST_BRANCH_ID, destBranch.getId());
         memento.putInteger(TRANSACTION_NUMBER, transactionId.getId());
      } else if (commitTrans != null) {
         memento.putInteger(COMMIT_NUMBER, commitTrans.getId());
      }

      if (sourceBranch != null || commitTrans != null) {
         SkynetViews.addDatabaseSourceId(memento);
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
      public Object execute(ExecutionEvent event) throws ExecutionException {
         if (!artifacts.isEmpty()) {
            try {
               RendererManager.previewInJob(artifacts);
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
         return null;
      }

      @Override
      public boolean isEnabledWithException() throws OseeCoreException {
         artifacts = new LinkedList<Artifact>();
         List<Conflict> conflicts = xMergeViewer.getSelectedConflicts();
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
                     if (conflict.statusNotResolvable() || conflict.statusInformational()) {
                        return false;
                     }
                     if (conflict.getArtifact() != null) {
                        artifacts.add(conflict.getArtifact());
                     }
                     break;
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

         return AccessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
      }
   }

   private class DiffHandler extends AbstractSelectionEnabledHandler {
      private final int diffToShow;
      private AttributeConflict attributeConflict;
      private ArtifactConflict artifactConflict;
      private List<Artifact> artifacts;

      public DiffHandler(MenuManager menuManager, int diffToShow) {
         super(menuManager);
         this.diffToShow = diffToShow;
      }

      @Override
      public Object execute(ExecutionEvent event) throws ExecutionException {
         try {
            if (attributeConflict != null) {
               switch (diffToShow) {
                  case 1:
                     MergeUtility.showCompareFile(
                           MergeUtility.getStartArtifact(attributeConflict),
                           attributeConflict.getSourceArtifact(),
                           "Source_Diff_For_" + attributeConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                                 ":", ";") + ".xml");
                     break;
                  case 2:
                     MergeUtility.showCompareFile(
                           MergeUtility.getStartArtifact(attributeConflict),
                           attributeConflict.getDestArtifact(),
                           "Destination_Diff_For_" + attributeConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                                 ":", ";") + ".xml");
                     break;
                  case 3:
                     MergeUtility.showCompareFile(
                           attributeConflict.getSourceArtifact(),
                           attributeConflict.getDestArtifact(),
                           "Source_Destination_Diff_For_" + attributeConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                                 ":", ";") + ".xml");
                     break;
                  case 4:
                     if (attributeConflict.wordMarkupPresent()) {
                        throw new OseeCoreException(AttributeConflict.DIFF_MERGE_MARKUP);
                     }
                     MergeUtility.showCompareFile(
                           attributeConflict.getSourceArtifact(),
                           attributeConflict.getArtifact(),
                           "Source_Merge_Diff_For_" + attributeConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                                 ":", ";") + ".xml");
                     break;
                  case 5:
                     if (attributeConflict.wordMarkupPresent()) {
                        throw new OseeCoreException(AttributeConflict.DIFF_MERGE_MARKUP);
                     }
                     MergeUtility.showCompareFile(
                           attributeConflict.getDestArtifact(),
                           attributeConflict.getArtifact(),
                           "Destination_Merge_Diff_For_" + attributeConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                                 ":", ";") + ".xml");
                     break;
               }
            } else if (artifactConflict != null) {
               if (diffToShow == 1) {
                  MergeUtility.showCompareFile(
                        artifactConflict.getSourceArtifact(),
                        MergeUtility.getStartArtifact(artifactConflict),
                        "Source_Diff_For_" + artifactConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                              ":", ";") + ".xml");
               }
               if (diffToShow == 2) {
                  MergeUtility.showCompareFile(
                        artifactConflict.getDestArtifact(),
                        MergeUtility.getStartArtifact(artifactConflict),
                        "Destination_Diff_For_" + artifactConflict.getArtifact().getSafeName() + new Date().toString().replaceAll(
                              ":", ";") + ".xml");
               }
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
         return null;
      }

      @Override
      public boolean isEnabledWithException() throws OseeCoreException {
         artifacts = new LinkedList<Artifact>();
         List<Conflict> conflicts = xMergeViewer.getSelectedConflicts();
         if (conflicts.size() != 1) {
            return false;
         }
         if (conflicts.get(0) instanceof AttributeConflict) {
            attributeConflict = (AttributeConflict) conflicts.get(0);
            artifactConflict = null;
            try {
               switch (diffToShow) {
                  case 1:
                     if (attributeConflict.getSourceArtifact() != null && MergeUtility.getStartArtifact(attributeConflict) != null) {
                        artifacts.add(attributeConflict.getSourceArtifact());
                        artifacts.add(MergeUtility.getStartArtifact(attributeConflict));
                     } else {
                        return false;
                     }
                     break;
                  case 2:
                     if (attributeConflict.getDestArtifact() != null && MergeUtility.getStartArtifact(attributeConflict) != null) {
                        artifacts.add(attributeConflict.getDestArtifact());
                        artifacts.add(MergeUtility.getStartArtifact(attributeConflict));
                     } else {
                        return false;
                     }
                     break;
                  case 3:
                     if (attributeConflict.getDestArtifact() != null && attributeConflict.getSourceArtifact() != null) {
                        artifacts.add(attributeConflict.getSourceArtifact());
                        artifacts.add(attributeConflict.getDestArtifact());
                     } else {
                        return false;
                     }
                     break;
                  case 4:
                     if (attributeConflict.getSourceArtifact() != null && attributeConflict.getArtifact() != null) {
                        artifacts.add(attributeConflict.getSourceArtifact());
                        artifacts.add(attributeConflict.getArtifact());
                     } else {
                        return false;
                     }
                     break;
                  case 5:
                     if (attributeConflict.getDestArtifact() != null && attributeConflict.getArtifact() != null) {
                        artifacts.add(attributeConflict.getDestArtifact());
                        artifacts.add(attributeConflict.getArtifact());
                     } else {
                        return false;
                     }
                     break;
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }

         } else if (conflicts.get(0) instanceof ArtifactConflict) {
            attributeConflict = null;
            artifactConflict = (ArtifactConflict) conflicts.get(0);
            try {
               switch (diffToShow) {
                  case 1:
                     if (artifactConflict.getSourceArtifact() != null && conflicts.get(0).statusNotResolvable() && MergeUtility.getStartArtifact(artifactConflict) != null) {
                        artifacts.add(artifactConflict.getSourceArtifact());
                        artifacts.add(MergeUtility.getStartArtifact(artifactConflict));
                     } else {
                        return false;
                     }
                     break;
                  case 2:
                     if (artifactConflict.getDestArtifact() != null && conflicts.get(0).statusInformational() && MergeUtility.getStartArtifact(artifactConflict) != null) {
                        artifacts.add(artifactConflict.getDestArtifact());
                        artifacts.add(MergeUtility.getStartArtifact(artifactConflict));
                     } else {
                        return false;
                     }
                     break;
                  case 3:
                     return false;
                  case 4:
                     return false;
                  case 5:
                     return false;
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }

         }
         return AccessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
      }
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (sourceBranch != null && destBranch != null && (sourceBranch.getId() == branchId || destBranch.getId() == branchId)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               if (xMergeViewer != null && xMergeViewer.getXViewer().getTree().isDisposed() != true) {
                  xMergeViewer.refresh();
               }
            }
         });
      }
   }

   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   @Override
   public void handleFrameworkTransactionEvent(final Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      try {
         if (sourceBranch == null || destBranch == null || sourceBranch.getId() != transData.getId() && destBranch.getId() != transData.getId() && ConflictManagerInternal.getMergeBranchId(
               sourceBranch.getId(), destBranch.getId()) != transData.getId()) {
            return;
         }
      } catch (OseeCoreException ex) {
         //ignore the exception for an event don't want them poping up on people for no reason
      }
      final MergeView mergeView = this;
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (xMergeViewer.getXViewer() == null || xMergeViewer.getXViewer().getTree() == null || xMergeViewer.getXViewer().getTree().isDisposed()) {
               return;
            }
            for (Artifact artifact : transData.cacheChangedArtifacts) {
               try {
                  Branch branch = artifact.getBranch();
                  if (showConflicts) {
                     for (Conflict conflict : conflicts) {
                        if (artifact.equals(conflict.getSourceArtifact()) && branch.equals(conflict.getSourceBranch()) || artifact.equals(conflict.getDestArtifact()) && branch.equals(conflict.getDestBranch())) {
                           xMergeViewer.setInputData(sourceBranch, destBranch, transactionId, mergeView, commitTrans,
                                 "Source Artifact Changed", showConflicts);
                           if (artifact.equals(conflict.getSourceArtifact()) && sender.isLocal()) {
                              new MessageDialog(
                                    Display.getDefault().getActiveShell().getShell(),
                                    "Modifying Source artifact while merging",
                                    null,
                                    "Typically changes done while merging should be done on the merge branch.  You should not normally merge on the source branch.",
                                    2, new String[] {"OK"}, 1).open();
                           }
                           return;
                        } else if (artifact.equals(conflict.getArtifact())) {
                           conflict.computeEqualsValues();
                           xMergeViewer.refresh();
                        }
                     }
                     if (conflicts.length > 0 && (branch.equals(conflicts[0].getSourceBranch()) || branch.equals(conflicts[0].getDestBranch()))) {
                        xMergeViewer.setInputData(
                              sourceBranch,
                              destBranch,
                              transactionId,
                              mergeView,
                              commitTrans,
                              branch.equals(conflicts[0].getSourceBranch()) ? "Source Branch Changed" : "Destination Branch Changed",
                              showConflicts);
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
            if (!transData.cacheDeletedArtifacts.isEmpty()) {
               Branch branch = transData.cacheDeletedArtifacts.iterator().next().getBranch();
               if (conflicts.length > 0 && (branch.equals(conflicts[0].getSourceBranch()) || branch.equals(conflicts[0].getDestBranch()))) {
                  xMergeViewer.setInputData(
                        sourceBranch,
                        destBranch,
                        transactionId,
                        mergeView,
                        commitTrans,
                        branch.equals(conflicts[0].getSourceBranch()) ? "Source Branch Changed" : "Destination Branch Changed",
                        showConflicts);
               }
            }
         }
      });

   }

   protected void showConflicts(boolean show) {
      showConflicts = show;
   }
}