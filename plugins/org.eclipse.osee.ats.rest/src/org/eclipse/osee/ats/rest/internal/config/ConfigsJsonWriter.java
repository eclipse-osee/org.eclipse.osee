/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Donald G. Dunne
 */
@Provider
public class ConfigsJsonWriter implements MessageBodyWriter<Collection<IAtsConfigObject>> {
   private JsonFactory jsonFactory;
   private AtsApi atsApi;
   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setAtsServer(IAtsServer atsServer) {
      this.atsApi = atsServer;
   }

   public void start() {
      jsonFactory = JsonUtil.getFactory();
   }

   public void stop() {
      jsonFactory = null;
   }

   @Override
   public long getSize(Collection<IAtsConfigObject> data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      boolean isWriteable = false;
      if (Collection.class.isAssignableFrom(type) && genericType instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType) genericType;
         Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();
         if (actualTypeArgs.length == 1) {
            Type t = actualTypeArgs[0];
            if (t instanceof Class) {
               Class<?> clazz = (Class<?>) t;
               isWriteable = IAtsConfigObject.class.isAssignableFrom(clazz);
            }
         }
      }
      return isWriteable;
   }

   private boolean matches(Class<? extends Annotation> toMatch, Annotation[] annotations) {
      for (Annotation annotation : annotations) {
         if (annotation.annotationType().isAssignableFrom(toMatch)) {
            return true;
         }
      }
      return false;
   }

   private AttributeTypes getAttributeTypes() {
      return orcsApi.getOrcsTypes().getAttributeTypes();
   }

   @Override
   public void writeTo(Collection<IAtsConfigObject> programs, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      JsonGenerator writer = null;
      try {
         writer = jsonFactory.createGenerator(entityStream);
         writer.writeStartArray();
         for (IAtsConfigObject program : programs) {
            ConfigJsonWriter.addProgramObject(atsApi, orcsApi, program, annotations, writer,
               matches(IdentityView.class, annotations), getAttributeTypes());
         }
         writer.writeEndArray();
      } finally {
         if (writer != null) {
            writer.flush();
         }
      }
   }
}
