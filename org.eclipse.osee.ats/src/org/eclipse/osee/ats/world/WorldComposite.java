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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.export.AtsExportManager;
import org.eclipse.osee.ats.export.AtsExportManager.ExportOption;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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

/**
 * @author Donald G. Dunne
 */
public class WorldComposite extends ScrolledComposite implements IFrameworkTransactionEventListener {

   private Action filterCompletedAction, releaseMetricsAction, selectionMetricsAction, toAction, toReview, toWorkFlow,
         toTask;
   private Label extraInfoLabel;
   private final WorldXViewer worldXViewer;
   private final WorldCompletedFilter worldCompletedFilter = new WorldCompletedFilter();
   private final Set<Artifact> worldArts = new HashSet<Artifact>(200);
   private final Set<Artifact> otherArts = new HashSet<Artifact>(200);
   private TableLoadOption[] tableLoadOptions;
   private final ToolBar toolBar;
   private final WorldEditor worldEditor;
   private final Composite mainComp;

   public WorldComposite(WorldEditor worldEditor, Composite parent, int style, ToolBar toolBar) {
      super(parent, style);
      this.worldEditor = worldEditor;
      this.toolBar = toolBar;

      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      mainComp = new Composite(this, SWT.NONE);
      mainComp.setLayout(new GridLayout());
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(this)) {
         extraInfoLabel = null;
         worldXViewer = null;
         return;
      }

      extraInfoLabel = new Label(mainComp, SWT.NONE);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      worldXViewer = new WorldXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
      worldXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      worldXViewer.setContentProvider(new WorldContentProvider(worldXViewer));
      worldXViewer.setLabelProvider(new WorldLabelProvider(worldXViewer));

      Tree tree = worldXViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
      gridData.heightHint = 100;
      gridData.widthHint = 100;
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

      new WorldViewDragAndDrop(this, WorldEditor.EDITOR_ID);

      createActions();

      setContent(mainComp);
      setExpandHorizontal(true);
      setExpandVertical(true);
      layout();

