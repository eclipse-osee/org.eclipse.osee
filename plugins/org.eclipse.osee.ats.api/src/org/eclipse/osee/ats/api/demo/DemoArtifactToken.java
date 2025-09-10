/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.api.demo;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.DemoCodeTeamWorkflow;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.DemoReqTeamWorkflow;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.DemoTestTeamWorkflow;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import static org.eclipse.osee.framework.core.data.ArtifactToken.valueOf;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.ats.api.config.tx.AtsActionableItemArtifactToken;
import org.eclipse.osee.ats.api.config.tx.AtsProgramArtifactToken;
import org.eclipse.osee.ats.api.config.tx.AtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.AtsVersionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsActionableItemArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsProgramArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsVersionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;

/**
 * @author Donald G. Dunne
 */
public final class DemoArtifactToken {

   // @formatter:off

   //////////////////////////////////
   // DemoBranchRegressionTest
   //////////////////////////////////
   public static final ArtifactToken SwReqParaOnlyNoTask = ArtifactToken.valueOf(82251433L, "SW Req w/ Para Only Chg - No task should be created", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirementMsWord);
   public static final ArtifactToken MsWordHeadingNoTask = ArtifactToken.valueOf(23238598L, "MS Word Heading - No task should be created", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken InBranchArtifactToDelete = ArtifactToken.valueOf(56565656, "In-Branch Artifact to Delete", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirementMsWord);
   public static final ArtifactToken SystemReqArtifact = ArtifactToken.valueOf(45454545, "System Req Artifact", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SystemRequirementMsWord);

   //////////////////////////////////
   // Impl Details Configuration
   //////////////////////////////////
   public static final ArtifactToken RobotInterfaceHeading = ArtifactToken.valueOf(659132, "Robot Interface Heading", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken RobotUserInterfaceHeading = ArtifactToken.valueOf(54212, "Robot User Interfaces", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken RobotAdminUserInterface = ArtifactToken.valueOf(789134, "Robot Admin User Interface", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken RobotAdminUserInterfaceImpl = ArtifactToken.valueOf(1235445, "Robot Admin UI Impl Details", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetailsMsWord);
   public static final ArtifactToken RobotUserInterface = ArtifactToken.valueOf(9798614, "Robot User Interface", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken RobotUserInterfaceImpl = ArtifactToken.valueOf(654969, "Robot UI Impl Details", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetailsMsWord);
   public static final ArtifactToken RobotCollabDetails = ArtifactToken.valueOf(324516, "Robot Collaberation Details", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetailsMsWord);
   public static final ArtifactToken EventsDetailHeader = ArtifactToken.valueOf(165416, "Events Detail Header", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken EventsDetails = ArtifactToken.valueOf(165131, "Events Details", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetailsMsWord);
   public static final ArtifactToken VirtualFixDetailHeader = ArtifactToken.valueOf(456135, "VirtualFixDetailHeader", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken VirtualFixDetails = ArtifactToken.valueOf(847323, "Virtual Fix Details", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetailsMsWord);
   public static final ArtifactToken VirtualFixDetailReq = ArtifactToken.valueOf(789216, "Virtual Fix Detail Requirements", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirementMsWord);
   public static final ArtifactToken VirtualFixDetailReqImplementation = ArtifactToken.valueOf(963692, "Virtual Fix Detail Requirements Implementation", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetailsMsWord);


   //////////////////////////////////
   // SAW PL Configuration
   //////////////////////////////////

   public static final IAtsProgramArtifactToken SAW_PL_Program = AtsProgramArtifactToken.valueOf(1917773L, "SAW PL Program");

   //////////////////////////////////
   // SAW PL Team Definitions
   //////////////////////////////////
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(283478888L, "SAW PL");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_CR_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(35712349L, "SAW PL Change Request");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_PR_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(1243526234L, "SAW PL Problem Report");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_HW_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(123913578L, "SAW PL HW");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_Code_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(123413478L, "SAW PL Code");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_Test_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(228828L, "SAW PL Test");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_SW_Design_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(29292929L, "SAW PL SW Design");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_Requirements_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(86758678L, "SAW PL Requirements");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_Requirements_Simple_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(23971902L, "SAW PL Requirements Simple");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_ARB_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(150338509L, "SAW PL ARB");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_MIM_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(1791969525L, "SAW PL MIM");

   //////////////////////////////////
   // SAW PL Actionable Items
   //////////////////////////////////
   public static final IAtsActionableItemArtifactToken SAW_PL_CSCI_AI = AtsActionableItemArtifactToken.valueOf(3234255L, "SAW PL CSCI");
   public static final IAtsActionableItemArtifactToken SAW_PL_CR_AI = AtsActionableItemArtifactToken.valueOf(428487777L, "SAW PL Change Request");
   public static final IAtsActionableItemArtifactToken SAW_PL_PR_AI = AtsActionableItemArtifactToken.valueOf(8989009812L, "SAW PL Problem Report");
   public static final IAtsActionableItemArtifactToken SAW_PL_HW_AI = AtsActionableItemArtifactToken.valueOf(95849383L, "SAW PL HW");
   public static final IAtsActionableItemArtifactToken PL_Adapter_AI = AtsActionableItemArtifactToken.valueOf(456465465L, "Adapter");
   public static final IAtsActionableItemArtifactToken SAW_PL_Test_AI = AtsActionableItemArtifactToken.valueOf(343434343L, "SAW PL Test");
   public static final IAtsActionableItemArtifactToken SAW_PL_Code_AI = AtsActionableItemArtifactToken.valueOf(4564563L, "SAW PL Code");
   public static final IAtsActionableItemArtifactToken SAW_PL_Requirements_AI = AtsActionableItemArtifactToken.valueOf(88837304L, "SAW PL Requirements");
   public static final IAtsActionableItemArtifactToken SAW_PL_Requirements_Simple_AI = AtsActionableItemArtifactToken.valueOf(25391621L, "SAW PL Requirements Simple");
   public static final IAtsActionableItemArtifactToken SAW_PL_SW_Design_AI = AtsActionableItemArtifactToken.valueOf(1122334455L, "SAW PL SW Design");
   public static final IAtsActionableItemArtifactToken SAW_PL_ARB_AI = AtsActionableItemArtifactToken.valueOf(669130985L, "SAW PL ARB");
   public static final IAtsActionableItemArtifactToken SAW_PL_MIM_AI = AtsActionableItemArtifactToken.valueOf(1791969526L, "SAW PL MIM");

   //////////////////////////////////
   // SAW PL Versions
   //////////////////////////////////
   public static final IAtsVersionArtifactToken SAW_Product_Line = AtsVersionArtifactToken.valueOf(33244422L, "SAW Product Line");
   public static final IAtsVersionArtifactToken SAW_PL_Hardening_Branch = AtsVersionArtifactToken.valueOf(44432231L, "SAW PL Hardening Branch");
   public static final IAtsVersionArtifactToken SAW_PL_SBVT1 = AtsVersionArtifactToken.valueOf(6234243L, "SAW_PL_SBVT1");
   public static final IAtsVersionArtifactToken SAW_PL_SBVT2 = AtsVersionArtifactToken.valueOf(6234244L, "SAW_PL_SBVT2");
   public static final IAtsVersionArtifactToken SAW_PL_SBVT3 = AtsVersionArtifactToken.valueOf(6234245L, "SAW_PL_SBVT3");


   //////////////////////////////////
   // SAW Leagacy Configuration
   //////////////////////////////////
   public static final IAtsProgramArtifactToken SAW_Program = AtsProgramArtifactToken.valueOf(19196003L, "SAW Program");

   //////////////////////////////////
   // SAW Team Definitions
   //////////////////////////////////
   public static final IAtsTeamDefinitionArtifactToken SAW_HW = AtsTeamDefinitionArtifactToken.valueOf(2876840L, "SAW HW");
   public static final IAtsTeamDefinitionArtifactToken SAW_Code = AtsTeamDefinitionArtifactToken.valueOf(30013695L, "SAW Code");
   public static final IAtsTeamDefinitionArtifactToken SAW_Test = AtsTeamDefinitionArtifactToken.valueOf(31608252L, "SAW Test");
   public static final IAtsTeamDefinitionArtifactToken SAW_SW_Design = AtsTeamDefinitionArtifactToken.valueOf(138220L, "SAW SW Design");
   public static final IAtsTeamDefinitionArtifactToken SAW_Requirements = AtsTeamDefinitionArtifactToken.valueOf(20592L, "SAW Requirements");
   public static final IAtsTeamDefinitionArtifactToken SAW_Systems_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(23452333L, "SAW Systems");
   public static final IAtsTeamDefinitionArtifactToken SAW_SubSystems_TeamDef = AtsTeamDefinitionArtifactToken.valueOf(4354624L, "SAW Sub-Systems");
   public static final IAtsTeamDefinitionArtifactToken SAW_SW = AtsTeamDefinitionArtifactToken.valueOf(3902389L, "SAW SW");

   //////////////////////////////////
   // SAW Actionable Items
   //////////////////////////////////
   public static final IAtsActionableItemArtifactToken SAW_HW_AI = AtsActionableItemArtifactToken.valueOf(4687946L, "SAW HW");
   public static final IAtsActionableItemArtifactToken SAW_CSCI_AI = AtsActionableItemArtifactToken.valueOf(1866L, "SAW CSCI");
   public static final IAtsActionableItemArtifactToken Adapter_AI = AtsActionableItemArtifactToken.valueOf(456465465L, "Adapter");
   public static final IAtsActionableItemArtifactToken SAW_Test_AI = AtsActionableItemArtifactToken.valueOf(75881049L, "SAW Test");
   public static final IAtsActionableItemArtifactToken SAW_Code_AI = AtsActionableItemArtifactToken.valueOf(733306468L, "SAW Code");
   public static final IAtsActionableItemArtifactToken SAW_Requirements_AI = AtsActionableItemArtifactToken.valueOf(668954846L, "SAW Requirements");
   public static final IAtsActionableItemArtifactToken SAW_Systems_AI = AtsActionableItemArtifactToken.valueOf(42662435L, "SAW Systems");
   public static final IAtsActionableItemArtifactToken SAW_SubSystems_AI = AtsActionableItemArtifactToken.valueOf(323423423L, "SAW Sub-Systems");
   public static final IAtsActionableItemArtifactToken SAW_SW_Design_AI = AtsActionableItemArtifactToken.valueOf(98129283L, "SAW SW Design");

   //////////////////////////////////
   // SAW_SW Versions
   //////////////////////////////////
   public static final IAtsVersionArtifactToken SAW_Bld_1 = AtsVersionArtifactToken.valueOf(2749182L, "SAW_Bld_1");
   public static final IAtsVersionArtifactToken SAW_Bld_2 = AtsVersionArtifactToken.valueOf(7632957L, "SAW_Bld_2");
   public static final IAtsVersionArtifactToken SAW_Bld_3 = AtsVersionArtifactToken.valueOf(577781L, "SAW_Bld_3");

   //////////////////////////////////
   //SAW Agile
   //////////////////////////////////
   public static final ArtifactToken SAW_Agile_Team = ArtifactToken.valueOf(111, "SAW Agile Team", COMMON, AtsArtifactTypes.AgileTeam);
   public static final ArtifactToken SAW_Sprint_1 = ArtifactToken.valueOf(222, "SAW Sprint 1", COMMON, AtsArtifactTypes.AgileSprint);
   public static final ArtifactToken SAW_Sprint_2 = ArtifactToken.valueOf(333, "SAW Sprint 2", COMMON, AtsArtifactTypes.AgileSprint);
   public static final ArtifactToken SAW_Backlog = ArtifactToken.valueOf(444, "SAW Backlog", COMMON, AtsArtifactTypes.AgileBacklog);

   //////////////////////////////////
   // SAW Actions
   //////////////////////////////////
   public static final String SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW =
      "SAW (committed) Reqt Changes for Diagram View";
   public static final ArtifactToken SAW_Commited_Req_TeamWf = ArtifactToken.valueOf(68678945, SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoReqTeamWorkflow);
   public static final ArtifactToken SAW_Commited_Code_TeamWf = ArtifactToken.valueOf(745689465, SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_Commited_Test_TeamWf = ArtifactToken.valueOf(4684523, SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoTestTeamWorkflow);
   public static final ArtifactToken SAW_Commited_SWDesign_TeamWf = ArtifactToken.valueOf(9784654, SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, TeamWorkflow);

   public static final String SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW =
      "SAW (uncommitted) More Reqt Changes for Diagram View";
   public static final ArtifactToken SAW_UnCommited_Code_TeamWf = ArtifactToken.valueOf(295510263, SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_UnCommited_Test_TeamWf = ArtifactToken.valueOf(81679355, SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoTestTeamWorkflow);
   public static final ArtifactToken SAW_UnCommited_Req_TeamWf = ArtifactToken.valueOf(55313463, SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoReqTeamWorkflow);
   public static final ArtifactToken SAW_UnCommited_SWDesign_TeamWf = ArtifactToken.valueOf(8879465, SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, TeamWorkflow);

   public static final String SAW_UNCOMMITTED_CONFLICTED_REQT_CHANGES_FOR_DIAGRAM_VIEW =
      "SAW (uncommitted-conflicted) More Requirement Changes for Diagram View";
   public static final ArtifactToken SAW_UnCommitedConflicted_Code_TeamWf = ArtifactToken.valueOf(114579, SAW_UNCOMMITTED_CONFLICTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_UnCommitedConflicted_Req_TeamWf = ArtifactToken.valueOf(9876413, SAW_UNCOMMITTED_CONFLICTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoReqTeamWorkflow);

   public static final String SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW =
      "SAW (no-branch) Even More Requirement Changes for Diagram View";
   public static final ArtifactToken SAW_NoBranch_Code_TeamWf = ArtifactToken.valueOf(8885445, SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_NoBranch_SWDesign_TeamWf = ArtifactToken.valueOf(7784565, SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, TeamWorkflow);
   public static final ArtifactToken SAW_NoBranch_Req_TeamWf = ArtifactToken.valueOf(5564873, SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoReqTeamWorkflow);
   public static final ArtifactToken SAW_NoBranch_Test_TeamWf = ArtifactToken.valueOf(11548766, SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoTestTeamWorkflow);

   public static final ArtifactToken SAW_Access_Control_Req_TeamWf = ArtifactToken.valueOf(32482, "SAW Access Ctrl Test - Req", AtsArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken SAW_Access_Control_Code_TeamWf = ArtifactToken.valueOf(32483, "SAW Access Ctrl Test - Code", AtsArtifactTypes.DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_Access_Control_Test_TeamWf = ArtifactToken.valueOf(32484, "SAW Access Ctrl Test - Test", AtsArtifactTypes.DemoTestTeamWorkflow);

   public static final ArtifactToken SAW_NotesAnnotations_Code_TeamWf = ArtifactToken.valueOf(45213456, "SAW Notes and Annotations", COMMON, AtsArtifactTypes.DemoCodeTeamWorkflow);

   //////////////////////////////////
   // RD Agile Configuration
   //////////////////////////////////

   public static final ArtifactToken RD_Agile_Program = ArtifactToken.valueOf(555, "RD Program", COMMON, AtsArtifactTypes.AgileProgram);
   public static final ArtifactToken RD_Program_Backlog = ArtifactToken.valueOf(48784848, "RD Program Backlog", COMMON, AtsArtifactTypes.AgileProgramBacklog);
   public static final ArtifactToken RD_Program_Backlog_Item_1 = ArtifactToken.valueOf(894868989, "Do this step first", COMMON, AtsArtifactTypes.AgileProgramBacklogItem);
   public static final ArtifactToken RD_Program_Backlog_Item_2 = ArtifactToken.valueOf(121264651, "Do this step second", COMMON, AtsArtifactTypes.AgileProgramBacklogItem);
   public static final ArtifactToken RD_Program_Backlog_Item_3 = ArtifactToken.valueOf(23236562, "Do this step third", COMMON, AtsArtifactTypes.AgileProgramBacklogItem);

   public static final ArtifactToken RD_Program_Feature_Robot_Nav = ArtifactToken.valueOf(546565168, "Create Robot Navigation", COMMON, AtsArtifactTypes.AgileProgramFeature);
   public static final ArtifactToken RD_Robot_Nav_Story_1 = ArtifactToken.valueOf(1234687984, "As a user I will move the robot forwards and backwards.", COMMON, AtsArtifactTypes.AgileStory);
   public static final ArtifactToken RD_Robot_Nav_Story_2 = ArtifactToken.valueOf(432156515, "As a user I will move the robot left and right.", COMMON, AtsArtifactTypes.AgileStory);
   public static final ArtifactToken RD_Robot_Nav_Story_3 = ArtifactToken.valueOf(794961344, "As a user I will move the robot up and down.", COMMON, AtsArtifactTypes.AgileStory);

   public static final ArtifactToken RD_Program_Feature_Robot_Voice = ArtifactToken.valueOf(123456789, "Create Robot Voice", COMMON, AtsArtifactTypes.AgileProgramFeature);

   //////////////////////////////////
   // CIS Program Configuration
   //////////////////////////////////

   public static final IAtsVersionArtifactToken CIS_Bld_1 = AtsVersionArtifactToken.valueOf(34523466L, "CIS_Bld_1");

   public static final IAtsProgramArtifactToken CIS_Program = AtsProgramArtifactToken.valueOf(8242414L, "CIS Program");

   public static final ArtifactToken CIS_Agile_Team = ArtifactToken.valueOf(7227, "CIS Agile Team", COMMON, AtsArtifactTypes.AgileTeam);
   public static final ArtifactToken CIS_Backlog = ArtifactToken.valueOf(8228, "CIS Backlog", COMMON, AtsArtifactTypes.AgileBacklog);

   public static final IAtsTeamDefinitionArtifactToken CIS_SW = AtsTeamDefinitionArtifactToken.valueOf(695910L, "CIS SW");
   public static final IAtsTeamDefinitionArtifactToken CIS_Code = AtsTeamDefinitionArtifactToken.valueOf(1629262L, "CIS Code");
   public static final IAtsTeamDefinitionArtifactToken CIS_Test = AtsTeamDefinitionArtifactToken.valueOf(541255L, "CIS Test");

   public static final IAtsActionableItemArtifactToken CIS_Test_AI = AtsActionableItemArtifactToken.valueOf(441657987L, "CIS Test");
   public static final IAtsActionableItemArtifactToken CIS_CSCI = AtsActionableItemArtifactToken.valueOf(442342798L, "CIS CSCI");

   //////////////////////////////////
   // Markdown Requirement Images
   //////////////////////////////////

   public static final ArtifactToken SAWTSR_Image_Markdown = ArtifactToken.valueOf(6829343659904028894L, "SAWTSR", DemoBranches.SAW_Bld_1, CoreArtifactTypes.Image);
   public static final ArtifactToken Robot_Data_Flow_Image_Markdown = ArtifactToken.valueOf(8446203177483923452L, "Robot Data Flow", DemoBranches.SAW_Bld_1, CoreArtifactTypes.Image);
   public static final ArtifactToken C_Image_Markdown = ArtifactToken.valueOf(1646203177483523742L, "C", DemoBranches.SAW_Bld_1, CoreArtifactTypes.Image);

   //////////////////////////////////
   // Other
   //////////////////////////////////

   public static final ArtifactToken Test_Group = ArtifactToken.valueOf(46894461, "Demo Group", COMMON, CoreArtifactTypes.UniversalGroup);
   public static final IAtsTeamDefinitionArtifactToken Process_Team = AtsTeamDefinitionArtifactToken.valueOf(55170736L, "Process_Team");

   //////////////////////////////////
   // Markdown Software Requirements
   //////////////////////////////////

   public static final ArtifactToken RobotApiSwMarkdown = ArtifactToken.valueOf(1995841667264415544L, "Robot API - Markdown", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirementMarkdown);
   public static final ArtifactToken VirtualFixtures = ArtifactToken.valueOf(1633658933L, "Virtual Fixtures", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirementMarkdown);
   public static final ArtifactToken IndividualRobotEvents = ArtifactToken.valueOf(1734668983L, "Individual Robot Events", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirementMarkdown);

   //////////////////////////////////
   // Markdown System Requirements
   //////////////////////////////////

   public static final ArtifactToken RobotCameraVisualization = ArtifactToken.valueOf(1970889096L, "Robot Camera Visualization", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SystemRequirementMarkdown);

   //////////////////////////////////
   // Word Template Content to Markdown Conversion Requirements
   //////////////////////////////////

   public static final ArtifactToken Folder_WtcToMarkdownConversion = ArtifactToken.valueOf(244629567L, "Folder_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.Folder);

   public static final ArtifactToken BoldItalicsUnderline_WtcToMarkdownConversion = ArtifactToken.valueOf(452126497L, "BoldItalicsUnderline_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);
   public static final ArtifactToken ArtifactLink_WtcToMarkdownConversion = ArtifactToken.valueOf(1277605003L, "ArtifactLink_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);
   public static final ArtifactToken ImageLinkWithCaption_WtcToMarkdownConversion = ArtifactToken.valueOf(233639558L, "ImageLinkWithCaption_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);
   public static final ArtifactToken Tab_WtcToMarkdownConversion = ArtifactToken.valueOf(1540361868L, "Tab_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);
   public static final ArtifactToken BulletedList1_WtcToMarkdownConversion = ArtifactToken.valueOf(1474871531L, "BulletedList1_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);
   public static final ArtifactToken BulletedList2_WtcToMarkdownConversion = ArtifactToken.valueOf(380524388L, "BulletedList2_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);
   public static final ArtifactToken SubscriptSuperscript_WtcToMarkdownConversion = ArtifactToken.valueOf(1189512800L, "SubscriptSuperscript_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);
   public static final ArtifactToken TableSimpleCells_WtcToMarkdownConversion = ArtifactToken.valueOf(1190087762L, "TableSimpleCells_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);
   public static final ArtifactToken TableMergeHeaderCells_WtcToMarkdownConversion = ArtifactToken.valueOf(206312175L, "TableMergeHeaderCells_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);
   public static final ArtifactToken NumberedList_WtcToMarkdownConversion = ArtifactToken.valueOf(925274269L, "NumberedList_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);
   public static final ArtifactToken Header_WtcToMarkdownConversion = ArtifactToken.valueOf(578119283L, "Header_WtcToMarkdownConversion", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SubsystemDesignMsWord);

   //////////////////////////////////
   // System Safety
   //////////////////////////////////

   public static final IAtsTeamDefinitionArtifactToken System_Safety_Team = AtsTeamDefinitionArtifactToken.valueOf(8934893L, "System Safety");
   public static final IAtsActionableItemArtifactToken System_Safety_Ai = AtsActionableItemArtifactToken.valueOf(2323232L, "System Safety");

   //////////////////////////////////
   // Tools Team
   //////////////////////////////////

   public static final IAtsTeamDefinitionArtifactToken Tools_Team = AtsTeamDefinitionArtifactToken.valueOf(4830548L, "Tools");
   public static final IAtsTeamDefinitionArtifactToken Website_Team = AtsTeamDefinitionArtifactToken.valueOf(2235542L, "Website");
   public static final IAtsActionableItemArtifactToken Tools_Ai = AtsActionableItemArtifactToken.valueOf(222222343L, "Tools");
   public static final IAtsActionableItemArtifactToken Reader_AI = AtsActionableItemArtifactToken.valueOf(132213123L, "Reader");
   public static final IAtsActionableItemArtifactToken Timesheet_AI = AtsActionableItemArtifactToken.valueOf(79465444L, "Timesheet");

   //////////////////////////////////
   // Facilities
   //////////////////////////////////

   public static final IAtsTeamDefinitionArtifactToken Facilities_Team = AtsTeamDefinitionArtifactToken.valueOf(4811031L, "Facilities");
   public static final IAtsTeamDefinitionArtifactToken Facilities_IT_Team = AtsTeamDefinitionArtifactToken.valueOf(2222231L, "IT Team");
   public static final IAtsActionableItemArtifactToken Facilities_Ai = AtsActionableItemArtifactToken.valueOf(48483928L, "Facilities");

   //////////////////////////////////
   // Actions / Team Workflows
   //////////////////////////////////

   public static final ArtifactToken WorkaroundForGraphViewForBld1_TeamWf = valueOf(468813246, "Workaround for Graph View for SAW_Bld_1", COMMON, DemoReqTeamWorkflow);
   public static final ArtifactToken WorkaroundForGraphViewForBld2_TeamWf = valueOf(794546444, "Workaround for Graph View for SAW_Bld_2", COMMON, DemoReqTeamWorkflow);
   public static final ArtifactToken WorkaroundForGraphViewForBld3_TeamWf = valueOf(184679646, "Workaround for Graph View for SAW_Bld_3", COMMON, DemoReqTeamWorkflow);
   public static final ArtifactToken WorkingWithDiagramTreeForBld1_TeamWf = valueOf(998798564, "Working with Diagram Tree for SAW_Bld_1", COMMON, TeamWorkflow);
   public static final ArtifactToken WorkingWithDiagramTreeForBld2_TeamWf = valueOf(312564659, "Working with Diagram Tree for SAW_Bld_2", COMMON, TeamWorkflow);
   public static final ArtifactToken WorkingWithDiagramTreeForBld3_TeamWf = valueOf(115469956, "Working with Diagram Tree for SAW_Bld_3", COMMON, TeamWorkflow);
   public static final ArtifactToken ButtonSDoesntWorkOnHelp_TeamWf = valueOf(791613525, "Button S doesn't work on help", COMMON, TeamWorkflow);
   public static final ArtifactToken ButtonWDoesntWorkOnSituationPage_TeamWf = valueOf(665156548, "Button W doesn't work on Situation Page", COMMON, DemoTestTeamWorkflow);
   public static final ArtifactToken CantLoadDiagramTree_TeamWf = valueOf(159753789, "Can't load Diagram Tree", COMMON, DemoTestTeamWorkflow);
   public static final ArtifactToken CantSeeTheGraphView_TeamWf = valueOf(444777888, "Can't see the Graph View", COMMON, DemoReqTeamWorkflow);
   public static final ArtifactToken ProblemInTree_TeamWf = valueOf(789987789, "Problem in Diagram Tree", COMMON, DemoTestTeamWorkflow);
   public static final ArtifactToken ProblemWithTheGraphView_TeamWf = valueOf(431354978, "Problem with the Graph View", COMMON, DemoReqTeamWorkflow);
   public static final ArtifactToken ProblemWithTheUserWindow_TeamWf = valueOf(456794312, "Problem with the user window", COMMON, TeamWorkflow);


   public static final ArtifactToken PeerReview2 = ArtifactToken.valueOf(414911237L, "2 - Peer Review algorithm used in code", COMMON, TeamWorkflow);
   // @formatter:on

   private DemoArtifactToken() {
      // Constants
   }
}
