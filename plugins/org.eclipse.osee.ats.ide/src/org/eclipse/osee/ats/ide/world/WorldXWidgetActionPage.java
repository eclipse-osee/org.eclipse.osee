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
package org.eclipse.osee.ats.ide.world;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.actions.DeleteTasksAction;
import org.eclipse.osee.ats.ide.actions.NewAction;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsTaskEditorAction;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsTaskEditorSelected;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsWorldEditorAction;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsWorldEditorSelectedAction;
import org.eclipse.osee.ats.ide.actions.TaskAddAction;
import org.eclipse.osee.ats.ide.actions.DeleteTasksAction.TaskArtifactProvider;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalManager;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskComposite;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.AttributeFormPart;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.util.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class WorldXWidgetActionPage extends FormPage {
   protected SwtXWidgetRenderer dynamicXWidgetLayout;
   protected final XFormToolkit toolkit;
   private Composite parametersContainer;
   private Section parameterSection;
   protected Composite resultsContainer;
   protected Section resultsSection;
   protected ScrolledForm scrolledForm;
   private String title;
   private String xWidgetXml;
   public static final String ID = "org.eclipse.osee.ats.ide.actionPage";
   public static final String MENU_GROUP_PRE = "world.menu.group.pre";
   private final WorldEditor worldEditor;
   private WorldComposite worldComposite;
   private Action filterCompletedAction, filterMyAssigneeAction, toAction, toGoal, toReview, toWorkFlow, toTask;
   private final WorldCompletedFilter worldCompletedFilter = new WorldCompletedFilter();
   private WorldAssigneeFilter worldAssigneeFilter = null;
   private WorkflowMetricsUI workflowMetricsUi;

   public WorldXWidgetActionPage(WorldEditor worldEditor) {
      super(worldEditor, ID, worldEditor.isTaskEditor() ? "Tasks" : "Actions");
      this.toolkit = new XFormToolkit();
      this.worldEditor = worldEditor;
   }

   public WorldComposite getWorldComposite() {
      return worldComposite;
   }

   public Result isResearchSearchValid() {
      return worldEditor.isDirty() ? new Result("Changes un-saved. Save first.") : Result.TrueResult;
   }

   public String getXWidgetsXml() {
      if (worldEditor.getWorldEditorProvider() instanceof IWorldEditorParameterProvider) {
         return ((IWorldEditorParameterProvider) worldEditor.getWorldEditorProvider()).getParameterXWidgetXml();
      }
      return null;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      scrolledForm = managedForm.getForm();
      scrolledForm.setImage(ImageManager.getImage(AtsImage.GLOBE));
      setPartName(getTabName());

      Result result = DbConnectionUtility.areOSEEServicesAvailable();
      if (result.isFalse()) {
         AWorkbench.popup("ERROR", "Database Unavailable");
         return;
      }

      FormsUtil.addHeadingGradient(toolkit, scrolledForm, true);

      Composite body = scrolledForm.getBody();
      body.setLayout(ALayout.getZeroMarginLayout(1, true));
      body.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false));

      xWidgetXml = getXWidgetsXml();
      try {
         if (Strings.isValid(xWidgetXml)) {
            managedForm.addPart(new SectionPart(createParametersSection(managedForm, body)));
         }
         managedForm.addPart(new SectionPart(createResultsSection(body)));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      AttributeFormPart.setLabelFonts(body, FontManager.getDefaultLabelFont());

      createToolBar();
      managedForm.refresh();

      try {
         worldEditor.getWorldEditorProvider().run(worldEditor, SearchType.Search, false);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   private void createToolBar() {
      IToolBarManager toolBarManager = scrolledForm.getToolBarManager();
      createToolBar(toolBarManager);
      scrolledForm.updateToolBar();
   }

   public void reflow() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IManagedForm manager = getManagedForm();
            if (manager != null && Widgets.isAccessible(manager.getForm())) {
               getManagedForm().reflow(true);
            }
         }
      });
   }

   private Section createParametersSection(IManagedForm managedForm, Composite body) {
      parameterSection = toolkit.createSection(body, ExpandableComposite.TWISTIE);
      parameterSection.setText("Parameters");
      parameterSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      parametersContainer = toolkit.createClientContainer(parameterSection, 1);
      parameterSection.setExpanded(true);

      Composite mainComp = toolkit.createComposite(parametersContainer, SWT.NONE);
      mainComp.setLayout(ALayout.getZeroMarginLayout(3, false));
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createButtonCompositeOnLeft(mainComp);
      createSearchParametersOnRight(managedForm, mainComp);
      createParametersSectionCompleted(managedForm, mainComp);

      return parameterSection;
   }

   public void createSearchParametersOnRight(IManagedForm managedForm, Composite mainComp) {
      Composite paramComp = new Composite(mainComp, SWT.NONE);
      paramComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      paramComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      List<XWidgetRendererItem> layoutDatas = null;
      dynamicXWidgetLayout = new SwtXWidgetRenderer(getDynamicWidgetLayoutListener(), getXWidgetOptionResolver());
      try {
         layoutDatas = XWidgetParser.extractWorkAttributes(dynamicXWidgetLayout, xWidgetXml);
         if (layoutDatas != null && !layoutDatas.isEmpty()) {
            dynamicXWidgetLayout.addWorkLayoutDatas(layoutDatas);
            dynamicXWidgetLayout.createBody(managedForm, paramComp, null, null, true);
            parametersContainer.layout();
            parametersContainer.getParent().layout();
         }
         parameterSection.setExpanded(true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void createButtonCompositeOnLeft(Composite mainComp) {
      Composite buttonComp = toolkit.createComposite(mainComp, SWT.NONE);
      buttonComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      buttonComp.setLayoutData(new GridData(SWT.NONE, SWT.FILL, false, true));

      Button runButton = toolkit.createButton(buttonComp, "Search", SWT.PUSH);
      GridData gridData = new GridData(SWT.FILL, SWT.BOTTOM, true, true);
      runButton.setLayoutData(gridData);
      runButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleSearchButtonPressed();
         }
      });

      buttonComp.layout();
   }

   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      if (worldEditor.getWorldEditorProvider() instanceof IWorldEditorParameterProvider) {
         return ((IWorldEditorParameterProvider) worldEditor.getWorldEditorProvider()).getDynamicWidgetLayoutListener();
      }
      return null;
   }

   public void reSearch() {
      Result result = isResearchSearchValid();
      if (result.isFalse()) {
         AWorkbench.popup(result);
         return;
      }
      reSearch(false);
   }

   public IXWidgetOptionResolver getXWidgetOptionResolver() {
      return new DefaultXWidgetOptionResolver();
   }

   public void handleSearchButtonPressed() {
      try {
         reSearch();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /*
    * Mainly for testing purposes
    */
   public void reSearch(boolean forcePend) {
      worldEditor.getWorldEditorProvider().run(worldEditor, SearchType.ReSearch, forcePend);
   }

   public void setTableTitle(final String title, final boolean warning) {
      this.title = Strings.truncate(title, WorldEditor.TITLE_MAX_LENGTH);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(scrolledForm)) {
               scrolledForm.setText(title);
            }
         };
      });
   }

   public Section createResultsSection(Composite body) {

      resultsSection = toolkit.createSection(body, ExpandableComposite.NO_TITLE);
      resultsSection.setLayoutData(new GridData(GridData.FILL_BOTH));

      resultsContainer = toolkit.createClientContainer(resultsSection, 1);

      worldComposite = new WorldComposite(worldEditor, resultsContainer, SWT.BORDER | SWT.NO_SCROLL);
      worldComposite.setLayout(ALayout.getZeroMarginLayout());
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.widthHint = 100;
      gd.heightHint = 100;
      worldComposite.setLayoutData(gd);
      workflowMetricsUi = new WorkflowMetricsUI(worldComposite, toolkit);

      toolkit.adapt(worldComposite);

      return resultsSection;
   }

   public ScrolledForm getScrolledForm() {
      return scrolledForm;
   }

   public String getCurrentTitleLabel() {
      String useTitle = "World Editor";
      if (title != null) {
         useTitle = title;
      }
      return Strings.truncate(useTitle, WorldEditor.TITLE_MAX_LENGTH);
   }

   private String getTabName() {
      String name = "Tasks";
      try {
         if (getTaskComposite() != null) {
            TeamWorkFlowArtifact teamArt = getTaskComposite().getTeamArt();
            name = String.format("Tasks (%d)", AtsClientService.get().getTaskService().getTasks(teamArt).size());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return name;
   }

   public class DropDownAction extends Action implements IMenuCreator {
      private Menu fMenu;

      public DropDownAction() {
         setText("Other");
         setMenuCreator(this);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GEAR));
         addKeyListener();
      }

      @Override
      public Menu getMenu(Control parent) {
         if (fMenu != null) {
            fMenu.dispose();
         }

         fMenu = new Menu(parent);
         addActionToMenu(fMenu, workflowMetricsUi.getOrCreateAction());
         addActionToMenu(fMenu, filterCompletedAction);
         addActionToMenu(fMenu, filterMyAssigneeAction);
         if (!worldEditor.isTaskEditor()) {
            new MenuItem(fMenu, SWT.SEPARATOR);
            addActionToMenu(fMenu, toAction);
            addActionToMenu(fMenu, toGoal);
            addActionToMenu(fMenu, toWorkFlow);
            addActionToMenu(fMenu, toTask);
            addActionToMenu(fMenu, toReview);
         }

         worldEditor.createToolBarPulldown(fMenu);

         return fMenu;
      }

      @Override
      public void dispose() {
         if (fMenu != null) {
            fMenu.dispose();
            fMenu = null;
         }
      }

      @Override
      public Menu getMenu(Menu parent) {
         return null;
      }

      protected void addActionToMenu(Menu parent, Action action) {
         ActionContributionItem item = new ActionContributionItem(action);
         item.fill(parent, -1);
      }

      @Override
      public void run() {
         // provided for subclass implementation
      }

      /**
       * Get's rid of the menu, because the menu hangs on to * the searches, etc.
       */
      void clear() {
         dispose();
      }

      private void addKeyListener() {
         Tree tree = worldComposite.getXViewer().getTree();
         GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
         gridData.heightHint = 100;
         gridData.widthHint = 100;
         tree.setLayoutData(gridData);
         tree.setHeaderVisible(true);
         tree.setLinesVisible(true);

         worldComposite.getXViewer().getTree().addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent event) {
               // do nothing
            }

            @Override
            public void keyReleased(KeyEvent event) {
               // if CTRL key is already pressed
               if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
                  if (event.keyCode == 'a') {
                     worldComposite.getXViewer().getTree().setSelection(
                        worldComposite.getXViewer().getTree().getItems());
                  } else if (event.keyCode == 'x') {
                     workflowMetricsUi.getOrCreateAction().setChecked(
                        !workflowMetricsUi.getOrCreateAction().isChecked());
                     workflowMetricsUi.getOrCreateAction().run();
                  } else if (event.keyCode == 'f') {
                     filterCompletedAction.setChecked(!filterCompletedAction.isChecked());
                     filterCompletedAction.run();
                  } else if (event.keyCode == 'g') {
                     filterMyAssigneeAction.setChecked(!filterMyAssigneeAction.isChecked());
                     filterMyAssigneeAction.run();
                  } else if (event.keyCode == 'd') {
                     filterMyAssigneeAction.setChecked(!filterMyAssigneeAction.isChecked());
                     filterCompletedAction.setChecked(!filterCompletedAction.isChecked());
                     filterCompletedAction.run();
                     filterMyAssigneeAction.run();
                  }
               }

            }
         });
      }
   }

   protected void createToolBar(IToolBarManager toolBarManager) {

      toolBarManager.add(new GroupMarker(MENU_GROUP_PRE));
      if (worldEditor.isTaskEditor()) {
         try {
            TaskComposite taskComposite = getTaskComposite();
            if (taskComposite != null && taskComposite.getIXTaskViewer().isTasksEditable()) {
               toolBarManager.add(new TaskAddAction(taskComposite));
               TaskArtifactProvider taskProvider = new TaskArtifactProvider() {

                  @Override
                  public List<TaskArtifact> getSelectedArtifacts() {
                     return taskComposite.getSelectedTaskArtifacts();
                  }
               };
               toolBarManager.add(new DeleteTasksAction(taskProvider));
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      toolBarManager.add(new Separator());

      toolBarManager.add(worldComposite.getXViewer().getCustomizeAction());
      toolBarManager.add(new Separator());
      if (worldEditor.isTaskEditor()) {
         toolBarManager.add(new OpenNewAtsTaskEditorAction(worldComposite));
         toolBarManager.add(new OpenNewAtsTaskEditorSelected(worldComposite));
      } else {
         toolBarManager.add(new OpenNewAtsWorldEditorAction(worldComposite));
         toolBarManager.add(new OpenNewAtsWorldEditorSelectedAction(worldComposite));
      }
      toolBarManager.add(new Separator());
      toolBarManager.add(new ExpandAllAction(worldComposite.getXViewer()));
      toolBarManager.add(new CollapseAllAction(worldComposite.getXViewer()));
      toolBarManager.add(new RefreshAction(worldComposite));
      toolBarManager.add(new Separator());
      toolBarManager.add(new NewAction());
      toolBarManager.add(new Separator());

      createDropDownMenuActions();
      toolBarManager.add(new DropDownAction());

      try {
         if (worldEditor.getWorldEditorProvider() instanceof IWorldEditorParameterProvider) {
            ((IWorldEditorParameterProvider) worldEditor.getWorldEditorProvider()).createToolbar(toolBarManager);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   private TaskComposite getTaskComposite() {
      TaskComposite taskComposite = null;
      if (worldComposite instanceof TaskComposite) {
         taskComposite = (TaskComposite) worldComposite;
      }
      return taskComposite;
   }

   protected void createDropDownMenuActions() {
      try {
         worldAssigneeFilter = new WorldAssigneeFilter();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      workflowMetricsUi.getOrCreateAction();

      filterCompletedAction = new Action("Filter Out Completed/Cancelled - Ctrl-F", IAction.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (filterCompletedAction.isChecked()) {
               worldComposite.getXViewer().addFilter(worldCompletedFilter);
            } else {
               worldComposite.getXViewer().removeFilter(worldCompletedFilter);
            }
            updateExtendedStatusString();
            worldComposite.getXViewer().refresh();
         }
      };
      filterCompletedAction.setToolTipText("Filter Out Completed/Cancelled - Ctrl-F");
      filterCompletedAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GREEN_PLUS));

      filterMyAssigneeAction = new Action("Filter My Assignee - Ctrl-G", IAction.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (filterMyAssigneeAction.isChecked()) {
               worldComposite.getXViewer().addFilter(worldAssigneeFilter);
            } else {
               worldComposite.getXViewer().removeFilter(worldAssigneeFilter);
            }
            updateExtendedStatusString();
            worldComposite.getXViewer().refresh();
         }
      };
      filterMyAssigneeAction.setToolTipText("Filter My Assignee - Ctrl-G");
      filterMyAssigneeAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.USER));

      toAction = new Action("Re-display as Actions", IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsAction();
         }
      };
      toAction.setToolTipText("Re-display as Actions");
      toAction.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ACTION));

      toGoal = new Action("Re-display as Goals", IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsGoals();
         }
      };
      toGoal.setToolTipText("Re-display as Goals");
      toGoal.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GOAL));

      toWorkFlow = new Action("Re-display as WorkFlows", IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsWorkFlow();
         }
      };
      toWorkFlow.setToolTipText("Re-display as WorkFlows");
      toWorkFlow.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.WORKFLOW));

      toTask = new Action("Re-display as Tasks", IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsTask();
         }
      };
      toTask.setToolTipText("Re-display as Tasks");
      toTask.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TASK));

      toReview = new Action("Re-display as Reviews", IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsReviews();
         }
      };
      toReview.setToolTipText("Re-display as Reviews");
      toReview.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.REVIEW));

   }

   public void redisplayAsAction() {
      final List<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job("Re-display as Actions") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<>();
               for (Artifact art : artifacts) {
                  if (art.isOfType(AtsArtifactTypes.Action)) {
                     arts.add(art);
                  } else if (art instanceof AbstractWorkflowArtifact) {
                     Artifact parentArt = ((AbstractWorkflowArtifact) art).getParentActionArtifact();
                     if (parentArt != null) {
                        arts.add(parentArt);
                     }
                  }
               }
               worldComposite.load(worldEditor.getWorldXWidgetActionPage().getCurrentTitleLabel(), arts);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void redisplayAsGoals() {
      final List<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job("Re-display as Goals") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> goals = new HashSet<>();
               new GoalManager().getCollectors(artifacts, goals, true);
               worldComposite.load(worldEditor.getWorldXWidgetActionPage().getCurrentTitleLabel(), goals);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void redisplayAsWorkFlow() {
      final List<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job("Re-display as Workflows") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<>();
               for (Artifact art : artifacts) {
                  if (art.isOfType(AtsArtifactTypes.Action)) {
                     arts.addAll(Collections.castAll(
                        AtsObjects.getArtifacts(AtsClientService.get().getWorkItemService().getTeams(art))));
                  } else if (art instanceof AbstractWorkflowArtifact) {
                     Artifact parentArt = ((AbstractWorkflowArtifact) art).getParentTeamWorkflow();
                     if (parentArt != null) {
                        arts.add(parentArt);
                     }
                  }
               }
               worldComposite.load(worldEditor.getWorldXWidgetActionPage().getCurrentTitleLabel(), arts);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void redisplayAsTask() {
      final List<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job("Re-display as Tasks") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<>();
               for (Artifact art : artifacts) {
                  if (art.isOfType(AtsArtifactTypes.Action)) {
                     for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(art)) {
                        arts.addAll(Collections.castAll(
                           AtsObjects.getArtifacts(AtsClientService.get().getTaskService().getTasks(team))));
                     }
                  } else if (art instanceof TeamWorkFlowArtifact) {
                     arts.addAll(Collections.castAll(AtsObjects.getArtifacts(
                        AtsClientService.get().getTaskService().getTasks((TeamWorkFlowArtifact) art))));
                  }
               }
               worldComposite.load(worldEditor.getWorldXWidgetActionPage().getCurrentTitleLabel(), arts);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void redisplayAsReviews() {
      final List<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job("Re-display as Reviews") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<>();
               for (Artifact art : artifacts) {
                  if (art.isOfType(AtsArtifactTypes.Action)) {
                     for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(art)) {
                        arts.addAll(Collections.castAll(
                           AtsObjects.getArtifacts(AtsClientService.get().getReviewService().getReviews(team))));
                     }
                  } else if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                     arts.addAll(ReviewManager.getReviews((TeamWorkFlowArtifact) art));
                  }
               }
               worldComposite.load(worldEditor.getWorldXWidgetActionPage().getCurrentTitleLabel(), arts);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void updateExtendedStatusString() {
      worldComposite.getXViewer().setExtendedStatusString(
         //
         (filterCompletedAction.isChecked() ? "[Complete/Cancel Filter]" : "") +
         //
            (filterMyAssigneeAction.isChecked() ? "[My Assignee Filter]" : ""));
   }

   public void createParametersSectionCompleted(IManagedForm managedForm, Composite mainComp) {
      try {
         if (worldEditor.getWorldEditorProvider() instanceof IWorldEditorParameterProvider) {
            IWorldEditorParameterProvider provider =
               (IWorldEditorParameterProvider) worldEditor.getWorldEditorProvider();
            provider.createParametersSectionCompleted(managedForm, mainComp);
            String editorTitle = provider.getSelectedName(SearchType.Search);
            if (Strings.isValid(editorTitle)) {
               ((WorldEditor) getEditor()).setEditorTitle(editorTitle);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}