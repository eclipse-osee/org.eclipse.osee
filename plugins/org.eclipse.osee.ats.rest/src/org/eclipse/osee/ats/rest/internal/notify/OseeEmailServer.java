/*
 * Created on Dec 16, 2021
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.rest.internal.notify;

import java.util.Collection;
import org.eclipse.osee.framework.core.util.OseeEmail;

public class OseeEmailServer extends OseeEmail {

   public OseeEmailServer() {
   }

   public OseeEmailServer(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType) {
      super(toAddresses, fromAddress, replyToAddress, subject, body, bodyType);
   }

   public OseeEmailServer(String fromEmail, String toAddress, String subject, String body, BodyType bodyType) {
      super(fromEmail, toAddress, subject, body, bodyType);
   }

   @Override
   public void setClassLoader() {
      Thread.currentThread().setContextClassLoader(new ExportClassLoader(ServiceUtil.getPackageAdmin()));
   }

   public static void emailHtml(String fromEmail, Collection<String> emails, String subject, String htmlBody) {
      OseeEmail emailMessage = new OseeEmailServer(emails, fromEmail, fromEmail, subject, htmlBody, BodyType.Html);
      emailMessage.send();
   }

}
