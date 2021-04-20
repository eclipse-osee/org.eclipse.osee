/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.orcs.rest.internal.email;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.util.MailStatus;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.mail.api.MailMessage;
import org.eclipse.osee.mail.api.MailMessage.MailMessageBuilder;
import org.eclipse.osee.mail.api.MailService;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Dominic Guss
 */
public class SupportEmailService {

   private static MailService mailService;
   private static OrcsApi orcsApi;

   public SupportEmailService() {
      // Default constructor required. Do nothing
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      SupportEmailService.orcsApi = orcsApi;
   }

   public void setMailService(MailService mailService) {
      SupportEmailService.mailService = mailService;
   }

   public List<MailStatus> sendSupportEmail(String subject, String msg) {
      List<String> recipients = new ArrayList<String>();
      IUserGroup oseeSupportGroup = orcsApi.userService().getUserGroup(CoreUserGroups.OseeSupport);
      Collection<UserToken> members = oseeSupportGroup.getMembers();

      String email;
      for (UserToken token : members) {
         email = token.getEmail();
         if (EmailUtil.isEmailValid(email)) {
            recipients.add(email);
         }
      }

      List<MailStatus> status = null;
      if (!recipients.isEmpty()) {
         MailMessageBuilder builder = MailMessage.newBuilder();
         builder.addText(msg);
         builder.subject(subject);
         builder.replyTo(mailService.getReplyToEmail());
         builder.from(mailService.getAdminEmail());
         builder.recipients(recipients);
         MailMessage message = builder.build();
         status = mailService.sendMessages(message);
      }
      return status;
   }
}
