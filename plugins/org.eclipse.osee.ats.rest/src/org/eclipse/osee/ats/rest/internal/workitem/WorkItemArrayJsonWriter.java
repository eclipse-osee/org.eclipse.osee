/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.WorkItemArray;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Donald G. Dunne
 * @author David W. Miller
 */
@Provider
public class WorkItemArrayJsonWriter implements MessageBodyWriter<WorkItemArray> {

   private JsonFactory jsonFactory;

   private IAtsServer atsServer;

   public void setAtsServer(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   public void start() {
      jsonFactory = org.eclipse.osee.ats.rest.util.JsonFactory.create();
   }

   public void stop() {
      jsonFactory = null;
   }

   @Override
   public long getSize(WorkItemArray data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      boolean assignableFrom = WorkItemArray.class.isAssignableFrom(type);
      return assignableFrom && MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
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
      return atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes();
   }

   @Override
   public void writeTo(WorkItemArray workItemArray, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      JsonGenerator writer = null;
      try {
         writer = jsonFactory.createJsonGenerator(entityStream);
         writer.writeStartObject();
         writer.writeArrayFieldStart("workItems");
         for (IAtsWorkItem workItem : workItemArray.getWorkItems()) {
            WorkItemJsonWriter.addWorkItem(atsServer, workItem, annotations, writer,
               matches(IdentityView.class, annotations), getAttributeTypes());
         }
         writer.writeEndArray();
         writer.writeEndObject();
      } finally {
         if (writer != null) {
            writer.flush();
         }
      }
   }
}