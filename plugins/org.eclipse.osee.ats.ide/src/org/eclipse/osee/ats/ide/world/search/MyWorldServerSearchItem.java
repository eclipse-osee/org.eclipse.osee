/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.world.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * Create search criteria on client, ship to server to run and return result tokens to load on client.
 *
 * @author Donald G. Dunne
 */
public class MyWorldServerSearchItem extends UserSearchItem {

   public MyWorldServerSearchItem(String name, boolean currentUser) {
      this(name, null);
      setUseCurrentUser(currentUser);
   }

   public MyWorldServerSearchItem(String name, AtsUser user) {
      super(name, user, AtsImage.GLOBE);
   }

   public MyWorldServerSearchItem(MyWorldServerSearchItem myWorldSearchItem) {
      super(myWorldSearchItem);
   }

   @Override
   public Collection<Artifact> searchIt(AtsUser user) {

      AtsApi atsApi = AtsApiService.get();

      QueryBuilder query = ArtifactQuery.createQueryBuilderServer(AtsApiService.get().getAtsBranch());
      query.and(AtsAttributeTypes.CurrentStateAssignee, Arrays.asList(user.getArtifactId().getIdString()));

      List<ArtifactReadable> artToks = AtsApiService.get().getServerEndpoints().getArtifactEp().ideSearch(query);
      List<ArtifactId> ardIds = new ArrayList<>();
      for (ArtifactReadable tok : artToks) {
         ardIds.add(tok.getArtifactId());
      }
      Set<Artifact> results = new HashSet<>();
      for (Artifact art : ArtifactQuery.getArtifactListFrom(ardIds, AtsApiService.get().getAtsBranch())) {
         if (art.isOfType(AtsArtifactTypes.Task)) {
            IAtsTask task = atsApi.getWorkItemService().getTask(art);
            results.add((Artifact) task.getParentTeamWorkflow().getStoreObject());
         } else {
            results.add(art);
         }
      }

      return results;
   }

   @Override
   public WorldUISearchItem copy() {
      return new MyWorldServerSearchItem(this);
   }

}