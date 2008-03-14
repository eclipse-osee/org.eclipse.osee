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

package org.eclipse.osee.framework.ui.skynet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.CacheArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.TransactionArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.CacheRelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.TransactionRelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.Commands;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchViewPage;
import org.eclipse.osee.framework.ui.skynet.search.ArtifactExportJob;
import org.eclipse.osee.framework.ui.skynet.search.ArtifactSearchResult;
import org.eclipse.osee.framework.ui.skynet.search.report.RelationMatrixExportJob;
import org.eclipse.osee.framework.ui.skynet.search.report.ReportJob;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.osgi.framework.Bundle;

public class ArtifactSearchViewPage extends AbstractArtifactSearchViewPage implements IEventReceiver {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactSearchViewPage.class);
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final AccessControlManager accessControlManager = AccessControlManager.getInstance();
   private static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynetd.ArtifactSearchView";
   private IHandlerService handlerService;
   private TableViewer viewer;
   private ArtifactLabelProvider artifactLabelProvider;

   private static String[] partitions =
         new String[] {"ACS", "ARC201", "ARC231", "ASE", "CND", "COMM", "HM", "IOP", "MPEGP", "MPEGR", "MSM", "NAV",
               "NCO", "REND_H", "REND_L", "REND_R", "REND_T", "SSRD_GW", "USM", "VAM", "WPS"};

   public static class DecoratorIgnoringViewerSorter extends ViewerSorter {
      private final ILabelProvider aLabelProvider;

      public DecoratorIgnoringViewerSorter(ILabelProvider labelProvider) {
         super(null); // lazy initialization
         aLabelProvider = labelProvider;
      }

      @SuppressWarnings("unchecked")
      public int compare(Viewer viewer, Object e1, Object e2) {
         String name1 = aLabelProvider.getText(e1);
         String name2 = aLabelProvider.getText(e2);
         if (name1 == null) name1 = "";
         if (name2 == null) name2 = "";
         return getComparator().compare(name1, name2);
      }
   }

   private ArtifactListContentProvider aContentProvider;

   public ArtifactSearchViewPage() {
   }

   @Override
   protected void configureTableViewer(final TableViewer viewer) {
      viewer.setUseHashlookup(true);
      this.viewer = viewer;

      artifactLabelProvider = new ArtifactLabelProvider();

      viewer.setLabelProvider(new DecoratingLabelProvider(artifactLabelProvider,
            PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

      aContentProvider = new ArtifactListContentProvider(this);
      viewer.setContentProvider(aContentProvider);
      viewer.setSorter(new DecoratorIgnoringViewerSorter(artifactLabelProvider));
      //      globalMenuHelper = new ArtifactTableViewerGlobalMenuHelper(viewer);

      viewer.addDoubleClickListener(new ArtifactDoubleClick());

      createContextMenu(viewer.getControl());

      new SearchDragAndDrop(viewer.getTable(), VIEW_ID);

      SkynetContributionItem.addTo(this, false);
      getSite().getActionBars().updateActionBars();
   }

   @Override
   public void setInput(ISearchResult search, Object viewState) {
      if (search != null) {
         artifactLabelProvider.showBranch(((ArtifactSearchResult) search).getQuery().showBranch());
      }
      super.setInput(search, viewState);
   }

   private void createContextMenu(Control menuOnwer) {
      PlatformUI.getWorkbench().getService(IHandlerService.class);
      handlerService = (IHandlerService) getSite().getService(IHandlerService.class);

      MenuManager menuManager = new MenuManager();
      menuManager.add(new Separator());
      viewer.getTable().setMenu(menuManager.createContextMenu(viewer.getTable()));
      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.ArtifactSearchView", menuManager, viewer);

      createReportHandler(menuManager, viewer);
      createViewTableHandler(menuManager, viewer);
      menuManager.add(new Separator());
      createShowInExplorerHandler(menuManager, viewer);
      createResourceHistoryHandler(menuManager, viewer);
      menuManager.add(new Separator());
      createExportHandler(menuManager, viewer);
      menuManager.add(new Separator());
      createOpenArtifactHandler(menuManager, viewer);
      createOpenInAtsWorldHandler(menuManager, viewer);
      createEditArtifactHandler(menuManager, viewer);
      createPreviewArtifactHandler(menuManager, viewer);
      createOpenInMassArtifactEditorHandler(menuManager, viewer);
      menuManager.add(new Separator());
      createSetAllPartitions(menuManager, viewer);
      menuManager.add(new Separator());
      createDeleteArtifactHandler(menuManager, viewer);
      createPurgeArtifactHandler(menuManager, viewer);

      // The additions group is a standard group
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
   }

   /**
    * @param menuManager
    * @param viewer
    */
   private void createPurgeArtifactHandler(MenuManager menuManager, final TableViewer viewer) {

      CommandContributionItem purgeArtifactCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.purge.command", getSite(), null,
                  null, null, null, null, null, null, null);
      menuManager.add(purgeArtifactCommand);

      handlerService.activateHandler(purgeArtifactCommand.getId(), new AbstractSelectionEnabledHandler(menuManager) {

         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            final List<Artifact> artifacts = getSelectedArtifacts(viewer);

            if (MessageDialog.openConfirm(
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Confirm Artifact Purge ",
                  " Are you sure you want to purge this artifact, all of " + "its children and all history associated with these artifacts from the database ?")) {
               Job job = new Job("Purge artifact") {

                  @Override
                  protected IStatus run(IProgressMonitor monitor) {
                     IStatus toReturn = Status.CANCEL_STATUS;
                     monitor.beginTask("Purge artifact", artifacts.size());
                     final IProgressMonitor fMonitor = monitor;

                     AbstractSkynetTxTemplate purgeTx =
                           new AbstractSkynetTxTemplate(artifacts.iterator().next().getBranch()) {
                              @Override
                              protected void handleTxWork() throws Exception {
                                 for (Artifact artifactToPurge : artifacts) {
                                    if (!artifactToPurge.isDeleted()) {
                                       fMonitor.setTaskName("Purge: " + artifactToPurge.getDescriptiveName());
                                       artifactToPurge.purge();
                                    }
                                    fMonitor.worked(1);
                                 }
                                 fMonitor.done();
                              }
                           };

                     // Perform the purge transaction
                     try {
                        purgeTx.execute();
                        toReturn = Status.OK_STATUS;
                     } catch (Exception ex) {
                        OSEELog.logException(SkynetGuiPlugin.class, ex, false);
                        toReturn = new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, -1, ex.getMessage(), ex);
                     } finally {
                        monitor.done();
                     }
                     return toReturn;
                  }
               };
               Jobs.startJob(job);
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            if (PlatformUI.getWorkbench().isClosing()) {
               return false;
            }
            boolean isEnabled =
                  OseeProperties.getInstance().isDeveloper() && accessControlManager.checkObjectListPermission(
                        getSelectedArtifacts(viewer), PermissionEnum.WRITE);
            return isEnabled;
         }
      });
   }

   /**
    * @param menuManager
    * @param viewer
    */
   private void createDeleteArtifactHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem deleteArtifactCommand =
            Commands.getLocalCommandContribution("org.eclipse.ui.edit.delete", getSite(), null, null, null, null, null,
                  null, null, null);
      menuManager.add(deleteArtifactCommand);

      handlerService.activateHandler(deleteArtifactCommand.getId(), new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            try {
               MessageDialog dialog =
                     new MessageDialog(Display.getCurrent().getActiveShell(), "Confirm Artifact Deletion", null,
                           " Are you sure you want to delete this artifact and all of the default hierarchy children?",
                           MessageDialog.QUESTION,
                           new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
               if (dialog.open() == 0) {
                  ArtifactPersistenceManager.getInstance().deleteArtifact(
                        getSelectedArtifacts(viewer).toArray(Artifact.EMPTY_ARRAY));
               }
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            return accessControlManager.checkObjectListPermission(getSelectedArtifacts(viewer), PermissionEnum.WRITE);
         }
      });
   }

   /**
    * @param menuManager
    * @param viewer
 * @throws SQLException 
    */
   private void createReportHandler(MenuManager menuManager, TableViewer viewer) {
      MenuManager reportManager = new MenuManager("Run Reports");

      createRelationMatrixReportMenuItem(menuManager, reportManager);
      createDynamicReportCommands(menuManager, reportManager);
      menuManager.add(reportManager);
   }

   /**
    * @param menuManager
    * @param viewer
    */
   private void createOpenArtifactHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openArtifactCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.openInEdit.command", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(openArtifactCommand);

      handlerService.activateHandler(openArtifactCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            ArtifactEditor.editArtifacts(getSelectedArtifacts(viewer));
            return null;
         }

         @Override
         public boolean isEnabled() {
            return true;
         }
      });
   }

   /**
    * @param menuManager
    * @param viewer
    */
   private void createEditArtifactHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem editArtifactCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.edit.command", getSite(), null,
                  null, null, null, null, null, null, null);
      menuManager.add(editArtifactCommand);

      handlerService.activateHandler(editArtifactCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            RendererManager.getInstance().editInJob(getSelectedArtifacts(viewer));
            return null;
         }

         @Override
         public boolean isEnabled() {
            return accessControlManager.checkObjectListPermission(getSelectedArtifacts(viewer), PermissionEnum.WRITE);
         }
      });
   }

   /**
    * @param menuManager
    * @param viewer
    */
   private void createPreviewArtifactHandler(MenuManager menuManager, final TableViewer viewer) {
      MenuManager previewMenu = new MenuManager("Preview");

      CommandContributionItem previewArtifactCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.previewArtifact.command",
                  getSite(), null, null, null, null, null, null, null, null);
      previewMenu.add(previewArtifactCommand);

      handlerService.activateHandler(previewArtifactCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            RendererManager.getInstance().previewInJob(getSelectedArtifacts(viewer), "PREVIEW_ARTIFACT");
            return null;
         }

         @Override
         public boolean isEnabled() {
            return accessControlManager.checkObjectListPermission(getSelectedArtifacts(viewer), PermissionEnum.READ);
         }
      });

      CommandContributionItem previewArtifactRecurseCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.previewArtifactRecurse.command",
                  getSite(), null, null, null, null, null, null, null, null);
      previewMenu.add(previewArtifactRecurseCommand);

      handlerService.activateHandler(previewArtifactRecurseCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            RendererManager.getInstance().previewInJob(getSelectedArtifacts(viewer), "PREVIEW_WITH_RECURSE");
            return null;
         }

         @Override
         public boolean isEnabled() {
            return accessControlManager.checkObjectListPermission(getSelectedArtifacts(viewer), PermissionEnum.READ);
         }
      });

      menuManager.add(previewMenu);
   }

   private void createViewTableHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openArtifactsCommand =
            Commands.getLocalCommandContribution(ArtifactSearchViewPage.VIEW_ID, getSite(), "viewTableCommand",
                  "View Table Report", null, null, null, "V", null, null);

      menuManager.add(openArtifactsCommand);

      handlerService.activateHandler(openArtifactsCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            (new TableViewerReport(viewer)).open();
            return null;
         }

         @Override
         public boolean isEnabled() {
            return true;
         }
      });
   }

   private void createDynamicReportCommands(MenuManager parentMenuManager, MenuManager childMenuManager) {
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.ui.skynet.ArtifactReport");
      for (IExtension extension : point.getExtensions()) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement element : elements) {
            if (element.getName().equals("report")) {
               classname = element.getAttribute("class");
               bundleName = element.getContributor().getName();

               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> reportClass = bundle.loadClass(classname);
                     Object obj = reportClass.newInstance();
                     ReportJob reportJob = (ReportJob) obj;
                     createReportJobCommand(parentMenuManager, childMenuManager, reportJob);
                  } catch (Exception ex) {
                     logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                  } catch (NoClassDefFoundError er) {
                     logger.log(
                           Level.WARNING,
                           "Failed to find a class definition for " + classname + ", registered from bundle " + bundleName,
                           er);
                  }
               }
            }
         }
      }
   }

   private void createReportJobCommand(MenuManager parentMenuManager, MenuManager childMenuManager, final ReportJob reportJob) {
      CommandContributionItem reportCommand =
            Commands.getLocalCommandContribution(
                  "org.eclipse.osee.framework.ui.skynet." + reportJob.getName() + ".command", getSite(), null,
                  reportJob.getName(), null, null, null, null, null, null);
      childMenuManager.add(reportCommand);

      handlerService.activateHandler(reportCommand.getId(),

      new AbstractSelectionEnabledHandler(parentMenuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            Jobs.startJob(reportJob);
            return null;
         }

         @Override
         public boolean isEnabled() {
            return true;
         }
      });
   }

   private void createRelationMatrixReportMenuItem(MenuManager menuManager, MenuManager reportManager) {
      MenuManager matrixManager = new MenuManager("Relation Matrix Reports");
      RelationPersistenceManager relationManager = RelationPersistenceManager.getInstance();
      try {
		for (IRelationLinkDescriptor descriptor : relationManager.getIRelationLinkDescriptors(branchManager.getDefaultBranch())) {
		     final ReportJob reportJob = new RelationMatrixExportJob(descriptor);
		     createReportJobCommand(menuManager, matrixManager, reportJob);
		  }
	} catch (SQLException ex) {
		OSEELog.logException(SkynetGuiPlugin.class, ex, true);
	}
      reportManager.add(matrixManager);
   }

   private void createExportHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openArtifactsCommand =
            Commands.getLocalCommandContribution(ArtifactSearchViewPage.VIEW_ID, getSite(), "createExportCommand",
                  "Export Artifact(s)", null, null, null, "V", null, null);
      menuManager.add(openArtifactsCommand);

      handlerService.activateHandler(openArtifactsCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            try {
               Jobs.startJob(new ArtifactExportJob(viewer));
            } catch (Exception ex) {
               logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            return true;
         }
      });
   }

   private void createResourceHistoryHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem resourceHistoryCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.resource.command", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(resourceHistoryCommand);

      handlerService.activateHandler(resourceHistoryCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            Artifact selectedArtifact = getSelectedArtifact(viewer);
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               RevisionHistoryView revisionHistoryView =
                     (RevisionHistoryView) page.showView(RevisionHistoryView.VIEW_ID, selectedArtifact.getGuid(),
                           IWorkbenchPage.VIEW_ACTIVATE);
               revisionHistoryView.explore(selectedArtifact);
            } catch (Exception ex) {
               logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            return true;
         }
      });
   }

   //   private void createHistoryMenuItem(Menu parentMenu, final TableViewer viewer) {
   //      revisionMenuItem = new MenuItem(parentMenu, SWT.PUSH);
   //      revisionMenuItem.setText("&Show Resource History ");
   //      revisionMenuItem.addSelectionListener(new SelectionAdapter() {
   //
   //         public void widgetSelected(SelectionEvent e) {
   //            Artifact selectedArtifact = getSelectedArtifact(viewer);
   //            IWorkbenchPage page = AWorkbench.getActivePage();
   //            try {
   //               RevisionHistoryView revisionHistoryView =
   //                     (RevisionHistoryView) page.showView(RevisionHistoryView.VIEW_ID, selectedArtifact.getGuid(),
   //                           IWorkbenchPage.VIEW_ACTIVATE);
   //               revisionHistoryView.explore(selectedArtifact);
   //            } catch (Exception ex) {
   //               logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
   //            }
   //         }
   //      });
   //   }

   private void createShowInExplorerHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem showInArtifactExplorerCommand =
            Commands.getLocalCommandContribution(
                  "org.eclipse.osee.framework.ui.skynet.revealArtifactInExplorer.command", getSite(), null, null, null,
                  null, null, null, null, null);
      menuManager.add(showInArtifactExplorerCommand);

      handlerService.activateHandler(showInArtifactExplorerCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            Artifact artifact = getSelectedArtifact(viewer);
            try {
               ArtifactExplorer.revealArtifact(artifact.getGuid(), artifact.getBranch());
            } catch (PartInitException ex) {
               throw new ExecutionException(ex.getLocalizedMessage());
            } catch (SQLException ex) {
               throw new ExecutionException(ex.getLocalizedMessage());
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            return true;
         }
      });
   }

   private void createOpenInMassArtifactEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem editArtifactCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.OpenMassEditcommand", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(editArtifactCommand);

      handlerService.activateHandler(editArtifactCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            MassArtifactEditor.editArtifacts("", getSelectedArtifacts(viewer));
            return null;
         }

         @Override
         public boolean isEnabled() {
            return accessControlManager.checkObjectListPermission(getSelectedArtifacts(viewer), PermissionEnum.WRITE);
         }
      });
   }

   private void createOpenInAtsWorldHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openInAtsWorldCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.openInAtsWorld", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(openInAtsWorldCommand);

      handlerService.activateHandler(openInAtsWorldCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            try {
               if (OseeAts.getAtsLib() != null) OseeAts.getAtsLib().openInAtsWorld("", getSelectedArtifacts(viewer));
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            return true;
         }
      });
   }

   private void createSetAllPartitions(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem setAllPartitionsCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.SetAllPartitions", getSite(),
                  null, "Set all Partitions", null, null, null, null, null, null);
      menuManager.add(setAllPartitionsCommand);

      handlerService.activateHandler(setAllPartitionsCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            StringBuffer sb = new StringBuffer();
            final Set<Artifact> arts = new HashSet<Artifact>();
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            Iterator<?> iter = selection.iterator();
            while (iter.hasNext()) {
               Artifact art = (Artifact) ((Match) iter.next()).getElement();
               arts.add(art);
               sb.append(art.getDescriptiveName() + "\n");
            }
            if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Set All Partitions",
                  "Set All Partitions on Artifacts\n\n" + sb.toString())) {

               AbstractSkynetTxTemplate partitionsTx = new AbstractSkynetTxTemplate(branchManager.getDefaultBranch()) {

                  @Override
                  protected void handleTxWork() throws Exception {
                     for (Artifact art : arts) {
                        DynamicAttributeManager dam = art.getAttributeManager("Partition");
                        for (String partition : partitions) {
                           boolean found = false;
                           for (Attribute attr : dam.getAttributes()) {
                              if (attr.getStringData().equals(partition)) {
                                 found = true;
                                 break;
                              }
                           }
                           if (!found) dam.getNewAttribute().setStringData(partition);
                        }
                        for (Attribute attr : dam.getAttributes()) {
                           if (attr.getStringData().equals("Unspecified")) attr.delete();
                        }

                        art.persistAttributes();
                     }
                  }
               };
               try {
                  partitionsTx.execute();
               } catch (Exception ex) {
                  OSEELog.logException(getClass(), ex, false);
               }
            }
            return null;
         }

         @Override
         public boolean isEnabled() {
            return OseeProperties.getInstance().isDeveloper() && accessControlManager.checkObjectListPermission(
                  getSelectedArtifacts(viewer), PermissionEnum.WRITE);
         }
      });
   }

   private void registerForEvents() {
      eventManager.unRegisterAll(this);

      eventManager.register(CacheArtifactModifiedEvent.class, this);
      eventManager.register(CacheRelationModifiedEvent.class, this);
      eventManager.register(TransactionRelationModifiedEvent.class, this);
      eventManager.register(TransactionArtifactModifiedEvent.class, this);
   }

   @Override
   protected void elementsChanged(Object[] objects) {
      if (aContentProvider != null) {
         aContentProvider.elementsChanged(objects);
      }
   }

   private Artifact getSelectedArtifact(TableViewer viewer) {
      IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
      if (selection.size() > 0) {
         return (Artifact) ((Match) selection.getFirstElement()).getElement();
      }
      return null;
   }

   private List<Artifact> getSelectedArtifacts(TableViewer viewer) {
      IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
      Object[] objects = selection.toArray();
      LinkedList<Artifact> artifacts = new LinkedList<Artifact>();

      if (objects.length == 0) return new ArrayList<Artifact>(0);

      if (objects[0] instanceof Match) {
         for (Object object : objects) {
            artifacts.add((Artifact) ((Match) object).getElement());
         }
      }
      return artifacts;
   }

   @Override
   public void dispose() {
      eventManager.unRegisterAll(this);
      super.dispose();
   }

   public void onEvent(Event event) {
      if (viewer != null) {
         viewer.refresh();
      }
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }

   //   public class ArtifactMenuListener implements MenuListener {
   //
   //      public void menuHidden(MenuEvent e) {
   //      }
   //
   //      public void menuShown(MenuEvent e) {
   //         // Use this menu listener until all menu items can be moved to GlobaMenu
   //         GlobalMenuPermissions permiss = new GlobalMenuPermissions(globalMenuHelper);
   //
   //         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
   //         addTemplateItem.setEnabled(selection.size() == 1 && ((Match) selection.getFirstElement()).getElement() instanceof WordRenderer);
   //
   //         openMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isWritePermission());
   //         openInAtsWorldMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isWritePermission());
   //         openInMassEditorMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isWritePermission());
   //         skywalkerEditorMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isReadPermission());
   //         // previewMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isReadPermission());
   //         editItem.setEnabled(permiss.isHasArtifacts() && permiss.isWritePermission());
   //         revisionMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isReadPermission());
   //         showInExplorerMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isReadPermission());
   //         exportMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isReadPermission());
   //         setAllPartitionsMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isWritePermission());
   //      }
   //   }

   private class SearchDragAndDrop extends SkynetDragAndDrop {

      public SearchDragAndDrop(Table table, String viewId) {
         super(table, viewId);
      }

      @Override
      public Artifact[] getArtifacts() {
         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

         Object[] matches = selection.toArray();
         Artifact[] artifacts = new Artifact[matches.length];

         for (int index = 0; index < matches.length; index++) {
            Match match = (Match) matches[index];
            artifacts[index] = (Artifact) match.getElement();
         }

         return artifacts;
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         event.detail = DND.DROP_NONE;
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper#getSelectedArtifacts()
    */
   public Collection<Artifact> getSelectedArtifacts() {
      registerForEvents();
      return getSelectedArtifacts(viewer);
   }

   /**
    * @return the viewer
    */
   public TableViewer getViewer() {
      return viewer;
   }
}
