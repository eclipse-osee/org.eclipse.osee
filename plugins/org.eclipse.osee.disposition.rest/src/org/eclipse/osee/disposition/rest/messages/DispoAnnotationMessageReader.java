/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class DispoAnnotationMessageReader implements MessageBodyReader<DispoAnnotationData> {

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == DispoAnnotationData.class;
   }

   @Override
   public DispoAnnotationData readFrom(Class<DispoAnnotationData> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
      String inputStreamToString = Lib.inputStreamToString(entityStream);
      try {
         JSONObject jsonObject = new JSONObject(inputStreamToString);
         DispoAnnotationData annotationData = DispoUtil.jsonObjToDispoAnnotationData(jsonObject);
         return annotationData;
      } catch (Exception ex) {
         throw new IOException("Error deserializing a Dispositionable Item.", ex);
      }
   }
}
