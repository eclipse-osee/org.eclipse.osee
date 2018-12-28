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
package org.eclipse.osee.framework.core.client.server;

import java.util.List;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Roberto E. Escobar
 */
final class HttpGetMethod implements IHttpMethod {

   private static ExtensionDefinedObjects<IHttpServerRequest> extensionObjects = null;

   protected HttpGetMethod() {
   }

   @Override
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      String requestType = httpRequest.getUrlRequest();
      if (extensionObjects == null) {
         extensionObjects = new ExtensionDefinedObjects<>(
            "org.eclipse.osee.framework.skynet.core.HttpServerRequest", "IHttpServerRequest", "classname");
      }
      List<IHttpServerRequest> httpServerRequests = extensionObjects.getObjects();
      for (IHttpServerRequest request : httpServerRequests) {
         if (request.getRequestType().equals(requestType)) {
            request.processRequest(httpRequest, httpResponse);
         }
      }
   }
}
