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
public class XFloatAttribute extends XTextAttribute {
   private double minValue = 0;
   private boolean minValueSet = false;
   private double maxValue = 0;
   private boolean maxValueSet = false;
   private boolean greaterThanZero = false;

   public XFloatAttribute(String displayLabel) {
      super(displayLabel);
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
      if (super.requiredEntry() || (super.get().equals(""))) {
         if (!super.isValid())
            return new Result("Invalid");
         else if (!this.isFloat())
            return new Result("Must be a Float");
         else if (this.getFloat() <= 0 && greaterThanZero)
            return new Result("Must be > 0");
         else if (minValueSet && (this.getFloat() < minValue))
            return new Result("Must be >= " + minValue);
         else if (maxValueSet && (this.getFloat() > maxValue)) return new Result("Must be <= " + maxValue);
      }
      return Result.TrueResult;
   }

   public boolean isGreaterThanZero() {
      return greaterThanZero;
   }

   public void setGreaterThanZero(boolean greaterThanZero) {
      this.greaterThanZero = greaterThanZero;
   }
}
