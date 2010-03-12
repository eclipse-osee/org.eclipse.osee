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

import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Andrew M. Finkbeiner
 */
public class BaseStatus implements IHealthStatus, Serializable {
   private static final long serialVersionUID = -3767182052813764517L;
   private final Level level;
   private final Throwable th;
   private final String message;
   private final Object[] args;
   private final String sourceName;

   public BaseStatus(String sourceName, Level level, Throwable th, String message, Object... args) {
      this.sourceName = sourceName;
      this.level = level;
      this.th = th;
      this.message = message;
      this.args = args;
   }

   public BaseStatus(String sourceName, Level level, String message, Object... args) {
      this(sourceName, level, null, message, args);
   }

   public BaseStatus(String sourceName, Level level, Throwable th) {
      this(sourceName, level, th, th.getMessage(), (Object[]) null);
   }

   @Override
   public Throwable getException() {
      return th;
   }

   @Override
   public String getMessage() {
      String toReturn = null;
      if (message != null && args != null) {
         toReturn = String.format(message, args);
      } else if (message != null) {
         toReturn = message;
      } else {
         toReturn = "Unavailable";
      }
      return toReturn;
   }

   @Override
   public Level getLevel() {
      return this.level;
   }

   @Override
   public String getSourceName() {
      return sourceName;
   }

   @Override
   public boolean isOk() {
      return Level.INFO.intValue() >= getLevel().intValue();
   }
   
   public String toString(){
	   StringBuilder sb = new StringBuilder();
	   if(message != null){
	      sb.append(message);
	   	  sb.append("\n");
	   }
	   sb.append(Lib.exceptionToString(th));
	   return sb.toString();   
   }
}
