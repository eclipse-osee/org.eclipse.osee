/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
