/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.mbse.cameo;

import com.nomagic.magicdraw.hyperlinks.HyperlinkHandler;
import com.nomagic.magicdraw.hyperlinks.ui.HyperlinkEditorPanel;

/**
 * @author David W. Miller
 */
class OSEEHyperlinkPanel extends HyperlinkEditorPanel {

   private static final long serialVersionUID = 8758379346949213385L;

   public OSEEHyperlinkPanel(HyperlinkHandler handler) {
      super("OSEE hyperlink", "Type OSEE link", true, handler, OSEEHyperlink.PROTOCOL);
   }

   @Override
   public boolean isProjectScope() {
      return false;
   }

   @Override
   protected void browse() {
      //
   }
}
