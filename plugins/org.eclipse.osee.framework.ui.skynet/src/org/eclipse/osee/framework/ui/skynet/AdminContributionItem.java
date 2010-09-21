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

import org.eclipse.osee.framework.ui.plugin.OseeStatusContributionItem;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCm;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class AdminContributionItem extends OseeStatusContributionItem {

   private static final String ID = "ats.admin";

   private static String ENABLED_TOOLTIP = "AtsAdmin";
   private static String DISABLED_TOOLTIP = "";

   public AdminContributionItem() {
      super(ID);
      init();
   }

   private void init() {
      updateStatus(true);
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

   @Override
   public boolean isCreationAllowed() {
      IOseeCmService atsService = OseeCm.getInstance();
      return atsService != null && atsService.isCmAdmin();
   }

}
