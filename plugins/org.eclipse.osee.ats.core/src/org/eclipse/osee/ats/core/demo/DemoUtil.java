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

package org.eclipse.osee.ats.core.demo;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class DemoUtil {

   private static String atsIds;
   public static List<String> Saw_Code_Committed_Task_Titles = Arrays.asList("Look into Graph View.",
      "Redesign how view shows values.", "Discuss new design with Senior Engineer", "Develop prototype",
      "Show prototype to management", "Create development plan", "Create test plan", "Make changes");
   public static List<String> Saw_Code_UnCommitted_Task_Titles =
      Arrays.asList("Document how Graph View works", "Update help contents", "Review new documentation",
         "Publish documentation to website", "Remove old viewer", "Deploy release");
   public static String INTERFACE_INITIALIZATION = "Interface Initialization";

   private DemoUtil() {
      // Utility class
   }

   public static Collection<ArtifactToken> getSoftwareRequirements(boolean DEBUG, SoftwareRequirementStrs str,
      BranchToken branch) {
      return getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirementMsWord, str.name(), branch);
   }

   public static Collection<ArtifactToken> getArtifactsFromType(boolean DEBUG, ArtifactTypeToken artifactType,
      BranchToken branch) {
      if (DEBUG) {
         OseeLog.log(DemoUtil.class, Level.INFO,
            "Getting \"" + artifactType.getName() + "\" requirement(s) from Branch " + branch.getIdString());
      }
      Collection<ArtifactToken> arts =
         AtsApiService.get().getQueryService().getArtifacts(BranchId.valueOf(branch.getId()), false, artifactType);

      if (DEBUG) {
         OseeLog.log(DemoUtil.class, Level.INFO, "Found " + arts.size() + " Artifacts");
      }
      return arts;
   }

   public static Collection<ArtifactToken> getArtTypeRequirements(boolean DEBUG, ArtifactTypeToken artifactType,
      String artifactNameStr, BranchToken branch) {
      if (DEBUG) {
         OseeLog.log(DemoUtil.class, Level.INFO,
            "Getting \"" + artifactNameStr + "\" requirement(s) from Branch " + branch.getIdString());
      }
      Collection<ArtifactToken> arts = AtsApiService.get().getQueryService().getArtifactsFromTypeAndName(artifactType,
         artifactNameStr, branch, QueryOption.CONTAINS_MATCH_OPTIONS);

      if (DEBUG) {
         OseeLog.log(DemoUtil.class, Level.INFO, "Found " + arts.size() + " Artifacts");
      }
      return arts;
   }
   public static enum SoftwareRequirementStrs {
      Robot,
      CISST,
      daVinci,
      Functional,
      Event,
      Haptic
   };
   public static String HAPTIC_CONSTRAINTS_REQ = "Haptic Constraints";

   public static ArtifactToken getInterfaceInitializationSoftwareRequirement(boolean DEBUG, BranchToken branch) {
      if (DEBUG) {
         OseeLog.log(DemoUtil.class, Level.INFO, "Getting \"" + INTERFACE_INITIALIZATION + "\" requirement.");
      }
      return AtsApiService.get().getQueryService().getArtifactByName(CoreArtifactTypes.SoftwareRequirementMsWord,
         INTERFACE_INITIALIZATION, branch);
   }

   public static Collection<IAtsActionableItem> getActionableItems(ArtifactToken... aiTokens) {
      Set<IAtsActionableItem> aias = new HashSet<>();
      for (ArtifactToken aiToken : aiTokens) {
         aias.add(AtsApiService.get().getActionableItemService().getActionableItemById(aiToken));
      }
      return aias;
   }

   public static int getNumTasks() {
      return Saw_Code_Committed_Task_Titles.size() + Saw_Code_UnCommitted_Task_Titles.size();
   }

   public static Collection<IAtsActionableItem> getActionableItems(AtsApi atsApi, ArtifactToken... aiTokens) {
      Set<IAtsActionableItem> aias = new HashSet<>();
      for (ArtifactToken aiToken : aiTokens) {
         aias.add(atsApi.getActionableItemService().getActionableItemById(aiToken));
      }
      return aias;
   }

   public static Result isDbPopulatedWithDemoData() {
      Collection<ArtifactToken> robotArtifacts = AtsApiService.get().getQueryService().getArtifactsFromTypeAndName(
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
      return AtsApiService.get().isOseeInfo("DbInitSuccess", "true");
   }

   public static void setDbInitSuccessful(boolean success) {
      AtsApiService.get().setOseeInfo("DbInitSuccess", String.valueOf(success));
   }

   public static boolean isPopulateDbSuccessful() {
      return AtsApiService.get().isOseeInfo("PopulateSuccessful", "true");
   }

   public static void setPopulateDbSuccessful(boolean success) {
      AtsApiService.get().setOseeInfo("PopulateSuccessful", String.valueOf(success));
   }

   public static IAtsTeamWorkflow getSawCodeCommittedWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_Commited_Code_TeamWf);
   }

   public static IAtsTeamWorkflow getSawTestCommittedWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_Commited_Test_TeamWf);
   }

   public static IAtsTeamWorkflow getSawReqCommittedWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_Commited_Req_TeamWf);
   }

   public static IAtsTeamWorkflow getSawSWDesignCommittedWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_Commited_SWDesign_TeamWf);
   }

   public static IAtsTeamWorkflow getSawCodeUnCommittedWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_UnCommited_Code_TeamWf);
   }

   public static IAtsTeamWorkflow getSawTestUnCommittedWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_UnCommited_Test_TeamWf);
   }

   public static IAtsTeamWorkflow getSawReqUnCommittedWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_UnCommited_Req_TeamWf);
   }

   public static IAtsTeamWorkflow getSawSWDesignUnCommittedWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_UnCommited_SWDesign_TeamWf);
   }

   public static IAtsTeamWorkflow getSwDesignNoBranchWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_NoBranch_SWDesign_TeamWf);
   }

   public static IAtsTeamWorkflow getSawCodeNoBranchWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_NoBranch_Code_TeamWf);
   }

   public static IAtsTeamWorkflow getSawTestNoBranchWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_NoBranch_Test_TeamWf);
   }

   public static IAtsTeamWorkflow getSawReqNoBranchWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_NoBranch_Req_TeamWf);
   }

   public static IAtsTeamWorkflow getSawSWDesignNoBranchWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.SAW_NoBranch_SWDesign_TeamWf);
   }

   public static IAtsTeamWorkflow getButtonWDoesntWorkOnSituationPageWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.ButtonWDoesntWorkOnSituationPage_TeamWf);
   }

   public static IAtsTeamWorkflow getCantLoadDiagramTreeWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.CantLoadDiagramTree_TeamWf);
   }

   public static IAtsTeamWorkflow getProblemInTree_TeamWfWf() {
      return (IAtsTeamWorkflow) AtsApiService.get().getQueryService().getWorkItem(
         DemoArtifactToken.ProblemInTree_TeamWf);
   }

   public static Collection<IAtsTeamWorkflow> getSawCommittedTeamWfs() {
      return Arrays.asList(DemoUtil.getSawTestCommittedWf(), DemoUtil.getSawReqCommittedWf(),
         DemoUtil.getSawCodeCommittedWf());
   }

   public static Collection<IAtsTeamWorkflow> getSawUnCommittedTeamWfs() {
      return Arrays.asList(DemoUtil.getSawTestUnCommittedWf(), DemoUtil.getSawReqUnCommittedWf(),
         DemoUtil.getSawCodeUnCommittedWf(), DemoUtil.getSawSWDesignUnCommittedWf());
   }

   public static String getSawAtsIdsStr() {
      if (atsIds == null) {
         atsIds = Collections.toString(",", AtsObjects.toAtsIds(getSawCommittedTeamWfs()));
      }
      return atsIds;
   }

   public static void assertNotNull(Object obj, XResultData rd) {
      if (obj == null) {
         rd.errorf("Object should not be null\n");
      }
   }

}
