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
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.rs.security.oauth2.common.OAuthError;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.jaxrs.server.internal.resources.AbstractHtmlWriter;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class OAuthErrorHtmlWriter extends AbstractHtmlWriter<OAuthError> {

   //@formatter:off
   private static final ResourceToken ERROR_PAGE__TEMPLATE = newTemplate("oauth_error.html", OAuthErrorHtmlWriter.class);
   private static final String ERROR_PAGE__CODE_TAG = "errorCode";
   private static final String ERROR_PAGE__DESCRIPTION_TAG = "errorDescription";
   private static final String ERROR_PAGE__ERROR_URI = "errorUri";
   private static final String ERROR_PAGE__ERROR_STATE = "errorState";
   //@formatter:on

   private static final IResourceRegistry registry = newSingleTemplateRegistry(ERROR_PAGE__TEMPLATE);

   @Override
   public Class<OAuthError> getSupportedClass() {
      return OAuthError.class;
   }

   @Override
   public void writeTo(OAuthError data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, Writer writer) {
      PageCreator creator = PageFactory.newPageCreator(registry, // 
         ERROR_PAGE__CODE_TAG, asTemplateValue(data.getError()), //
         ERROR_PAGE__DESCRIPTION_TAG, asTemplateValue(data.getErrorDescription()), //
         ERROR_PAGE__ERROR_URI, asTemplateValue(data.getErrorUri()), //
         ERROR_PAGE__ERROR_STATE, asTemplateValue(data.getState()));
      creator.realizePage(ERROR_PAGE__TEMPLATE, writer);
   }

}
