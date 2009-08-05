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
package org.eclipse.osee.framework.ui.skynet.group;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

public class GroupLabelProvider extends LabelProvider {
   public GroupLabelProvider() {
      super();
   }

   /*
    * @see ILabelProvider#getImage(Object)
    */
   @Override
   public Image getImage(Object element) {
      if (element instanceof GroupExplorerItem) {
         GroupExplorerItem item = (GroupExplorerItem) element;
         return ImageManager.getImage(item.getArtifact());
      }
      return ImageManager.getImage(FrameworkImage.MISSING);
   }

   /*
    * @see ILabelProvider#getText(Object)
    */
   @Override
   public String getText(Object element) {
      if (element instanceof GroupExplorerItem) {
         GroupExplorerItem item = (GroupExplorerItem) element;
         Artifact artifact = item.getArtifact();
         if (artifact.isDeleted()) {
            throw new IllegalArgumentException("Can not display a deleted artifact");
         }

         try {
            if (artifact instanceof IGroupExplorerProvider) {
               return ((IGroupExplorerProvider) artifact).getGroupExplorerName();
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE, ex);
         }
         String name = artifact.getName();
         if (name == null) {
            return "";
         }
         return name;
      }
      throw new IllegalArgumentException("wrong type: " + element.getClass().getName());
   }
}