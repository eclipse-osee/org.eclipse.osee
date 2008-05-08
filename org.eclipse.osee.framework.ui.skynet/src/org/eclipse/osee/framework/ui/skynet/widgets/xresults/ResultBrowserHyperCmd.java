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
package org.eclipse.osee.framework.ui.skynet.widgets.xresults;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public enum ResultBrowserHyperCmd {
   openAction,
   openArtifctBranch,
   openArtifactEditor,
   openArtifactHyperViewer,
   openBranch,
   openChangeReport,
   openPriorityHelp;

   public static String getHyperCmdStr(ResultBrowserHyperCmd resultBrowserHyperCmd, String value) {
      return String.format("%s=%s", resultBrowserHyperCmd.name(), value);
   }

   public static ResultBrowserHyperCmd getCmdStrHyperCmd(String hyperCmdStr) {
      Matcher m = Pattern.compile("(.*?)=(.*)").matcher(hyperCmdStr);
      if (m.find()) return ResultBrowserHyperCmd.getHyperCmd(m.group(1));
      return null;
   }

   public static String getCmdStrValue(String hyperCmdStr) {
      Matcher m = Pattern.compile("(.*?)=(.*)").matcher(hyperCmdStr);
      if (m.find()) return (String) m.group(2);
      return "";
   }

   public static ResultBrowserHyperCmd getHyperCmd(String str) {
      for (ResultBrowserHyperCmd resultBrowserHyperCmd : ResultBrowserHyperCmd.values()) {
         if (resultBrowserHyperCmd.toString().equals(str)) return resultBrowserHyperCmd;
      }
      throw new IllegalArgumentException("Invalid ResultBrowserHyperCmd Name");
   }
};
