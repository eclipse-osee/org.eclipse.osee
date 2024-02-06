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

package org.eclipse.osee.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.activation.DataHandler;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.AttachmentBuilder;
import org.apache.cxf.jaxrs.ext.multipart.InputStreamDataSource;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * A {@link MessageBodyReader} implementation that creates an {@link InputStream} for the received HTTP data and saves
 * it in a new {@link Attachment}. The HTTP response headers including the &quot;Content-Disposition&quot; are also
 * saved in the new {@link Attachment}.
 *
 * @implNote This class is for the OSEE Client to receive {@link Attachment} objects. It is loaded as a provider by the
 * client initialization class {@link CxfJaxRsClientConfigurator}.
 * @author Loren K Ashley
 */

@Provider
public class AttachmentMessageBodyReader implements MessageBodyReader<Attachment> {

   /**
    * Predicate to determine if the {@link MessageBodyReader} implementation can produce an instance of the class
    * specified by <code>type</code>.
    *
    * @param type the type of {@link Class} the {@link AttachmentMessageBodyReader} is being asked to create.
    * @param genericType unused
    * @param annotations unused
    * @param mediaType unused
    * @returns <code>true</code>, when <code>type</code> equals {@link Attachment#getClass}; otherwise,
    * <code>false</code>.
    */

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == Attachment.class;
   }

   /**
    * Produces an {@link Attachment} object from an HTTP message.
    *
    * @param type the class of the instance that is to be created.
    * @param genericType the type of instance to be created. GenericEntity provides a way to specify this information at
    * runtime.
    * @param annotations an array of the annotations attached to the message entity instance.
    * @param mediaType the media type of the HTTP entity.
    * @param httpHeaders a immutable map of the HTTP message headers.
    * @param inputStream the {@link InputStream} for the HTTP entity. The implementation should not close the output
    * stream.
    * @param the {@link Attachment} produced from the <code>inputStream</code>.
    * @throws NoContentException when the {@link InputStream} is empty.
    * @throws IOException if determining the available bytes in the {@link InputStream} fails.
    */

   //@formatter:off
   @Override
   public Attachment
      readFrom
         (
            Class<Attachment> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream inputStream
         )
            throws IOException, WebApplicationException
   //@formatter:on
   {

      if (Objects.isNull(inputStream) || inputStream.available() == 0) {
         //@formatter:off
         throw
            new NoContentException
                   (
                      new Message()
                             .title( "AttachmentMessageBodyReader::readFrom, an empty input stream was recieved." )
                             .indentInc()
                             .segment       ( "Type",         type        )
                             .segment       ( "Generic Type", genericType )
                             .segmentIndexed( "Annotations",  annotations )
                             .segment       ( "Media Type",   mediaType   )
                             .segmentMap    ( "HTTP Headers", httpHeaders )
                             .toString()
                   );
         //@formatter:on
      }

      var attachmentBuilder = new AttachmentBuilder();

      if (Objects.nonNull(httpHeaders)) {
         attachmentBuilder.headers(httpHeaders);
      }

      if (Objects.nonNull(inputStream)) {
         var dataHandler = new DataHandler(new InputStreamDataSource(inputStream, "application/octet-stream"));
         attachmentBuilder.dataHandler(dataHandler);
      }

      var attachment = attachmentBuilder.build();

      return attachment;
   }

}

/* EOF */