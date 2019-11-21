/*
 * Created on Nov 13, 2019
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.ide.integration.tests.ats.branch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.branch.BranchRegressionTest;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Assert;

public class DemoBranchRegressionTest extends BranchRegressionTest {

   private final List<String> BranchNames = Arrays.asList(DemoBranches.SAW_Bld_1.getName(),
      DemoBranches.SAW_Bld_2.getName(), DemoBranches.SAW_Bld_3.getName());
   private List<String> taskNames;

   @Override
   public void testCreateAction() {
      String rpcrNumber = getRpcrNumber();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());

      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(DemoArtifactToken.SAW_Requirements_AI,
         DemoArtifactToken.SAW_Code_AI, DemoArtifactToken.SAW_Test_AI);
      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();
      String priority = "1";

      ActionResult actionResult = AtsClientService.get().getActionFactory().createAction(null,
         getClass().getSimpleName(), "Problem with the Diagram View", ChangeType.Problem, priority, false, null, aias,
         createdDate, createdBy, Arrays.asList(new PcrNumberActionListener()), changes);

      if (actionResult.getResults().isErrors()) {
         throw new OseeStateException(actionResult.getResults().toString());
      }
      changes.execute();

      Collection<IAtsTeamWorkflow> teamWfs =
         AtsClientService.get().getQueryService().createQuery(WorkItemType.TeamWorkflow).andAttr(
            AtsAttributeTypes.LegacyPcrId, rpcrNumber).getItems();

      Assert.assertEquals(3, teamWfs.size());
      testTeamWorkflows(teamWfs);
   }

   @Override
   public String getRpcrNumber() {
      return "2019";
   }

   @Override
   public String[] getPreBranchCscis() {
      return new String[] {"Communication", "Aircraft Systems"};
   }

   @Override
   public String[] getFirstArtifactCscis() {
      return new String[] {"Communication", "Aircraft Systems"};
   }

   @Override
   public String[] getSecondArtifactCscis() {
      return new String[] {"Communication", "Navigation"};
   }

   @Override
   public String[] getThirdArtifactCscis() {
      return new String[] {"Aircraft Systems"};
   }

   @Override
   public String[] getInBranchArtifactCscis() {
      return new String[] {"Aircraft Systems"};
   }

   @Override
   public AttributeTypeId getCsciAttribute() {
      return CoreAttributeTypes.Partition;
   }

   @Override
   public BranchId getProgramBranch() {
      return DemoBranches.SAW_Bld_2;
   }

   @Override
   public Result verifyCodeTestTasks() throws Exception {
      System.err.println(getClass().getSimpleName() + " - TBD: Add checks");
      return Result.TrueResult;
   }

   @Override
   public Result verifyShowRelatedTasksAction() {
      System.err.println(getClass().getSimpleName() + " - TBD: Add checks");
      return Result.TrueResult;
   }

   @Override
   public Result verifyShowRelatedRequirementAction() {
      System.err.println(getClass().getSimpleName() + " - TBD: Add checks");
      return Result.TrueResult;
   }

   @Override
   public Result verifyShowRequirementDiffsAction() {
      System.err.println(getClass().getSimpleName() + " - TBD: Add checks");
      return Result.TrueResult;
   }

   @Override
   public List<String> getBranchNames() throws Exception {
      return BranchNames;
   }

   @Override
   public int getExpectedBranchConfigArts() {
      return 3;
   }

   private class PcrNumberActionListener implements INewActionListener {

      @Override
      public void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
         INewActionListener.super.teamCreated(action, teamWf, changes);
         changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.LegacyPcrId, getRpcrNumber());
         IAtsVersion version = AtsClientService.get().getVersionService().getVersion(DemoArtifactToken.SAW_Bld_2);
         AtsClientService.get().getVersionService().setTargetedVersion(teamWf, version, changes);
      }

   }

   @Override
   public ArtifactTypeToken getCodeTeamWfArtType() {
      return AtsDemoOseeTypes.DemoCodeTeamWorkflow;
   }

   @Override
   public ArtifactTypeToken getTestTeamWfArtType() {
      return AtsDemoOseeTypes.DemoTestTeamWorkflow;
   }

   @Override
   protected XResultData verifyShowRelatedTasksAction(Collection<IAtsTask> tasks) {
      XResultData results = new XResultData();
      for (IAtsTask task : tasks) {
         if (!getTaskNames().contains(task.getName())) {
            results.errorf("Task named [%s]; not found for task %s", task.getName(), task.toStringWithId());
         }
      }
      for (String taskName : getTaskNames()) {
         boolean found = false;
         for (IAtsTask task : tasks) {
            if (task.getName().equals(taskName)) {
               found = true;
               break;
            }
         }
         if (!found) {
            results.errorf("Expected Task named [%s] not found", taskName);
         }
      }
      return results;
   }

   private List<String> getTaskNames() {
      if (taskNames == null) {
         taskNames = Arrays.asList("Handle Add/Mod change to [Fifth Artifact - Unspecified CSCI]",
            "Handle Add/Mod change to [First Artifact]", "Handle Add/Mod change to [Fourth Artifact - No CSCI]",
            "Handle Add/Mod change to [Parent Artifact]", "Handle Add/Mod change to [Pre-Branch Artifact to Delete]",
            "Handle Add/Mod change to [Second Artifact]",
            "Handle Add/Mod change to [Subsystem Artifact (no partition)]", "Handle Add/Mod change to [Third Artifact]",
            "Handle Relation change to [Fifth Artifact - Unspecified CSCI]",
            "Handle Relation change to [First Artifact]", "Handle Relation change to [Fourth Artifact - No CSCI]",
            "Handle Relation change to [Parent Artifact]", "Handle Relation change to [Pre-Branch Artifact to Delete]",
            "Handle Relation change to [Second Artifact]", "Handle Relation change to [Software Requirements]",
            "Handle Relation change to [Subsystem Artifact (no partition)]",
            "Handle Relation change to [Third Artifact]");
      }
      return taskNames;
   }
}
