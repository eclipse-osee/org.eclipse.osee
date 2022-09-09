/*********************************************************************
* Copyright (c) 2009 Boeing
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

package org.eclipse.osee.ats.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.enums.OseeImage;

/**
 * @author Donald G. Dunne
 */
public class AtsImage extends OseeImage {

   public static List<AtsImage> values = new ArrayList<>();
   private static final Long ENUM_ID = 4522234322L;
   private static String IDE_PLUGIN_ID = "org.eclipse.osee.ats.ide";

   public static AtsImage ACTION = new AtsImage("action.gif");
   public static AtsImage ACTIONABLE_ITEM = new AtsImage("AI.gif");
   public static AtsImage ARROW_LEFT_YELLOW = new AtsImage("nav_forward.gif");
   public static AtsImage ART_VIEW = new AtsImage("artView.gif");
   public static AtsImage ATS = new AtsImage("atsPerspective.gif");
   public static AtsImage BUILD_IMPACT = new AtsImage("buildImpact.gif");
   public static AtsImage CENTER = new AtsImage("center.gif");
   public static AtsImage CHECK_BLUE = new AtsImage("check.gif");
   public static AtsImage CHECK_CLIPBOARD = new AtsImage("checkClipboard.gif");
   public static AtsImage CHANGE_REQUEST = new AtsImage("changeRequest.gif");
   public static AtsImage COMPOSITE_STATE_ITEM = new AtsImage("compositeStateItem.gif");
   public static AtsImage CHECKBOX_ENABLED = new AtsImage("chkbox_enabled.gif");
   public static AtsImage CHECKBOX_DISABLED = new AtsImage("chkbox_disabled.gif");
   public static AtsImage CONECTION_16 = new AtsImage("connection_s16.gif");
   public static AtsImage CONECTION_24 = new AtsImage("connection_s24.gif");
   public static AtsImage CONTEXT_CHANGE_REPORT = new AtsImage("contextChangeReport.gif");
   public static AtsImage COPY_TO_CLIPBOARD = new AtsImage("copyToClipboard.gif");
   public static AtsImage CUSTOMIZE = new AtsImage("customize.gif");
   public static AtsImage DECISION_REVIEW = new AtsImage("decision_review.gif");
   public static AtsImage DOWN_TRIANGLE = new AtsImage("downTriangle.gif");
   public static AtsImage DROP_HERE_TO_ADD_BACKGROUND = new AtsImage("dropHereToAddBackground.gif");
   public static AtsImage DROP_HERE_TO_REMOVE_BACKGROUND = new AtsImage("dropHereToRemoveBackground.gif");
   public static AtsImage E_BOXED = new AtsImage("e_boxed.gif");
   public static AtsImage EDIT_CLIPBOARD = new AtsImage("editClipboard.gif");
   public static AtsImage ELLIPSE_ICON = new AtsImage("ellipse16.gif");
   public static AtsImage EXPAND_TABLE = new AtsImage("expandTable.gif");
   public static AtsImage FAVORITE = new AtsImage("star.gif");
   public static AtsImage FAVORITE_OVERLAY = new AtsImage("favorite.gif");
   public static AtsImage FLASHLIGHT = new AtsImage("flashlight.gif");
   public static AtsImage GLOBE = new AtsImage("globe.gif");
   public static AtsImage GLOBE_SELECT = new AtsImage("globeSelect.gif");
   public static AtsImage GOAL = new AtsImage("goal.gif");
   public static AtsImage GOAL_NEW = new AtsImage("goalNew.gif");
   public static AtsImage GROUP = new AtsImage("group.gif");
   public static AtsImage HOME = new AtsImage("home.gif");
   public static AtsImage INSERTION = new AtsImage("insertion.gif");
   public static AtsImage INSERTION_ACTIVITY = new AtsImage("insertionActivity.gif");
   public static AtsImage LAYOUT = new AtsImage("layout.gif");
   public static AtsImage NEW_ACTION = new AtsImage("newAction.gif");
   public static AtsImage NEW_NOTE = new AtsImage("newNote.gif");
   public static AtsImage NEW_TASK = new AtsImage("newTask.gif");
   public static AtsImage NEXT = new AtsImage("yellowN_8_8.gif");
   public static AtsImage OPEN_BY_ID = new AtsImage("openId.gif");
   public static AtsImage OPEN_PARENT = new AtsImage("openParent.gif");
   public static AtsImage PEER_REVIEW = new AtsImage("peer_review.gif");
   public static AtsImage PIN_EDITOR = new AtsImage("pinEditor.gif");
   public static AtsImage PLAY_GREEN = new AtsImage("play.gif");
   public static AtsImage PROBLEM = new AtsImage("problem.gif");
   public static AtsImage PROGRAM = new AtsImage("program.gif");
   public static AtsImage PUBLISH = new AtsImage("publish.gif");
   public static AtsImage RELEASED = new AtsImage("orangeR_8_8.gif");
   public static AtsImage REPORT = new AtsImage("report.gif");
   public static AtsImage REVIEW = new AtsImage("review.gif");
   public static AtsImage REVIEW_SEARCH = new AtsImage("reviewSearch2.gif");
   public static AtsImage RIGHT_ARROW_SM = new AtsImage("right_arrow_sm.gif");
   public static AtsImage ROLE = new AtsImage("role.gif");
   public static AtsImage SEARCH = new AtsImage("search.gif");
   public static AtsImage STATE = new AtsImage("state.gif");
   public static AtsImage STATE_DEFINITION = new AtsImage("stateDefinition.gif");
   public static AtsImage STATE_ITEM = new AtsImage("stateItem.gif");
   public static AtsImage SUBSCRIBED = new AtsImage("subscribedEmail.gif");
   public static AtsImage SUBSCRIBED_OVERLAY = new AtsImage("subscribed.gif");
   public static AtsImage TASK = new AtsImage("task.gif");
   public static AtsImage TASK_SELECTED = new AtsImage("taskSelected.gif");
   public static AtsImage TEAM_DEFINITION = new AtsImage("team.gif");
   public static AtsImage WORKFLOW = new AtsImage("workflow.gif");
   public static AtsImage WORKFLOW_DEFINITION = new AtsImage("workDef.gif");
   public static AtsImage DEMO = new AtsImage("demo.gif");
   public static AtsImage TRANSITION = new AtsImage("transition.gif");
   public static AtsImage VERSION = new AtsImage("version.gif");
   public static AtsImage VERSION_LOCKED = new AtsImage("yellowV_8_8.gif");
   public static AtsImage VERSION_NEXT = new AtsImage("versionNext.gif");
   public static AtsImage WORK_PACKAGE = new AtsImage("workPackage.gif");
   public static AtsImage TOOL = new AtsImage("T.gif");
   public static AtsImage ZOOM_IN = new AtsImage("zoom_in.gif");
   public static AtsImage ZOOM_OUT = new AtsImage("zoom_out.gif");

   private AtsImage(ArtifactImage artImage) {
      super(artImage.getImageName());
      values.add(this);
   }

   protected AtsImage(String fileName) {
      super(fileName);
      values.add(this);
   }

   @Override
   public String getPluginId() {
      return IDE_PLUGIN_ID;
   }

   public static Collection<AtsImage> getValues() {
      return values;
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

}
