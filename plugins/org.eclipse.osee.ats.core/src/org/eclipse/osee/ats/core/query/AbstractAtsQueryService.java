/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.core.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchDataResults;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.query.ISearchCriteriaProvider;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsQueryService implements IAtsQueryService {

   protected final JdbcClient jdbcClient;
   private final AtsApi atsApi;

   public AbstractAtsQueryService(JdbcService jdbcService, AtsApi atsApi) {
      jdbcClient = jdbcService.getClient();
      this.atsApi = atsApi;
   }

   @Override
   public List<ArtifactToken> getArtifactsByIds(String ids) {
      List<ArtifactToken> actions = new ArrayList<>();
      for (String id : getIdsFromStr(ids)) {
         ArtifactToken action = getArtifactById(id);
         if (action != null) {
            actions.add(action);
         }
      }
      return actions;
   }

   @Override
   public ArtifactToken getArtifactById(String id) {
      ArtifactToken result = null;
      if (Strings.isNumeric(id)) {
         result = getArtifact(Long.valueOf(id));
      }
      if (result == null) {
         result = getArtifactByAtsId(id);
      }
      return result;
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItemsByLegacyPcrId(String id) {
      List<IAtsWorkItem> workItems = new LinkedList<>();
      for (ArtifactToken art : atsApi.getQueryService().fromAtsBranch().and(AtsAttributeTypes.LegacyPcrId,
         Arrays.asList(id)).asArtifacts()) {
         IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(art);
         if (workItem != null) {
            workItems.add(workItem);
         }
      }
      return workItems;
   }

   @Override
   public List<String> getIdsFromStr(String ids) {
      List<String> idStrs = new LinkedList<>();
      for (String id : ids.split(",")) {
         id = id.replaceAll("^ +", "");
         id = id.replaceAll(" +$", "");
         idStrs.add(id);
      }
      return idStrs;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(ArtifactId artId) {
      Conditions.assertTrue(artId.getId() > 0, "Art Id must be > 0, not %s", artId);
      T atsObject = null;
      ArtifactToken artifact = getArtifact(artId);
      if (artifact != null) {
         atsObject = (T) AtsObjects.getConfigObject(artifact, atsApi);
      }
      return atsObject;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(Long id) {
      T atsObject = null;
      ArtifactToken artifact = getArtifact(id);
      if (artifact != null && artifact.isOfType(AtsArtifactTypes.AtsConfigArtifact)) {
         atsObject = (T) AtsObjects.getConfigObject(artifact, atsApi);
      }
      return atsObject;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactId artifact) {
      return atsApi.getWorkItemService().getTeamWf(artifact);
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(Long id) {
      return atsApi.getWorkItemService().getTeamWf(ArtifactId.valueOf(id));
   }

   @Override
   public List<ArtifactToken> getArtifacts(ArtifactTypeToken artifactType) {
      return Collections.castAll(getArtifacts(atsApi.getAtsBranch(), true, artifactType));
   }

   @Override
   public ArtifactToken getConfigArtifact(IAtsConfigObject atsConfigObject) {
      if (atsConfigObject.getStoreObject() != null) {
         return atsConfigObject.getStoreObject();
      }
      return getArtifact(atsConfigObject.getId());
   }

   @Override
   public ArtifactToken getArtifactByIdOrAtsId(String id) {
      ArtifactToken art = null;
      try {
         if (Strings.isValid(id)) {
            if (Strings.isNumeric(id)) {
               art = getArtifact(Long.valueOf(id));
            } else {
               art = getArtifactByAtsId(id);
            }
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return art;
   }

   @Override
   public Collection<ArtifactToken> getArtifactsByIdsOrAtsIds(String searchStr) {
      List<ArtifactToken> artifacts = new LinkedList<>();
      for (String str : searchStr.split(",")) {
         str = str.replaceAll("^ ", "");
         str = str.replaceAll("$ ", "");
         try {
            if (Strings.isValid(str)) {
               ArtifactToken art = null;
               if (Strings.isNumeric(str)) {
                  art = getArtifact(Long.valueOf(str));
               } else {
                  art = getArtifactByAtsId(str);
               }
               if (art != null) {
                  artifacts.add(art);
               }
            }
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
      }
      return artifacts;
   }

   @Override
   public ArtifactToken getOrCreateArtifact(ArtifactToken parent, ArtifactToken artifactTok, IAtsChangeSet changes) {
      ArtifactToken artifact = getArtifact(artifactTok);
      if (artifact == null || artifact.isInvalid()) {
         artifact = changes.createArtifact(artifactTok);
      }
      if (atsApi.getRelationResolver().getParent(artifact) == null) {
         changes.addChild(parent, artifact);
      }
      return artifact;
   }

   @Override
   public AtsSearchDataResults getArtifacts(AtsSearchData atsSearchData, ISearchCriteriaProvider provider) {
      AtsSearchDataSearch query = new AtsSearchDataSearch(atsSearchData, atsApi, provider);
      return query.performSearch();
   }

   @Override
   public AtsSearchDataResults getArtifactsNew(AtsSearchData atsSearchData, ISearchCriteriaProvider provider) {
      AtsSearchDataSearch query = new AtsSearchDataSearch(atsSearchData, atsApi, provider);
      return query.performSearchNew();
   }

   @Override
   public Collection<ArtifactToken> getArtifactsFromObjects(Collection<? extends IAtsObject> atsObjects) {
      List<ArtifactToken> arts = new ArrayList<>();
      for (IAtsObject obj : atsObjects) {
         arts.add(getArtifact(obj));
      }
      return arts;
   }

   @Override
   public ArtifactToken getArtifactByName(ArtifactTypeToken artType, String name, BranchToken branch) {
      return getArtifactFromTypeAndAttribute(artType, CoreAttributeTypes.Name, name, branch);
   }

   @Override
   public List<ArtifactToken> getArtifactsFromIds(Collection<String> atsIds) {
      List<ArtifactToken> toReturn = new LinkedList<>();
      if (!atsIds.isEmpty()) {
         List<ArtifactToken> fromIds =
            getArtifactsFromAttributeValues(AtsAttributeTypes.AtsId, atsIds, CoreBranches.COMMON, atsIds.size());
         toReturn.addAll(fromIds);
      }
      return toReturn;
   }

   @Override
   public Collection<ArtifactToken> getAssigned(AtsUser user) {
      return atsApi.getQueryService().getArtifacts(AtsAttributeTypes.CurrentStateAssignee,
         user.getArtifactId().getIdString(), atsApi.getAtsBranch());
   }

   @Override
   public Collection<? extends ArtifactToken> getArtifactsFromName(String name, BranchToken branch,
      DeletionFlag excludeDeleted, QueryOption[] containsMatchOptions) {
      return getArtifacts(CoreAttributeTypes.Name, name, branch);
   }

   @Override
   public ArtifactToken getArtifactFromName(ArtifactTypeToken artType, String name, BranchToken branch) {
      Collection<ArtifactToken> arts =
         getArtifactsFromTypeAndName(artType, name, branch, QueryOption.CONTAINS_MATCH_OPTIONS);
      if (!arts.isEmpty()) {
         return arts.iterator().next();
      }
      return null;
   }

   @Override
   public Collection<ArtifactToken> getFavorites(UserToken user) {
      return atsApi.getRelationResolver().getRelated(user, AtsRelationTypes.FavoriteUser_Artifact);
   }

   @Override
   public Collection<ArtifactToken> getSubscribed(UserToken user) {
      return atsApi.getRelationResolver().getRelated(user, AtsRelationTypes.SubscribedUser_Artifact);
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItems(ArtifactTypeToken artType) {
      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (ArtifactToken art : atsApi.getQueryService().getArtifacts(artType)) {
         workItems.add(atsApi.getWorkItemService().getWorkItem(art));
      }
      return workItems;
   }

}