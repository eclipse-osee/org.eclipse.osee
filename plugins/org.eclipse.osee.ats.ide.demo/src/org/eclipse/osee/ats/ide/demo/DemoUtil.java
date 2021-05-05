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

package org.eclipse.osee.ats.ide.demo;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;

/**
 * @author Roberto E. Escobar
 */
public class DemoUtil {

   private static String atsIds;
   public static List<String> Saw_Code_Committed_Task_Titles = Arrays.asList("Look into Graph View.",
      "Redesign how view shows values.", "Discuss new design with Senior Engineer", "Develop prototype",
      "Show prototype to management", "Create development plan", "Create test plan", "Make changes");
   public static List<String> Saw_Code_UnCommitted_Task_Titles =
      Arrays.asList("Document how Graph View works", "Update help contents", "Review new documentation",
         "Publish documentation to website", "Remove old viewer", "Deploy release");

   public static int getNumTasks() {
      return Saw_Code_Committed_Task_Titles.size() + Saw_Code_UnCommitted_Task_Titles.size();
   }

   private DemoUtil() {
      // Utility class
   }

   public static Result isDbPopulatedWithDemoData() {
      Collection<Artifact> robotArtifacts = ArtifactQuery.getArtifactListFromTypeAndName(
         CoreArtifactTypes.SoftwareRequirementMsWord, "Robot", SAW_Bld_1, QueryOption.CONTAINS_MATCH_OPTIONS);
      if (robotArtifacts.size() < 6) {
         return new Result(String.format(
            "Expected at least 6 Software Requirements with name \"Robot\" but found [%s].  Database is not be populated with demo data.",
            robotArtifacts.size()));
      }
      return Result.TrueResult;
   }

   public static void checkDbInitSuccess() {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
   }

   public static void checkDbInitAndPopulateSuccess() {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
      if (!isPopulateDbSuccessful()) {
         throw new OseeStateException("PopulateDemoDb must be successful to continue");
      }
   }

   public static boolean isDbInitSuccessful() {
      return OseeInfo.getValue("DbInitSuccess").equals("true");
   }

   public static void setDbInitSuccessful(boolean success) {
      OseeInfo.setValue("DbInitSuccess", String.valueOf(success));
   }

   public static boolean isPopulateDbSuccessful() {
      return OseeInfo.getValue("PopulateSuccessful").equals("true");
   }

   public static void setPopulateDbSuccessful(boolean success) {
      OseeInfo.setValue("PopulateSuccessful", String.valueOf(success));
   }

   public static TeamWorkFlowArtifact getSawCodeCommittedWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_Commited_Code_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawTestCommittedWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_Commited_Test_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawReqCommittedWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_Commited_Req_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawSWDesignCommittedWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_Commited_SWDesign_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawCodeUnCommittedWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_UnCommited_Code_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawTestUnCommittedWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_UnCommited_Test_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawReqUnCommittedWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_UnCommited_Req_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawSWDesignUnCommittedWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_UnCommited_SWDesign_TeamWf);
   }

   public static TeamWorkFlowArtifact getSwDesignNoBranchWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_NoBranch_SWDesign_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawCodeNoBranchWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_NoBranch_Code_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawTestNoBranchWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_NoBranch_Test_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawReqNoBranchWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_NoBranch_Req_TeamWf);
   }

   public static TeamWorkFlowArtifact getSawSWDesignNoBranchWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.SAW_NoBranch_SWDesign_TeamWf);
   }

   public static TeamWorkFlowArtifact getButtonWDoesntWorkOnSituationPageWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.ButtonWDoesntWorkOnSituationPage_TeamWf);
   }

   public static TeamWorkFlowArtifact getCantLoadDiagramTreeWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.CantLoadDiagramTree_TeamWf);
   }

   public static TeamWorkFlowArtifact getProblemInDiagramTree_TeamWfWf() {
      return (TeamWorkFlowArtifact) AtsApiService.get().getQueryService().getArtifact(
         DemoArtifactToken.ProblemInDiagramTree_TeamWf);
   }

   public static Collection<TeamWorkFlowArtifact> getSawCommittedTeamWfs() {
      return Arrays.asList(DemoUtil.getSawTestCommittedWf(), DemoUtil.getSawReqCommittedWf(),
         DemoUtil.getSawCodeCommittedWf());
   }

   public static Collection<TeamWorkFlowArtifact> getSawUnCommittedTeamWfs() {
      return Arrays.asList(DemoUtil.getSawTestUnCommittedWf(), DemoUtil.getSawReqUnCommittedWf(),
         DemoUtil.getSawCodeUnCommittedWf(), DemoUtil.getSawSWDesignUnCommittedWf());
   }

   public static String getSawAtsIdsStr() {
      if (atsIds == null) {
         atsIds = Collections.toString(",", AtsObjects.toAtsIds(getSawCommittedTeamWfs()));
      }
      return atsIds;
   }

}
