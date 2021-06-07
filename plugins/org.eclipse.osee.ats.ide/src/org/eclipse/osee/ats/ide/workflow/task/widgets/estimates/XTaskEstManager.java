/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task.widgets.estimates;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstUtil;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValue;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Donald G. Dunne
 */
public abstract class XTaskEstManager extends GenericXWidget implements ArtifactWidget, IBranchEventListener, IArtifactEventListener {

   private static final String INFO_STRING =
      "Select to create estimating tasks.  Complete all estimates/tasks.  Double-Click to open/edit task/fields.";
   private TaskEstXViewer xTaskEstViewer;
   public final static String normalColor = "#EEEEEE";
   private TeamWorkFlowArtifact teamArt;
   private static final int paddedTableHeightHint = 2;
   private Label extraInfoLabel;
   public static final String WIDGET_NAME = "XTaskEstManager";
   public static final String NAME = "Estimate Manager";
   public static final String DESCRIPTION = "Generate and manage estimating tasks.";
   private int lastSize = 0;
   private final int MAX_TABLE_SIZE = 10;
   private Composite mainComp;
   private Composite parentComp;
   private final AtsApi atsApi;
   private Collection<TaskEstDefinition> taskDefs;
   private XLabelValue pointsLabel;

   public XTaskEstManager() {
      super(NAME);
      OseeEventManager.addListener(this);
      atsApi = AtsApiService.get();
   }

   abstract public Collection<TaskEstDefinition> getTaskDefs();

   public Collection<TaskEstDefinition> getItems() {
      return taskDefs;
   }

   /**
    * @return String.format for related task name
    */
   public String getTaskNameFormat() {
      return "Estimate for %s";
   }

   @Override
   public TeamWorkFlowArtifact getArtifact() {
      return teamArt;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      // parentComp needs to be created and remain intact; mainComp will be disposed and re-created as necessary
      parentComp = new Composite(parent, SWT.FLAT);
      parentComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      parentComp.setLayout(ALayout.getZeroMarginLayout());

      redrawComposite();
   }

   private void redrawComposite() {
      if (parentComp == null || !Widgets.isAccessible(parentComp)) {
         return;
      }
      if (mainComp != null && Widgets.isAccessible(mainComp)) {
         mainComp.dispose();
         xTaskEstViewer = null;
      }
      mainComp = new Composite(parentComp, SWT.FLAT);
      mainComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      mainComp.setLayout(new GridLayout(1, true));
      if (toolkit != null) {
         toolkit.paintBordersFor(mainComp);
      }

      labelWidget = new Label(mainComp, SWT.NONE);
      labelWidget.setText(getLabel() + ":");
      if (getToolTip() != null) {
         labelWidget.setToolTipText(getToolTip());
      }

      try {
         Composite tableComp = new Composite(mainComp, SWT.BORDER);
         tableComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         tableComp.setLayout(ALayout.getZeroMarginLayout());
         if (toolkit != null) {
            toolkit.paintBordersFor(tableComp);
         }

         TaskEstActionBar actionBar = new TaskEstActionBar(this);
         ToolBar toolBar = actionBar.createTaskActionBar(tableComp);

         xTaskEstViewer = new TaskEstXViewer(tableComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this, teamArt,
            AtsApiService.get());
         xTaskEstViewer.getTree().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         xTaskEstViewer.setContentProvider(new TaskEstContentProvider());
         xTaskEstViewer.setLabelProvider(new TaskEstLabelProvider(xTaskEstViewer));

         new ActionContributionItem(getXViewer().getCustomizeAction()).fill(toolBar, -1);

         if (toolkit != null && xTaskEstViewer.getStatusLabel() != null) {
            toolkit.adapt(xTaskEstViewer.getStatusLabel(), false, false);
         }

         pointsLabel = new XLabelValue("Total Estimated Points", "0");
         pointsLabel.createWidgets(tableComp, 2);
         pointsLabel.getComp().setLayout(new GridLayout(2, false));
         pointsLabel.getLabelWidget().setFont(FontManager.getCourierNew12Bold());
         if (toolkit != null) {
            toolkit.adapt(pointsLabel.getLabelWidget(), false, false);
            toolkit.adapt(pointsLabel.getValueTextWidget(), false, false);
         }

         refresh();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      // reset bold for label
      WorkflowEditor.setLabelFonts(labelWidget, FontManager.getDefaultLabelFont());

      parentComp.layout();
   }

   public void setXviewerTreeSize() {
      Tree tree = xTaskEstViewer.getTree();
      int size = xTaskEstViewer.getTree().getItemCount();
      if (size > MAX_TABLE_SIZE) {
         size = MAX_TABLE_SIZE;
      }
      if (size == lastSize) {
         return;
      }
      lastSize = size;
      int treeItemHeight = xTaskEstViewer.getTree().getItemHeight();
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.heightHint = treeItemHeight * (paddedTableHeightHint + size);
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
   }

   public void loadTable() {
      try {
         if (xTaskEstViewer != null && teamArt != null && xTaskEstViewer.getContentProvider() != null) {
            taskDefs = loadTaskDefs();
            xTaskEstViewer.setInput(taskDefs);
            xTaskEstViewer.refresh();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * @return TaskEstDefinitions matched with existing tasks
    */
   private Collection<TaskEstDefinition> loadTaskDefs() {
      Collection<TaskEstDefinition> taskDefs = getTaskDefs();
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamArt)) {
         boolean found = false;
         for (TaskEstDefinition ted : taskDefs) {
            if (task.hasTag(ted.getIdString())) {
               ted.setTask(task);
               found = true;
               break;
            }
         }
         // Add TED for manually created tasks
         if (!found && task.getWorkDefinition().getId().equals(getTaskWorkDefTok().getId())) {
            TaskEstDefinition ted = new TaskEstDefinition(task.getId(), task.getName(), null, null);
            ted.setTask(task);
            ted.setManual(true);
            taskDefs.add(ted);
         }
      }
      return taskDefs;
   }

   @Override
   public Control getControl() {
      if (xTaskEstViewer == null) {
         return null;
      }
      return xTaskEstViewer.getTree();
   }

   @Override
   public void dispose() {
      if (xTaskEstViewer != null) {
         xTaskEstViewer.dispose();
      }
      OseeEventManager.removeListener(this);
   }

   @Override
   public void refresh() {
      if (xTaskEstViewer == null || xTaskEstViewer.getTree() == null || xTaskEstViewer.getTree().isDisposed()) {
         return;
      }
      loadTable();
      setXviewerTreeSize();

      float points = 0;
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamArt)) {
         points += Float.valueOf(
            atsApi.getAttributeResolver().getSoleAttributeValueAsString(task, xTaskEstViewer.getPointsAttrType(), "0"));
      }
      pointsLabel.setValueText(String.valueOf(points));
   }

