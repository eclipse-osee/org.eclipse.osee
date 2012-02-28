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
package org.eclipse.osee.orcs.db.internal.util;

import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.framework.core.operation.OperationLogger;

/**
 * @author Ryan D. Brooks
 */
public class OperationLoggerAdapter extends OperationLogger {
   private final Console ci;

   public OperationLoggerAdapter(Console ci) {
      this.ci = ci;
   }

   @Override
   public void log(String... row) {
      for (String cell : row) {
         ci.write(cell);
         ci.write("   ");
      }
      ci.writeln();
   }

   @Override
   public void log(Throwable th) {
      ci.writeln(th);
   }
}