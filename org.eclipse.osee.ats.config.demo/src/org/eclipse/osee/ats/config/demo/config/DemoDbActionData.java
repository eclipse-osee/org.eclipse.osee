/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.UserCommunity;

/**
 * @author Donald G. Dunne
 */
public class DemoDbActionData {
   public final String postFixTitle;
   public final PriorityType priority;
   public final String[] actionableItems;
   public final DefaultTeamState toState;
   public final Integer[] userCommunityIndecies;
   public String[] configuredUserCommunities;
   public final String[] prefixTitles;
   private final CreateReview[] createReviews;
   public enum CreateReview {
      Decision, Peer, None
   };

   public DemoDbActionData(String[] prefixTitles, String postFixTitle, PriorityType priority, String[] actionableItems, Integer[] userCommunityIndecies, DefaultTeamState toState, CreateReview... createReviews) {
      this.prefixTitles = prefixTitles;
      this.postFixTitle = postFixTitle;
      this.priority = priority;
      this.actionableItems = actionableItems;
      this.userCommunityIndecies = userCommunityIndecies;
      this.toState = toState;
      this.createReviews = createReviews;
   }

   public Set<String> getUserCommunities() {
      if (configuredUserCommunities == null) {
         configuredUserCommunities =
               UserCommunity.getInstance().getUserCommunityNames().toArray(
                     new String[UserCommunity.getInstance().getUserCommunityNames().size()]);
      }
      Set<String> userComms = new HashSet<String>();
      for (Integer index : userCommunityIndecies)
         userComms.add(configuredUserCommunities[index]);
      return userComms;
   }

   public Collection<ActionableItemArtifact> getActionableItems() throws OseeCoreException, SQLException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (String str : actionableItems) {
         for (ActionableItemArtifact aia : ActionableItemArtifact.getActionableItems()) {
            if (str.equals(aia.getDescriptiveName())) aias.add(aia);
         }
      }
      return aias;
   }

   public static Set<DemoDbActionData> getReqSawActionsData() {
      Set<DemoDbActionData> actionDatas = new HashSet<DemoDbActionData>();
      actionDatas.add(new DemoDbActionData(new String[] {"SAW (committed) Reqt Changes for"}, "Diagram View",
            PriorityType.Priority_1, new String[] {DemoDbAIs.SAW_Requirements.getAIName(),
                  DemoDbAIs.SAW_Code.getAIName(), DemoDbAIs.SAW_Test.getAIName()}, new Integer[] {1},
            DefaultTeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"SAW (uncommitted) More Reqt Changes for"}, "Diagram View",
            PriorityType.Priority_3, new String[] {DemoDbAIs.SAW_Code.getAIName(), DemoDbAIs.SAW_SW_Design.getAIName(),
                  DemoDbAIs.SAW_Requirements.getAIName(), DemoDbAIs.SAW_Test.getAIName()}, new Integer[] {1},
            DefaultTeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"SAW (no-branch) Even More Requirement Changes for"},
            "Diagram View", PriorityType.Priority_3, new String[] {DemoDbAIs.SAW_Code.getAIName(),
                  DemoDbAIs.SAW_SW_Design.getAIName(), DemoDbAIs.SAW_Requirements.getAIName(),
                  DemoDbAIs.SAW_Test.getAIName()}, new Integer[] {1}, DefaultTeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"SAW (uncommitted-conflicted) More Requirement Changes for"},
            "Diagram View", PriorityType.Priority_3, new String[] {DemoDbAIs.SAW_Requirements.getAIName()},
            new Integer[] {1}, DefaultTeamState.Implement));
      return actionDatas;
   }

   public static Set<DemoDbActionData> getNonReqSawActionData() {
      Set<DemoDbActionData> actionDatas = new HashSet<DemoDbActionData>();
      actionDatas.add(new DemoDbActionData(new String[] {"Workaround for"}, "Graph View", PriorityType.Priority_1,
            new String[] {DemoDbAIs.Adapter.getAIName()}, new Integer[] {1}, DefaultTeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"Working with"}, "Diagram Tree", PriorityType.Priority_3,
            new String[] {DemoDbAIs.SAW_SW_Design.getAIName()}, new Integer[] {0, 2}, DefaultTeamState.Endorse));
      return actionDatas;
   }

   public static Set<DemoDbActionData> getGenericActionData() {
      Set<DemoDbActionData> actionDatas = new HashSet<DemoDbActionData>();
      actionDatas.add(new DemoDbActionData(new String[] {"Problem with the", "Can't see the"}, "Graph View",
            PriorityType.Priority_1, new String[] {DemoDbAIs.Adapter.getAIName()}, new Integer[] {1},
            DefaultTeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"Problem in", "Can't load"}, "Diagram Tree",
            PriorityType.Priority_3, new String[] {DemoDbAIs.CIS_Test.getAIName()}, new Integer[] {0, 2},
            DefaultTeamState.Endorse));
      actionDatas.add(new DemoDbActionData(new String[] {"Button W doesn't work on"}, "Situation Page",
            PriorityType.Priority_3, new String[] {DemoDbAIs.CIS_Test.getAIName()}, new Integer[] {0, 2},
            DefaultTeamState.Analyze));
      actionDatas.add(new DemoDbActionData(new String[] {"Problem with the"}, "user window", PriorityType.Priority_4,
            new String[] {DemoDbAIs.Timesheet.getAIName()}, new Integer[] {1}, DefaultTeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"Button S doesn't work on"}, "help", PriorityType.Priority_3,
            new String[] {DemoDbAIs.Reader.getAIName()}, new Integer[] {1}, DefaultTeamState.Completed,
            CreateReview.Decision));
      return actionDatas;
   }

   /**
    * @return the createReviews
    */
   public CreateReview[] getCreateReviews() {
      return createReviews;
   }

}
