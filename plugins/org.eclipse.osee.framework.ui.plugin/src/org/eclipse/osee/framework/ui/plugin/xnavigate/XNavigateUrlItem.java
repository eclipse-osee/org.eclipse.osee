/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.net.URL;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Donald G. Dunne
 */
public class XNavigateUrlItem extends XNavigateItemAction {

   private final String url;
   private final boolean external;

   /**
    * Creates a navigation item that will open the given url either internal or external to Eclipse.
    *
    * @param name to use as display name
    * @param url to open
    * @param external true to open in system browser; false to open inside Eclipse
    */
   public XNavigateUrlItem(String name, String url, boolean external, XNavItemCat xNavItemCat) {
      this(name, url, external, PluginUiImage.URL, xNavItemCat);
   }

   public XNavigateUrlItem(String name, String url, boolean external, KeyedImage oseeImage, XNavItemCat xNavItemCat) {
      super(name, oseeImage, xNavItemCat);
      this.url = url;
      this.external = external;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (external) {
         Program.launch(url);
      } else {
         IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
         try {
            IWebBrowser browser = browserSupport.createBrowser("osee.ats.navigator.browser");
            browser.openURL(new URL(url));
         } catch (Exception ex) {
            OseeLog.log(UiPluginConstants.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

}
