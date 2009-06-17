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
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.configuration.ConfigFileHandler;
import org.eclipse.osee.ote.ui.test.manager.configuration.ISaveConfig;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTableViewer;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SaveScriptPage implements ISaveConfig, ScriptPageConstants, Xmlizable{

   private Document doc;
   private Element root;
   private ScriptPage scriptPage;

   public SaveScriptPage(ScriptPage tmPage) throws ParserConfigurationException {
      this.scriptPage = tmPage;
      doc = Jaxp.newDocument();
      root = doc.createElement(ScriptPageConstants.SCRIPTPAGE_CONFIG);
      doc.appendChild(root);
   }

   public void printXmlTree() {
      OseeLog.log(TestManagerPlugin.class, Level.INFO, "Script Page Tree: ");
      try {
         OseeLog.log(TestManagerPlugin.class, Level.INFO, Jaxp.xmlToString(doc, Jaxp.getPrettyFormat(doc)));
      }
      catch (IOException ex) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
      }
   }

   public void saveConfig(File fileName) throws Exception {
      ScriptTableViewer scriptTable = scriptPage.getScriptTableViewer();
      Vector<ScriptTask> tasks = scriptTable.getTasks();

      root.appendChild(miscellaneousInfoToXml(doc));

      for (ScriptTask task : tasks) {
         root.appendChild(scriptTaskToXml(doc, task));
      }

      ConfigFileHandler.writeFile(doc, fileName.getAbsolutePath());
      debug(fileName.getAbsolutePath());
   }

   public Element toXml(Document doc) {
      return root;
   }

   private void debug(String val) {
      OseeLog.log(TestManagerPlugin.class, Level.INFO, "Saved to: " + val);
   }

   private Element miscellaneousInfoToXml(Document doc) {
      Element node = doc.createElement(ScriptPageConstants.SERVICES_ENTRY);
      // TODO add preferred host
      // TODO add ofp
      // TODO add view if unit test config

      return node;
   }

   private Element scriptTaskToXml(Document doc, ScriptTask task) {
      Element taskRoot = doc.createElement(ScriptPageConstants.SCRIPT_ENTRY);
      taskRoot.appendChild(Jaxp.createElement(doc, ScriptPageConstants.SCRIPT_NAME_FIELD,task.getName()));
      taskRoot.appendChild(Jaxp.createElement(doc, ScriptPageConstants.RAW_FILENAME_FIELD,task.getScriptModel().getWorkspaceRelativePath()));
      taskRoot.appendChild(Jaxp.createElement(doc, ScriptPageConstants.RUNNABLE_FIELD,Boolean.toString(task.isRunnable())));
      return taskRoot;
   }
}