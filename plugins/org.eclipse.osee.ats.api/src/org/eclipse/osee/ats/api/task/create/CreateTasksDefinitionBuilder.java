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

package org.eclipse.osee.ats.api.task.create;

import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CreateTasksDefinitionBuilder extends NamedIdBase {

   protected CreateTasksDefinition createTasksDef;

   public CreateTasksDefinitionBuilder(Long id, String name) {
      super(id, name);
      createTasksDef = new CreateTasksDefinition(id, name);
   }

   public CreateTasksDefinition getCreateTasksDef() {
      return createTasksDef;
   }

   public CreateTasksDefinitionBuilder(AtsTaskDefToken taskSetToken) {
      this(taskSetToken.getId(), taskSetToken.getName());
   }

   public CreateTasksDefinitionBuilder andTransitionTo(StateToken toState) {
      createTasksDef.setRuleEvent(RuleEventType.TransitionTo);
      createTasksDef.setToState(toState.getName());
      return this;
   }

   public CreateTasksDefinitionBuilder andEventType(RuleEventType ruleEventType) {
      Conditions.assertFalse(ruleEventType == RuleEventType.TransitionTo,
         "Invalid event type TransitionTo; use andTransitionTo()");
      createTasksDef.setRuleEvent(ruleEventType);
      return this;
   }

   public CreateTasksDefinitionBuilder andStaticTask(String title, String desc, StateToken relatedToState, Long... assigneeAccountId) {
      return andStaticTask(title, desc, relatedToState, AtsWorkDefinitionTokens.WorkDef_Team_Default,
         assigneeAccountId);
   }

   public CreateTasksDefinitionBuilder andStaticTask(String name, String desc, StateToken relatedToState, AtsWorkDefinitionToken workDef, Long... assigneeAccountId) {
      Conditions.assertNotNullOrEmpty(name, "title can not be empty");
      StaticTaskDefinition taskDef = new StaticTaskDefinition();
      taskDef.setName(name);
      if (Strings.isValid(desc)) {
         taskDef.setDescription(desc);
      }
      if (relatedToState != null && Strings.isValid(relatedToState.getName())) {
         taskDef.setRelatedToState(relatedToState.getName());
      }
      for (Long assigneAccountId : assigneeAccountId) {
         taskDef.getAssigneeAccountIds().add(assigneAccountId);
      }
      taskDef.setWorkDefTok(workDef);
      createTasksDef.getStaticTaskDefs().add(taskDef);
      return this;
   }

   public CreateTasksDefinitionBuilder andStaticTask(String name, String desc) {
      return andStaticTask(name, desc, null, AtsWorkDefinitionTokens.WorkDef_Task_Default);
   }

   public CreateTasksDefinitionBuilder andStaticTask(String name) {
      return andStaticTask(name, null, null, AtsWorkDefinitionTokens.WorkDef_Task_Default);
   }

   public CreateTasksDefinitionBuilder andStaticTask(String title, AtsWorkDefinitionToken taskWorkDef) {
      return andStaticTask(title, null, null, taskWorkDef);
   }

   public CreateChangeReportTasksDefinitionBuilder andChgRptBuilder() {
      if (this instanceof CreateChangeReportTasksDefinitionBuilder) {
         return (CreateChangeReportTasksDefinitionBuilder) this;
      }
      throw new OseeArgumentException("andChgReportBuilder only valid for CreateChangeReportTasksDefinitionBuilder");
   }

   public ChangeReportOptions getChgRptOptions() {
      return createTasksDef.getChgRptOptions();
   }

   public void setChgRptOptions(ChangeReportOptions chgRptOptions) {
      createTasksDef.setChgRptOptions(chgRptOptions);
   }

   public CreateTasksDefinitionBuilder andCopyAttributes(AttributeTypeToken... copyAttrTypes) {
      for (AttributeTypeToken copyAttrType : copyAttrTypes) {
         createTasksDef.getChgRptOptions().getCopyAttrTypes().add(copyAttrType);
      }
      return this;
   }

}
