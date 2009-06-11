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
package org.eclipse.osee.framework.ui.skynet;

/**
 * @author Ryan D. Brooks
 */
public enum FrameworkImage implements OseeImage {
   LOCKED_WITH_ACCESS("green_lock.gif"),
   LOCKED_NO_ACCESS("red_lock.gif"),
   WARNING_OVERLAY("alert_8_8.gif"),
   ERROR_OVERLAY("error.gif"),
   PURPLE("purple.gif"),
   FLASHLIGHT("flashlight.gif"),
   USER("user.gif"),
   WARNING("warn.gif"),
   ERROR("errorRound.gif"),
   PROBLEM("greenBug.gif"),
   GREEN_PLUS("greenPlus.gif"),
   SUPPORT("users2.gif"),
   ADMIN("admin.gif"),
   FOLDER("folder.gif"),
   CONFLICTING_Deleted("CONFLICTING_Deleted.gif"),
   CONFLICTING_Modified("CONFLICTING_Modified.gif"),
   CONFLICTING_New("CONFLICTING_New.gif"),
   INCOMING_Deleted("INCOMING_Deleted.gif"),
   INCOMING_Modified("INCOMING_Modified.gif"),
   INCOMING_New("INCOMING_New.gif"),
   OUTGOING_Artifact_Deleted("OUTGOING_Deleted.gif"),
   OUTGOING_Deleted("OUTGOING_Deleted.gif"),
   OUTGOING_Introduced("OUTGOING_New.gif"),
   OUTGOING_Merged("branch_merge.gif"),
   OUTGOING_New("OUTGOING_New.gif"),
   OUTGOING_Modified("OUTGOING_Modified.gif"),
   ATTRIBUTE("molecule.gif"),
   LASER("laser_16_16.gif"),
   RELATION("relate.gif"),
   REFRESH("refresh.gif"),
   EMAIL("email.gif"),
   VERSION("version.gif");

   private final String fileName;

   private FrameworkImage(String fileName) {
      this.fileName = fileName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#getSymbolicBundleName()
    */
   @Override
   public String getSymbolicBundleName() {
      return SkynetGuiPlugin.PLUGIN_ID;
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
