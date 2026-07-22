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
import org.eclipse.osee.framework.core.enums.OseeImage;

/**
 * @author Donald G. Dunne
 */
public class AtsImage extends OseeImage {

   public static final List<AtsImage> values = new ArrayList<>();
   private static final Long ENUM_ID = 4522234322L;
   private static final String IDE_PLUGIN_ID = "org.eclipse.osee.ats.ide";

   public static final AtsImage ACTION = new AtsImage("action.gif");
   public static final AtsImage ACTION_WALKER = new AtsImage("actionWalker.gif");
   public static final AtsImage ACTIONABLE_ITEM = new AtsImage("AI.gif");
   public static final AtsImage ARROW_LEFT_YELLOW = new AtsImage("nav_forward.gif");
   public static final AtsImage ART_VIEW = new AtsImage("artView.gif");
   public static final AtsImage ATS = new AtsImage("atsPerspective.gif");
   public static final AtsImage BUILD_IMPACT = new AtsImage("buildImpact.gif");
   public static final AtsImage CENTER = new AtsImage("center.gif");
   public static final AtsImage CHECK_BLUE = new AtsImage("check.gif");
   public static final AtsImage CHECK_CLIPBOARD = new AtsImage("checkClipboard.gif");
   public static final AtsImage CHANGE_REQUEST = new AtsImage("changeRequest.gif");
   public static final AtsImage COMPOSITE_STATE_ITEM = new AtsImage("compositeStateItem.gif");
   public static final AtsImage CHECKBOX_ENABLED = new AtsImage("chkbox_enabled.gif");
   public static final AtsImage CHECKBOX_DISABLED = new AtsImage("chkbox_disabled.gif");
   public static final AtsImage CONECTION_16 = new AtsImage("connection_s16.gif");
   public static final AtsImage CONECTION_24 = new AtsImage("connection_s24.gif");
   public static final AtsImage CONTEXT_CHANGE_REPORT = new AtsImage("contextChangeReport.gif");
   public static final AtsImage COPY_TO_CLIPBOARD = new AtsImage("copyToClipboard.gif");
   public static final AtsImage CUSTOMIZE = new AtsImage("customize.gif");
   public static final AtsImage DECISION_REVIEW = new AtsImage("decision_review.gif");
   public static final AtsImage DOWN_TRIANGLE = new AtsImage("downTriangle.gif");
   public static final AtsImage DROP_HERE_TO_ADD_BACKGROUND = new AtsImage("dropHereToAddBackground.gif");
   public static final AtsImage DROP_HERE_TO_REMOVE_BACKGROUND = new AtsImage("dropHereToRemoveBackground.gif");
   public static final AtsImage E_BOXED = new AtsImage("e_boxed.gif");
   public static final AtsImage EDIT_CLIPBOARD = new AtsImage("editClipboard.gif");
   public static final AtsImage ELLIPSE_ICON = new AtsImage("ellipse16.gif");
   public static final AtsImage EXPAND_TABLE = new AtsImage("expandTable.gif");
   public static final AtsImage FAVORITE = new AtsImage("star.gif");
   public static final AtsImage FAVORITE_OVERLAY = new AtsImage("favorite.gif");
   public static final AtsImage FLASHLIGHT = new AtsImage("flashlight.gif");
   public static final AtsImage GLOBE = new AtsImage("globe.gif");
   public static final AtsImage GLOBE_SELECT = new AtsImage("globeSelect.gif");
   public static final AtsImage GOAL = new AtsImage("goal.gif");
   public static final AtsImage GOAL_NEW = new AtsImage("goalNew.gif");
   public static final AtsImage GROUP = new AtsImage("group.gif");
   public static final AtsImage HOME = new AtsImage("home.gif");
   public static final AtsImage INSERTION = new AtsImage("insertion.gif");
   public static final AtsImage INSERTION_ACTIVITY = new AtsImage("insertionActivity.gif");
   public static final AtsImage JSON = new AtsImage("json.gif");
   public static final AtsImage JIRA = new AtsImage("jira.gif");
   public static final AtsImage JIRA_ADD = new AtsImage("jiraAdd.gif");
   public static final AtsImage JIRA_UPDATE = new AtsImage("jiraUpdate.gif");
   public static final AtsImage JIRA_LINKED = new AtsImage("jiraLinked.gif");
   public static final AtsImage JIRA_TRANSITION = new AtsImage("jiraTransition.gif");
   public static final AtsImage JIRA_SEARCH = new AtsImage("jiraSearch.gif");
   public static final AtsImage LAYOUT = new AtsImage("layout.gif");
   public static final AtsImage NEW_ACTION = new AtsImage("newAction.gif");
   public static final AtsImage NEW_NOTE = new AtsImage("newNote.gif");
   public static final AtsImage NEW_TASK = new AtsImage("newTask.gif");
   public static final AtsImage NEXT = new AtsImage("yellowN_8_8.gif");
   public static final AtsImage NOTE = new AtsImage("note.gif");
   public static final AtsImage OPEN_BY_ID = new AtsImage("openId.gif");
   public static final AtsImage OPEN_PARENT = new AtsImage("openParent.gif");
   public static final AtsImage PEER_REVIEW = new AtsImage("peer_review.gif");
   public static final AtsImage PIN_EDITOR = new AtsImage("pinEditor.gif");
   public static final AtsImage PLAY_GREEN = new AtsImage("play.gif");
   public static final AtsImage PROBLEM_REPORT = new AtsImage("probRept.gif");
   public static final AtsImage PROBLEM = new AtsImage("problem.gif");
   public static final AtsImage PROGRAM = new AtsImage("program.gif");
   public static final AtsImage PUBLISH = new AtsImage("publish.gif");
   public static final AtsImage RELEASED = new AtsImage("released.gif");
   public static final AtsImage REPORT = new AtsImage("report.gif");
   public static final AtsImage REVIEW = new AtsImage("review.gif");
   public static final AtsImage REVIEW_SEARCH = new AtsImage("reviewSearch2.gif");
   public static final AtsImage RIGHT_ARROW_SM = new AtsImage("right_arrow_sm.gif");
   public static final AtsImage ROLE = new AtsImage("role.gif");
   public static final AtsImage SEARCH = new AtsImage("search.gif");
   public static final AtsImage SORT = new AtsImage("sort.gif");
   public static final AtsImage STATE = new AtsImage("state.gif");
   public static final AtsImage STATE_DEFINITION = new AtsImage("stateDefinition.gif");
   public static final AtsImage STATE_ITEM = new AtsImage("stateItem.gif");
   public static final AtsImage SUBSCRIBED = new AtsImage("subscribedEmail.gif");
   public static final AtsImage SUBSCRIBED_OVERLAY = new AtsImage("subscribed.gif");
   public static final AtsImage TASK = new AtsImage("task.gif");
   public static final AtsImage TASK_SELECTED = new AtsImage("taskSelected.gif");
   public static final AtsImage TEAM_DEFINITION = new AtsImage("team.gif");
   public static final AtsImage WORKFLOW = new AtsImage("workflow.gif");
   public static final AtsImage WORKFLOW_DEFINITION = new AtsImage("workDef.gif");
   public static final AtsImage DEMO = new AtsImage("demo.gif");
   public static final AtsImage TRANSITION = new AtsImage("transition.gif");
   public static final AtsImage VERSION = new AtsImage("version.gif");
   public static final AtsImage VERSION_LOCKED = new AtsImage("yellowV_8_8.gif");
   public static final AtsImage VERSION_NEXT = new AtsImage("versionNext.gif");
   public static final AtsImage WARNING = new AtsImage("warn.gif");
   public static final AtsImage WORK_PACKAGE = new AtsImage("workPackage.gif");
   public static final AtsImage TOOL = new AtsImage("T.gif");
   public static final AtsImage ZOOM_IN = new AtsImage("zoom_in.gif");
   public static final AtsImage ZOOM_OUT = new AtsImage("zoom_out.gif");

   protected AtsImage(String filename) {
      super(ENUM_ID, ENUM_ID + Long.valueOf(filename.hashCode()), filename);
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
