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

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/** Resolves transported Work Definition identifiers against the client Work Definition provider. */
@Provider
public class WorkDefinitionsJsonReader implements MessageBodyReader<Collection<WorkDefinition>> {

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return Lib.isCollectionOfType(type, genericType, WorkDefinition.class);
   }

   @Override
   public Collection<WorkDefinition> readFrom(Class<Collection<WorkDefinition>> type, Type genericType,
      Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream) throws IOException, WebApplicationException {
      try {
         JsonNode workDefinitionNodes = AtsApiService.get().jaxRsApi().readTree(Lib.inputStreamToString(entityStream));
         Collection<WorkDefinition> workDefinitions = new ArrayList<>();
         for (JsonNode workDefinitionNode : workDefinitionNodes) {
            workDefinitions.add(
               AtsApiService.get().getWorkDefinitionService().getWorkDefinition(workDefinitionNode.get("id").asLong()));
         }
         return workDefinitions;
      } catch (Exception ex) {
         throw new IOException("Error deserializing Work Definitions.", ex);
      }
   }
}
