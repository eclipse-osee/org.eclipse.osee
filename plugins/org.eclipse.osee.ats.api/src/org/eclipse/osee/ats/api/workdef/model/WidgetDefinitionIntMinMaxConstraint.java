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
package org.eclipse.osee.ats.api.workdef.model;

import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinitionIntMinMaxConstraint;

/**
 * @author Donald G. Dunne
 */
public class WidgetDefinitionIntMinMaxConstraint implements IAtsWidgetDefinitionIntMinMaxConstraint {
   private Integer minValue = null;
   private Integer maxValue = null;

   public WidgetDefinitionIntMinMaxConstraint(Integer minValue, Integer maxValue) {
      set(minValue, maxValue);
   }

   public WidgetDefinitionIntMinMaxConstraint(String minValue, String maxValue) {
      if (minValue != null) {
         this.minValue = new Integer(minValue);
      }
      if (maxValue != null) {
         this.maxValue = new Integer(maxValue);
      }
   }

   @Override
   public void set(Integer minValue, Integer maxValue) {
      this.minValue = minValue;
      this.maxValue = maxValue;
   }

   @Override
   public Integer getMinValue() {
      return minValue;
   }

   @Override
   public Integer getMaxValue() {
      return maxValue;
   }

}
