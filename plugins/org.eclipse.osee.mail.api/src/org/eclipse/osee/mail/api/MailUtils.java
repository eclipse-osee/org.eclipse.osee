/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.mail.api;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.windows.OutlookCalendarEvent;
import org.eclipse.osee.mail.api.internal.MultiPartDataSource;
import org.eclipse.osee.mail.api.internal.StringDataSource;
import org.eclipse.osee.mail.api.internal.UrlDataSource;

/**
 * @author Roberto E. Escobar
 */
public final class MailUtils {

   private static final String OUTLOOK_CALENDAR_EXTENSION = ".vcs";

   private MailUtils() {
      // Utility Class
   }

   public static boolean isValidEmail(String email) {
      boolean result = false;
      try {
         InternetAddress[] addresses = InternetAddress.parse(email, true);
         if (addresses != null && addresses.length > 0) {
            result = true;
         }
      } catch (AddressException ex) {
         // Do Nothing
      }
      return result;
   }

   public static DataSource createFromString(String name, String message, Object... args) {
      String data;
      if (args.length > 0) {
         data = String.format(message, args);
      } else {
         data = message;
      }
      StringDataSource dataSource = new StringDataSource(name, data);
      dataSource.setCharset("UTF-8");
      dataSource.setContentType(MediaType.TEXT_PLAIN);
      return dataSource;
   }

   public static DataSource createFromHtml(final String name, String htmlData) {
      String plainText = stripHtmlTags(htmlData);
      return createAlternativeDataSource(name, htmlData, plainText);
   }

   public static DataSource createAlternativeDataSource(String name, String htmlText, String plainText) {
      final MimeMultipart content = new MimeMultipart("alternative");

      MimeBodyPart html = new MimeBodyPart();
      MimeBodyPart text = new MimeBodyPart();

      try {
         html.setContent(htmlText, MediaType.TEXT_HTML);
      } catch (MessagingException ex) {
         throw new OseeCoreException(ex, "Error adding HTML content");
      }
      try {
         text.setText(plainText, "UTF-8");
      } catch (MessagingException ex) {
         throw new OseeCoreException(ex, "Error adding Text content");
      }
      try {
         content.addBodyPart(html);
      } catch (MessagingException ex) {
         throw new OseeCoreException(ex, "Error adding HTML body part");
      }
      try {
         content.addBodyPart(text);
      } catch (MessagingException ex) {
         throw new OseeCoreException(ex, "Error adding test body part");
      }
      return new MultiPartDataSource(name, content);
   }

   public static DataSource createFromUrl(String name, URL url, MediaType mediaType) {
      return new UrlDataSource(name, url, mediaType.toString());
   }

   public static DataSource createOutlookEvent(String eventName, String location, Date startDate, Date endDate) {
      OutlookCalendarEvent calendarEvent = new OutlookCalendarEvent(location, eventName, startDate, endDate);
      String fileName = toFileName(eventName, OUTLOOK_CALENDAR_EXTENSION);

      StringDataSource dataSource = new StringDataSource(fileName, calendarEvent.getEvent()) {
         @Override
         public OutputStream getOutputStream() {
            throw new UnsupportedOperationException("OutputStream is not available for this source");
         }
      };
      dataSource.setCharset("UTF-8");
      dataSource.setContentType(MediaType.TEXT_PLAIN);
      return dataSource;
   }

   private static String toFileName(String value, String extension) {
      String fileName = value;
      if (fileName.endsWith(OUTLOOK_CALENDAR_EXTENSION)) {
         fileName = Lib.removeExtension(fileName);
      }
      String validName;
      try {
         validName = URLEncoder.encode(fileName, "UTF-8");
      } catch (UnsupportedEncodingException ex) {
         throw new OseeCoreException(ex, "Error encoding filename");
      }
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
