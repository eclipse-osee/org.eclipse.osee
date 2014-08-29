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
package org.eclipse.osee.orcs.rest.internal.writers;

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
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class ArtifactJsonWriter implements MessageBodyWriter<ArtifactReadable> {

   private JsonFactory jsonFactory;
   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void start() {
      jsonFactory = new JsonFactory();
   }

   public void stop() {
      jsonFactory = null;
   }

   @Override
   public long getSize(ArtifactReadable data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return ArtifactReadable.class.isAssignableFrom(type) && MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
   }

   private boolean matches(Class<? extends Annotation> toMatch, Annotation[] annotations) {
      for (Annotation annotation : annotations) {
         if (annotation.annotationType().isAssignableFrom(toMatch)) {
            return true;
         }
      }
      return false;
   }

   private AttributeTypes getAttibuteTypes() {
      return orcsApi.getOrcsTypes(null).getAttributeTypes();
   }

   @Override
   public void writeTo(ArtifactReadable artifact, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      JsonGenerator writer = null;
      try {
         writer = jsonFactory.createJsonGenerator(entityStream);
         writer.writeStartObject();
         writer.writeNumberField("uuid", artifact.getLocalId());
         if (matches(IdentityView.class, annotations)) {
            writer.writeStringField("Name", artifact.getName());
         } else {
            AttributeTypes attributeTypes = getAttibuteTypes();
            Collection<? extends IAttributeType> attrTypes = attributeTypes.getAll();
            ResultSet<? extends AttributeReadable<Object>> attributes = artifact.getAttributes();
            if (!attributes.isEmpty()) {
               for (IAttributeType attrType : attrTypes) {
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