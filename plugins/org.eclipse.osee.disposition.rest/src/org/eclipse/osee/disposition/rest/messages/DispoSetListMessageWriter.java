/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.disposition.rest.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */
public class DispoSetListMessageWriter implements MessageBodyWriter<List<DispoSet>> {

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      if (genericType instanceof ParameterizedType) {
         return ((ParameterizedType) genericType).getActualTypeArguments()[0] == DispoSet.class;
      } else {
         return false;
      }
   }

   @Override
   public long getSize(List<DispoSet> dispoSets, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
      return -1;
   }

   @Override
   public void writeTo(List<DispoSet> dispoSets, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
      throws IOException, WebApplicationException {
      String jsonString = JsonUtil.toJson(dispoSets);
      entityStream.write(jsonString.getBytes(Strings.UTF_8));
   }
}
