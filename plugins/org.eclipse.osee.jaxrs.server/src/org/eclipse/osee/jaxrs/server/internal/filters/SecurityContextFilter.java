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

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class SecurityContextFilter implements ContainerRequestFilter {

   private final SecurityContextProvider contextProvider;

   public SecurityContextFilter(SecurityContextProvider contextProvider) {
      super();
      this.contextProvider = contextProvider;
   }

   @Override
   public void filter(ContainerRequestContext request) {
      SecurityContext securityContext = contextProvider.getSecurityContext(request);
      if (securityContext != null) {
         request.setSecurityContext(securityContext);
      }
   }

}
