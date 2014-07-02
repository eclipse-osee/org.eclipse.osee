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
package org.eclipse.osee.ats.core.client.internal.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.client.internal.IAtsWorkItemArtifactService;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsQuery implements IAtsQuery {

   private final Collection<? extends IAtsWorkItem> items;
   private final IAtsWorkItemService workItemService;
   private final IAtsWorkItemArtifactService workItemArtifactProvider;

   public AtsQuery(Collection<? extends IAtsWorkItem> workItems, IAtsWorkItemService workItemService, IAtsWorkItemArtifactService teamDefService) {
      this.items = workItems;
      this.workItemService = workItemService;
      this.workItemArtifactProvider = teamDefService;
   }

   @Override
   public IAtsQuery isOfType(IArtifactType... artifactType) throws OseeCoreException {
      boolean found = false;
      for (IAtsWorkItem item : new CopyOnWriteArrayList<IAtsWorkItem>(items)) {
         for (IArtifactType matchType : artifactType) {
            if (workItemService.isOfType(item, matchType)) {
               found = true;
               break;
            }
         }
         if (!found) {
            items.remove(item);
         }
      }
      return this;
   }

   @Override
   public IAtsQuery union(IAtsQuery... atsQuery) throws OseeCoreException {
      Set<IAtsWorkItem> items = new HashSet<IAtsWorkItem>();
      for (IAtsQuery query : atsQuery) {
         items.addAll(query.getItems());
      }
      return this;
   }

   @Override
   public IAtsQuery fromTeam(IAtsTeamDefinition teamDef) throws OseeCoreException {
      for (IAtsWorkItem workItem : new CopyOnWriteArrayList<IAtsWorkItem>(items)) {
         IAtsTeamDefinition itemTeamDef = workItemArtifactProvider.getTeamDefinition(workItem);
         if (!itemTeamDef.getGuid().equals(teamDef.getGuid())) {
            items.remove(workItem);
         }
      }
      return this;
   }

   @Override
   public IAtsQuery isStateType(StateType... stateType) throws OseeCoreException {
      List<StateType> types = new ArrayList<StateType>();
      for (StateType type : stateType) {
         types.add(type);
      }
      for (IAtsWorkItem workItem : new CopyOnWriteArrayList<IAtsWorkItem>(items)) {
         IAtsStateManager mgr = workItem.getStateMgr();
         if (mgr.getStateType().isCompleted() && !types.contains(StateType.Completed)) {
            items.remove(workItem);
         } else if (mgr.getStateType().isCancelled() && !types.contains(StateType.Cancelled)) {
            items.remove(workItem);
         } else if (mgr.getStateType().isInWork() && !types.contains(StateType.Working)) {
            items.remove(workItem);
         }
      }
      return this;
   }

   @Override
   public Collection<? extends IAtsWorkItem> getItems() {
      return items;
   }

   @Override
   public IAtsQuery withOrValue(IAttributeType attributeType, Collection<? extends Object> matchValues) throws OseeCoreException {
      if (matchValues != null && !matchValues.isEmpty()) {
         for (IAtsWorkItem workItem : new CopyOnWriteArrayList<IAtsWorkItem>(items)) {
            Collection<Object> currAttrValues = workItemService.getAttributeValues(workItem, attributeType);
            boolean found = false;
            for (Object matchValue : matchValues) {
               if (currAttrValues.contains(matchValue)) {
                  found = true;
                  break;
               }
            }
            if (!found) {
               items.remove(workItem);
            }
         }
      }
      return this;
   }

}
