/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.data.model.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Ryan D. Brooks
 */
public enum GraphitiImage implements KeyedImage {

   GRAPHITI_DIAGRAM("diagram.gif");

   private static final String symbolicBundleName = "org.eclipse.osee.framework.ui.data.model.editor";

   private final String fileName;

   private GraphitiImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(symbolicBundleName, fileName);
   }

   @Override
   public String getImageKey() {
      return symbolicBundleName + "." + fileName;
   }
}