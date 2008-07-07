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
package org.eclipse.osee.framework.servlet.log;

import java.util.logging.Level;

public class Logger implements ILogger {

   public void log(Level level, String message) {
      System.out.println(message);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.servlet.log.ILogger#log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
    */
   @Override
   public void log(Level level, String message, Throwable ex) {
      System.out.println(message);
   }

}
