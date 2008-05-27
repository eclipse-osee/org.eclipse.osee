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

import java.io.Serializable;
import java.util.logging.Level;

/**
 * @author afinkbei
 */
public class BaseStatus implements IHealthStatus, Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = -3767182052813764517L;
   private Level level;
   private Throwable th;
   private String message;
   private Object[] args;
   private String formattedMessage = null;

   public BaseStatus(Level level, Throwable th, String message, Object... args) {
      this.level = level;
      this.th = th;
      this.message = message;
      this.args = args;
   }

   public BaseStatus(Level level, Throwable th, String message) {
      this.level = level;
      this.th = th;
      this.message = message;
      this.args = null;
   }

   public BaseStatus(Level level, Throwable th) {
      this(level, th, th.getMessage());
   }

   public BaseStatus(Level level, String message, Object... args) {
      this(level, null, message, args);
   }

   public BaseStatus(Level level, String message) {
      this(level, null, message);
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IStatus#getException()
    */
   @Override
   public Throwable getException() {
      return th;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IStatus#getMessage()
    */
   @Override
   public String getMessage() {
      if (formattedMessage == null) {
         if (args == null) {
            formattedMessage = message;
         } else {
            formattedMessage = String.format(message, args);
         }
      }
      return formattedMessage;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IStatus#getPlugin()
    */
   @Override
   public String getPlugin() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.logging.IOSEEStatus#getLevel()
    */
   @Override
   public Level getLevel() {
      return this.level;
   }
}
