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
package org.eclipse.osee.ats.ide.workflow.cr.estimates;

import java.util.Arrays;
import java.util.Date;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstDefinition;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstUtil;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstActionBar implements TaskEstNameProvider {

   private final XTaskEstWidget xTaskEstWidget;
   private Label extraInfoLabel;
   private ToolItem refreshItem;
   private final AtsApi atsApi;
   private final IAtsTeamWorkflow teamWf;

   public XTaskEstActionBar(XTaskEstWidget xTaskEstWidget) {
      this.xTaskEstWidget = xTaskEstWidget;
      atsApi = AtsApiService.get();
      this.teamWf = xTaskEstWidget.getTeamWf();
   }

   public ToolBar createTaskActionBar(Composite parent) {

      Composite bComp = new Composite(parent, SWT.NONE);
      bComp.setLayout(new GridLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite leftComp = new Composite(bComp, SWT.NONE);
      leftComp.setLayout(new GridLayout());
      leftComp.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(leftComp, SWT.NONE);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("");
      extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      xTaskEstWidget.setExtraInfoLabel(extraInfoLabel);

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);

      ToolItem addCannedItem = new ToolItem(toolBar, SWT.PUSH);
      addCannedItem.setText("Create Checked Task(s)");
      addCannedItem.setImage(ImageManager.getImage(FrameworkImage.GREEN_PLUS));
      addCannedItem.setToolTipText("Create Checked Estimating Task(s)");
      addCannedItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            createCannedTasks();
         }

      });

      ToolItem addManualItem = new ToolItem(toolBar, SWT.PUSH);
      addManualItem.setText("Create Manual Task(s)");
      addManualItem.setImage(ImageManager.getImage(AtsImage.NEW_TASK));
      addManualItem.setToolTipText("Create Manual Estimating Task(s) from entered list");
      addManualItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            createManualTasks();
         }

      });

      new ToolItem(toolBar, SWT.SEPARATOR);

      refreshItem = new ToolItem(toolBar, SWT.PUSH);
      refreshItem.setImage(ImageManager.getImage(PluginUiImage.REFRESH));
      refreshItem.setToolTipText("Refresh");
      refreshItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            xTaskEstWidget.loadTable();
         }
      });

      new ToolItem(toolBar, SWT.SEPARATOR);

      return toolBar;
   }

   protected void createManualTasks() {
      EntryDialog dialog = new EntryDialog("Create Manual Estimating Task(s)", "Enter task names one per line.");
      dialog.setFillVertically(true);
      dialog.setAddOpenInEditorOption(false);
      if (dialog.open() != Window.OK) {
         return;
      }
      NewTaskSet newTaskSet = NewTaskSet.create("Create Task(s)", atsApi.getUserService().getCurrentUserId());
      NewTaskData newTaskData = NewTaskData.create(newTaskSet, teamWf);
      for (String name : dialog.getEntry().split("\r\n")) {
         if (Strings.isValid(name)) {
            newTaskData.setTaskWorkDef(xTaskEstWidget.getTaskWorkDefTok());
            JaxAtsTask task = new JaxAtsTask();
            task.setName(name);
            task.setCreatedByUserId(atsApi.getUserService().getCurrentUserId());
            task.setCreatedDate(new Date());
            task.addAttributes(CoreAttributeTypes.StaticId,
               Arrays.asList(TaskEstUtil.TASK_EST_MANUAL, TaskEstUtil.TASK_EST_STATIC_ID));
            task.addRelation(AtsRelationTypes.Derive_From, teamWf.getId());
            newTaskData.add(task);
         }
      }
      if (newTaskData.getTasks().isEmpty()) {
         AWorkbench.popup("No Tasks Entered");
      } else {
         newTaskSet = atsApi.getTaskService().createTasks(newTaskSet);
         if (newTaskSet.isErrors() || newTaskSet.getTaskData().isEmpty()) {
            XResultDataUI.report(newTaskSet.getResults(), XTaskEstWidget.NAME);
         } else {
            AWorkbench.popup("New Tasks Created");
         }
      }
   }

   protected void createCannedTasks() {
      if (xTaskEstWidget.getTaskEstDefsFromTableChecked().isEmpty()) {
         AWorkbench.popup("No Tasks Selected");
         return;
      }
      TaskEstOperations ops = new TaskEstOperations(atsApi, xTaskEstWidget.getTaskWorkDefTok(), this);
      NewTaskSet newTaskSet = ops.createCannedTasks(teamWf, xTaskEstWidget.getTaskEstDefsFromTableChecked());

      if (newTaskSet.isErrors() || newTaskSet.getTaskData().isEmpty()) {
         XResultDataUI.report(newTaskSet.getResults(), XTaskEstWidget.NAME);
      } else {
         AWorkbench.popup("New Tasks Created");
      }
   }

   @Override
   public String getTaskName(TaskEstDefinition ted) {
      return String.format(xTaskEstWidget.getTaskNameFormat(), ted.getName());
   }

}
