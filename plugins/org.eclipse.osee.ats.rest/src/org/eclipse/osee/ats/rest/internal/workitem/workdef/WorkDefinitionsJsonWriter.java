/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 **********************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem.workdef;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/** Writes stable Work Definition identities used by clients to resolve their local definitions. */
@Provider
public class WorkDefinitionsJsonWriter implements MessageBodyWriter<Collection<WorkDefinition>> {

   @Override
   public long getSize(Collection<WorkDefinition> data, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
      return -1;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return Lib.isCollectionOfType(type, genericType, WorkDefinition.class)
         && MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
   }

   @Override
   public void writeTo(Collection<WorkDefinition> workDefinitions, Class<?> type, Type genericType,
      Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream) throws IOException, WebApplicationException {
      try (JsonGenerator writer = JsonUtil.getFactory().createGenerator(entityStream)) {
         writer.writeStartArray();
         for (WorkDefinition workDefinition : workDefinitions) {
            writer.writeStartObject();
            writer.writeNumberField("id", workDefinition.getId());
            writer.writeEndObject();
         }
         writer.writeEndArray();
      }
   }
}
