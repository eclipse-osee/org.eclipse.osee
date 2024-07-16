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

package org.eclipse.osee.ats.api.ai;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface IAtsActionableItemService {

   Set<IAtsActionableItem> getActionableItems(IAtsWorkItem workItem);

   String getActionableItemsStr(IAtsWorkItem workItem);

   Collection<ArtifactId> getActionableItemIds(IAtsWorkItem workItem);

   void addActionableItem(IAtsWorkItem workItem, IAtsActionableItem aia, IAtsChangeSet changes);

   void removeActionableItem(IAtsWorkItem workItem, IAtsActionableItem aia, IAtsChangeSet changes);

   Result setActionableItems(IAtsWorkItem workItem, Collection<IAtsActionableItem> newItems, IAtsChangeSet changes);

   boolean hasActionableItems(IAtsWorkItem workItem);

   Collection<ActionableItem> getActionableItems(IAtsTeamDefinition teamDef);

   List<IAtsActionableItem> getActiveActionableItemsAndChildren(IAtsTeamDefinition teamDef);

   /**
    * @return this object casted, else if hard artifact constructed, else valid then load/construct, else return null
    */
   ActionableItem getActionableItemById(ArtifactId aiId);

   IAtsActionableItem getActionableItem(IAtsTeamDefinition teamDef);

   ActionableItem createActionableItem(String name, long id, IAtsChangeSet changes);

   ActionableItem createActionableItem(String name, IAtsChangeSet changes);

   IAtsActionableItem getActionableItem(String value);

   Collection<AtsUser> getSubscribed(IAtsActionableItem ai);

   Collection<AtsUser> getLeads(IAtsActionableItem ai);

   IAtsTeamDefinition getTeamDefinitionInherited(IAtsActionableItem ai);

   ActionableItem createActionableItem(ArtifactToken aiArt);

   Set<IAtsActionableItem> getAIsFromItemAndChildren(IAtsActionableItem ai);

   Set<IAtsActionableItem> getActionableItemsFromItemAndChildren(IAtsActionableItem ai);

   void getActionableItemsFromItemAndChildren(IAtsActionableItem ai, Set<IAtsActionableItem> aias);

   Set<IAtsActionableItem> getActionableItems(Collection<String> actionableItemNames);

   Collection<IAtsActionableItem> getUserEditableActionableItems(Collection<IAtsActionableItem> actionableItems);

   Set<IAtsActionableItem> getChildren(IAtsActionableItem topActionableItem, boolean recurse);

   List<IAtsActionableItem> getActive(Collection<IAtsActionableItem> ais, Active active);

   List<IAtsActionableItem> getTopLevelActionableItems(Active active);

   Collection<IAtsActionableItem> getActionableItemsAll(IAtsQueryService queryService);

   IAtsActionableItem getTopActionableItem(AtsApi atsApi);

   String getNotActionableItemError(IAtsConfigObject configObject);

   Collection<IAtsActionableItem> getActionableItems(Active active, IAtsQueryService queryService);

   Collection<IAtsTeamDefinition> getImpactedTeamDefs(Collection<IAtsActionableItem> ais);

   Collection<IAtsActionableItem> getActionableItems(IAtsAction action);

}