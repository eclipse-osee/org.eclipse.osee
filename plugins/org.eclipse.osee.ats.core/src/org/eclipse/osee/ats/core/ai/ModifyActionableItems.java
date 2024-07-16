/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.CreateTeamData;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.config.ITeamDefinitionUtility;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G Dunne
 */
public class ModifyActionableItems {

   private final XResultData results;
   private final IAtsTeamWorkflow teamWf;
   private final Collection<IAtsActionableItem> currAIsForAllWfs;
   private final Collection<IAtsActionableItem> currWorkflowDesiredAIs;
   private final Collection<IAtsActionableItem> newAIs;
   private final AtsUser modifiedBy;
   private final List<CreateTeamData> teamDatas = new ArrayList<>();
   private final List<IAtsActionableItem> addAis = new ArrayList<>();
   private final List<IAtsActionableItem> removeAis = new ArrayList<>();
   private final Map<IAtsTeamDefinition, CreateTeamData> teamDefToTeamDataMap = new HashMap<>();
   private final ITeamDefinitionUtility teamDefUtil;
   private final AtsApi atsApi;

   public ModifyActionableItems(XResultData results, IAtsTeamWorkflow teamWf, Collection<IAtsActionableItem> currAIsForAllWfs, //
      Collection<IAtsActionableItem> currWorkflowDesiredAIs, Collection<IAtsActionableItem> newAIs, AtsUser modifiedBy, //
      ITeamDefinitionUtility teamDefUtil, AtsApi atsApi) {
      this.results = results;
      this.teamWf = teamWf;
      this.currAIsForAllWfs = currAIsForAllWfs;
      this.currWorkflowDesiredAIs = currWorkflowDesiredAIs;
      this.newAIs = newAIs;
      this.modifiedBy = modifiedBy;
      this.teamDefUtil = teamDefUtil;
      this.atsApi = atsApi;
   }

   public void performModification() {
      Conditions.checkNotNull(results, "results");
      Conditions.checkNotNull(teamWf, "teamWf");
      Conditions.checkNotNull(modifiedBy, "modifiedBy");
      if (currWorkflowDesiredAIs != null) {
         // Determine if changes to this workflow's actionable items
         processAisAddedRemovedFromSelectedTeamWf();
      }
      // Determine what workflows to add
      processAisAddedForNewWorkflows();
   }

   private void processAisAddedForNewWorkflows() {
      if (newAIs.isEmpty()) {
         return;
      }
      Set<IAtsActionableItem> allAIsForNewWorkflow = new HashSet<>();
      Set<IAtsActionableItem> duplicatedAIs = new HashSet<>();
      // determine AIs that already have a team workflow associated
      for (IAtsActionableItem checkAi : newAIs) {
         if (!checkAi.isActionable()) {
            results.errorf("Actionable Item [%s] is not actionable; select item lower in hierarchy\n", checkAi);
         } else if (!checkAi.isAllowUserActionCreation()) {
            results.errorf("Actionable Item [%s] is not actionable by users; select another item\n", checkAi);
         } else {
            if (currAIsForAllWfs.contains(checkAi)) {
               duplicatedAIs.add(checkAi);
            }
            allAIsForNewWorkflow.add(checkAi);
         }
      }
      if (allAIsForNewWorkflow.isEmpty()) {
         results.errorf("No Actionable Items Selected");
         return;
      }
      Date createdDate = new Date();
      // process new and duplicated workflows
      for (IAtsActionableItem ai : allAIsForNewWorkflow) {
         IAtsTeamDefinition teamDef = getImpactedTeamDef(ai);
         if (teamDefToTeamDataMap.containsKey(teamDef)) {
            CreateTeamData createTeamData = teamDefToTeamDataMap.get(teamDef);
            createTeamData.getActionableItems().add(ai);
         } else {
            CreateTeamData createTeamData = new CreateTeamData(teamDef, Arrays.asList(ai),
               new LinkedList<>(atsApi.getTeamDefinitionService().getLeads(teamDef)), createdDate, modifiedBy,
               CreateTeamOption.Duplicate_If_Exists);
            teamDatas.add(createTeamData);
            teamDefToTeamDataMap.put(teamDef, createTeamData);
         }
      }
      // add messages to results
      for (CreateTeamData data : teamDatas) {
         results.log(String.format("Create New Team Workflow for Actionable Item(s) %s", data.getActionableItems()));
         for (IAtsActionableItem ai : data.getActionableItems()) {
            if (duplicatedAIs.contains(ai)) {
               results.logf("   - Note: Actionable Item [%s] is impacted by an existing Team Workflow\n", ai);
            }
         }
      }
   }

   /**
    * Return single Team Definition associated with this Actionable Item
    */
   private IAtsTeamDefinition getImpactedTeamDef(IAtsActionableItem ai) {
      IAtsTeamDefinition teamDef = null;
      Collection<IAtsTeamDefinition> impactedTeamDefs = teamDefUtil.getImpactedTeamDefs(Arrays.asList(ai));
      if (impactedTeamDefs.size() > 0) {
         teamDef = impactedTeamDefs.iterator().next();
      }
      return teamDef;
   }

   private void processAisAddedRemovedFromSelectedTeamWf() {
      Set<IAtsActionableItem> currAIs = teamWf.getActionableItems();
      if (currWorkflowDesiredAIs.isEmpty()) {
         results.error("All AIs can not be removed from a Team Workflow; Cancel workflow instead");
      } else {
         for (IAtsActionableItem checkedAi : currWorkflowDesiredAIs) {
            if (!currAIs.contains(checkedAi)) {
               results.logf("Add Actionable Item [%s] to the selected Team Workflow\n", checkedAi);
               addAis.add(checkedAi);
            }
         }
         for (IAtsActionableItem currAi : currAIs) {
            if (!currWorkflowDesiredAIs.contains(currAi)) {
               results.logf("Remove Actionable Item [%s] to the selected Team Workflow\n", currAi);
               removeAis.add(currAi);
            }
         }
      }
   }

   public List<CreateTeamData> getTeamDatas() {
      return teamDatas;
   }

   public List<IAtsActionableItem> getAddAis() {
      return addAis;
   }

   public List<IAtsActionableItem> getRemoveAis() {
      return removeAis;
   }

}
