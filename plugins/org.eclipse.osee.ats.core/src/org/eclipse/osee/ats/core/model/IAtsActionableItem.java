/*
 * Created on May 31, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsActionableItem extends IAtsConfigObject {

   /*****************************
    * Name, Full Name, Description
    ******************************/
   void setHumanReadableId(String humanReadableId);

   void setName(String name);

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

   boolean isActive();

   void setActionable(boolean actionable);

   public boolean isActionable() throws OseeCoreException;

   void setActive(boolean active);

   /*****************************************************
    * Team Leads, Members
    ******************************************************/
   Collection<IAtsUser> getLeads() throws OseeCoreException;

   Collection<IAtsUser> getSubscribed() throws OseeCoreException;

}
