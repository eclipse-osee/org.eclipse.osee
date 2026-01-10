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

package org.eclipse.osee.ats.ide.search.internal.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.IAtsConfigCacheQuery;
import org.eclipse.osee.ats.api.query.IAtsConfigQuery;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryService;
import org.eclipse.osee.ats.core.query.AtsConfigCacheQueryImpl;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.OrcsQueryService;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceImpl extends AbstractAtsQueryService {

   private final AtsApi atsApi;

   public AtsQueryServiceImpl(AtsApi atsApi, JdbcService jdbcService) {
      super(jdbcService, atsApi);
      this.atsApi = atsApi;
   }

   @Override
   public ArtifactToken getArtifactByAtsId(String id) {
      AtsSearchData data = new AtsSearchData();
      data.setAtsIds(Arrays.asList(id));
      XResultData rd = atsApi.getServerEndpoints().getActionEndpoint().queryIds(data);
      if (rd.getIds().size() > 0) {
         return ArtifactQuery.getArtifactFromId(ArtifactId.valueOf(rd.getIds().iterator().next()), atsApi.branch());
      }
      return ArtifactToken.SENTINEL;
   }

   @Override
   public ArtifactToken getArtifactByLegacyPcrId(String id) {
      try {
         Collection<ArtifactToken> wfArts = getArtifactsByLegacyPcrId(id);
         if (wfArts.size() == 1) {
            return wfArts.iterator().next();
         } else if (wfArts.size() > 1) {
            throw new OseeStateException("More than 1 artifact exists with legacy id [%s]", id);
         }
      } catch (ItemDoesNotExist ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public Collection<ArtifactToken> getArtifactsByLegacyPcrId(String id) {
      AtsSearchData data = new AtsSearchData();
      data.setLegacyIds(Arrays.asList(id));
      XResultData rd = atsApi.getServerEndpoints().getActionEndpoint().queryIds(data);
      List<ArtifactId> artIds = new ArrayList<>();
      for (String retId : rd.getIds()) {
         artIds.add(ArtifactId.valueOf(retId));
      }
      List<Artifact> arts = ArtifactQuery.getArtifactListFrom(artIds, atsApi.branch());
      return Collections.castAll(arts);
   }

   @Override
   public IAtsQuery createQuery(WorkItemType workItemType, WorkItemType... workItemTypes) {
      Conditions.checkNotNull(workItemType, "workItemType");
      AtsQueryImpl query = new AtsQueryImpl(atsApi);
      query.isOfType(workItemType);
      if (workItemTypes != null) {
         for (WorkItemType type : workItemTypes) {
            query.isOfType(type);
         }
      }
      return query;
   }

   @Override
   public IAtsConfigQuery createQuery(ArtifactTypeToken... artifactType) {
      AtsConfigQueryImpl query = new AtsConfigQueryImpl(atsApi);
      query.isOfType(artifactType);
      return query;
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(Collection<? extends ArtifactId> ids, BranchId branch) {
      return Collections.castAll(ArtifactQuery.getArtifactListFrom(ids, branch));
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(BranchId branch, boolean includeInherited,
      ArtifactTypeToken... artifactType) {
      List<ArtifactTypeToken> types = Arrays.asList(artifactType);
      if (includeInherited) {
         if (artifactType.length == 1) {
            return Collections.castAll(ArtifactQuery.getArtifactListFromTypeWithInheritence(types.iterator().next(),
               branch, DeletionFlag.EXCLUDE_DELETED));
         } else {
            throw new UnsupportedOperationException("Not supported on client");
         }
      }
      return Collections.castAll(ArtifactQuery.getArtifactListFromTypes(types, branch, DeletionFlag.EXCLUDE_DELETED));
   }

   @Override
   public ArtifactToken getArtifactToken(ArtifactId artifactId) {
      return ArtifactQuery.getArtifactTokenFromId(atsApi.getAtsBranch(), artifactId);
   }

   @Override
   public Collection<ArtifactToken> getRelatedToTokens(BranchToken branch, ArtifactId artifact,
      RelationTypeSide relationType, ArtifactTypeId artifactType) {
      HashCollection<ArtifactId, ArtifactToken> tokenMap = ArtifactQuery.getArtifactTokenListFromRelated(
         atsApi.getAtsBranch(), java.util.Collections.singleton(artifact), artifactType, relationType);
      Collection<ArtifactToken> result = tokenMap.getValues(artifact);
      if (result != null) {
         return result;
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public Artifact getArtifact(Long id) {
      Conditions.assertTrue(id > 0, "Art Id must be > 0, not %s", id);
      return ArtifactQuery.checkArtifactFromId(ArtifactId.valueOf(id), atsApi.getAtsBranch());
   }

   @Override
   public <T extends IAtsObject> Artifact getArtifact(T atsObject) {
      Artifact result = null;
      try {
         if (atsObject.getStoreObject() instanceof Artifact) {
            result = (Artifact) atsObject.getStoreObject();
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
   public <T extends ArtifactId> Artifact getArtifact(T artifact) {
      Artifact result;
      if (artifact instanceof Artifact) {
         result = (Artifact) artifact;
      } else if (artifact instanceof ArtifactToken) {
         if (((ArtifactToken) artifact).getBranch().isInvalid()) {
            result = ArtifactQuery.getArtifactOrNull(artifact, atsApi.getAtsBranch(), DeletionFlag.EXCLUDE_DELETED);
         } else {
            result = ArtifactQuery.getArtifactOrNull((ArtifactToken) artifact, DeletionFlag.EXCLUDE_DELETED);
         }
      } else if (artifact instanceof IAtsObject) {
         IAtsObject atsObject = (IAtsObject) artifact;
         if (atsObject.getStoreObject() instanceof Artifact) {
            result = ArtifactQuery.getArtifactFromToken((Artifact) atsObject.getStoreObject());
         } else {
            result = getArtifact(atsObject.getId());
         }
      } else {
         result = getArtifact(artifact.getId());
      }
      return result;
   }

   @Override
   public Artifact getArtifact(ArtifactId artifact, BranchToken branch) {
      return getArtifact(artifact, branch, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public Artifact getArtifact(ArtifactId artifact, BranchToken branch, DeletionFlag deletionFlag) {
      return ArtifactQuery.getArtifactOrNull(artifact, branch, deletionFlag);
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(Collection<Long> ids) {
      List<ArtifactId> artifactIds = new ArrayList<>(ids.size());
      for (Long id : ids) {
         artifactIds.add(ArtifactId.valueOf(id));
      }
      return Collections.castAll(
         ArtifactQuery.getArtifactListFrom(artifactIds, atsApi.getAtsBranch(), DeletionFlag.EXCLUDE_DELETED));
   }

   @Override
   public ArtifactToken getArtifactByName(ArtifactTypeToken artType, String name) {
      return ArtifactQuery.checkArtifactFromTypeAndName(artType, name, atsApi.getAtsBranch());
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(AttributeTypeToken attrType, String value, BranchToken branch) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromAttribute(attrType, value, branch));
   }

   @Override
   public ArtifactToken getArtifactByNameOrSentinel(ArtifactTypeToken artType, String name) {

      if (ArtifactQuery.checkArtifactFromTypeAndName(artType, name, atsApi.getAtsBranch()) != null) {
         return ArtifactQuery.checkArtifactFromTypeAndName(artType, name, atsApi.getAtsBranch());
      }
      return ArtifactReadable.SENTINEL;

   }

   @Override
   public ArtifactToken getHistoricalArtifactOrNull(ArtifactId artifact, TransactionToken transaction,
      DeletionFlag deletionFlag) {
      return ArtifactQuery.getHistoricalArtifactOrNull(artifact, transaction, deletionFlag);
   }

   @Override
   public ArtifactToken getArtifactByGuid(String guid) {
      return ArtifactQuery.getArtifactFromId(guid, atsApi.getAtsBranch());
   }

   @Override
   public ArtifactToken getArtifactByGuidOrSentinel(String guid) {
      if (ArtifactQuery.getArtifactFromId(guid, atsApi.getAtsBranch()) != null) {
         return ArtifactQuery.getArtifactFromId(guid, atsApi.getAtsBranch());
      }
      return ArtifactReadable.SENTINEL;
   }

   @Override
   public List<ArtifactToken> getArtifactsFromTypeWithInheritence(ArtifactTypeToken artifactType, BranchId branch,
      DeletionFlag deletionFlag) {
      return Collections.castAll(
         ArtifactQuery.getArtifactListFromTypeWithInheritence(artifactType, branch, deletionFlag));
   }

   @Override
   public IAtsConfigCacheQuery createConfigCacheQuery(ArtifactTypeToken... artifactType) {
      AtsConfigCacheQueryImpl query = new AtsConfigCacheQueryImpl(atsApi);
      query.isOfType(artifactType);
      return query;
   }

   @Override
   public List<ArtifactToken> getArtifactsFromTypeAndAttribute(ArtifactTypeToken artifactType,
      AttributeTypeToken attributeType, String attributeValue, BranchId branch) {
      return Collections.castAll(
         ArtifactQuery.getArtifactListFromTypeAndAttribute(artifactType, attributeType, attributeValue, branch));
   }

   @Override
   public List<ArtifactToken> getArtifactsFromAttributeValues(AttributeTypeId attributeType,
      Collection<ArtifactToken> ids, BranchId branch) {
      return Collections.castAll(
         ArtifactQuery.getArtifactListFromAttributeValues(attributeType, Collections.castAll(ids), branch));
   }

   @Override
   public List<ArtifactToken> getArtifactsFromAttributeValues(AttributeTypeId attributeType,
      Collection<String> attributeValues, BranchId branch, int artifactCountEstimate) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromAttributeValues(attributeType, attributeValues,
         branch, artifactCountEstimate));
   }

   @Override
   public List<ArtifactToken> getArtifactsFromTypeAndAttribute(ArtifactTypeToken artifactType,
      AttributeTypeId attributeType, Set<ArtifactToken> ids, BranchId branch) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromTypeAndAttribute(artifactType, attributeType,
         Collections.castAll(ids), branch));
   }

   @Override
   public Collection<? extends ArtifactToken> getArtifactsFromAttributeKeywords(BranchId branch, String userId,
      boolean isMatchWordOrder, DeletionFlag deletionFlag, boolean caseSensitive, AttributeTypeString... attrType) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromAttributeKeywords(branch, userId, isMatchWordOrder,
         deletionFlag, caseSensitive, attrType));
   }

   @Override
   public Collection<ArtifactToken> getArtifactsById(Collection<ArtifactId> artifactIds, BranchToken branch,
      DeletionFlag deletionFlag) {
      return Collections.castAll(ArtifactQuery.getArtifactListFrom(artifactIds, branch, deletionFlag));
   }

   @Override
   public ArtifactToken getArtifactFromAttribute(AttributeTypeToken attrType, String value, BranchId branch) {
      return ArtifactQuery.getArtifactFromAttribute(attrType, value, branch);
   }

   @Override
   public List<ArtifactToken> getArtifactsFromAttributeValues(AttributeTypeToken attributeType,
      Collection<String> values, int estimatedCount) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromAttributeValues(attributeType, values,
         atsApi.getAtsBranch(), estimatedCount));
   }

   @Override
   public ArtifactToken getArtifactFromTypeAndAttribute(ArtifactTypeToken artifactType,
      AttributeTypeToken attributeType, String value, BranchId branch) {
      return ArtifactQuery.getArtifactFromTypeAndAttribute(artifactType, attributeType, value, branch);
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(ArtifactTypeToken artType, AttributeTypeToken attrType, String value,
      BranchToken branch) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromTypeAndAttribute(artType, attrType, value, branch));
   }

   @Override
   public Collection<ArtifactToken> getArtifactsFromTypeAndName(ArtifactTypeToken artType, String name,
      BranchToken branch, QueryOption[] queryOption) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromTypeAndName(artType, name, branch, queryOption));
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(ArtifactTypeToken artType, BranchId branch) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromType(artType, branch));
   }

   @Override
   public List<ArtifactToken> asArtifacts(ArtifactTypeToken user, RelationTypeSide relTypeSide) {
      throw new UnsupportedOperationException("not supported on client");
   }

   @Override
   public IAtsQuery createQueryWithApplic(BranchViewToken configTok, BranchId configurationBranch) {
      throw new UnsupportedOperationException("not supported on client");
   }

   @Override
   public QueryBuilder fromAtsBranch() {
      return fromBranch(atsApi.branch());
   }

   @Override
   public QueryBuilder fromBranch(BranchToken branch) {
      return OrcsQueryService.fromBranch(branch);
   }

   @Override
   public ArtifactReadable getArtifactNew(Long id) {
      return getArtifactNew(ArtifactId.valueOf(id));
   }

   @Override
   public ArtifactReadable getArtifactNew(ArtifactId artId) {
      return (ArtifactReadable) getArtifact(artId);
   }

}
