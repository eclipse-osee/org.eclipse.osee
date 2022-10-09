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

package org.eclipse.osee.logger.slf4j.internal;

import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

/**
 * @author Roberto E. Escobar
 */
public class OsgiSlf4jLogger {

   private LogListener listener;
   private LogReaderService logService;

   public synchronized void setLoggingService(LogReaderService logService) {
      this.logService = logService;
   }

   public synchronized void activate() {
      listener = new Slf4jLogListener();
      logService.addLogListener(listener);
   }

   public synchronized void deactivate() {
      logService.removeLogListener(listener);
      listener = null;
      logService = null;
   }
}
