/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.skywalker.arttype;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.EntityConnectionData;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeWalkerLabelProvider implements ILabelProvider {

   private final ArtifactTypeContentProvider contentProvider;

   public ArtifactTypeWalkerLabelProvider(ArtifactTypeContentProvider contentProvider) {
      this.contentProvider = contentProvider;
   }

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
      } else if (arg0 instanceof EntityConnectionData) {
         EntityConnectionData connection = (EntityConnectionData) arg0;
         ArtifactTypeToken dest = (ArtifactTypeToken) connection.dest;
         if (contentProvider.getParentTypes().contains(dest)) {
            return "parent";
         }
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
