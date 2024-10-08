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

package org.eclipse.osee.ote.ui.define;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.ote.ui.define.internal.Activator;

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