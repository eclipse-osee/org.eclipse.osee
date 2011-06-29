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
