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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.jaxrs.server.internal.JaxRsUtils;

/**
 * @author Roberto E. Escobar
 */
@Provider
public abstract class AbstractHtmlWriter<T> extends AbstractMessageBodyWriter<T> {

   @Override
   public Collection<MediaType> getSupportedMediaTypes() {
      return JaxRsUtils.HTML_MEDIA_TYPES;
   }

   @Override
   public final void writeTo(T data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      Writer writer = new OutputStreamWriter(entityStream, JaxRsUtils.UTF_8_ENCODING);
      try {
         writeTo(data, type, genericType, annotations, mediaType, httpHeaders, writer);
      } finally {
         writer.flush();
      }
   }

   public abstract void writeTo(T data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, Writer writer) throws IOException, WebApplicationException;

}
