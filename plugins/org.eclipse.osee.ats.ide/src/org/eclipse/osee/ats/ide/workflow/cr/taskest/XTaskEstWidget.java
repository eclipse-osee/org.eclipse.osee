/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.taskest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstDefinition;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.task.TaskXViewer;
import org.eclipse.osee.ats.ide.workflow.task.mini.XMiniTaskWidget;
import org.eclipse.osee.ats.ide.world.WorldContentProvider;
import org.eclipse.osee.ats.ide.world.WorldLabelProvider;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Table showing siblings and roll-up for Team Workflow
 *
 * @author Donald G. Dunne
 */
public abstract class XTaskEstWidget extends XMiniTaskWidget {

   public static final String WIDGET_ID = XTaskEstWidget.class.getSimpleName();
   private static final String INFO_STRING =
      "Select to create estimating tasks.  Complete all estimates/tasks.  Double-Click to open/edit task/fields.";
   private AttributeTypeToken pointsAttrType;
   private final List<Object> input = new ArrayList<>();
   public static String NAME = "Estimate Manager";

   public XTaskEstWidget() {
      super(NAME, new XTaskEstXViewerFactory());
   }

   @Override
   public ToolBar createActionBar(Composite tableComp) {
      XTaskEstActionBar actionBar = new XTaskEstActionBar(this);
      ToolBar toolBar = actionBar.createTaskActionBar(tableComp);
      return toolBar;
   }

   @Override
   protected abstract TaskXViewer createXTaskViewer(Composite tableComp);

   @Override
   protected WorldLabelProvider getWorldLabelProvider(TaskXViewer xTaskViewer) {
      return new XTaskEstLabelProvider(xTaskViewer);
   }

   @Override
   protected WorldContentProvider getWorldContentProvider(TaskXViewer xTaskViewer) {
      return new XTaskEstContentProvider(xTaskViewer);
   }

   @Override
   public Artifact getArtifact() {
      return (Artifact) teamWf.getStoreObject();
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public Pair<Integer, String> getExtraInfoString() {
      return new Pair<Integer, String>(SWT.COLOR_BLACK, "Edit (most) fields here or double-click to open.");
   }

   public IAtsTeamWorkflow getTeamWf() {
      return teamWf;
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   public abstract Collection<TaskEstDefinition> getTaskEstDefs();

   public Collection<TaskEstDefinition> getTaskEstDefsFromTable() {
      List<TaskEstDefinition> teds = new ArrayList<>();
      for (Object obj : input) {
         if (obj instanceof TaskEstDefinition) {
            teds.add((TaskEstDefinition) obj);
         }
      }
      return teds;
   }

   public Collection<TaskEstDefinition> getTaskEstDefsFromTableChecked() {
      List<TaskEstDefinition> teds = new ArrayList<>();
      for (TaskEstDefinition ted : getTaskEstDefsFromTable()) {
         if (ted.isChecked()) {
            teds.add(ted);
         }
      }
      return teds;
   }

   /**
    * @return TaskEstDefinitions for missing tasks or IAtsTask for created tasks
    */
   private Collection<Object> getInput() {
      input.clear();
      Collection<TaskEstDefinition> taskDefs = getTaskEstDefs();
      Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks(teamWf);
      // For any task matching ted, remove ted from list and add task
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWf)) {
         for (TaskEstDefinition ted : getTaskEstDefs()) {
            if (task.hasTag(ted.getIdString())) {
               taskDefs.remove(ted);
               tasks.remove(task);
               input.add(task);
               break;
            }
         }
      }
      // Add remaining tasks and teds
      input.addAll(tasks);
      input.addAll(taskDefs);
      return input;
   }

   @Override
   public void loadTable() {
      try {
         if (xTaskViewer != null && xTaskViewer.getContentProvider() != null) {
            xTaskViewer.setInput(getInput());
            xTaskViewer.refresh();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void refresh() {
      if (xTaskViewer == null || xTaskViewer.getTree() == null || xTaskViewer.getTree().isDisposed()) {
         return;
      }
      super.refresh();

      float points = 0;
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWf)) {
         points +=
            Float.valueOf(atsApi.getAttributeResolver().getSoleAttributeValueAsString(task, getPointsAttrType(), "0"));
      }
      pointsLabel.setValueText(String.valueOf(points));
   }

   public AttributeTypeToken getPointsAttrType() {
      if (pointsAttrType == null) {
         IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeam(teamWf);
         if (agileTeam != null) {
            pointsAttrType = atsApi.getAgileService().getAgileTeamPointsAttributeType(agileTeam);
         }
         if (pointsAttrType == null) {
            pointsAttrType = AtsAttributeTypes.PointsNumeric;
         }
      }
      return pointsAttrType;
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
         for (IAtsTask task : atsApi.getTaskService().getTasks(teamWf)) {
            if (task.isInWork()) {
               inWork = true;
               returnStatus =
                  new Status(IStatus.ERROR, getClass().getSimpleName(), "All tasks must be completed/cancelled.");
               break;
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

}
