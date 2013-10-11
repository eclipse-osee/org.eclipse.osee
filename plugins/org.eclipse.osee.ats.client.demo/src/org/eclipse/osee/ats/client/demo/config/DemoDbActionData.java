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
package org.eclipse.osee.ats.client.demo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.util.UserCommunity;

/**
 * @author Donald G. Dunne
 */
public class DemoDbActionData {
   public final String postFixTitle;
   public final String priority;
   public final String[] actionableItems;
   public final TeamState toState;
   public final Integer[] userCommunityIndices;
   public final String[] prefixTitles;
   private final CreateReview[] createReviews;
   public enum CreateReview {
      Decision,
      Peer,
      None
   };

   public DemoDbActionData(String[] prefixTitles, String postFixTitle, String priority, String[] actionableItems, Integer[] userCommunityIndecies, TeamState toState, CreateReview... createReviews) {
      this.prefixTitles = prefixTitles;
      this.postFixTitle = postFixTitle;
      this.priority = priority;
      this.actionableItems = actionableItems;
      this.userCommunityIndices = userCommunityIndecies;
      this.toState = toState;
      this.createReviews = createReviews;
   }

   public Set<String> getUserCommunities() {
      String[] configuredUserCommunities = UserCommunity.getInstance().getUserCommunityNames().toArray(new String[] {});
      Set<String> userComms = new HashSet<String>();

      for (Integer index : userCommunityIndices) {
         userComms.add(configuredUserCommunities[index]);
      }
      return userComms;
   }

   public Collection<IAtsActionableItem> getActionableItems() throws OseeCoreException {
      return DemoDbUtil.getActionableItems(actionableItems);
   }

   public static List<DemoDbActionData> getNonReqSawActionData() {
      List<DemoDbActionData> actionDatas = new ArrayList<DemoDbActionData>();
      actionDatas.add(new DemoDbActionData(new String[] {"Workaround for"}, "Graph View", "1",
         new String[] {DemoDbAIs.Adapter.getAIName()}, new Integer[] {1}, TeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"Working with"}, "Diagram Tree", "3",
         new String[] {DemoDbAIs.SAW_SW_Design.getAIName()}, new Integer[] {0, 2}, TeamState.Endorse));
      return actionDatas;
   }

   public static List<DemoDbActionData> getGenericActionData() {
      List<DemoDbActionData> actionDatas = new ArrayList<DemoDbActionData>();
      actionDatas.add(new DemoDbActionData(new String[] {"Problem with the", "Can't see the"}, "Graph View", "1",
         new String[] {DemoDbAIs.Adapter.getAIName()}, new Integer[] {1}, TeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"Problem in", "Can't load"}, "Diagram Tree", "3",
         new String[] {DemoDbAIs.CIS_Test.getAIName()}, new Integer[] {0, 2}, TeamState.Endorse));
      actionDatas.add(new DemoDbActionData(new String[] {"Button W doesn't work on"}, "Situation Page", "3",
         new String[] {DemoDbAIs.CIS_Test.getAIName()}, new Integer[] {0, 2}, TeamState.Analyze));
      actionDatas.add(new DemoDbActionData(new String[] {"Problem with the"}, "user window", "4",
         new String[] {DemoDbAIs.Timesheet.getAIName()}, new Integer[] {1}, TeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"Button S doesn't work on"}, "help", "3",
         new String[] {DemoDbAIs.Reader.getAIName()}, new Integer[] {1}, TeamState.Completed, CreateReview.Decision));
      return actionDatas;
   }

   /**
    * @return the createReviews
    */
   public CreateReview[] getCreateReviews() {
      return createReviews;
   }

}
