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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author Ryan D. Brooks
 */
public class WriterOperationLogger extends OperationLogger {
   private final PrintWriter writer;

   public WriterOperationLogger(Writer writer) {
      this(new PrintWriter(writer));
   }

   public WriterOperationLogger(PrintWriter writer) {
      this.writer = writer;
   }

   public WriterOperationLogger(PrintStream stream) {
      this(new PrintWriter(stream));
   }

   @Override
   public void log(String... row) {
      for (String cell : row) {
         writer.print(cell);
         writer.print("   ");
      }
      writer.println();
   }

   public PrintWriter getWriter() {
      return writer;
   }
}