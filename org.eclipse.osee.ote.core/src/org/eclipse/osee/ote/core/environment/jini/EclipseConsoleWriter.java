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
package org.eclipse.osee.ote.core.environment.jini;

import java.io.IOException;
import java.io.Writer;
import org.eclipse.osee.ote.core.IUserSession;

/**
 * @author Andrew M. Finkbeiner
 */
public class EclipseConsoleWriter extends Writer {

   private final IUserSession callback;
   private StringBuffer buffer;
   private StringBuffer masterBuffer;

   public EclipseConsoleWriter(IUserSession callback) {
      this.callback = callback;
      buffer = new StringBuffer(256);
      masterBuffer = new StringBuffer(256);
   }

   public void write(char[] cbuf, int off, int len) {
      buffer.append(cbuf, off, len);
      masterBuffer.append(cbuf, off, len);
   }

   public void flush() throws IOException {
      if (buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == '\n') {
         buffer.deleteCharAt(buffer.length() - 1);
      }
      try {
         callback.initiateInformationalPrompt(buffer.toString());
      } catch (Exception ex) {
         throw new IOException("failed to initate promt");
      }
      buffer.delete(0, buffer.length());
   }

   public void close() throws IOException {
      flush();
   }

   public String getAllOutput() {
      return masterBuffer.toString();
   }

}
