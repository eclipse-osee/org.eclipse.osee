/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

public final class SendEmailCall implements Callable<String> {
   private final OseeEmail emailMessage;
   private final String description;

   public SendEmailCall(OseeEmail emailMessage, String description) {
      this.emailMessage = emailMessage;
      this.description = description;
   }

   @Override
   public String call() {
      XResultData results = emailMessage.sendLocalThread();
      results.log(description);
      if (results.isErrors()) {
         if (!results.toString().contains("User unknown")) {
            XResultDataUI.report(results, description);
         } else {
            // Don't need full stack trace for User-unknown
            try {
               return String.format("User [%s] Unknown", String.valueOf(emailMessage.getAllRecipients()));
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
      return results.toString();
   }
}