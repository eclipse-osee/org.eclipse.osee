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
public class XPercent extends XText {

   public XPercent(String displayLabel) {
      super(displayLabel);
   }

   public void set(int percent) {
      super.set(percent + "");
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry()) {
         IStatus result = super.isValid();
         if (!result.isOK()) {
            return result;
         } else if (!this.isInteger()) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Percent must be an Integer");
         } else if (this.getInteger() < 0 || this.getInteger() > 100) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Percent must be between 0 and 100");
         }
      }
      return Status.OK_STATUS;
   }
}
