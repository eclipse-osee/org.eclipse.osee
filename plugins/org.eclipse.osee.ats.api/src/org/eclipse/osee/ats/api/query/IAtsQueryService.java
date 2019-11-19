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
import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;

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

   AtsSearchData getSearch(IAtsUser atsUser, Long id);

   AtsSearchData getSearch(String jsonStr);

   AtsSearchData createSearchData(String namespace, String searchName);

   @NonNull
   IAtsConfigQuery createQuery(ArtifactTypeToken... artifactType);

   Collection<ArtifactToken> getArtifacts(List<ArtifactId> ids, BranchId branch);

   void runUpdate(String query, Object... data);

   Collection<ArtifactToken> getArtifactsFromQuery(String query, Object... data);

   Collection<ArtifactToken> getArtifacts(BranchId branch, boolean includeInherited, ArtifactTypeToken... artifactType);

   List<ArtifactId> getArtifactIdsFromQuery(String query, Object... data);

   ArtifactToken getArtifactToken(ArtifactId artifactId);

   ArtifactToken getArtifactTokenOrSentinal(ArtifactId valueOf);

   List<ArtifactToken> getArtifactTokensFromQuery(String query, Object... data);

   Collection<ArtifactToken> getRelatedToTokens(BranchId branch, ArtifactId artifact, RelationTypeSide relationType, ArtifactTypeId artifactType);

   /**
    * @param id artifact id or ATS Id
    */
   IAtsWorkItem getWorkItem(String id);

   /**
    * @param comma separated id artifact id or ATS Id
    */
   List<IAtsWorkItem> getWorkItemsByIds(String ids);

   /**
    * @param comma separated id artifact id or ATS Id
    */
   List<ArtifactToken> getArtifactsByIds(String ids);

   /**
    * @param comma separated id artifact id or ATS Id
    */
   ArtifactToken getArtifactById(String id);

   /**
    * @param comma separated ids
    */
   List<String> getIdsFromStr(String ids);

   ArtifactToken getArtifactByAtsId(String id);

   ArtifactToken getArtifactByIdOrAtsId(String id);

   ArtifactToken getArtifactByLegacyPcrId(String id);

   Collection<ArtifactToken> getArtifactsByLegacyPcrId(String id);

   Collection<IAtsWorkItem> getWorkItemsByLegacyPcrId(String id);

   @Nullable
   ArtifactToken getArtifact(Long id);

   @Nullable
   <T extends ArtifactId> ArtifactToken getArtifact(T artifact);

   @Nullable
   <T extends IAtsObject> ArtifactToken getArtifact(T atsObject);

   <T> T getConfigItem(Long id);

   <T> T getConfigItem(ArtifactId artId);

   IAtsTeamWorkflow getTeamWf(Long id);

   IAtsTeamWorkflow getTeamWf(ArtifactId artifact);

   default IAtsWorkItem getTeamWf(long id) {
      return getTeamWf(ArtifactId.valueOf(id));
   }

   List<ArtifactToken> getArtifacts(ArtifactTypeToken artifactType);

   ArtifactToken getConfigArtifact(IAtsConfigObject atsConfigObject);

   ArtifactToken getArtifact(ArtifactId artifact, BranchId branch);

   Collection<ArtifactToken> getArtifacts(Collection<Long> ids);

   ArtifactToken getArtifactByName(ArtifactTypeId artifactType, String name);

   ArtifactToken getArtifactByNameOrSentinel(ArtifactTypeId artifactType, String name);

   ArtifactToken getArtifact(ArtifactId artifact, BranchId branch, DeletionFlag deletionFlag);

   ArtifactToken getHistoricalArtifactOrNull(ArtifactId artifact, TransactionToken transaction, DeletionFlag deletionFlag);

   /**
    * This method should be used sparingly. Use long ids instead.
    */
   ArtifactToken getArtifactByGuid(String guid);

   ArtifactToken getArtifactByGuidOrSentinel(String guid);

   /**
    * Search using comma deliminated long ids and/or ATS Ids
    */
   Collection<ArtifactToken> getArtifactsByIdsOrAtsIds(String searchStr);

   List<ArtifactToken> getArtifactListFromTypeWithInheritence(ArtifactTypeToken artifactType, BranchId branch, DeletionFlag deletionFlag);

   /**
    * Query for the AtsConfigurations cache. Since this is pre-loaded, this is the best query for configuration
    * information.
    */
   IAtsConfigCacheQuery createConfigCacheQuery(ArtifactTypeToken... artifactType);

   ArtifactToken getOrCreateArtifact(ArtifactToken parent, ArtifactToken artifact, IAtsChangeSet changes);

   List<ArtifactToken> getArtifactListFromTypeAndAttribute(ArtifactTypeId artifactType, AttributeTypeId attributeType, String attributeValue, BranchId branch);

   List<ArtifactToken> getArtifactListFromAttributeValues(AttributeTypeId attributeType, Collection<ArtifactToken> attributeValues, BranchId branch);

   List<ArtifactToken> getArtifactListFromAttributeValues(AttributeTypeId attributeType, Collection<String> attributeValues, BranchId branch, int artifactCountEstimate);

   List<ArtifactToken> getArtifactListFromTypeAndAttribute(ArtifactTypeToken artifactType, AttributeTypeId attributeType, Set<ArtifactToken> ids, BranchId branch);

   Collection<? extends ArtifactToken> getArtifactListFromAttributeKeywords(BranchId branch, String userId, boolean isMatchWordOrder, DeletionFlag deletionFlag, boolean caseSensitive, AttributeTypeString... attrType);

}