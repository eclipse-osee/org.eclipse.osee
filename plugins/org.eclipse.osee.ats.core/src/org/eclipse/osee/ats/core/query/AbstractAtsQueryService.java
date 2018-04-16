/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsQueryService implements IAtsQueryService {

   protected final JdbcService jdbcService;
   private final AtsApi atsApi;
   /**
    * Quick Search for single attribute search takes 22 seconds, just use straight database call instead. Replace this
    * when searching is improved.
    */
   private static String ATTR_QUERY =
      "SELECT art.art_id FROM osee_artifact art, osee_txs txs, OSEE_ATTRIBUTE attr WHERE art.gamma_id = txs.gamma_id " //
         + "AND txs.tx_current = 1 AND txs.branch_id = ? and attr.ART_ID = art.ART_ID and " //
         + "attr.ATTR_TYPE_ID = ? and attr.VALUE = ?";

   public AbstractAtsQueryService(JdbcService jdbcService, AtsApi atsApi) {
      this.jdbcService = jdbcService;
      this.atsApi = atsApi;
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItemsFromQuery(String query, Object... data) {
      List<ArtifactId> ids = new LinkedList<>();
      jdbcService.getClient().runQuery(stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))), query, data);
      List<IAtsWorkItem> workItems = new LinkedList<>();
      for (ArtifactToken art : atsApi.getQueryService().getArtifacts(ids, atsApi.getAtsBranch())) {
         if (atsApi.getStoreService().isOfType(art, AtsArtifactTypes.AbstractWorkflowArtifact)) {
            IAtsWorkItem workItem = atsApi.getWorkItemFactory().getWorkItem(art);
            if (workItem != null) {
               workItems.add(workItem);
            }
         }
      }
      return workItems;
   }

   @Override
   public Collection<ArtifactToken> getArtifactsFromQuery(String query, Object... data) {
      List<ArtifactId> ids = new LinkedList<>();
      jdbcService.getClient().runQuery(stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))), query, data);
      return atsApi.getQueryService().getArtifacts(ids, atsApi.getAtsBranch());
   }

   @Override
   public List<ArtifactId> getArtifactIdsFromQuery(String query, Object... data) {
      List<ArtifactId> ids = new LinkedList<>();
      jdbcService.getClient().runQuery(stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))), query, data);
      return ids;
   }

   @Override
   public List<ArtifactToken> getArtifactTokensFromQuery(String query, Object... data) {
      List<ArtifactToken> ids = new LinkedList<>();
      jdbcService.getClient().runQuery(stmt -> ids.add(ArtifactToken.valueOf(stmt.getLong("art_id"),
         stmt.getString("name"), BranchId.valueOf(stmt.getLong("branch_id")))), query, data);
      return ids;
   }

   @Override
   public void runUpdate(String query, Object... data) {
      jdbcService.getClient().runPreparedUpdate(query, data);
   }

   @Override
   public List<IAtsWorkItem> getWorkItemsByIds(String ids) {
      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (ArtifactToken art : getArtifactsByIds(ids)) {
         IAtsWorkItem workItem = atsApi.getWorkItemFactory().getWorkItem(art);
         if (workItem != null) {
            workItems.add(workItem);
         }
      }
      return workItems;
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
   public IAtsWorkItem getWorkItem(String id) {
      IAtsWorkItem workItem = null;
      ArtifactToken workItemArt = getArtifactById(id);
      if (workItemArt != null) {
         workItem = atsApi.getWorkItemFactory().getWorkItem(workItemArt);
      }
      return workItem;
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
   public ArtifactToken getArtifactByAtsId(String id) {
      ArtifactToken artifact = null;
      try {
         Collection<ArtifactToken> workItems =
            getArtifactsFromQuery(ATTR_QUERY, atsApi.getAtsBranch(), AtsAttributeTypes.AtsId.getIdString(), id);
         if (!workItems.isEmpty()) {
            artifact = workItems.iterator().next();
         }
      } catch (ItemDoesNotExist ex) {
         // do nothing
      }
      return artifact;
   }

   @Override
   public ArtifactToken getArtifactByLegacyPcrId(String id) {
      try {
         Collection<ArtifactToken> workItems =
            getArtifactsFromQuery(ATTR_QUERY, atsApi.getAtsBranch(), AtsAttributeTypes.LegacyPcrId.getIdString(), id);
         if (workItems.size() == 1) {
            return workItems.iterator().next();
         } else if (workItems.size() > 1) {
            throw new OseeStateException("More than 1 artifact exists with legacy id [%s]", id);
         }
      } catch (ItemDoesNotExist ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public Collection<ArtifactToken> getArtifactsByLegacyPcrId(String id) {
      return getArtifactsFromQuery(ATTR_QUERY, atsApi.getAtsBranch(), AtsAttributeTypes.LegacyPcrId.getIdString(), id);
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItemsByLegacyPcrId(String id) {
      List<IAtsWorkItem> workItems = new LinkedList<>();
      for (ArtifactToken art : getArtifactsFromQuery(ATTR_QUERY, atsApi.getAtsBranch(),
         AtsAttributeTypes.LegacyPcrId.getIdString(), id)) {
         IAtsWorkItem workItem = atsApi.getWorkItemFactory().getWorkItem(art);
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

   @Override
   public <A extends IAtsConfigObject> A getSoleById(long id, Class<A> clazz) {
      return getCache().getAtsObject(id);
   }

   private IAtsCache getCache() {
      return atsApi.getCache();
   }

   @Override
   public <T> T getConfigItem(ArtifactId artifact) {
      return getConfigItem(artifact.getId());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(String id) {
      T atsObject = null;
      if (Strings.isNumeric(id)) {
         atsObject = getCache().getAtsObject(Long.valueOf(id));
         if (atsObject == null) {
            ArtifactId artifact = getArtifact(Long.valueOf(id));
            if (artifact != null && artifact instanceof IAtsConfigObject) {
               atsObject = (T) atsApi.getConfigItemFactory().getConfigObject(artifact);
            }
         }
      }
      return atsObject;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(Long id) {
      T atsObject = getCache().getAtsObject(id);
      if (atsObject == null) {
         ArtifactId artifact = getArtifact(id);
         if (artifact != null && artifact instanceof IAtsConfigObject) {
            atsObject = (T) atsApi.getConfigItemFactory().getConfigObject(artifact);
         }
      }
      return atsObject;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactId artifact) {
      ArtifactId art = getArtifact(artifact);
      if (art != null) {
         return atsApi.getWorkItemFactory().getTeamWf(art);
      }
      return null;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(Long id) {
      ArtifactId art = getArtifact(id);
      if (art != null) {
         return atsApi.getWorkItemFactory().getTeamWf(art);
      }
      return null;
   }

   @Override
   public List<ArtifactToken> getArtifacts(IArtifactType artifactType) {
      return Collections.castAll(atsApi.getQueryService().getArtifacts(atsApi.getAtsBranch(), true, artifactType));
   }

   @Override
   public ArtifactToken getConfigArtifact(IAtsConfigObject atsConfigObject) {
      if (atsConfigObject.getStoreObject() != null) {
         return atsConfigObject.getStoreObject();
      }
      return getArtifact(atsConfigObject.getId());
   }

}