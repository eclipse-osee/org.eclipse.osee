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
package org.eclipse.osee.framework.ui.service.control;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Andrew M. Finkbeiner
 */
public enum ServiceControlImage implements OseeImage {
   ANNOTATE("annotate.gif"),
   CONFIG("config.gif"),
   CONNECT_FOLDER("connect_folder.gif"),
   CONNECTED_PLUG("connected_plug.gif"),
   CONNECTION("connection.gif"),
   DISCONNECTED("disconnected.gif"),
   FILE("file.gif"),
   GROUP("group.gif"),
   HELP("help.gif"),
   MONITOR("monitor.GIF"),
   REFRESH("refresh.gif"),
   ROCKET("rocket.gif"),
   TOOLS("tools.gif");

   private final String fileName;

   private ServiceControlImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(ControlPlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return ControlPlugin.PLUGIN_ID + ".images." + fileName;
   }
}
