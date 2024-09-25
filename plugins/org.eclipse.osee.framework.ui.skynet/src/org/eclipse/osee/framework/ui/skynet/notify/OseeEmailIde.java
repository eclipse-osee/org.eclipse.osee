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

   private OseeEmailIde(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType, Collection<String> toAbridgedAddresses, String abridgedSubject) {
      super(toAddresses, fromAddress, replyToAddress, subject, body, bodyType, toAbridgedAddresses, abridgedSubject);
   }

   private OseeEmailIde(String toAddress, String fromEmail, String subject, String body, BodyType bodyType, String toAbridgedAddress, String abridgedSubject) {
      super(fromEmail, toAddress, subject, body, bodyType, toAbridgedAddress, abridgedSubject);
   }

   @Override
   public void setClassLoader() {
      Thread.currentThread().setContextClassLoader(new ExportClassLoader(ServiceUtil.getPackageAdmin()));
   }

   public static void emailHtml(String fromEmail, Collection<String> toAddresses, String subject, String htmlBody,
      Collection<String> toAbridgedAddresses, String abridgedSubject) {
      // TODO : Ensure that abridged emails are addressed when this method is actually utilized.
      OseeEmail emailMessage = new OseeEmailIde(toAddresses, fromEmail, fromEmail, subject, htmlBody, BodyType.Html,
         toAbridgedAddresses, abridgedSubject);
      emailMessage.send();
   }

   public static OseeEmailIde create() {
      loadDefaultMailServer();
      return new OseeEmailIde();
   }

   public static OseeEmailIde create(Collection<String> toAddresses, String fromAddress, String replyToAddress,
      String subject, String body, BodyType bodyType, Collection<String> toAbridgedAddresses, String abridgedSubject) {
      loadDefaultMailServer();
      return new OseeEmailIde(toAddresses, fromAddress, replyToAddress, subject, body, bodyType, toAbridgedAddresses,
         abridgedSubject);
   }

   public static OseeEmailIde create(String fromEmail, String toAddress, String subject, String body, BodyType bodyType,
      String toAbridgedAddress, String abridgedSubject) {
      loadDefaultMailServer();
      return new OseeEmailIde(toAddress, fromEmail, subject, body, bodyType, toAbridgedAddress, abridgedSubject);
   }

   private static void loadDefaultMailServer() {
      if (defaultMailServer == null) {
         defaultMailServer = OseeSystemArtifacts.getGlobalPreferenceArtifact().getSoleAttributeValue(
            CoreAttributeTypes.DefaultMailServer, "");
      }
   }

   @Override
   protected OseeEmail createAbridgedEmail(OseeEmail email) {
      return OseeEmailIde.create(email.getToAbridgedAddresses(), email.getFromAddress(), email.getReplyToAddress(),
         email.getAbridgedSubject(), getAbridgedBodyText(), email.getBodyType(), Collections.emptyList(), "");
   }

}
