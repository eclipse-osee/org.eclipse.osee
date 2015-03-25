package org.eclipse.osee.ote.io.internal;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.osee.ote.io.SystemOutputListener;

public class SystemOutputListerImpl implements SystemOutputListener {

   private OutputStream outputStream;

   public SystemOutputListerImpl(OutputStream outputStream) {
      this.outputStream = outputStream;
   }

   @Override
   public void close() throws IOException {
      outputStream.close();
   }

   @Override
   public void flush() throws IOException {
      outputStream.flush();
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException {
      outputStream.write(b, off, len);
   }

   @Override
   public void write(byte[] b) throws IOException {
      outputStream.write(b);
   }

}
