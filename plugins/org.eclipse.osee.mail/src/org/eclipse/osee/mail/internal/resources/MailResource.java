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
package org.eclipse.osee.mail.internal.resources;

import java.util.List;
import org.eclipse.osee.mail.api.MailEndpoint;
import org.eclipse.osee.mail.api.MailMessage;
import org.eclipse.osee.mail.api.MailService;
import org.eclipse.osee.mail.api.MailStatus;

/**
 * @author Roberto E. Escobar
 */
public class MailResource implements MailEndpoint {

   private final MailService mailService;

   public MailResource(MailService mailService) {
      super();
      this.mailService = mailService;
   }

   @Override
   public MailStatus sendTestMail() {
      return mailService.sendTestMessage();
   }

   @Override
   public MailStatus[] sendMail(MailMessage mailMessage) {
      List<MailStatus> results = mailService.sendMessages(mailMessage);
      return results.toArray(new MailStatus[0]);
   }
}
