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

package org.eclipse.osee.framework.ui.skynet.action.browser;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
