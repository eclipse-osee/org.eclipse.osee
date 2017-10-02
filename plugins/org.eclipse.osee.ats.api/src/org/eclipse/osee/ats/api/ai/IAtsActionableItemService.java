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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsActionableItemService {

   Set<IAtsActionableItem> getActionableItems(IAtsObject atsObject) ;

   String getActionableItemsStr(IAtsObject atsObject) ;

   Collection<ArtifactId> getActionableItemIds(IAtsObject atsObject);

   void addActionableItem(IAtsObject atsObject, IAtsActionableItem aia, IAtsChangeSet changes) ;

   void removeActionableItem(IAtsObject atsObject, IAtsActionableItem aia, IAtsChangeSet changes) ;

   Result setActionableItems(IAtsObject atsObject, Collection<IAtsActionableItem> newItems, IAtsChangeSet changes) ;

   boolean hasActionableItems(IAtsObject atsObject);

   Collection<IAtsTeamDefinition> getCorrespondingTeamDefinitions(IAtsObject atsObject);

   List<IAtsActionableItem> getActiveActionableItemsAndChildren(IAtsTeamDefinition teamDef);

}