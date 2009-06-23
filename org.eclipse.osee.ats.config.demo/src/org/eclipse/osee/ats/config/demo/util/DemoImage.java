/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.demo.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.config.demo.internal.OseeAtsConfigDemoActivator;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Donald G. Dunne
 */
public enum DemoImage implements OseeImage {
   DEMO_WORKFLOW("workflow.gif");

   private final String fileName;

   private DemoImage(String fileName) {
      this.fileName = fileName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#createImageDescriptor()
    */
   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(OseeAtsConfigDemoActivator.PLUGIN_ID, "images", fileName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#getImageKey()
    */
   @Override
   public String getImageKey() {
      return OseeAtsConfigDemoActivator.PLUGIN_ID + "." + fileName;
   }
}