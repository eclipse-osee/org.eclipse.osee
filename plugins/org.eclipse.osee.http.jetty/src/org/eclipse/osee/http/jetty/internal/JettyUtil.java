/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty.internal;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.http.jetty.JettyException;
import org.eclipse.osee.http.jetty.JettyLogger;

/**
 * @author Roberto E. Escobar
 */
public final class JettyUtil {

   private JettyUtil() {
      // Util Class
   }

   public static String normalizeContext(String context) {
      return context != null && !context.startsWith("/") ? "/" + context : context;
   }

   public static int getRandomPort() {
      PortUtil port = PortUtil.getInstance();
      try {
         return port.getValidPort();
      } catch (IOException ex) {
         throw JettyException.newJettyException(ex, "Error acquiring random port for JettyServer");
      }
   }

   public static boolean deleteDirectory(File directory) {
      if (directory.exists() && directory.isDirectory()) {
         File[] files = directory.listFiles();
         for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
               deleteDirectory(files[i]);
            } else {
               files[i].delete();
            }
         }
      }
      return directory.delete();
   }

   public static void checkNotNull(Object object, String objectName) {
      if (object == null) {
         throw JettyException.newJettyException("%s cannot be null", objectName);
      }
   }

   public static void checkNotNullOrEmpty(String object, String objectName) throws OseeCoreException {
      checkNotNull(object, objectName);
      if (object.length() == 0) {
         throw JettyException.newJettyException("%s cannot be empty", objectName);
      }
   }

   public static int getInt(Map<String, Object> props, String key, int defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return Strings.isNumeric(toReturn) ? Integer.parseInt(toReturn) : -1;
   }

   public static boolean getBoolean(Map<String, Object> props, String key, boolean defaultValue) {
      String toReturn = get(props, key, String.valueOf(defaultValue));
      return Boolean.parseBoolean(toReturn);
   }

   public static String get(Map<String, Object> props, String key, String defaultValue) {
      String toReturn = defaultValue;
      Object object = props != null ? props.get(key) : null;
      if (object != null) {
         toReturn = String.valueOf(object);
      }
      return toReturn;
   }

   public static JettyLogger newConsoleLogger() {
      return new JettyLogger() {

         @Override
         public void debug(String msg, Object... data) {
            if (data.length > 0) {
               System.out.printf(msg, data);
               System.out.println();
            } else {
               System.out.println(msg);
            }
         }

         @Override
         public void error(Throwable ex, String msg, Object... data) {
            error(msg, data);
            if (ex != null) {
               error(Lib.exceptionToString(ex));
            }
         }

         @Override
         public void warn(Throwable ex, String msg, Object... data) {
            error(msg, data);
            if (ex != null) {
               error(Lib.exceptionToString(ex));
            }
         }

         private void error(String msg, Object... data) {
            if (data.length > 0) {
               System.err.printf(msg, data);
               System.err.println();
            } else {
               System.err.println(msg);
            }
         }
      };
   }

   public static JettyLogger newNoopLogger() {
      return new JettyLogger() {

         @Override
         public void debug(String msg, Object... data) {
            //
         }

         @Override
         public void error(Throwable ex, String msg, Object... data) {
            //
         }

         @Override
         public void warn(Throwable ex, String msg, Object... data) {
            //
         }

      };
   }

}