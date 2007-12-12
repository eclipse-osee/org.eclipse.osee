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
package org.eclipse.osee.framework.ui.service.control.wizards.launcher.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;

/**
 * @author Roberto E. Escobar
 */
abstract class ExecutionCommandFormatter {
   protected List<String> jvmArgs;
   protected List<String> applicationArgs;

   public ExecutionCommandFormatter() {
      jvmArgs = new ArrayList<String>();
      applicationArgs = new ArrayList<String>();
   }

   public void addJvmArg(String value) {
      jvmArgs.add(value);
   }

   public void addApplicationArgs(String value) {
      applicationArgs.add(value);
   }

   public String getApplicationArgsString() {
      String applicationArgsString = "";
      if (applicationArgs.size() > 0) {
         applicationArgsString =
               ServiceItem.EXEC_SEPARATOR + StringFormat.listToValueSeparatedString(applicationArgs,
                     ServiceItem.EXEC_SEPARATOR);
      }
      return applicationArgsString;
   }

   public String getJvmArgsString() {
      String jvmArgsString = "";
      if (jvmArgs.size() > 0) {
         jvmArgsString =
               ServiceItem.EXEC_SEPARATOR + StringFormat.listToValueSeparatedString(jvmArgs, ServiceItem.EXEC_SEPARATOR);
      }
      return jvmArgsString;
   }

   protected abstract String buildExecString();

   public abstract void setServiceExecutionString(ServiceItem item);

   public abstract void setRemoteAllowed(ServiceItem item);
}
