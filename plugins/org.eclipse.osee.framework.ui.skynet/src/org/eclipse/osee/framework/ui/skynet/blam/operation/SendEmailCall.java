/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
      XResultDataUI.report(results, description);
      return results.toString();
   }
}