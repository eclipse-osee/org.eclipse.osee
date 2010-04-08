/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.ats;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.util.Resource;
import org.w3c.dom.Node;

/**
 * @author Roberto E. Escobar
 */
public class XmlMessage {

   public XmlMessage() {
   }

   public void sendError(HttpServletResponse response, String name, Throwable ex) {
      String errorMessage = Lib.exceptionToString(ex);
      OseeLog.log(Activator.class, Level.SEVERE, errorMessage, ex);
      try {
         StringWriter writer = new StringWriter();
         createXmlErrorMessage(writer, "0", errorMessage);
         IResource resource = new StreamResource(name, writer.toString());
         sendMessage(response, name, resource, true);
      } catch (Exception ex1) {
         OseeLog.log(Activator.class, Level.SEVERE, ex1.toString(), ex1);
      }
   }

   public void sendMessage(HttpServletResponse response, String name, String status, Collection<Node> nodes) {
      try {
         StringWriter writer = new StringWriter();
         createXmlMessage(writer, "0", false, nodes);
         IResource resource = new StreamResource(name, writer.toString());
         sendMessage(response, name, resource, true);
      } catch (Exception ex) {
         sendError(response, name, ex);
      }
   }

   private void createXmlErrorMessage(Writer output, String status, String... messages) throws XMLStreamException {
      createXmlMessage(output, status, true, null, messages);
   }

   private void createXmlMessage(Writer output, String status, boolean isError, Collection<Node> nodes, String... messages) throws XMLStreamException {
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = factory.createXMLStreamWriter(output);
      writer.writeStartDocument("UTF-8", "1.0");
      writer.writeStartElement("response");

      writer.writeStartElement("status");
      writer.writeCharacters(status);
      writer.writeEndElement();

      writer.writeStartElement("startRow");
      writer.writeEndElement();

      writer.writeStartElement("endRow");
      writer.writeEndElement();

      writer.writeStartElement("totalRows");
      writer.writeEndElement();

      if (isError) {
         writer.writeStartElement("errors");
      } else {
         writer.writeStartElement("data");
      }

      try {
         if (nodes != null && !nodes.isEmpty()) {
            XmlUtil.serialize(writer, nodes);
         }
         if (messages != null && messages.length > 0) {
            for (String message : messages) {
               writer.writeStartElement("message");
               writer.writeCharacters(message);
               writer.writeEndElement();
            }
         }
      } finally {
         writer.writeEndElement();
      }
      writer.writeEndElement();
      writer.writeEndDocument();
   }

   private void sendMessage(HttpServletResponse response, String name, IResource resource, boolean isErrorSendingAllowed) {
      InputStream inputStream = null;
      try {
         inputStream = resource.getContent();

         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentLength(inputStream.available());
         response.setCharacterEncoding("ISO-8859-1");

         String mimeType = HttpURLConnection.guessContentTypeFromStream(inputStream);
         if (mimeType == null) {
            mimeType = HttpURLConnection.guessContentTypeFromName(resource.getLocation().toString());
            if (mimeType == null) {
               mimeType = "application/*";
            }
         }
         response.setContentType(mimeType);

         if (!mimeType.startsWith("text") && resource.isCompressed()) {
            response.setHeader("Content-Disposition", "attachment; filename=" + resource.getName());
         }
         Lib.inputStreamToOutputStream(inputStream, response.getOutputStream());
      } catch (Exception ex) {
         if (isErrorSendingAllowed) {
            sendError(response, name, ex);
         } else {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } finally {
         Lib.close(inputStream);
      }
   }

   private final static class StreamResource extends Resource {

      private final ByteArrayInputStream inputStream;
      private final String name;

      public StreamResource(String name, String data) throws UnsupportedEncodingException {
         this(name, false, new ByteArrayInputStream(data.getBytes("ISO-8859-1")));
      }

      public StreamResource(String name, boolean isCompressed, ByteArrayInputStream inputStream) {
         super(null, isCompressed);
         this.name = name;
         this.inputStream = inputStream;
      }

      @Override
      public InputStream getContent() throws OseeCoreException {
         return inputStream;
      }

      @Override
      public String getName() {
         return name;
      }
   }
}
