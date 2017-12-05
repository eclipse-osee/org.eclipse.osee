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
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Ryan D. Brooks
 */
public enum AtsImage implements KeyedImage {
   ACTION("action.gif"),
   ACTIONABLE_ITEM("AI.gif"),
   ART_VIEW("artView.gif"),
   ATS("atsPerspective.gif"),
   CENTER("center.gif"),
   CHECK_BLUE("check.gif"),
   CONECTION_16("connection_s16.gif"),
   CONECTION_24("connection_s24.gif"),
   COMPOSITE_STATE_ITEM("compositeStateItem.gif"),
   COPY_TO_CLIPBOARD("copyToClipboard.gif"),
   CUSTOMIZE("customize.gif"),
   DECISION_REVIEW("decision_review.gif"),
   DOWN_TRIANGLE("downTriangle.gif"),
   DROP_HERE_TO_ADD_BACKGROUND("dropHereToAddBackground.gif"),
   DROP_HERE_TO_REMOVE_BACKGROUND("dropHereToRemoveBackground.gif"),
   E_BOXED("e_boxed.gif"),
   ELLIPSE_ICON("ellipse16.gif"),
   EXPAND_TABLE("expandTable.gif"),
   FAVORITE("star.gif"),
   FAVORITE_OVERLAY("favorite.gif"),
   GLOBE("globe.gif"),
   GLOBE_SELECT("globeSelect.gif"),
   GOAL("goal.gif"),
   GOAL_NEW("goalNew.gif"),
   HOME("home.gif"),
   INSERTION("insertion.gif"),
   INSERTION_ACTIVITY("insertionActivity.gif"),
   LAYOUT("layout.gif"),
   NEW_ACTION("newAction.gif"),
   NEW_NOTE("newNote.gif"),
   NEW_TASK("newTask.gif"),
   NEXT("yellowN_8_8.gif"),
   OPEN_BY_ID("openId.gif"),
   OPEN_PARENT("openParent.gif"),
   PEER_REVIEW("peer_review.gif"),
   PIN_EDITOR("pinEditor.gif"),
   PLAY_GREEN("play.gif"),
   PRIVILEGED_EDIT("privEdit.gif"),
   PROGRAM("program.gif"),
   PUBLISH("publish.gif"),
   RELEASED("orangeR_8_8.gif"),
   REPORT("report.gif"),
   REVIEW("review.gif"),
   REVIEW_SEARCH("reviewSearch.gif"),
   RIGHT_ARROW_SM("right_arrow_sm.gif"),
   ROLE("role.gif"),
   RULE_DEFINITION("workDef.gif"),
   SEARCH("search.gif"),
   STATE("state.gif"),
   STATE_DEFINITION("stateDefinition.gif"),
   STATE_ITEM("stateItem.gif"),
   SUBSCRIBED("subscribedEmail.gif"),
   SUBSCRIBED_OVERLAY("subscribed.gif"),
   TASK("task.gif"),
   TASK_SELECTED("taskSelected.gif"),
   TEAM_DEFINITION("team.gif"),
   TEAM_WORKFLOW("workflow.gif"),
   TRACE("trace.gif"),
   TRANSITION("transition.gif"),
   VERSION_LOCKED("yellowV_8_8.gif"),
   WORKFLOW_CONFIG("workflow.gif"),
   WORK_DEFINITION("workDef.gif"),
   WORK_PACKAGE("workPackage.gif"),
   TOOL("T.gif"),
   ZOOM("zoom_in.gif"),
   ZOOM_IN("zoom_in.gif"),
   ZOOM_OUT("zoom_out.gif");

   private final String fileName;

   private AtsImage(ArtifactImage artImage) {
      this.fileName = artImage.getImageName();
   }

   private AtsImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(Activator.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return Activator.PLUGIN_ID + "." + fileName;
   }
}