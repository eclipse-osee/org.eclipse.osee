/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.ui.skynet.util;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

public class LogUtil {

   /**
    * Create exception string and return. Log if in test so SevereLoggingMonitor picks up.
    */
   public static String getCellExceptionString(Exception ex) {
      String errStr = XViewerCells.getCellExceptionString(ex);
      if (OseeProperties.isInTest()) {
         OseeLog.log(Activator.class, Level.SEVERE, errStr);
      }
      return errStr;
   }

}
