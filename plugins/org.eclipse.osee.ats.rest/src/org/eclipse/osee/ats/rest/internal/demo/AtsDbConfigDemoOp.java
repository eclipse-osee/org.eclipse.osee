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

package org.eclipse.osee.ats.rest.internal.demo;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.Csci;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxActionableItem;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxTeamDef;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.demo.DemoAis;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.query.NextRelease;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.core.config.OrganizePrograms;
import org.eclipse.osee.ats.core.task.TaskSetDefinitionTokensDemo;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Initialization class that will load configuration information for a sample DB.
 *
 * @author Donald G. Dunne
 */
public class AtsDbConfigDemoOp {

   private final AtsApi atsApi;
   private IAtsConfigTx cfgTx;

   public AtsDbConfigDemoOp(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public XResultData run() {

      configTxDemoAisAndTeams();
      configureForParallelCommit();

      IAtsChangeSet changes = atsApi.createChangeSet("Set ATS Admin");
      changes.relate(AtsUserGroups.AtsTempAdmin, CoreRelationTypes.Users_User, DemoUsers.Joe_Smith);
      changes.execute();

      (new OrganizePrograms(atsApi)).run();

      createDemoWebConfig();

      atsApi.setConfigValue(AtsUtil.SINGLE_SERVER_DEPLOYMENT, "true");
      return new XResultData();
   }

   private void createDemoWebConfig() {
      ArtifactToken headingArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.AtsTopFolder);

      IAtsChangeSet changes = atsApi.createChangeSet("Create Web Programs");
      ArtifactToken oseeWebArt = changes.createArtifact(headingArt, AtsArtifactToken.WebPrograms);

      ArtifactToken sawProgram = atsApi.getQueryService().getArtifact(DemoArtifactToken.SAW_PL_Program);
      changes.relate(oseeWebArt, CoreRelationTypes.UniversalGrouping_Members, sawProgram);

      changes.execute();
   }

   private void configTxDemoAisAndTeams() {

      cfgTx = atsApi.getConfigService().createConfigTx("Create Demo Config", AtsCoreUsers.SYSTEM_USER);
      IAtsConfigTxTeamDef topTeamDef =
         cfgTx.createTeamDef((IAtsTeamDefinition) null, AtsArtifactToken.TopTeamDefinition).andWorkDef(
            AtsWorkDefinitionTokens.WorkDef_Team_Default);
      IAtsConfigTxActionableItem topAi =
         cfgTx.createActionableItem(AtsArtifactToken.TopActionableItem).andActionable(false);

      createSawPlTeamConfig(cfgTx, topTeamDef, topAi);
      createSawTeamConfig(cfgTx, topTeamDef, topAi);
      createCisTeamConfig(cfgTx, topTeamDef, topAi);
      createFacilitiesTeamConfig(cfgTx, topTeamDef, topAi);
      createProcessTeamConfig(cfgTx, topTeamDef, topAi);
      createToolsTeamConfig(cfgTx, topTeamDef, topAi);
      createSystemSafetyTeamConfig(cfgTx, topTeamDef, topAi);

      cfgTx.execute();

   }

