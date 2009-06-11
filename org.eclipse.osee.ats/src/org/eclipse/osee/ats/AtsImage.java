/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats;

import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Ryan D. Brooks
 */
public enum AtsImage implements OseeImage {
   RELEASED("orangeR_8_8.gif"),
   NEXT("yellowN_8_8.gif"),
   FAVORITE("favorite.gif"),
   SUBSCRIBED("subscribed.gif"),
   GLOBE("globe.gif"),
   REPORT("report.gif"),
   TASK("task.gif");

   private final String fileName;

   private AtsImage(String fileName) {
      this.fileName = fileName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#getSymbolicBundleName()
    */
   @Override
   public String getSymbolicBundleName() {
      return AtsPlugin.PLUGIN_ID;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#getFileName()
    */
   @Override
   public String getFileName() {
      return fileName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#getPath()
    */
   @Override
   public String getPath() {
      return "images";
   }
}
