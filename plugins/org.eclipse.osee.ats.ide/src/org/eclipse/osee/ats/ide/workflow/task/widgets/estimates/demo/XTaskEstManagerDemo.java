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
package org.eclipse.osee.ats.ide.workflow.task.widgets.estimates.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.ide.workflow.task.widgets.estimates.TaskEstDefinition;
import org.eclipse.osee.ats.ide.workflow.task.widgets.estimates.XTaskEstManager;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstManagerDemo extends XTaskEstManager {

   public static final String WIDGET_ID = XTaskEstManagerDemo.class.getSimpleName();

   @Override
   public Collection<TaskEstDefinition> getTaskDefs() {
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

}
