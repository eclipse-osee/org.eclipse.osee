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
package org.eclipse.osee.framework.jini.util;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.rmi.RemoteException;

public class RemotePrintStream extends PrintStream {

   private IRemotePrintTarget target;
   private PipedInputStream pis;
   private StringWriter writer;
   private byte[] buffer;

   public RemotePrintStream(PipedOutputStream os, IRemotePrintTarget target) throws IOException {
      super(os, true);
      this.pis = new PipedInputStream(os);
      this.target = target;
      writer = new StringWriter();
      buffer = new byte[2048];
   }

   @Override
   public void flush() {
      super.flush();
      int read;
      try {
         read = pis.available();
         do {
            if (read > 2048) read = 2048;
            int readbytes = pis.read(buffer, 0, read);
            writer.append(new String(buffer, 0, readbytes));
            read = pis.available();
         } while (read > 0);
      } catch (IOException e) {
         e.printStackTrace();
      }

      try {
         target.print(writer.toString());
         writer.flush();
         writer.getBuffer().delete(0, writer.getBuffer().capacity());
      } catch (RemoteException e) {
         e.printStackTrace();
      }

   }
}
