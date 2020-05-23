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

package org.eclipse.osee.framework.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;

/**
 * @author Andrew M. Finkbeiner
 */
public class SimpleOseeHandler extends Handler {

   public SimpleOseeHandler() {
      setFormatter(new SimpleOseeFormatter());
   }

   @Override
   public void close() throws SecurityException {
      // do nothing
   }

   @Override
   public void flush() {
      // do nothing
   }

   @Override
   public void publish(LogRecord record) {
      if (isLoggable(record)) {
         if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
            XConsoleLogger.err(getFormatter().format(record));
         } else {
            XConsoleLogger.out(getFormatter().format(record));
         }
      }
   }

}
