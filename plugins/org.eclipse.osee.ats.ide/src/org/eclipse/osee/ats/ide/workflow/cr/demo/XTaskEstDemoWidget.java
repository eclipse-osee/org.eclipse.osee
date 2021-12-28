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
package org.eclipse.osee.ats.ide.workflow.cr.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstDefinition;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstUtil;
import org.eclipse.osee.ats.ide.workflow.cr.taskest.XTaskEstWidget;
import org.eclipse.osee.ats.ide.workflow.cr.taskest.XTaskEstXViewerFactory;
import org.eclipse.osee.ats.ide.workflow.task.TaskXViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstDemoWidget extends XTaskEstWidget {

   private XTaskEstDemoXViewer xTaskEstDemoViewer;

   @Override
   public Collection<TaskEstDefinition> getTaskEstDefs() {
      List<TaskEstDefinition> taskDefs = new ArrayList<TaskEstDefinition>();
      taskDefs.add(
         new TaskEstDefinition(111L, "Integration Test", "Description", null, DemoArtifactToken.SAW_PL_Test_AI));
      taskDefs.add(new TaskEstDefinition(112L, "Quality", "Description", null, null));
      TaskEstUtil.getTaskDefsFromUserGroupsOff(DemoArtifactToken.SAW_PL_CR_TeamDef, taskDefs, atsApi);
      return taskDefs;
   }

   @Override
   protected TaskXViewer createXTaskViewer(Composite tableComp) {
      xTaskEstDemoViewer = new XTaskEstDemoXViewer(tableComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION,
         new XTaskEstXViewerFactory(), null, teamWf);
      xTaskViewer = xTaskEstDemoViewer;
      return xTaskViewer;
   }

   @Override
   public AtsWorkDefinitionToken getTaskWorkDefTok() {
      return DemoWorkDefinitions.WorkDef_Task_Demo_For_CR_Estimating;
   }

   @Override
   public Collection<IAtsTask> getTasks() {
      return atsApi.getTaskService().getTasks(teamWf);
   }

}
