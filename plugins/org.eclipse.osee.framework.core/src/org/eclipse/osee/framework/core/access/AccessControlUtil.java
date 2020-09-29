/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.access.context.AccessContext;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AccessControlUtil {

   private static boolean debugOn = false;
   private static Boolean logDebugOn = null;

   private AccessControlUtil() {
      // Utility Class
   }

   public static Collection<AccessContext> getContexts(Collection<AccessContextToken> contextIds, XResultData rd) {
      List<AccessContext> contexts = new ArrayList<>();
      for (AccessContextToken token : contextIds) {
         AccessContext accessContext = AccessContext.getAccessContext(token);
         if (accessContext == null) {
            OseeLog.logf(AccessControlUtil.class, Level.SEVERE,
               "Context Id: No Access Context can be found for Context Id [%s]", token.toStringWithId());
            rd.errorf("Context Id: No Access Context can be found for Context Id [%s]", token.toStringWithId());
         } else {
            contexts.add(accessContext);
         }
      }
      return contexts;
   }

   public static void errorf(String message, Object... data) {
      if (logDebugOn == null) {
         logDebugOn = "true".equals(System.getProperty("access.debug"));
      }
      if (debugOn || logDebugOn) {
         XConsoleLogger.err(message, data);
      }
      if (logDebugOn) {
         OseeLog.log(AccessControlUtil.class, Level.INFO, String.format(message, data));
      }
   }

   public static void setDebugOn(boolean debugOn) {
      logDebugOn = debugOn;
   }

   public static boolean isDebugOn() {
      return logDebugOn;
   }

}
