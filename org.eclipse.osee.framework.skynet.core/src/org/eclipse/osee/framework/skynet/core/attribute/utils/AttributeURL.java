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

package org.eclipse.osee.framework.skynet.core.attribute.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AttributeURL {

   private AttributeURL() {
   }

   public static URL getStorageURL(int gammaId, String artifactGuid, String extension) throws OseeDataStoreException, MalformedURLException, OseeAuthenticationRequiredException {
      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put("sessionId", ClientSessionManager.getSessionId());
      parameterMap.put("protocol", "attr");
      parameterMap.put("seed", Integer.toString(gammaId));
      parameterMap.put("name", artifactGuid);
      if (Strings.isValid(extension) != false) {
         parameterMap.put("extension", extension);
      }
      String urlString =
            HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.RESOURCE_CONTEXT, parameterMap);
      return new URL(urlString);
   }

   private static URL generatePathURL(String uri) throws OseeDataStoreException {
      try {
         Map<String, String> parameterMap = new HashMap<String, String>();
         parameterMap.put("sessionId", ClientSessionManager.getSessionId());
         parameterMap.put("uri", uri);
         String urlString =
               HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.RESOURCE_CONTEXT, parameterMap);
         return new URL(urlString);
      } catch (Exception ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public static URL getAcquireURL(String uri) throws OseeDataStoreException {
      return generatePathURL(uri);
   }

   public static URL getDeleteURL(String uri) throws OseeDataStoreException {
      return generatePathURL(uri);
   }
}
