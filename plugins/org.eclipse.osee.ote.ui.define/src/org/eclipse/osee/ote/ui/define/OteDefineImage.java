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
package org.eclipse.osee.ote.ui.define;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public enum OteDefineImage implements KeyedImage {
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
   TEST_CASE("file.gif"),
   TEST_PROCEDURE("procedure.gif"),
   TEST_RUN("testrun.gif"),
   TEST_RUN_VIEW("testRunView.gif"),
   TEST_SUPPORT("function.gif"),

   VERSION_CONTROLLED("version_controlled.gif");

   private final String fileName;

   private OteDefineImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(Activator.PLUGIN_ID, fileName);
   }

   @Override
   public String getImageKey() {
      return Activator.PLUGIN_ID + ".images." + fileName;
   }
}