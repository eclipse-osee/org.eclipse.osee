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
package org.eclipse.osee.ats.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemFilter implements IAtsWorkItemFilter {

   private Collection<? extends IAtsWorkItem> items;
   private AtsApi atsApi;

   public AtsWorkItemFilter(AtsApi atsApi) {
      this(null, atsApi);
      this.atsApi = atsApi;
   }

   public AtsWorkItemFilter(Collection<? extends IAtsWorkItem> workItems, AtsApi atsApi) {
      this.items = new ArrayList<>(workItems);
      this.atsApi = atsApi;
   }

   @Override
   public IAtsWorkItemFilter isOfType(ArtifactTypeToken... artifactType) {
      boolean found = false;
      for (IAtsWorkItem item : new CopyOnWriteArrayList<IAtsWorkItem>(items)) {
         for (ArtifactTypeToken matchType : artifactType) {
            if (atsApi.getArtifactResolver().isOfType(item, matchType)) {
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
   public IAtsWorkItemFilter union(IAtsWorkItemFilter... workItemFilter) {
      Set<IAtsWorkItem> items = new HashSet<>();
      items.addAll(this.items);
      for (IAtsWorkItemFilter filter : workItemFilter) {
         items.addAll(filter.getItems());
      }
      this.items = items;
      return this;
   }

   @Override
   public IAtsWorkItemFilter fromTeam(IAtsTeamDefinition teamDef) {
      for (IAtsWorkItem workItem : new CopyOnWriteArrayList<IAtsWorkItem>(items)) {
         IAtsTeamDefinition itemTeamDef = workItem.getParentTeamWorkflow().getTeamDefinition();
         if (itemTeamDef.notEqual(teamDef)) {
            items.remove(workItem);
         }
      }
      return this;
   }

   @Override
   public IAtsWorkItemFilter isStateType(StateType... stateType) {
      List<StateType> types = new ArrayList<>();
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

   @SuppressWarnings("unchecked")
   @Override
   public <T extends IAtsWorkItem> Collection<T> getItems() {
      Set<T> workItems = new HashSet<>();
      Iterator<? extends IAtsWorkItem> iterator = items.iterator();
      while (iterator.hasNext()) {
         workItems.add((T) iterator.next());
      }
      return workItems;
   }

   @Override
   public IAtsWorkItemFilter withOrValue(AttributeTypeId attributeType, Collection<? extends Object> matchValues) {
      if (matchValues != null && !matchValues.isEmpty()) {
         for (IAtsWorkItem workItem : new CopyOnWriteArrayList<IAtsWorkItem>(items)) {
            Collection<Object> currAttrValues =
               atsApi.getAttributeResolver().getAttributeValues(workItem, attributeType);
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

   @Override
   public Collection<IAtsAction> getActions() {
      Set<IAtsAction> actions = new HashSet<>();
      for (IAtsWorkItem workItem : getItems()) {
         actions.add(workItem.getParentAction());
      }
      return actions;
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTeamWorkflows() {
      Set<IAtsTeamWorkflow> teamWfs = new HashSet<>();
      for (IAtsWorkItem workItem : getItems()) {
         teamWfs.add(workItem.getParentTeamWorkflow());
      }
      return teamWfs;
   }

}
