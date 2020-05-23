/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public abstract class OperationLogger {

   /**
    * Reports as a row. Subsequent calls are reported as a new row (i.e. a new line is appended)
    * 
    * @param row array of strings treated as fields of a row
    */
   public abstract void log(String... row);

   public void log(Throwable th) {
      log(Lib.exceptionToString(th));
   }

   public void log(IStatus status) {
      if (status.getSeverity() == IStatus.ERROR) {
         log(status.getException());
      }
   }

   public final void logf(String format, Object... args) {
      log(String.format(format, args));
   }
}
