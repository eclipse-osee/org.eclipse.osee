/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.mail.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Based on org.apache.commons.exec.LogOutputStream
 * 
 * @author Roberto E. Escobar
 */
public abstract class ForwardingStream extends OutputStream {

   private static final int INTIAL_SIZE = 132;
   private static final int CR = 0x0d;
   private static final int LF = 0x0a;

   private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(INTIAL_SIZE);

   private boolean skip = false;

   @Override
   public void write(final int cc) {
      final byte c = (byte) cc;
      if (c == '\n' || c == '\r') {
         if (!skip) {
            forwardBuffer();
         }
      } else {
         buffer.write(cc);
      }
      skip = c == '\r';
   }

   protected void forwardBuffer() {
      forward(buffer.toString());
      buffer.reset();
   }

   @Override
   public void write(final byte[] b, final int off, final int len) {
      int offset = off;
      int blockStartOffset = offset;
      int remaining = len;
      while (remaining > 0) {
         while (remaining > 0 && b[offset] != LF && b[offset] != CR) {
            offset++;
            remaining--;
         }
         final int blockLength = offset - blockStartOffset;
         if (blockLength > 0) {
            buffer.write(b, blockStartOffset, blockLength);
         }
         while (remaining > 0 && (b[offset] == LF || b[offset] == CR)) {
            write(b[offset]);
            offset++;
            remaining--;
         }
         blockStartOffset = offset;
      }
   }

   @Override
   public void flush() {
      if (buffer.size() > 0) {
         forwardBuffer();
      }
   }

   @Override
   public void close() throws IOException {
      if (buffer.size() > 0) {
         forwardBuffer();
      }
      super.close();
   }

   protected abstract void forward(String data);

}