      OseeEventManager.addListener(this);
   }

   public double getManHoursPerDayPreference() throws OseeCoreException {
      if (worldArts.size() > 0) {
         Artifact artifact = worldArts.iterator().next();
         if (artifact instanceof ActionArtifact) {
            artifact = ((ActionArtifact) artifact).getTeamWorkFlowArtifacts().iterator().next();
         }
         return ((StateMachineArtifact) artifact).getManHrsPerDayPreference();
      }
      return StateMachineArtifact.DEFAULT_MAN_HOURS_PER_DAY;
   }

   public void setCustomizeData(CustomizeData customizeData) {
      worldXViewer.getCustomizeMgr().loadCustomization(customizeData);
   }

   public Control getControl() {
      return worldXViewer.getControl();
   }

   public void load(final String name, final Collection<? extends Artifact> arts, TableLoadOption... tableLoadOption) {
      load(name, arts, null, tableLoadOption);
   }

   public void load(final String name, final Collection<? extends Artifact> arts, final CustomizeData customizeData, TableLoadOption... tableLoadOption) {
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
            if (customizeData != null && !worldXViewer.getCustomizeMgr().generateCustDataFromTable().equals(
                  customizeData)) {
               setCustomizeData(customizeData);
            }
            if (arts.size() == 0)
               setTableTitle("No Results Found - " + name, true);
            else
               setTableTitle(name, false);
            worldXViewer.refresh();
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
      // Need to reflow the managed page based on the results.  Don't put this in the above thread.
      worldEditor.getActionPage().reflow();
   }

   public class FilterLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         try {
            return ((WorldSearchItem) arg0).getSelectedName(SearchType.Search);
         } catch (OseeCoreException ex) {
            return ex.getLocalizedMessage();
         }
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

   public void setTableTitle(final String title, final boolean warning) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               worldEditor.setTableTitle(title, warning);
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
            verArt = smaArts.iterator().next().getWorldViewTargetedVersion();
            SMAMetrics sMet = new SMAMetrics(smaArts, verArt, smaArts.iterator().next().getManHrsPerDayPreference());
            str = sMet.toString();
         }
      } else if (selectionMetricsAction.isChecked() && getXViewer().getSelectedSMAArtifacts().size() > 0) {
         SMAMetrics sMet =
               new SMAMetrics(getXViewer().getSelectedSMAArtifacts(), null,
                     getXViewer().getSelectedSMAArtifacts().iterator().next().getManHrsPerDayPreference());
         str = sMet.toString();
      }

      extraInfoLabel.setText(str);
      extraInfoLabel.getParent().layout();
   }

   protected void createActions() {

      Action newWorldEditor = new Action("Open New ATS World Editor") {

         @Override
         public void run() {
            try {
               IWorldEditorProvider provider =
                     ((WorldEditorInput) worldEditor.getEditorInput()).getIWorldEditorProvider().copyProvider();
               provider.setCustomizeData(worldXViewer.getCustomizeMgr().generateCustDataFromTable());
               provider.setTableLoadOptions(TableLoadOption.NoUI);
               WorldEditor.open(provider);
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
      newWorldEditor.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("globe.gif"));
      newWorldEditor.setToolTipText("Open in ATS World Editor");

      Action newWorldEditorSelected = new Action("Open Selected in ATS World Editor") {

         @Override
         public void run() {

            if (worldXViewer.getSelectedArtifacts().size() == 0) {
               AWorkbench.popup("ERROR", "Select items to open");
               return;
            }
            try {
               WorldEditor.open(new WorldEditorSimpleProvider("ATS World", worldXViewer.getSelectedArtifacts(),
                     worldXViewer.getCustomizeMgr().generateCustDataFromTable(), tableLoadOptions));
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
      newWorldEditorSelected.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("globeSelect.gif"));
      newWorldEditorSelected.setToolTipText("Open Selected in ATS World Editor");

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
               worldEditor.reSearch();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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

      toAction = new Action("Re-display as Actions", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsAction();
         }
      };
      toAction.setToolTipText("Re-display as Actions");

      toWorkFlow = new Action("Re-display as WorkFlows", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsWorkFlow();
         }
      };
      toWorkFlow.setToolTipText("Re-display as WorkFlows");

      toTask = new Action("Re-display as Tasks", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsTask();
         }
      };
      toTask.setToolTipText("Re-display as Tasks");

      toReview = new Action("Re-display as Reviews", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsReviews();
         }
      };
      toReview.setToolTipText("Re-display as Reviews");

      if (toolBar != null) {
         actionToToolItem(toolBar, refreshAction, "refresh.gif");
         new ToolItem(toolBar, SWT.SEPARATOR);
         actionToToolItem(toolBar, expandAllAction, "expandAll.gif");
         actionToToolItem(toolBar, worldXViewer.getCustomizeAction(), "customize.gif");
         new ToolItem(toolBar, SWT.SEPARATOR);
         actionToToolItem(toolBar, newWorldEditor, "globe.gif");
         actionToToolItem(toolBar, newWorldEditorSelected, "globeSelect.gif");
         new ToolItem(toolBar, SWT.SEPARATOR);
         actionToToolItem(toolBar, new NewAction(), "newAction.gif");

         OseeAts.addButtonToEditorToolBar(worldEditor, AtsPlugin.getInstance(), toolBar, WorldEditor.EDITOR_ID,
               "ATS World");
         new ToolItem(toolBar, SWT.SEPARATOR);

         createToolBarPulldown(toolBar, toolBar.getParent());
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

      Action exportSelectedArtifacts = new Action("Export Selected ATS Artifacts", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            try {
               AtsExportManager.export(worldXViewer.getSelection(), ExportOption.POPUP_DIALOG);
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
      exportSelectedArtifacts.setToolTipText("Allows ATS artifacts to be exported from OSEE.");

      actionToMenuItem(menu, filterCompletedAction, SWT.CHECK);
      new MenuItem(menu, SWT.SEPARATOR);
      actionToMenuItem(menu, releaseMetricsAction, SWT.CHECK);
      actionToMenuItem(menu, selectionMetricsAction, SWT.CHECK);
      new MenuItem(menu, SWT.SEPARATOR);
      actionToMenuItem(menu, exportSelectedArtifacts, SWT.PUSH);
      new MenuItem(menu, SWT.SEPARATOR);
      actionToMenuItem(menu, toAction, SWT.PUSH);
      actionToMenuItem(menu, toWorkFlow, SWT.PUSH);
      actionToMenuItem(menu, toTask, SWT.PUSH);
      actionToMenuItem(menu, toReview, SWT.PUSH);
      new MenuItem(menu, SWT.SEPARATOR);
      try {
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            for (final IAtsWorldEditorMenuItem atsMenuItem : item.getWorldEditorMenuItems(
                  worldEditor.getWorldEditorProvider(), worldEditor)) {
               MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
               menuItem.setText(atsMenuItem.getName());
               menuItem.addSelectionListener(new SelectionAdapter() {
                  /* (non-Javadoc)
                   * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                   */
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                     try {
                        atsMenuItem.run(worldEditor);
                     } catch (Exception ex) {
                        OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                     }
                  }
               });
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

   }

   private ToolItem actionToToolItem(ToolBar toolBar, Action action, String imageName) {
      final Action fAction = action;
      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(AtsPlugin.getInstance().getImage(imageName));
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
      final ArrayList<Artifact> artifacts = worldXViewer.getLoadedArtifacts();
      Job job = new Job("Re-display as Actions") {
         /* (non-Javadoc)
          * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
          */
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<Artifact>();
               for (Artifact art : artifacts) {
                  if (art instanceof ActionArtifact) {
                     arts.add(art);
                  } else if (art instanceof StateMachineArtifact) {
                     Artifact parentArt = ((StateMachineArtifact) art).getParentActionArtifact();
                     if (parentArt != null) {
                        arts.add(parentArt);
                     }
                  }
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     load(worldEditor.getCurrentTitleLabel(), arts);
                  }
               });
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void redisplayAsWorkFlow() {
      final ArrayList<Artifact> artifacts = worldXViewer.getLoadedArtifacts();
      Job job = new Job("Re-display as Workflows") {
         /* (non-Javadoc)
          * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
          */
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<Artifact>();
               for (Artifact art : artifacts) {
                  if (art instanceof ActionArtifact) {
                     arts.addAll(((ActionArtifact) art).getTeamWorkFlowArtifacts());
                  } else if (art instanceof StateMachineArtifact) {
                     Artifact parentArt = ((StateMachineArtifact) art).getParentTeamWorkflow();
                     if (parentArt != null) {
                        arts.add(parentArt);
                     }
                  }
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     load(worldEditor.getCurrentTitleLabel(), arts);
                  }
               });
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void redisplayAsTask() {
      final ArrayList<Artifact> artifacts = worldXViewer.getLoadedArtifacts();
      Job job = new Job("Re-display as Tasks") {
         /* (non-Javadoc)
          * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
          */
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<Artifact>();
               for (Artifact art : artifacts) {
                  if (art instanceof ActionArtifact) {
                     for (TeamWorkFlowArtifact team : ((ActionArtifact) art).getTeamWorkFlowArtifacts()) {
                        arts.addAll(team.getSmaMgr().getTaskMgr().getTaskArtifacts());
                     }
                  } else if (art instanceof StateMachineArtifact) {
                     arts.addAll(((StateMachineArtifact) art).getSmaMgr().getTaskMgr().getTaskArtifacts());
                  }
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     load(worldEditor.getCurrentTitleLabel(), arts);
                  }
               });
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void redisplayAsReviews() {
      final ArrayList<Artifact> artifacts = worldXViewer.getLoadedArtifacts();
      Job job = new Job("Re-display as Reviews") {
         /* (non-Javadoc)
          * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
          */
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<Artifact>();
               for (Artifact art : artifacts) {
                  if (art instanceof ActionArtifact) {
                     for (TeamWorkFlowArtifact team : ((ActionArtifact) art).getTeamWorkFlowArtifacts()) {
                        arts.addAll(team.getSmaMgr().getReviewManager().getReviews());
                     }
                  } else if (art instanceof StateMachineArtifact) {
                     arts.addAll(((StateMachineArtifact) art).getSmaMgr().getReviewManager().getReviews());
                  }
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     load(worldEditor.getCurrentTitleLabel(), arts);
                  }
               });
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
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

}
