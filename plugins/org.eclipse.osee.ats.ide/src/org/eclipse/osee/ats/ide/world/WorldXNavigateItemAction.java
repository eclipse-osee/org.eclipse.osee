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