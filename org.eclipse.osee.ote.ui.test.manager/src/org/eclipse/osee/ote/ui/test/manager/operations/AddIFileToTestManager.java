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
package org.eclipse.osee.ote.ui.test.manager.operations;

import java.util.List;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.util.PluginUtil;
import org.eclipse.osee.ote.ui.test.manager.util.TestManagerSelectDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class AddIFileToTestManager {
   private static AddIFileToTestManager instance = null;

   private AddIFileToTestManager() {
   }

   public static AddIFileToTestManager getOperation() {
      if (instance == null) {
         instance = new AddIFileToTestManager();
      }
      return instance;
   }

   public void removeAllScriptsFromPage() {
      if (PluginUtil.areTestManagersAvailable() != true) {
         AWorkbench.popup("ERROR", "Test Manager Not Opened");
         return;
      }
      for(TestManagerEditor editor: PluginUtil.getTestManagers()){
         editor.getPageManager().getScriptPage().getScriptTableViewer().getTaskList().removeAllTasks();
      }
   }
   
   public void addFilesToScriptPage(List<String> files, boolean removeOtherScripts){
      if (PluginUtil.areTestManagersAvailable() != true) {
         AWorkbench.popup("ERROR", "Test Manager Not Opened");
         return;
      }
      TestManagerEditor[] itemsToOpen = PluginUtil.getTestManagers();
      for (TestManagerEditor tme : itemsToOpen) {
         if(removeOtherScripts){
            tme.getPageManager().getScriptPage().getScriptTableViewer().getTaskList().removeAllTasks();
         }
         tme.addFiles(files.toArray(new String[files.size()]));
      }
   }
   
   public void addIFileToScriptsPage(String fullPath) {
      if (PluginUtil.areTestManagersAvailable() != true) {
         AWorkbench.popup("ERROR", "Test Manager Not Opened");
         return;
      }
      TestManagerEditor[] itemsToOpen = PluginUtil.getTestManagers();
      if (itemsToOpen.length > 1) {
         TestManagerEditor[] selected = TestManagerSelectDialog.getTestManagerFromUser();
         if (selected.length > 0) {
            handleItemsSelected(itemsToOpen, fullPath);
         }
      } else {
         handleItemsSelected(itemsToOpen, fullPath);
      }
   }

   private void handleItemsSelected(TestManagerEditor[] items, String fullPath) {
      for (TestManagerEditor tme : items) {
         tme.addFile(fullPath);
      }
      openFirstItemSelected(items);
   }

   private void openFirstItemSelected(TestManagerEditor[] items) {
      // Show first Test Manager Editor
      TestManagerEditor tme = items[0];
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      page.activate(tme.getSite().getPart());
      tme.activateScriptsPage();
   }
}
