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

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import javax.activation.CommandMap;
import javax.activation.DataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.windows.OutlookCalendarEvent;

/**
 * @author Roberto E. Escobar
 */
public final class MailUtils {

   private static final String OUTLOOK_CALENDAR_EXTENSION = ".vcs";

   private MailUtils() {
      // Utility Class
   }

   public static MailcapCommandMap getMailcapCommandMap() {
      MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
      mc.addMailcap("text/*;; x-java-content-handler=com.sun.mail.handlers.text_plain");
      mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
      mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
      mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
      mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
      mc.addMailcap("multipart/mixed;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
      mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
      mc.addMailcap("image/jpeg;; x-java-content-handler=com.sun.mail.handlers.image_jpeg");
      mc.addMailcap("image/gif;; x-java-content-handler=com.sun.mail.handlers.image_gif");
      return mc;
   }

   public static StringDataSource createFromString(String name, String message, Object... args) {
      String data;
      if (args.length > 0) {
         data = String.format(message, args);
      } else {
         data = message;
      }
      StringDataSource dataSource = new StringDataSource(name, data);
      dataSource.setCharset("UTF-8");
      dataSource.setContentType("text/plain");
      return dataSource;
   }

   public static DataSource createFromHtml(final String name, String htmlData) throws Exception {
      String plainText = stripHtmlTags(htmlData);
      return createAlternativeDataSource(name, htmlData, plainText);
   }

   public static DataSource createAlternativeDataSource(String name, String htmlText, String plainText) throws Exception {
      final MimeMultipart content = new MimeMultipart("alternative");

      MimeBodyPart html = new MimeBodyPart();
      html.setContent(htmlText, "text/html");

      MimeBodyPart text = new MimeBodyPart();
      text.setText(plainText);

      content.addBodyPart(html);
      content.addBodyPart(text);

      return new MultiPartDataSource(name, content);
   }

   public static UrlDataSource createFromUrl(String name, URL url, String contentType) {
      return new UrlDataSource(name, url, contentType);
   }

   public static StringDataSource createOutlookEvent(String eventName, String location, Date date, String startTime, String endTime) throws UnsupportedEncodingException {
      OutlookCalendarEvent calendarEvent = new OutlookCalendarEvent(location, eventName, date, startTime, endTime);
      String fileName = toFileName(eventName, OUTLOOK_CALENDAR_EXTENSION);

      StringDataSource dataSource = new StringDataSource(fileName, calendarEvent.getEvent()) {
         @Override
         public OutputStream getOutputStream() {
            throw new UnsupportedOperationException("OutputStream is not available for this source");
         }
      };
      dataSource.setCharset("UTF-8");
      dataSource.setContentType("text/plain");
      return dataSource;
   }

   private static String toFileName(String value, String extension) throws UnsupportedEncodingException {
      String fileName = value;
      if (fileName.endsWith(OUTLOOK_CALENDAR_EXTENSION)) {
         fileName = Lib.removeExtension(fileName);
      }
      String validName = URLEncoder.encode(fileName, "UTF-8");
      StringBuilder builder = new StringBuilder();
      builder.append(validName);
      builder.append(OUTLOOK_CALENDAR_EXTENSION);
      return builder.toString();
   }

   private static String stripHtmlTags(String html) {
      String plainText = html.replaceAll("<(.|\n)*?>", "");
      plainText = plainText.replaceAll("\t", " ");
      plainText = plainText.replaceAll("\r\n", "");
      plainText = plainText.replaceAll("  +", " ");
      return plainText;
   }
}
