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

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.hyperlinks.Hyperlink;
import com.nomagic.magicdraw.hyperlinks.HyperlinkHandler;
import com.nomagic.magicdraw.hyperlinks.ui.HyperlinkEditor;
import com.nomagic.ui.ScalableImageIcon;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import javax.swing.Icon;
import org.eclipse.osee.mbse.cameo.browser.OSEEHttpClient;

/**
 * @author David W. Miller
 */
class OSEEHyperlinkHandler implements HyperlinkHandler {
   private final Icon icon;

   OSEEHyperlinkHandler() {
      icon = new ScalableImageIcon(getClass(), "img.gif");
   }

   @Override
   public boolean isSupportedProtocol(String protocol) {
      return OSEEHyperlink.PROTOCOL.equals(protocol);
   }

   @Override
   public Icon getIcon(Hyperlink link) {
      return icon;
   }

   @Override
   public Hyperlink create(String text, String url) {
      return new OSEEHyperlink(text, url);
   }

   @Override
   public HyperlinkEditor getEditor() {
      return new OSEEHyperlinkPanel(this);
   }

   @Override
   public void activate(Element element, Hyperlink link) {
      Application.getInstance().getGUILog().showMessage("OSEE hyperlink activated: " + link.getUrl());
      OSEEHttpClient client = new OSEEHttpClient();
      client.connectElementToOSEE(element.getHumanName());
   }
}
