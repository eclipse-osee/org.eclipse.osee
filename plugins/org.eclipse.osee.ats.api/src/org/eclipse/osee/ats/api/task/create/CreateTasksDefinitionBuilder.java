/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.create;

import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
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

   public CreateTasksDefinitionBuilder andTask(String title, String desc, StateToken relatedToState, Long... assigneeAccountId) {
      return andTask(title, desc, relatedToState, AtsWorkDefinitionTokens.WorkDef_Team_Default, assigneeAccountId);
   }

   public CreateTasksDefinitionBuilder andTask(String title, String desc, StateToken relatedToState, AtsWorkDefinitionToken workDef, Long... assigneeAccountId) {
      Conditions.assertNotNullOrEmpty(title, "title can not be empty");
      CreateTaskDefinition taskDef = new CreateTaskDefinition();
      taskDef.setTitle(title);
      if (Strings.isValid(desc)) {
         taskDef.setDescription(desc);
      }
      if (relatedToState != null && Strings.isValid(relatedToState.getName())) {
         taskDef.setRelatedToState(relatedToState.getName());
      }
      for (Long assigneAccountId : assigneeAccountId) {
         taskDef.getAssigneeAccountIds().add(assigneAccountId);
      }
      taskDef.setWorkDefId(workDef);
      createTasksDef.getTaskDefs().add(taskDef);
      return this;
   }

   public CreateTasksDefinitionBuilder andTask(String title) {
      return andTask(title, null, null, AtsWorkDefinitionTokens.WorkDef_Task_Default);
   }

   public CreateTasksDefinitionBuilder andTask(String title, AtsWorkDefinitionToken taskWorkDef) {
      return andTask(title, null, null, taskWorkDef);
   }

}