   private void createFacilitiesTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // Facilities Team
      IAtsConfigTxTeamDef facilitiesTeamDef =
         topTeam.createChildTeamDef(topTeam.getTeamDef(), DemoArtifactToken.Facilities_Team) //
            .andLeads(DemoUsers.John_Stevens) //
            .andMembers(DemoUsers.John_Stevens, DemoUsers.Steven_Michael, DemoUsers.Michael_John,
               DemoUsers.Jason_Stevens) //
            .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Simple);

      // IT Team
      facilitiesTeamDef.createChildTeamDef(facilitiesTeamDef.getTeamDef(), DemoArtifactToken.Facilities_IT_Team) //
         .andLeads(DemoUsers.Kay_Wheeler) //
         .andMembers(DemoUsers.Kay_Wheeler, DemoUsers.Jason_Stevens, DemoUsers.Michael_John, DemoUsers.Jason_Stevens) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Simple);

      // Facilities Actionable Items
      IAtsConfigTxActionableItem facilitiesAi =
         topActionableItem.createChildActionableItem(DemoArtifactToken.Facilities_Ai) //
            .andActionable(true) //
            .andTeamDef(DemoArtifactToken.Facilities_Team);

      facilitiesAi.createChildActionableItem("Backups") //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.Facilities_IT_Team);

      facilitiesAi.createChildActionableItem("Break Room") //
         .andActionable(true);

      facilitiesAi.createChildActionableItem("Computers") //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.Facilities_IT_Team);

      facilitiesAi.createChildActionableItem("Network") //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.Facilities_IT_Team);

      facilitiesAi.createChildActionableItem("Vending Machines") //
         .andActionable(true);

   }

   private void createToolsTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // Tools Team
      IAtsConfigTxTeamDef toolsTeamDef = topTeam.createChildTeamDef(topTeam.getTeamDef(), DemoArtifactToken.Tools_Team) //
         .andLeads(DemoUsers.Jeffery_Kay) //
         .andMembers(DemoUsers.Jeffery_Kay, DemoUsers.Roland_Stevens) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Default);

      // Web Team
      toolsTeamDef.createChildTeamDef(toolsTeamDef.getTeamDef(), DemoArtifactToken.Website_Team) //
         .andLeads(DemoUsers.Karmen_John) //
         .andMembers(DemoUsers.Karmen_John, DemoUsers.Jeffery_Kay, DemoUsers.Roland_Stevens) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Simple);

      // Tools Actionable Items
      IAtsConfigTxActionableItem toolsAi = topActionableItem.createChildActionableItem(DemoArtifactToken.Tools_Ai) //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.Tools_Team);

      toolsAi.createChildActionableItem(DemoArtifactToken.Reader_AI) //
         .andActionable(true);

      toolsAi.createChildActionableItem("Results Reporter") //
         .andActionable(true);

      toolsAi.createChildActionableItem(DemoArtifactToken.Timesheet_AI) //
         .andActionable(true);

      toolsAi.createChildActionableItem("Website") //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.Website_Team);

   }

   private void createSystemSafetyTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeamDef, IAtsConfigTxActionableItem topAi) {

      // System Safety Team
      topTeamDef.createChildTeamDef(topTeamDef.getTeamDef(), DemoArtifactToken.System_Safety_Team) //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Jeffery_Kay) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Default);

      // System Safety Actionable Items
      topAi.createChildActionableItem(DemoArtifactToken.System_Safety_Ai) //
         .andTeamDef(DemoArtifactToken.System_Safety_Team) //
         .andActionable(true);

   }

   private void createProcessTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeamDef, IAtsConfigTxActionableItem topActionableItem) {

      // Process Team
      topTeamDef.createChildTeamDef(topTeamDef.getTeamDef(), DemoArtifactToken.Process_Team) //
         .andLeads(DemoUsers.Alex_Kay) //
         .andMembers(DemoUsers.Keith_Johnson, DemoUsers.Michael_Alex, DemoUsers.Janice_Michael, DemoUsers.Alex_Kay) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Default);

      // Process Actionable Items
      IAtsConfigTxActionableItem processesAi = topActionableItem.createChildActionableItem("Processes") //
         .andActionable(false) //
         .andTeamDef(DemoArtifactToken.Process_Team);

      processesAi.createChildActionableItem("Coding Standards") //
         .andActionable(true);

      processesAi.createChildActionableItem("Config Mgmt") //
         .andActionable(true);

      processesAi.createChildActionableItem("New Employee Manual") //
         .andActionable(true);

      processesAi.createChildActionableItem("Reviews") //
         .andActionable(true);
   }

   private void createSawPlTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // SAW PL
      IAtsConfigTxTeamDef sawPlTeamDef = topTeam.createChildTeamDef(topTeam.getTeamDef(), DemoArtifactToken.SAW_PL_TeamDef) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andLeads(DemoUsers.Kay_Jason) //
         .andMembers(DemoUsers.Kay_Jason, DemoUsers.Michael_John, DemoUsers.Steven_Kohn) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Default) //
         .andVersion(DemoArtifactToken.SAW_Product_Line, ReleasedOption.UnReleased, DemoBranches.SAW_PL,
            NextRelease.Next) //
         .andVersion(DemoArtifactToken.SAW_Hardening_Branch, ReleasedOption.UnReleased,
            DemoBranches.SAW_PL_Hardening_Branch, NextRelease.Next) //
         .andRelatedPeerWorkflowDefinition(AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer_Demo);

      // SAW PL Team Defs
      sawPlTeamDef.createChildTeamDef(sawPlTeamDef.getTeamDef(), DemoArtifactToken.SAW_PL_Code_TeamDef) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.Code) //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_Code) //
         .andTeamWorkflowArtifactType(AtsDemoOseeTypes.DemoCodeTeamWorkflow);

      sawPlTeamDef.createChildTeamDef(sawPlTeamDef.getTeamDef(), DemoArtifactToken.SAW_PL_HW_TeamDef) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.Hardware) //
         .andLeads(DemoUsers.Jason_Michael) //
         .andMembers(DemoUsers.Jason_Michael) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Default) //
         .andTeamWorkflowArtifactType(AtsDemoOseeTypes.DemoReqTeamWorkflow);

      sawPlTeamDef.createChildTeamDef(sawPlTeamDef.getTeamDef(), DemoArtifactToken.SAW_PL_Requirements_TeamDef) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.Requirements) //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_Req) //
         .andTeamWorkflowArtifactType(AtsDemoOseeTypes.DemoReqTeamWorkflow);

      sawPlTeamDef.createChildTeamDef(sawPlTeamDef.getTeamDef(), DemoArtifactToken.SAW_PL_SW_Design_TeamDef) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.SW_Design) //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_SwDesign);

      sawPlTeamDef.createChildTeamDef(sawPlTeamDef.getTeamDef(), DemoArtifactToken.SAW_PL_Test_TeamDef) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.Test) //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_Test) //
         .andTeamWorkflowArtifactType(AtsDemoOseeTypes.DemoTestTeamWorkflow);

      sawPlTeamDef.createChildTeamDef(sawPlTeamDef.getTeamDef(), DemoArtifactToken.SAW_PL_ARB_TeamDef) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.ARB) //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_ProductLine) //
         .andTeamWorkflowArtifactType(AtsArtifactTypes.TeamWorkflow);

      // SAW SW Actionable Items
      IAtsConfigTxActionableItem sawPlSwAi =
         topActionableItem.createChildActionableItem(DemoArtifactToken.SAW_PL_CSCI_AI) //
            .andProgram(DemoArtifactToken.SAW_PL_Program) //
            .andActionable(false);

      sawPlSwAi.createChildActionableItem(DemoArtifactToken.SAW_PL_Code_AI) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.Code) //
         .andTeamDef(DemoArtifactToken.SAW_PL_Code_TeamDef) //
         .andActionable(true) //
         .andChildAis("COMM", "MSM", "NAV", "Test Page");

      sawPlSwAi.createChildActionableItem(DemoArtifactToken.SAW_PL_Requirements_AI) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.Requirements) //
         .andTeamDef(DemoArtifactToken.SAW_PL_Requirements_TeamDef) //
         .andActionable(true) //
         .andChildAis("COMM", "MSM", "NAV") //
         .and(CoreAttributeTypes.StaticId, DemoAis.TEST_AI);

      sawPlSwAi.createChildActionableItem(DemoArtifactToken.SAW_PL_Test_AI) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.Test) //
         .andTeamDef(DemoArtifactToken.SAW_PL_Test_TeamDef) //
         .andActionable(true) //
         .andChildAis("ADT", "COMM", "MSM", "NAV", "RulLists");

      sawPlSwAi.createChildActionableItem(DemoArtifactToken.SAW_PL_SW_Design_AI) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.SW_Design) //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.SAW_PL_SW_Design_TeamDef);

      sawPlSwAi.createChildActionableItem(DemoArtifactToken.SAW_PL_HW_AI) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.Hardware) //
         .andTeamDef(DemoArtifactToken.SAW_PL_HW_TeamDef) //
         .andActionable(true) //
         .andChildAis("Adapter", "Case", "Manual", "Screen");

      sawPlSwAi.createChildActionableItem(DemoArtifactToken.SAW_PL_ARB_AI) //
         .andProgram(DemoArtifactToken.SAW_PL_Program) //
         .andWorkType(WorkType.ARB) //
         .andTeamDef(DemoArtifactToken.SAW_PL_ARB_TeamDef) //
         .andActionable(true);

      // SAW PL Program
      cfgTx.createProgram(DemoArtifactToken.SAW_PL_Program) //
         .andTeamDef(DemoArtifactToken.SAW_PL_TeamDef) //
         .and(AtsAttributeTypes.Description, "Program object for SAW PL Program") //
         .and(AtsAttributeTypes.Namespace, "org.demo.saw.pl");

   }

   private void createSawTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // SAW SW
      IAtsConfigTxTeamDef sawSwTeamDef = topTeam.createChildTeamDef(topTeam.getTeamDef(), DemoArtifactToken.SAW_SW) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .and(CoreAttributeTypes.StaticId, "saw.teamDefHoldingVersions") //
         .andLeads(DemoUsers.Kay_Jason) //
         .andMembers(DemoUsers.Steven_Kohn, DemoUsers.Michael_John, DemoUsers.Kay_Jason) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Default) //
         .andVersion(DemoArtifactToken.SAW_Bld_1, ReleasedOption.Released, DemoBranches.SAW_Bld_1, NextRelease.None) //
         .andVersion(DemoArtifactToken.SAW_Bld_2, ReleasedOption.UnReleased, DemoBranches.SAW_Bld_2, NextRelease.Next) //
         .andVersion(DemoArtifactToken.SAW_Bld_3, ReleasedOption.UnReleased, DemoBranches.SAW_Bld_3, NextRelease.None) //
         .andRelatedPeerWorkflowDefinition(AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer_Demo);

      /**
       * Artifact types used can be declared through Team Definition or Workflow Definition. This will get its artifact
       * type through the Workflow Definition.
       */
      // SAW SW Team Defs
      sawSwTeamDef.createChildTeamDef(sawSwTeamDef.getTeamDef(), DemoArtifactToken.SAW_Code) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andWorkType(WorkType.Code) //
         .andCsci(Csci.DP, Csci.SP, Csci.WP) //
         .and(CoreAttributeTypes.StaticId, "saw.code") //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_Code);

      /**
       * Artifact types used can be declared through Team Definition or Workflow Definition. This is an example of the
       * config through Team Definition.
       */
      sawSwTeamDef.createChildTeamDef(sawSwTeamDef.getTeamDef(), DemoArtifactToken.SAW_HW) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andWorkType(WorkType.Hardware) //
         .andLeads(DemoUsers.Jason_Michael) //
         .andMembers(DemoUsers.Jason_Michael) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Default) //
         .andTeamWorkflowArtifactType(AtsDemoOseeTypes.DemoReqTeamWorkflow);

      sawSwTeamDef.createChildTeamDef(sawSwTeamDef.getTeamDef(), DemoArtifactToken.SAW_Test) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andWorkType(WorkType.Test) //
         .andCsci(Csci.DP, Csci.SP, Csci.WP) //
         .and(CoreAttributeTypes.StaticId, "saw.test") //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_Test) //
         .andTeamWorkflowArtifactType(AtsDemoOseeTypes.DemoTestTeamWorkflow);

      sawSwTeamDef.createChildTeamDef(sawSwTeamDef.getTeamDef(), DemoArtifactToken.SAW_SW_Design) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andWorkType(WorkType.SW_Design) //
         .and(CoreAttributeTypes.StaticId, "saw.sw.design") //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_SwDesign) //
         .andRelatedPeerWorkflowDefinition(DemoWorkDefinitions.WorkDef_Review_Demo_Peer_SwDesign) //
         .andRelatedTaskWorkflowDefinition(DemoWorkDefinitions.WorkDef_Task_Demo_SwDesign,
            AtsWorkDefinitionTokens.WorkDef_Task_Default) //
         .andTaskSet(TaskSetDefinitionTokensDemo.SawSwDesignTestingChecklist,
            TaskSetDefinitionTokensDemo.SawSwDesignProcessChecklist);

      sawSwTeamDef.createChildTeamDef(sawSwTeamDef.getTeamDef(), DemoArtifactToken.SAW_Requirements) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andWorkType(WorkType.Requirements) //
         .andCsci(Csci.DP, Csci.SP, Csci.WP) //
         .and(CoreAttributeTypes.StaticId, "saw.reqirements") //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_Req) //
         .andTeamWorkflowArtifactType(AtsDemoOseeTypes.DemoReqTeamWorkflow);

      // SAW SW Actionable Items
      IAtsConfigTxActionableItem sawSwAi = topActionableItem.createChildActionableItem(DemoArtifactToken.SAW_CSCI_AI) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andCsci(Csci.DP, Csci.SP, Csci.WP) //
         .andActionable(false);

      sawSwAi.createChildActionableItem(DemoArtifactToken.SAW_Code_AI) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andWorkType(WorkType.Code) //
         .andCsci(Csci.DP, Csci.SP, Csci.WP) //
         .andTeamDef(DemoArtifactToken.SAW_Code) //
         .andActionable(true) //
         .andChildAis("COMM", "MSM", "NAV", "Test Page");

      sawSwAi.createChildActionableItem(DemoArtifactToken.SAW_HW_AI) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andWorkType(WorkType.Hardware) //
         .andTeamDef(DemoArtifactToken.SAW_HW) //
         .andChildAis(DemoArtifactToken.Adapter_AI) //
         .andActionable(true) //
         .andChildAis("Case", "Manual", "Screen");

      sawSwAi.createChildActionableItem(DemoArtifactToken.SAW_Requirements_AI) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andWorkType(WorkType.Requirements) //
         .andCsci(Csci.DP, Csci.SP, Csci.WP) //
         .andTeamDef(DemoArtifactToken.SAW_Requirements) //
         .andActionable(true) //
         .andChildAis("COMM", "MSM", "NAV");

      sawSwAi.createChildActionableItem(DemoArtifactToken.SAW_Test_AI) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andWorkType(WorkType.Test) //
         .andCsci(Csci.DP, Csci.SP, Csci.WP) //
         .andTeamDef(DemoArtifactToken.SAW_Test) //
         .andActionable(true) //
         .andChildAis("ADT", "COMM", "MSM", "NAV", "RunLists");

      sawSwAi.createChildActionableItem(DemoArtifactToken.SAW_SW_Design_AI) //
         .andProgram(DemoArtifactToken.SAW_Program) //
         .andWorkType(WorkType.SW_Design) //
         .andTeamDef(DemoArtifactToken.SAW_SW_Design) //
         .andActionable(true);

      // SAW Program
      cfgTx.createProgram(DemoArtifactToken.SAW_Program) //
         .andTeamDef(DemoArtifactToken.SAW_SW) //
         .andCsci(Csci.DP, Csci.SP, Csci.WP) //
         .and(AtsAttributeTypes.Description, "Program object for SAW Program") //
         .and(AtsAttributeTypes.Namespace, "org.demo.saw");

   }

   private void createCisTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // CIS SW
      IAtsConfigTxTeamDef cisSwTeamDef = topTeam.createChildTeamDef(topTeam.getTeamDef(), DemoArtifactToken.CIS_SW) //
         .and(CoreAttributeTypes.StaticId, "cis.teamDefHoldingVersions") //
         .andLeads(DemoUsers.Kay_Jason) //
         .andMembers(DemoUsers.Steven_Kohn, DemoUsers.Michael_John, DemoUsers.Kay_Jason) //
         .andWorkDef(AtsWorkDefinitionTokens.WorkDef_Team_Default) //
         .andVersion("CIS Bld 1", ReleasedOption.Released, DemoBranches.CIS_Bld_1, NextRelease.None) //
         .andVersion("CIS Bld 2", ReleasedOption.UnReleased, null, NextRelease.Next) //
         .andVersion("CIS Bld 3", ReleasedOption.UnReleased, null, NextRelease.None);

      // CIS SW Team Defs
      cisSwTeamDef.createChildTeamDef(cisSwTeamDef.getTeamDef(), DemoArtifactToken.CIS_Code) //
         .and(CoreAttributeTypes.StaticId, "cis.code") //
         .andLeads(DemoUsers.Jason_Michael) //
         .andMembers(DemoUsers.Jason_Michael) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_Code);

      cisSwTeamDef.createChildTeamDef(cisSwTeamDef.getTeamDef(), DemoArtifactToken.CIS_Test) //
         .and(CoreAttributeTypes.StaticId, "cis.test") //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_Test) //
         .andTeamWorkflowArtifactType(AtsDemoOseeTypes.DemoTestTeamWorkflow);

      cisSwTeamDef.createChildTeamDef("CIS SW Design") //
         .and(CoreAttributeTypes.StaticId, "cis.sw.design") //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_SwDesign);

      cisSwTeamDef.createChildTeamDef("CIS Requirements") //
         .and(CoreAttributeTypes.StaticId, "cis.reqirements") //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(DemoWorkDefinitions.WorkDef_Team_Demo_Req) //
         .andTeamWorkflowArtifactType(AtsDemoOseeTypes.DemoReqTeamWorkflow);

      // CIS SW Actionable Items
      IAtsConfigTxActionableItem cisSwAi = topActionableItem.createChildActionableItem(DemoArtifactToken.CIS_CSCI) //
         .andActionable(false);

      cisSwAi.createChildActionableItem("CIS Code") //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.CIS_Code);

      cisSwAi.createChildActionableItem("CIS Requirements") //
         .andActionable(true) //
         .andTeamDef("CIS Requirements");

      cisSwAi.createChildActionableItem(DemoArtifactToken.CIS_Test_AI) //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.CIS_Test);

      cisSwAi.createChildActionableItem("CIS SW Design") //
         .andActionable(true) //
         .andTeamDef("CIS SW Design");

      // CIS Program
      cfgTx.createProgram(DemoArtifactToken.CIS_Program) //
         .andTeamDef(DemoArtifactToken.CIS_SW) //
         .and(AtsAttributeTypes.Description, "Program object for CIS Program") //
         .and(AtsAttributeTypes.Namespace, "org.demo.cis");

   }

   /**
    * Configure SAW_Bld_1 and SAW_Bld_2 for parallel commit, including recursive setup where SAW_Bld_1 needs to be
    * committed to SAW_Bld_1 and SAW_Bld_2 and SAW_Bld_2 needs to be committed to SAW_Bld_2 and SAW_Bld_1
    */
   private void configureForParallelCommit() {
      IAtsChangeSet changes = atsApi.createChangeSet("configureForParallelCommit");

      IAtsVersion sawBld1Ver = atsApi.getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = atsApi.getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = atsApi.getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_3);

      changes.relate(sawBld1Ver, AtsRelationTypes.ParallelVersion_Child, sawBld2Ver);
      changes.relate(sawBld2Ver, AtsRelationTypes.ParallelVersion_Child, sawBld1Ver);
      changes.relate(sawBld2Ver, AtsRelationTypes.ParallelVersion_Child, sawBld3Ver);

      changes.execute();
   }
}
