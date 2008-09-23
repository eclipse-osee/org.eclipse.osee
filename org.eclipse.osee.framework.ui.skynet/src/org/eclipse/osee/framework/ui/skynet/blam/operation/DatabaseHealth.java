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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthTask.Operation;
import org.osgi.framework.Bundle;

/**
 * @author Jeff C. Phillips
 */
public class DatabaseHealth extends AbstractBlam {
   private Map<String, DatabaseHealthTask> dbFix = new TreeMap<String, DatabaseHealthTask>();
   private Map<String, DatabaseHealthTask> dbVerify = new TreeMap<String, DatabaseHealthTask>();
   private static final String SHOW_DETAILS_PROMPT = "Show Details of Operations";

   public DatabaseHealth() {
      loadExtensions();
   }

   @Override
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      runTasks(variableMap, monitor);
   }

   private void loadExtensions() {
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.ui.skynet.DBHealthTask");
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement element : elements) {
            classname = element.getAttribute("class");
            bundleName = element.getContributor().getName();

            if (classname != null && bundleName != null) {
               Bundle bundle = Platform.getBundle(bundleName);
               try {
                  Class<?> taskClass = bundle.loadClass(classname);
                  Object obj = taskClass.newInstance();
                  DatabaseHealthTask task = (DatabaseHealthTask) obj;

                  if (task.getVerifyTaskName() != null) {
                     dbVerify.put(task.getVerifyTaskName(), task);
                  }
                  if (task.getFixTaskName() != null) {
                     dbFix.put(task.getFixTaskName(), task);
                  }
               } catch (Exception ex) {
               }
            }
         }
      }
   }

   private void runTasks(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Database Health", dbFix.size() + dbVerify.size());
      if (OseeProperties.isDeveloper()) {
         StringBuilder builder = new StringBuilder();
         boolean showDetails = variableMap.getBoolean(SHOW_DETAILS_PROMPT);
         for (String taskName : dbFix.keySet()) {
            if (variableMap.getBoolean(taskName)) {
               monitor.setTaskName(taskName);
               DatabaseHealthTask task = dbFix.get(taskName);
               task.run(variableMap, new SubProgressMonitor(monitor, 1), Operation.Fix, builder, showDetails);
               monitor.worked(1);
            }
         }
         for (String taskName : dbVerify.keySet()) {
            if (variableMap.getBoolean(taskName)) {
               monitor.setTaskName(taskName);
               DatabaseHealthTask task = dbVerify.get(taskName);
               task.run(variableMap, new SubProgressMonitor(monitor, 1), Operation.Verify, builder, showDetails);
               monitor.worked(1);
            }
         }
         appendResultLine(builder.toString());
      } else {
         appendResultLine("Must be a Developer to run this BLAM\n");
      }

   }

   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + SHOW_DETAILS_PROMPT + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Clean Up Operations to Run:\"/>");
      for (String taskName : dbFix.keySet()) {
         builder.append(getOperationsCheckBoxes(taskName));
      }
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Verification Operations to Run:\"/>");
      for (String taskName : dbVerify.keySet()) {
         builder.append(getOperationsCheckBoxes(taskName));
      }
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private String getOperationsCheckBoxes(String checkboxName) {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(checkboxName);
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      return builder.toString();
   }
}
