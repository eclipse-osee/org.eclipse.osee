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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public class XPercentDam extends XIntegerDam {

   public XPercentDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry()) {
         IStatus result = super.isValid();
         if (!result.isOK()) {
            return result;
         } else if (!this.isInteger()) {
            return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, "Percent must be an Integer");
         } else if (this.getInteger() < 0 || this.getInteger() > 100) {
            return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, "Percent must be between 0 and 100");
         }
      }
      return Status.OK_STATUS;
   }
}
