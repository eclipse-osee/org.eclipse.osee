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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.IAtsConfigCacheQuery;
import org.eclipse.osee.ats.api.query.IAtsConfigQuery;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryService;
import org.eclipse.osee.ats.core.query.AtsConfigCacheQueryImpl;
import org.eclipse.osee.ats.core.query.AtsWorkItemFilter;
import org.eclipse.osee.framework.core.JaxRsApi;
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
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceImpl extends AbstractAtsQueryService {

   private static final Pattern namespacePattern = Pattern.compile("\"namespace\"\\s*:\\s*\"(.*?)\"");

   private final AtsApi atsApi;
   private final JaxRsApi jaxRsApi;

   public AtsQueryServiceImpl(AtsApi atsApi, JdbcService jdbcService) {
      super(jdbcService, atsApi);
      this.atsApi = atsApi;
      this.jaxRsApi = atsApi.jaxRsApi();
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
   public IAtsWorkItemFilter createFilter(Collection<? extends IAtsWorkItem> workItems) {
      return new AtsWorkItemFilter(workItems, atsApi);
   }

   @Override
   public TransactionId saveSearch(AtsSearchData data) {
      User userArt = UserManager.getUser();
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Save ATS Search", atsApi.getUserService().getCurrentUser());

      TransactionId transaction = TransactionId.SENTINEL;
      try {
         IAttribute<Object> attr = getAttrById(userArt, data.getId());
         if (attr == null) {
            changes.addAttribute(userArt, CoreAttributeTypes.AtsActionSearch, jaxRsApi.toJson(data));
         } else {
            changes.setAttribute(userArt, attr, jaxRsApi.toJson(data));
         }
         if (!changes.isEmpty()) {
            transaction = changes.execute();
         }
         atsApi.getUserService().getCurrentUserNoCache();
      } catch (Exception ex) {
         throw new OseeCoreException("Unable to store ATS Search", ex);
      }
      return transaction;
   }

   private IAttribute<Object> getAttrById(ArtifactId artifact, Long attrId) {
      for (IAttribute<Object> attr : atsApi.getAttributeResolver().getAttributes(artifact,
         CoreAttributeTypes.AtsActionSearch)) {
         String jsonValue = (String) attr.getValue();
         try {
            AtsSearchData data = fromJson(jsonValue);
            if (attrId.equals(data.getId())) {
               return attr;
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      return null;
   }

   @Override
   public TransactionId removeSearch(AtsSearchData data) {
      User user = UserManager.getUser();
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Remove ATS Search", atsApi.getUserService().getCurrentUser());

      TransactionId transaction = TransactionId.SENTINEL;
      try {
         IAttribute<Object> attr = getAttrById(user, data.getId());
         if (attr != null) {
            changes.deleteAttribute(user, attr);
            transaction = changes.execute();
         }
         atsApi.getUserService().getCurrentUserNoCache();
      } catch (Exception ex) {
         throw new OseeCoreException("Unable to remove ATS Search", ex);
      }
      return transaction;
   }

   @Override
   public AtsSearchData getSearch(AtsUser atsUser, Long id) {
      try {
         ArtifactId userArt = atsApi.getStoreObject(atsUser);
         IAttribute<Object> attr = getAttrById(userArt, id);
         if (attr != null) {
            AtsSearchData existing = fromJson((String) attr.getValue());
            if (existing != null) {
               return existing;
            }
         }
         return null;
      } catch (Exception ex) {
         throw new OseeCoreException("Unable to get ATS Search", ex);
      }
   }

   private AtsSearchData fromJson(String jsonValue) {
      Matcher m = namespacePattern.matcher(jsonValue);
      if (m.find()) {
         String namespace = m.group(1);
         return atsApi.getSearchDataProvider(namespace).fromJson(namespace, jsonValue);
      }
      return null;
   }

   @Override
   public AtsSearchData getSearch(String jsonStr) {
      return fromJson(jsonStr);
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(Collection<ArtifactId> ids, BranchId branch) {
      return Collections.castAll(ArtifactQuery.getArtifactListFrom(ids, branch));
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(BranchId branch, boolean includeInherited, ArtifactTypeToken... artifactType) {
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
   public Collection<ArtifactToken> getRelatedToTokens(BranchToken branch, ArtifactId artifact, RelationTypeSide relationType, ArtifactTypeId artifactType) {
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
         result = ArtifactQuery.getArtifactOrNull((Artifact) artifact, DeletionFlag.EXCLUDE_DELETED);
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
   public Artifact getArtifact(ArtifactId artifact, BranchId branch) {
      return getArtifact(artifact, branch, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public Artifact getArtifact(ArtifactId artifact, BranchId branch, DeletionFlag deletionFlag) {
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
   public ArtifactToken getHistoricalArtifactOrNull(ArtifactId artifact, TransactionToken transaction, DeletionFlag deletionFlag) {
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
   public List<ArtifactToken> getArtifactsFromTypeWithInheritence(ArtifactTypeToken artifactType, BranchId branch, DeletionFlag deletionFlag) {
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
   public List<ArtifactToken> getArtifactsFromTypeAndAttribute(ArtifactTypeId artifactType, AttributeTypeId attributeType, String attributeValue, BranchId branch) {
      return Collections.castAll(
         ArtifactQuery.getArtifactListFromTypeAndAttribute(artifactType, attributeType, attributeValue, branch));
   }

   @Override
   public List<ArtifactToken> getArtifactsFromAttributeValues(AttributeTypeId attributeType, Collection<ArtifactToken> ids, BranchId branch) {
      return Collections.castAll(
         ArtifactQuery.getArtifactListFromAttributeValues(attributeType, Collections.castAll(ids), branch));
   }

   @Override
   public List<ArtifactToken> getArtifactsFromAttributeValues(AttributeTypeId attributeType, Collection<String> attributeValues, BranchId branch, int artifactCountEstimate) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromAttributeValues(attributeType, attributeValues,
         branch, artifactCountEstimate));
   }

   @Override
   public List<ArtifactToken> getArtifactsFromTypeAndAttribute(ArtifactTypeToken artifactType, AttributeTypeId attributeType, Set<ArtifactToken> ids, BranchId branch) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromTypeAndAttribute(artifactType, attributeType,
         Collections.castAll(ids), branch));
   }

   @Override
   public Collection<? extends ArtifactToken> getArtifactsFromAttributeKeywords(BranchId branch, String userId, boolean isMatchWordOrder, DeletionFlag deletionFlag, boolean caseSensitive, AttributeTypeString... attrType) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromAttributeKeywords(branch, userId, isMatchWordOrder,
         deletionFlag, caseSensitive, attrType));
   }

   @Override
   public Collection<ArtifactToken> getArtifactsById(Collection<ArtifactId> artifactIds, BranchToken branch, DeletionFlag deletionFlag) {
      return Collections.castAll(ArtifactQuery.getArtifactListFrom(artifactIds, branch, deletionFlag));
   }

   @Override
   public ArtifactToken getArtifactFromAttribute(AttributeTypeToken attrType, String value, BranchId branch) {
      return ArtifactQuery.getArtifactFromAttribute(attrType, value, branch);
   }

   @Override
   public List<ArtifactToken> getArtifactsFromAttributeValues(AttributeTypeToken attributeType, Collection<String> values, int estimatedCount) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromAttributeValues(attributeType, values,
         atsApi.getAtsBranch(), estimatedCount));
   }

   @Override
   public ArtifactToken getArtifactFromTypeAndAttribute(ArtifactTypeToken artifactType, AttributeTypeToken attributeType, String value, BranchId branch) {
      return ArtifactQuery.getArtifactFromTypeAndAttribute(artifactType, attributeType, value, branch);
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(ArtifactTypeToken artType, AttributeTypeToken attrType, String value, BranchToken branch) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromTypeAndAttribute(artType, attrType, value, branch));
   }

}