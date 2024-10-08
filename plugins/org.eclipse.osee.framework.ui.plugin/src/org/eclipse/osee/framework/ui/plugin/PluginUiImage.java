/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.ui.plugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Ryan D. Brooks
 */
public enum PluginUiImage implements KeyedImage {
   ADMIN("admin.gif"),
   BUG("bug.gif"),
   FOLDER("folder.gif"),
   REFRESH("refresh.gif"),
   CHECKBOX_ENABLED("chkbox_enabled.gif"),
   CHECKBOX_DISABLED("chkbox_disabled.gif"),
   OSEE_16("osee_16.png"),
   SHADED("shaded.gif"),
   URL("www.gif"),
   GEAR("gear.gif");

   private final String fileName;

   private PluginUiImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(UiPluginConstants.PLUGIN_ID, fileName);
   }

   @Override
   public String getImageKey() {
      return UiPluginConstants.PLUGIN_ID + "." + fileName;
   }
}