/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
