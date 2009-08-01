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
public class AdminContributionItem extends OseeContributionItem {

   private static final String ID = "ats.admin";

   private static String ENABLED_TOOLTIP = "AtsAdmin";
   private static String DISABLED_TOOLTIP = "";

   private AdminContributionItem() {
      super(ID);
      init();
   }

   private void init() {
      updateStatus(true);
   }

   public static void addTo(IStatusLineManager manager) {
      boolean wasFound = false;
      for (IContributionItem item : manager.getItems()) {
         if (item instanceof AdminContributionItem) {
            wasFound = true;
            break;
         }
      }
      if (!wasFound) {
         manager.add(new AdminContributionItem());
      }
   }

   @Override
   protected Image getDisabledImage() {
      return ImageManager.getImage(FrameworkImage.EXCLAIM_RED);
   }

   @Override
   protected String getDisabledToolTip() {
      return DISABLED_TOOLTIP;
   }

   @Override
   protected Image getEnabledImage() {
      return ImageManager.getImage(FrameworkImage.EXCLAIM_RED);
   }

   @Override
   protected String getEnabledToolTip() {
      return ENABLED_TOOLTIP;
   }

}
