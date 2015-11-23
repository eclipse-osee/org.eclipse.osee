/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.search;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.search.WorldUISearchItem;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class WorldSearchItem extends WorldUISearchItem {

   AtsSearchData data;

   public WorldSearchItem(AtsSearchData data) {
      super(data.getSearchName());
      this.data = data.copy();
   }

   public WorldSearchItem(String searchName) {
      super(searchName);
      data = new AtsSearchData(searchName);
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return super.getSelectedName(searchType);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      List<WorkItemType> workItemTypes = data.getWorkItemTypes();
      if (workItemTypes.isEmpty()) {
         workItemTypes.add(WorkItemType.WorkItem);
      }
      IAtsQuery query = AtsClientService.get().getQueryService().createQuery(data.getWorkItemTypes().iterator().next(),
         workItemTypes.toArray(new WorkItemType[workItemTypes.size()]));
      if (Strings.isValid(data.getTitle())) {
         query.andAttr(AtsAttributeTypes.Title, data.getTitle(), QueryOption.CONTAINS_MATCH_OPTIONS);
      }
      if (!data.getStateTypes().isEmpty()) {
         query.andStateType(data.getStateTypes().toArray(new StateType[data.getStateTypes().size()]));
      }
      if (Strings.isValid(data.getUserId())) {
         AtsSearchUserType userType = data.getUserType();
         IAtsUser userById = AtsClientService.get().getUserService().getUserById(data.getUserId());
         if (userType == AtsSearchUserType.Originated) {
            query.andOriginator(userById);
         } else if (userType == AtsSearchUserType.Subscribed) {
            query.andSubscribed(userById);
         } else if (userType == AtsSearchUserType.Favorites) {
            query.andFavorite(userById);
         } else {
            query.andAssignee(userById);
         }
      }
      if (!data.getTeamDefUuids().isEmpty()) {
         query.andTeam(data.getTeamDefUuids());
      }
      if (!data.getAiUuids().isEmpty()) {
         query.andActionableItem(data.getAiUuids());
      }
      if (data.getVersionUuid() != null && data.getVersionUuid() > 0L) {
         query.andVersion(data.getVersionUuid());
      }
      if (Strings.isValid(data.getState())) {
         query.andState(data.getState());
      }
      if (data.getProgramUuid() > 0L) {
         query.andProgram(data.getProgramUuid());
      }
      if (data.getInsertionUuid() > 0L) {
         query.andInsertion(data.getInsertionUuid());
      }
      if (data.getInsertionActivityUuid() > 0L) {
         query.andInsertionActivity(data.getInsertionActivityUuid());
      }
      if (data.getWorkPackageUuid() > 0L) {
         query.andWorkPackage(data.getWorkPackageUuid());
      }
      if (Strings.isValid(data.getColorTeam())) {
         query.andColorTeam(data.getColorTeam());
      }
      performSearch(query);
      return Collections.castAll(query.getResultArtifacts().getList());
   }

   /**
    * Implement to populate query with extended options
    */
   protected void performSearch(IAtsQuery query) {
      // do nothing
   }

   @Override
   public WorldUISearchItem copy() {
      return new WorldSearchItem(data);
   }

   public AtsSearchData getData() {
      return data;
   }

}
