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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
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
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.branch.BranchLabelProvider;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactTableViewerGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenuListener;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenuPermissions;
import org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu.GlobalMenuItem;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchViewPage;
import org.eclipse.osee.framework.ui.skynet.search.ArtifactExportJob;
import org.eclipse.osee.framework.ui.skynet.search.ArtifactSearchResult;
import org.eclipse.osee.framework.ui.skynet.search.report.RelationMatrixExportJob;
import org.eclipse.osee.framework.ui.skynet.search.report.ReportJob;
import org.eclipse.osee.framework.ui.skynet.search.report.ReportSelectionListener;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
import org.osgi.framework.Bundle;

public class ArtifactSearchViewPage extends AbstractArtifactSearchViewPage implements IEventReceiver {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactSearchViewPage.class);
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynetd.ArtifactSearchView";
   private IHandlerService handlerService;
   private StatusLineContributionItem branchStatusItem;
   //   private MenuItem viewRelationTreeItem;
   private TableViewer viewer;
   private MenuItem openMenuItem;
   private MenuItem skywalkerEditorMenuItem;
   private MenuItem openInMassEditorMenuItem;
   private MenuItem openInAtsWorldMenuItem;
   private MenuItem editItem;
   private MenuItem addTemplateItem;
   private MenuItem revisionMenuItem;
   private MenuItem showInExplorerMenuItem;
   private MenuItem exportMenuItem;
   private MenuItem setAllPartitionsMenuItem;
   private ArtifactLabelProvider artifactLabelProvider;
   private IGlobalMenuHelper globalMenuHelper;

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
      branchStatusItem = new StatusLineContributionItem("skynet.branch", true, 30);
      branchStatusItem.setImage(SkynetGuiPlugin.getInstance().getImage("branch.gif"));
      branchStatusItem.setToolTipText("The branch that the artifacts in the explorer are from.");
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
      globalMenuHelper = new ArtifactTableViewerGlobalMenuHelper(viewer);

      viewer.addDoubleClickListener(new ArtifactDoubleClick());

      createContextMenu(viewer.getControl());

      new SearchDragAndDrop(viewer.getTable(), VIEW_ID);

      getSite().getActionBars().getStatusLineManager().add(branchStatusItem);
      SkynetContributionItem.addTo(this, false);
      getSite().getActionBars().updateActionBars();
   }

   private GlobalMenuListener globalMenuListener = new GlobalMenuListener() {

      public void actioned(GlobalMenuItem item, Collection<Artifact> artifacts) {
         if (item == GlobalMenuItem.DeleteArtifacts || item == GlobalMenuItem.PurgeArtifacts) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  viewer.refresh();
               };
            });
         }
      }

      // Must remove items from search result prior to delete
      public Result actioning(GlobalMenuItem item, Collection<Artifact> artifacts) {
         if (item == GlobalMenuItem.DeleteArtifacts || item == GlobalMenuItem.PurgeArtifacts) {
            AbstractArtifactSearchResult search = (AbstractArtifactSearchResult) viewer.getInput();
            for (Object object : search.getElements()) {

               Artifact searchArtifact = ((Artifact) ((Match) object).getElement());
               if (artifacts.contains(searchArtifact)) {
                  search.removeMatch((Match) object);
                  try {
                     search.removeArtifacts(searchArtifact.getChildren());
                  } catch (SQLException ex) {
                     OSEELog.logException(SkynetGuiPlugin.class, ex, false);
                  }
               }
            }
         }
         return Result.TrueResult;
      }

   };

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
      //      Menu popupMenu = new Menu(menuOnwer.getParent());
      //      ArtifactMenuListener listener = new ArtifactMenuListener();
      //      popupMenu.addMenuListener(listener);
      //
      //      createReportMenuItem(popupMenu, viewer);
      //      * createViewTableMenuItem(popupMenu, viewer);
      //      * new MenuItem(popupMenu, SWT.SEPARATOR);
      //      * createShowInExplorerMenuItem(popupMenu, viewer);
      //      * createHistoryMenuItem(popupMenu, viewer);
      //      * new MenuItem(popupMenu, SWT.SEPARATOR);
      //      *createExportMenuItem(popupMenu, viewer);
      //      *new MenuItem(popupMenu, SWT.SEPARATOR);
      //      * ArtifactPreviewMenu.createPreviewMenuItem(popupMenu, viewer);
      //
      //      // previewMenuItem = MenuItems.createMenuItem(popupMenu, SWT.PUSH, new
      //      // PreviewArtifactsAction(viewer));
      //      *createEditMenuItem(popupMenu, viewer);
      //      *createOpenInArtifactEditor(popupMenu, viewer);
      //      *createOpenInMassArtifactEditor(popupMenu, viewer);
      //      *createOpenInSkywalkerArtifactEditor(popupMenu, viewer);
      //      *createPopulateAtsWorld(popupMenu, viewer);
      //      createSetAllPartitions(popupMenu, viewer);
      //      new MenuItem(popupMenu, SWT.SEPARATOR);
      //      GlobalMenu menu = new GlobalMenu(popupMenu, globalMenuHelper);
      //      menu.addGlobalMenuListener(globalMenuListener);
      //      new MenuItem(popupMenu, SWT.SEPARATOR);
      //      createAddTemplateMenuItem(popupMenu, viewer);
      //
      //      menuOnwer.setMenu(popupMenu);

      MenuManager menuManager = new MenuManager();
      menuManager.add(new Separator());
      viewer.getTable().setMenu(menuManager.createContextMenu(viewer.getTable()));
      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.branch.ArtifactSearchViewPage.main",
            menuManager, viewer);

      createViewTableMenuItem(menuManager, viewer);
      menuManager.add(new Separator());
      createShowInExplorerMenuItem(menuManager, viewer);
      menuManager.add(new Separator());
      createExportMenuItem(menuManager, viewer);
      menuManager.add(new Separator());

      // The additions group is a standard group
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
   }

   /**
    * 
    */
   public void updateBranch(Branch branch) {
      branchStatusItem.setText(branch.getDisplayName());
      branchStatusItem.setImage(BranchLabelProvider.getBranchImage(branch));
   }

   private void createReportMenuItem(Menu parentMenu, final TableViewer viewer) {
      MenuItem runReportsItem = new MenuItem(parentMenu, SWT.CASCADE);
      runReportsItem.setText("Run Reports");

      Menu reportsMenu = new Menu(parentMenu);
      runReportsItem.setMenu(reportsMenu);
      SelectionListener listener = new ReportSelectionListener(viewer);

      createRelationMatrixReportMenuItem(reportsMenu, listener);
      createDynamicReportMenuItems(reportsMenu, listener);
   }

   private void createViewTableMenuItem(MenuManager menuManager, final TableViewer viewer) {
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

   private void createDynamicReportMenuItems(Menu parentMenu, SelectionListener listener) {
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
                     createReportJobMenuItem(parentMenu, reportJob, listener);
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

   private void createReportJobMenuItem(Menu parentMenu, ReportJob reportJob, SelectionListener listener) {
      MenuItem item = new MenuItem(parentMenu, SWT.PUSH);
      item.setText(reportJob.getName());
      item.setData(reportJob);
      item.addSelectionListener(listener);
   }

   private void createRelationMatrixReportMenuItem(Menu parentMenu, SelectionListener listener) {
      MenuItem matrixItem = new MenuItem(parentMenu, SWT.CASCADE);
      matrixItem.setText("Relation Matrix Reports");

      Menu matrixMenu = new Menu(parentMenu);

      RelationPersistenceManager relationManager = RelationPersistenceManager.getInstance();
      for (IRelationLinkDescriptor descriptor : relationManager.getIRelationLinkDescriptors(branchManager.getDefaultBranch())) {
         ReportJob reportJob = new RelationMatrixExportJob(descriptor);
         createReportJobMenuItem(matrixMenu, reportJob, listener);
      }
      matrixItem.setMenu(matrixMenu);
   }

   //   private void createExportMenuItem(Menu parentMenu, final TableViewer viewer) {
   //      exportMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
   //      exportMenuItem.setText("Export Artifact(s)");
   //      exportMenuItem.addSelectionListener(new SelectionAdapter() {
   //         public void widgetSelected(SelectionEvent ev) {
   //            try {
   //               Jobs.startJob(new ArtifactExportJob(viewer));
   //            }
   //            catch (Exception ex) {
   //               logger.log(Level.SEVERE, ex.getMessage(), ex);
   //            }
   //         }
   //      });
   //   }

   private void createExportMenuItem(MenuManager menuManager, final TableViewer viewer) {
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

   //   private void createPreviewMenuItem(MenuManager menuManager, final TableViewer viewer) {
   //      MenuManager previewSubMenu = new MenuManager("Preview");
   //
   //      CommandContributionItem previewArtifactCommand = Commands.getLocalCommandContribution(ArtifactSearchViewPage.VIEW_ID, getSite(), "org.eclipse.osee.framework.ui.skynet.previewArtifact.command",
   //            "Preview Artifact", null, null, null, null, null, null);
   //      CommandContributionItem previewArtifactRecurseCommand = Commands.getLocalCommandContribution(ArtifactSearchViewPage.VIEW_ID, getSite(), "org.eclipse.osee.framework.ui.skynet.previewArtifactRecurse.command",
   //            "Preview with child recursion", null, null,null, null, null, null);
   //      previewSubMenu.add(previewArtifactCommand);
   //      previewSubMenu.add(previewArtifactRecurseCommand);
   //
   //      menuManager.add(previewSubMenu);
   //
   //      handlerService.activateHandler(previewArtifactCommand.getId(),
   //
   //      new AbstractSelectionEnabledHandler(menuManager) {
   //         @Override
   //         public Object execute(ExecutionEvent event) throws ExecutionException {
   //            return null;
   //         }
   //
   //         @Override
   //         public boolean isEnabled() {
   //            return true;
   //         }
   //      });
   //
   //      handlerService.activateHandler(previewArtifactRecurseCommand.getId(),
   //
   //      new AbstractSelectionEnabledHandler(menuManager) {
   //         @Override
   //         public Object execute(ExecutionEvent event) throws ExecutionException {
   //            return null;
   //         }
   //
   //         @Override
   //         public boolean isEnabled() {
   //            return true;
   //         }
   //      });
   //   }

   private void createHistoryMenuItem(Menu parentMenu, final TableViewer viewer) {
      revisionMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      revisionMenuItem.setText("&Show Resource History ");
      revisionMenuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
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
         }
      });
   }

   private void createShowInExplorerMenuItem(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openArtifactsCommand =
            Commands.getLocalCommandContribution(ArtifactSearchViewPage.VIEW_ID, getSite(),
                  "showInArtifactExplorerCommand", "Show in Artifact Explorer", null, null, null, "V", null, null);
      menuManager.add(openArtifactsCommand);

      handlerService.activateHandler(openArtifactsCommand.getId(),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            ArtifactExplorer.revealArtifact(getSelectedArtifact(viewer));
            return null;
         }

         @Override
         public boolean isEnabled() {
            return true;
         }
      });
   }

   private void createOpenInArtifactEditor(Menu parentMenu, final TableViewer viewer) {
      openMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      openMenuItem.setText("Open in Artifact Editor");
      openMenuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {

            ArtifactEditor.editArtifacts(getSelectedArtifacts(viewer));
         }
      });
   }

   private void createOpenInMassArtifactEditor(Menu parentMenu, final TableViewer viewer) {
      openInMassEditorMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      openInMassEditorMenuItem.setText("Open in Mass Artifact Editor");
      openInMassEditorMenuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {

            MassArtifactEditor.editArtifacts("", getSelectedArtifacts(viewer));
         }
      });
   }

   private void createOpenInSkywalkerArtifactEditor(Menu parentMenu, final TableViewer viewer) {
      skywalkerEditorMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      skywalkerEditorMenuItem.setText("Sky Walker");
      skywalkerEditorMenuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            SkyWalkerView.exploreArtifact(getSelectedArtifacts(viewer).iterator().next());
         }
      });
   }

   private void createPopulateAtsWorld(Menu parentMenu, final TableViewer viewer) {
      openInAtsWorldMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      openInAtsWorldMenuItem.setText("Open in ATS World");
      openInAtsWorldMenuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            try {
               if (OseeAts.getAtsLib() != null) OseeAts.getAtsLib().openInAtsWorld("", getSelectedArtifacts(viewer));
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });
   }

   private void createSetAllPartitions(Menu parentMenu, final TableViewer viewer) {
      setAllPartitionsMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      setAllPartitionsMenuItem.setText("Set All Partitions");
      setAllPartitionsMenuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
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

                        art.persist();
                     }
                  }
               };
               try {
                  partitionsTx.execute();
               } catch (Exception ex) {
                  OSEELog.logException(getClass(), ex, false);
               }
            }
         }
      });
   }

   //   private void createEditMenuItem(Menu parentMenu, final TableViewer viewer) {
   //      editItem = new MenuItem(parentMenu, SWT.CASCADE);
   //      // All items should be on same branch, so take branch of first item.
   //      Artifact artifact = getSelectedArtifact(viewer);
   //      String branchString = artifact == null ? "" : " (" + artifact.getBranch() + ")";
   //      editItem.setText("&Edit" + branchString);
   //
   //      editItem.addSelectionListener(new SelectionAdapter() {
   //         public void widgetSelected(SelectionEvent e) {
   //            RendererManager.getInstance().editInJob(getSelectedArtifacts(viewer));
   //         }
   //      });
   //   }

   private void registerForEvents() {
      eventManager.unRegisterAll(this);

      eventManager.register(CacheArtifactModifiedEvent.class, this);
      eventManager.register(CacheRelationModifiedEvent.class, this);
      eventManager.register(TransactionRelationModifiedEvent.class, this);
      eventManager.register(TransactionArtifactModifiedEvent.class, this);
   }

   private void createAddTemplateMenuItem(Menu parentMenu, final TableViewer viewer) {
      final Shell shell = parentMenu.getShell();
      addTemplateItem = new MenuItem(parentMenu, SWT.CASCADE);
      addTemplateItem.setText("Add Template Attribute");

      addTemplateItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Artifact template = getSelectedArtifact(viewer);

            FileDialog dialog = new FileDialog(shell);
            String path = dialog.open();

            if (path != null && !path.equals("")) {
               try {
                  template.setSoleAttributeValue(WordRenderer.TEMPLATE_ATTRIBUTE, new String(
                        Streams.getByteArray(new FileInputStream(path)), "UTF-8"));
                  template.persist();
               } catch (IllegalStateException ex) {
                  logger.log(Level.SEVERE, ex.toString(), ex);
               } catch (FileNotFoundException ex) {
                  logger.log(Level.SEVERE, ex.toString(), ex);
               } catch (UnsupportedEncodingException ex) {
                  logger.log(Level.SEVERE, ex.toString(), ex);
               } catch (SQLException ex) {
                  logger.log(Level.SEVERE, ex.toString(), ex);
               }
            }
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

   public class ArtifactMenuListener implements MenuListener {

      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         // Use this menu listener until all menu items can be moved to GlobaMenu
         GlobalMenuPermissions permiss = new GlobalMenuPermissions(globalMenuHelper);

         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
         addTemplateItem.setEnabled(selection.size() == 1 && ((Match) selection.getFirstElement()).getElement() instanceof WordRenderer);

         openMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isWritePermission());
         openInAtsWorldMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isWritePermission());
         openInMassEditorMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isWritePermission());
         skywalkerEditorMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isReadPermission());
         // previewMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isReadPermission());
         editItem.setEnabled(permiss.isHasArtifacts() && permiss.isWritePermission());
         revisionMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isReadPermission());
         showInExplorerMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isReadPermission());
         exportMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isReadPermission());
         setAllPartitionsMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isWritePermission());
      }
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
