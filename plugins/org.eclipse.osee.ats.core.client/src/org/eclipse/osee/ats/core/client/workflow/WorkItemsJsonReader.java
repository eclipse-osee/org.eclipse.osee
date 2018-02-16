/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.workflow;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.codehaus.jackson.map.type.TypeFactory;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
@Provider
public class WorkItemsJsonReader implements MessageBodyReader<Collection<IAtsWorkItem>> {

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return Lib.isCollectionOfType(type, genericType, IAtsWorkItem.class);
   }

   @JsonIgnoreProperties(ignoreUnknown = true)
   public static class WorkItem {
      @JsonSerialize(using = ToStringSerializer.class)
      private Long id;

      public Long getId() {
         return id;
      }

      public void setId(Long id) {
         this.id = id;
      }
   }

   @Override
   public Collection<IAtsWorkItem> readFrom(Class<Collection<IAtsWorkItem>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
      try {
         String jsonStr = Lib.inputStreamToString(entityStream);
         return getWorkItemsFromJson(jsonStr);
      } catch (Exception ex) {
         throw new IOException("Error deserializing a IAtsWorkItem.", ex);
      }
   }

   public static Collection<IAtsWorkItem> getWorkItemsFromJson(String jsonStr) throws IOException, JsonParseException, JsonMappingException {
      List<Long> ids = getWorkItemIdsFromJson(jsonStr);

      List<IAtsWorkItem> items = new LinkedList<>();
      if (!ids.isEmpty()) {
         items.addAll(AtsClientService.get().getQueryService().createQuery(WorkItemType.WorkItem).andIds(
            ids.toArray(new Long[ids.size()])).getItems());
      }
      return items;
   }

   public static List<Long> getWorkItemIdsFromJson(String jsonStr) throws IOException, JsonParseException, JsonMappingException {
      ObjectMapper objectMapper = new ObjectMapper();
      TypeFactory typeFactory = objectMapper.getTypeFactory();

      List<WorkItem> workItemObjects =
         objectMapper.readValue(jsonStr, typeFactory.constructCollectionType(List.class, WorkItem.class));
      List<Long> ids = new ArrayList<>();
      for (WorkItem workItem : workItemObjects) {
         ids.add(workItem.getId());
      }
      return ids;
   }
}
