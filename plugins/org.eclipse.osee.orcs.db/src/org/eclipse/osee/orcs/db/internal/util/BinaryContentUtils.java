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
package org.eclipse.osee.orcs.db.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;

/**
 * @author Roberto E. Escobar
 */
public final class BinaryContentUtils {

   private BinaryContentUtils() {
      // Utility Class
   }

   public static String getContentType(IResource resource) throws OseeCoreException {
      String mimeType;
      InputStream inputStream = null;
      try {
         inputStream = resource.getContent();
         mimeType = URLConnection.guessContentTypeFromStream(inputStream);
      } catch (IOException ex) {
         throw new OseeCoreException(ex, "Error determining mime type for - [%s]", resource.getName());
      } finally {
         Lib.close(inputStream);
      }
      if (mimeType == null) {
         mimeType = URLConnection.guessContentTypeFromName(resource.getLocation().toASCIIString());
         if (mimeType == null) {
            mimeType = "application/*";
         }
      }
      return mimeType;
   }

   public static String getContentType(String extension) {
      String contentType = null;
      if (Strings.isValid(extension)) {
         contentType = URLConnection.guessContentTypeFromName("dummy." + extension);
      } else {
         contentType = "application/*";
      }
      return contentType;
   }
}