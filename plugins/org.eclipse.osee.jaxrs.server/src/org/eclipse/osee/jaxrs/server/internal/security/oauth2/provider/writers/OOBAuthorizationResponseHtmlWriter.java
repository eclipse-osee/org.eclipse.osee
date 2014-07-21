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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.writers;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.asTemplateValue;
import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.newSingleTemplateRegistry;
import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.newTemplate;
import static org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil.asExpirationValue;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.rs.security.oauth2.common.OOBAuthorizationResponse;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.jaxrs.server.internal.resources.AbstractHtmlWriter;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class OOBAuthorizationResponseHtmlWriter extends AbstractHtmlWriter<OOBAuthorizationResponse> {

   //@formatter:off
   private static final ResourceToken AUTH_CODE_PAGE__TEMPLATE = newTemplate("authorization_code.html", OOBAuthorizationResponseHtmlWriter.class);
   private static final String AUTH_CODE_PAGE__CODE_TAG = "authorizationCode";
   private static final String AUTH_CODE_PAGE__CLIENT_ID_TAG = "clientId";
   private static final String AUTH_CODE_PAGE__EXPIRES_IN_TAG = "expiresIn";
   private static final String AUTH_CODE_PAGE__LOGGED_IN_AS_TAG = "loggedInAs";
   //@formatter:on

   private static final IResourceRegistry REGISTRY = newSingleTemplateRegistry(AUTH_CODE_PAGE__TEMPLATE);

   @Override
   public Class<OOBAuthorizationResponse> getSupportedClass() {
      return OOBAuthorizationResponse.class;
   }

   @Override
   public void writeTo(OOBAuthorizationResponse data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, Writer writer) {
      PageCreator creator = PageFactory.newPageCreator(REGISTRY, // 
         AUTH_CODE_PAGE__CODE_TAG, asTemplateValue(data.getAuthorizationCode()), //
         AUTH_CODE_PAGE__CLIENT_ID_TAG, asTemplateValue(data.getClientId()), //
         AUTH_CODE_PAGE__EXPIRES_IN_TAG, asExpirationValue(data.getExpiresIn()), //
         AUTH_CODE_PAGE__LOGGED_IN_AS_TAG, asTemplateValue(data.getUserId()));
      creator.realizePage(AUTH_CODE_PAGE__TEMPLATE, writer);
   }
}
