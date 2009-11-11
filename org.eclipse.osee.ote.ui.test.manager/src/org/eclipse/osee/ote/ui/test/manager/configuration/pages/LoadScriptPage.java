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
package org.eclipse.osee.ote.ui.test.manager.configuration.pages;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.configuration.ILoadConfig;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTableViewer;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LoadScriptPage implements ILoadConfig, ScriptPageConstants {

   private final ScriptPage scriptPage;
   private final ScriptTableViewer scriptTableViewer;
   private final Vector<ScriptTask> scriptTasks;

   public LoadScriptPage(ScriptPage tmPage) {
      this.scriptPage = tmPage;
      this.scriptTableViewer = scriptPage.getScriptTableViewer();
      scriptTasks = new Vector<ScriptTask>();
   }

   public void loadConfiguration(final File toProcess) throws Exception {

      Job job = new Job(String.format("Loading Script Run List [%s]", toProcess.getName())) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            Document doc;
            try {
               doc = Jaxp.readXmlDocument(toProcess);
               parseConfig(doc);

               Display.getDefault().syncExec(new Runnable() {
                  public void run() {
                     scriptTableViewer.loadTasksFromList(scriptTasks);
                     scriptTableViewer.refresh();
                     debug(toProcess.getAbsolutePath());
                  }
               });
            } catch (ParserConfigurationException ex) {
               OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
            } catch (SAXException ex) {
               OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
            } catch (IOException ex) {
               OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
            }

            return Status.OK_STATUS;
         }

      };
      job.schedule();
   }

   private void debug(String val) {
      OseeLog.log(TestManagerPlugin.class, Level.INFO, "Loaded From: " + val);
   }

   private void parseConfig(Document doc) {
      NodeList nl = doc.getElementsByTagName(ScriptPageConstants.SCRIPTPAGE_CONFIG);

      for (int i = 0; i < nl.getLength(); i++) {
         Element element = (Element) nl.item(i);
         parseMiscellaneousInfo(element);
         parseScriptEntries(element);
      }
   }

   private void parseMiscellaneousInfo(Element element) {
      // Iterator iterator = node.getDescendants(new RegExElementFilter(
      // Pattern.compile(ScriptPageConstants.SERVICES_ENTRY)));
      // while (iterator.hasNext()) {
      // Element child = (Element) iterator.next();
      // TODO load miscellaneous information to page
      // }
   }

   private class LoadScriptHelper implements Runnable {
      private boolean stopLoading = false;
      private final String path;

      public LoadScriptHelper(String path) {
         this.path = path;
      }

      public void run() {
         if (!MessageDialog.openQuestion(
               Display.getDefault().getActiveShell(),
               "Script not found",
               "The script " + path + " was not found in this workspace. Do you want to continue loading from the script list file. ")) {
            stopLoading = true;
         }
      }

      public boolean stop() {
         return stopLoading;
      }
   }

   private void parseScriptEntries(Element element) {
      NodeList nl = element.getElementsByTagName(ScriptPageConstants.SCRIPT_ENTRY);
      String alternatePath = scriptPage.getTestManager().getAlternateOutputDir();
      for (int i = 0; i < nl.getLength(); i++) {
         Element child = (Element) nl.item(i);
         final String path = Jaxp.getChildText(child, ScriptPageConstants.RAW_FILENAME_FIELD);
         String runnable = Jaxp.getChildText(child, ScriptPageConstants.RUNNABLE_FIELD);
         IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
         if (file.exists()) {
            try {
               ScriptTask task = new ScriptTask(file.getLocation().toString(), alternatePath);
               task.setRun(Boolean.parseBoolean(runnable));
               scriptTasks.add(task);
            } catch (NullPointerException e) {
               e.printStackTrace();
            }
         } else {
            LoadScriptHelper helper = new LoadScriptHelper(path);
            Display.getDefault().syncExec(helper);
            if (helper.stop()) {
               break;
            }
         }
      }
   }

}
