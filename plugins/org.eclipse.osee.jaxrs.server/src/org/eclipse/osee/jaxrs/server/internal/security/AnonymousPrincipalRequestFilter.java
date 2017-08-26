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
package org.eclipse.osee.jaxrs.server.internal.security;

import java.security.Principal;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil;

/**
 * @author Roberto E. Escobar
 */
@Provider
@PreMatching
public class AnonymousPrincipalRequestFilter implements ContainerRequestFilter {

   @Override
   public void filter(ContainerRequestContext requestContext) {
      SecurityContext sc = requestContext.getSecurityContext();
      Principal principal = sc.getUserPrincipal();
      if (principal == null) {
         org.apache.cxf.security.SecurityContext securityContext =
            OAuthUtil.newSecurityContext(OseePrincipal.Anonymous);
         MessageContext mc = getMessageContext();
         OAuthUtil.saveSecurityContext(mc, securityContext);
      }
   }

   public MessageContext getMessageContext() {
      return new MessageContextImpl(PhaseInterceptorChain.getCurrentMessage());
   }
}