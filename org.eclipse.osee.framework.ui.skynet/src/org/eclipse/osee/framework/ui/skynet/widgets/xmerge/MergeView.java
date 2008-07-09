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

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.Commands;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.branch.BranchView;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
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
public class MergeView extends ViewPart implements IActionable {
   private static final RendererManager rendererManager = RendererManager.getInstance();
   private static final AccessControlManager accessControlManager = AccessControlManager.getInstance();
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView";
   private static String HELP_CONTEXT_ID = "MergeManagerView";
   private XMergeViewer xMergeViewer;
   private Conflict[] conflicts;

   /*
    *   Code development
    *   BranchView.getBranchView().
    */

   private IHandlerService handlerService;
   private Branch sourceBranch;
   private Branch destBranch;
   private TransactionId transactionId;

   /**
    * @author Donald G. Dunne
    */
   public MergeView() {
   }

   public static void openViewUpon(final Branch sourceBranch, final Branch destBranch, final TransactionId tranId) {
      Job job = new Job("Open Merge View") {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  try {
                     IWorkbenchPage page = AWorkbench.getActivePage();
                     MergeView mergeView =
                           (MergeView) page.showView(MergeView.VIEW_ID, String.valueOf(sourceBranch.getBranchId()),
                                 IWorkbenchPage.VIEW_VISIBLE);
                     mergeView.explore(sourceBranch, destBranch, tranId);
                     //                     }
                  } catch (Exception ex) {
                     OSEELog.logException(SkynetGuiPlugin.class, ex, true);
                  }
               }
            });

            monitor.done();
            return Status.OK_STATUS;
         }
      };

      Jobs.startJob(job);
   }

   public static void closeView(final Branch sourceBranch) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               IWorkbenchPage page = AWorkbench.getActivePage();
               MergeView mergeView =
                     (MergeView) page.showView(MergeView.VIEW_ID, String.valueOf(sourceBranch.getBranchId()),
                           IWorkbenchPage.VIEW_VISIBLE);
               mergeView.dispose();
               //                     }
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });
   }

   @Override
   public void dispose() {
      super.dispose();
   }

   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   public void createPartControl(Composite parent) {
      /*
       * Create a grid layout object so the text and treeviewer are layed out the way I want.
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

      try {
         if (conflicts != null) xMergeViewer.setConflicts(conflicts);
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            MenuManager menuManager = (MenuManager) manager;
            fillPopupMenu(menuManager);
         }

         private void fillPopupMenu(MenuManager menuManager) {
            addSourceBranchDefaultMenuItem(menuManager);
            addDestBranchDefaultMenuItem(menuManager);
            menuManager.add(new Separator());
            addPreviewMenuItem(menuManager);
            addDiffMenuItem(menuManager);
            menuManager.add(new Separator());
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      });

      xMergeViewer.getXViewer().getTree().setMenu(menuManager.createContextMenu(xMergeViewer.getXViewer().getTree()));

      createSourceBranchDefaultMenuItem(menuManager);
      createDestBranchDefaultMenuItem(menuManager);
      menuManager.add(new Separator());
      createPreviewMenuItem(menuManager);
      createDiffMenuItem(menuManager);
      menuManager.add(new Separator());
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

      SkynetContributionItem.addTo(this, true);
      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynetd.widgets.xmerge.MergeView", menuManager,
            xMergeViewer.getXViewer());

      getSite().setSelectionProvider(xMergeViewer.getXViewer());
      SkynetGuiPlugin.getInstance().setHelp(parent, HELP_CONTEXT_ID);
   }

   /**
    * @param menuManager
    */
   private void addPreviewMenuItem(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Preview", "previewTransaction");
      menuManager.add(subMenuManager);
      addPreviewItems(subMenuManager, "Preview Source Artifact");
      addPreviewItems(subMenuManager, "Preview Destination Artifact");
      addPreviewItems(subMenuManager, "Preview Merge Artifact");
   }

   /**
    * @param menuManager
    */
   private void createPreviewMenuItem(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Preview", "previewTransaction");
      menuManager.add(subMenuManager);
      createPreviewItems(subMenuManager, new PreviewHandler(menuManager, 1), "Preview Source Artifact");
      createPreviewItems(subMenuManager, new PreviewHandler(menuManager, 2), "Preview Destination Artifact");
      createPreviewItems(subMenuManager, new PreviewHandler(menuManager, 3), "Preview Merge Artifact");
   }

   /**
    * @param subMenuManager
    */
   private String addPreviewItems(MenuManager subMenuManager, String command) {
      CommandContributionItem previewCommand =
            Commands.getLocalCommandContribution(getSite(), subMenuManager.getId() + command, command, null, null,
                  SkynetGuiPlugin.getInstance().getImageDescriptor("preview_artifact.gif"), null, null, null);
      subMenuManager.add(previewCommand);
      return previewCommand.getId();
   }

   /**
    * @param subMenuManager
    */
   private void createPreviewItems(MenuManager subMenuManager, PreviewHandler handler, String command) {
      handlerService.activateHandler(addPreviewItems(subMenuManager, command), handler);
   }

   /**
    * @param menuManager
    */
   private void addDiffMenuItem(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Differences", "diffTransaction");
      menuManager.add(subMenuManager);
      addDiffItems(subMenuManager, "Show Source Branch Differences");
      addDiffItems(subMenuManager, "Show Destination Branch Differences");
      addDiffItems(subMenuManager, "Show Source/Destination Differences");
   }

   /**
    * @param menuManager
    */
   private void createDiffMenuItem(MenuManager menuManager) {
      MenuManager subMenuManager = new MenuManager("Differences", "diffTransaction");
      menuManager.add(subMenuManager);
      createDiffItems(subMenuManager, new DiffHandler(menuManager, 1), "Show Source Branch Differences");
      createDiffItems(subMenuManager, new DiffHandler(menuManager, 2), "Show Destination Branch Differences");
      createDiffItems(subMenuManager, new DiffHandler(menuManager, 3), "Show Source/Destination Differences");
   }

   /**
    * @param subMenuManager
    */
   private String addDiffItems(MenuManager subMenuManager, String command) {
      CommandContributionItem diffCommand =
            Commands.getLocalCommandContribution(getSite(), subMenuManager.getId() + command, command, null, null,
                  null, null, null, null);
      subMenuManager.add(diffCommand);
      return diffCommand.getId();
   }

   /**
    * @param subMenuManager
    */
   private void createDiffItems(MenuManager subMenuManager, DiffHandler handler, String command) {
      handlerService.activateHandler(addDiffItems(subMenuManager, command), handler);
   }

   /**
    * @param menuManager
    */
   private String addDestBranchDefaultMenuItem(MenuManager menuManager) {
      CommandContributionItem setDestBranchDefaultCommand;
      if (conflicts != null && conflicts.length != 0 && conflicts[0].getDestBranch() == BranchPersistenceManager.getInstance().getDefaultBranch()) {
         setDestBranchDefaultCommand =
               Commands.getLocalCommandContribution(getSite(), "setDestBranchDefaultCommand",
                     "Set Destination as Default Branch", null, null, SkynetGuiPlugin.getInstance().getImageDescriptor(
                           "chkbox_enabled.gif"), "D", null, "branch_manager_default_branch_menu");
      } else {
         setDestBranchDefaultCommand =
               Commands.getLocalCommandContribution(getSite(), "setDestBranchDefaultCommand",
                     "Set Destination as Default Branch", null, null, null, "D", null,
                     "branch_manager_default_branch_menu");

      }
      menuManager.add(setDestBranchDefaultCommand);
      return setDestBranchDefaultCommand.getId();
   }

   /**
    * @param menuManager
    */
   private void createDestBranchDefaultMenuItem(MenuManager menuManager) {

      handlerService.activateHandler(addDestBranchDefaultMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            BranchView branchView = BranchView.getBranchView();
            if (branchView != null) {
               branchView.setDefaultBranch(conflicts[0].getDestBranch());
            } else {
               BranchPersistenceManager.getInstance().setDefaultBranch(conflicts[0].getDestBranch());
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            if (conflicts == null || conflicts.length == 0) return false;
            return conflicts[0].getDestBranch() != BranchPersistenceManager.getInstance().getDefaultBranch();
         }
      });
   }

   /**
    * @param menuManager
    */
   private String addSourceBranchDefaultMenuItem(MenuManager menuManager) {
      CommandContributionItem setSourceBranchDefaultCommand;
      if (conflicts != null && conflicts.length != 0 && conflicts[0].getSourceBranch() == BranchPersistenceManager.getInstance().getDefaultBranch()) {
         setSourceBranchDefaultCommand =
               Commands.getLocalCommandContribution(getSite(), "setSourceBranchDefaultCommand",
                     "Set Source as Default Branch", null, null, SkynetGuiPlugin.getInstance().getImageDescriptor(
                           "chkbox_enabled.gif"), "S", null, "branch_manager_default_branch_menu");
      } else {
         setSourceBranchDefaultCommand =
               Commands.getLocalCommandContribution(getSite(), "setSourceBranchDefaultCommand",
                     "Set Source as Default Branch", null, null, null, "S", null, "branch_manager_default_branch_menu");
      }
      menuManager.add(setSourceBranchDefaultCommand);
      return setSourceBranchDefaultCommand.getId();
   }

   /**
    * @param menuManager
    */
   private void createSourceBranchDefaultMenuItem(MenuManager menuManager) {

      handlerService.activateHandler(addSourceBranchDefaultMenuItem(menuManager),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            BranchView branchView = BranchView.getBranchView();
            if (branchView != null) {
               branchView.setDefaultBranch(conflicts[0].getSourceBranch());
            } else {
               BranchPersistenceManager.getInstance().setDefaultBranch(conflicts[0].getSourceBranch());
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            if (conflicts == null || conflicts.length == 0) return false;
            return conflicts[0].getSourceBranch() != BranchPersistenceManager.getInstance().getDefaultBranch();
         }
      });
   }

   public void explore(final Branch sourceBranch, final Branch destBranch, final TransactionId transactionId) {
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
      this.transactionId = transactionId;
      try {
         xMergeViewer.setInputData(sourceBranch, destBranch, transactionId, this);
         setPartName("Merge Manager: " + sourceBranch.getBranchShortName());

      } catch (Exception ex) {
         OSEELog.logException(MergeView.class, ex, true);
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
         Integer transactionId = null;

         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               sourceBranchId = memento.getInteger(SOURCE_BRANCH_ID);
               final Branch sourceBranch = BranchPersistenceManager.getInstance().getBranch(sourceBranchId);
               if (sourceBranch == null) {
                  OSEELog.logWarning(SkynetGuiPlugin.class,
                        "Merge View can't init due to invalid source branch id " + sourceBranchId, false);
                  xMergeViewer.setLabel("Could not restore this Merge View");
                  return;
               }
               destBranchId = memento.getInteger(DEST_BRANCH_ID);
               final Branch destBranch = BranchPersistenceManager.getInstance().getBranch(destBranchId);
               if (destBranch == null) {
                  OSEELog.logWarning(SkynetGuiPlugin.class,
                        "Merge View can't init due to invalid destination branch id " + sourceBranchId, false);
                  xMergeViewer.setLabel("Could not restore this Merge View");
                  return;
               }
               transactionId = memento.getInteger(TRANSACTION_NUMBER);
               final TransactionId transId =
                     TransactionIdManager.getInstance().getNonEditableTransactionId(transactionId);
               if (transId == null) {
                  OSEELog.logWarning(SkynetGuiPlugin.class,
                        "Merge View can't init due to invalid transaction id " + transactionId, false);
                  xMergeViewer.setLabel("Could not restore this Merge View");
                  return;
               }
               openViewUpon(sourceBranch, destBranch, transId);
            }
         }
      } catch (Exception ex) {
         OSEELog.logWarning(SkynetGuiPlugin.class, "Merge View error on init", ex, false);
      }
   }
   private static final String INPUT = "input";
   private static final String SOURCE_BRANCH_ID = "sourceBranchId";
   private static final String DEST_BRANCH_ID = "destBranchId";
   private static final String TRANSACTION_NUMBER = "transactionNumber";

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento = memento.createChild(INPUT);

      memento.putInteger(SOURCE_BRANCH_ID, sourceBranch.getBranchId());
      memento.putInteger(DEST_BRANCH_ID, destBranch.getBranchId());
      memento.putInteger(TRANSACTION_NUMBER, transactionId.getTransactionNumber());
   }

   private class PreviewHandler extends AbstractSelectionEnabledHandler {
      private static final String PREVIEW_ARTIFACT = "PREVIEW_ARTIFACT";
      private int partToPreview;
      private List<Artifact> artifacts;

      public PreviewHandler(MenuManager menuManager, int partToPreview) {
         super(menuManager);
         this.partToPreview = partToPreview;
      }

      @Override
      public Object execute(ExecutionEvent event) throws ExecutionException {
         if (!artifacts.isEmpty()) {
            rendererManager.previewInJob(artifacts, PREVIEW_ARTIFACT);
         }
         return null;
      }

      @Override
      public boolean isEnabled() {
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
                     if (conflict.statusNotResolvable() || conflict.statusInformational()) return false;
                     if (conflict.getArtifact() != null) {
                        artifacts.add(conflict.getArtifact());
                     }
                     break;
               }
            } catch (Exception ex) {
               OSEELog.logException(MergeView.class, ex, true);
            }
         }

         return accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
      }
   }

   private class DiffHandler extends AbstractSelectionEnabledHandler {
      private int diffToShow;
      private AttributeConflict attributeConflict;
      private ArtifactConflict artifactConflict;
      private List<Artifact> artifacts;

      public DiffHandler(MenuManager menuManager, int diffToShow) {
         super(menuManager);
         this.diffToShow = diffToShow;
      }

      @Override
      public Object execute(ExecutionEvent event) throws ExecutionException {
         if (attributeConflict != null) {
            switch (diffToShow) {
               case 1:
                  MergeUtility.showSourceCompareFile(attributeConflict);
                  break;
               case 2:
                  MergeUtility.showDestCompareFile(attributeConflict);
                  break;
               case 3:
                  MergeUtility.showSourceDestCompareFile(attributeConflict);
                  break;
            }
         } else if (artifactConflict != null) {
            if (diffToShow == 1) {
               MergeUtility.showSourceCompareFile(artifactConflict);
            }
            if (diffToShow == 2) {
               MergeUtility.showDestCompareFile(artifactConflict);
            }
         }
         return null;
      }

      @Override
      public boolean isEnabled() {
         artifacts = new LinkedList<Artifact>();
         List<Conflict> conflicts = xMergeViewer.getSelectedConflicts();
         if (conflicts.size() != 1) return false;
         if (conflicts.get(0) instanceof AttributeConflict) {
            attributeConflict = (AttributeConflict) conflicts.get(0);
            artifactConflict = null;
            try {
               switch (diffToShow) {
                  case 1:
                     if (attributeConflict.getSourceArtifact() != null) {
                        artifacts.add(attributeConflict.getSourceArtifact());
                     } else
                        return false;
                     break;
                  case 2:
                     if (attributeConflict.getDestArtifact() != null) {
                        artifacts.add(attributeConflict.getDestArtifact());
                     } else
                        return false;
                     break;
                  case 3:
                     if (attributeConflict.getDestArtifact() != null && attributeConflict.getSourceArtifact() != null) {
                        artifacts.add(attributeConflict.getSourceArtifact());
                        artifacts.add(attributeConflict.getDestArtifact());
                     } else
                        return false;
                     break;
               }
            } catch (Exception ex) {
               OSEELog.logException(MergeView.class, ex, true);
            }

         } else if (conflicts.get(0) instanceof ArtifactConflict) {

            attributeConflict = null;
            artifactConflict = (ArtifactConflict) conflicts.get(0);
            try {
               switch (diffToShow) {
                  case 1:
                     if (artifactConflict.getSourceArtifact() != null && conflicts.get(0).statusNotResolvable()) {
                        artifacts.add(artifactConflict.getSourceArtifact());
                     } else
                        return false;
                     break;
                  case 2:
                     if (artifactConflict.getDestArtifact() != null && conflicts.get(0).statusInformational()) {
                        artifacts.add(artifactConflict.getDestArtifact());
                     } else
                        return false;
                     break;
                  case 3:
                     return false;
               }
            } catch (Exception ex) {
               OSEELog.logException(MergeView.class, ex, true);
            }

         }
         return accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
      }
   }
}