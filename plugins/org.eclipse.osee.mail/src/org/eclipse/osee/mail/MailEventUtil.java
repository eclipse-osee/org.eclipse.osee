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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.TransportEvent;

/**
 * @author Roberto E. Escobar
 */
public final class MailEventUtil {

   private MailEventUtil() {
      // Utility
   }

   public static Map<String, String> createTransportEventData(TransportEvent event) {
      Map<String, String> data = new HashMap<String, String>();
      Message message = event.getMessage();

      String uuid;
      try {
         String[] header = message.getHeader(MailConstants.MAIL_UUID_HEADER);
         uuid = header.length == 0 ? "-1" : header[0];
      } catch (MessagingException ex) {
         uuid = ex.getMessage();
      }
      data.put(MailConstants.MAIL_UUID, uuid);
      try {
         Date sentDate = message.getSentDate();
         data.put(MailConstants.MAIL_DATE_SENT, String.valueOf(sentDate.getTime()));
      } catch (MessagingException ex) {
         // Do nothing
      }
      String subject;
      try {
         subject = message.getSubject();
      } catch (MessagingException ex) {
         subject = ex.getMessage();
      }
      data.put(MailConstants.MAIL_SUBJECT, subject);

      try {
         addAddress(data, MailConstants.MAIL_FROM_ADDRESS, message.getFrom());
      } catch (MessagingException ex) {
         data.put(MailConstants.MAIL_FROM_ADDRESS, ex.getMessage());
      }
      addAddress(data, MailConstants.MAIL_INVALID_ADDRESS, event.getInvalidAddresses());
      addAddress(data, MailConstants.MAIL_VALID_SENT_ADDRESS, event.getValidSentAddresses());
      addAddress(data, MailConstants.MAIL_VALID_UNSENT_ADDRESS, event.getValidUnsentAddresses());
      return data;
   }

   private static void addAddress(Map<String, String> data, String baseKey, Address... addresses) {
      int size = addresses != null ? addresses.length : 0;
      data.put(baseKey + ".count", String.valueOf(size));
      for (int index = 0; index < size; index++) {
         Address address = addresses[index];
         data.put(baseKey + "." + index, address.toString());
      }
   }
}
