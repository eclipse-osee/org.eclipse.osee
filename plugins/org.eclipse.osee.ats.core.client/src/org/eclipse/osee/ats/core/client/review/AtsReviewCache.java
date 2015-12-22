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
package org.eclipse.osee.ats.core.client.review;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Review cache for reviewable workflows
 *
 * @author Donald G. Dunne
 */
public class AtsReviewCache {

   // GUID to Reivew Artifacts
   private static Map<String, Collection<AbstractReviewArtifact>> teamReviewCache =
      new ConcurrentHashMap<String, Collection<AbstractReviewArtifact>>();

   private AtsReviewCache() {
      // Utility class
   }

   public static void decache(Artifact sma) {
      if (sma != null) {
         teamReviewCache.remove(sma.getGuid());
      }
   }

   public static void decache(TeamWorkFlowArtifact sma) {
      if (sma != null) {
         teamReviewCache.remove(sma.getGuid());
      }
   }

   public static Collection<AbstractReviewArtifact> getReviewArtifacts(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Collection<AbstractReviewArtifact> reviews = teamReviewCache.get(teamArt.getGuid());
      if (reviews == null || containsDeleted(reviews)) {
         //         System.out.println("caching reviews for " + teamArt.toStringWithId());
         // Get and cache tasks
         reviews =
            teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowToReview_Review, AbstractReviewArtifact.class);
         teamReviewCache.put(teamArt.getGuid(), reviews);
      }
      return reviews;
   }

   private static boolean containsDeleted(Collection<AbstractReviewArtifact> reviews) {
      boolean result = false;
      for (AbstractReviewArtifact review : reviews) {
         if (review.isDeleted()) {
            result = true;
            break;
         }
      }
      return result;
   }

}
