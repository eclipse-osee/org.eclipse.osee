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

package org.eclipse.osee.orcs.rest.internal.writers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class ArtifactJsonWriter implements MessageBodyWriter<Object> {

   private JsonFactory jsonFactory;
   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void start() {
      jsonFactory = orcsApi.jaxRsApi().getFactory();
   }

   public void stop() {
      jsonFactory = null;
   }

   @Override
   public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      boolean result = false;
      if (Iterable.class.isAssignableFrom(type) && genericType.getClass().isAssignableFrom(
         ArtifactReadable.class) && MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
         result = true;
      }
      return result;
   }

   private boolean matches(Class<? extends Annotation> toMatch, Annotation[] annotations) {
      for (Annotation annotation : annotations) {
         if (annotation.annotationType().isAssignableFrom(toMatch)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      JsonGenerator writer = null;
      if (type.isAssignableFrom(ArtifactReadable.class)) {
         ArtifactReadable artifact = (ArtifactReadable) object;
         try {
            writer = jsonFactory.createGenerator(entityStream);
            //         writer.setPrettyPrinter(new DefaultPr)
            writer.writeStartObject();
            writer.writeNumberField("uuid", artifact.getId());
            if (matches(IdentityView.class, annotations)) {
               writer.writeStringField("Name", artifact.getName());
            } else {
               Collection<AttributeTypeGeneric<?>> attrTypes = orcsApi.tokenService().getAttributeTypes();
               ResultSet<? extends AttributeReadable<Object>> attributes = artifact.getAttributes();
               if (!attributes.isEmpty()) {
                  for (AttributeTypeGeneric<?> attrType : attrTypes) {
                     if (artifact.isAttributeTypeValid(attrType)) {
                        List<Object> attributeValues = artifact.getAttributeValues(attrType);
                        if (!attributeValues.isEmpty()) {

                           if (attributeValues.size() > 1) {
                              writer.writeArrayFieldStart(attrType.getName());
                              for (Object value : attributeValues) {
                                 writer.writeObject(value);
                              }
                              writer.writeEndArray();
                           } else if (attributeValues.size() == 1) {
                              Object value = attributeValues.iterator().next();
                              writer.writeObjectField(attrType.getName(), value);
                           }

                        }
                     }
                  }
               }
            }
            writer.writeEndObject();
         } finally {
            if (writer != null) {
               writer.flush();
            }
         }
      }
   }
}