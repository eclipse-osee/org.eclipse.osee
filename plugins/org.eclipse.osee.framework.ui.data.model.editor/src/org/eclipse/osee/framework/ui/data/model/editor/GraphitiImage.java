/*********************************************************************
 * Copyright (c) 2015 Boeing
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