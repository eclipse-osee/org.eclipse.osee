/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

/**
 * A {@link MessageBodyWriter} implementation that outputs the {@link InputStream} contained in an {@link Attachment}
 * and sets the &quot;Content-Disposition&quot; of the HTTP response from the {@link Attachment}'s content disposition.
 *
 * @implNote This class is for the OSEE Server to send {@link Attachment} objects. It is loaded by OSGI as a service.
 * @author Loren K. Ashley
 */

@Provider
public class AttachmentMessageBodyWriter implements MessageBodyWriter<Attachment> {

   /**
    * Predicate to determine if the {@link MessageBodyWriter} implementation can output the provided type. This
    * implementation always returns <code>true</code>;
    *
    * @param type the class of instance that is to be written.
    * @param genericType the type of instance to be written, obtained either by reflection of a resource method return
    * type or via inspection of the returned instance. GenericEntity provides a way to specify this information at
    * runtime.
    * @param annotations an array of the annotations attached to the message entity instance.
    * @param mediaType the media type of the HTTP entity.
    * @return <code>true</code>;
    */

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return true;
   }

   /**
    * Writes an {@link Attachment} type to an HTTP message. The message header map is mutable but any changes must be
    * made before writing to the output stream since the headers will be flushed prior to writing the message body.
    *
    * @param attachment the instance to write.
    * @param type the class of the instance that is to be written.
    * @param genericType the type of instance to be written. GenericEntity provides a way to specify this information at
    * runtime.
    * @param annotations an array of the annotations attached to the message entity instance.
    * @param mediaType the media type of the HTTP entity.
    * @param httpHeaders a mutable map of the HTTP message headers.
    * @param outputStream the {@link OutputStream} for the HTTP entity. The implementation should not close the output
    * stream.
    * @throws IOException when a failure occurs transferring the {@link Attachment} {@link InputStream} to the
    * <code>outputSream</code>.
    */

   @SuppressWarnings("unchecked")
   @Override
   public void writeTo(Attachment attachment, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream outputStream)
      throws IOException, WebApplicationException {

      httpHeaders.putAll((Map<? extends String, ? extends List<Object>>) attachment.getHeaders());
      httpHeaders.put("Content-Disposition", List.of(attachment.getContentDisposition().toString()));
      try (var stream = attachment.getDataHandler().getInputStream();) {
         stream.transferTo(outputStream);
      }
   }

}

/* EOF */
