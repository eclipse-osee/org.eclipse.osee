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
