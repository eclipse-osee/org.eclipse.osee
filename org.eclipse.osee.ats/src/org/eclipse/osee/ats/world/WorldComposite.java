/*
 * Created on Nov 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserCache;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class WorldComposite extends Composite implements IFrameworkTransactionEventListener {

   private Action filterCompletedAction, releaseMetricsAction, selectionMetricsAction, toAction, toWorkFlow;
   private final Label warningLabel, searchNameLabel, extraInfoLabel;
   private WorldSearchItem lastSearchItem;
   private final WorldXViewer worldXViewer;
   private final WorldCompletedFilter worldCompletedFilter = new WorldCompletedFilter();
   private final Set<Artifact> worldArts = new HashSet<Artifact>(200);
   private final Set<Artifact> otherArts = new HashSet<Artifact>(200);
   private final IViewSite viewSite;
   private WorldSearchItem searchItem;
   private TableLoadOption[] tableLoadOptions;
   private Collection<? extends Artifact> arts;
   private String loadName;
   private final Composite toolBarComposite;

   /**
    * @param parent
    * @param style
    */
   public WorldComposite(String viewEditorId, IViewSite viewSite, Composite parent, int style) {
      super(parent, style);
      this.viewSite = viewSite;

      setLayout(new GridLayout(1, false));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      // Header Composite
      Composite headerComp = new Composite(this, SWT.NONE);
      headerComp.setLayout(ALayout.getZeroMarginLayout(3, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      headerComp.setLayoutData(gd);

      warningLabel = new Label(headerComp, SWT.NONE);
      searchNameLabel = new Label(headerComp, SWT.NONE);

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(this)) {
         toolBarComposite = null;
         extraInfoLabel = null;
         worldXViewer = null;
         return;
      }

      if (viewSite == null) {
         toolBarComposite = new Composite(headerComp, SWT.NONE);
         toolBarComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false, 1, 1));
         toolBarComposite.setLayout(ALayout.getZeroMarginLayout(1, false));
      } else {
         toolBarComposite = null;
         String nameStr = getWhoAmI();
         if (AtsPlugin.isAtsAdmin()) nameStr += " - Admin";
         if (AtsPlugin.isAtsDisableEmail()) nameStr += " - Email Disabled";
         if (AtsPlugin.isAtsAlwaysEmailMe()) nameStr += " - AtsAlwaysEmailMe";
         if (!nameStr.equals("")) {
            Label label = new Label(headerComp, SWT.NONE);
            label.setText(nameStr);
            if (AtsPlugin.isAtsAdmin()) {
               label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            } else {
               label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
            }
            gd = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
            label.setLayoutData(gd);
         }

      }

      extraInfoLabel = new Label(headerComp, SWT.NONE);
      gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = 3;
      extraInfoLabel.setLayoutData(gd);

      worldXViewer = new WorldXViewer(this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
      worldXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      worldXViewer.setContentProvider(new WorldContentProvider(worldXViewer));
      worldXViewer.setLabelProvider(new WorldLabelProvider(worldXViewer));

      Tree tree = worldXViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      worldXViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            try {
               updateExtraInfoLine();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });
      worldXViewer.getTree().addKeyListener(new KeyListener() {
         public void keyPressed(KeyEvent event) {
         }

         public void keyReleased(KeyEvent event) {
            // if CTRL key is already pressed
            if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
               if (event.keyCode == 'a') {
                  worldXViewer.getTree().setSelection(worldXViewer.getTree().getItems());
                  try {
                     updateExtraInfoLine();
                  } catch (OseeCoreException ex) {
                     OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                  }
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

      new WorldViewDragAndDrop(worldXViewer, viewEditorId);
      parent.layout();
      createActions();

      OseeEventManager.addListener(this);
   }

   public void setCustomizeData(CustomizeData customizeData) {
      worldXViewer.getCustomizeMgr().loadCustomization(customizeData);
   }

   public Control getControl() {
      return worldXViewer.getControl();
   }

   public void loadIt(final String name, final Collection<? extends Artifact> arts, final TableLoadOption... tableLoadOption) {
      final Set<TableLoadOption> options = new HashSet<TableLoadOption>();
      options.addAll(Arrays.asList(tableLoadOption));
      options.add(TableLoadOption.ClearLastSearchItem);
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
            load(name, arts, options.toArray(new TableLoadOption[options.size()]));
         }
      }, options.contains(TableLoadOption.ForcePend));
   }

   public void load(final String name, final Collection<? extends Artifact> arts, TableLoadOption... tableLoadOption) {
      this.loadName = name;
      this.arts = arts;
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
            worldXViewer.set(worldArts);
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

   public void loadTable(WorldSearchItem searchItem, TableLoadOption... tableLoadOptions) throws InterruptedException, OseeCoreException {
      searchItem.setCancelled(false);
      loadTable(searchItem, SearchType.Search, tableLoadOptions);
   }

   public void loadTable(WorldSearchItem searchItem, SearchType searchType, TableLoadOption... tableLoadOptions) throws InterruptedException, OseeCoreException {
      this.searchItem = searchItem;
      this.tableLoadOptions = tableLoadOptions;
      Set<TableLoadOption> options = new HashSet<TableLoadOption>();
      options.addAll(Arrays.asList(tableLoadOptions));
      searchItem.setCancelled(false);
      this.lastSearchItem = searchItem;
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
      job = new LoadTableJob(searchItem, SearchType.Search);
      job.setUser(false);
      job.setPriority(Job.LONG);
      job.schedule();
      if (options.contains(TableLoadOption.ForcePend)) job.join();
   }

   private class LoadTableJob extends Job {

      private final WorldSearchItem searchItem;
      private boolean cancel = false;
      private final SearchType searchType;

      public LoadTableJob(WorldSearchItem searchItem, SearchType searchType) {
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
         final Collection<Artifact> artifacts;
         worldXViewer.clear();
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
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
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
            try {
               if (warning)
                  warningLabel.setImage(AtsPlugin.getInstance().getImage("warn.gif"));
               else
                  warningLabel.setImage(null);
               searchNameLabel.setText(title);
               searchNameLabel.getParent().layout();
               worldXViewer.setReportingTitle(title + " - " + XDate.getDateNow());
               updateExtraInfoLine();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         };
      });
   }

   public void updateExtraInfoLine() throws OseeCoreException {
      if (extraInfoLabel == null || extraInfoLabel.isDisposed()) return;
      String str = "";
      if (releaseMetricsAction.isChecked()) {
         VersionArtifact verArt = null;
         Set<StateMachineArtifact> smaArts = getXViewer().getSelectedSMAArtifacts();
         if (smaArts.size() != 0) {
            verArt = smaArts.iterator().next().getTargetedForVersion();
            SMAMetrics sMet = new SMAMetrics(smaArts, verArt);
            str = sMet.toString();
         }
      } else if (selectionMetricsAction.isChecked()) {
         SMAMetrics sMet = new SMAMetrics(getXViewer().getSelectedSMAArtifacts(), null);
         str = sMet.toString();
      } else
         extraInfoLabel.setText(str);
      extraInfoLabel.getParent().layout();
   }

   private String getWhoAmI() {
      try {
         String userName = UserCache.getUser().getName();
         return String.format("%s - %s:%s", userName, ClientSessionManager.getDataStoreName(),
               ClientSessionManager.getDataStoreLoginName());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   protected void createActions() {
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

      Action newWorldEditor = new Action("Open in New Editor") {

         @Override
         public void run() {
            try {
               if (arts != null) {
                  WorldEditor.open(loadName, arts, worldXViewer.getCustomizeMgr().generateCustDataFromTable(),
                        tableLoadOptions);
               } else if (searchItem != null) {
                  WorldEditor.open(searchItem, worldXViewer.getCustomizeMgr().generateCustDataFromTable(),
                        tableLoadOptions);
               } else {
                  AWorkbench.popup("ERROR", "Nothing loaded");
               }
            } catch (OseeCoreException ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      };
      newWorldEditor.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("newWorld.gif"));
      newWorldEditor.setToolTipText("Open in New Editor");

      Action newWorldEditorSelected = new Action("Open Selected in New Editor") {

         @Override
         public void run() {

            if (worldXViewer.getSelectedArtifacts().size() == 0) {
               AWorkbench.popup("ERROR", "Select items to open");
               return;
            }
            WorldEditorInput worldEditorInput =
                  new WorldEditorInput("ATS - " + worldXViewer.getSelectedArtifacts().size() + " Selected",
                        worldXViewer.getSelectedArtifacts(),
                        worldXViewer.getCustomizeMgr().generateCustDataFromTable(), tableLoadOptions);
            if (worldEditorInput != null) {
               IWorkbenchPage page = AWorkbench.getActivePage();
               try {
                  page.openEditor(worldEditorInput, WorldEditor.EDITOR_ID);
               } catch (PartInitException ex) {
                  OSEELog.logException(AtsPlugin.class, ex, true);
               }
            }
         }
      };
      newWorldEditorSelected.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("newWorldSelected.gif"));
      newWorldEditorSelected.setToolTipText("Open Selected in New Editor");

      Action expandAllAction = new Action("Expand All") {

         @Override
         public void run() {
            worldXViewer.expandAll();
         }
      };
      expandAllAction.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("expandAll.gif"));
      expandAllAction.setToolTipText("Expand All");

      filterCompletedAction = new Action("Filter Out Completed/Cancelled - Ctrl-F", Action.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (filterCompletedAction.isChecked()) {
               worldXViewer.addFilter(worldCompletedFilter);
            } else {
               worldXViewer.removeFilter(worldCompletedFilter);
            }
            updateExtendedStatusString();
            worldXViewer.refresh();
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

      releaseMetricsAction = new Action("Show Release Metrics by Release Version - Ctrl-Z", Action.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (releaseMetricsAction.isChecked()) selectionMetricsAction.setChecked(false);
            try {
               updateExtraInfoLine();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      };
      releaseMetricsAction.setToolTipText("Show Release Metrics by Release Version - Ctrl-Z");

      selectionMetricsAction = new Action("Show Release Metrics by Selection - Ctrl-X", Action.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (selectionMetricsAction.isChecked()) releaseMetricsAction.setChecked(false);
            try {
               updateExtraInfoLine();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      };
      selectionMetricsAction.setToolTipText("Show Release Metrics by Selection - Ctrl-X");

      toAction = new Action("Re-display WorkFlows as Actions", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsAction();
         }
      };
      toAction.setToolTipText("Re-display WorkFlows as Actions");

      toWorkFlow = new Action("Re-display Actions as WorkFlows", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsWorkFlow();
         }
      };
      toWorkFlow.setToolTipText("Re-display Actions as WorkFlows");

      if (viewSite != null) {
         IToolBarManager toolbarManager = viewSite.getActionBars().getToolBarManager();
         toolbarManager.add(myWorldAction);
         toolbarManager.add(new NewAction());
         toolbarManager.add(expandAllAction);
         toolbarManager.add(newWorldEditor);
         toolbarManager.add(newWorldEditorSelected);
         toolbarManager.add(refreshAction);
         getXViewer().addCustomizeToViewToolbar(toolbarManager);

         IMenuManager manager = viewSite.getActionBars().getMenuManager();
         manager.add(filterCompletedAction);
         manager.add(new Separator());
         manager.add(releaseMetricsAction);
         manager.add(selectionMetricsAction);
         manager.add(new Separator());
         manager.add(toAction);
         manager.add(toWorkFlow);
         if (AtsPlugin.isAtsAdmin()) {
            manager.add(new Separator());
         }
      } else {

         ToolBar toolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);
         toolBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1));

         actionToToolItem(toolBar, expandAllAction);
         actionToToolItem(toolBar, newWorldEditor);
         actionToToolItem(toolBar, newWorldEditorSelected);
         actionToToolItem(toolBar, refreshAction);
         actionToToolItem(toolBar, worldXViewer.getCustomizeAction());

         createToolBarPulldown(toolBar, toolBarComposite);
      }
   }

   public void createToolBarPulldown(final ToolBar toolBar, Composite composite) {
      final ToolItem dropDown = new ToolItem(toolBar, SWT.PUSH);
      dropDown.setImage(AtsPlugin.getInstance().getImage("downTriangle.gif"));
      final Menu menu = new Menu(composite);

      dropDown.addListener(SWT.Selection, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            Rectangle rect = dropDown.getBounds();
            Point pt = new Point(rect.x, rect.y + rect.height);
            pt = toolBar.toDisplay(pt);
            menu.setLocation(pt.x, pt.y);
            menu.setVisible(true);
         }
      });

      actionToMenuItem(menu, filterCompletedAction, SWT.CHECK);
      new MenuItem(menu, SWT.SEPARATOR);
      actionToMenuItem(menu, releaseMetricsAction, SWT.CHECK);
      actionToMenuItem(menu, selectionMetricsAction, SWT.CHECK);
      new MenuItem(menu, SWT.SEPARATOR);
      actionToMenuItem(menu, toAction, SWT.PUSH);
      actionToMenuItem(menu, toWorkFlow, SWT.PUSH);
      if (AtsPlugin.isAtsAdmin()) {
         new MenuItem(menu, SWT.SEPARATOR);
      }
   }

   private ToolItem actionToToolItem(ToolBar toolBar, Action action) {
      final Action fAction = action;
      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(action.getImageDescriptor().createImage());
      item.setToolTipText(action.getToolTipText());
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            fAction.run();
         }
      });
      return item;
   }

   private MenuItem actionToMenuItem(Menu menu, final Action action, final int buttonType) {
      final Action fAction = action;
      MenuItem item = new MenuItem(menu, buttonType);
      item.setText(action.getText());
      if (action.getImageDescriptor() != null) {
         item.setImage(action.getImageDescriptor().createImage());
      }
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (buttonType == SWT.CHECK) {
               action.setChecked(!action.isChecked());
            }
            fAction.run();
         }
      });
      return item;
   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      return getXViewer().getLoadedArtifacts();
   }

   public void updateExtendedStatusString() {
      worldXViewer.setExtendedStatusString(filterCompletedAction.isChecked() ? "[Complete/Cancel Filter]" : "");
   }

   public void redisplayAsAction() {
      try {
         TreeItem treeItem[] = worldXViewer.getTree().getItems();
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
      } catch (OseeCoreException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public void redisplayAsWorkFlow() {
      try {
         TreeItem treeItem[] = worldXViewer.getTree().getItems();
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
      } catch (OseeCoreException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public void disposeComposite() {
      OseeEventManager.removeListener(this);
      if (worldXViewer != null && !worldXViewer.getTree().isDisposed()) {
         worldXViewer.dispose();
      }
   }

   /**
    * @return the xViewer
    */
   public WorldXViewer getXViewer() {
      return worldXViewer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (transData.branchId == AtsPlugin.getAtsBranch().getBranchId()) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  updateExtraInfoLine();
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      }
   }

   /**
    * @return the lastSearchItem
    */
   public WorldSearchItem getLastSearchItem() {
      return lastSearchItem;
   }

}
