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
