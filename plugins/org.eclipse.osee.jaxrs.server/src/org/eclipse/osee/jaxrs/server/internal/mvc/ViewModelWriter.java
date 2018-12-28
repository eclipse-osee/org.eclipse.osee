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
package org.eclipse.osee.jaxrs.server.internal.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.jaxrs.mvc.ViewResolver;
import org.eclipse.osee.jaxrs.mvc.ViewWriter;
import org.eclipse.osee.jaxrs.server.internal.JaxRsUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class ViewModelWriter implements MessageBodyWriter<ViewModel> {

   private final ConcurrentHashMap<String, ViewResolver<?>> resolvers =
      new ConcurrentHashMap<>();

   @Context
   private ResourceInfo resourceInfo;

   public void addResolver(ServiceReference<ViewResolver<?>> reference) {
      String componentName = JaxRsUtils.getComponentName(reference);
      Bundle bundle = reference.getBundle();
      ViewResolver<?> resolver = bundle.getBundleContext().getService(reference);
      resolvers.put(componentName, resolver);
   }

   public void removeResolver(ServiceReference<ViewResolver<?>> reference) {
      String componentName = JaxRsUtils.getComponentName(reference);
      resolvers.remove(componentName);
   }

   private Iterable<ViewResolver<?>> getViewResolvers() {
      return resolvers.values();
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return ViewModel.class.isAssignableFrom(type);
   }

   @Override
   public long getSize(ViewModel model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public void writeTo(ViewModel model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      ViewWriter writer = resolve(model, mediaType);
      if (writer == null) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "Template [%s] could not be resolved",
            model.getViewId());
      }
      httpHeaders.putSingle(HttpHeaders.CONTENT_TYPE, writer.getMediaType());
      writer.write(httpHeaders, entityStream);
   }

   private ViewWriter resolve(ViewModel model, MediaType mediaType) {
      ViewWriter toReturn = null;
      if (model instanceof ViewWriter) {
         toReturn = (ViewWriter) model;
      } else {
         Class<?> resourceClass = resourceInfo.getResourceClass();
         Iterable<ViewResolver<?>> resolvers = getViewResolvers();
         List<MediaType> mediaTypes = getMediaTypesProduced(resourceInfo);
         mediaTypes.add(0, mediaType);
         toReturn = resolve(model, resourceClass, mediaTypes, resolvers);
      }
      return toReturn;
   }

   private ViewWriter resolve(ViewModel model, Class<?> resolvingClass, Iterable<MediaType> mediaTypes, Iterable<ViewResolver<?>> resolvers) {
      ViewWriter toReturn = null;
      for (ViewResolver<?> resolver : resolvers) {
         for (MediaType mediaType : mediaTypes) {
            ViewWriter writer = resolve(model, resolvingClass, mediaType, resolver);
            if (writer != null) {
               toReturn = writer;
               break;
            }
         }
      }
      return toReturn;
   }

   private <T> ResolvedView<T> resolve(ViewModel model, Class<?> resourceClass, MediaType mediaType, ViewResolver<T> resolver) {
      ResolvedView<T> toReturn = null;
      T viewReference = resolver.resolve(model.getViewId(), mediaType);
      if (viewReference != null) {
         toReturn = newResolved(resolver, resourceClass, mediaType, model, viewReference);
      }
      return toReturn;
   }

   private static <T> List<T> getAnnotations(Method method, Class<T> clazz) {
      List<T> annotations = new ArrayList<>();
      if (method != null) {
         for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().isAssignableFrom(clazz)) {
               @SuppressWarnings("unchecked")
               T object = (T) annotation;
               annotations.add(object);
            }
         }
      }
      return annotations;
   }

   private static List<MediaType> getMediaTypesProduced(ResourceInfo resourceInfo) {
      ArrayList<MediaType> produces = new ArrayList<>();

      boolean hasText = false;
      Method method = resourceInfo.getResourceMethod();
      List<Produces> annotations = getAnnotations(method, Produces.class);
      for (Produces annotation : annotations) {
         String[] mediaTypes = annotation.value();
         for (String mediaType : mediaTypes) {
            MediaType toAdd = MediaType.valueOf(mediaType);
            if (MediaType.TEXT_HTML_TYPE.equals(toAdd)) {
               hasText = true;
            } else {
               produces.add(toAdd);
            }
         }
      }

      if (hasText) {
         produces.add(0, MediaType.TEXT_HTML_TYPE);
      }
      if (produces.isEmpty()) {
         produces.add(MediaType.WILDCARD_TYPE);
      }
      return produces;
   }

   private static <T> ResolvedView<T> newResolved(ViewResolver<T> resolver, Class<?> resourceClass, MediaType mediaType, ViewModel model, T viewReference) {
      String viewId = model.getViewId();
      ResolvedView<T> toReturn = new ResolvedView<>(viewId, resolver, mediaType, viewReference);
      toReturn.asMap().putAll(model.asMap());
      return toReturn;
   }

   private static final class ResolvedView<T> extends ViewModel implements ViewWriter {

      private final ViewResolver<T> resolver;
      private final MediaType mediaType;
      private final T viewReference;

      public ResolvedView(String viewId, ViewResolver<T> resolver, MediaType mediaType, T viewReference) {
         super(viewId);
         this.resolver = resolver;
         this.mediaType = mediaType;
         this.viewReference = viewReference;
      }

      @Override
      public MediaType getMediaType() {
         return mediaType;
      }

      @Override
      public void write(MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
         resolver.write(this, viewReference, mediaType, httpHeaders, entityStream);
      }
   }
}