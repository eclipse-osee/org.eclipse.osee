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
package org.eclipse.osee.ats.config;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

/**
 * Retrieve workflow extension points and import workflows into system
 */
public class ImportWorkflowAction extends Action {
   private final boolean prompt;
   private final String pluginId;

   /**
    * Will import all workflows as specified in plugin extensions
    * 
    * @param prompt
    */
   public ImportWorkflowAction(boolean prompt) {
      this(prompt, null);
   }

   /**
    * Will import only workflows specified in pluginId's extensions The constructor.
    */
   public ImportWorkflowAction(boolean prompt, String pluginId) {
      super("Import Workflows");
      this.prompt = prompt;
      this.pluginId = pluginId;
   }

   /**
    * The action has been activated. The argument of the method represents the 'real' action sitting in the workbench
    * UI.
    */
   public void run() {
      if (prompt && !MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Import ATS Workflows?",
            "Importing ATS Workflows\n\nAre you sure?")) return;

      for (Entry<String, URL> idToURL : loadWorkflowIdToURL(pluginId).entrySet()) {
         // System.out.println(" Loading " + idToURL.getKey());
         WorkflowDiagramFactory diagFact = WorkflowDiagramFactory.getInstance();
         try {
            diagFact.importWorkflowDiagramToSkynet(idToURL.getValue().openStream(), idToURL.getKey());
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
         }
      }
   }

   public static String getWorkflowXmlFromFile(String contains) throws IOException {
      for (Entry<String, URL> idToURL : loadWorkflowIdToURL().entrySet()) {
         if (idToURL.getKey().contains(contains)) {
            return Lib.inputStreamToString(idToURL.getValue().openStream());
         }
      }
      return null;
   }

   @SuppressWarnings("deprecation")
   private static Map<String, URL> loadWorkflowIdToURL() {
      return loadWorkflowIdToURL(null);
   }

   private static Map<String, URL> loadWorkflowIdToURL(String pluginId) {
      Map<String, URL> resources = new HashMap<String, URL>();
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsVueWorkflowDiagram");
      if (point == null) {
         OSEELog.logSevere(AtsPlugin.class, "Can't access AtsVueWorkflowDiagram extension point", true);
         return resources;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String vueFilename = null;
         String id = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsVueWorkflowDiagram")) {
               id = el.getAttribute("id");
               vueFilename = el.getAttribute("vueFilename");
               bundleName = el.getContributor().getName();
               if (pluginId == null || pluginId.equals(bundleName)) {
                  if (vueFilename != null && bundleName != null) {
                     Bundle bundle = Platform.getBundle(bundleName);
                     try {
                        URL url = bundle.getEntry(vueFilename);
                        if (url == null) throw new IllegalStateException(
                              "Invalid AtsVueWorkflowDiagram filename =>" + vueFilename + " specified in bundle => " + bundleName);
                        resources.put(id, url);
                     } catch (Exception ex) {
                        OSEELog.logException(AtsPlugin.class, "Error loading AtsVueWorkflowDiagram extension", ex,
                              false);
                     }
                  }
               }
            }
         }
      }
      return resources;
   }
}