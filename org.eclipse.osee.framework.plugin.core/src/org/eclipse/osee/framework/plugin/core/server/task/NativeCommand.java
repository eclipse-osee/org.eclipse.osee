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
package org.eclipse.osee.framework.plugin.core.server.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * @author Ryan D. Brooks
 */
public class NativeCommand extends Command {
   public static final int NATIVE_CMD_ID = 0;

   public NativeCommand() {
      super(NATIVE_CMD_ID);
   }

   public void sendNativeCommand(ObjectOutputStream toServer, String[] callAndArgs) throws IOException {
      Object[] params = new Object[callAndArgs.length];
      System.arraycopy(callAndArgs, 0, params, 0, params.length);
      sendCommand(toServer, params);
   }

   /* (non-Javadoc)
    * @see osee.plugin.core.server.task.Command#invoke(java.lang.Object...)
    */
   public Object invoke(Object... parameters) throws IOException {
      String[] callAndArgs = new String[parameters.length];
      System.arraycopy(parameters, 0, callAndArgs, 0, parameters.length);

      Process process = Runtime.getRuntime().exec(callAndArgs);
      BufferedReader inError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      BufferedReader inOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

      ArrayList<String> lines = new ArrayList<String>();
      String line = null;
      while ((line = inOutput.readLine()) != null) {
         lines.add(line);
      }
      inOutput.close();

      while ((line = inError.readLine()) != null) {
         lines.add(line);
      }
      inError.close();
      return lines.toArray(new String[lines.size()]);
   }
}
