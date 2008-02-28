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

import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;

/**
 * @author Donald G. Dunne
 */
public class SearchNavigateItem extends XNavigateItem {

   private final WorldSearchItem wsi;

   /**
    * @param parent
    * @param wsi
    */
   public SearchNavigateItem(XNavigateItem parent, WorldSearchItem wsi) {
      super(parent, wsi.getName());
      this.wsi = wsi;
   }

   public WorldSearchItem getWorldSearchItem() {
      return wsi;
   }

}
