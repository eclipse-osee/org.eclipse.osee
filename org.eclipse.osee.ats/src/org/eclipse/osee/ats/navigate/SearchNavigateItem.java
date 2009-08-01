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
package org.eclipse.osee.ats.navigate;

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class SearchNavigateItem extends XNavigateItem {

   private final WorldSearchItem wsi;

   /**
    * @param parent
    * @param wsi
    * @throws OseeCoreException
    */
   public SearchNavigateItem(XNavigateItem parent, WorldSearchItem wsi) throws OseeCoreException {
      super(parent, wsi.getName(), AtsImage.GLOBE);
      this.wsi = wsi;
   }

   public SearchNavigateItem(XNavigateItem parent, WorldSearchItem wsi, OseeImage oseeImage) throws OseeCoreException {
      super(parent, wsi.getName(), oseeImage);
      this.wsi = wsi;
   }

   public WorldSearchItem getWorldSearchItem() {
      return wsi;
   }

   @Override
   public Image getImage() {
      Image image = wsi.getImage();
      if (image != null) return image;
      return ImageManager.getImage(AtsImage.GLOBE);
   }

}
