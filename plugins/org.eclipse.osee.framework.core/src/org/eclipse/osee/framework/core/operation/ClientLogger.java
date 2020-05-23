/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.core.operation;

import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Karol M. Wilk
 * @author Ryan D. Brooks
 */
public class ClientLogger extends OperationLogger {
   private final Class<?> activatorClass;

   public ClientLogger(Class<?> activatorClass) {
      this.activatorClass = activatorClass;
   }

   @Override
   public void log(String... row) {
      OseeLog.log(activatorClass, Level.INFO, Arrays.deepToString(row));
   }

   @Override
   public void log(Throwable th) {
      OseeLog.log(activatorClass, OseeLevel.SEVERE_POPUP, th);
   }
}