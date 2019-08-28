/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.demo;

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
   // Impl Details Configuration
   //////////////////////////////////
   public static final ArtifactToken RobotInterfaceHeading = ArtifactToken.valueOf(659132, "Robot Interface Heading", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken RobotUserInterfaceHeading = ArtifactToken.valueOf(54212, "Robot User Interfaces", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken RobotAdminUserInterface = ArtifactToken.valueOf(789134, "Robot Admin User Interface", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken RobotAdminUserInterfaceImpl = ArtifactToken.valueOf(1235445, "Robot Admin UI Impl Details", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetails);
   public static final ArtifactToken RobotUserInterface = ArtifactToken.valueOf(9798614, "Robot User Interface", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken RobotUserInterfaceImpl = ArtifactToken.valueOf(654969, "Robot UI Impl Details", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetails);
   public static final ArtifactToken RobotCollabDetails = ArtifactToken.valueOf(324516, "Robot Collaberation Details", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetails);
   public static final ArtifactToken EventsDetailHeader = ArtifactToken.valueOf(165416, "Events Detail Header", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken EventsDetails = ArtifactToken.valueOf(165131, "Events Details", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetails);
   public static final ArtifactToken VirtualFixDetailHeader = ArtifactToken.valueOf(456135, "VirtualFixDetailHeader", DemoBranches.SAW_Bld_1, CoreArtifactTypes.HeadingMsWord);
   public static final ArtifactToken VirtualFixDetails = ArtifactToken.valueOf(847323, "Virtual Fix Details", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetails);
   public static final ArtifactToken VirtualFixDetailReq = ArtifactToken.valueOf(789216, "Virtual Fix Detail Requirements", DemoBranches.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirement);
   public static final ArtifactToken VirtualFixDetailReqImplementation = ArtifactToken.valueOf(963692, "Virtual Fix Detail Requirements Implementation", DemoBranches.SAW_Bld_1, CoreArtifactTypes.ImplementationDetails);


   //////////////////////////////////
   // SAW PL Configuration
   //////////////////////////////////

   public static final IAtsProgramArtifactToken SAW_PL_Program = AtsProgramArtifactToken.valueOf(1917773L, "SAW PL Program");

   // SAW PL Team Definitions
   public static final IAtsTeamDefinitionArtifactToken SAW_PL = AtsTeamDefinitionArtifactToken.valueOf(283478888L, "SAW PL");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_HW = AtsTeamDefinitionArtifactToken.valueOf(123913578L, "SAW PL HW");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_Code = AtsTeamDefinitionArtifactToken.valueOf(123413478L, "SAW PL Code");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_Test = AtsTeamDefinitionArtifactToken.valueOf(228828L, "SAW PL Test");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_SW_Design = AtsTeamDefinitionArtifactToken.valueOf(29292929L, "SAW PL SW Design");
   public static final IAtsTeamDefinitionArtifactToken SAW_PL_Requirements = AtsTeamDefinitionArtifactToken.valueOf(86758678L, "SAW PL Requirements");

   // SAW PL Actionable Items
   public static final IAtsActionableItemArtifactToken SAW_PL_CSCI_AI = AtsActionableItemArtifactToken.valueOf(3234255L, "SAW PL CSCI");
   public static final IAtsActionableItemArtifactToken SAW_PL_HW_AI = AtsActionableItemArtifactToken.valueOf(95849383L, "SAW PL HW");
   public static final IAtsActionableItemArtifactToken PL_Adapter_AI = AtsActionableItemArtifactToken.valueOf(456465465L, "Adapter");
   public static final IAtsActionableItemArtifactToken SAW_PL_Test_AI = AtsActionableItemArtifactToken.valueOf(343434343L, "SAW PL Test");
   public static final IAtsActionableItemArtifactToken SAW_PL_Code_AI = AtsActionableItemArtifactToken.valueOf(4564563L, "SAW PL Code");
   public static final IAtsActionableItemArtifactToken SAW_PL_Requirements_AI = AtsActionableItemArtifactToken.valueOf(88837304L, "SAW PL Requirements");
   public static final IAtsActionableItemArtifactToken SAW_PL_SW_Design_AI = AtsActionableItemArtifactToken.valueOf(1122334455L, "SAW PL SW Design");

   public static final IAtsVersionArtifactToken SAW_Product_Line = AtsVersionArtifactToken.valueOf(33244422L, "SAW Product Line");
   public static final IAtsVersionArtifactToken SAW_Hardening_Branch = AtsVersionArtifactToken.valueOf(44432231L, "SAW Hardening Branch");

   //////////////////////////////////
   // SAW Leagacy Configuration
   //////////////////////////////////

   public static final IAtsProgramArtifactToken SAW_Program = AtsProgramArtifactToken.valueOf(19196003L, "SAW Program");

   // SAW Team Definitions
   public static final IAtsTeamDefinitionArtifactToken SAW_HW = AtsTeamDefinitionArtifactToken.valueOf(2876840L, "SAW HW");
   public static final IAtsTeamDefinitionArtifactToken SAW_Code = AtsTeamDefinitionArtifactToken.valueOf(30013695L, "SAW Code");
   public static final IAtsTeamDefinitionArtifactToken SAW_Test = AtsTeamDefinitionArtifactToken.valueOf(31608252L, "SAW Test");
   public static final IAtsTeamDefinitionArtifactToken SAW_SW_Design = AtsTeamDefinitionArtifactToken.valueOf(138220L, "SAW SW Design");
   public static final IAtsTeamDefinitionArtifactToken SAW_Requirements = AtsTeamDefinitionArtifactToken.valueOf(20592L, "SAW Requirements");
   public static final IAtsTeamDefinitionArtifactToken SAW_SW = AtsTeamDefinitionArtifactToken.valueOf(3902389L, "SAW SW");

   // SAW Actionable Items
   public static final IAtsActionableItemArtifactToken SAW_HW_AI = AtsActionableItemArtifactToken.valueOf(4687946L, "SAW HW");
   public static final IAtsActionableItemArtifactToken SAW_CSCI_AI = AtsActionableItemArtifactToken.valueOf(1866L, "SAW CSCI");
   public static final IAtsActionableItemArtifactToken Adapter_AI = AtsActionableItemArtifactToken.valueOf(456465465L, "Adapter");
   public static final IAtsActionableItemArtifactToken SAW_Test_AI = AtsActionableItemArtifactToken.valueOf(75881049L, "SAW Test");
   public static final IAtsActionableItemArtifactToken SAW_Code_AI = AtsActionableItemArtifactToken.valueOf(733306468L, "SAW Code");
   public static final IAtsActionableItemArtifactToken SAW_Requirements_AI = AtsActionableItemArtifactToken.valueOf(668954846L, "SAW Requirements");
   public static final IAtsActionableItemArtifactToken SAW_SW_Design_AI = AtsActionableItemArtifactToken.valueOf(98129283L, "SAW SW Design");

   // SAW_SW Versions
   public static final IAtsVersionArtifactToken SAW_Bld_1 = AtsVersionArtifactToken.valueOf(2749182L, "SAW_Bld_1");
   public static final IAtsVersionArtifactToken SAW_Bld_2 = AtsVersionArtifactToken.valueOf(7632957L, "SAW_Bld_2");
   public static final IAtsVersionArtifactToken SAW_Bld_3 = AtsVersionArtifactToken.valueOf(577781L, "SAW_Bld_3");

   //SAW Agile
   public static final ArtifactToken SAW_Agile_Team = ArtifactToken.valueOf(111, "SAW Agile Team", COMMON, AtsArtifactTypes.AgileTeam);
   public static final ArtifactToken SAW_Sprint_1 = ArtifactToken.valueOf(222, "SAW Sprint 1", COMMON, AtsArtifactTypes.AgileSprint);
   public static final ArtifactToken SAW_Sprint_2 = ArtifactToken.valueOf(333, "SAW Sprint 2", COMMON, AtsArtifactTypes.AgileSprint);
   public static final ArtifactToken SAW_Backlog = ArtifactToken.valueOf(444, "SAW Backlog", COMMON, AtsArtifactTypes.AgileBacklog);

   // SAW Actions
   public static final ArtifactToken SAW_Code_Team_WorkPackage_01 = ArtifactToken.valueOf(38512616, "AZp8M1dPuESWYBPPbDgA", "Work Pkg 01", COMMON, AtsArtifactTypes.WorkPackage);
   public static final ArtifactToken SAW_Code_Team_WorkPackage_02 = ArtifactToken.valueOf(513994, "AZp8M1d7TCJiBw6A5bgA", "Work Pkg 02", COMMON, AtsArtifactTypes.WorkPackage);
   public static final ArtifactToken SAW_Code_Team_WorkPackage_03 = ArtifactToken.valueOf(304908, "AZp8M1em4EC1xE6bPEwA", "Work Pkg 03", COMMON, AtsArtifactTypes.WorkPackage);

   public static final ArtifactToken SAW_Test_AI_WorkPackage_0A = ArtifactToken.valueOf(75666, "AZp8M1fSc1JwMDQBtLwA", "Work Pkg 0A", COMMON, AtsArtifactTypes.WorkPackage);
   public static final ArtifactToken SAW_Test_AI_WorkPackage_0B = ArtifactToken.valueOf(281326, "AZp8M1hP81QOm6W9yNgA", "Work Pkg 0B", COMMON, AtsArtifactTypes.WorkPackage);
   public static final ArtifactToken SAW_Test_AI_WorkPackage_0C = ArtifactToken.valueOf(8141323, "AZp8M1kvEGrRt9tybTwA", "Work Pkg 0C", COMMON, AtsArtifactTypes.WorkPackage);

   public static final ArtifactToken SAW_Commited_Req_TeamWf = ArtifactToken.valueOf(68678945, DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken SAW_Commited_Code_TeamWf = ArtifactToken.valueOf(745689465, DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_Commited_Test_TeamWf = ArtifactToken.valueOf(4684523, DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoTestTeamWorkflow);
   public static final ArtifactToken SAW_Commited_SWDesign_TeamWf = ArtifactToken.valueOf(9784654, DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, AtsArtifactTypes.TeamWorkflow);

   public static final ArtifactToken SAW_UnCommited_Code_TeamWf = ArtifactToken.valueOf(295510263, DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_UnCommited_Test_TeamWf = ArtifactToken.valueOf(81679355, DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoTestTeamWorkflow);
   public static final ArtifactToken SAW_UnCommited_Req_TeamWf = ArtifactToken.valueOf(55313463, DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken SAW_UnCommited_SWDesign_TeamWf = ArtifactToken.valueOf(8879465, DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, AtsArtifactTypes.TeamWorkflow);

   public static final ArtifactToken SAW_UnCommitedConflicted_Code_TeamWf = ArtifactToken.valueOf(114579, DemoWorkflowTitles.SAW_UNCOMMITTED_CONFLICTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_UnCommitedConflicted_Req_TeamWf = ArtifactToken.valueOf(9876413, DemoWorkflowTitles.SAW_UNCOMMITTED_CONFLICTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoReqTeamWorkflow);

   public static final ArtifactToken SAW_NoBranch_Code_TeamWf = ArtifactToken.valueOf(8885445, DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_NoBranch_SWDesign_TeamWf = ArtifactToken.valueOf(7784565, DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, AtsArtifactTypes.TeamWorkflow);
   public static final ArtifactToken SAW_NoBranch_Req_TeamWf = ArtifactToken.valueOf(5564873, DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken SAW_NoBranch_Test_TeamWf = ArtifactToken.valueOf(11548766, DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, COMMON, DemoArtifactTypes.DemoTestTeamWorkflow);

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

   public static final IAtsProgramArtifactToken CIS_Program = AtsProgramArtifactToken.valueOf(8242414L, "CIS Program");

   public static final ArtifactToken CIS_Agile_Team = ArtifactToken.valueOf(7227, "CIS Agile Team", COMMON, AtsArtifactTypes.AgileTeam);
   public static final ArtifactToken CIS_Backlog = ArtifactToken.valueOf(8228, "CIS Backlog", COMMON, AtsArtifactTypes.AgileBacklog);

   public static final IAtsTeamDefinitionArtifactToken CIS_SW = AtsTeamDefinitionArtifactToken.valueOf(695910L, "CIS SW");
   public static final IAtsTeamDefinitionArtifactToken CIS_Code = AtsTeamDefinitionArtifactToken.valueOf(1629262L, "CIS Code");
   public static final IAtsTeamDefinitionArtifactToken CIS_Test = AtsTeamDefinitionArtifactToken.valueOf(541255L, "CIS Test");

   public static final IAtsActionableItemArtifactToken CIS_Test_AI = AtsActionableItemArtifactToken.valueOf(441657987L, "CIS Test");
   public static final IAtsActionableItemArtifactToken CIS_CSCI = AtsActionableItemArtifactToken.valueOf(442342798L, "CIS CSCI");

   //////////////////////////////////
   // Other
   //////////////////////////////////

   public static final ArtifactToken Test_Group = ArtifactToken.valueOf(46894461, "Demo Group", COMMON, CoreArtifactTypes.UniversalGroup);
   public static final IAtsTeamDefinitionArtifactToken Process_Team = AtsTeamDefinitionArtifactToken.valueOf(55170736L, "Process_Team");

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

   public static final ArtifactToken WorkaroundForGraphViewWorkflowForBld1_TeamWf = ArtifactToken.valueOf(468813246, DemoWorkflowTitles.WORKAROUND_FOR_GRAPH_VIEW_FOR_BLD_1_ACTION, COMMON, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken WorkaroundForGraphViewWorkflowForBld2_TeamWf = ArtifactToken.valueOf(794546444, DemoWorkflowTitles.WORKAROUND_FOR_GRAPH_VIEW_FOR_BLD_2_ACTION, COMMON, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken WorkaroundForGraphViewWorkflowForBld3_TeamWf = ArtifactToken.valueOf(184679646, DemoWorkflowTitles.WORKAROUND_FOR_GRAPH_VIEW_FOR_BLD_3_ACTION, COMMON, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken WorkingWithDiagramTreeWorkflowForBld1_TeamWf = ArtifactToken.valueOf(998798564, DemoWorkflowTitles.WORKAROUND_WITH_DIAGRAM_TREE_FOR_BLD_1_ACTION, COMMON, AtsArtifactTypes.TeamWorkflow);
   public static final ArtifactToken WorkingWithDiagramTreeWorkflowForBld2_TeamWf = ArtifactToken.valueOf(312564659, DemoWorkflowTitles.WORKAROUND_WITH_DIAGRAM_TREE_FOR_BLD_2_ACTION, COMMON, AtsArtifactTypes.TeamWorkflow);
   public static final ArtifactToken WorkingWithDiagramTreeWorkflowForBld3_TeamWf = ArtifactToken.valueOf(115469956, DemoWorkflowTitles.WORKAROUND_WITH_DIAGRAM_TREE_FOR_BLD_3_ACTION, COMMON, AtsArtifactTypes.TeamWorkflow);
   public static final ArtifactToken ButtonSDoesntWorkOnHelp_TeamWf = ArtifactToken.valueOf(791613525, DemoWorkflowTitles.BUTTON_S_DOESNT_WORK_ON_HELP, COMMON, AtsArtifactTypes.TeamWorkflow);
   public static final ArtifactToken ButtonWDoesntWorkOnSituationPage_TeamWf = ArtifactToken.valueOf(665156548, DemoWorkflowTitles.BUTTON_W_DOESNT_WORK_ON_SITUATION_PAGE, COMMON, DemoArtifactTypes.DemoTestTeamWorkflow);
   public static final ArtifactToken CantLoadDiagramTree_TeamWf = ArtifactToken.valueOf(159753789, DemoWorkflowTitles.CANT_LOAD_DIAGRAM_TREE, COMMON, DemoArtifactTypes.DemoTestTeamWorkflow);
   public static final ArtifactToken CantSeeTheGraphView_TeamWf = ArtifactToken.valueOf(444777888, DemoWorkflowTitles.CANT_SEE_THE_GRAPH_TREE, COMMON, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken ProblemInDiagramTree_TeamWf = ArtifactToken.valueOf(789987789, DemoWorkflowTitles.PROBLEM_IN_DIAGRAM_TREE, COMMON, DemoArtifactTypes.DemoTestTeamWorkflow);
   public static final ArtifactToken ProblemWithTheGraphView_TeamWf = ArtifactToken.valueOf(431354978, DemoWorkflowTitles.PROBLEM_WITH_THE_GRAPH_VIEW_ACTION, COMMON, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken ProblemWithTheUserWindow_TeamWf = ArtifactToken.valueOf(456794312, DemoWorkflowTitles.PROBLEM_WITH_THE_USER_WINDOW, COMMON, AtsArtifactTypes.TeamWorkflow);
   // @formatter:on

   private DemoArtifactToken() {
      // Constants
   }
}
