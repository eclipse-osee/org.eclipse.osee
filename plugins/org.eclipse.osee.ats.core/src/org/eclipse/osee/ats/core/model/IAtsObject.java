/*
 * Created on Feb 9, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model;

/**
 * Base class to build all ats config and action objects on
 *
 * @author Donald G. Dunne
 */
public interface IAtsObject {

   public String getName();

   public String getGuid();

   public String getDescription();

   public String getHumanReadableId();

   public IAtsChildren getAtsChildren();

   public Integer getIdInt();
}
