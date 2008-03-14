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
package org.eclipse.osee.framework.skynet.core.linking;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class HttpResourceRequest implements IHttpMethod {

   private static HttpResourceRequest instance = new HttpResourceRequest();

   private HttpResourceRequest() {
   }

   public static HttpResourceRequest getInstance() {
      return instance;
   }

   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      String urlRequest = httpRequest.getUrlRequest();
      URL url = findResource(urlRequest);
      if (url != null) {
         sendResource(url, httpResponse.getOutputStream());
      } else {
         httpResponse.outputStandardError(400, "Invalid Request: *" + urlRequest + "*");
      }
   }

   private void sendResource(URL url, OutputStream outputStream) {
      BufferedInputStream bis = null;
      try {
         bis = new BufferedInputStream(url.openStream());
         PrintStream ps = new PrintStream(outputStream, true, "UTF-8");
         byte[] buffer = new byte[1024];
         int count;
         while ((count = bis.read(buffer)) != -1) {
            ps.write(buffer, 0, count);
         }
      } catch (IOException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, "Error sending requested resource", ex);
      } finally {
         try {
            bis.close();
         } catch (IOException ex) {
            SkynetActivator.getLogger().log(Level.SEVERE, "Error closing stream", ex);
         }
      }
   }

   private URL findResource(String urlRequested) {
      URL resource = null;
      if (Strings.isValid(urlRequested) && urlRequested.endsWith("/") != true) {
         String uploadPath = OseeProperties.getInstance().getRemoteHttpServerUploadPath();
         if (Strings.isValid(uploadPath)) {
            try {
               File file = new File(uploadPath + File.separator + urlRequested);
               if (file != null && file.exists() && file.canRead()) {
                  resource = file.toURI().toURL();
               }
            } catch (Exception ex) {
               // Don't do anything since we will continue to look in other places
            }
         }

         if (resource == null) {
            List<IConfigurationElement> elements =
                  ExtensionPoints.getExtensionElements("org.eclipse.osee.framework.skynet.core.WebPage",
                        "WebPageFolder");
            for (IConfigurationElement element : elements) {
               String resourceName = element.getAttribute("Path");
               String bundleName = element.getContributor().getName();

               if (Strings.isValid(bundleName) && Strings.isValid(resourceName)) {
                  try {
                     Bundle bundle = Platform.getBundle(bundleName);
                     URL url = bundle.getEntry(resourceName + urlRequested);
                     if (url != null) {
                        resource = FileLocator.resolve(url);
                        break;
                     }
                  } catch (Exception ex) {
                     throw new IllegalArgumentException(String.format("Unable to Load: [%s.%s]", bundleName,
                           resourceName));
                  }
               }
            }
         }
      }
      return resource;
   }
}
