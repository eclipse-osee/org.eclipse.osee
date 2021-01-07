/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.mail.api;

import java.util.List;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.core.util.MailStatus;

/**
 * @author Roberto E. Escobar
 */
public interface MailService {

   MailStatus sendTestMessage();

   String getAdminEmail();

   String getReplyToEmail();

   Future<MailStatus> sendAsyncTestMessage();

   List<MailStatus> sendMessages(MailMessage... email);

   List<MailStatus> sendMessages(Iterable<MailMessage> email);

   List<Future<MailStatus>> sendAsyncMessages(Iterable<MailMessage> emails);

   List<Future<MailStatus>> sendAsyncMessages(MailMessage... emails);
}