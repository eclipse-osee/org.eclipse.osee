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
package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.net.HttpURLConnection;
import org.eclipse.osee.framework.core.client.server.HttpRequest;
import org.eclipse.osee.framework.core.client.server.HttpResponse;
import org.eclipse.osee.framework.core.client.server.IHttpServerRequest;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ClientLoopbackHandler implements IHttpServerRequest {

   private static final ExtensionDefinedObjects<IClientLoopbackCmd> commands =
      new ExtensionDefinedObjects<>(Activator.PLUGIN_ID + ".ClientLoopbackCmd", "ClientLoopbackCmd",
         "className");

   @Override
   public String getRequestType() {
      return "osee/loopback";
   }

   @Override
   public void processRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) {
      final String cmd = httpRequest.getParameter("cmd");
      if (Strings.isValid(cmd)) {
         boolean wasProcessed = false;
         for (IClientLoopbackCmd command : commands.getObjects()) {
            if (command.isApplicable(cmd)) {
               wasProcessed = true;
               command.execute(httpRequest.getParameters(), httpResponse);
            }
         }
         if (!wasProcessed) {
            httpResponse.outputStandardError(HttpURLConnection.HTTP_NOT_FOUND,
               String.format("Unable to process request: [%s]", httpRequest.getRawRequest()));
         }
      } else {
         httpResponse.outputStandardError(HttpURLConnection.HTTP_BAD_REQUEST,
            String.format("Unable to process request: [%s]", httpRequest.getRawRequest()));
      }
   }
}
