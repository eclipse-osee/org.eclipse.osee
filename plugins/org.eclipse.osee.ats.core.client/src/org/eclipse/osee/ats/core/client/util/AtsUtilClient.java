/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.ITransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchUuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
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
   private static List<IEventFilter> atsObjectEventFilter = new ArrayList<>(2);
   private static boolean emailEnabled = true;
   private static BranchUuidEventFilter commonBranchUuidEventFilter;
   private static Boolean atsAdmin = null;

   public static boolean isEmailEnabled() {
      return emailEnabled;
   }

   /**
    * This is necessary for ATS Config object's uuid so they can be persisted. Can be removed once artifact ids go to
    * random longs
    *
    * @return uuid for ATS Config Object creation
    */
   public static long createConfigObjectUuid() {
      return Lib.generateArtifactIdAsInt();
   }

   public static void setEmailEnabled(boolean enabled) {
      if (!DbUtil.isDbInit() && !AtsUtilCore.isInTest()) {
         OseeLog.log(Activator.class, Level.INFO, "Email " + (enabled ? "Enabled" : "Disabled"));
      }
      emailEnabled = enabled;
   }

   public static BranchUuidEventFilter getAtsBranchFilter() {
      if (commonBranchUuidEventFilter == null) {
         commonBranchUuidEventFilter = new BranchUuidEventFilter(AtsClientService.get().getAtsBranch());
      }
      return commonBranchUuidEventFilter;
   }

   /**
    * TODO Remove duplicate Active flags, need to convert all ats.Active to Active in DB
    *
    * @param artifacts to iterate through
    * @param active state to validate against; Both will return all artifacts matching type
    * @param clazz type of artifacts to consider; null for all
    * @return set of Artifacts of type clazz that match the given active state of the "Active" or "ats.Active" attribute
    * value. If no attribute exists, Active == true;
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> List<A> getActive(Collection<A> artifacts, Active active, Class<? extends Artifact> clazz) throws OseeCoreException {
      List<A> results = new ArrayList<>();
      Collection<? extends Artifact> artsOfClass =
         clazz != null ? Collections.castMatching(clazz, artifacts) : artifacts;
      for (Artifact art : artsOfClass) {
         if (active == Active.Both) {
            results.add((A) art);
         } else {
            // assume active unless otherwise specified
            boolean attributeActive = ((A) art).getSoleAttributeValue(AtsAttributeTypes.Active, false);
            if (active == Active.Active && attributeActive) {
               results.add((A) art);
            } else if (active == Active.InActive && !attributeActive) {
               results.add((A) art);
            }
         }
      }
      return results;
   }

   public static boolean isAtsAdmin(boolean useCache) {
      if (!useCache) {
         atsAdmin = AtsGroup.AtsAdmin.isMember(
            AtsClientService.get().getUserService().getCurrentUser()) || AtsGroup.AtsAdmin.isCurrentUserTemporaryOverride();
      }
      return isAtsAdmin();
   }

   public static boolean isAtsAdmin() {
      if (atsAdmin == null) {
         try {
            atsAdmin = AtsClientService.get().getConfigurations().getAtsAdmins().contains(
               AtsClientService.get().getUserService().getCurrentUser().getId());
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            atsAdmin = false;
         }
      }
      return atsAdmin;
   }

   public static String getAtsId(Artifact art) throws OseeCoreException {
      String toReturn = art.getSoleAttributeValueAsString(AtsAttributeTypes.AtsId, AtsUtilCore.DEFAULT_ATS_ID_VALUE);
      Conditions.checkNotNull("AtsId", "AtsId");
      if (AtsUtilCore.DEFAULT_ATS_ID_VALUE.equals(toReturn)) {
         toReturn = art.getGuid();
      }
      return toReturn;
   }

   public static Artifact getFromToken(ArtifactToken token) {
      Artifact toReturn = null;
      try {
         toReturn = ArtifactQuery.getArtifactFromId(token, AtsClientService.get().getAtsBranch());
      } catch (OseeCoreException ex) {
         // Do Nothing;
      }
      return toReturn;
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

   public static ArtifactTypeEventFilter getAtsObjectArtifactTypeEventFilter() {
      return atsObjectArtifactTypesFilter;
   }

   public static ArtifactTypeEventFilter getTeamWorkflowArtifactTypeEventFilter() {
      return teamWorkflowArtifactTypesFilter;
   }

   public static ArtifactTypeEventFilter getReviewArtifactTypeEventFilter() {
      return reviewArtifactTypesFilter;
   }

   /**
    * Log exceptions to OseeLog. Don't always want to do this due to testing.
    */
   public static void logExceptions(TransitionResults transResult) {
      for (ITransitionResult result : transResult.getResults()) {
         Exception ex = result.getException();
         if (ex != null) {
            OseeLog.log(TransitionResults.class, Level.SEVERE, result.getDetails(), ex);
         }
      }
      for (Entry<IAtsWorkItem, List<ITransitionResult>> entry : transResult.getWorkItemToResults().entrySet()) {
         for (ITransitionResult result : entry.getValue()) {
            Exception ex = result.getException();
            if (ex != null) {
               String message = entry.getKey().toStringWithId() + " - " + result.getDetails();
               OseeLog.log(TransitionResults.class, Level.SEVERE, message, ex);
            }
         }
      }
   }

}
