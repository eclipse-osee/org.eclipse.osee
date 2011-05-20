/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

/**
 * @author Donald G. Dunne
 */
public class WidgetDefinitionIntMinMaxConstraint implements WidgetConstraint {
   private Integer minValue = null;
   private Integer maxValue = null;

   public WidgetDefinitionIntMinMaxConstraint(Integer minValue, Integer maxValue) {
      set(minValue, maxValue);
   }

   public WidgetDefinitionIntMinMaxConstraint(String minValue, String maxValue) {
      if (minValue == null) {
         this.minValue = null;
      } else {
         this.minValue = new Integer(minValue);
      }
      if (maxValue == null) {
         this.maxValue = null;
      } else {
         this.maxValue = new Integer(maxValue);
      }
   }

   public void set(Integer minValue, Integer maxValue) {
      this.minValue = minValue;
      this.maxValue = maxValue;
   }

   public Integer getMinValue() {
      return minValue;
   }

   public Integer getMaxValue() {
      return maxValue;
   }

}