   private void updateExtraInfoLabel(final int color, final String infoStr) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(extraInfoLabel)) {
               String currentString = extraInfoLabel.getText();
               if (infoStr == null && currentString != null || //
               infoStr != null && currentString == null || //
               infoStr != null && currentString != null && !infoStr.equals(currentString)) {
                  extraInfoLabel.setText(INFO_STRING);
               }
               extraInfoLabel.setForeground(Displays.getSystemColor(color));
            }
         }
      });
   }

   @Override
   public IStatus isValid() {
      Status returnStatus = new Status(IStatus.OK, getClass().getSimpleName(), "");
      try {
         int backgroundColor = SWT.COLOR_BLACK;
         boolean inWork = false;
         for (TaskEstDefinition ted : getItems()) {
            if (ted.hasTask()) {
               IAtsTask task = ted.getTask();
               if (task.isInWork()) {
                  inWork = true;
                  returnStatus =
                     new Status(IStatus.ERROR, getClass().getSimpleName(), "All tasks must be completed/cancelled.");
                  break;
               }
            }
         }
         if (inWork) {
            backgroundColor = SWT.COLOR_RED;
         }
         updateExtraInfoLabel(backgroundColor, INFO_STRING);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Status(IStatus.ERROR, getClass().getSimpleName(), ex.getLocalizedMessage());
      }
      return returnStatus;
   }

   public TaskEstXViewer getXViewer() {
      return xTaskEstViewer;
   }

   @Override
   public Object getData() {
      return xTaskEstViewer.getInput();
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (!artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         throw new OseeStateException("Must be TeamWorkflowArtifact, set was a [%s]", artifact.getArtifactTypeName());
      }
      this.teamArt = (TeamWorkFlowArtifact) artifact;
      loadTable();
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   /**
    * @return the artifact
    */
   public TeamWorkFlowArtifact getTeamArt() {
      return teamArt;
   }

   @Override
   public Control getErrorMessageControl() {
      return labelWidget;
   }

   @Override
   public String toString() {
      return String.format("%s", getLabel());
   }

   @Override
   public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            redrawComposite();
         }
      });
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (getTeamArt() != null && artifactEvent.isModified(getTeamArt())) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               loadTable();
            }
         });
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public boolean isEmpty() {
      return xTaskEstViewer.getXCommitViewer().getXViewer().getTree().getItemCount() == 0;
   }

   public void setExtraInfoLabel(Label extraInfoLabel) {
      this.extraInfoLabel = extraInfoLabel;
   }

   abstract public AtsWorkDefinitionToken getTaskWorkDefTok();

   /**
    * Create dynamic TEDs from children UserGroups off given teamDef where UserGroup has TaskEst static id
    */
   protected void getTaskDefsFromUserGroupsOff(IAtsTeamDefinitionArtifactToken teamDef, List<TaskEstDefinition> taskDefs) {
      for (ArtifactToken childArt : atsApi.getRelationResolver().getChildren(teamDef)) {
         if (atsApi.getAttributeResolver().getAttributesToStringList(childArt, CoreAttributeTypes.StaticId).contains(
            TaskEstUtil.TASK_EST_STATIC_ID)) {
            String desc = atsApi.getAttributeResolver().getSoleAttributeValueAsString(childArt,
               CoreAttributeTypes.Description, "");
            List<ArtifactId> assigneeAccountIds = new LinkedList<>();
            for (UserToken user : atsApi.getUserGroupService().getUserGroup(childArt).getMembers()) {
               assigneeAccountIds.add(ArtifactId.valueOf(user.getId()));
            }
            taskDefs.add(new TaskEstDefinition(childArt.getId(), childArt.getName(), desc, assigneeAccountIds));
         }
      }
   }

}
