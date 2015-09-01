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
package org.eclipse.osee.ats.api.ai;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.rule.IAtsRules;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsActionableItem extends IAtsConfigObject, IAtsRules {

   /*****************************
    * Name, Full Name, Description
    ******************************/
   void setName(String name) throws OseeCoreException;

   void setDescription(String description);

   /*****************************
    * Parent and Children Team Definitions
    ******************************/
   Collection<IAtsActionableItem> getChildrenActionableItems();

   IAtsActionableItem getParentActionableItem();

   IAtsTeamDefinition getTeamDefinition();

   IAtsTeamDefinition getTeamDefinitionInherited();

   void setParentActionableItem(IAtsActionableItem parentActionableItem);

   void setTeamDefinition(IAtsTeamDefinition teamDef);

   /*****************************
    * Misc
    ******************************/
   Collection<String> getStaticIds();

   void setActionable(boolean actionable);

   public boolean isActionable() throws OseeCoreException;

   void setActive(boolean active);

   /*****************************************************
    * Team Leads, Members
    ******************************************************/
   Collection<IAtsUser> getLeads() throws OseeCoreException;

   Collection<IAtsUser> getSubscribed() throws OseeCoreException;

   boolean isAllowUserActionCreation();

   void setAllowUserActionCreation(boolean allowUserActionCreation);

}
