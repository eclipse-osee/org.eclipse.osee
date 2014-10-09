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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptBlam extends AbstractBlam {

   private static final String SCRIPT_DSL_ID = "orcs";

   @Override
   public String getName() {
      return "Orcs Script (Experimental)";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String script = variableMap.getString("Console");
      String params = variableMap.getString("Parameters");
      boolean debug = variableMap.getBoolean("Debug");
      if (Strings.isValid(script)) {
         StringWriter writer = new StringWriter();
         OseeClient oseeClient = ServiceUtil.getOseeClient();
         Properties props = getParameters(params);
         oseeClient.executeScript(script, props, debug, writer);
         log(writer.toString());
      } else {
         log("Console was empty - Type script in console.");
      }
   }

   private Properties getParameters(String params) throws IOException {
      Properties props = new Properties();
      if (Strings.isValid(params)) {
         props.load(new StringReader(params));
      }
      return props;
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Debug\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("<XWidget xwidgetType=\"XText\" fill=\"Vertically\" displayName=\"Parameters\" />");
      builder.append("<XWidget xwidgetType=\"XDslEditorWidget\" displayName=\"Console\" defaultValue=\"");
      builder.append(SCRIPT_DSL_ID);
      builder.append("\" fill=\"vertically\"/>");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define");
   }

   @Override
   public String getDescriptionUsage() {
      return "Type script in console. Click run to execute and display results.";
   }

}