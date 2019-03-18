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
package org.eclipse.osee.ats.rest.internal.workitem;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.WorkItemWriterOptions;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Donald G. Dunne
 */
@Provider
public class WorkItemsJsonWriter implements MessageBodyWriter<Collection<IAtsWorkItem>> {
   private JsonFactory jsonFactory;
   private AtsApi atsApi;
   @Context
   private UriInfo uriInfo;
   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public UriInfo getUriInfo() {
      return uriInfo;
   }

   public void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
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
   public long getSize(Collection<IAtsWorkItem> data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return Lib.isCollectionOfType(type, genericType, IAtsWorkItem.class);
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
   public void writeTo(Collection<IAtsWorkItem> workItems, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      JsonGenerator writer = null;
      try {
         writer = jsonFactory.createGenerator(entityStream);
         writer.writeStartArray();
         MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(true);
         List<WorkItemWriterOptions> options = new LinkedList<>();
         boolean valuesWithIds = false;
         if (queryParameters.containsKey(WorkItemWriterOptions.KeysAsIds.name()) && queryParameters.getFirst(
            WorkItemWriterOptions.KeysAsIds.name()).equals("true")) {
            options.add(WorkItemWriterOptions.KeysAsIds);
         }
         if (queryParameters.containsKey(WorkItemWriterOptions.DatesAsLong.name()) && queryParameters.getFirst(
            WorkItemWriterOptions.DatesAsLong.name()).equals("true")) {
            options.add(WorkItemWriterOptions.DatesAsLong);
         }
         if (queryParameters.containsKey(WorkItemWriterOptions.ValuesWithIds.name()) && queryParameters.getFirst(
            WorkItemWriterOptions.ValuesWithIds.name()).equals("true")) {
            options.add(WorkItemWriterOptions.ValuesWithIds);
            valuesWithIds = true;
         }
         if (queryParameters.containsKey(WorkItemWriterOptions.WriteRelatedAsTokens.name()) && queryParameters.getFirst(
            WorkItemWriterOptions.WriteRelatedAsTokens.name()).equals("true")) {
            options.add(WorkItemWriterOptions.WriteRelatedAsTokens);
         }
         for (IAtsWorkItem workItem : workItems) {
            if (valuesWithIds) {
               WorkItemJsonWriter.addWorkItemWithIds(atsApi, orcsApi, workItem, annotations, writer,
                  matches(IdentityView.class, annotations), options);
            } else {
               WorkItemJsonWriter.addWorkItem(atsApi, orcsApi, workItem, annotations, writer,
                  matches(IdentityView.class, annotations), getAttributeTypes(), options);
            }
         }
         writer.writeEndArray();
      } finally {
         if (writer != null) {
            writer.flush();
         }
      }
   }
}