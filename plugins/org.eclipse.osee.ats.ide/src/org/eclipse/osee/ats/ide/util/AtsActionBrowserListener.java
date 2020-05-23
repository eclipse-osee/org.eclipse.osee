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

package org.eclipse.osee.ats.ide.util;

import org.eclipse.osee.framework.ui.skynet.results.html.XResultBrowserListener;
import org.eclipse.swt.browser.LocationEvent;

/**
 * @author Donald G. Dunne
 */
public class AtsActionBrowserListener extends XResultBrowserListener {

   @Override
   public void changing(LocationEvent event) {
      String location = event.location;
      if (location.contains("javascript:print")) {
         return;
      }
      super.changing(event);
   }
}
