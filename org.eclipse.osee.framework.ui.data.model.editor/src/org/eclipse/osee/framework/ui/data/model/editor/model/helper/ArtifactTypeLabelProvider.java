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
package org.eclipse.osee.framework.ui.data.model.editor.model.helper;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeLabelProvider implements ILabelProvider {

   @Override
   public void addListener(ILabelProviderListener listener) {
   }

   @Override
   public void dispose() {
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
   }

   @Override
   public Image getImage(Object element) {
      if (element instanceof ArtifactDataType) {
         return ((ArtifactDataType) element).getImage();
      }
      if (element instanceof DataTypeSource) {
         return ((DataTypeSource) element).isFromDataStore() ? ImageManager.getImage(ODMImage.DATASTORE_IMAGE) : ImageManager.getImage(ODMImage.FILE_SOURCE_IMAGE);
      }
      if (element instanceof PackageModel) {
         return ImageManager.getImage(ODMImage.NAMESPACE_IMAGE);
      }
      return null;
   }

   @Override
   public String getText(Object element) {
      if (element instanceof DataTypeSource) {
         return ((DataTypeSource) element).getSourceId();
      }
      if (element instanceof ArtifactDataType) {
         return ((ArtifactDataType) element).getName();
      }
      if (element instanceof String) {
         return (String) element;
      }
      return null;
   }
}