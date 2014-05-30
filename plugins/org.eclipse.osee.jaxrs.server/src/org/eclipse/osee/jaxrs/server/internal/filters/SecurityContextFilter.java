/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.filters;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.jaxrs.server.internal.SecurityContextProvider;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class SecurityContextFilter implements ResourceFilter, ContainerRequestFilter {

   private final SecurityContextProvider contextProvider;

   public SecurityContextFilter(SecurityContextProvider contextProvider) {
      super();
      this.contextProvider = contextProvider;
   }

   @Override
   public ContainerRequest filter(ContainerRequest request) {
      SecurityContext securityContext = contextProvider.getSecurityContext(request);
      if (securityContext != null) {
         request.setSecurityContext(securityContext);
      }
      return request;
   }

   @Override
   public ContainerRequestFilter getRequestFilter() {
      return this;
   }

   @Override
   public ContainerResponseFilter getResponseFilter() {
      return null;
   }

}
