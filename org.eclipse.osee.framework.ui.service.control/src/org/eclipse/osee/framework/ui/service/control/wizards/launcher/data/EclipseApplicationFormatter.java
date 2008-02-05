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

import java.io.File;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;

/**
 * @author Roberto E. Escobar
 */
public class EclipseApplicationFormatter extends ExecutionCommandFormatter {
   // private final String baseExecString = StringFormat.separateWith(new String[] {"#JAVA#",
   // "#JVM_ARGS#", "-cp",
   // "#ECLIPSE_INSTALL_HOME#" + (Lib.isWindows() ? "\\eclipse.exe" : "/eclipse"),
   // "org.eclipse.core.launcher.Main", "-application", "#APPLICATION_NAME#", "#APPLICATION_ARGS#"},
   // EXEC_SEPARATOR);
   private final String baseExecString =
         StringFormat.separateWith(new String[] {"#ECLIPSE_INSTALL_HOME#", "-nosplash", "-application",
               "#APPLICATION_NAME#", "#APPLICATION_ARGS#", "-vmargs", "#JVM_ARGS#"}, ServiceItem.EXEC_SEPARATOR);

   private String applicationName;

   public EclipseApplicationFormatter(String applicationName) {
      this.applicationName = applicationName;
   }

   protected String buildExecString() {
      String toReturn = baseExecString;
      toReturn = toReturn.replaceAll("#JAVA#", "java");
      String localLocation = null;
      if (Platform.isRunning()) {
         localLocation = (new File(Platform.getInstallLocation().getURL().getFile())).getAbsolutePath();
      } else {
         throw new IllegalStateException("Platform needs to be running");
      }
      if (Lib.isWindows()) {
         localLocation = "\"" + localLocation + "\\eclipse.exe" + "\"";
      } else {
         localLocation += "/eclipse";
      }
      toReturn = toReturn.replace("#ECLIPSE_INSTALL_HOME#", localLocation);
      toReturn = toReturn.replace("#APPLICATION_NAME#", applicationName);
      toReturn = toReturn.replace("#JVM_ARGS#", this.getJvmArgsString());
      toReturn = toReturn.replace("#APPLICATION_ARGS#", this.getApplicationArgsString());
      return toReturn;
   }

   @Override
   public void setServiceExecutionString(ServiceItem item) {
      item.setEclipseAppExecution(buildExecString());
   }

   @Override
   public void setRemoteAllowed(ServiceItem item) {
      item.setRemoteExecution(buildExecString());
   }
}
