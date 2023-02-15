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
package org.eclipse.osee.ats.ide.workflow.task.widgets;

import java.util.Date;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxData;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxes;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Check Boxes with button to generate/update tasks based on what's checked
 *
 * @author Donald G. Dunne
 */
public abstract class XCheckBoxesWithTaskGen extends XCheckBoxes implements ArtifactWidget {

   private IAtsTeamWorkflow teamWf;
   private final AtsWorkDefinitionToken taskWorkDef;
   private final AtsApi atsApi;
   private List<XCheckBoxesWithTaskGenData> checkBoxesWithTasks;

   public XCheckBoxesWithTaskGen(String displayLabel, AtsWorkDefinitionToken taskWorkDef, int numColumns) {
      super(displayLabel, numColumns);
      this.taskWorkDef = taskWorkDef;
      atsApi = AtsApiService.get();
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      Composite buttonComp = new Composite(composite, SWT.NONE);
      GridLayout layout = new GridLayout(3, true);
      buttonComp.setLayout(layout);
      buttonComp.setLayoutData(new GridData());

      Button updateButton = new Button(buttonComp, SWT.PUSH);
      updateButton.setText("Generate Tasks from Checked");
      updateButton.setToolTipText("Generate tasks for any checked items that do not already have tasks.");
      updateButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleTaskGen();
         }

      });

      Button addTasksButton = new Button(buttonComp, SWT.PUSH);
      addTasksButton.setText("Add Manual Task(s)");
      addTasksButton.setToolTipText("Create manual tasks using same naming and task workflow definition.");
      addTasksButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleManualTask();
         }

      });

      refreshEnablement();

   }

   private void refreshEnablement() {
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWf)) {
         for (XCheckBoxesWithTaskGenData cbTaskData : getLoadedCheckboxeWithTaskGenData()) {
            if (task.getTags().contains(cbTaskData.getId().toString())) {
               cbTaskData.setTask(task);
               cbTaskData.getCheckBox().getCheckButton().setEnabled(false);
               cbTaskData.getCheckBox().getCheckButton().setSelection(false);
               String boxLabel = getTaskLabel(cbTaskData);
               cbTaskData.getCheckBox().setLabel(boxLabel);
               break;
            }
         }
      }
   }

   @Override
   protected String getTaskLabel(XCheckBoxData cbd) {
      XCheckBoxesWithTaskGenData cbdTask = (XCheckBoxesWithTaskGenData) cbd;
      String name = cbd.getLabel();
      IAtsTask task = cbdTask.getTask();
      String state = "(No Task)";
      if (task != null) {
         state = " (" + task.getCurrentStateName() + ")";
      }
      name = String.format("%s %s", name, state);
      return name;
   }

   protected void handleTaskGen() {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getLabel(), getLabel() + "\n\nAre you sure?")) {
         return;
      }
      NewTaskSet newTaskSet = NewTaskSet.create(getLabel(), atsApi.getUserService().getCurrentUserId());
      NewTaskData newTaskData = NewTaskData.create(newTaskSet, teamWf);
      newTaskData.setTaskWorkDef(taskWorkDef);
      for (XCheckBoxesWithTaskGenData cbTaskData : getLoadedCheckboxeWithTaskGenData()) {
         if (cbTaskData.isChecked() && cbTaskData.getTask() == null) {
            JaxAtsTask task = new JaxAtsTask();
            String name = String.format(getTaskNameFormat(), cbTaskData.getLabel());
            task.setName(name);
            task.addAttribute(CoreAttributeTypes.StaticId, cbTaskData.getId());
            task.setCreatedByUserId(atsApi.getUserService().getCurrentUserId());
            task.setCreatedDate(new Date());
            newTaskData.add(task);
         }
      }
      if (newTaskData.getTasks().isEmpty()) {
         AWorkbench.popup("No New Tasks To Create");
      } else {
         newTaskSet = atsApi.getTaskService().createTasks(newTaskSet);
         if (newTaskSet.isErrors() || newTaskSet.getTaskData().isEmpty()) {
            XResultDataUI.report(newTaskSet.getResults(), getLabel());
         } else {
            AWorkbench.popup("New Tasks Created");
         }
      }
   }

   protected void handleManualTask() {
      CreateTasksFromSimpleList operation = new CreateTasksFromSimpleList(teamWf, taskWorkDef);
      operation.setTeamWf(teamWf);
      BlamEditor.edit(operation);
   }

   @Override
   protected List<XCheckBoxData> getCheckBoxes() {
      return Collections.castAll(getCheckBoxeWithTaskDatas());
   }

   abstract protected List<XCheckBoxesWithTaskGenData> getCheckBoxeWithTaskDatas();

   public List<XCheckBoxesWithTaskGenData> getLoadedCheckboxeWithTaskGenData() {
      if (checkBoxesWithTasks == null) {
         checkBoxesWithTasks = getCheckBoxeWithTaskDatas();
      }
      return checkBoxesWithTasks;
   }

   // Need to override so abstract can use base checkbox data
   @Override
   public List<XCheckBoxData> getLoadedCheckboxes() {
      return Collections.castAll(getLoadedCheckboxeWithTaskGenData());
   }

   @Override
   public Artifact getArtifact() {
      return (Artifact) teamWf.getStoreObject();
   }

   @Override
   public void saveToArtifact() {
      // do nothing
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
   public void setArtifact(Artifact artifact) {
      if (artifact instanceof IAtsTeamWorkflow) {
         teamWf = (IAtsTeamWorkflow) artifact;
      }
   }

   protected String getTaskNameFormat() {
      return "%s";
   }

   @Override
   public void refresh() {
      refreshEnablement();
   }

}
