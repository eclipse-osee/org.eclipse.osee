/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.demo.config;

import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxActionableItem;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxTeamDef;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.NextRelease;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.ats.ide.config.AtsDatabaseConfig;
import org.eclipse.osee.ats.ide.demo.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsGroup;
import org.eclipse.osee.ats.ide.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Initialization class that will load configuration information for a sample DB.
 *
 * @author Donald G. Dunne
 */
public class DemoDatabaseConfig implements IDbInitializationTask {

   @Override
   public void run() {

      configTxDemoAisAndTeams();

      AtsWorkDefinitionSheetProviders.initializeDatabase(new XResultData(false), "demo");
      TestUtil.setDemoDb(true);

      configureForParallelCommit();

      AtsGroup.AtsTempAdmin.addMember(UserManager.getUser(DemoUsers.Joe_Smith));
      AtsGroup.AtsTempAdmin.getArtifact().persist("Set Joe as Temp Admin");

      AtsDatabaseConfig.organizePrograms(AtsArtifactTypes.Program, DemoArtifactToken.DemoPrograms);

      AtsClientService.get().setConfigValue(AtsUtil.SINGLE_SERVER_DEPLOYMENT, "true");
      // Reload caches cause Demo sheet import added new users
      AtsClientService.getConfigEndpoint().getWithPend();
      AtsClientService.get().getConfigService().getConfigurations();
   }

   private void configTxDemoAisAndTeams() {
      IAtsConfigTx cfgTx =
         AtsClientService.get().getConfigService().createConfigTx("Create Demo Config", AtsCoreUsers.SYSTEM_USER);

      cfgTx.createUsers(DemoUsers.values());

      IAtsConfigTxTeamDef topTeam =
         cfgTx.createTeamDef(AtsArtifactToken.TopTeamDefinition).andWorkDef(AtsArtifactToken.WorkDef_Team_Default);
      IAtsConfigTxActionableItem topActionableItem =
         cfgTx.createActionableItem(AtsArtifactToken.TopActionableItem).andActionable(false);

      createSawPlTeamConfig(cfgTx, topTeam, topActionableItem);
      createSawTeamConfig(cfgTx, topTeam, topActionableItem);
      createCisTeamConfig(cfgTx, topTeam, topActionableItem);
      createFacilitiesTeamConfig(cfgTx, topTeam, topActionableItem);
      createProcessTeamConfig(cfgTx, topTeam, topActionableItem);
      createToolsTeamConfig(cfgTx, topTeam, topActionableItem);
      createSystemSafetyTeamConfig(cfgTx, topTeam, topActionableItem);

      cfgTx.execute();

   }

