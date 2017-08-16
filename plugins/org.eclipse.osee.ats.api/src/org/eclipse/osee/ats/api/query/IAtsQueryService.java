/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;

/**
 * @author Donald G. Dunne
 */
public interface IAtsQueryService {

   IAtsQuery createQuery(WorkItemType workItemType, WorkItemType... workItemTypes);

   /**
    * Run query that returns art_ids of IAtsWorkItems to return
    */
   Collection<IAtsWorkItem> getWorkItemsFromQuery(String query, Object... data);

   IAtsWorkItemFilter createFilter(Collection<? extends IAtsWorkItem> workItems);

   ArrayList<AtsSearchData> getSavedSearches(IAtsUser atsUser, String namespace);

   void saveSearch(IAtsUser atsUser, AtsSearchData data);

   void removeSearch(IAtsUser atsUser, AtsSearchData data);

   AtsSearchData getSearch(IAtsUser atsUser, Long uuid);

   AtsSearchData getSearch(String jsonStr);

   AtsSearchData createSearchData(String namespace, String searchName);

   @NonNull
   IAtsConfigQuery createQuery(IArtifactType... artifactType);

   Collection<ArtifactToken> getArtifacts(List<ArtifactId> ids, BranchId branch);

   List<IAtsWorkItem> getWorkItemListByIds(String id);

   List<ArtifactToken> getArtifactListByIdsStr(String id);

   /**
    * @param id guid, uuid or AtsId
    */
   ArtifactToken getArtifactById(String id);

   void runUpdate(String query, Object... data);

   IAtsOrcsScriptQuery createOrcsScriptQuery(String query, Object... data);

   Collection<ArtifactToken> getArtifactsFromQuery(String query, Object... data);

   Collection<ArtifactToken> getArtifacts(IArtifactType artifactType, BranchId branch);

   List<String> getIdsFromStr(String idList);

   List<ArtifactId> getArtifactIdsFromQuery(String query, Object... data);

}
