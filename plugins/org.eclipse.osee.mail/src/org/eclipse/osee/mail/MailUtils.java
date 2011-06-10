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
import javax.activation.CommandMap;
import javax.activation.DataSource;
import javax.activation.MailcapCommandMap;
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

   public static MailcapCommandMap getMailcapCommandMap() {
      MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
      mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
      mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
      mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
      mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
      mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
      return mc;
   }

   //The String.format can handle the '%' character.
   public static DataSource createFromString(String name, String message, Object... args) {
      //The '%' has special meaning as a format specifier to the 
      // String::format() function.  Because of this we replace
      // the '%' with its unicode representation.
      String msgWReplacedChar = message.replace("%", "\\u0025");
      String data = String.format(msgWReplacedChar, args);
      StringDataSource dataSource = new StringDataSource(name, data);
      dataSource.setCharset("UTF-8");
      dataSource.setContentType("text/plain");
      return dataSource;
   }

   //returns -1 if searchChars are NOT found.
   // Else it returns the index (>=0) into str where the first searchChar is found.
   public static int containsAny(String str, char[] searchChars) {
      if (str == null || str.length() == 0 || searchChars == null || searchChars.length == 0) {
         return -1;
      }
      for (int i = 0; i < str.length(); i++) {
         char ch = str.charAt(i);
         for (int j = 0; j < searchChars.length; j++) {
            if (searchChars[j] == ch) {
               return i;
            }
         }
      }
      return -1;
   }

   public static DataSource createOutlookEvent(String location, String event, Date date, String startTime, String endTime) {
      OutlookCalendarEvent calendarEvent = new OutlookCalendarEvent(location, event, date, startTime, endTime);

      //The event string is used as a file name and therefore must be
      // validated and its contents restricted to valid filename characters.
      // Invalid Windows characters based on - http://msdn.microsoft.com/en-us/library/aa365247%28v=vs.85%29.aspx#naming_conventions
      char[] illegalChars_Windows = {'<', '>', ':', '\"', '/', '\\', '|', '?', '*'};
      int charIndex = containsAny(event, illegalChars_Windows);
      if (charIndex >= 0) {
         System.out.println("Illegal Windows character found in event string: " + event.substring(0, charIndex + 1) + "<--");
         return null;
      }

      StringBuilder strbld = new StringBuilder(event);
      strbld.append(".vcs");
      //System.out.println("event:" + event + " strbld:" + strbld.toString());
      return createFromString(strbld.toString(), calendarEvent.getEvent());
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
         throw new UnsupportedOperationException("OutputStream is not available for this source");
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
