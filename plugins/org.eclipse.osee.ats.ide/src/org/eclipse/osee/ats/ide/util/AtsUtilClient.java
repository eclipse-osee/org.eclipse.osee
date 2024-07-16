/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.api.workflow.transition.TransitionWorkItemResult;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchIdEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ArtifactTopicTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.BranchIdTopicEventFilter;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * @author Donald G. Dunne
 */
public class AtsUtilClient {
   private static ArtifactTypeEventFilter atsObjectArtifactTypesFilter =
      new ArtifactTypeEventFilter(AtsArtifactTypes.TeamWorkflow, AtsArtifactTypes.Action, AtsArtifactTypes.Task,
         AtsArtifactTypes.Goal, AtsArtifactTypes.AgileSprint, AtsArtifactTypes.PeerToPeerReview,
         AtsArtifactTypes.DecisionReview, AtsArtifactTypes.Version);
   private static ArtifactTypeEventFilter reviewArtifactTypesFilter =
      new ArtifactTypeEventFilter(AtsArtifactTypes.PeerToPeerReview, AtsArtifactTypes.DecisionReview);
   private static ArtifactTypeEventFilter teamWorkflowArtifactTypesFilter =
      new ArtifactTypeEventFilter(AtsArtifactTypes.TeamWorkflow);

   private static ArtifactTopicTypeEventFilter atsTopicObjectArtifactTypesFilter =
      new ArtifactTopicTypeEventFilter(AtsArtifactTypes.TeamWorkflow, AtsArtifactTypes.Action, AtsArtifactTypes.Task,
         AtsArtifactTypes.Goal, AtsArtifactTypes.AgileSprint, AtsArtifactTypes.PeerToPeerReview,
         AtsArtifactTypes.DecisionReview, AtsArtifactTypes.Version);
   private static ArtifactTopicTypeEventFilter reviewTopicArtifactTypesFilter =
      new ArtifactTopicTypeEventFilter(AtsArtifactTypes.PeerToPeerReview, AtsArtifactTypes.DecisionReview);
   private static ArtifactTopicTypeEventFilter teamWorkflowTopicArtifactTypesFilter =
      new ArtifactTopicTypeEventFilter(AtsArtifactTypes.TeamWorkflow);

   private static List<IEventFilter> atsObjectEventFilter = new ArrayList<>(2);
   private static List<ITopicEventFilter> atsTopicObjectEventFilter = new ArrayList<>(2);
   private static boolean emailEnabled = true;
   private static BranchIdEventFilter commonBranchIdEventFilter =
      new BranchIdEventFilter(AtsApiService.get().getAtsBranch());
   private static BranchIdTopicEventFilter commonBranchTopicIdEventFilter =
      new BranchIdTopicEventFilter(AtsApiService.get().getAtsBranch());

   public static boolean isEmailEnabled() {
      return emailEnabled;
   }

   public static void setEmailEnabled(boolean enabled) {
      if (!DbUtil.isDbInit() && !AtsUtil.isInTest()) {
         OseeLog.log(Activator.class, Level.INFO, "Email " + (enabled ? "Enabled" : "Disabled"));
      }
      emailEnabled = enabled;
   }

   public static BranchIdEventFilter getAtsBranchFilter() {
      return commonBranchIdEventFilter;
   }

   public static BranchIdTopicEventFilter getAtsTopicBranchFilter() {
      return commonBranchTopicIdEventFilter;
   }

   public synchronized static List<IEventFilter> getAtsObjectEventFilters() {
      try {
         if (atsObjectEventFilter.isEmpty()) {
            atsObjectEventFilter.add(AtsUtilClient.getAtsBranchFilter());
            atsObjectEventFilter.add(getAtsObjectArtifactTypeEventFilter());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return atsObjectEventFilter;
   }

   public synchronized static List<ITopicEventFilter> getAtsTopicObjectEventFilters() {
      try {
         if (atsTopicObjectEventFilter.isEmpty()) {
            atsTopicObjectEventFilter.add(AtsUtilClient.getAtsTopicBranchFilter());
            atsTopicObjectEventFilter.add(getAtsTopicObjectArtifactTypeEventFilter());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return atsTopicObjectEventFilter;
   }

   public static ArtifactTypeEventFilter getAtsObjectArtifactTypeEventFilter() {
      return atsObjectArtifactTypesFilter;
   }

   public static ArtifactTypeEventFilter getTeamWorkflowArtifactTypeEventFilter() {
      return teamWorkflowArtifactTypesFilter;
   }

   public static ArtifactTypeEventFilter getReviewArtifactTypeEventFilter() {
      return reviewArtifactTypesFilter;
   }

   public static ArtifactTopicTypeEventFilter getAtsTopicObjectArtifactTypeEventFilter() {
      return atsTopicObjectArtifactTypesFilter;
   }

   public static ArtifactTopicTypeEventFilter getTeamWorkflowTopicArtifactTypeEventFilter() {
      return teamWorkflowTopicArtifactTypesFilter;
   }

   public static ArtifactTopicTypeEventFilter getReviewTopicArtifactTypeEventFilter() {
      return reviewTopicArtifactTypesFilter;
   }

   /**
    * Log exceptions to OseeLog. Don't always want to do this due to testing.
    */
   public static void logExceptions(TransitionResults transResult) {
      for (TransitionResult result : transResult.getResults()) {
         String ex = result.getException();
         if (Strings.isValid(ex)) {
            OseeLog.log(TransitionResults.class, Level.SEVERE, result.getDetails() + "\n\n" + ex);
         }
      }
      for (TransitionWorkItemResult transitionWorkItemResult : transResult.getTransitionWorkItems()) {
         for (TransitionResult result : transitionWorkItemResult.getResults()) {
            String ex = result.getException();
            if (Strings.isValid(ex)) {
               String message = transitionWorkItemResult.getWorkItemId().toStringWithId() + " - " + result.getDetails();
               OseeLog.log(TransitionResults.class, Level.SEVERE, message + "\n\n" + ex);
            }
         }
      }
   }
}
