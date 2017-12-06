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
package org.eclipse.osee.ats.demo.api;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public final class DemoArtifactToken {

   // @formatter:off
   public static final ArtifactToken Process_Team = ArtifactToken.valueOf(55170736, "Process_Team", COMMON, AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken Tools_Team = ArtifactToken.valueOf(4830548, "Tools_Team", COMMON, AtsArtifactTypes.TeamDefinition);

   public static final ArtifactToken SAW_Program = ArtifactToken.valueOf(19196003, "SAW Program", COMMON, AtsArtifactTypes.Program);
   public static final ArtifactToken SAW_HW = ArtifactToken.valueOf(2876840, "SAW HW", COMMON, AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken SAW_Code = ArtifactToken.valueOf(30013695, "SAW Code", COMMON, AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken SAW_Test = ArtifactToken.valueOf(31608252, "SAW Test", COMMON, AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken SAW_SW_Design = ArtifactToken.valueOf(138220, "SAW SW Design", COMMON, AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken SAW_Requirements = ArtifactToken.valueOf(20592, "SAW Requirements", COMMON, AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken SAW_SW = ArtifactToken.valueOf(3902389, "SAW SW", COMMON, AtsArtifactTypes.TeamDefinition);

   public static final ArtifactToken SAW_Agile_Team = ArtifactToken.valueOf(111, "SAW Agile Team", COMMON, AtsArtifactTypes.AgileTeam);
   public static final ArtifactToken SAW_Sprint_1 = ArtifactToken.valueOf(222, "SAW Sprint 1", COMMON, AtsArtifactTypes.AgileSprint);
   public static final ArtifactToken SAW_Sprint_2 = ArtifactToken.valueOf(333, "SAW Sprint 2", COMMON, AtsArtifactTypes.AgileSprint);
   public static final ArtifactToken SAW_Backlog = ArtifactToken.valueOf(444, "SAW Backlog", COMMON, AtsArtifactTypes.AgileBacklog);

   public static final ArtifactToken CIS_Agile_Team = ArtifactToken.valueOf(7227, "CIS Agile Team", COMMON, AtsArtifactTypes.AgileTeam);
   public static final ArtifactToken CIS_Backlog = ArtifactToken.valueOf(8228, "CIS Backlog", COMMON, AtsArtifactTypes.AgileBacklog);

   public static final ArtifactToken RD_Agile_Program = ArtifactToken.valueOf(555, "RD Program", COMMON, AtsArtifactTypes.AgileProgram);
   public static final ArtifactToken RD_Program_Backlog = ArtifactToken.valueOf(48784848, "RD Program Backlog", COMMON, AtsArtifactTypes.AgileProgramBacklog);
   public static final ArtifactToken RD_Program_Backlog_Item_1 = ArtifactToken.valueOf(894868989, "Do this step first", COMMON, AtsArtifactTypes.AgileProgramBacklogItem);
   public static final ArtifactToken RD_Program_Backlog_Item_2 = ArtifactToken.valueOf(121264651, "Do this step second", COMMON, AtsArtifactTypes.AgileProgramBacklogItem);
   public static final ArtifactToken RD_Program_Backlog_Item_3 = ArtifactToken.valueOf(23236562, "Do this step third", COMMON, AtsArtifactTypes.AgileProgramBacklogItem);

   public static final ArtifactToken RD_Program_Feature_Robot_Nav = ArtifactToken.valueOf(546565168, "Create Robot Navigation", COMMON, AtsArtifactTypes.AgileProgramFeature);
   public static final ArtifactToken RD_Robot_Nav_Story_1 = ArtifactToken.valueOf(1234687984, "As a user I will move the robot forwards and backwards.", COMMON, AtsArtifactTypes.AgileStory);
   public static final ArtifactToken RD_Robot_Nav_Story_2 = ArtifactToken.valueOf(432156515, "As a user I will move the robot left and right.", COMMON, AtsArtifactTypes.AgileStory);

   public static final ArtifactToken RD_Program_Feature_Robot_Voice = ArtifactToken.valueOf(123456789, "Create Robot Voice", COMMON, AtsArtifactTypes.AgileProgramFeature);

   public static final ArtifactToken SAW_HW_AI = ArtifactToken.valueOf(4687946, "SAW HW", COMMON, AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_CSCI_AI = ArtifactToken.valueOf(1866, "SAW CSCI", COMMON, AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken Timesheet_AI = ArtifactToken.valueOf(79465444, "Timesheet", COMMON, AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken Reader_AI = ArtifactToken.valueOf(132213123, "Reader", COMMON, AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken Adapter_AI = ArtifactToken.valueOf(456465465, "Adapter", COMMON, AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken CIS_Test_AI = ArtifactToken.valueOf(441657987, "CIS Test", COMMON, AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_Test_AI = ArtifactToken.valueOf(75881049, "SAW Test", COMMON, AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_Code_AI = ArtifactToken.valueOf(733306468, "SAW Code", COMMON, AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_Requirements_AI = ArtifactToken.valueOf(668954846, "SAW Req", COMMON, AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_SW_Design_AI = ArtifactToken.valueOf(98129283, "SAW SW Design", COMMON, AtsArtifactTypes.ActionableItem);

   // Demo Group
   public static final ArtifactToken Test_Group = ArtifactToken.valueOf(46894461, "Demo Group", COMMON, CoreArtifactTypes.UniversalGroup);

   // SAW_SW Versions
   public static final ArtifactToken SAW_Bld_1 = ArtifactToken.valueOf(2749182, "SAW_Bld_1", COMMON, AtsArtifactTypes.Version);
   public static final ArtifactToken SAW_Bld_2 = ArtifactToken.valueOf(7632957, "SAW_Bld_2", COMMON, AtsArtifactTypes.Version);
   public static final ArtifactToken SAW_Bld_3 = ArtifactToken.valueOf(577781, "SAW_Bld_3", COMMON, AtsArtifactTypes.Version);

   public static final ArtifactToken CIS_Program = ArtifactToken.valueOf(8242414, "CIS Program", COMMON, AtsArtifactTypes.Program);

   public static final ArtifactToken CIS_SW = ArtifactToken.valueOf(695910, "CIS_SW", COMMON, AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken CIS_Code = ArtifactToken.valueOf(1629262, "CIS_Code", COMMON, AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken CIS_Test = ArtifactToken.valueOf(541255, "CIS_Test", COMMON, AtsArtifactTypes.TeamDefinition);

   public static final ArtifactToken Facilities_Team = ArtifactToken.valueOf(4811031, "Facilities_Team", COMMON, CoreArtifactTypes.Folder);
   public static final ArtifactToken DemoPrograms = ArtifactToken.valueOf(90120, "Demo Programs", COMMON, CoreArtifactTypes.Artifact);

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
