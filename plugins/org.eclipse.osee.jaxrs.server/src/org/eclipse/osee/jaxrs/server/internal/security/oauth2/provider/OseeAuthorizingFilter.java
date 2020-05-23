/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider;

import java.util.List;
import java.util.TreeMap;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.interceptor.security.AbstractAuthorizingInInterceptor;
import org.apache.cxf.jaxrs.security.SimpleAuthorizingFilter;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
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

      Message currentMessage = JAXRSUtils.getCurrentMessage();
      @SuppressWarnings("unchecked")
      TreeMap<String, List<String>> protocolHeaders =
         (TreeMap<String, List<String>>) currentMessage.getContextualProperty(
            "org.apache.cxf.message.Message.PROTOCOL_HEADERS");
      List<String> referers = protocolHeaders.get("Referer");

      if (resource != null && referers != null && !referers.toString().contains("coverage")) {
         try {
            interceptor.handleMessage(currentMessage);
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
