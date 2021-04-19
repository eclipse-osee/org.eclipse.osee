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
import org.eclipse.jface.dialogs.IMessageProvider;

/**
 * @author Roberto E. Escobar
 */
public final class XWidgetValidateUtility {

   private XWidgetValidateUtility() {
      // Utility class
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
