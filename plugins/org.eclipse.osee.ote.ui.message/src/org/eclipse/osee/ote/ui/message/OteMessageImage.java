/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.ote.ui.message.internal.Activator;

/**
 * @author Andrew M. Finkbeiner
 */
public enum OteMessageImage implements KeyedImage {
   BINARY("binary.gif"),
   BINOCULARS("binoculars.gif"),
   BUG("bug.gif"),
   COLLAPSE_STATE("collapse_state.gif"),
   CONFIG("config.gif"),
   DATABASE("database.png"),
   DELETE("delete.gif"),
   DELETE_ALL("deleteAll.gif"),
   ELEMENT("element.gif"),
   ERROR_SM("errorSm.gif"),
   EXPAND_STATE("expand_state.gif"),
   GEAR("gear.png"),
   GLASSES("glasses.gif"),
   HEX("hex.gif"),
   MESSAGE_OLD("message_old.gif"),
   MSG_READ_IMG("msgReadImg.gif"),
   MSG_WRITE_IMG("msgWriteImg.gif"),
   OPEN("open.gif"),
   PIPE("pipe.png"),
   REFRESH("refresh.gif"),
   REMOVE("remove.gif"),
   SAVE("save.gif"),
   SHOW_NAMES("showNames.gif"),
   WATCHLIST_VIEW("watchlist_view.gif"),
   WIRE_AIU("wire_aiu.gif");

   private final String fileName;

   private OteMessageImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(Activator.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return Activator.PLUGIN_ID + ".images." + fileName;
   }
}
