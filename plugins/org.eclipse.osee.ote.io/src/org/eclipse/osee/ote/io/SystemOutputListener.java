package org.eclipse.osee.ote.io;

import java.io.IOException;

public interface SystemOutputListener {

   public void close() throws IOException;

   public void flush() throws IOException;

   public void write(byte[] b, int off, int len) throws IOException;

   public void write(byte[] b) throws IOException;
   
}
