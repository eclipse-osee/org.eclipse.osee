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

/**
 * @author Roberto E. Escobar
 */
public class StandAloneApplicationFormatter extends ExecutionCommandFormatter {
   private final String baseExecString = "nohup" + ServiceItem.EXEC_SEPARATOR + "#JAVA#";
   private String executionString;

   public StandAloneApplicationFormatter(String executionString) {
      this.executionString = executionString;
   }

   @Override
   protected String buildExecString() {
      String toReturn = baseExecString.replace("#JAVA#", "java");
      toReturn +=
            this.getJvmArgsString() + ServiceItem.EXEC_SEPARATOR + executionString + this.getApplicationArgsString();
      return toReturn;
   }

   @Override
   public void setServiceExecutionString(ServiceItem item) {
      item.setStandAloneExecution(buildExecString());
   }

   @Override
   public void setRemoteAllowed(ServiceItem item) {
      item.setRemoteExecution(buildExecString());
   }
}
