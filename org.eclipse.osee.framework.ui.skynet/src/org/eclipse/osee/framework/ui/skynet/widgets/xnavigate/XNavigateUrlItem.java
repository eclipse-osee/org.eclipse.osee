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
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.net.URL;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
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
    * @param parent
    * @param name to use as display name
    * @param url to open
    * @param external true to open in system browser; false to open inside Eclipse
    * @throws OseeArgumentException
    */
   public XNavigateUrlItem(XNavigateItem parent, String name, String url, boolean external) throws OseeArgumentException {
      this(parent, name, url, external, null);
   }

   public XNavigateUrlItem(XNavigateItem parent, String name, String url, boolean external, OseeImage oseeImage) throws OseeArgumentException {
      super(parent, name, oseeImage);
      this.url = url;
      this.external = external;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (external)
         Program.launch(url);
      else {
         IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
         try {
            IWebBrowser browser = browserSupport.createBrowser("osee.ats.navigator.browser");
            browser.openURL(new URL(url));
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}
