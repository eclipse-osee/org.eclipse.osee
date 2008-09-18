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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class AdminContributionItem extends SkynetContributionItem {

   private static final String ID = "ats.admin";

   private static final Image ENABLED_IMAGE = SkynetGuiPlugin.getInstance().getImage("redExclaim.gif");
   private static final Image DISABLED_IMAGE = ENABLED_IMAGE;

   private static String ENABLED_TOOLTIP = "AtsAdmin";
   private static String DISABLED_TOOLTIP = "";

   public AdminContributionItem() {
      super(ID, ENABLED_IMAGE, DISABLED_IMAGE, ENABLED_TOOLTIP, DISABLED_TOOLTIP);
      init();
   }

   private void init() {
      updateStatus(true);
   }

   public static void addTo(IStatusLineManager manager) {
      for (IContributionItem item : manager.getItems())
         if (item instanceof AdminContributionItem) return;
      manager.add(new AdminContributionItem());
   }

}
