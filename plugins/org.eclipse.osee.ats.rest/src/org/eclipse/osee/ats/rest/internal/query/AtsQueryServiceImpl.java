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

package org.eclipse.osee.ats.rest.internal.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.IAtsConfigCacheQuery;
import org.eclipse.osee.ats.api.query.IAtsConfigQuery;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryService;
import org.eclipse.osee.ats.core.query.AtsConfigCacheQueryImpl;
import org.eclipse.osee.ats.core.query.AtsWorkItemFilter;
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
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceImpl extends AbstractAtsQueryService {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;
   private final QueryFactory query;

   public AtsQueryServiceImpl(AtsApi atsApi, JdbcService jdbcService, OrcsApi orcsApi) {
      super(jdbcService, atsApi);
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
      this.query = orcsApi.getQueryFactory();
   }

   private QueryBuilder getQuery() {
      return query.fromBranch(atsApi.getAtsBranch());
   }

   @Override
   public IAtsQuery createQuery(WorkItemType workItemType, WorkItemType... workItemTypes) {
      AtsQueryImpl query = new AtsQueryImpl(atsApi, orcsApi);
      query.isOfType(workItemType);
      for (WorkItemType type : workItemTypes) {
         query.isOfType(type);
      }
      return query;
   }

   @Override
   public IAtsConfigQuery createQuery(ArtifactTypeToken... artifactType) {
      AtsConfigQueryImpl query = new AtsConfigQueryImpl(atsApi, orcsApi);
      query.isOfType(artifactType);
      return query;
   }

   @Override
   public IAtsConfigCacheQuery createConfigCacheQuery(ArtifactTypeToken... artifactType) {
      AtsConfigCacheQueryImpl query = new AtsConfigCacheQueryImpl(atsApi);
      query.isOfType(artifactType);
      return query;
   }

   @Override
   public IAtsWorkItemFilter createFilter(Collection<? extends IAtsWorkItem> workItems) {
      return new AtsWorkItemFilter(workItems, atsApi);
   }

   @Override
   public TransactionId saveSearch(AtsSearchData data) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public TransactionId removeSearch(AtsSearchData data) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public AtsSearchData getSearch(AtsUser atsUser, Long id) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public AtsSearchData getSearch(String jsonStr) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(Collection<ArtifactId> ids, BranchId branch) {
      return Collections.castAll(query.fromBranch(branch).andIds(ids).getResults().getList());
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(BranchId branch, boolean includeInherited, ArtifactTypeToken... artifactType) {
      if (includeInherited) {
         return Collections.castAll(query.fromBranch(branch).andIsOfType(artifactType).getResults().getList());
      } else {
         return Collections.castAll(query.fromBranch(branch).andTypeEquals(artifactType).getResults().getList());
      }
   }

   @Override
   public ArtifactToken getArtifactToken(ArtifactId artifactId) {
      return getQuery().andId(artifactId).asArtifactToken();
   }

   @Override
   public Collection<ArtifactToken> getRelatedToTokens(BranchToken branch, ArtifactId artifact, RelationTypeSide relationType, ArtifactTypeId artifactType) {
      HashCollection<ArtifactId, ArtifactToken> tokenMap = TokenSearchOperations.getArtifactTokenListFromRelated(branch,
         java.util.Collections.singleton(artifact), artifactType, relationType, orcsApi, jdbcClient);
      Collection<ArtifactToken> result = tokenMap.getValues(artifact);
      if (result != null) {
         return result;
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public <T extends ArtifactId> ArtifactReadable getArtifact(T artifact) {
      ArtifactReadable result = null;
      try {
         if (artifact instanceof ArtifactReadable) {
            result = (ArtifactReadable) artifact;
         } else if (artifact instanceof IAtsObject) {
            IAtsObject atsObject = (IAtsObject) artifact;
            if (atsObject.getStoreObject() instanceof ArtifactReadable) {
               result = (ArtifactReadable) atsObject.getStoreObject();
            } else {
               result = getArtifact(atsObject.getId());
            }
         } else {
            result = getArtifact(artifact.getId());
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public <T extends IAtsObject> ArtifactReadable getArtifact(T atsObject) {
      ArtifactReadable result = null;
      try {
         if (atsObject.getStoreObject() instanceof ArtifactReadable) {
            result = (ArtifactReadable) atsObject.getStoreObject();
         } else {
            result = getArtifact(atsObject.getId());
            if (result != null) {
               atsObject.setStoreObject(result);
            }
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public ArtifactReadable getArtifact(Long id) {
      return getQuery().andUuid(id).getResults().getAtMostOneOrNull();
   }

   @Override
   public ArtifactReadable getArtifact(ArtifactId artifact, BranchId branch) {
      return (ArtifactReadable) query.fromBranch(branch).andId(artifact).getArtifactOrNull();
   }

   @Override
   public ArtifactReadable getArtifact(ArtifactId artifact, BranchId branch, DeletionFlag deletionFlag) {
      return (ArtifactReadable) query.fromBranch(branch).andId(artifact).includeDeletedArtifacts().getArtifactOrNull();
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(Collection<Long> ids) {
      Collection<ArtifactToken> artifacts = new LinkedList<>();
      Iterator<ArtifactReadable> iterator = getQuery().andUuids(ids).getResults().iterator();
      while (iterator.hasNext()) {
         artifacts.add(iterator.next());
      }
      return artifacts;
   }

   @Override
   public ArtifactToken getArtifactByName(ArtifactTypeToken artifactType, String name) {
      return getQuery().andIsOfType(artifactType).andNameEquals(name).getResults().getExactlyOne();

   }

   @Override
   public ArtifactToken getArtifactByNameOrSentinel(ArtifactTypeToken artifactType, String name) {
      return getQuery().andIsOfType(artifactType).andNameEquals(name).getResults().getAtMostOneOrDefault(
         ArtifactReadable.SENTINEL);

   }

   @Override
   public ArtifactToken getHistoricalArtifactOrNull(ArtifactId artifact, TransactionToken transaction, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public ArtifactToken getArtifactByGuid(String guid) {
      return orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).andGuid(guid).getResults().getExactlyOne();
   }

   @Override
   public ArtifactToken getArtifactByGuidOrSentinel(String guid) {
      return orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).andGuid(
         guid).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
   }

   @Override
   public List<ArtifactToken> getArtifactListFromTypeWithInheritence(ArtifactTypeToken artifactType, BranchId branch, DeletionFlag deletionFlag) {
      return Collections.castAll(
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(artifactType).includeDeletedArtifacts(
            deletionFlag == DeletionFlag.INCLUDE_DELETED).getResults().getList());
   }

   @Override
   public List<ArtifactToken> getArtifactListFromTypeAndAttribute(ArtifactTypeId artifactType, AttributeTypeId attributeType, String attributeValue, BranchId branch) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public List<ArtifactToken> getArtifactListFromAttributeValues(AttributeTypeId attributeType, Collection<ArtifactToken> attributeValues, BranchId branch) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public List<ArtifactToken> getArtifactListFromAttributeValues(AttributeTypeId attributeType, Collection<String> attributeValues, BranchId branch, int artifactCountEstimate) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public List<ArtifactToken> getArtifactListFromTypeAndAttribute(ArtifactTypeToken artifactType, AttributeTypeId attributeType, Set<ArtifactToken> ids, BranchId branch) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public Collection<? extends ArtifactToken> getArtifactListFromAttributeKeywords(BranchId branch, String userId, boolean isMatchWordOrder, DeletionFlag deletionFlag, boolean caseSensitive, AttributeTypeString... attrType) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public Collection<ArtifactToken> getArtifactsById(Collection<ArtifactId> artifactIds, BranchToken branch, DeletionFlag deletionFlag) {
      return Collections.castAll(
         orcsApi.getQueryFactory().fromBranch(branch).andIds(artifactIds).includeDeletedArtifacts(
            deletionFlag == DeletionFlag.INCLUDE_DELETED).getResults().getList());
   }

   @Override
   public ArtifactToken getArtifactFromAttribute(AttributeTypeToken attrType, String value, BranchId branch) {
      List<ArtifactReadable> arts = orcsApi.getQueryFactory().fromBranch(branch).and(attrType, Arrays.asList(value),
         QueryOption.EXACT_MATCH_OPTIONS).getResults().getList();
      if (arts.size() == 1) {
         return arts.iterator().next();
      }
      if (arts.size() > 1) {
         throw new OseeArgumentException("Multiple artifacts found with value [%s]", value);
      }
      return ArtifactToken.SENTINEL;
   }

   @Override
   public List<ArtifactToken> getArtifactListFromAttributeValues(AttributeTypeToken attributeType, Collection<String> values, int estimatedCount) {
      return Collections.castAll(
         orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).and(attributeType, values).getResults().getList());
   }

   @Override
   public ArtifactToken getArtifactFromTypeAndAttribute(ArtifactTypeToken artifactType, AttributeTypeToken attributeType, String value, BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(artifactType).and(attributeType,
         Arrays.asList(value)).getResults().getOneOrNull();
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(AttributeTypeToken attrType, String value, BranchToken branch) {
      return Collections.castAll(
         orcsApi.getQueryFactory().fromBranch(branch).and(attrType, Arrays.asList(value)).getResults().getList());
   }

}