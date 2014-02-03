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
package org.eclipse.osee.framework.core.client.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;

public abstract class OseeServer {

   private final String name;
   private IHealthStatus status;

   public OseeServer(String serverName) {
      this.name = serverName;
   }

   public void set(Level level, Exception ex, String message, Object... args) {
      Level myLevel = level;
      if (myLevel == null) {
         myLevel = Level.INFO;
      }
      status = new BaseStatus(getName(), myLevel, ex, message, args);
   }

   public String getName() {
      return name;
   }

   public void report() {
      if (status == null) {
         status = new BaseStatus(name, Level.INFO, null, (Throwable) null);
      }
      OseeLog.reportStatus(new BaseStatus(name, status.getLevel(), status.getException(), "%s: %s %s",
         status.getLevel(), status.getMessage(),
         (status.getException() != null ? "[" + status.getException().getLocalizedMessage() + "]" : "")));
      OseeLog.log(OseeApplicationServer.class, status.getLevel(), status.getMessage(), status.getException());
   }

}
