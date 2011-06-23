/*
 * Created on Jun 3, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.mail;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.event.TransportEvent;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.junit.Assert;

/**
 * Test unit for {@link MailEventUtil}
 * 
 * @author Roberto E. Escobar
 */
public class MailEventUtilTest {

   private static final String ADDRESS1 = "hello@hello.com";
   private static final String ADDRESS2 = "goodbye@goodbye.com";
   private static final String ADDRESS3 = "dummy@inbox.com";
   private static final String ADDRESS4 = "another@x.com";
   private static final String ADDRESS5 = "another@asda.com";
   private static final String ADDRESS6 = "anor@asd.com";
   private static final String ADDRESS7 = "anoasdadaasdrf@asdfas.com";
   private static final String ADDRESS8 = "s.d.f.a.s.dfasdf@com";

   @org.junit.Test
   public void testCreateTransportEventData() throws MessagingException {
      Address[] from = toAddress(ADDRESS1);
      Address[] sent = toAddress(ADDRESS2, ADDRESS3, ADDRESS8);
      Address[] unsent = toAddress(ADDRESS4, ADDRESS5);
      Address[] invalid = toAddress(ADDRESS6, ADDRESS7);

      String uuid = "123114135";
      Date sendDate = new Date(12345);
      String subject = "dummy subject";

      Session session = Session.getDefaultInstance(new Properties());

      MimeMessage message = new MimeMessage(session);
      message.setHeader(MailConstants.MAIL_UUID_HEADER, uuid);
      message.setSentDate(sendDate);
      message.setSubject(subject);
      message.setFrom(from[0]);

      TransportEvent event = createEvent(session, message, sent, unsent, invalid);

      Map<String, String> data = MailEventUtil.createTransportEventData(event);
      assertEventData(data, uuid, message.getSentDate(), subject, from, sent, unsent, invalid);
   }

   private static void assertEventData(Map<String, String> data, String uuid, Date sendDate, String subject, Address[] from, Address[] validSent, Address[] validUnsent, Address[] invalid) {
      Assert.assertEquals(uuid, data.get(MailConstants.MAIL_UUID));
      Assert.assertEquals(subject, data.get(MailConstants.MAIL_SUBJECT));

      String actualDate = data.get(MailConstants.MAIL_DATE_SENT);
      if (sendDate != null) {
         Assert.assertEquals(sendDate.getTime(), Long.parseLong(actualDate));
      } else {
         Assert.assertNull(actualDate);
      }

      assertAddress(data, MailConstants.MAIL_FROM_ADDRESS, from);
      assertAddress(data, MailConstants.MAIL_INVALID_ADDRESS, invalid);
      assertAddress(data, MailConstants.MAIL_VALID_SENT_ADDRESS, validSent);
      assertAddress(data, MailConstants.MAIL_VALID_UNSENT_ADDRESS, validUnsent);
   }

   private static void assertAddress(Map<String, String> data, String key, Address[] addresses) {
      Assert.assertEquals(addresses.length, Integer.parseInt(data.get(key + ".count")));
      for (int index = 0; index < addresses.length; index++) {
         Address address = addresses[index];
         Assert.assertEquals(address.toString(), data.get(key + "." + index));
      }
   }

   private static TransportEvent createEvent(Session session, MimeMessage message, Address[] sent, Address[] unsent, Address[] invalid) {
      Transport transport = new Transport(session, new URLName("http://hello.com")) {

         @Override
         public void sendMessage(Message arg0, Address[] arg1) {
            // Do nothing
         }
      };
      return new TransportEvent(transport, 1, sent, unsent, invalid, message);
   }

   private static Address[] toAddress(String... rawAddresses) throws AddressException {
      Address[] toReturn = new Address[rawAddresses.length];
      int index = 0;
      for (String rawAddress : rawAddresses) {
         toReturn[index++] = asAddress(rawAddress);
      }
      return toReturn;
   }

   private static Address asAddress(String value) throws AddressException {
      return new InternetAddress(value);
   }
}
