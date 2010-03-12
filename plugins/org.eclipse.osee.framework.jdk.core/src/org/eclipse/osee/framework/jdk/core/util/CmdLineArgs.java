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
package org.eclipse.osee.framework.jdk.core.util;

import java.util.HashMap;

public class CmdLineArgs {

   private HashMap<String, String> cmdArgs;

   public CmdLineArgs(String[] args) {
      cmdArgs = new HashMap<String, String>();

      for (int i = 0; i < args.length; i++) {
         if (args[i].matches("-\\w.*")) {
            if ((i + 1 < args.length) && (!args[i + 1].matches("-\\D.*"))) {
               cmdArgs.put(args[i], args[i + 1]);
               i++;
            } else {
               cmdArgs.put(args[i], null);
            }
         } else {
            cmdArgs.put(args[i], null);
         }
      }
   }

   public String get(String key) {
      return cmdArgs.get(key);
   }

   public HashMap<String, String> getArgs() {
      return cmdArgs;
   }
}
