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
package org.eclipse.osee.ote.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.ote.ui.internal.TestCoreGuiPlugin;

/**
 * @author Andrew M. Finkbeiner
 */
public enum OteImage implements KeyedImage {
   CHECKOUT("checkout.gif"),
   CONNECTED("connected_sm.gif"),
   OTE("welcome_item3.gif");

   private final String fileName;

   private OteImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(TestCoreGuiPlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return TestCoreGuiPlugin.PLUGIN_ID + ".images." + fileName;
   }
}