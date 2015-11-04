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

         List<Long> uuids = new ArrayList<>();
         while (jParser.nextToken() != JsonToken.END_ARRAY) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
               String key = jParser.getCurrentName();
               if ("uuid".equals(key)) {
                  jParser.nextToken();
                  String value = jParser.getText();
                  if (Strings.isNumeric(value)) {
                     long uuid = Long.valueOf(value);
                     uuids.add(uuid);
                  }
               }
            }
         }
         Collection<IAtsWorkItem> items =
            AtsClientService.get().getQueryService().createQuery(WorkItemType.WorkItem).andUuids(
               uuids.toArray(new Long[uuids.size()])).getItems();
         return items;
      } catch (Exception ex) {
         throw new IOException("Error deserializing a IAtsWorkItem.", ex);
      }
   }
}
