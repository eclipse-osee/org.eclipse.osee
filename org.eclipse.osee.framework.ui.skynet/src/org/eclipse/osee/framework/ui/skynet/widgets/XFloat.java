/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class XFloat extends XText {
   private double minValue = 0;
   private boolean minValueSet = false;
   private double maxValue = 0;
   private boolean maxValueSet = false;

   public XFloat(String displayLabel) {
      super(displayLabel, "float");
   }

   public XFloat(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
   }

   public void setMinValue(double minValue) {
      minValueSet = true;
      this.minValue = minValue;
   }

   public void setMaxValue(double maxValue) {
      maxValueSet = false;
      this.maxValue = maxValue;
   }

   public double getFloat() {
      if (get().equals(""))
         return 0.0;
      else
         return new Double(get());
   }

   public boolean isValid() {
      return isValidResult().isTrue();
   }

   public Result isValidResult() {
      if (super.requiredEntry() || (super.get().compareTo("") != 0)) {
         String name = getLabel();
         if (name.equals("")) name = "Value";
         if (!super.isValid()) {
            return new Result(name + " is invalid");
         } else if (!this.isFloat()) {
            return new Result(name + " must be a Float");
         } else if (minValueSet && (this.getFloat() < minValue)) {
            return new Result(name + " must be >= " + minValue);
         } else if (maxValueSet && (this.getFloat() > maxValue)) {
            return new Result(name + " must be <= " + maxValue);
         }
      }
      return Result.TrueResult;
   }
}
