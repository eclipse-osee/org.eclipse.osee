/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */
public class DispoItemListMessageWriter implements MessageBodyWriter<List<DispoItem>> {

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      if (genericType instanceof ParameterizedType) {
         return ((ParameterizedType) genericType).getActualTypeArguments()[0] == DispoItem.class;
      } else {
         return false;
      }
   }

   @Override
   public long getSize(List<DispoItem> dispoItems, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public void writeTo(List<DispoItem> dispoItems, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      List<DispoItem> reconstructedDispoItems = new ArrayList<>();
      for (DispoItem item : dispoItems) {
         DispoItem dispoItemData = DispoUtil.reconstructDispoItem(item, item.getIsIncludeDetails());
         reconstructedDispoItems.add(dispoItemData);
      }
      String jsonString = JsonUtil.toJson(reconstructedDispoItems);
      entityStream.write(jsonString.getBytes(Strings.UTF_8));
   }
}
