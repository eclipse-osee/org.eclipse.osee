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
package org.eclipse.osee.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.MultipartDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.eclipse.osee.framework.jdk.core.util.windows.OutlookCalendarEvent;

/**
 * @author Roberto E. Escobar
 */
public final class MailUtils {

   private MailUtils() {
      // Utility Class
   }

   public static DataSource createFromString(String name, String message, Object... args) {
      String data = String.format(message, args);
      StringDataSource dataSource = new StringDataSource(name, data);
      dataSource.setCharset("UTF-8");
      dataSource.setContentType("text/plain");
      return dataSource;
   }

   public static DataSource createOutlookEvent(String location, String event, Date date, String startTime, String endTime) {
      OutlookCalendarEvent calendarEvent = new OutlookCalendarEvent(location, event, date, startTime, endTime);
      String attachmentName = String.format("%s.vcs", event);
      return createFromString(calendarEvent.getEvent(), attachmentName);
   }

   public static DataSource createFromHtml(final String name, String htmlData) throws MessagingException {
      String plainText = stripHtmlTags(htmlData);
      return createAlternativeDataSource(name, plainText, htmlData);
   }

   private static String stripHtmlTags(String html) {
      String plainText = html.replaceAll("<(.|\n)*?>", "");
      plainText = plainText.replaceAll("\t", " ");
      plainText = plainText.replaceAll("\r\n", "");
      plainText = plainText.replaceAll("  +", " ");
      return plainText;

   }

   public static DataSource createAlternativeDataSource(String name, String plainText, String htmlText) throws MessagingException {
      final MimeMultipart content = new MimeMultipart("alternative");

      MimeBodyPart text = new MimeBodyPart();
      text.setText(plainText);

      MimeBodyPart html = new MimeBodyPart();
      html.setContent(htmlText, "text/html");

      content.addBodyPart(html);
      content.addBodyPart(text);

      return new MultiPartDataSource(name, content);
   }

   private static final class MultiPartDataSource implements MultipartDataSource {
      private final String name;
      private final MimeMultipart content;

      public MultiPartDataSource(String name, MimeMultipart content) {
         this.name = name;
         this.content = content;
      }

      @Override
      public String getContentType() {
         return content.getContentType();
      }

      @Override
      public InputStream getInputStream() throws IOException {
         ByteArrayOutputStream os = new ByteArrayOutputStream();
         try {
            content.writeTo(os);
         } catch (MessagingException ex) {
            throw new IOException(ex);
         }
         return new ByteArrayInputStream(os.toByteArray());
      }

      @Override
      public String getName() {
         return name;
      }

      @Override
      public OutputStream getOutputStream() {
         return null;
      }

      @Override
      public int getCount() {
         try {
            return content.getCount();
         } catch (MessagingException ex) {
            return 0;
         }
      }

      @Override
      public BodyPart getBodyPart(int index) throws MessagingException {
         return content.getBodyPart(index);
      }

   };

   private static final class StringDataSource implements javax.activation.DataSource {

      private final String name;
      private String data;
      private String charset;
      private String contentType;
      private ByteArrayOutputStream outputStream;

      public StringDataSource(String name, String data) {
         super();
         this.name = name;
         this.data = data;
      }

      @Override
      public String getName() {
         return name;
      }

      public void setCharset(String charset) {
         this.charset = charset;
      }

      public void setContentType(String contentType) {
         this.contentType = contentType.toLowerCase();
      }

      @Override
      public InputStream getInputStream() throws IOException {
         if (data == null && outputStream == null) {
            throw new IOException("No data");
         }
         if (outputStream != null) {
            String encodedOut = outputStream.toString(charset);
            if (data == null) {
               data = encodedOut;
            } else {
               data = data.concat(encodedOut);
            }
            outputStream = null;
         }
         return new ByteArrayInputStream(data.getBytes(charset));
      }

      @Override
      public OutputStream getOutputStream() {
         if (outputStream == null) {
            outputStream = new ByteArrayOutputStream();
         }
         return outputStream;
      }

      @Override
      public String getContentType() {
         String toReturn;
         if (contentType != null && contentType.indexOf("charset") > 0 && contentType.startsWith("text/")) {
            toReturn = contentType;
         } else {
            toReturn = String.format("%s; charset=%s", contentType != null ? contentType : "text/plain", charset);
         }
         return toReturn;
      }

   }
}
