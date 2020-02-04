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
package org.eclipse.osee.ats.api.ai;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
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

   Collection<IAtsActionableItem> getActionableItems(IAtsTeamDefinition teamDef);

   List<IAtsActionableItem> getActiveActionableItemsAndChildren(IAtsTeamDefinition teamDef);

   /**
    * @return this object casted, else if hard artifact constructed, else load and construct
    */
   IAtsActionableItem getActionableItemById(ArtifactId aiId);

   IAtsActionableItem getActionableItem(IAtsTeamDefinition teamDef);

   ActionableItem createActionableItem(String name, long id, IAtsChangeSet changes, AtsApi atsApi);

   ActionableItem createActionableItem(String name, IAtsChangeSet changes, AtsApi atsApi);

   IAtsActionableItem getActionableItem(String value);

   Collection<WorkType> getWorkTypes(IAtsWorkItem workItem);

   boolean isWorkType(IAtsWorkItem workItem, WorkType workType);

   Collection<IAtsUser> getSubscribed(IAtsActionableItem ai);

   Collection<IAtsUser> getLeads(IAtsActionableItem ai);

   IAtsTeamDefinition getTeamDefinitionInherited(IAtsActionableItem ai);

   ActionableItem createActionableItem(ArtifactToken aiArt);

}