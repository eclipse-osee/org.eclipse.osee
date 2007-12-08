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

public class XIntegerAttribute extends XTextAttribute {

   private int minValue = 0;
   private boolean minValueSet = false;
   private int maxValue = 0;
   private boolean maxValueSet = false;

   public XIntegerAttribute(String displayLabel) {
      super(displayLabel);
   }

   public void setMinValue(int minValue) {
      minValueSet = true;
      this.minValue = minValue;
   }

   public void setMaxValue(int maxValue) {
      maxValueSet = false;
      this.maxValue = maxValue;
   }

   public boolean isValid() {
      return isValidResult().isTrue();
   }

   public Result isValidResult() {
      if (super.requiredEntry() || (super.get().compareTo("") != 0)) {
         if (!super.isValid()) {
            return new Result("Invalid");
         } else if (!this.isInteger()) {
            return new Result("Must be an Integer");
         } else if (minValueSet && (this.getInteger() < minValue)) {
            return new Result("Must be >= " + minValue);
         } else if (maxValueSet && (this.getInteger() > maxValue)) {
            return new Result("Must be <= " + maxValue);
         }
      }
      return Result.TrueResult;
   }

}
