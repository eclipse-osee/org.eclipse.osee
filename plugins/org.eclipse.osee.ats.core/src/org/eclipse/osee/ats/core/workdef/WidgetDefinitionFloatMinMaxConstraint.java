/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;


/**
 * @author Donald G. Dunne
 */
public class WidgetDefinitionFloatMinMaxConstraint implements WidgetConstraint {
   private Double minValue = null;
   private Double maxValue = null;

   public WidgetDefinitionFloatMinMaxConstraint(Double minValue, Double maxValue) {
      set(minValue, maxValue);
   }

   public WidgetDefinitionFloatMinMaxConstraint(String minValue, String maxValue) {
      if (minValue == null) {
         this.minValue = null;
      } else {
         this.minValue = new Double(minValue);
      }
      if (maxValue == null) {
         this.maxValue = null;
      } else {
         this.maxValue = new Double(maxValue);
      }
   }

   public void set(Double minValue, Double maxValue) {
      this.minValue = minValue;
      this.maxValue = maxValue;
   }

   public Double getMinValue() {
      return minValue;
   }

   public Double getMaxValue() {
      return maxValue;
   }

}
