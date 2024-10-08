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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.Id;
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
   // Hard coded tasks to create regardless of change report contents.
   protected List<StaticTaskDefinition> staticTaskDefs = new ArrayList<StaticTaskDefinition>();
   // Additional options to specify change report items to use or ignore
   private ChangeReportOptions chgRptOptions;
   @JsonIgnore
   private IAtsTaskSetDefinitionHelper helper = IAtsTaskSetDefinitionHelper.emptyHelper();

   public CreateTasksDefinition() {
      super(Id.SENTINEL, "");
   }

   public CreateTasksDefinition(Long id, String name) {
      super(id, name);
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

   public List<StaticTaskDefinition> getStaticTaskDefs() {
      return staticTaskDefs;
   }

   public void setStaticTaskDefs(List<StaticTaskDefinition> staticTaskDefs) {
      this.staticTaskDefs = staticTaskDefs;
   }

   public IAtsTaskSetDefinitionHelper getHelper() {
      return helper;
   }

   public void andHelper(IAtsTaskSetDefinitionHelper helper) {
      this.helper = helper;
   }

}
