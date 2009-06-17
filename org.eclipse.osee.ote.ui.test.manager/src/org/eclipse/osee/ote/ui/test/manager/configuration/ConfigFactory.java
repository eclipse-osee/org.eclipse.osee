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

import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.configuration.pages.LoadScriptPage;
import org.eclipse.osee.ote.ui.test.manager.configuration.pages.SaveScriptPage;
import org.eclipse.osee.ote.ui.test.manager.pages.AdvancedPage;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage;

public class ConfigFactory {
   private static ConfigFactory instance = null;

   private ConfigFactory() {
   }

   public static ConfigFactory getInstance() {
      if (instance == null) {
         instance = new ConfigFactory();
      }
      return instance;
   }

   public ILoadConfig getLoadConfigHandler(TestManagerPage tmPage) {
      ILoadConfig toReturn = null;

      if (tmPage instanceof ScriptPage) {
         toReturn = new LoadScriptPage((ScriptPage) tmPage);
      } else if (tmPage instanceof AdvancedPage) {
         // toReturn = new AdvancedPageLoad(tmPage);
      }
      return toReturn;
   }

   public ISaveConfig getSaveConfigHandler(TestManagerPage tmPage) {
      ISaveConfig toReturn = null;

      if (tmPage instanceof ScriptPage) {
         try {
            toReturn = new SaveScriptPage((ScriptPage) tmPage);
         } catch (ParserConfigurationException ex) {
            OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
         }
      } else if (tmPage instanceof AdvancedPage) {
         // toReturn = new AdvancedPageSave(tmPage);
      }
      return toReturn;
   }

}
