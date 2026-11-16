/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 **********************************************************************/
package org.eclipse.osee.ats.core.workflow.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/** Resolves a transported Work Definition identifier against the client Work Definition provider. */
@Provider
public class WorkDefinitionJsonReader implements MessageBodyReader<WorkDefinition> {

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == WorkDefinition.class;
   }

   @Override
   public WorkDefinition readFrom(Class<WorkDefinition> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
      throws IOException, WebApplicationException {
      try {
         long workDefinitionId = AtsApiService.get().jaxRsApi().readTree(Lib.inputStreamToString(entityStream)).get(
            "id").asLong();
         return AtsApiService.get().getWorkDefinitionService().getWorkDefinition(workDefinitionId);
      } catch (Exception ex) {
         throw new IOException("Error deserializing a Work Definition.", ex);
      }
   }
}
