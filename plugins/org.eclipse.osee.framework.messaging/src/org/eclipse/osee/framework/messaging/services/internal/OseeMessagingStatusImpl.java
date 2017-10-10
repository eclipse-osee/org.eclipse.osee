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
