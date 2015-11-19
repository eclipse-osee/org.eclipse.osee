/*
 * Created on Nov 24, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.query;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

public class QueryTest {

   private final IAtsServices services;
   private IAtsUser joeSmith;
   private IAtsWorkPackage wp;

   public QueryTest(IAtsServices services) {
      this.services = services;
   }

   public void run() {
      IAtsQueryService queryService = services.getQueryService();
      this.joeSmith = services.getUserService().getUserById("3333");

      ArtifactId wpArt = services.getArtifactByName(AtsArtifactTypes.WorkPackage, "Work Pkg 01");
      Conditions.checkNotNull(wpArt, "Work Package");
      wp = services.getProgramService().getWorkPackage(wpArt.getUuid());

      // test by type
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);

      assertEquals(25, query.getResults().size());
      query = queryService.createQuery(WorkItemType.Task);
      assertEquals(14, query.getResults().size());

      // assignee
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andAssignee(services.getUserService().getUserById("3333"));
      assertEquals(7, query.getResults().size());

      // team
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andTeam(Arrays.asList(30013695L));
      assertEquals(3, query.getResults().size());

      // ai
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      ArtifactId ai = services.getArtifactByName(AtsArtifactTypes.ActionableItem, "SAW Requirements");
      query.andActionableItem(Arrays.asList(ai.getUuid()));
      assertEquals(4, query.getResults().size());

      // by uuids (hijack two workflows from previous search)
      List<Long> uuids = new LinkedList<>();
      for (IAtsWorkItem workItem : query.getResults()) {
         uuids.add(workItem.getUuid());
      }
      query = queryService.createQuery(WorkItemType.WorkItem);
      Iterator<Long> iterator = uuids.iterator();
      query.andUuids(iterator.next(), iterator.next());
      assertEquals(2, query.getResults().size());

      // by state name
      query = queryService.createQuery(WorkItemType.WorkItem);
      query.isOfType(WorkItemType.PeerReview);
      query.andState("Prepare");
      assertEquals(4, query.getResults().size());

      // by state type
      query = queryService.createQuery(WorkItemType.WorkItem);
      query.andStateType(StateType.Working);
      assertEquals(48, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      assertEquals(22, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Completed);
      assertEquals(3, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Completed, StateType.Working);
      assertEquals(25, query.getResults().size());

      // by version
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      ArtifactId version = services.getArtifactByName(AtsArtifactTypes.Version, "SAW_Bld_2");
      query.andVersion(version.getUuid());
      assertEquals(14, query.getResults().size());

      // by assignee
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andAssignee(joeSmith);
      assertEquals(7, query.getResults().size());

      // by originator
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andOriginator(joeSmith);
      assertEquals(25, query.getResults().size());

      // by favorite
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      query.andFavorite(joeSmith);
      assertEquals(3, query.getResults().size());

      // by subscribed
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      query.andSubscribed(joeSmith);
      assertEquals(1, query.getResults().size());

      // setup code workflow and task to have a work package

      IAtsProgramService programService = services.getProgramService();

      IAtsInsertionActivity activity = programService.getInsertionActivity(wp);
      IAtsInsertion insertion = programService.getInsertion(activity);
      IAtsProgram program = programService.getProgram(insertion);

      IAtsTeamWorkflow codeWf = null;
      IAtsTask codeTask = null;
      IAtsQuery query2 = queryService.createQuery(WorkItemType.WorkItem);
      query2.isOfType(WorkItemType.TeamWorkflow);
      query2.andAssignee(joeSmith);
      for (IAtsWorkItem workItem : query2.getResults()) {
         if (workItem.getArtifactTypeName().contains("Code")) {
            codeWf = (IAtsTeamWorkflow) workItem;
            for (IAtsTask task : services.getTaskService().getTasks(codeWf)) {
               codeTask = task;
               break;
            }
         }
         if (codeTask != null) {
            break;
         }
      }
      Conditions.checkNotNull(codeWf, "Code Team Workflow");
      Conditions.checkNotNull(codeTask, "Code Team Workflow");

      services.getProgramService().setWorkPackage(wp, Arrays.asList(codeWf, codeTask));

      // by program
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andProgram(program.getUuid());
      assertEquals(1, query.getResults().size());

      query = queryService.createQuery(WorkItemType.Task);
      query.andProgram(program.getUuid());
      assertEquals(1, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andProgram(program.getUuid());
      assertEquals(2, query.getResults().size());

      // by insertion
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andInsertion(insertion.getUuid());
      assertEquals(2, query.getResults().size());

      // by insertion activity
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andInsertionActivity(activity.getUuid());
      assertEquals(2, query.getResults().size());

      // by work package
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andWorkPackage(wp.getUuid());
      assertEquals(2, query.getResults().size());
   }

   public static void assertEquals(int v1, int v2) {
      if (v1 != v2) {
         throw new OseeStateException("Expected %d, was %d", v1, v2);
      }
   }
}
