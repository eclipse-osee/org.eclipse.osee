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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CreateTasksDefinition extends NamedIdBase {

   protected ArtifactId asUser;
   protected String comment;
   protected RuleEventType ruleEvent;
   private String toState;
   protected List<CreateTaskDefinition> tasksDef = new ArrayList<CreateTaskDefinition>();
   private ChangeReportOptions chgRptOptions;

   public CreateTasksDefinition(Long id, String name) {
      super(id, name);
   }

   public List<CreateTaskDefinition> getTaskDefs() {
      return tasksDef;
   }

   public void setTaskDefs(List<CreateTaskDefinition> taskDefs) {
      this.tasksDef = taskDefs;
   }

   public ArtifactId getAsUser() {
      return asUser;
   }

   public void setAsUser(ArtifactId asUser) {
      this.asUser = asUser;
   }

   public String getComment() {
      if (Strings.isInValid(comment)) {
         return String.format("Create task from Task Set [%s] and rule [%s]", getName(), ruleEvent.name());
      }
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public RuleEventType getRuleEvent() {
      return ruleEvent;
   }

   public void setRuleEvent(RuleEventType ruleEvent) {
      this.ruleEvent = ruleEvent;
   }

   public String getToState() {
      return toState;
   }

   public void setToState(String toState) {
      this.toState = toState;
   }

   public ChangeReportOptions getChgRptOptions() {
      if (chgRptOptions == null) {
         chgRptOptions = new ChangeReportOptions();
      }
      return chgRptOptions;
   }

   public void setChgRptOptions(ChangeReportOptions chgRptOptions) {
      this.chgRptOptions = chgRptOptions;
   }

}
