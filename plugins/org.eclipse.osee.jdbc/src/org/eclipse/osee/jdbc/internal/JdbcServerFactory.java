/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jdbc.internal;

import java.lang.reflect.Constructor;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcException;
import org.eclipse.osee.jdbc.JdbcLogger;
import org.eclipse.osee.jdbc.JdbcServer;
import org.eclipse.osee.jdbc.JdbcServerConfig;

/**
 * @author Roberto E. Escobar
 */
public final class JdbcServerFactory {

   private JdbcServerFactory() {
      //
   }

   @SuppressWarnings("unchecked")
   private static Class<JdbcServer> getJdbcServerImplClass(String serverClassName) throws ClassNotFoundException {
      return (Class<JdbcServer>) Class.forName(serverClassName);
   }

   public static JdbcServer newJbdcServer(JdbcServerConfig config, boolean loggingEnabled, JdbcLogger logger) {
      if (loggingEnabled && logger == null) {
         logger = newConsoleLogger();
      }

      String serverClassName = config.getServerImplClassName();
      try {
         Class<JdbcServer> clazz = getJdbcServerImplClass(serverClassName);
         Constructor<JdbcServer> constructor = clazz.getConstructor(JdbcLogger.class, JdbcServerConfig.class);
         return constructor.newInstance(logger, config);
      } catch (ClassNotFoundException ex) {
         throw JdbcException.newJdbcException(ex, "Unable to find class [%s]", serverClassName);
      } catch (Exception ex) {
         throw JdbcException.newJdbcException(ex, "Error instantiating class [%s]", serverClassName);
      }
   }

   public static JdbcLogger newConsoleLogger() {
      return new JdbcLogger() {

         @Override
         public void info(String msg, Object... data) {
            if (data.length > 0) {
               System.out.printf(msg, data);
               System.out.println();
            } else {
               System.out.println(msg);
            }
         }

         @Override
         public void error(String msg, Object... data) {
            if (data.length > 0) {
               XConsoleLogger.err(msg + "\n", data);
            } else {
               XConsoleLogger.err(msg);
            }
         }

         @Override
         public void debug(String msg, Object... data) {
            info(msg, data);
         }

         @Override
         public void error(Throwable ex, String msg, Object... data) {
            error(msg, data);
            if (ex != null) {
               error(Lib.exceptionToString(ex));
            }
         }
      };
   }

}