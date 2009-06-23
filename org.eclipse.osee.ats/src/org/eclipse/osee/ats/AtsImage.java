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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Ryan D. Brooks
 */
public enum AtsImage implements OseeImage {
   ACTION("action.gif"),
   ACTIONABLE_ITEM("AI.gif"),
   ART_VIEW("artView.gif"),
   CENTER("center.gif"),
   CHECK_BLUE("check.gif"),
   CONECTION_16("connection_s16.gif"),
   CONECTION_24("connection_s24.gif"),
   COPY_TO_CLIPBOARD("copyToClipboard.gif"),
   CUSTOMIZE("customize.gif"),
   DOWN_TRIANGLE("downTriangle.gif"),
   ELLIPSE_ICON("ellipse16.gif"),
   FAVORITE("star.gif"),
   FAVORITE_OVERLAY("favorite.gif"),
   GLOBE("globe.gif"),
   GLOBE_SELECT("globeSelect.gif"),
   HOME("home.gif"),
   MY_WORLD("MyWorld.gif"),
   NEW_ACTION("newAction.gif"),
   NEW_NOTE("newNote.gif"),
   NEW_TASK("newTask.gif"),
   NEXT("yellowN_8_8.gif"),
   OPEN_BY_ID("openId.gif"),
   OPEN_PARENT("openParent.gif"),
   PIN_EDITOR("pinEditor.gif"),
   PLAY_GREEN("play.gif"),
   PRIVILEDGED_EDIT("privEdit.gif"),
   PUBLISH("publish.gif"),
   RELEASED("orangeR_8_8.gif"),
   REPORT("report.gif"),
   REVIEW("R.gif"),
   ROLE("role.gif"),
   SUBSCRIBED("subscribedEmail.gif"),
   SUBSCRIBED_OVERLAY("subscribed.gif"),
   TASK("task.gif"),
   TASK_SELECTED("taskSelected.gif"),
   TEAM_DEFINITION("team.gif"),
   TEAM_WORKFLOW("workflow.gif"),
   WORKFLOW_CONFIG("workflowConfig.gif"),
   TOOL("tool.gif"),
   T("T.gif"),
   ZOOM("zoom_in.gif"),
   ZOOM_IN("zoom_in.gif"),
   ZOOM_OUT("zoom_in.gif");

   private final String fileName;

   private AtsImage(String fileName) {
      this.fileName = fileName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#createImageDescriptor()
    */
   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(AtsPlugin.PLUGIN_ID, "images", fileName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#getImageKey()
    */
   @Override
   public String getImageKey() {
      return AtsPlugin.PLUGIN_ID + "." + fileName;
   }
}