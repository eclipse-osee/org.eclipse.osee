/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.CreateTeamData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.ai.ModifyActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitionUtility;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AITreeContentProvider;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AtsObjectNameSorter;
import org.eclipse.osee.ats.ide.workflow.duplicate.DuplicateWorkflowAction;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateFilteredTreeViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateTreeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.ICheckBoxStateTreeListener;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.ICheckBoxStateTreeViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTree;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ModifyActionableItemsBlam extends AbstractBlam {

   protected final static String TEAM_WORKFLOW = "Team Workflow (drop here)";
   private TeamWorkFlowArtifact defaultTeamWorkflow;
   private FilteredCheckboxTree wfTree;
   private FilteredCheckboxTree otherTree;
   protected CheckBoxStateFilteredTreeViewer<IAtsActionableItem> newTree;
   protected XListDropViewer dropViewer;
   private XText resultsText;
   private Set<IAtsActionableItem> currAIsForAllWfs;
   private List<IAtsActionableItem> currWorkflowDesiredAIs;
   private List<IAtsActionableItem> newAIs;
   private WorkflowsActiveAisContentProvider wfAiProvider;

   public ModifyActionableItemsBlam() {
      // do nothing
   }

   protected String getDropLabelStr() {
      return TEAM_WORKFLOW;
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(getDropLabelStr())) {
         createTreeViewers(xWidget.getControl().getParent());

         dropViewer = (XListDropViewer) xWidget;
         if (defaultTeamWorkflow != null) {
            dropViewer.setInput(defaultTeamWorkflow);
         }
         dropViewer.addXModifiedListener(new DropListener());
         Control control = dropViewer.getControl();
         GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
         data.heightHint = 20;
         control.setLayoutData(data);
      }
   }

   protected void createTreeViewers(Composite parent) {

      Composite treeComp = new Composite(parent, SWT.BORDER);
      treeComp.setLayout(new GridLayout(getLayoutColumns(), true));
      GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
      data.heightHint = 300;
      treeComp.setLayoutData(data);

      if (displayWfTree()) {
         Label wfLabel = new Label(treeComp, SWT.BOLD | SWT.CENTER);
         wfLabel.setText("This Workflow's Actionable Items\n(select to add to this workflow)");
      }

      if (displayOtherTree()) {
         Label otherLabel = new Label(treeComp, SWT.BOLD | SWT.CENTER);
         otherLabel.setText("Actionable Items in other Team Workflows\n(readonly)");
      }

      Label newLabel = new Label(treeComp, SWT.BOLD | SWT.CENTER);
      newLabel.setText("New Workflows\n(select to create new workflows)");

      if (displayWfTree()) {
         wfTree = new FilteredCheckboxTree(treeComp, SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
         wfAiProvider = new WorkflowsActiveAisContentProvider(defaultTeamWorkflow == null ? null : defaultTeamWorkflow,
            Active.Active);
         wfTree.getViewer().setContentProvider(wfAiProvider);
         wfTree.getViewer().setLabelProvider(new AtsObjectLabelProvider());
         wfTree.getViewer().setComparator(new AtsObjectNameSorter());
         wfTree.setLayoutData(data);
         wfTree.getViewer().addPostSelectionChangedListener(new ModificationListener());
      }

      if (displayOtherTree()) {
         otherTree = new FilteredCheckboxTree(treeComp, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
         otherTree.getViewer().setContentProvider(new ArrayTreeContentProvider());
         otherTree.getViewer().setLabelProvider(new AtsObjectLabelProvider());
         otherTree.getViewer().setComparator(new AtsObjectNameSorter());
         otherTree.setLayoutData(data);
         otherTree.setEnabled(false);
      }

      newTree =
         new CheckBoxStateFilteredTreeViewer<>(treeComp, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      newTree.getViewer().setContentProvider(new AITreeContentProvider(Active.Active));
      newTree.getViewer().setLabelProvider(new AITreeLabelProvider(newTree));
      newTree.getViewer().setComparator(new AtsObjectNameSorter());
      newTree.setLayoutData(data);
      newTree.addCheckListener(new ICheckBoxStateTreeListener() {

         @Override
         public void checkStateChanged(Object obj) {
            if (obj instanceof IAtsActionableItem) {
               IAtsActionableItem ai = (IAtsActionableItem) obj;
               if (ai.isActionable()) {
                  ICheckBoxStateTreeListener.super.checkStateChanged(obj);
               } else {
                  if (newTree.getChecked().contains(ai)) {
                     newTree.setChecked(ai, false);
                  }
               }
            }
         }

      });
      newTree.getViewer().addPostSelectionChangedListener(new ModificationListener());

      resultsText = new XText("Results if run");
      resultsText.setVerticalLabel(true);
      GridData data2 = new GridData(SWT.FILL, SWT.NONE, true, false);
      data2.heightHint = 100;
      resultsText.setFillHorizontally(true);
      resultsText.setFillVertically(true);
      resultsText.createWidgets(parent, 1);
      resultsText.getStyledText().setLayoutData(data2);

      Composite buttonComp = new Composite(parent, SWT.NONE);
      buttonComp.setLayout(new GridLayout(2, true));
      buttonComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      Button executeButton = new Button(buttonComp, SWT.PUSH | SWT.LEFT);
      executeButton.setText(getRunText());
      executeButton.setImage(ImageManager.getImage(FrameworkImage.RUN_EXC));
      executeButton.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, false));
      executeButton.addListener(SWT.MouseUp, new Listener() {

         @Override
         public void handleEvent(Event event) {
            runOperation(null, null);
         }

      });

      if (displayDuplicateButton()) {
         Button advancedDuplicate = new Button(buttonComp, SWT.PUSH | SWT.RIGHT);
         advancedDuplicate.setText("Open Advanced Duplicate Workflow");
         advancedDuplicate.setImage(ImageManager.getImage(FrameworkImage.DUPLICATE));
         advancedDuplicate.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, false));
         advancedDuplicate.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
               TeamWorkFlowArtifact teamWf = getDroppedTeamWf();
               List<TeamWorkFlowArtifact> teams = new ArrayList<>();
               if (teamWf != null) {
                  teams.add(teamWf);
               }
               new DuplicateWorkflowAction(teams).run();
            }

         });
      }

      if (defaultTeamWorkflow != null) {
         refreshTables(defaultTeamWorkflow);
      }
   }

   public static class AITreeLabelProvider extends CheckBoxStateTreeLabelProvider {

      public AITreeLabelProvider(ICheckBoxStateTreeViewer treeViewer) {
         super(treeViewer);
      }

      @Override
      protected boolean isEnabled(Object element) {
         boolean enabled = false;
         if (element instanceof IAtsActionableItem) {
            IAtsActionableItem ai = (IAtsActionableItem) element;
            if (ai.isActionable()) {
               enabled = true;
            }
         }
         return enabled;
      }

   }

   protected int getLayoutColumns() {
      return 3;
   }

   private class ModificationListener implements ISelectionChangedListener {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
         refreshResultsArea(getDroppedTeamWf());
      }
   }

   private class DropListener implements XModifiedListener {

      @Override
      public void widgetModified(XWidget widget) {
         refreshTables(getDroppedTeamWf());
      }

   }

   private void refreshTables(final TeamWorkFlowArtifact teamWf) {
      final WorkflowsActiveAisContentProvider fWfAiProvider = wfAiProvider;

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (teamWf == null) {
               clearTables();
            } else {
               try {
                  if (wfTree != null) {
                     fWfAiProvider.setTeamWf(teamWf);
                     wfTree.getViewer().setInput(teamWf);
                     Set<IAtsActionableItem> actionableItems = teamWf.getActionableItems();
                     wfTree.setInitalChecked(actionableItems);
                  }

                  if (otherTree != null) {
                     Set<IAtsActionableItem> ais = new HashSet<>();
                     for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemServiceIde().getTeams(
                        teamWf.getParentAction())) {
                        if (team.notEqual(teamWf)) {
                           ais.addAll(team.getActionableItems());
                        }
                     }
                     otherTree.getViewer().setInput(ais);
                  }

                  newTree.getViewer().setInput(
                     AtsApiService.get().getActionableItemService().getTopLevelActionableItems(Active.Active));
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
            refreshResultsArea(teamWf);
         }
      });
   }

   private void clearTables() {
      List<Object> emptyList = Collections.emptyList();
      if (wfTree != null) {
         wfTree.getViewer().setInput(emptyList);
         wfTree.clearChecked();
      }
      if (otherTree != null) {
         otherTree.getViewer().setInput(emptyList);
      }
      newTree.getViewer().setInput(emptyList);
   }

   private void refreshResultsArea(TeamWorkFlowArtifact teamWf) {
      XResultData results = new XResultData(false);
      performModification(teamWf, results, true);
      resultsText.setText(results.toString());
   }

   private TeamWorkFlowArtifact getDroppedTeamWf() {
      TeamWorkFlowArtifact teamWf = null;
      List<Artifact> artifacts = dropViewer.getArtifacts();
      if (artifacts.size() == 1) {
         teamWf = (TeamWorkFlowArtifact) artifacts.iterator().next();
      }
      return teamWf;
   }

   private void performModification(TeamWorkFlowArtifact teamWf, XResultData results, boolean logOnly) {
      if (teamWf == null) {
         AWorkbench.popup("Must drop a Team Workflow to modify");
         return;
      }
      try {
         currAIsForAllWfs = new HashSet<>();
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemServiceIde().getTeams(teamWf.getParentAction())) {
            currAIsForAllWfs.addAll(team.getActionableItems());
         }

         if (wfTree != null) {
            currWorkflowDesiredAIs = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(wfTree.getChecked());
         }

         Collection<IAtsActionableItem> checked = newTree.getChecked();
         newAIs = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(checked);
         AtsUser modifiedBy = AtsApiService.get().getUserService().getCurrentUser();

         ModifyActionableItems job = new ModifyActionableItems(results, teamWf, currAIsForAllWfs,
            currWorkflowDesiredAIs, newAIs, modifiedBy, new TeamDefinitionUtility(), AtsApiService.get());
         job.performModification();

         if (!logOnly) {
            if (results.isErrors()) {
               AWorkbench.popup("Must resolve all errors before running");
               return;
            }
            ModifyActionableItemOperation op = new ModifyActionableItemOperation(teamWf, results, job);
            Operations.executeAsJob(op, false, Job.SHORT, new ModifyActionableItemListener(op));
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public class ModifyActionableItemListener extends JobChangeAdapter {

      private final ModifyActionableItemOperation op;

      public ModifyActionableItemListener(ModifyActionableItemOperation op) {
         this.op = op;
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         List<IAtsTeamWorkflow> newTeamWfs = op.getNewTeamWfs();
         refreshTables(op.getTeamWf());
         if (!newTeamWfs.isEmpty()) {
            AtsEditors.openInAtsWorldEditor("New Team Workflows", newTeamWfs);
         }
      }
   }

   public class ModifyActionableItemOperation extends AbstractOperation {

      private final ModifyActionableItems job;
      List<IAtsTeamWorkflow> newTeamWfs = new ArrayList<>();
      private final TeamWorkFlowArtifact teamWf;

      public TeamWorkFlowArtifact getTeamWf() {
         return teamWf;
      }

      private final XResultData results;

      public ModifyActionableItemOperation(TeamWorkFlowArtifact teamWf, XResultData results, ModifyActionableItems job) {
         super("Modify Actionable Items", Activator.PLUGIN_ID);
         this.teamWf = teamWf;
         this.results = results;
         this.job = job;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(getName());
         Date createdDate = new Date();
         for (CreateTeamData data : job.getTeamDatas()) {
            IAtsTeamWorkflow newTeamWf =
               AtsApiService.get().getActionService().createTeamWorkflow(teamWf.getParentAction(), data.getTeamDef(),
                  data.getActionableItems(), new LinkedList<AtsUser>(data.getAssignees()), changes, createdDate,
                  data.getCreatedBy(), null, data.getCreateTeamOption());
            newTeamWfs.add(newTeamWf);
         }

         for (IAtsActionableItem checkedAi : job.getAddAis()) {
            results.logf("Actionable Item [%s] will be added to this workflow\n", checkedAi);
            AtsApiService.get().getActionableItemService().addActionableItem(teamWf, checkedAi, changes);
         }
         for (IAtsActionableItem currAi : job.getRemoveAis()) {
            results.logf("Actionable Item [%s] will be removed from this workflow\n", currAi);
            AtsApiService.get().getActionableItemService().removeActionableItem(teamWf, currAi, changes);
            changes.add(teamWf);
         }
         if (!changes.isEmpty()) {
            changes.execute();
         }
      }

      public List<IAtsTeamWorkflow> getNewTeamWfs() {
         return newTeamWfs;
      }
   }

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               List<Artifact> artifacts = dropViewer.getArtifacts();
               if (artifacts.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must drag in a Team Workflow to duplicate.");
                  return;
               } else if (artifacts.size() != 1 || !(artifacts.iterator().next() instanceof IAtsTeamWorkflow)) {
                  AWorkbench.popup("ERROR", "Only one Team Workflow can be processed at a time");
               }
               XResultData data = new XResultData(false);
               TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) artifacts.iterator().next();
               performModification(teamWf, data, false);
               if (data.isErrors()) {
                  XResultDataUI.report(data, getName());
               }
               clearTables();
               refreshTables(teamWf);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   @Override
   public boolean showExecuteSection() {
      return false;
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets>"
         //
         + "<XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + getDropLabelStr() + "\" />" +
         //
         "</xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Add, remove or modify actionable items impacted in this Action.";
   }

   public void setDefaultTeamWorkflow(TeamWorkFlowArtifact team) {
      this.defaultTeamWorkflow = team;
   }

   @Override
   public String getName() {
      return "Modify Actionable Items";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public void execute(OperationLogger logger, VariableMap variableMap, IJobChangeListener jobChangeListener) {
      runOperation(null, null);
   }

   @Override
   public String getTabTitle() {
      return getName();
   }

   @Override
   public String getTitle() {
      return getName();
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.ACTIONABLE_ITEM);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.ACTIONABLE_ITEM);
   }

   public int getWorkflowTreeItemCount() {
      if (wfTree != null) {
         return wfTree.getViewer().getTree().getItemCount();
      }
      return 0;
   }

   public int getOtherTreeItemCount() {
      if (otherTree != null) {
         return otherTree.getViewer().getTree().getItemCount();
      }
      return 0;
   }

   public int getNewTreeItemCount() {
      return newTree.getViewer().getTree().getItemCount();
   }

   protected boolean displayWfTree() {
      return true;
   }

   protected boolean displayOtherTree() {
      return true;
   }

   protected boolean displayDuplicateButton() {
      return true;
   }

}