   private void createFacilitiesTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // Facilities Team
      IAtsConfigTxTeamDef facilitiesTeam = topTeam.createChildTeamDef(DemoArtifactToken.Facilities_Team) //
         .andLeads(DemoUsers.John_Stevens) //
         .andMembers(DemoUsers.John_Stevens, DemoUsers.Steven_Michael, DemoUsers.Michael_John, DemoUsers.Jason_Stevens) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Simple);

      // IT Team
      facilitiesTeam.createChildTeamDef(DemoArtifactToken.Facilities_IT_Team) //
         .andLeads(DemoUsers.Kay_Wheeler) //
         .andMembers(DemoUsers.Kay_Wheeler, DemoUsers.Jason_Stevens, DemoUsers.Michael_John, DemoUsers.Jason_Stevens) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Simple);

      // Facilities Actionable Items
      IAtsConfigTxActionableItem facilitiesAI =
         topActionableItem.createChildActionableItem(DemoArtifactToken.Facilities_Ai) //
            .andActionable(true) //
            .andTeamDef(DemoArtifactToken.Facilities_Team);

      facilitiesAI.createChildActionableItem("Backups") //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.Facilities_IT_Team);

      facilitiesAI.createChildActionableItem("Break Room") //
         .andActionable(true);

      facilitiesAI.createChildActionableItem("Computers") //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.Facilities_IT_Team);

      facilitiesAI.createChildActionableItem("Network") //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.Facilities_IT_Team);

      facilitiesAI.createChildActionableItem("Vending Machines") //
         .andActionable(true);

   }

   private void createToolsTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // Tools Team
      IAtsConfigTxTeamDef toolsTeamTeam = topTeam.createChildTeamDef(DemoArtifactToken.Tools_Team) //
         .andLeads(DemoUsers.Jeffery_Kay) //
         .andMembers(DemoUsers.Jeffery_Kay, DemoUsers.Roland_Stevens) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Default);

      // Web Team
      toolsTeamTeam.createChildTeamDef(DemoArtifactToken.Website_Team) //
         .andLeads(DemoUsers.Karmen_John) //
         .andMembers(DemoUsers.Karmen_John, DemoUsers.Jeffery_Kay, DemoUsers.Roland_Stevens) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Simple);

      // Tools Actionable Items
      IAtsConfigTxActionableItem toolsTeamAI = topActionableItem.createChildActionableItem(DemoArtifactToken.Tools_Ai) //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.Tools_Team);

      toolsTeamAI.createChildActionableItem(DemoArtifactToken.Reader_AI) //
         .andActionable(true);

      toolsTeamAI.createChildActionableItem("Results Reporter") //
         .andActionable(true);

      toolsTeamAI.createChildActionableItem(DemoArtifactToken.Timesheet_AI) //
         .andActionable(true);

      toolsTeamAI.createChildActionableItem("Website") //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.Website_Team);

   }

   private void createSystemSafetyTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // System Safety Team
      topTeam.createChildTeamDef(DemoArtifactToken.System_Safety_Team) //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Jeffery_Kay) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Default);

      // System Safety Actionable Items
      topActionableItem.createChildActionableItem(DemoArtifactToken.System_Safety_Ai) //
         .andTeamDef(DemoArtifactToken.System_Safety_Team) //
         .andActionable(true);

   }

   private void createProcessTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // Process Team
      topTeam.createChildTeamDef(DemoArtifactToken.Process_Team) //
         .andLeads(DemoUsers.Alex_Kay) //
         .andMembers(DemoUsers.Keith_Johnson, DemoUsers.Michael_Alex, DemoUsers.Janice_Michael, DemoUsers.Alex_Kay) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Default);

      // Process Actionable Items
      IAtsConfigTxActionableItem processesAI = topActionableItem.createChildActionableItem("Processes") //
         .andActionable(false) //
         .andTeamDef(DemoArtifactToken.Process_Team);

      processesAI.createChildActionableItem("Coding Standards") //
         .andActionable(true);

      processesAI.createChildActionableItem("Config Mgmt") //
         .andActionable(true);

      processesAI.createChildActionableItem("New Employee Manual") //
         .andActionable(true);

      processesAI.createChildActionableItem("Reviews") //
         .andActionable(true);
   }

   private void createSawPlTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // SAW PL
      IAtsConfigTxTeamDef sawPlTeam = topTeam.createChildTeamDef(DemoArtifactToken.SAW_PL) //
         .andLeads(DemoUsers.Kay_Jason) //
         .andMembers(DemoUsers.Kay_Jason, DemoUsers.Michael_John, DemoUsers.Steven_Kohn) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Default) //
         .andVersion(DemoArtifactToken.SAW_Product_Line, ReleasedOption.Released, DemoBranches.SAW_PL, NextRelease.None) //
         .andVersion(DemoArtifactToken.SAW_Hardening_Branch, ReleasedOption.UnReleased,
            DemoBranches.SAW_PL_Hardening_Branch, NextRelease.Next);

      // SAW PL Team Defs
      sawPlTeam.createChildTeamDef(DemoArtifactToken.SAW_PL_Code) //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_Code) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoCodeTeamWorkflow);

      sawPlTeam.createChildTeamDef(DemoArtifactToken.SAW_PL_HW) //
         .andLeads(DemoUsers.Jason_Michael) //
         .andMembers(DemoUsers.Jason_Michael) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Default) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoReqTeamWorkflow);

      sawPlTeam.createChildTeamDef(DemoArtifactToken.SAW_PL_Requirements) //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_Req) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoReqTeamWorkflow);

      sawPlTeam.createChildTeamDef(DemoArtifactToken.SAW_PL_SW_Design) //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_SwDesign);

      sawPlTeam.createChildTeamDef(DemoArtifactToken.SAW_PL_Test) //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_Test) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoTestTeamWorkflow);

      // SAW SW Actionable Items
      IAtsConfigTxActionableItem sawPlSwAI =
         topActionableItem.createChildActionableItem(DemoArtifactToken.SAW_PL_CSCI_AI) //
            .andActionable(false);

      sawPlSwAI.createChildActionableItem(DemoArtifactToken.SAW_PL_Code_AI) //
         .andTeamDef(DemoArtifactToken.SAW_PL_Code) //
         .andActionable(true) //
         .andChildAis("COMM", "MSM", "NAV", "Test Page");

      sawPlSwAI.createChildActionableItem(DemoArtifactToken.SAW_PL_Requirements_AI) //
         .andTeamDef(DemoArtifactToken.SAW_PL_Requirements) //
         .andActionable(true) //
         .andChildAis("COMM", "MSM", "NAV");

      sawPlSwAI.createChildActionableItem(DemoArtifactToken.SAW_PL_Test_AI) //
         .andTeamDef(DemoArtifactToken.SAW_PL_Test) //
         .andActionable(true) //
         .andChildAis("ADT", "COMM", "MSM", "NAV", "RulLists");

      sawPlSwAI.createChildActionableItem(DemoArtifactToken.SAW_PL_SW_Design_AI) //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.SAW_PL_SW_Design);

      sawPlSwAI.createChildActionableItem(DemoArtifactToken.SAW_PL_HW_AI) //
         .andTeamDef(DemoArtifactToken.SAW_PL_HW) //
         .andActionable(true) //
         .andChildAis("Adapter", "Case", "Manual", "Screen");

      // SAW PL Program
      cfgTx.createProgram(DemoArtifactToken.SAW_PL_Program) //
         .andTeamDef(DemoArtifactToken.SAW_PL) //
         .and(AtsAttributeTypes.Description, "Program object for SAW PL Program") //
         .and(AtsAttributeTypes.Namespace, "org.demo.saw.pl");

   }

   private void createSawTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // SAW SW
      IAtsConfigTxTeamDef sawSwTeam = topTeam.createChildTeamDef(DemoArtifactToken.SAW_SW) //
         .and(CoreAttributeTypes.StaticId, "saw.teamDefHoldingVersions") //
         .andLeads(DemoUsers.Kay_Jason) //
         .andMembers(DemoUsers.Steven_Kohn, DemoUsers.Michael_John, DemoUsers.Kay_Jason) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Default) //
         .andVersion(DemoArtifactToken.SAW_Bld_1, ReleasedOption.Released, DemoBranches.SAW_Bld_1, NextRelease.None) //
         .andVersion(DemoArtifactToken.SAW_Bld_2, ReleasedOption.UnReleased, DemoBranches.SAW_Bld_2, NextRelease.Next) //
         .andVersion(DemoArtifactToken.SAW_Bld_3, ReleasedOption.UnReleased, DemoBranches.SAW_Bld_3, NextRelease.None);

      // SAW SW Team Defs
      sawSwTeam.createChildTeamDef(DemoArtifactToken.SAW_Code) //
         .and(CoreAttributeTypes.StaticId, "saw.code") //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_Code) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoCodeTeamWorkflow);

      sawSwTeam.createChildTeamDef(DemoArtifactToken.SAW_HW) //
         .andLeads(DemoUsers.Jason_Michael) //
         .andMembers(DemoUsers.Jason_Michael) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Default) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoReqTeamWorkflow);

      sawSwTeam.createChildTeamDef(DemoArtifactToken.SAW_Test) //
         .and(CoreAttributeTypes.StaticId, "saw.test") //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_Test) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoTestTeamWorkflow);

      sawSwTeam.createChildTeamDef(DemoArtifactToken.SAW_SW_Design) //
         .and(CoreAttributeTypes.StaticId, "saw.sw.design") //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_SwDesign);

      sawSwTeam.createChildTeamDef(DemoArtifactToken.SAW_Requirements) //
         .and(CoreAttributeTypes.StaticId, "saw.reqirements") //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_Req) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoReqTeamWorkflow);

      // SAW SW Actionable Items
      IAtsConfigTxActionableItem sawSwAI = topActionableItem.createChildActionableItem(DemoArtifactToken.SAW_CSCI_AI) //
         .andActionable(false);

      sawSwAI.createChildActionableItem(DemoArtifactToken.SAW_Code_AI) //
         .andTeamDef(DemoArtifactToken.SAW_Code) //
         .andActionable(true) //
         .andChildAis("COMM", "MSM", "NAV", "Test Page");

      sawSwAI.createChildActionableItem(DemoArtifactToken.SAW_HW_AI) //
         .andTeamDef(DemoArtifactToken.SAW_HW) //
         .andChildAis(DemoArtifactToken.Adapter_AI) //
         .andActionable(true) //
         .andChildAis("Case", "Manual", "Screen");

      sawSwAI.createChildActionableItem(DemoArtifactToken.SAW_Requirements_AI) //
         .andTeamDef(DemoArtifactToken.SAW_Requirements) //
         .andActionable(true) //
         .andChildAis("COMM", "MSM", "NAV");

      sawSwAI.createChildActionableItem(DemoArtifactToken.SAW_Test_AI) //
         .andTeamDef(DemoArtifactToken.SAW_Test) //
         .andActionable(true) //
         .andChildAis("ADT", "COMM", "MSM", "NAV", "RunLists");

      sawSwAI.createChildActionableItem(DemoArtifactToken.SAW_SW_Design_AI) //
         .andTeamDef(DemoArtifactToken.SAW_SW_Design) //
         .andActionable(true);

      // SAW Program
      cfgTx.createProgram(DemoArtifactToken.SAW_Program) //
         .andTeamDef(DemoArtifactToken.SAW_SW) //
         .and(AtsAttributeTypes.Description, "Program object for SAW Program") //
         .and(AtsAttributeTypes.Namespace, "org.demo.saw");

   }

   private void createCisTeamConfig(IAtsConfigTx cfgTx, IAtsConfigTxTeamDef topTeam, IAtsConfigTxActionableItem topActionableItem) {

      // CIS SW
      IAtsConfigTxTeamDef cisSwTeam = topTeam.createChildTeamDef(DemoArtifactToken.CIS_SW) //
         .and(CoreAttributeTypes.StaticId, "cis.teamDefHoldingVersions") //
         .andLeads(DemoUsers.Kay_Jason) //
         .andMembers(DemoUsers.Steven_Kohn, DemoUsers.Michael_John, DemoUsers.Kay_Jason) //
         .andWorkDef(AtsArtifactToken.WorkDef_Team_Default) //
         .andVersion("CIS Bld 1", ReleasedOption.Released, DemoBranches.CIS_Bld_1, NextRelease.None) //
         .andVersion("CIS Bld 2", ReleasedOption.UnReleased, null, NextRelease.Next) //
         .andVersion("CIS Bld 3", ReleasedOption.UnReleased, null, NextRelease.None);

      // CIS SW Team Defs
      cisSwTeam.createChildTeamDef(DemoArtifactToken.CIS_Code) //
         .and(CoreAttributeTypes.StaticId, "cis.code") //
         .andLeads(DemoUsers.Jason_Michael) //
         .andMembers(DemoUsers.Jason_Michael) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_Code) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoCodeTeamWorkflow);

      cisSwTeam.createChildTeamDef(DemoArtifactToken.CIS_Test) //
         .and(CoreAttributeTypes.StaticId, "cis.test") //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_Test) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoTestTeamWorkflow);

      cisSwTeam.createChildTeamDef("CIS SW Design") //
         .and(CoreAttributeTypes.StaticId, "cis.sw.design") //
         .andLeads(DemoUsers.Kay_Jones) //
         .andMembers(DemoUsers.Kay_Jones) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_SwDesign);

      cisSwTeam.createChildTeamDef("CIS Requirements") //
         .and(CoreAttributeTypes.StaticId, "cis.reqirements") //
         .andLeads(DemoUsers.Joe_Smith) //
         .andMembers(DemoUsers.Joe_Smith) //
         .andWorkDef(DemoArtifactToken.WorkDef_Team_Demo_Req) //
         .andTeamWorkflowArtifactType(DemoArtifactTypes.DemoReqTeamWorkflow);

      // CIS SW Actionable Items
      IAtsConfigTxActionableItem cisSwAI = topActionableItem.createChildActionableItem(DemoArtifactToken.CIS_CSCI) //
         .andActionable(false);

      cisSwAI.createChildActionableItem("CIS Code") //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.CIS_Code);

      cisSwAI.createChildActionableItem("CIS Requirements") //
         .andActionable(true) //
         .andTeamDef("CIS Requirements");

      cisSwAI.createChildActionableItem(DemoArtifactToken.CIS_Test_AI) //
         .andActionable(true) //
         .andTeamDef(DemoArtifactToken.CIS_Test);

      cisSwAI.createChildActionableItem("CIS SW Design") //
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
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("configureForParallelCommit");

      IAtsVersion sawBld1Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_3);

      changes.relate(sawBld1Ver, AtsRelationTypes.ParallelVersion_Child, sawBld2Ver);

      changes.relate(sawBld2Ver, AtsRelationTypes.ParallelVersion_Child, sawBld1Ver);
      changes.relate(sawBld2Ver, AtsRelationTypes.ParallelVersion_Child, sawBld3Ver);

      changes.execute();
   }
}
