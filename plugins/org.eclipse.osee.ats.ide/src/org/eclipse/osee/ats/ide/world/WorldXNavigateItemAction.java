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
package org.eclipse.osee.ats.ide.world;

import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class WorldXNavigateItemAction extends XNavigateItemAction {

   public WorldXNavigateItemAction(XNavigateItem parent, String name, KeyedImage oseeImage) {
      super(parent, name, oseeImage == null ? determineImage(name) : oseeImage);
   }

   public WorldXNavigateItemAction(XNavigateItem parent, String name) {
      super(parent, name, determineImage(name));
   }

   private static KeyedImage determineImage(String name) {
      if (name.contains("Report") || name.contains("Metrics")) {
         return AtsImage.REPORT;
      } else if (name.contains("Search")) {
         return FrameworkImage.FLASHLIGHT;
      } else if (name.contains("Task")) {
         return AtsImage.TASK;
      } else if (name.contains("Sync")) {
         return FrameworkImage.ARROW_LEFT_YELLOW;
      }
      return AtsImage.GLOBE;
   }
}