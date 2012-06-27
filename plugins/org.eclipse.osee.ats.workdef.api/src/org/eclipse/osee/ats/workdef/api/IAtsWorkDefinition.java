/*
 * Created on Jun 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.api;

import java.util.List;
import org.eclipse.osee.framework.core.data.HasDescription;
import org.eclipse.osee.framework.core.data.Identifiable;

public interface IAtsWorkDefinition extends Identifiable, HasDescription {

   /**
    * Identification
    */
   @Override
   public abstract String getName();

   public abstract String getId();

   @Override
   public abstract String getDescription();

   /**
    * States
    */
   public abstract List<IAtsStateDefinition> getStates();

   public abstract IAtsStateDefinition getStateByName(String name);

   public abstract IAtsStateDefinition getStartState();

}