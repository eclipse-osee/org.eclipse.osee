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
package org.eclipse.osee.ats.world;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class WorldXNavigateItemAction extends XNavigateItemAction {

   /**
    * @param parent
    * @param name
    */
   public WorldXNavigateItemAction(XNavigateItem parent, String name) {
      super(parent, name);
   }

   /**
    * @param parent
    * @param name
    * @param image
    */
   public WorldXNavigateItemAction(XNavigateItem parent, String name, Image image) {
      super(parent, name, image);
   }

   /**
    * @param parent
    * @param name
    * @param promptFirst
    * @param image
    */
   public WorldXNavigateItemAction(XNavigateItem parent, String name, boolean promptFirst, Image image) {
      super(parent, name, promptFirst, image);
   }

   /**
    * @param parent
    * @param action
    */
   public WorldXNavigateItemAction(XNavigateItem parent, Action action) {
      super(parent, action);
   }

   /**
    * @param parent
    * @param action
    * @param image
    */
   public WorldXNavigateItemAction(XNavigateItem parent, Action action, Image image) {
      super(parent, action, image);
   }

   /**
    * @param parent
    * @param action
    * @param image
    * @param promptFirst
    */
   public WorldXNavigateItemAction(XNavigateItem parent, Action action, Image image, boolean promptFirst) {
      super(parent, action, image, promptFirst);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem#getImage()
    */
   @Override
   public Image getImage() {
      if (getName().contains("Report")) {
         return ImageManager.getImage(AtsImage.REPORT);
      } else if (getName().contains("Search")) {
         return ImageManager.getImage(FrameworkImage.FLASHLIGHT);
      } else if (getName().contains("Task")) {
         return ImageManager.getImage(AtsImage.TASK);
      }
      return ImageManager.getImage(AtsImage.GLOBE);
   }

}
