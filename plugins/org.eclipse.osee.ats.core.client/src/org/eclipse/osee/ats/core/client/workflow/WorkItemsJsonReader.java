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
import java.util.Collections;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
@Provider
public class WorkItemsJsonReader implements MessageBodyReader<Collection<IAtsWorkItem>> {

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return Lib.isCollectionOfType(type, genericType, IAtsWorkItem.class);
   }

   @Override
   public Collection<IAtsWorkItem> readFrom(Class<Collection<IAtsWorkItem>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
      try {
         JsonFactory jfactory = new JsonFactory();
         JsonParser jParser = jfactory.createJsonParser(entityStream);

         List<Long> ids = new ArrayList<>();
         JsonToken nextToken = jParser.nextToken();
         while (nextToken != JsonToken.END_ARRAY) {
            if (nextToken == JsonToken.START_OBJECT) {
               while (nextToken != JsonToken.END_OBJECT) {
                  String key = jParser.getCurrentName();
                  if ("id".equals(key)) {
                     jParser.nextToken();
                     String value = jParser.getText();
                     if (Strings.isNumeric(value)) {
                        long id = Long.valueOf(value);
                        ids.add(id);
                     }
                  }
                  nextToken = jParser.nextToken();
               }
            }
            nextToken = jParser.nextToken();
         }
         Collection<IAtsWorkItem> items = null;
         if (ids.isEmpty()) {
            items = Collections.emptyList();
         } else {
            items = AtsClientService.get().getQueryService().createQuery(WorkItemType.WorkItem).andIds(
               ids.toArray(new Long[ids.size()])).getItems();
         }
         return items;
      } catch (Exception ex) {
         throw new IOException("Error deserializing a IAtsWorkItem.", ex);
      }
   }
}
