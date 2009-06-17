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
package org.eclipse.osee.ote.ui.test.manager.configuration;

import java.io.File;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.osee.ote.ui.test.manager.pages.StatusWindowWidget;
import org.eclipse.osee.ote.ui.test.manager.util.Dialogs;

/**
 * @author Roberto E. Escobar
 */
public class LoadConfigurationOperation {

   private LoadConfigurationOperation() {
   }

   public static boolean load(TestManagerEditor testManager, File selectedFile) {
      boolean isOk = updateFileStatus(testManager, selectedFile);
      if (isOk != false) {
         isOk &= reLoad(testManager);
      }
      return isOk;
   }

   private static boolean updateFileStatus(TestManagerEditor testManager, File selectedFile) {
      boolean result = false;
      StatusWindowWidget statusWindow = testManager.getPageManager().getScriptPage().getStatusWindow();
      if (statusWindow != null) {
         String filePath = selectedFile.getAbsolutePath();
         statusWindow.setValue(ScriptPage.UpdateableLabel.CONFIGPATHLABEL.name(), filePath);
         testManager.storeValue(testManager.configFileName, filePath);
         statusWindow.refresh();
         result = true;
      }
      return result;
   }

   public static boolean reLoad(TestManagerEditor testManager) {
      boolean result = false;
      String configFile = testManager.loadValue(testManager.configFileName);

      ILoadConfig loadConfig =
            ConfigFactory.getInstance().getLoadConfigHandler(testManager.getPageManager().getScriptPage());
      try {
         loadConfig.loadConfiguration(new File(configFile));
         result = true;
      } catch (Exception ex) {
         Dialogs.popupError("Error Loading File", String.format("Error loading file: [%s]\n%s", configFile,
               TestManagerPlugin.getStackMessages(ex)));
      }
      return result;
   }
}
