/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.server.internal.resources;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * @author Roberto E. Escobar
 */
@Provider
public abstract class AbstractMessageBodyWriter<T> implements MessageBodyWriter<T> {

   public abstract Collection<MediaType> getSupportedMediaTypes();

   public abstract Class<T> getSupportedClass();

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return getSupportedClass().isAssignableFrom(type) && getSupportedMediaTypes().contains(mediaType);
   }

   @Override
   public long getSize(T data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

}
