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
package org.eclipse.osee.jaxrs.server.internal.exceptions;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.asTemplateValue;
import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.newSingleTemplateRegistry;
import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.newTemplate;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.jaxrs.ErrorResponse;
import org.eclipse.osee.jaxrs.server.internal.resources.AbstractHtmlWriter;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class ErrorResponseMessageBodyWriter extends AbstractHtmlWriter<ErrorResponse> {

   private static final ResourceToken ERROR_PAGE__TEMPLATE = newTemplate("error_template.html",
      ErrorResponseMessageBodyWriter.class);
   private static final String ERROR_PAGE__MESSAGE_TAG = "errorMessage";
   private static final String ERROR_PAGE__CODE_TAG = "errorStatusCode";
   private static final String ERROR_PAGE__REASON_TAG = "errorReason";
   private static final String ERROR_PAGE__TYPE_TAG = "errorType";
   private static final String ERROR_PAGE__EXCEPTION_TAG = "errorException";
   private static final IResourceRegistry REGISTRY = newSingleTemplateRegistry(ERROR_PAGE__TEMPLATE);

   @Override
   public Class<ErrorResponse> getSupportedClass() {
      return ErrorResponse.class;
   }

   @Override
   public void writeTo(ErrorResponse data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, Writer writer) throws WebApplicationException {
      writeErrorHtml(data, writer);
   }

   public static void writeErrorHtml(ErrorResponse data, Writer writer) {
      PageCreator creator = PageFactory.newPageCreator(REGISTRY, // 
         ERROR_PAGE__MESSAGE_TAG, asTemplateValue(data.getErrorMessage()), //
         ERROR_PAGE__CODE_TAG, asTemplateValue(String.valueOf(data.getErrorCode())), //
         ERROR_PAGE__REASON_TAG, asTemplateValue(data.getErrorReason()), //
         ERROR_PAGE__TYPE_TAG, asTemplateValue(data.getErrorType()), //
         ERROR_PAGE__EXCEPTION_TAG, asTemplateValue(data.getException()) //
      );
      creator.realizePage(ERROR_PAGE__TEMPLATE, writer);
   }

}
