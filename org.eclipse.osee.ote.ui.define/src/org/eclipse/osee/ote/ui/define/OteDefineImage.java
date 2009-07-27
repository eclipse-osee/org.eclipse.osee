package org.eclipse.osee.ote.ui.define;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public enum OteDefineImage implements OseeImage {
   ADDITION("addition.gif"),
   CHECKBOX_ENABLED("chkbox_enabled.gif"),
   CHECKBOX_DISABLED("chkbox_disabled.gif"),
   CHILD_BRANCH("childBranch.gif"),
   COLLAPSE_STATE("collapseState.gif"),
   COMMIT("commit.gif"),
   COMMIT_WIZ("commit_wiz.png"),
   CONFAUTO_OV("confauto_ov.gif"),
   DELETE("delete.gif"),
   DELETE_ALL("deleteAll.gif"),
   EXPAND_STATE("expandState.gif"),
   OBSTRUCTED("obstructed.gif"),
   OBSTRUCTED_16_BY_16("obstructed_16x16.gif"),
   REFRESH("refresh.gif"),
   REMOVE("remove.gif"),
   REMOVE_ALL("removeAll.gif"),
   SWITCHED("switched.gif"),
   TEST_RUN_VIEW("testRunView.gif"),
   VERSION_CONTROLLED("version_controlled.gif");

   private final String fileName;

   private OteDefineImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(OteUiDefinePlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return OteUiDefinePlugin.PLUGIN_ID + ".images." + fileName;
   }
}