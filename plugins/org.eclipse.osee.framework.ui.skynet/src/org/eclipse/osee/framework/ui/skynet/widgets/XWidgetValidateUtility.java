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
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;

/**
 * @author Roberto E. Escobar
 */
public class XWidgetValidateUtility {

   private XWidgetValidateUtility() {
   }

   public static void validate(int requiredQualityOfService, XWidget xWidget, Artifact artifact, String attributeTypeName, Object proposedValue) {
      IStatus status =
            OseeValidator.getInstance().validate(requiredQualityOfService, artifact, attributeTypeName, proposedValue);
      setStatus(status, xWidget);
   }

   public static void setStatus(IStatus status, XWidget xWidget) {
      if (status.isMultiStatus()) {
         for (IStatus item : status.getChildren()) {
            if (item.isOK()) {
               xWidget.removeControlCausedMessage(item.getPlugin());
            } else {
               xWidget.setControlCausedMessage(item.getPlugin(), item.getMessage(),
                     toMessageProviderLevel(item.getSeverity()));
            }
         }
      } else {
         if (!status.isOK()) {
            xWidget.setControlCausedMessageByObject(status.getMessage(), toMessageProviderLevel(status.getSeverity()));
         } else {
            xWidget.removeControlCausedMessageByObject();
         }
      }
   }

   public static boolean isValueInRange(int value, int min, int max) {
      return min <= value && value < max;
   }

   public static int toMessageProviderLevel(int level) {
      int toReturn = IMessageProvider.NONE;
      if (level == IStatus.INFO) {
         toReturn = IMessageProvider.INFORMATION;
      } else if (level == IStatus.WARNING) {
         toReturn = IMessageProvider.WARNING;
      } else if (level == IStatus.ERROR) {
         toReturn = IMessageProvider.ERROR;
      }
      return toReturn;
   }
}
