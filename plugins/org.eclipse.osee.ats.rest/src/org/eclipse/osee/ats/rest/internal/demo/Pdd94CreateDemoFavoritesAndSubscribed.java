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

package org.eclipse.osee.ats.rest.internal.demo;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd94CreateDemoFavoritesAndSubscribed extends AbstractPopulateDemoDatabase {

   public Pdd94CreateDemoFavoritesAndSubscribed(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      // Mark all CIS Code "Team Workflows" as Favorites for Joe Smith
      IAtsChangeSet changes = atsApi.createChangeSet("Set Favorites");
      Collection<ArtifactToken> artifactsFromTypeAndName =
         atsApi.getQueryService().getArtifactsFromTypeAndName(AtsArtifactTypes.DemoCodeTeamWorkflow, "Diagram View",
            atsApi.getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS);
      for (ArtifactToken workflow : artifactsFromTypeAndName) {
         changes.toggleFavorite(DemoUsers.Joe_Smith, workflow, true);
      }

      // Mark all Tools Team "Team Workflows" as Subscribed for Joe Smith
      Collection<ArtifactToken> artifactsFromTypeAndName2 = atsApi.getQueryService().getArtifactsFromTypeAndName(
         AtsArtifactTypes.DemoCodeTeamWorkflow, "Even", atsApi.getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS);
      for (ArtifactToken workflow : artifactsFromTypeAndName2) {
         changes.toggleSubscribed(DemoUsers.Joe_Smith, workflow, true);
      }
      TransactionToken tx = changes.execute();
      if (tx.isInvalid()) {
         rd.errorf("Changes not saved");
      }
   }

}
