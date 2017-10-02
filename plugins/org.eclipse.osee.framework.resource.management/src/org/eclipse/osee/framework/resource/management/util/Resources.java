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
package org.eclipse.osee.framework.resource.management.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.internal.CompressedResourceBridge;

/**
 * @author Roberto E. Escobar
 */
public class Resources {

   private static String removeName(String path) {
      int index = path.lastIndexOf("/");
      if (index != -1) {
         path = path.substring(0, index + 1);
      }
      return path;
   }

   public static IResource compressResource(IResource resource) {
      InputStream inputStream = null;
      byte[] buffer = new byte[0];
      try {
         inputStream = resource.getContent();
         buffer = Lib.compressStream(inputStream, resource.getName());
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }
      return createResourceFromBytes(buffer, resource.getLocation() + ".zip", true);
   }

   public static IResource decompressResource(IResource resource) {
      String path = resource.getLocation().toASCIIString();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      String fileName = null;

      InputStream inputStream = null;
      try {
         inputStream = resource.getContent();
         fileName = Lib.decompressStream(inputStream, outputStream);
         fileName = URLEncoder.encode(fileName, "UTF-8");
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
         Lib.close(outputStream);
      }
      if (fileName != null && fileName.length() > 0) {
         path = removeName(path) + fileName;
      } else {
         path = Lib.removeExtension(path);
      }
      return createResourceFromBytes(outputStream.toByteArray(), path, false);
   }

   public static IResource createResourceFromBytes(byte[] bytes, String path, boolean isCompressed) {
      try {
         return new CompressedResourceBridge(bytes, new URI(path), isCompressed);
      } catch (URISyntaxException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }
}
