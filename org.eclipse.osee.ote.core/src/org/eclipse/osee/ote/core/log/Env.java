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
package org.eclipse.osee.ote.core.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 */
public class Env extends Logger {

   private static Env instance = null;

   public static Env getInstance() {
      if (instance == null)
         instance = new Env(null, Level.ALL);  // FileHandler was giving problems sometimes.
      return instance;
   }

   public static Env getInstance(Class<?> newClass, Level level) {
      if (instance == null)
         instance = new Env(newClass, level);
      return instance;
   }

   /**
    * @param handlerType
    * @param level
    */
   private Env(Class<?> handlerType, Level level) {
      super("osee.test.core.log.EnvServiceLogger", null);
      Handler handler;
      if (handlerType == FileHandler.class) {
         try {
            handler = new FileHandler("TestEnvLog_" + this.hashCode() + "%g.log");
         }
         catch (Exception e) {
        	 OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
            handler = new ConsoleHandler();
         }
      }
      else {
         handler = new ConsoleHandler();
      }
      handler.setLevel(level);
      this.setLevel(level);
      this.addHandler(handler);
   }

   public void debug(String message) {
      try {
         log(Level.FINE, message);
      }
      catch (Exception e) {
    	  OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
      }
   }

   public void exception(Throwable ex) {
      try {
         throwing("", "", ex);
      }
      catch (Exception e) {
    	  OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
      }
   }

   public void message(String message) {
      try {
         log(Level.INFO, message);
      }
      catch (Exception e) {
    	  OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
      }
   }

   public void close() {
      Handler[] handlers = Env.instance.getHandlers();
      for (int i = 0; i < handlers.length; i++) {
         handlers[i].close();
      }
   }
}
