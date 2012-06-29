/*
 * Created on Jun 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

public interface IAtsWidgetDefinitionListMinMaxSelectedConstraint extends IAtsWidgetConstraint {

   public abstract void set(Integer minSelected, Integer maxSelected);

   public abstract Integer getMinSelected();

   public abstract Integer getMaxSelected();

}