/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Date;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class XDateWithValidateDam extends XDateDam {

   public static final String WIDGET_ID = XDateWithValidateDam.class.getSimpleName();

   public XDateWithValidateDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               if (date != null) {
                  Date today = new Date();
                  if (date.before(today)) {
                     status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Date is past today");
                  }
               }
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

}
