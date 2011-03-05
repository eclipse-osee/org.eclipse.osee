/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public abstract class OperationLogger {

   /**
    * Reports as a row. Subsequent calls are reported as a new row (i.e. new line)
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
