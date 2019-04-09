/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints;

import com.google.common.io.ByteSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
@Path("/client")
public class ClientEndpoint extends AbstractClientService {

   public ClientEndpoint(Log logger) {
      super(logger);
   }

   @GET
   @Path("{application-guid}/logo")
   @Produces({"image/png", "image/jpeg", "image/gif"})
   public Response getApplicationLogo(@Context final UriInfo uriInfo, @PathParam("application-guid") final String applicationGuid) {
      return Response.ok(new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws IOException, WebApplicationException {
            ByteSource supplier = getDataProvider().getClientLogoSupplier(uriInfo, applicationGuid);
            if (supplier != null) {
               InputStream inputStream = null;
               try {
                  inputStream = supplier.openStream();
                  Lib.inputStreamToOutputStream(inputStream, outputStream);
               } finally {
                  Lib.close(inputStream);
               }
            }
         }
      }).build();
   }
}