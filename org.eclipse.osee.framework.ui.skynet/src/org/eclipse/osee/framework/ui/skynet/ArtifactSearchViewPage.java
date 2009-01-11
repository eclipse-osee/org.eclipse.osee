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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.Commands;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers.PreviewWordHandler;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchViewPage;
import org.eclipse.osee.framework.ui.skynet.search.report.RelationMatrixExportJob;
import org.eclipse.osee.framework.ui.skynet.search.report.ReportJob;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.osgi.framework.Bundle;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactSearchViewPage extends AbstractArtifactSearchViewPage implements IRebuildMenuListener, IFrameworkTransactionEventListener, IArtifactsPurgedEventListener {
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

      @Override
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
      viewer.addDoubleClickListener(new ArtifactDoubleClick());

      createContextMenu(viewer.getControl());

      new SearchDragAndDrop(viewer.getTable(), VIEW_ID);

      OseeContributionItem.addTo(this, false);
      getSite().getActionBars().updateActionBars();
      OseeEventManager.addListener(this);
   }

   private void createContextMenu(Control menuOnwer) {
      PlatformUI.getWorkbench().getService(IHandlerService.class);
      handlerService = (IHandlerService) getSite().getService(IHandlerService.class);

      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            fillPopupMenu(manager);
         }
      });

      menuManager.add(new Separator());
      viewer.getTable().setMenu(menuManager.createContextMenu(viewer.getTable()));
      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.ArtifactSearchView", menuManager, viewer);

      // The additions group is a standard group
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

      createOpenWordPreviewChildrenHandler(menuManager, viewer);
      createOpenWordPreviewHandler(menuManager, viewer);
      createOpenWordEditorHandler(menuManager, viewer);
      createOpenNativeEditorHandler(menuManager, viewer);
      createOpenRdtEditorHandler(menuManager, viewer);
      createOpenArtifactHandler(menuManager, viewer);
      createOpenArtifactEditorHandler(menuManager, viewer);
      createOpenInAtsWorldHandler(menuManager, viewer);
      createOpenInAtsTaskHandler(menuManager, viewer);
      createOpenInMassArtifactEditorHandler(menuManager, viewer);
      menuManager.add(new Separator());
      createReportHandler(menuManager, viewer);
      createViewTableHandler(menuManager, viewer);
      menuManager.add(new Separator());
      createShowInExplorerHandler(menuManager, viewer);
      createResourceHistoryHandler(menuManager, viewer);
      menuManager.add(new Separator());
      createSetAllPartitions(menuManager, viewer);
      menuManager.add(new Separator());
   }

   private void fillPopupMenu(IMenuManager Manager) {
      MenuManager menuManager = (MenuManager) Manager;
      menuManager.add(new Separator());

      // The additions group is a standard group
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

      //      addOpenInAtsWorldHandler(menuManager, viewer);
      addOpenInAtsTaskHandler(menuManager, viewer);
      addOpenInMassArtifactEditorHandler(menuManager, viewer);
      menuManager.add(new Separator());
      addReportHandler(menuManager, viewer);
      addViewTableHandler(menuManager, viewer);
      menuManager.add(new Separator());
      addShowInExplorerHandler(menuManager, viewer);
      addResourceHistoryHandler(menuManager, viewer);
      menuManager.add(new Separator());
      addExportHandler(menuManager, viewer);
      menuManager.add(new Separator());
      addSetAllPartitions(menuManager, viewer);
      menuManager.add(new Separator());

   }

   /**
    * @param menuManager
    * @param viewer
    */
   private void addReportHandler(MenuManager menuManager, TableViewer viewer) {
      MenuManager reportManager = new MenuManager("Run Reports");

      addRelationMatrixReportMenuItem(menuManager, reportManager);
      addDynamicReportCommands(menuManager, reportManager);
      menuManager.add(reportManager);
   }

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
   private String addOpenArtifactEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openArtifactCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.artifacteditor.command",
                  getSite(), null, null, null, null, null, null, null, null);
      menuManager.add(openArtifactCommand);

      return openArtifactCommand.getId();
   }

   private void createOpenArtifactEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenArtifactEditorHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            ArtifactEditor.editArtifacts(getSelectedArtifacts(viewer));
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            boolean isEnabled = true;
            List<Artifact> artifacts = getSelectedArtifacts(viewer);
            isEnabled = accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
            return isEnabled;
         }
      });
   }

   /**
    * @param menuManager
    * @param viewer
    */
   private String addOpenArtifactHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openArtifactEditorCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.open.command", getSite(), null,
                  null, null, null, null, null, null, null);
      menuManager.add(openArtifactEditorCommand);

      return openArtifactEditorCommand.getId();
   }

   private void createOpenArtifactHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenArtifactHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            try {
               RendererManager.previewInJob(getSelectedArtifacts(viewer));
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            boolean isEnabled = true;
            List<Artifact> artifacts = getSelectedArtifacts(viewer);
            isEnabled = accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
            //whole word artifacts can only be viewed as a single document
            for (Artifact artifact : artifacts) {
               if (artifact.isOfType(WordArtifact.WHOLE_WORD)) {
                  isEnabled &= artifacts.size() == 1;
                  break;
               }
            }
            return isEnabled;
         }
      });
   }

   private String addOpenWordPreviewChildrenHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem command =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.wordpreviewChildren.command",
                  getSite(), null, null, null, null, null, null, null, null);
      menuManager.add(command);

      return command.getId();
   }

   private void createOpenWordPreviewChildrenHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenWordPreviewChildrenHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            WordTemplateRenderer renderer = new WordTemplateRenderer(WordTemplateRenderer.RENDERER_EXTENSION);
            try {
               renderer.setOptions(new VariableMap(ITemplateRenderer.PREVIEW_WITH_RECURSE_OPTION_PAIR));
               renderer.preview(getSelectedArtifacts(viewer));
            } catch (OseeCoreException ex) {
               OseeLog.log(PreviewWordHandler.class, Level.SEVERE, ex);
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            boolean isEnabled = true;
            List<Artifact> artifacts = getSelectedArtifacts(viewer);
            isEnabled = accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
            return isEnabled;
         }
      });
   }

   /**
    * @param menuManager
    * @param viewer
    */
   private String addOpenWordPreviewHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem command =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.wordpreview.command", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(command);

      return command.getId();
   }

   private void createOpenWordPreviewHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenWordPreviewHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            WordTemplateRenderer renderer = new WordTemplateRenderer(WordTemplateRenderer.RENDERER_EXTENSION);
            try {
               renderer.preview(getSelectedArtifacts(viewer));
            } catch (OseeCoreException ex) {
               OseeLog.log(PreviewWordHandler.class, Level.SEVERE, ex);
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            boolean isEnabled = true;
            List<Artifact> artifacts = getSelectedArtifacts(viewer);
            isEnabled = accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
            return isEnabled;
         }
      });
   }

   /**
    * @param menuManager
    * @param viewer
    */
   private String addOpenRdtEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem command =
            Commands.getLocalCommandContribution("lba.ui.rdt.rdteditor.command", getSite(), null, null, null, null,
                  null, null, null, null);
      menuManager.add(command);

      return command.getId();
   }

   private void createOpenRdtEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenRdtEditorHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            RendererManager.openInJob(getSelectedArtifacts(viewer), PresentationType.SPECIALIZED_EDIT);
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            boolean isEnabled = true;
            List<Artifact> artifacts = getSelectedArtifacts(viewer);
            isEnabled = accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.WRITE);
            return isEnabled;
         }
      });
   }

   /**
    * @param menuManager
    * @param viewer
    */
   private String addOpenWordEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem command =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.wordeditor.command", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(command);

      return command.getId();
   }

   private void createOpenWordEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenWordEditorHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            RendererManager.openInJob(getSelectedArtifacts(viewer), PresentationType.SPECIALIZED_EDIT);
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            boolean isEnabled = true;
            List<Artifact> artifacts = getSelectedArtifacts(viewer);
            isEnabled = accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.WRITE);
            return isEnabled;
         }
      });
   }

   /**
    * @param menuManager
    * @param viewer
    */
   private String addOpenNativeEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem command =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.nativeeditor.command",
                  getSite(), null, null, null, null, null, null, null, null);
      menuManager.add(command);

      return command.getId();
   }

   private void createOpenNativeEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenNativeEditorHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            RendererManager.openInJob(getSelectedArtifacts(viewer), PresentationType.SPECIALIZED_EDIT);
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            boolean isEnabled = true;
            List<Artifact> artifacts = getSelectedArtifacts(viewer);
            isEnabled = accessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
            return isEnabled;
         }
      });
   }

   private String addViewTableHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openArtifactsCommand =
            Commands.getLocalCommandContribution(ArtifactSearchViewPage.VIEW_ID, getSite(), "viewTableCommand",
                  "View Table Report", null, null, null, "V", null, null);

      menuManager.add(openArtifactsCommand);

      return openArtifactsCommand.getId();
   }

   private void createViewTableHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addViewTableHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            (new TableViewerReport(viewer)).open();
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            return true;
         }
      });
   }

   private void addDynamicReportCommands(MenuManager parentMenuManager, MenuManager childMenuManager) {
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
                     addReportJobCommand(parentMenuManager, childMenuManager, reportJob);
                  } catch (Exception ex) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                  } catch (NoClassDefFoundError er) {
                     OseeLog.log(
                           SkynetGuiPlugin.class,
                           Level.WARNING,
                           "Failed to find a class definition for " + classname + ", registered from bundle " + bundleName,
                           er);
                  }
               }
            }
         }
      }
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
                     OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                  } catch (NoClassDefFoundError er) {
                     OseeLog.log(
                           SkynetGuiPlugin.class,
                           Level.WARNING,
                           "Failed to find a class definition for " + classname + ", registered from bundle " + bundleName,
                           er);
                  }
               }
            }
         }
      }
   }

   private String addReportJobCommand(MenuManager parentMenuManager, MenuManager childMenuManager, final ReportJob reportJob) {
      CommandContributionItem reportCommand =
            Commands.getLocalCommandContribution(
                  "org.eclipse.osee.framework.ui.skynet." + reportJob.getName() + ".command", getSite(), null,
                  reportJob.getName(), null, null, null, null, null, null);
      childMenuManager.add(reportCommand);
      return reportCommand.getId();
   }

   private void createReportJobCommand(MenuManager parentMenuManager, MenuManager childMenuManager, final ReportJob reportJob) {
      String id = addReportJobCommand(parentMenuManager, childMenuManager, reportJob);

      handlerService.activateHandler(id,

      new AbstractSelectionEnabledHandler(parentMenuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            Jobs.startJob(reportJob);
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            return true;
         }
      });
   }

   private void addRelationMatrixReportMenuItem(MenuManager menuManager, MenuManager reportManager) {
      MenuManager matrixManager = new MenuManager("Relation Matrix Reports");

      try {
         for (RelationType descriptor : RelationTypeManager.getValidTypes(BranchManager.getDefaultBranch())) {
            final ReportJob reportJob = new RelationMatrixExportJob(descriptor);
            addReportJobCommand(menuManager, matrixManager, reportJob);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      reportManager.add(matrixManager);
   }

   private void createRelationMatrixReportMenuItem(MenuManager menuManager, MenuManager reportManager) {
      MenuManager matrixManager = new MenuManager("Relation Matrix Reports");
      try {
         for (RelationType descriptor : RelationTypeManager.getValidTypes(BranchManager.getDefaultBranch())) {
            final ReportJob reportJob = new RelationMatrixExportJob(descriptor);
            createReportJobCommand(menuManager, matrixManager, reportJob);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      reportManager.add(matrixManager);
   }

   private String addExportHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openArtifactsCommand =
            Commands.getLocalCommandContribution(ArtifactSearchViewPage.VIEW_ID, getSite(), "createExportCommand",
                  "Export Artifact(s)", null, null, null, "V", null, null);
      menuManager.add(openArtifactsCommand);

      return openArtifactsCommand.getId();
   }

   private String addResourceHistoryHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem resourceHistoryCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.resource.command", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(resourceHistoryCommand);

      return resourceHistoryCommand.getId();
   }

   private void createResourceHistoryHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addResourceHistoryHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            Artifact selectedArtifact = getSelectedArtifact(viewer);
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               RevisionHistoryView revisionHistoryView =
                     (RevisionHistoryView) page.showView(RevisionHistoryView.VIEW_ID, selectedArtifact.getGuid(),
                           IWorkbenchPage.VIEW_VISIBLE);
               revisionHistoryView.explore(selectedArtifact);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            return true;
         }
      });
   }

   private String addShowInExplorerHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem showInArtifactExplorerCommand =
            Commands.getLocalCommandContribution(
                  "org.eclipse.osee.framework.ui.skynet.revealArtifactInExplorer.command", getSite(), null, null, null,
                  null, null, null, null, null);
      menuManager.add(showInArtifactExplorerCommand);

      return showInArtifactExplorerCommand.getId();
   }

   private void createShowInExplorerHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addShowInExplorerHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            Artifact artifact = getSelectedArtifact(viewer);
            ArtifactExplorer.revealArtifact(artifact);
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            return true;
         }
      });
   }

   private String addOpenInMassArtifactEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem editArtifactCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.OpenMassEditcommand", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(editArtifactCommand);

      return editArtifactCommand.getId();
   }

   private void createOpenInMassArtifactEditorHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenInMassArtifactEditorHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            MassArtifactEditor.editArtifacts("", getSelectedArtifacts(viewer));
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            return accessControlManager.checkObjectListPermission(getSelectedArtifacts(viewer), PermissionEnum.WRITE);
         }
      });
   }

   private String addOpenInAtsWorldHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openInAtsWorldCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.atseditor.command", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(openInAtsWorldCommand);

      return openInAtsWorldCommand.getId();
   }

   private void createOpenInAtsWorldHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenInAtsWorldHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            try {
               if (OseeAts.getAtsLib() != null) OseeAts.getAtsLib().openInAtsWorldEditor("ATS",
                     getSelectedArtifacts(viewer));
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            return true;
         }
      });
   }

   private String addOpenInAtsTaskHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openInAtsTaskCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.openInAtsTaskEditor", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(openInAtsTaskCommand);

      return openInAtsTaskCommand.getId();
   }

   private void createOpenInAtsTaskHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenInAtsTaskHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            try {
               if (OseeAts.getAtsLib() != null) OseeAts.getAtsLib().openInAtsTaskEditor("Tasks",
                     getSelectedArtifacts(viewer));
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            return true;
         }
      });
   }

   private String addSetAllPartitions(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem setAllPartitionsCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.SetAllPartitions", getSite(),
                  null, "Set all Partitions", null, null, null, null, null, null);
      menuManager.add(setAllPartitionsCommand);

      return setAllPartitionsCommand.getId();
   }

   private void createSetAllPartitions(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addSetAllPartitions(menuManager, viewer),

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
               try {
                  SkynetTransaction transaction = new SkynetTransaction(BranchManager.getDefaultBranch());

                  for (Artifact art : arts) {
                     for (String partition : partitions) {
                        boolean found = false;
                        for (Attribute<?> attr : art.getAttributes(Requirements.PARTITION)) {
                           if (attr.toString().equals(partition)) {
                              found = true;
                              break;
                           }
                        }
                        if (!found) {
                           art.addAttribute(Requirements.PARTITION, partition);
                        }
                     }
                     for (Attribute<?> attr : art.getAttributes(Requirements.PARTITION)) {
                        if (attr.toString().equals("Unspecified")) attr.delete();
                     }

                     art.persistAttributes(transaction);
                  }
                  transaction.execute();
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            return AccessControlManager.isOseeAdmin() && accessControlManager.checkObjectListPermission(
                  getSelectedArtifacts(viewer), PermissionEnum.WRITE);
         }
      });
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
      OseeEventManager.removeListener(this);
      super.dispose();
   }

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
      return getSelectedArtifacts(viewer);
   }

   /**
    * @return the viewer
    */
   @Override
   public TableViewer getViewer() {
      return viewer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            if (viewer != null) {
               viewer.remove(transData.cacheDeletedArtifacts);
               viewer.refresh();
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener#handleArtifactsPurgedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleArtifactsPurgedEvent(Sender sender, final LoadedArtifacts loadedArtifacts) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            try {
               if (viewer != null) {
                  viewer.remove(loadedArtifacts.getLoadedArtifacts());
                  viewer.refresh();
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener#rebuildMenu()
    */
   @Override
   public void rebuildMenu() {
      //      createContextMenu(viewer.getControl());
   }
}
