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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.interceptor.security.AbstractAuthorizingInInterceptor;
import org.apache.cxf.jaxrs.security.SimpleAuthorizingFilter;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.eclipse.osee.jaxrs.server.internal.JaxRsResourceManager;
import org.eclipse.osee.jaxrs.server.internal.JaxRsResourceManager.Resource;

/**
 * @author Angel Avila
 */

@Provider
public class OseeAuthorizingFilter extends SimpleAuthorizingFilter {

   private AbstractAuthorizingInInterceptor interceptor;
   private JaxRsResourceManager resourceManager;

   @Override
   public void filter(ContainerRequestContext context) {
      context.getSecurityContext().getUserPrincipal();
      Resource resource = resourceManager.findResource(context);
      if (resource == null) {
         try {
            interceptor.handleMessage(JAXRSUtils.getCurrentMessage());
         } catch (Exception ex) {
            context.abortWith(Response.status(Response.Status.FORBIDDEN).build());
         }
      }
   }

   @Override
   public void setInterceptor(AbstractAuthorizingInInterceptor in) {
      interceptor = in;
   }

   public void setResourceManager(JaxRsResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

}
