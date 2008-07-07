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
package org.eclipse.osee.framework.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Andrew M. Finkbeiner
 */
public class SimpleOseeHandler extends Handler {

   public SimpleOseeHandler() {
      setFormatter(new SimpleOseeFormatter());
   }

   @Override
   public void close() throws SecurityException {
      // TODO Auto-generated method stub
   }

   @Override
   public void flush() {
      // TODO Auto-generated method stub
   }

   @Override
   public void publish(LogRecord record) {
      if (isLoggable(record)) {
         if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
            System.err.println(getFormatter().format(record));
         } else {
            System.out.println(getFormatter().format(record));
         }
      }
   }

}
