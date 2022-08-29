/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public interface IAtsQueryService {

   IAtsQuery createQuery(WorkItemType workItemType, WorkItemType... workItemTypes);

   default public Collection<IAtsWorkItem> getWorkItems(Collection<ArtifactToken> artifacts) {
      return Collections.castAll(artifacts);
   }

   /**
    * Run query that returns art_ids of IAtsWorkItems to return
    */
   Collection<IAtsWorkItem> getWorkItemsFromQuery(String query, Object... data);

   IAtsWorkItemFilter createFilter(Collection<? extends IAtsWorkItem> workItems);

   ArrayList<AtsSearchData> getSavedSearches(String namespace);

   /**
    * Persists the saved ATS searches for the current user
    */
   TransactionId saveSearch(AtsSearchData data);

   TransactionId removeSearch(AtsSearchData data);

   AtsSearchData getSearch(AtsUser atsUser, Long id);

   AtsSearchData getSearch(String jsonStr);

   AtsSearchData createSearchData(String namespace, String searchName);

   @NonNull
   IAtsConfigQuery createQuery(ArtifactTypeToken... artifactType);

   Collection<ArtifactToken> getArtifacts(Collection<ArtifactId> collection, BranchId branch);

   void runUpdate(String query, Object... data);

   Collection<ArtifactToken> getArtifactsFromQuery(String query, Object... data);

   Collection<ArtifactToken> getArtifacts(BranchId branch, boolean includeInherited, ArtifactTypeToken... artifactType);

   List<ArtifactId> getArtifactIdsFromQuery(String query, Object... data);

   ArtifactToken getArtifactToken(ArtifactId artifactId);

   ArtifactToken getArtifactTokenOrSentinal(ArtifactId valueOf);

   Collection<ArtifactToken> getRelatedToTokens(BranchToken branch, ArtifactId artifact, RelationTypeSide relationType, ArtifactTypeId artifactType);

   /**
    * @param id artifact id or ATS Id
    */
   IAtsWorkItem getWorkItem(String id);

   /**
    * @param ATS Id
    */
   IAtsWorkItem getWorkItemByAtsId(String atsId);

   Map<String, IAtsWorkItem> getWorkItemsByAtsId(Collection<String> atsIds);

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

   ArtifactToken getArtifactByName(ArtifactTypeToken artifactType, String name);

   ArtifactToken getArtifactByNameOrSentinel(ArtifactTypeToken artifactType, String name);

   /**
    * @return artifact or null
    */
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

   Collection<ArtifactToken> getArtifactsById(Collection<ArtifactId> artifacts);

   Collection<ArtifactToken> getArtifactsById(Collection<ArtifactId> modified, BranchToken branch, DeletionFlag deletionFlag);

   Collection<ArtifactToken> getArtifacts(AtsSearchData atsSearchData, ISearchCriteriaProvider provider);

   Collection<ArtifactToken> getArtifactsNew(AtsSearchData atsSearchData, ISearchCriteriaProvider provider);

   @NonNull
   ArtifactToken getArtifactFromAttribute(AttributeTypeToken attrType, String value, BranchId branch);

   List<ArtifactToken> getArtifactListFromAttributeValues(AttributeTypeToken attributeType, Collection<String> values, int estimatedCount);

   Collection<ArtifactToken> getArtifactsFromObjects(Collection<? extends IAtsObject> atsObjects);

   ArtifactToken getArtifactFromTypeAndAttribute(ArtifactTypeToken artifactType, AttributeTypeToken attributeType, String value, BranchId branch);

   Collection<ArtifactToken> getArtifacts(AttributeTypeToken attrType, String value, BranchToken branch);

   Collection<ArtifactToken> getArtifacts(ArtifactTypeToken artType, AttributeTypeToken attrType, String value, BranchToken branch);

   ArtifactToken getArtifactByName(ArtifactTypeToken artType, String name, BranchToken branch);

   /**
    * Run query and return list of column,value of results
    */
   List<Map<String, String>> query(String query, Object... data);

}