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
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultBrowserHyperCmd;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultBrowserListener;
import org.eclipse.swt.browser.LocationEvent;

/**
 * @author Donald G. Dunne
 */
public class AtsActionBrowserListener extends XResultBrowserListener {

   public void changing(LocationEvent event) {
      String location = event.location;
      if (location.contains("javascript:print")) return;
      String cmdStr = location.replaceFirst("about:blank", "");
      cmdStr = cmdStr.replaceFirst("blank", "");
      XResultBrowserHyperCmd xResultBrowserHyperCmd = XResultBrowserHyperCmd.getCmdStrHyperCmd(cmdStr);
      if (xResultBrowserHyperCmd == XResultBrowserHyperCmd.openPriorityHelp) {
         event.doit = false;
         AtsPlugin.getInstance().getWorkbench().getHelpSystem().displayHelp(AtsPriority.PRIORITY_HELP_CONTEXT_ID);
      }
      super.changing(event);
   }
}
