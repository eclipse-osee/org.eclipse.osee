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
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * Generic Artifact Label Provider showing Descriptive Name as text
 * 
 * @author Donald G. Dunne
 */
public class ArtifactTypeLabelProvider implements ILabelProvider {

   @Override
   public Image getImage(Object arg0) {
      if (arg0 instanceof ArtifactTypeToken) {
         return ArtifactImageManager.getImage((ArtifactTypeToken) arg0);
      }
      return null;
   }

   @Override
   public String getText(Object arg0) {
      if (arg0 instanceof ArtifactTypeToken) {
         return ((ArtifactTypeToken) arg0).getName();
      }
      return "";
   }

   @Override
   public void addListener(ILabelProviderListener arg0) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object arg0, String arg1) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener arg0) {
      // do nothing
   }

}
