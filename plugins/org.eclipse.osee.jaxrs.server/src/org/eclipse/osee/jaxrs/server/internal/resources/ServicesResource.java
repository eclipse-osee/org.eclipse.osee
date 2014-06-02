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
package org.eclipse.osee.jaxrs.server.internal.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitable;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitor;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
@Path("services")
public class ServicesResource {

   private static final String CSS = "<style>" + "@CHARSET \"ISO-8859-1\"; " + //
   ".heading { font-size: large; } " + //
   ".field { font-weight: bold; } " + //
   ".value { font-weight: normal; } " + //
   ".porttypename { font-weight: bold; } " + //
   "table { border: solid; border-collapse: collapse; border-width: 2px; } " + //
   "td { border: solid; border-width: 1px; vertical-align: text-top; padding: 5px; } " + //
   "</style>";

   private final JaxRsVisitable visitable;

   public ServicesResource(JaxRsVisitable visitable) {
      super();
      this.visitable = visitable;
   }

   @PermitAll
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getServices(final @Context UriInfo uriInfo) {
      final StringBuilder builder = new StringBuilder();
      builder.append("<html>");
      builder.append("<head>");
      builder.append(CSS);
      builder.append("<meta http-equiv=content-type content=\"text/html; charset=UTF-8\">");
      builder.append("<title>JAX-RS - Service list</title>");
      builder.append("</head>");
      builder.append("<br/><span class=\"heading\">Available RESTful services:</span><br/>");
      builder.append("<table cellpadding=\"1\" cellspacing=\"1\" border=\"1\" width=\"100%\">");

      visitable.accept(new JaxRsVisitor() {

         @Override
         public void onApplication(String applicationContext, String componentName, Bundle bundle, Application application) {
            String absolutePath = getServletPath();
            UriBuilder uriBuilder = UriBuilder.fromPath(absolutePath).path(applicationContext);
            String baseApplicationPath = uriBuilder.build().toASCIIString();
            String wadlPath = uriBuilder.path("application.wadl").build().toASCIIString();

            builder.append("<tr><td>");
            builder.append("<span class=\"field\">Endpoint address:</span>");
            builder.append("<span class=\"value\">");
            builder.append(baseApplicationPath);
            builder.append("</span>");
            builder.append("<br />");
            builder.append("<span class=\"field\">WADL :</span>");
            builder.append("<a href=\"");
            builder.append(wadlPath);
            builder.append("\">");
            builder.append(wadlPath);
            builder.append("</a></td></tr>");
         }

         private String getServletPath() {
            String absolutePath = uriInfo.getAbsolutePath().toASCIIString();
            absolutePath = absolutePath.replaceAll("/services", "");
            return absolutePath;
         }
      });
      builder.append("</table>");
      builder.append("</html>");
      return builder.toString();
   }

}
