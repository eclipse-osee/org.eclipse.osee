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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsQueryService implements IAtsQueryService {

   protected final JdbcService jdbcService;
   private final IAtsServices services;

   public AbstractAtsQueryService(JdbcService jdbcService, IAtsServices services) {
      this.jdbcService = jdbcService;
      this.services = services;
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItemsFromQuery(String query, Object... data) {
      List<ArtifactId> ids = new LinkedList<>();
      jdbcService.getClient().runQuery(stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))), query, data);
      List<IAtsWorkItem> workItems = new LinkedList<>();
      for (ArtifactToken art : services.getQueryService().getArtifacts(ids, services.getAtsBranch())) {
         if (services.getStoreService().isOfType(art, AtsArtifactTypes.AbstractWorkflowArtifact)) {
            IAtsWorkItem workItem = services.getWorkItemFactory().getWorkItem(art);
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
      return services.getQueryService().getArtifacts(ids, services.getAtsBranch());
   }

   @Override
   public void runUpdate(String query, Object... data) {
      jdbcService.getClient().runPreparedUpdate(query, data);
   }

   @Override
   public List<IAtsWorkItem> getWorkItemListByIds(String ids) {
      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (ArtifactToken art : getArtifactListByIdsStr(ids)) {
         IAtsWorkItem workItem = services.getWorkItemFactory().getWorkItem(art);
         if (workItem != null) {
            workItems.add(workItem);
         }
      }
      return workItems;
   }

   /**
    * @param idList id,id,id
    */
   @Override
   public List<ArtifactToken> getArtifactListByIdsStr(String idList) {
      List<ArtifactToken> actions = new ArrayList<>();
      for (String id : idList.split(",")) {
         id = id.replaceAll("^ +", "");
         id = id.replaceAll(" +$", "");
         ArtifactToken action = getArtifactById(id);
         if (action != null) {
            actions.add(action);
         }
      }
      return actions;
   }

   @Override
   public ArtifactToken getArtifactById(String id) {
      ArtifactToken action = null;
      if (GUID.isValid(id)) {
         action = getArtifactByGuid(id);
      }
      Long uuid = null;
      try {
         uuid = Long.parseLong(id);
      } catch (NumberFormatException ex) {
         // do nothing
      }
      if (uuid != null) {
         action = getArtifact(uuid);
      }
      if (action == null) {
         action = getArtifactByAtsId(id);
      }
      return action;
   }

   private ArtifactToken getArtifactByAtsId(String id) {
      return services.getArtifactByAtsId(id);
   }

   private ArtifactToken getArtifact(Long uuid) {
      return services.getArtifact(uuid);
   }

   public ArtifactToken getArtifactByGuid(String guid) {
      return services.getArtifactByGuid(guid);
   }

   @Override
   public ArtifactToken getArtifact(IAtsObject atsObject) {
      ArtifactToken result = null;
      if (atsObject.getStoreObject() != null) {
         result = atsObject.getStoreObject();
      } else {
         result = services.getArtifact(atsObject.getId());
      }
      return result;
   }
}