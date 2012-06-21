/*
 * Created on Jun 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.api;

public interface IAtsWidgetDefinitionIntMinMaxConstraint extends IAtsWidgetConstraint {

   public abstract void set(Integer minValue, Integer maxValue);

   public abstract Integer getMinValue();

   public abstract Integer getMaxValue();

}