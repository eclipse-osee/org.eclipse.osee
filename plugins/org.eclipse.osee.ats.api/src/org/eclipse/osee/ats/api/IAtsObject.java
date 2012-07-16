/*
 * Created on Feb 9, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api;

import org.eclipse.osee.framework.core.data.HasDescription;
import org.eclipse.osee.framework.core.data.Identifiable;

/**
 * Base class to build all ats config and action objects on
 * 
 * @author Donald G. Dunne
 */
public interface IAtsObject extends Identifiable, HasDescription {

   @Override
   public String getName();

   @Override
   public String getGuid();

   @Override
   public String getDescription();

   public String getHumanReadableId();

}
