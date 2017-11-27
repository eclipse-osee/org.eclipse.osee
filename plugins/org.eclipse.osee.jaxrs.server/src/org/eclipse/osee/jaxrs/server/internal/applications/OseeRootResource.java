/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.applications;

import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
@Path("/")
public class OseeRootResource {

   @Context
   private UriInfo uriInfo;

   @Context
   private ContainerRequestContext context;

   private final Log logger;

   public OseeRootResource(Log logger) {
      this.logger = logger;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getRoot() {
      String forwardedServer = OAuthUtil.getForwarderServer();
      if (logger != null) {
         logger.trace("Forwarded Server [%s]", forwardedServer);
      }
      String basePath;
      URI location = uriInfo.getRequestUri();
      if (logger != null) {
         logger.trace("URI Location [%s]", location.toString());
      }
      if (Strings.isValid(forwardedServer)) {
         String[] server = forwardedServer.split(",");
         basePath = server[0];
      } else {
         basePath = location.toString();
      }
      if (logger != null) {
         logger.trace("Base Path [%s]", basePath);
      }

      String scheme = location.getScheme();
      URI finalUri = UriBuilder//
         .fromPath(basePath)//
         .scheme(scheme)//
         .path("/osee/ui/index.html")//
         .fragment(location.getRawFragment())//
         .buildFromEncoded();
      if (logger != null) {
         logger.trace("Final Uri [%s]", finalUri);
      }
      return Response.seeOther(finalUri).build();
   }

}
