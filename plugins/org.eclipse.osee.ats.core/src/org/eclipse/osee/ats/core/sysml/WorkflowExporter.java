/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.core.sysml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.sysml.OseeRelationToSysmlMapper;
import org.eclipse.osee.ats.api.sysml.SysmlConnection;
import org.eclipse.osee.ats.api.sysml.SysmlConstants;
import org.eclipse.osee.ats.api.sysml.SysmlPackage;
import org.eclipse.osee.ats.api.sysml.SysmlPartDef;
import org.eclipse.osee.ats.api.sysml.SysmlPartUsage;
import org.eclipse.osee.ats.api.sysml.SysmlStateMachine;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Exports ATS workflow objects (Actions, Team Workflows, Tasks, Reviews, BIT) for a given program to a SysmlPackage
 * model for serialization to SysML V2 textual notation.
 *
 * @author Donald G. Dunne
 */
public class WorkflowExporter {

   private final AtsApi atsApi;

   public WorkflowExporter(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   /**
    * Exports all workflow objects for the given program into a SysmlPackage.
    */
   public SysmlPackage export(ArtifactId programId) {
      IAtsProgram program = atsApi.getProgramService().getProgramById(programId);
      if (program == null) {
         throw new OseeArgumentException("Program not found for id [%s]", programId);
      }
      String packageName = sanitizeName(program.getName()) + "_Workflows";
      SysmlPackage pkg = new SysmlPackage(packageName);

      // Import config schema
      pkg.addImport(sanitizeName(program.getName()) + "_Config::*");

      // Query all team workflows for this program's teams
      Collection<IAtsTeamDefinition> teamDefs = atsApi.getProgramService().getTeamDefs(program);
      List<IAtsTeamWorkflow> allTeamWfs = new ArrayList<>();
      for (IAtsTeamDefinition teamDef : teamDefs) {
         Collection<IAtsWorkItem> items =
            atsApi.getQueryService().createQuery(WorkItemType.TeamWorkflow).andTeam(teamDef).getItems();
         for (IAtsWorkItem item : items) {
            if (item instanceof IAtsTeamWorkflow) {
               allTeamWfs.add((IAtsTeamWorkflow) item);
            }
         }
      }

      // Collect unique actions
      Set<IAtsAction> allActions = new HashSet<>();
      for (IAtsTeamWorkflow teamWf : allTeamWfs) {
         IAtsAction action = teamWf.getParentAction();
         if (action != null) {
            allActions.add(action);
         }
      }

      // Collect unique work definitions for state machines
      Set<WorkDefinition> workDefs = new HashSet<>();
      for (IAtsTeamWorkflow teamWf : allTeamWfs) {
         WorkDefinition wd = teamWf.getWorkDefinition();
         if (wd != null) {
            workDefs.add(wd);
         }
      }

      // Part Defs
      pkg.addPartDef(buildActionPartDef());
      pkg.addPartDef(buildTeamWorkflowPartDef());
      pkg.addPartDef(buildProblemReportWorkflowPartDef());
      pkg.addPartDef(buildChangeRequestWorkflowPartDef());
      pkg.addPartDef(buildBuildImpactDataPartDef());

      // State Machines
      for (WorkDefinition wd : workDefs) {
         pkg.addStateMachine(buildStateMachine(wd));
      }

      // Connection Defs
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.ActionToWorkflow));
      pkg.addConnectionDef(
         OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.TeamWorkflowTargetedForVersion));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.TeamWorkflowToFoundInVersion));
      pkg.addConnectionDef(
         OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.TeamWorkflowToIntroducedInVersion));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.TeamWfToTask));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.TeamWorkflowToReview));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.ProblemReportToBid));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.BuildImpactDataToTeamWf));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.BuildImpactDataToVer));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.Derive));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.ResolvedBy));

      // Part Usages (instances)
      for (IAtsAction action : allActions) {
         pkg.addPartUsage(buildActionUsage(action));
      }
      for (IAtsTeamWorkflow teamWf : allTeamWfs) {
         pkg.addPartUsage(buildTeamWfUsage(teamWf));
      }

      // Connections (instances)
      for (IAtsTeamWorkflow teamWf : allTeamWfs) {
         IAtsAction action = teamWf.getParentAction();
         if (action != null) {
            pkg.addConnection(
               new SysmlConnection("ActionToWorkflow", String.valueOf(action.getId()), getWfName(teamWf)));
         }

         // Targeted version
         IAtsVersion targetedVersion = atsApi.getVersionService().getTargetedVersion(teamWf);
         if (targetedVersion != null) {
            pkg.addConnection(new SysmlConnection("TeamWorkflowTargetedForVersion", getWfName(teamWf),
               String.valueOf(targetedVersion.getId())));
         }

         // Found-in version
         Collection<ArtifactToken> foundInVersions = atsApi.getRelationResolver().getRelated(teamWf.getStoreObject(),
            AtsRelationTypes.TeamWorkflowToFoundInVersion_Version);
         for (ArtifactToken version : foundInVersions) {
            pkg.addConnection(new SysmlConnection("TeamWorkflowToFoundInVersion", getWfName(teamWf),
               version.getIdString()));
         }

         // BID relations (for PR workflows): PR -> BID -> CR and BID -> Version
         if (teamWf.isProblemReport()) {
            Collection<ArtifactToken> bids = atsApi.getRelationResolver().getRelated(teamWf.getStoreObject(),
               AtsRelationTypes.ProblemReportToBid_Bid);
            for (ArtifactToken bid : bids) {
               // PR -> BID
               pkg.addConnection(
                  new SysmlConnection("ProblemReportToBid", getWfName(teamWf), bid.getIdString()));

               // BID part usage instance
               pkg.addPartUsage(buildBidUsage(bid));

               // BID -> CR team workflows
               for (ArtifactToken cr : atsApi.getRelationResolver().getRelated(bid,
                  AtsRelationTypes.BuildImpactDataToTeamWf_TeamWf)) {
                  pkg.addConnection(
                     new SysmlConnection("BuildImpactDataToTeamWf", bid.getIdString(), cr.getIdString()));
               }

               // BID -> Version (impacted build)
               for (ArtifactToken version : atsApi.getRelationResolver().getRelated(bid,
                  AtsRelationTypes.BuildImpactDataToVer_Version)) {
                  pkg.addConnection(
                     new SysmlConnection("BuildImpactDataToVersion", bid.getIdString(), version.getIdString()));
               }
            }
         }
      }

      return pkg;
   }

   // Part Defs

   private SysmlPartDef buildActionPartDef() {
      SysmlPartDef def = new SysmlPartDef("Action");
      def.addAttribute("artifactId", SysmlConstants.INTEGER);
      def.addAttribute("atsId", SysmlConstants.STRING);
      def.addAttribute("name", SysmlConstants.STRING);
      def.addAttribute("changeType", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("priority", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      return def;
   }

   private SysmlPartDef buildTeamWorkflowPartDef() {
      SysmlPartDef def = new SysmlPartDef("TeamWorkflow");
      def.addAttribute("artifactId", SysmlConstants.INTEGER);
      def.addAttribute("atsId", SysmlConstants.STRING);
      def.addAttribute("name", SysmlConstants.STRING);
      def.addAttribute("currentState", SysmlConstants.STRING);
      def.addAttribute("currentStateType", SysmlConstants.STRING);
      def.addAttribute("createdDate", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("createdBy", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("changeType", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("priority", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("description", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("teamDefinitionReference", SysmlConstants.INTEGER, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("percentComplete", SysmlConstants.INTEGER, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      return def;
   }

   private SysmlPartDef buildProblemReportWorkflowPartDef() {
      SysmlPartDef def = new SysmlPartDef("ProblemReportWorkflow", "TeamWorkflow");
      def.addAttribute("howFound", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("ship", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("testNumber", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("flightNumber", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("systemAnalysis", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("softwareAnalysis", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      return def;
   }

   private SysmlPartDef buildChangeRequestWorkflowPartDef() {
      SysmlPartDef def = new SysmlPartDef("ChangeRequestWorkflow", "TeamWorkflow");
      def.addAttribute("rootCause", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("proposedResolution", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      return def;
   }

   private SysmlPartDef buildBuildImpactDataPartDef() {
      SysmlPartDef def = new SysmlPartDef("BuildImpactData");
      def.addAttribute("artifactId", SysmlConstants.INTEGER);
      def.addAttribute("state", SysmlConstants.STRING);
      return def;
   }

   // State Machine from WorkDefinition

   private SysmlStateMachine buildStateMachine(WorkDefinition wd) {
      SysmlStateMachine sm = new SysmlStateMachine(stateMachineName(wd), wd.getId());
      StateDefinition startState = wd.getStartState();

      for (StateDefinition state : wd.getStates()) {
         boolean isEntry = state.equals(startState);
         sm.addState(sanitizeName(state.getName()), isEntry);
      }

      for (StateDefinition state : wd.getStates()) {
         for (StateDefinition toState : state.getToStates()) {
            sm.addTransition(sanitizeName(state.getName()), sanitizeName(toState.getName()));
         }
      }
      return sm;
   }

   // Part Usages (instances)

   private SysmlPartUsage buildActionUsage(IAtsAction action) {
      SysmlPartUsage usage = new SysmlPartUsage(String.valueOf(action.getId()), "Action");
      usage.addAttributeValue("artifactId", action.getId());
      usage.addAttributeValue("atsId", atsApi.getAtsId(action));
      usage.addAttributeValue("name", action.getName());
      return usage;
   }

   private SysmlPartUsage buildBidUsage(ArtifactToken bid) {
      SysmlPartUsage usage = new SysmlPartUsage(bid.getIdString(), "BuildImpactData");
      usage.addAttributeValue("artifactId", bid.getId());
      String state = atsApi.getAttributeResolver().getSoleAttributeValue(bid, AtsAttributeTypes.BitState, "");
      if (!state.isEmpty()) {
         usage.addAttributeValue("state", state);
      }
      return usage;
   }

   private SysmlPartUsage buildTeamWfUsage(IAtsTeamWorkflow teamWf) {
      String defName = "TeamWorkflow";
      if (teamWf.isProblemReport()) {
         defName = "ProblemReportWorkflow";
      } else if (teamWf.isChangeRequest()) {
         defName = "ChangeRequestWorkflow";
      }

      SysmlPartUsage usage = new SysmlPartUsage(String.valueOf(teamWf.getId()), defName);
      usage.addAttributeValue("artifactId", teamWf.getId());
      usage.addAttributeValue("atsId", teamWf.getAtsId());
      usage.addAttributeValue("name", teamWf.getName());
      usage.addAttributeValue("currentState", teamWf.getCurrentStateName());
      usage.addAttributeValue("currentStateType", teamWf.getCurrentStateType().name());

      WorkDefinition wd = teamWf.getWorkDefinition();
      if (wd != null) {
         usage.addAttributeValue("workflowDefinitionReference", wd.getId());
         usage.setExhibitState(stateMachineName(wd));
      }

      if (teamWf.getCreatedDate() != null) {
         usage.addAttributeValue("createdDate", teamWf.getCreatedDate().toString());
      }
      if (teamWf.getCreatedBy() != null) {
         usage.addAttributeValue("createdBy", teamWf.getCreatedBy().getUserId());
      }

      IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
      if (teamDef != null) {
         usage.addAttributeValue("teamDefinitionReference", teamDef.getId());
      }

      return usage;
   }

   // Utilities

   /**
    * State machine name includes the WorkDefinition id so a workflow's workflowDefinitionReference (also the id) maps
    * directly to its state machine.
    */
   private String stateMachineName(WorkDefinition wd) {
      return sanitizeName(wd.getName()) + "_" + wd.getId();
   }

   private String getWfName(IAtsTeamWorkflow teamWf) {
      return String.valueOf(teamWf.getId());
   }

   private String sanitizeName(String name) {
      return name.replace(" ", "_").replace("-", "_").replace(".", "_");
   }
}
