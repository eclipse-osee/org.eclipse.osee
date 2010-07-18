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
package org.eclipse.osee.ote.ui.mux;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Andrew M. Finkbeiner
 */
public enum OteMuxImage implements KeyedImage {
   MUX("1553.gif");

   private final String fileName;

   private OteMuxImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(MuxToolPlugin.PLUGIN_ID, "icons", fileName);
   }

   @Override
   public String getImageKey() {
      return MuxToolPlugin.PLUGIN_ID + ".icons." + fileName;
   }
}
