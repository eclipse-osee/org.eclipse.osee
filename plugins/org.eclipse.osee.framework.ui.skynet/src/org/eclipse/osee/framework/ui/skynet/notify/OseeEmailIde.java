/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.notify;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;

public class OseeEmailIde extends OseeEmail {

   private OseeEmailIde() {
   }

   private OseeEmailIde(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType, Collection<String> emailAdressAbridged, String subjectAbridged, String bodyAbridged) {
      super(toAddresses, fromAddress, replyToAddress, subject, body, bodyType, emailAdressAbridged, subjectAbridged,
         bodyAbridged);
   }

   private OseeEmailIde(String toAddress, String fromEmail, String subject, String body, BodyType bodyType, String emailAddressAbridged, String subjectAbridged, String bodyAbridged) {
      super(fromEmail, toAddress, subject, body, bodyType, emailAddressAbridged, subjectAbridged, bodyAbridged);
   }

   @Override
   public void setClassLoader() {
      Thread.currentThread().setContextClassLoader(new ExportClassLoader(ServiceUtil.getPackageAdmin()));
   }

   public static void emailHtml(String fromEmail, Collection<String> toAddresses, String subject, String htmlBody,
      Collection<String> emailAdressAbridged, String subjectAbridged, String bodyAbridged) {
      // TODO : Ensure that abridged emails are addressed when this method is actually utilized.
      OseeEmail emailMessage = new OseeEmailIde(toAddresses, fromEmail, fromEmail, subject, htmlBody, BodyType.Html,
         emailAdressAbridged, subjectAbridged, bodyAbridged);
      emailMessage.send();
   }

   public static OseeEmailIde create() {
      loadDefaultMailServer();
      return new OseeEmailIde();
   }

   public static OseeEmailIde create(Collection<String> toAddresses, String fromAddress, String replyToAddress,
      String subject, String body, BodyType bodyType, Collection<String> emailAddressAbridged, String subjectAbridged,
      String bodyAbridged) {
      loadDefaultMailServer();
      return new OseeEmailIde(toAddresses, fromAddress, replyToAddress, subject, body, bodyType, emailAddressAbridged,
         subjectAbridged, bodyAbridged);
   }

   public static OseeEmailIde create(String fromEmail, String toAddress, String subject, String body, BodyType bodyType,
      String emailAddressAbridged, String subjectAbridged, String bodyAbridged) {
      loadDefaultMailServer();
      return new OseeEmailIde(toAddress, fromEmail, subject, body, bodyType, emailAddressAbridged, subjectAbridged,
         bodyAbridged);
   }

   private static void loadDefaultMailServer() {
      if (defaultMailServer == null) {
         defaultMailServer = OseeSystemArtifacts.getGlobalPreferenceArtifact().getSoleAttributeValue(
            CoreAttributeTypes.DefaultMailServer, "");
      }
   }

   @Override
   protected OseeEmail createAbridgedEmail(OseeEmail email) {
      // create OseeEmail from abridged emails, subject, body to main so it can be sent
      return OseeEmailIde.create(email.getEmailAddressesAbridged(), email.getFromAddress(), email.getReplyToAddress(),
         email.getSubjectAbridged(), email.getBodyAbridged(), email.getBodyType(), Collections.emptyList(), "", "");
   }

}
