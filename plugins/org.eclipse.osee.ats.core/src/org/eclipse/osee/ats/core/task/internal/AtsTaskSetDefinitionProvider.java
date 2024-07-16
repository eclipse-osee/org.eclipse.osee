/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.task.internal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.task.create.CreateChangeReportTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.task.create.IAtsTaskSetDefinitionProvider;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.core.task.TaskSetDefinitionTokensDemo;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskSetDefinitionProvider implements IAtsTaskSetDefinitionProvider {

   @Override
   public Collection<CreateTasksDefinitionBuilder> getTaskSetDefinitions() {
      List<CreateTasksDefinitionBuilder> taskSets = new LinkedList<>();
      taskSets.add(new CreateTasksDefinitionBuilder(TaskSetDefinitionTokensDemo.SawSwDesignTestingChecklist) //
         .andEventType(RuleEventType.Manual) //
         .andStaticTask("1. Run unit tests", "desc", null) //
         .andStaticTask("2. Run integration testsk", "desc2", StateToken.Implement) //
         .andStaticTask("3. Run manual tests", DemoWorkDefinitions.WorkDef_Task_Demo_SwDesign) //
         .andStaticTask("4. Complete testing action")); //

      taskSets.add(new CreateTasksDefinitionBuilder(TaskSetDefinitionTokensDemo.SawSwDesignProcessChecklist) //
         .andEventType(RuleEventType.Manual) //
         .andStaticTask("1. Review processes", "desc", null) //
         .andStaticTask("2. Review work instruction", "desc2", StateToken.Implement) //
         .andStaticTask("3. Consult Mentor", DemoWorkDefinitions.WorkDef_Task_Demo_SwDesign) //
         .andStaticTask("4. Complete process action")); //

      taskSets.add(
         new CreateChangeReportTasksDefinitionBuilder(TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges) //
            .andEventType(RuleEventType.ChangeReportTasks) //
            .andChgRptBuilder() //
            .andFromSiblingTeam(DemoArtifactToken.SAW_Requirements) //
            .andToSiblingTeamAi(DemoArtifactToken.SAW_Test, DemoArtifactToken.SAW_Test_AI) //
            .andToSiblingTeamAi(DemoArtifactToken.SAW_Code, DemoArtifactToken.SAW_Code_AI) //
            .andArtifactType(CoreArtifactTypes.AbstractSoftwareRequirement) //
            .andNotArtifactType(CoreArtifactTypes.PlainText) //
            .andAttribute(CoreAttributeTypes.WordTemplateContent, CoreAttributeTypes.Name) //
            .andNotAttribute(CoreAttributeTypes.ParagraphNumber, CoreAttributeTypes.RelationOrder) //
            .andStaticTask("My Manual Task", "desc", null, AtsCoreUsers.UNASSIGNED_USER.getId()) //
            .andCopyAttributes(AtsAttributeTypes.ProposedResolution) //
      );

      return taskSets;
   }
}
