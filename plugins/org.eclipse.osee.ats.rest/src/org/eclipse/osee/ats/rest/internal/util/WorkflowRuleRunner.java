/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IExecutableRule;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workdef.RunRuleResults;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Mark Joy
 */
public class WorkflowRuleRunner {

   private final RuleEventType eventType;
   private final AtsApi atsApi;
   private final List<IAtsWorkItem> workflowsCreated;
   private final RunRuleResults ruleResults;

   public WorkflowRuleRunner(RuleEventType eventType, List<IAtsWorkItem> workflowsCreated, AtsApi atsApi) {
      this(eventType, workflowsCreated, atsApi, new RunRuleResults());
   }

   public WorkflowRuleRunner(RuleEventType eventType, List<IAtsWorkItem> workflowsCreated, AtsApi atsApi, RunRuleResults ruleResults) {
      this.eventType = eventType;
      this.workflowsCreated = workflowsCreated;
      this.atsApi = atsApi;
      this.ruleResults = ruleResults;
   }

   public RunRuleResults run() {
      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet("ATS Rule Runner", AtsCoreUsers.SYSTEM_USER);
      if (eventType == RuleEventType.CreateWorkflow) {
         for (IAtsWorkItem workItem : workflowsCreated) {
            if (workItem.isTeamWorkflow()) {

               try {
                  // check team definition
                  if (workItem.getParentTeamWorkflow() != null && workItem.getParentTeamWorkflow().getTeamDefinition() != null) {
                     for (String teamDefRule : workItem.getParentTeamWorkflow().getTeamDefinition().getRules()) {
                        IAtsRuleDefinition ruleDefinition =
                           atsApi.getWorkDefinitionService().getRuleDefinition(teamDefRule);

                        if (ruleDefinition != null && ruleDefinition.getRuleEvents().contains(
                           eventType) && ruleDefinition instanceof IExecutableRule) {
                           ((IExecutableRule) ruleDefinition).execute(workItem, atsApi, changes, ruleResults);
                        }
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(WorkflowRuleRunner.class, Level.SEVERE, ex);
               }

               try {
                  // check actionable items
                  if (workItem.getParentTeamWorkflow() != null && workItem.getParentTeamWorkflow().getTeamDefinition() != null) {
                     for (IAtsActionableItem ai : workItem.getParentTeamWorkflow().getActionableItems()) {
                        for (String aiRule : ai.getRules()) {
                           IAtsRuleDefinition ruleDefinition =
                              atsApi.getWorkDefinitionService().getRuleDefinition(aiRule);

                           if (ruleDefinition != null && ruleDefinition.getRuleEvents().contains(
                              eventType) && ruleDefinition instanceof IExecutableRule) {
                              ((IExecutableRule) ruleDefinition).execute(workItem, atsApi, changes, ruleResults);
                           }
                        }
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(WorkflowRuleRunner.class, Level.SEVERE, ex);
               }

               // check state definition
               try {
                  IAtsWorkDefinition workDef = workItem.getWorkDefinition();
                  if (workDef != null) {
                     IAtsStateDefinition stateDef =
                        workDef.getStateByName(workItem.getStateMgr().getCurrentStateName());
                     for (String teamDefRule : stateDef.getRules()) {
                        IAtsRuleDefinition ruleDefinition =
                           atsApi.getWorkDefinitionService().getRuleDefinition(teamDefRule);
                        if (ruleDefinition.getRuleEvents().contains(
                           eventType) && ruleDefinition instanceof IExecutableRule) {
                           ((IExecutableRule) ruleDefinition).execute(workItem, atsApi, changes, ruleResults);
                        }
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(WorkflowRuleRunner.class, Level.SEVERE, ex);
               }
            }
         }
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }
      return ruleResults;
   }

   public RunRuleResults getRuleResults() {
      return ruleResults;
   }

}
