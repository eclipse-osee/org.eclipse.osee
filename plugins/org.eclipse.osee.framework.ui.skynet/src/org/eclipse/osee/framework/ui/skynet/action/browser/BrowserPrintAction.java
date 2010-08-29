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
package org.eclipse.osee.framework.ui.skynet.action.browser;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class BrowserPrintAction extends Action {

   private final IBrowserActionHandler iBrowserActionHandler;

   public BrowserPrintAction(IBrowserActionHandler iBrowserActionHandler) {
      this.iBrowserActionHandler = iBrowserActionHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.PRINT));
      setToolTipText("Print");
   }

   @Override
   public void run() {
      try {
         iBrowserActionHandler.getBrowser().setUrl("javascript:print()");
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
