/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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