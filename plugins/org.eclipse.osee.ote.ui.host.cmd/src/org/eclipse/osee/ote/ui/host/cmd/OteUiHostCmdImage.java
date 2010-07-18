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
package org.eclipse.osee.ote.ui.host.cmd;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public enum OteUiHostCmdImage implements KeyedImage {
   CONSOLE("console.gif"),
   TEST_SERVER("test_server.gif"),
   USER("user.gif");

   private final String fileName;

   private OteUiHostCmdImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(UiPlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return UiPlugin.PLUGIN_ID + ".images." + fileName;
   }

}
