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

package org.eclipse.osee.ats.world;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.ActionDebug;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.BulkLoadAtsCache;
import org.eclipse.osee.ats.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.info.DbDetailData.ConfigField;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * ATS World View provides a tree-table view of the ATS object results loaded from searches
 * 
 * @see ViewPart
 * @author Donald G. Dunne
 */
public class WorldView extends ViewPart implements IFrameworkTransactionEventListener, IPartListener, IActionable {
   protected Browser browser;
   public static final String VIEW_ID = "org.eclipse.osee.ats.world.WorldView";
   public static final String HELP_CONTEXT_ID = "atsWorldView";
   private final ActionDebug debug = new ActionDebug(false, "WorldView");
   private Action filterCompletedAction, releaseMetricsAction, selectionMetricsAction;
   private Label warningLabel, searchNameLabel, extraInfoLabel;
   private WorldSearchItem lastSearchItem;
   private WorldXViewer xViewer;
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(WorldView.class);
   private final WorldCompletedFilter worldCompletedFilter = new WorldCompletedFilter();
   private final Set<Artifact> worldArts = new HashSet<Artifact>(200);
   private final Set<Artifact> otherArts = new HashSet<Artifact>(200);

   /**
    * The constructor.
    */
   public WorldView() {
   }

   public static WorldView getWorldView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         return (WorldView) page.showView(WorldView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
               "Couldn't Launch OSEE World View " + e1.getMessage());
      }
      return null;
   }

   public void atsArtifactSaved(Artifact artifact) {
      if (artifact instanceof IWorldViewArtifact) xViewer.refresh(artifact);
   }

   public static void loadIt(final String name, final Collection<? extends Artifact> arts, final TableLoadOption... tableLoadOption) {
      final Set<TableLoadOption> options = new HashSet<TableLoadOption>();
      options.addAll(Arrays.asList(tableLoadOption));
      options.add(TableLoadOption.ClearLastSearchItem);
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            try {
               WorldView worldView = (WorldView) page.showView(WorldView.VIEW_ID);
               worldView.load(name, arts, options.toArray(new TableLoadOption[options.size()]));
            } catch (PartInitException e1) {
               OSEELog.logSevere(AtsPlugin.class, "Couldn't Launch XViewer Dev View ", true);
            }
         }
      }, options.contains(TableLoadOption.ForcePend));
   }

   public void load(final String name, final Collection<? extends Artifact> arts, TableLoadOption... tableLoadOption) {
      Set<TableLoadOption> options = new HashSet<TableLoadOption>();
      options.addAll(Arrays.asList(tableLoadOption));
      options.add(TableLoadOption.ClearLastSearchItem);
      load(null, name, arts, tableLoadOption);
   }

   public void load(final WorldSearchItem searchItem, final String name, final Collection<? extends Artifact> arts, TableLoadOption... tableLoadOption) {
      List<TableLoadOption> options = Collections.getAggregate(tableLoadOption);
      if (options.contains(TableLoadOption.ClearLastSearchItem)) lastSearchItem = null;
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
            worldArts.clear();
            otherArts.clear();
            for (Artifact art : arts) {
               if (art instanceof IWorldViewArtifact)
                  worldArts.add(art);
               else
                  otherArts.add(art);
            }
            xViewer.set(worldArts);
            if (arts.size() == 0)
               setTableTitle("No Results Found - " + name, true);
            else
               setTableTitle(name, false);
            if (otherArts.size() > 0) {
               if (MessageDialog.openConfirm(
                     Display.getCurrent().getActiveShell(),
                     "Open in Artifact Editor?",
                     otherArts.size() + " Non-WorldView Artifacts were returned from request.\n\nOpen in Artifact Editor?")) {
                  ArtifactEditor.editArtifacts(otherArts);
               }
            }
         }
      }, true);
   }

   @Override
   public void setFocus() {
   }

   @Override
   public void createPartControl(Composite parent) {
      debug.report("createPartControl");

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;
      BulkLoadAtsCache.run(false);

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));
      // parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(new GridLayout(1, false));
      GridData gd = new GridData(GridData.FILL_BOTH);
      comp.setLayoutData(gd);

      // Heaader Composite
      Composite headerComp = new Composite(comp, SWT.NONE);
      headerComp.setLayout(ALayout.getZeroMarginLayout(3, false));
      gd = new GridData(GridData.FILL_HORIZONTAL);
      headerComp.setLayoutData(gd);

      warningLabel = new Label(headerComp, SWT.NONE);
      searchNameLabel = new Label(headerComp, SWT.NONE);

      String adminStr = "";
      if (AtsPlugin.isAtsAdmin()) adminStr = "Admin - " + getWhoAmI();
      if (AtsPlugin.isAtsDisableEmail()) adminStr += " - Email Disabled";
      if (AtsPlugin.isAtsAlwaysEmailMe()) adminStr += " - AtsAlwaysEmailMe";
      if (!adminStr.equals("")) {
         Label label = new Label(headerComp, SWT.NONE);
         label.setText(adminStr);
         label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         gd = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
         label.setLayoutData(gd);
      }

      extraInfoLabel = new Label(headerComp, SWT.NONE);
      gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = 3;
      extraInfoLabel.setLayoutData(gd);

      xViewer = new WorldXViewer(comp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
      xViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      xViewer.setContentProvider(new WorldContentProvider(xViewer));
      xViewer.setLabelProvider(new WorldLabelProvider(xViewer));
      AtsPlugin.getInstance().setHelp(xViewer.getControl(), HELP_CONTEXT_ID);

      Tree tree = xViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      xViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            updateExtraInfoLine();
         }
      });
      xViewer.getTree().addKeyListener(new KeyListener() {
         public void keyPressed(KeyEvent event) {
         }

         public void keyReleased(KeyEvent event) {
            // if CTRL key is already pressed
            if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
               if (event.keyCode == 'a') {
                  xViewer.getTree().setSelection(xViewer.getTree().getItems());
                  updateExtraInfoLine();
               } else if (event.keyCode == 'z') {
                  releaseMetricsAction.setChecked(!releaseMetricsAction.isChecked());
                  releaseMetricsAction.run();
               } else if (event.keyCode == 'x') {
                  selectionMetricsAction.setChecked(!selectionMetricsAction.isChecked());
                  selectionMetricsAction.run();
               } else if (event.keyCode == 'f') {
                  filterCompletedAction.setChecked(!filterCompletedAction.isChecked());
                  filterCompletedAction.run();
               }
            }
         }
      });

      parent.layout();
      createActions();
      SkynetContributionItem.addTo(this, false);
      setupDragAndDropSupport();

      OseeEventManager.addListener(this);

   }

   private void setupDragAndDropSupport() {
      DragSource source = new DragSource(xViewer.getTree(), DND.DROP_COPY);
      source.setTransfer(new Transfer[] {ArtifactTransfer.getInstance()});
      source.addDragListener(new DragSourceListener() {

         public void dragFinished(DragSourceEvent event) {
         }

         public void dragSetData(DragSourceEvent event) {
            String item = "work";
            event.data =
                  new ArtifactData(xViewer.getSelectedArtifacts().toArray(
                        new Artifact[xViewer.getSelectedArtifacts().size()]), item, WorldView.VIEW_ID);
         }

         public void dragStart(DragSourceEvent event) {
         }
      });
   }

   private String getWhoAmI() {
      return OseeDb.getDefaultDatabaseService().getDatabaseDetails().getFieldValue(ConfigField.DatabaseName) + ", " + OseeDb.getDefaultDatabaseService().getDatabaseDetails().getFieldValue(
            ConfigField.UserName) + " - " + SkynetAuthentication.getUser().getName();
   }

   public class FilterLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         return ((WorldSearchItem) arg0).getSelectedName(SearchType.Search);
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }
   }

   public class FilterContentProvider implements IStructuredContentProvider {
      public Object[] getElements(Object arg0) {
         return ((ArrayList<?>) arg0).toArray();
      }

      public void dispose() {
      }

      public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
      }
   }

   public void loadTable(WorldSearchItem searchItem, TableLoadOption... tableLoadOptions) throws InterruptedException, OseeCoreException, SQLException {
      searchItem.setCancelled(false);
      loadTable(searchItem, SearchType.Search, tableLoadOptions);
   }

   public void loadTable(WorldSearchItem searchItem, SearchType searchType, TableLoadOption... tableLoadOptions) throws InterruptedException, OseeCoreException, SQLException {
      Set<TableLoadOption> options = new HashSet<TableLoadOption>();
      options.addAll(Arrays.asList(tableLoadOptions));
      searchItem.setCancelled(false);
      this.lastSearchItem = searchItem;
      debug.report("loadTable", true);
      Result result = AtsPlugin.areOSEEServicesAvailable();
      if (result.isFalse()) {
         AWorkbench.popup("ERROR", "DB Connection Unavailable");
         return;
      }

      if (searchItem == null) return;

      if (!options.contains(TableLoadOption.NoUI)) {
         searchItem.performUI(searchType);
      }
      if (searchItem.isCancelled()) return;

      LoadTableJob job = null;
      job = new LoadTableJob(searchItem, SearchType.Search, this);
      job.setUser(false);
      job.setPriority(Job.LONG);
      job.schedule();
      if (options.contains(TableLoadOption.ForcePend)) job.join();
   }

   private class LoadTableJob extends Job {

      private final WorldSearchItem searchItem;
      private boolean cancel = false;
      private final SearchType searchType;

      public LoadTableJob(WorldSearchItem searchItem, SearchType searchType, WorldView worldView) {
         super("Loading \"" + searchItem.getSelectedName(searchType) + "\"...");
         this.searchItem = searchItem;
         this.searchType = searchType;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {

         setTableTitle(
               "Loading \"" + (searchItem.getSelectedName(searchType) != null ? searchItem.getSelectedName(searchType) : "") + "\"...",
               false);
         cancel = false;
         searchItem.setCancelled(cancel);
         debug.report("Querying DB", true);
         final Collection<Artifact> artifacts;
         xViewer.clear();
         try {
            artifacts = searchItem.performSearchGetResults(false, searchType);
            if (artifacts.size() == 0) {
               if (searchItem.isCancelled()) {
                  monitor.done();
                  setTableTitle("CANCELLED - " + searchItem.getSelectedName(searchType), false);
                  return Status.CANCEL_STATUS;
               } else {
                  monitor.done();
                  setTableTitle("No Results Found - " + searchItem.getSelectedName(searchType), true);
                  return Status.OK_STATUS;
               }
            }
            load(searchItem,
                  (searchItem.getSelectedName(searchType) != null ? searchItem.getSelectedName(searchType) : ""),
                  artifacts);
         } catch (final Exception ex) {
            String str = "Exception occurred. Network may be down.";
            if (ex.getLocalizedMessage() != null && !ex.getLocalizedMessage().equals("")) str +=
                  " => " + ex.getLocalizedMessage();
            setTableTitle("Searching Error - " + searchItem.getSelectedName(searchType), false);
            logger.log(Level.SEVERE, "Searching Error - " + ex.getLocalizedMessage(), ex);
            monitor.done();
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, str, null);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   public void setTableTitle(final String title, final boolean warning) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (warning)
               warningLabel.setImage(AtsPlugin.getInstance().getImage("warn.gif"));
            else
               warningLabel.setImage(null);
            searchNameLabel.setText(title);
            searchNameLabel.getParent().layout();
            xViewer.setReportingTitle(title + " - " + XDate.getDateNow());
            updateExtraInfoLine();
         };
      });
   }

   public void updateExtraInfoLine() {
      if (extraInfoLabel == null || extraInfoLabel.isDisposed()) return;
      if (releaseMetricsAction.isChecked()) {
         extraInfoLabel.setText(SMAMetrics.getReleaseEstRemainMetrics(getxViewer().getSelectedSMAArtifacts()));
      } else if (selectionMetricsAction.isChecked()) {
         extraInfoLabel.setText(SMAMetrics.getEstRemainMetrics(getxViewer().getSelectedSMAArtifacts()));
      } else
         extraInfoLabel.setText("");
      extraInfoLabel.getParent().layout();
   }

   protected void createActions() {
      debug.report("createActions");

      Action myWorldAction = new Action("My World") {

         @Override
         public void run() {
            try {
               loadTable(AtsNavigateViewItems.getInstance().getMyWorldSearchItem());
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      };
      myWorldAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("MyWorld.gif"));
      myWorldAction.setToolTipText("My World");

      Action expandAllAction = new Action("Expand All") {

         @Override
         public void run() {
            xViewer.expandAll();
         }
      };
      expandAllAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("expandAll.gif"));
      expandAllAction.setToolTipText("Expand All");

      filterCompletedAction = new Action("Filter Out Completed/Cancelled - Ctrl-F", Action.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (filterCompletedAction.isChecked()) {
               xViewer.addFilter(worldCompletedFilter);
            } else {
               xViewer.removeFilter(worldCompletedFilter);
            }
            updateExtendedStatusString();
            xViewer.refresh();
         }
      };
      filterCompletedAction.setToolTipText("Filter Out Completed/Cancelled - Ctrl-F");

      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            try {
               if (lastSearchItem != null) loadTable(lastSearchItem, SearchType.ReSearch);
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      };
      refreshAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("refresh.gif"));
      refreshAction.setToolTipText("Refresh");

      Action whoAmIAction = new Action("Who Am I") {

         @Override
         public void run() {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Who Am I", getWhoAmI());
         }
      };
      whoAmIAction.setToolTipText("Who Am I");

      releaseMetricsAction = new Action("Show Release Metrics by Release Version - Ctrl-Z", Action.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (releaseMetricsAction.isChecked()) selectionMetricsAction.setChecked(false);
            updateExtraInfoLine();
         }
      };
      releaseMetricsAction.setToolTipText("Show Release Metrics by Release Version - Ctrl-Z");

      selectionMetricsAction = new Action("Show Release Metrics by Selection - Ctrl-X", Action.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (selectionMetricsAction.isChecked()) releaseMetricsAction.setChecked(false);
            updateExtraInfoLine();
         }
      };
      selectionMetricsAction.setToolTipText("Show Release Metrics by Selection - Ctrl-X");

      Action toAction = new Action("Re-display WorkFlows as Actions", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsAction();
         }
      };
      toAction.setToolTipText("Re-display WorkFlows as Actions");

      Action toWorkFlow = new Action("Re-display Actions as WorkFlows", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsWorkFlow();
         }
      };
      toWorkFlow.setToolTipText("Re-display Actions as WorkFlows");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(myWorldAction);
      toolbarManager.add(new NewAction());
      toolbarManager.add(expandAllAction);
      toolbarManager.add(refreshAction);

      IMenuManager manager = getViewSite().getActionBars().getMenuManager();
      manager.add(filterCompletedAction);
      manager.add(new Separator());
      manager.add(whoAmIAction);
      manager.add(releaseMetricsAction);
      manager.add(selectionMetricsAction);
      manager.add(new Separator());
      manager.add(toAction);
      manager.add(toWorkFlow);
      if (AtsPlugin.isAtsAdmin()) {
         manager.add(new Separator());
      }
      xViewer.addCustomizeToViewToolbar(this);
      OseeAts.addBugToViewToolbar(this, this, AtsPlugin.getInstance(), VIEW_ID, "ATS World");
   }

   public static ArrayList<Artifact> getLoadedArtifacts() {
      return WorldView.getWorldView().getxViewer().getLoadedArtifacts();
   }

   public void updateExtendedStatusString() {
      String str = "";
      if (filterCompletedAction.isChecked()) {
         str += " Complete/Cancel FILTERED - ";
      }
      xViewer.setExtendedStatusString(str);
   }

   public void redisplayAsAction() {
      try {
         TreeItem treeItem[] = xViewer.getTree().getItems();
         Set<Artifact> arts = new HashSet<Artifact>();
         for (TreeItem item : treeItem) {
            Object obj = item.getData();
            if (obj instanceof Artifact) {
               Artifact art = (Artifact) obj;
               if (art instanceof TeamWorkFlowArtifact) {
                  arts.add(((TeamWorkFlowArtifact) art).getParentActionArtifact());
               } else
                  arts.add(art);
            }
         }
         load(searchNameLabel.getText(), arts);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public void redisplayAsWorkFlow() {
      try {
         TreeItem treeItem[] = xViewer.getTree().getItems();
         Set<Artifact> arts = new HashSet<Artifact>();
         for (TreeItem item : treeItem) {
            if (item.getData() instanceof Artifact) {
               Artifact art = (Artifact) item.getData();
               if (art instanceof ActionArtifact) {
                  arts.addAll(((ActionArtifact) art).getTeamWorkFlowArtifacts());
               } else
                  arts.add(art);
            }
         }
         load(searchNameLabel.getText(), arts);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public String getActionDescription() {
      if (lastSearchItem != null) return String.format("Search Item: %s",
            lastSearchItem.getSelectedName(SearchType.Search));
      return "";
   }

   public void partActivated(IWorkbenchPart part) {
   }

   public void partBroughtToTop(IWorkbenchPart part) {
   }

   public void partClosed(IWorkbenchPart part) {
      if (part.equals(this)) {
         xViewer.dispose();
      }
   }

   public void partDeactivated(IWorkbenchPart part) {
   }

   public void partOpened(IWorkbenchPart part) {
   }

   public WorldXViewer getxViewer() {
      return xViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#dispose()
    */
   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
      if (xViewer != null) xViewer.dispose();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) {
      if (transData.branchId == AtsPlugin.getAtsBranch().getBranchId()) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               updateExtraInfoLine();
            }
         });
      }
   }
}