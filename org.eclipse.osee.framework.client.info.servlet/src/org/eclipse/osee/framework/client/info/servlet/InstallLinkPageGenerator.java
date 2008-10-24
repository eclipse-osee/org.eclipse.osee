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
package org.eclipse.osee.framework.client.info.servlet;

import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public class InstallLinkPageGenerator {

   private static final String HTML_HEADER =
         "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html14/loose.dtd\">\n";

   private static final String CSS_SHEET =
         "<style type=\"text/css\"> table.oseeTable { border-width: 1px 1px 1px 1px; border-spacing: 2px; border-style: solid solid solid solid; border-color: blue blue blue blue; border-collapse: separate; background-color: rgb(255, 250, 250); } " + " table.oseeTable th { border-width: 1px 1px 1px 1px; padding: 4px 4px 4px 4px; border-style: solid solid solid solid; border-color: black black black black; background-color: white; -moz-border-radius: 0px 0px 0px 0px; } " + " table.oseeTable td { border-width: 1px 1px 1px 1px; padding: 4px 4px 4px 4px; border-style: solid solid solid solid; border-color: black black black black; background-color: white; -moz-border-radius: 0px 0px 0px 0px; } </style>\n";

   private static final String MULTI_LINK_TEMPLATE =
         HTML_HEADER + "<html>\n<head>\n" + CSS_SHEET + "%s</head>\n<body onload=\"initialize()\">\n%s</body>\n</html>";
   private static final String LAUNCH_PAGE_TEMPLATE =
         HTML_HEADER + "<html>\n<head>\n" + CSS_SHEET + "%s</head>\n<body onload=\"initialize()\">\n<div id='xmsg'/>\n</body>\n</html>";

   private static final String LAUNCH_ERROR_MESSAGE =
         "Please use Internet Explorer. Your browser does not support this operation.";

   private static final String LINK_ERROR_MESSAGE = "Links below will not work unless you use Internet Explorer.";

   private static final String JS_CHECK =
         "if (document.implementation && document.implementation.createDocument) {\nalert('%s');\n return;}\n";

   private InstallLinkPageGenerator() {
   }

   private static String normalizePath(String path) {
      StringBuilder pathBuilder = new StringBuilder();
      boolean wasLastPathSeparator = false;
      for (int charIndex = 0; charIndex < path.length(); charIndex++) {
         char charVal = path.charAt(charIndex);
         if (charVal == '\\' && !wasLastPathSeparator) {
            if (charIndex + 1 < path.length()) {
               if (path.charAt(charIndex + 1) != '\\') {
                  pathBuilder.append('\\');
               }
            } else {
               pathBuilder.append('\\');
            }
            wasLastPathSeparator = true;
         } else {
            wasLastPathSeparator = false;
         }
         pathBuilder.append(charVal);
      }
      return pathBuilder.toString();
   }

   private static String getOpenScript(ClientInstallInfo info, boolean isCloseAllowed, boolean isPromptAllowed) {
      StringBuilder builder = new StringBuilder();
      builder.append("<script type=\"text/javascript\">\n");
      builder.append("function initialize()\n{\n");
      builder.append(String.format(JS_CHECK, LAUNCH_ERROR_MESSAGE));
      String path = info.getExecPath();
      if (info.getOs().contains("win") && info.isActive()) {
         String execName = null;
         String execPath = "";
         int index = path.lastIndexOf("\\");
         if (index > -1) {
            execName = path.substring(index + 1, path.length());
            execPath = normalizePath(path.substring(0, index));
         } else {
            execName = path;
            execPath = "";
         }
         builder.append("var v = new ActiveXObject(\"Shell.Application\");\n");
         builder.append(String.format("v.ShellExecute(\"%s\",\"\",\"%s\", \"open\", 10);\n", execName, execPath));
         if (!isPromptAllowed) {
            builder.append("window.opener=\"self\";\n");
         }
         if (isCloseAllowed) {
            builder.append("window.close();\n");
         }
         builder.append("}\n");
      } else {
         // INVALID LINK PAGE
         builder.append(String.format(
               "var ex=\"No valid link found. Contact your OSEE administrator. Key was: [%s]\";", info.getName()));
         builder.append("document.getElementById('xmsg').innerHTML=ex;");
         builder.append("}\n");
      }
      builder.append("</script>\n");
      return builder.toString();
   }

   private static String getCheckScript() {
      StringBuilder builder = new StringBuilder();
      builder.append("<script type=\"text/javascript\">\n");
      builder.append("function initialize()\n{\n");
      builder.append(String.format(JS_CHECK, LINK_ERROR_MESSAGE));
      builder.append("}\n</script>\n");
      return builder.toString();
   }

   private static String getLinkTable(List<ClientInstallInfo> infos) {
      StringBuilder builder = new StringBuilder();
      builder.append("<h3>OSEE Client Installs</h3>");
      builder.append("<p>Click on the <i>Active</i> install names to launch OSEE.</p>");
      builder.append("<table class=\"oseeTable\" width=\"95%\">");
      builder.append("<tr style=\"background:gray\" ><th>Name</th> <th>Comment</th><th>Status</th>");
      for (ClientInstallInfo info : infos) {
         builder.append("<tr>");
         if (info.isActive()) {
            String path = info.getExecPath();
            builder.append("<td>");
            builder.append(String.format("<a href=\"%s\"> %s</a>",
                  path.startsWith("file://") ? path : "file://" + path,
                  info.getName().replaceAll("osee.install", "").toUpperCase().replaceAll("\\.", " ")));
            builder.append("</td>");
            builder.append(String.format("<td>%s</td>", info.getComment()));
            builder.append("<td>Active</td>");
         } else {
            builder.append(String.format("<td>%s</td>",
                  info.getName().replaceAll("osee.install", "").toUpperCase().replaceAll("\\.", " ")));
            builder.append(String.format("<td>%s</td>", info.getComment()));
            builder.append("<td>In Active</td>");
         }
         builder.append("</tr>");
      }
      builder.append("</table>");
      return builder.toString();
   }

   public static String generate(List<ClientInstallInfo> infos, boolean isCloseAllowed, boolean isPromptAllowed) {
      String toReturn = null;
      if (infos.size() == 1) {
         toReturn = String.format(LAUNCH_PAGE_TEMPLATE, getOpenScript(infos.get(0), isCloseAllowed, isPromptAllowed));
      } else {
         toReturn = String.format(MULTI_LINK_TEMPLATE, getCheckScript(), getLinkTable(infos));
      }
      return toReturn;
   }
}
