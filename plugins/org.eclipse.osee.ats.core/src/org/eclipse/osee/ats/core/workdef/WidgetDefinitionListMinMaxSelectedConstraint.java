/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

/**
 * @author Donald G. Dunne
 */
public class WidgetDefinitionListMinMaxSelectedConstraint implements WidgetConstraint {
   private Integer minSelected = null;
   private Integer maxSelected = null;

   public WidgetDefinitionListMinMaxSelectedConstraint(Integer minSelected, Integer maxSelected) {
      set(minSelected, maxSelected);
   }

   public WidgetDefinitionListMinMaxSelectedConstraint(String minSelected, String maxSelected) {
      if (minSelected == null) {
         this.minSelected = null;
      } else {
         this.minSelected = new Integer(minSelected);
      }
      if (maxSelected == null) {
         this.maxSelected = null;
      } else {
         this.maxSelected = new Integer(maxSelected);
      }
   }

   public void set(Integer minSelected, Integer maxSelected) {
      this.minSelected = minSelected;
      this.maxSelected = maxSelected;
   }

   public Integer getMinSelected() {
      return minSelected;
   }

   public Integer getMaxSelected() {
      return maxSelected;
   }

}
