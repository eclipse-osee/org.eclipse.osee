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