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
package org.eclipse.osee.framework.ui.skynet.results.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public enum XResultBrowserHyperCmd {
   openAction,
   openArtifctBranch,
   openArtifactEditor,
   openBranch,
   openChangeReport,
   openPriorityHelp,
   browserInternal,
   browserExternal;

   public static String getHyperCmdStr(XResultBrowserHyperCmd xResultBrowserHyperCmd, String value) {
      return String.format("%s=%s", xResultBrowserHyperCmd.name(), value);
   }

   public static XResultBrowserHyperCmd getCmdStrHyperCmd(String hyperCmdStr) {
      Matcher m = Pattern.compile("(.*?)=(.*)").matcher(hyperCmdStr);
      if (m.find()) return XResultBrowserHyperCmd.getHyperCmd(m.group(1));
      return null;
   }

   public static String getCmdStrValue(String hyperCmdStr) {
      Matcher m = Pattern.compile("(.*?)=(.*)").matcher(hyperCmdStr);
      if (m.find()) return m.group(2);
      return "";
   }

   public static XResultBrowserHyperCmd getHyperCmd(String str) {
      for (XResultBrowserHyperCmd xResultBrowserHyperCmd : XResultBrowserHyperCmd.values()) {
         if (xResultBrowserHyperCmd.toString().equals(str)) return xResultBrowserHyperCmd;
      }
      throw new IllegalArgumentException("Invalid XResultBrowserHyperCmd Name");
   }
};
