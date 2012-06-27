/*
 * Created on Jun 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.api;

import java.util.List;

public interface IAtsWorkDefinition {
   /**
    * NOTE: Extend Identifiable and HasDescription
    */

   /**
    * NOTE: Don't expose the set methods
    */

   /**
    * Identification
    */
   public abstract String getName();

   public abstract void setName(String string);

   public abstract String getId();

   public abstract void setId(String id);

   public abstract void setDescription(String format);

   public abstract String getDescription();

   /**
    * States
    */
   public abstract List<IAtsStateDefinition> getStates();

   public abstract IAtsStateDefinition addState(IAtsStateDefinition state);

   public abstract IAtsStateDefinition getStateByName(String name);

   public abstract IAtsStateDefinition getStartState();

   public abstract void setStartState(IAtsStateDefinition startState);

   /**
    * Rules
    */
   public abstract boolean hasRule(String rule);

   public abstract List<String> getRules();

   public abstract void addRule(String rule);

   public abstract void removeRule(String rule);

}