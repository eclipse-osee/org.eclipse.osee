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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.event.TransportEvent;
import org.eclipse.osee.mail.SendMailStatus.MailStatus;

/**
 * @author Roberto E. Escobar
 */
public final class MailEventUtil {

   private MailEventUtil() {
      // Utility
   }

   public static void loadStatus(SendMailStatus status, TransportEvent event) {
      MailStatus eventStatus = toStatus(event);
      status.add(eventStatus);
   }

   public static MailStatus toStatus(TransportEvent event) {
      MailStatus status = new MailStatus();
      status.setVerified(true);
      Message message = event.getMessage();
      String uuid;
      try {
         String[] header = message.getHeader(MailConstants.MAIL_UUID_HEADER);
         uuid = header.length == 0 ? "-1" : header[0];
      } catch (Exception ex) {
         uuid = ex.getMessage();
      }
      status.setUuid(uuid);
      try {
         Date sentDate = message.getSentDate();
         status.setDateSent(sentDate);
      } catch (Exception ex) {
         // Do nothing
      }
      String subject;
      try {
         subject = message.getSubject();
      } catch (Exception ex) {
         subject = ex.getMessage();
      }
      status.setSubject(subject);

      try {
         addAddress(status.getFromAddress(), message.getFrom());
      } catch (Exception ex) {
         //
      }
      addAddress(status.getInvalidAddress(), event.getInvalidAddresses());
      addAddress(status.getSentAddress(), event.getValidSentAddresses());
      addAddress(status.getUnsentAddress(), event.getValidUnsentAddresses());
      return status;
   }

   private static void addAddress(Collection<String> data, Address... addresses) {
      if (addresses != null) {
         for (Address address : addresses) {
            data.add(address.toString());
         }
      }
   }

   public static Map<String, String> createTransportEventData(TransportEvent event) {
      Map<String, String> data = new HashMap<String, String>();
      Message message = event.getMessage();

      String uuid;
      try {
         String[] header = message.getHeader(MailConstants.MAIL_UUID_HEADER);
         uuid = header.length == 0 ? "-1" : header[0];
      } catch (Exception ex) {
         uuid = ex.getMessage();
      }
      data.put(MailConstants.MAIL_UUID, uuid);
      try {
         Date sentDate = message.getSentDate();
         data.put(MailConstants.MAIL_DATE_SENT, String.valueOf(sentDate.getTime()));
      } catch (Exception ex) {
         // Do nothing
      }
      String subject;
      try {
         subject = message.getSubject();
      } catch (Exception ex) {
         subject = ex.getMessage();
      }
      data.put(MailConstants.MAIL_SUBJECT, subject);

      try {
         addAddress(data, MailConstants.MAIL_FROM_ADDRESS, message.getFrom());
      } catch (Exception ex) {
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
