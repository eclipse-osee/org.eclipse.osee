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

package org.eclipse.osee.framework.messaging.services.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeMessagingStatusImpl implements OseeMessagingStatusCallback {

   private final String failureMessage;
   private final Class<?> clazz;

   public OseeMessagingStatusImpl(String failureMessage, Class<?> clazz) {
      this.failureMessage = failureMessage;
      this.clazz = clazz;
   }

   @Override
   public void fail(Throwable th) {
      th.printStackTrace();
      OseeLog.log(clazz, Level.SEVERE, failureMessage, th);
   }

}
