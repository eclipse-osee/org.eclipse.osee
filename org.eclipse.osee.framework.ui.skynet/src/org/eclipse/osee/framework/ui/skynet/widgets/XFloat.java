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

import java.text.NumberFormat;
import java.text.ParseException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

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

   @Override
   public double getFloat() {
      if (get().equals("")) {
         return 0.0;
      }
      try {
         return NumberFormat.getInstance().parse(get()).doubleValue();
      } catch (ParseException e) {
         return 0.0;
      }
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() || Strings.isValid(get())) {
         String name = getLabel();
         if (name.equals("")) {
            name = "Value";
         }
         IStatus result = super.isValid();
         if (!result.isOK()) {
            return result;
         } else if (!this.isFloat()) {
            return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, name + " must be a Float");
         } else if (minValueSet && this.getFloat() < minValue) {
            return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, name + " must be >= " + minValue);
         } else if (maxValueSet && this.getFloat() > maxValue) {
            return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, name + " must be <= " + maxValue);
         }
      }
      return Status.OK_STATUS;
   }
}
