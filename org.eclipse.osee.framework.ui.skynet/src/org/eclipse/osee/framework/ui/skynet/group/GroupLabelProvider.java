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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class GroupLabelProvider extends LabelProvider {
   private static final OseeUiActivator plugin = SkynetGuiPlugin.getInstance();

   public GroupLabelProvider() {
      super();
   }

   /*
    * @see ILabelProvider#getImage(Object)
    */
   public Image getImage(Object element) {
      if (element instanceof GroupExplorerItem) {
         GroupExplorerItem item = (GroupExplorerItem) element;
         String typename = item.getArtifact().getArtifactTypeName();
         if (typename.equals("Heading"))
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
         else if (typename.equals("Narrative"))
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
         else if (typename.equals("Action"))
            return plugin.getImage("A.gif");
         else if (typename.equals("Product Impact"))
            return plugin.getImage("product.gif");
         else if (typename.equals("Aspect Impact"))
            return plugin.getImage("aspect.gif");
         else if (typename.equals("Task"))
            return plugin.getImage("task.gif");
         else if (typename.equals("Universal Group")) return plugin.getImage("group.gif");
      }
      return plugin.getImage("laser_16_16.gif");

   }

   /*
    * @see ILabelProvider#getText(Object)
    */
   public String getText(Object element) {
      if (element instanceof GroupExplorerItem) {
         GroupExplorerItem item = (GroupExplorerItem) element;
         Artifact artifact = item.getArtifact();
         if (artifact.isDeleted()) throw new IllegalArgumentException("Can not display a deleted artifact");

         String name = artifact.getDescriptiveName();
         if (name == null) {
            return "";
         }
         return name;
      }
      throw new IllegalArgumentException("wrong type: " + element.getClass().getName());
   }
}