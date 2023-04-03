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

package org.eclipse.osee.ats.core.workflow.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
@Provider
public class WorkItemJsonReader implements MessageBodyReader<IAtsWorkItem> {

   /**
    * OSGI activation method.
    */

   public void start() {
      //no action
   }

   /**
    * OSGI deactivation method.
    */

   public void stop() {
      //no action
   }

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == IAtsWorkItem.class;
   }

   @Override
   public IAtsWorkItem readFrom(Class<IAtsWorkItem> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
      try {
         String jsonStr = Lib.inputStreamToString(entityStream);

         List<Long> workItems = WorkItemsJsonReader.getWorkItemIdsFromJson(jsonStr);

         return AtsApiService.get().getQueryService().createQuery(WorkItemType.WorkItem).andIds(
            workItems.iterator().next()).getResults().getExactlyOne();
      } catch (Exception ex) {
         throw new IOException("Error deserializing a TraxRpcr Item.", ex);
      }
   }
}
