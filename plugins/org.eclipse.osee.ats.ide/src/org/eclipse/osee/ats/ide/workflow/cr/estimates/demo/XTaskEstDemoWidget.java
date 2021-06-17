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
package org.eclipse.osee.ats.ide.workflow.cr.estimates.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.workflow.cr.estimates.TaskEstDefinition;
import org.eclipse.osee.ats.ide.workflow.cr.estimates.XTaskEstWidget;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstDemoWidget extends XTaskEstWidget {
   public static final String WIDGET_ID = XTaskEstDemoWidget.class.getSimpleName();

   @Override
   public Collection<TaskEstDefinition> getTaskEstDefs() {
      List<TaskEstDefinition> taskDefs = new ArrayList<TaskEstDefinition>();
      taskDefs.add(new TaskEstDefinition(111L, "Integration Test", "Description", null));
      taskDefs.add(new TaskEstDefinition(112L, "Quality", "Description", null));
      getTaskDefsFromUserGroupsOff(DemoArtifactToken.SAW_PL_CR_TeamDef, taskDefs);
      return taskDefs;
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
