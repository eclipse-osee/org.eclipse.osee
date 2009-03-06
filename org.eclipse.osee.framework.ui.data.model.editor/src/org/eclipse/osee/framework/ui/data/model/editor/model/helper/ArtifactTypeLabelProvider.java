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
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImages;
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

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
    */
   @Override
   public Image getImage(Object element) {
      if (element instanceof ArtifactDataType) {
         return ((ArtifactDataType) element).getImage();
      }
      if (element instanceof DataTypeSource) {
         return ((DataTypeSource) element).isFromDataStore() ? ODMImages.getImage(ODMImages.DATASTORE_IMAGE) : ODMImages.getImage(ODMImages.FILE_SOURCE_IMAGE);
      }
      if (element instanceof PackageModel) {
         return ODMImages.getImage(ODMImages.NAMESPACE_IMAGE);
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
    */
   @Override
   public String getText(Object element) {
      if (element instanceof DataTypeSource) {
         return ((DataTypeSource) element).getSourceId();
      }
      if (element instanceof PackageModel) {
         return ((PackageModel) element).getNamespace();
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