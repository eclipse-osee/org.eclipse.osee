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

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class WorldXNavigateItemAction extends XNavigateItemAction {

   /**
    * @param parent
    * @param name
    * @param oseeImage
    * @throws OseeArgumentException
    */
   public WorldXNavigateItemAction(XNavigateItem parent, String name, OseeImage oseeImage) throws OseeArgumentException {
      super(parent, name, oseeImage);
   }

   /**
    * @param parent
    * @param name
    * @throws OseeArgumentException
    */
   public WorldXNavigateItemAction(XNavigateItem parent, String name) throws OseeArgumentException {
      super(parent, name, determineImage(name));
   }

   private static OseeImage determineImage(String name) {
      if (name.contains("Report")) {
         return AtsImage.REPORT;
      } else if (name.contains("Search")) {
         return FrameworkImage.FLASHLIGHT;
      } else if (name.contains("Task")) {
         return AtsImage.TASK;
      }
      return AtsImage.GLOBE;
   }
}