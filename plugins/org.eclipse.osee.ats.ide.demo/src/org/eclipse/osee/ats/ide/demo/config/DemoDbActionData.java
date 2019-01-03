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
package org.eclipse.osee.ats.ide.demo.config;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class DemoDbActionData {
   public final String postFixTitle;
   public final String priority;
   public final ArtifactToken aiToken;
   public final TeamState toState;
   public final String[] prefixTitles;
   private final CreateReview[] createReviews;
   public enum CreateReview {
      Decision,
      Peer,
      None
   };

   public DemoDbActionData(String[] prefixTitles, String postFixTitle, String priority, ArtifactToken aiToken, TeamState toState, CreateReview... createReviews) {
      this.prefixTitles = prefixTitles;
      this.postFixTitle = postFixTitle;
      this.priority = priority;
      this.aiToken = aiToken;
      this.toState = toState;
      this.createReviews = createReviews;
   }

   public static List<DemoDbActionData> getNonReqSawActionData() {
      List<DemoDbActionData> actionDatas = new ArrayList<>();
      actionDatas.add(new DemoDbActionData(new String[] {"Workaround for"}, "Graph View", "1",
         DemoArtifactToken.Adapter_AI, TeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"Working with"}, "Diagram Tree", "3",
         DemoArtifactToken.SAW_SW_Design_AI, TeamState.Endorse));
      return actionDatas;
   }

   public static List<DemoDbActionData> getGenericActionData() {
      List<DemoDbActionData> actionDatas = new ArrayList<>();
      actionDatas.add(new DemoDbActionData(new String[] {"Problem with the", "Can't see the"}, "Graph View", "1",
         DemoArtifactToken.Adapter_AI, TeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"Problem in", "Can't load"}, "Diagram Tree", "3",
         DemoArtifactToken.CIS_Test_AI, TeamState.Endorse));
      actionDatas.add(new DemoDbActionData(new String[] {"Button W doesn't work on"}, "Situation Page", "3",
         DemoArtifactToken.CIS_Test_AI, TeamState.Analyze));
      actionDatas.add(new DemoDbActionData(new String[] {"Problem with the"}, "user window", "4",
         DemoArtifactToken.Timesheet_AI, TeamState.Implement));
      actionDatas.add(new DemoDbActionData(new String[] {"Button S doesn't work on"}, "help", "3",
         DemoArtifactToken.Reader_AI, TeamState.Completed, CreateReview.Decision));
      return actionDatas;
   }

   /**
    * @return the createReviews
    */
   public CreateReview[] getCreateReviews() {
      return createReviews;
   }

}
