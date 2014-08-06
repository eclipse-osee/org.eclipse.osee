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
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.jaxrs.server.internal.JaxRsUtils;

/**
 * @author Roberto E. Escobar
 */
@Provider
public abstract class AbstractHtmlWriter<T> extends AbstractMessageBodyWriter<T> {

   @Context
   private Providers providers;

   @Override
   public Collection<MediaType> getSupportedMediaTypes() {
      return JaxRsUtils.HTML_MEDIA_TYPES;
   }

   @Override
   public final void writeTo(T data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      ViewModel model = asViewModel(data);
      MessageBodyWriter<ViewModel> writer =
         providers.getMessageBodyWriter(ViewModel.class, genericType, annotations, mediaType);
      writer.writeTo(model, ViewModel.class, genericType, annotations, mediaType, httpHeaders, entityStream);
   }

   public abstract ViewModel asViewModel(T data);

}
