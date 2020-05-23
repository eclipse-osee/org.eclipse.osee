/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class XInteger extends XText {
   private int minValue = 0;
   private boolean minValueSet = false;
   private int maxValue = 0;
   private boolean maxValueSet = false;

   public XInteger(String displayLabel) {
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

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() || super.get().compareTo("") != 0) {
         IStatus result = super.isValid();
         if (!result.isOK()) {
            return result;
         } else if (!this.isInteger()) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must be an Integer");
         } else if (minValueSet && this.getInteger() < minValue) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must be >= " + minValue);
         } else if (maxValueSet && this.getInteger() > maxValue) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must be <= " + maxValue);
         }
      }
      return Status.OK_STATUS;
   }
}
