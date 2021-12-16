/*
 * Created on Dec 16, 2021
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.notify;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;

public class OseeEmailIde extends OseeEmail {

   private OseeEmailIde() {
   }

   private OseeEmailIde(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType) {
      super(toAddresses, fromAddress, replyToAddress, subject, body, bodyType);
   }

   private OseeEmailIde(String fromEmail, String toAddress, String subject, String body, BodyType bodyType) {
      super(fromEmail, toAddress, subject, body, bodyType);
   }

   @Override
   public void setClassLoader() {
      Thread.currentThread().setContextClassLoader(new ExportClassLoader(ServiceUtil.getPackageAdmin()));
   }

   public static void emailHtml(String fromEmail, Collection<String> emails, String subject, String htmlBody) {
      OseeEmail emailMessage = new OseeEmailIde(emails, fromEmail, fromEmail, subject, htmlBody, BodyType.Html);
      emailMessage.send();
   }

   public static OseeEmailIde create() {
      loadDefaultMailServer();
      return new OseeEmailIde();
   }

   public static OseeEmailIde create(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType) {
      loadDefaultMailServer();
      return new OseeEmailIde(toAddresses, fromAddress, replyToAddress, subject, body, bodyType);
   }

   public static OseeEmailIde create(String fromEmail, String toAddress, String subject, String body, BodyType bodyType) {
      loadDefaultMailServer();
      return new OseeEmailIde(fromEmail, toAddress, subject, body, bodyType);
   }

   private static void loadDefaultMailServer() {
      if (defaultMailServer == null) {
         defaultMailServer = OseeSystemArtifacts.getGlobalPreferenceArtifact().getSoleAttributeValue(
            CoreAttributeTypes.DefaultMailServer, "");
      }
   }

}
