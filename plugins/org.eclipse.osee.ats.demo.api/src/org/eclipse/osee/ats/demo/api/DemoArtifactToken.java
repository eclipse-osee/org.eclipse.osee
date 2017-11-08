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

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public final class DemoArtifactToken {

   public static final ArtifactToken Process_Team =
      TokenFactory.createArtifactToken(55170736, "Process_Team", AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken Tools_Team =
      TokenFactory.createArtifactToken(4830548, "Tools_Team", AtsArtifactTypes.TeamDefinition);

   public static final ArtifactToken SAW_Program =
      TokenFactory.createArtifactToken(19196003, "SAW Program", AtsArtifactTypes.Program);
   public static final ArtifactToken SAW_HW =
      TokenFactory.createArtifactToken(2876840, "SAW HW", AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken SAW_Code =
      TokenFactory.createArtifactToken(30013695, "SAW Code", AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken SAW_Test =
      TokenFactory.createArtifactToken(31608252, "SAW Test", AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken SAW_SW_Design =
      TokenFactory.createArtifactToken(138220, "SAW SW Design", AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken SAW_Requirements =
      TokenFactory.createArtifactToken(20592, "SAW Requirements", AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken SAW_SW =
      TokenFactory.createArtifactToken(3902389, "SAW SW", AtsArtifactTypes.TeamDefinition);

   public static final ArtifactToken SAW_Agile_Team =
      TokenFactory.createArtifactToken(111, "SAW Agile Team", AtsArtifactTypes.AgileTeam);
   public static final ArtifactToken SAW_Sprint_1 =
      TokenFactory.createArtifactToken(222, "SAW Sprint 1", AtsArtifactTypes.AgileSprint);
   public static final ArtifactToken SAW_Sprint_2 =
      TokenFactory.createArtifactToken(333, "SAW Sprint 2", AtsArtifactTypes.AgileSprint);
   public static final ArtifactToken SAW_Backlog =
      TokenFactory.createArtifactToken(444, "SAW Backlog", AtsArtifactTypes.AgileBacklog);

   public static final ArtifactToken CIS_Agile_Team =
      TokenFactory.createArtifactToken(7227, "CIS Agile Team", AtsArtifactTypes.AgileTeam);
   public static final ArtifactToken CIS_Backlog =
      TokenFactory.createArtifactToken(8228, "CIS Backlog", AtsArtifactTypes.AgileBacklog);

   public static final ArtifactToken RD_Agile_Program =
      TokenFactory.createArtifactToken(555, "RD Program", AtsArtifactTypes.AgileProgram);
   public static final ArtifactToken RD_Program_Backlog =
      TokenFactory.createArtifactToken(48784848, "RD Program Backlog", AtsArtifactTypes.AgileProgramBacklog);
   public static final ArtifactToken RD_Program_Backlog_Item_1 =
      TokenFactory.createArtifactToken(894868989, "Do this step first", AtsArtifactTypes.AgileProgramBacklogItem);
   public static final ArtifactToken RD_Program_Backlog_Item_2 =
      TokenFactory.createArtifactToken(121264651, "Do this step second", AtsArtifactTypes.AgileProgramBacklogItem);
   public static final ArtifactToken RD_Program_Backlog_Item_3 =
      TokenFactory.createArtifactToken(23236562, "Do this step third", AtsArtifactTypes.AgileProgramBacklogItem);

   public static final ArtifactToken RD_Program_Feature_Robot_Nav =
      TokenFactory.createArtifactToken(546565168, "Create Robot Navigation", AtsArtifactTypes.AgileProgramFeature);
   public static final ArtifactToken RD_Robot_Nav_Story_1 = TokenFactory.createArtifactToken(1234687984,
      "As a user I will move the robot forwards and backwards.", AtsArtifactTypes.AgileStory);
   public static final ArtifactToken RD_Robot_Nav_Story_2 = TokenFactory.createArtifactToken(432156515,
      "As a user I will move the robot left and right.", AtsArtifactTypes.AgileStory);

   public static final ArtifactToken RD_Program_Feature_Robot_Voice =
      TokenFactory.createArtifactToken(123456789, "Create Robot Voice", AtsArtifactTypes.AgileProgramFeature);

   public static final ArtifactToken SAW_HW_AI =
      TokenFactory.createArtifactToken(4687946, "SAW HW", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_CSCI_AI =
      TokenFactory.createArtifactToken(1866, "SAW CSCI", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken Timesheet_AI =
      TokenFactory.createArtifactToken(79465444, "Timesheet", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken Reader_AI =
      TokenFactory.createArtifactToken(132213123, "Reader", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken Adapter_AI =
      TokenFactory.createArtifactToken(456465465, "Adapter", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken CIS_Test_AI =
      TokenFactory.createArtifactToken(441657987, "CIS Test", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_Test_AI =
      TokenFactory.createArtifactToken(75881049, "SAW Test", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_Code_AI =
      TokenFactory.createArtifactToken(733306468, "SAW Code", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_Requirements_AI =
      TokenFactory.createArtifactToken(668954846, "SAW Req", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_SW_Design_AI =
      TokenFactory.createArtifactToken(98129283, "SAW SW Design", AtsArtifactTypes.ActionableItem);

   // Demo Group
   public static final ArtifactToken Test_Group =
      TokenFactory.createArtifactToken(46894461, "Demo Group", CoreArtifactTypes.UniversalGroup);

   // SAW_SW Versions
   public static final ArtifactToken SAW_Bld_1 =
      TokenFactory.createArtifactToken(2749182, "SAW_Bld_1", AtsArtifactTypes.Version);
   public static final ArtifactToken SAW_Bld_2 =
      TokenFactory.createArtifactToken(7632957, "SAW_Bld_2", AtsArtifactTypes.Version);
   public static final ArtifactToken SAW_Bld_3 =
      TokenFactory.createArtifactToken(577781, "SAW_Bld_3", AtsArtifactTypes.Version);

   public static final ArtifactToken CIS_Program =
      TokenFactory.createArtifactToken(8242414, "CIS Program", AtsArtifactTypes.Program);

   public static final ArtifactToken CIS_SW =
      TokenFactory.createArtifactToken(695910, "CIS_SW", AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken CIS_Code =
      TokenFactory.createArtifactToken(1629262, "CIS_Code", AtsArtifactTypes.TeamDefinition);
   public static final ArtifactToken CIS_Test =
      TokenFactory.createArtifactToken(541255, "CIS_Test", AtsArtifactTypes.TeamDefinition);

   public static final ArtifactToken Facilities_Team =
      TokenFactory.createArtifactToken(4811031, "Facilities_Team", CoreArtifactTypes.Folder);

   public static final ArtifactToken DemoPrograms =
      TokenFactory.createArtifactToken(90120, "Demo Programs", CoreArtifactTypes.Artifact);

   public static final ArtifactToken SAW_Code_Team_WorkPackage_01 =
      TokenFactory.createArtifactToken(38512616, "AZp8M1dPuESWYBPPbDgA", "Work Pkg 01", AtsArtifactTypes.WorkPackage);
   public static final ArtifactToken SAW_Code_Team_WorkPackage_02 =
      TokenFactory.createArtifactToken(513994, "AZp8M1d7TCJiBw6A5bgA", "Work Pkg 02", AtsArtifactTypes.WorkPackage);
   public static final ArtifactToken SAW_Code_Team_WorkPackage_03 =
      TokenFactory.createArtifactToken(304908, "AZp8M1em4EC1xE6bPEwA", "Work Pkg 03", AtsArtifactTypes.WorkPackage);

   public static final ArtifactToken SAW_Test_AI_WorkPackage_0A =
      TokenFactory.createArtifactToken(75666, "AZp8M1fSc1JwMDQBtLwA", "Work Pkg 0A", AtsArtifactTypes.WorkPackage);
   public static final ArtifactToken SAW_Test_AI_WorkPackage_0B =
      TokenFactory.createArtifactToken(281326, "AZp8M1hP81QOm6W9yNgA", "Work Pkg 0B", AtsArtifactTypes.WorkPackage);
   public static final ArtifactToken SAW_Test_AI_WorkPackage_0C =
      TokenFactory.createArtifactToken(8141323, "AZp8M1kvEGrRt9tybTwA", "Work Pkg 0C", AtsArtifactTypes.WorkPackage);

   public static final ArtifactToken SAW_Commited_Req_TeamWf = TokenFactory.createArtifactToken(68678945,
      DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken SAW_Commited_Code_TeamWf = TokenFactory.createArtifactToken(745689465,
      DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, DemoArtifactTypes.DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_Commited_Test_TeamWf = TokenFactory.createArtifactToken(4684523,
      DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, DemoArtifactTypes.DemoTestTeamWorkflow);
   public static final ArtifactToken SAW_Commited_SWDesign_TeamWf = TokenFactory.createArtifactToken(9784654,
      DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, AtsArtifactTypes.TeamWorkflow);

   public static final ArtifactToken SAW_UnCommited_Code_TeamWf = TokenFactory.createArtifactToken(295510263,
      DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, DemoArtifactTypes.DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_UnCommited_Test_TeamWf = TokenFactory.createArtifactToken(81679355,
      DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, DemoArtifactTypes.DemoTestTeamWorkflow);
   public static final ArtifactToken SAW_UnCommited_Req_TeamWf = TokenFactory.createArtifactToken(55313463,
      DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken SAW_UnCommited_SWDesign_TeamWf = TokenFactory.createArtifactToken(8879465,
      DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, AtsArtifactTypes.TeamWorkflow);

   public static final ArtifactToken SAW_UnCommitedConflicted_Code_TeamWf = TokenFactory.createArtifactToken(114579,
      DemoWorkflowTitles.SAW_UNCOMMITTED_CONFLICTED_REQT_CHANGES_FOR_DIAGRAM_VIEW,
      DemoArtifactTypes.DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_UnCommitedConflicted_Req_TeamWf = TokenFactory.createArtifactToken(9876413,
      DemoWorkflowTitles.SAW_UNCOMMITTED_CONFLICTED_REQT_CHANGES_FOR_DIAGRAM_VIEW,
      DemoArtifactTypes.DemoReqTeamWorkflow);

   public static final ArtifactToken SAW_NoBranch_Code_TeamWf = TokenFactory.createArtifactToken(8885445,
      DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, DemoArtifactTypes.DemoCodeTeamWorkflow);
   public static final ArtifactToken SAW_NoBranch_SWDesign_TeamWf = TokenFactory.createArtifactToken(7784565,
      DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, AtsArtifactTypes.TeamWorkflow);
   public static final ArtifactToken SAW_NoBranch_Req_TeamWf = TokenFactory.createArtifactToken(5564873,
      DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken SAW_NoBranch_Test_TeamWf = TokenFactory.createArtifactToken(11548766,
      DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW, DemoArtifactTypes.DemoTestTeamWorkflow);

   public static final ArtifactToken WorkaroundForGraphViewWorkflowForBld1_TeamWf = TokenFactory.createArtifactToken(
      468813246, DemoWorkflowTitles.WORKAROUND_FOR_GRAPH_VIEW_FOR_BLD_1_ACTION, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken WorkaroundForGraphViewWorkflowForBld2_TeamWf = TokenFactory.createArtifactToken(
      794546444, DemoWorkflowTitles.WORKAROUND_FOR_GRAPH_VIEW_FOR_BLD_2_ACTION, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken WorkaroundForGraphViewWorkflowForBld3_TeamWf = TokenFactory.createArtifactToken(
      184679646, DemoWorkflowTitles.WORKAROUND_FOR_GRAPH_VIEW_FOR_BLD_3_ACTION, DemoArtifactTypes.DemoReqTeamWorkflow);

   public static final ArtifactToken WorkingWithDiagramTreeWorkflowForBld1_TeamWf = TokenFactory.createArtifactToken(
      998798564, DemoWorkflowTitles.WORKAROUND_WITH_DIAGRAM_TREE_FOR_BLD_1_ACTION, AtsArtifactTypes.TeamWorkflow);
   public static final ArtifactToken WorkingWithDiagramTreeWorkflowForBld2_TeamWf = TokenFactory.createArtifactToken(
      312564659, DemoWorkflowTitles.WORKAROUND_WITH_DIAGRAM_TREE_FOR_BLD_2_ACTION, AtsArtifactTypes.TeamWorkflow);
   public static final ArtifactToken WorkingWithDiagramTreeWorkflowForBld3_TeamWf = TokenFactory.createArtifactToken(
      115469956, DemoWorkflowTitles.WORKAROUND_WITH_DIAGRAM_TREE_FOR_BLD_3_ACTION, AtsArtifactTypes.TeamWorkflow);

   public static final ArtifactToken ButtonSDoesntWorkOnHelp_TeamWf = TokenFactory.createArtifactToken(791613525,
      DemoWorkflowTitles.BUTTON_S_DOESNT_WORK_ON_HELP, AtsArtifactTypes.TeamWorkflow);
   public static final ArtifactToken ButtonWDoesntWorkOnSituationPage_TeamWf = TokenFactory.createArtifactToken(
      665156548, DemoWorkflowTitles.BUTTON_W_DOESNT_WORK_ON_SITUATION_PAGE, DemoArtifactTypes.DemoTestTeamWorkflow);
   public static final ArtifactToken CantLoadDiagramTree_TeamWf = TokenFactory.createArtifactToken(159753789,
      DemoWorkflowTitles.CANT_LOAD_DIAGRAM_TREE, DemoArtifactTypes.DemoTestTeamWorkflow);
   public static final ArtifactToken CantSeeTheGraphView_TeamWf = TokenFactory.createArtifactToken(444777888,
      DemoWorkflowTitles.CANT_SEE_THE_GRAPH_TREE, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken ProblemInDiagramTree_TeamWf = TokenFactory.createArtifactToken(789987789,
      DemoWorkflowTitles.PROBLEM_IN_DIAGRAM_TREE, DemoArtifactTypes.DemoTestTeamWorkflow);
   public static final ArtifactToken ProblemWithTheGraphView_TeamWf = TokenFactory.createArtifactToken(431354978,
      DemoWorkflowTitles.PROBLEM_WITH_THE_GRAPH_VIEW_ACTION, DemoArtifactTypes.DemoReqTeamWorkflow);
   public static final ArtifactToken ProblemWithTheUserWindow_TeamWf = TokenFactory.createArtifactToken(456794312,
      DemoWorkflowTitles.PROBLEM_WITH_THE_USER_WINDOW, AtsArtifactTypes.TeamWorkflow);

   private DemoArtifactToken() {
      // Constants
   }
}
