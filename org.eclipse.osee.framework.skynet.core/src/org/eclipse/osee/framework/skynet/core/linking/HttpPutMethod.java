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

/**
 * @author Roberto E. Escobar
 */
final class HttpPutMethod implements IHttpMethod {

   // private static ExtensionDefinedObjects<IHttpServerRequest> extensionObjects = null;

   protected HttpPutMethod() {
   }

   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      //      String requestType = httpRequest.getUrlRequest();
      boolean handled = false;

      System.out.println("Put request: " + httpRequest.getRawRequest());

      //      if (!httpRequest.getMethod().equals(HttpMethod.RESOURCE_GET)) {
      //         if (extensionObjects == null) {
      //            extensionObjects = new ExtensionDefinedObjects<IHttpServerRequest>(
      //                  "org.eclipse.osee.framework.skynet.core.HttpServerRequest", "IHttpServerRequest", "classname");
      //         }
      //         List<IHttpServerRequest> httpServerRequests = extensionObjects.getObjects();
      //         for (IHttpServerRequest request : httpServerRequests) {
      //            if (request.getRequestType().equals(requestType)) {
      //               request.processRequest(httpRequest, httpResponse);
      //               handled = true;
      //            }
      //         }
      //      }
      //      if (handled == false) {
      //         // If none of the other requests work then we have a resource request
      //         HttpResourceRequest.getInstance().processRequest(httpRequest, httpResponse);
      //      }
   }
}
