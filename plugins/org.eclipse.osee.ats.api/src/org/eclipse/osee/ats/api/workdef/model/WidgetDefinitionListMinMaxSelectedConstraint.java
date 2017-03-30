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

import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinitionListMinMaxSelectedConstraint;

/**
 * @author Donald G. Dunne
 */
public class WidgetDefinitionListMinMaxSelectedConstraint implements IAtsWidgetDefinitionListMinMaxSelectedConstraint {
   private Integer minSelected = null;
   private Integer maxSelected = null;

   public WidgetDefinitionListMinMaxSelectedConstraint(Integer minSelected, Integer maxSelected) {
      set(minSelected, maxSelected);
   }

   public WidgetDefinitionListMinMaxSelectedConstraint(String minSelected, String maxSelected) {
      if (minSelected != null) {
         this.minSelected = new Integer(minSelected);
      }
      if (maxSelected != null) {
         this.maxSelected = new Integer(maxSelected);
      }
   }

   @Override
   public void set(Integer minSelected, Integer maxSelected) {
      this.minSelected = minSelected;
      this.maxSelected = maxSelected;
   }

   @Override
   public Integer getMinSelected() {
      return minSelected;
   }

   @Override
   public Integer getMaxSelected() {
      return maxSelected;
   }

}
