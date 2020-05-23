/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.group;

import java.util.logging.Level;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.util.IGroupExplorerProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
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
         return ArtifactImageManager.getImage(item.getArtifact());
      }
      return ImageManager.getImage(ImageManager.MISSING);
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
            OseeLog.log(Activator.class, Level.SEVERE, ex);
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