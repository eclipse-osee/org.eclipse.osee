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
package org.eclipse.osee.framework.plugin.core.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ryan D. Brooks
 */
public abstract class ResourceFinder {

   public abstract byte[] find(String path) throws IOException;

   public byte[] getBytes(InputStream in, long length) throws IOException {
      DataInputStream din = new DataInputStream(in);
      byte[] bytes = new byte[(int) length];
      try {
         din.readFully(bytes);
      } finally {
         din.close();
      }
      return bytes;
   }

   public byte[] getBytes(InputStream stream) throws IOException {
      return getBytes(stream, stream.available());
   }

   public void dispose() {
      // do nothing
   }
}
