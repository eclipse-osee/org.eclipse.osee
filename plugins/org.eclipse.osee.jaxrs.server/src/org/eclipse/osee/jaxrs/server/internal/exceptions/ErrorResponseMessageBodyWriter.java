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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.ErrorResponse;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class ErrorResponseMessageBodyWriter implements MessageBodyWriter<ErrorResponse> {

   private static final ResourceToken ERROR_PAGE__TEMPLATE = createToken("error_template.html");
   private static final String ERROR_PAGE__MESSAGE_TAG = "errorMessage";
   private static final String ERROR_PAGE__CODE_TAG = "errorStatusCode";
   private static final String ERROR_PAGE__REASON_TAG = "errorReason";
   private static final String ERROR_PAGE__TYPE_TAG = "errorType";
   private static final String ERROR_PAGE__EXCEPTION_TAG = "errorException";

   private IResourceRegistry registry;

   private IResourceRegistry getRegistry() {
      if (registry == null) {
         registry = new ResourceRegistry();
         registry.registerResource(-1L, ERROR_PAGE__TEMPLATE);
      }
      return registry;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == ErrorResponse.class && MediaType.TEXT_HTML_TYPE.equals(mediaType);
   }

   @Override
   public long getSize(ErrorResponse data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public void writeTo(ErrorResponse data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      String errorMessage = data.getErrorMessage();
      int errorCode = data.getErrorCode();
      String reason = data.getErrorReason();
      String errorType = data.getErrorType();
      String exception = data.getException();

      Writer writer = new OutputStreamWriter(entityStream, "UTF-8");
      PageCreator creator = PageFactory.newPageCreator(getRegistry(), // 
         ERROR_PAGE__MESSAGE_TAG, normalize(errorMessage), //
         ERROR_PAGE__CODE_TAG, normalize(String.valueOf(errorCode)), //
         ERROR_PAGE__REASON_TAG, normalize(reason), //
         ERROR_PAGE__TYPE_TAG, normalize(errorType), //
         ERROR_PAGE__EXCEPTION_TAG, normalize(exception) //
      );
      creator.realizePage(ERROR_PAGE__TEMPLATE, writer);
      writer.flush();
   }

   private static String normalize(String value) {
      String toReturn = "N/A";
      if (Strings.isValid(value)) {
         toReturn = value.trim();
         toReturn = toReturn.replaceAll("\r?\n", "<br/>");
      }
      return toReturn;
   }

   private static ResourceToken createToken(String fileName) {
      return new ClassBasedResourceToken(fileName, ErrorResponseMessageBodyWriter.class);
   }

}
