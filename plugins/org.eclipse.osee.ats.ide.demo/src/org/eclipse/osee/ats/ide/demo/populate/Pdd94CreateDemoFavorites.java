/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.demo.populate;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.FavoritesManager;
import org.eclipse.osee.ats.ide.util.SubscribeManagerUI;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class Pdd94CreateDemoFavorites {

   public void run() throws Exception {
      // Mark all CIS Code "Team Workflows" as Favorites for Joe Smith
      for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.DemoCodeTeamWorkflow,
         "Diagram View", AtsApiService.get().getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS)) {
         new FavoritesManager((AbstractWorkflowArtifact) art).toggleFavorite(false);
      }

      // Mark all Tools Team "Team Workflows" as Subscribed for Joe Smith
      for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.DemoCodeTeamWorkflow, "Even",
         AtsApiService.get().getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS)) {
         new SubscribeManagerUI((AbstractWorkflowArtifact) art).toggleSubscribe(false);
      }
   }

}
