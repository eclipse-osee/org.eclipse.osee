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

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Ryan D. Brooks
 */
public enum FrameworkImage implements OseeImage {
   ACCEPT("accept.gif"),
   ADMIN("admin.gif"),
   ADD_GREEN("add.gif"),
   ARCHIVE("archive.gif"),
   ARTIFACT_EDITOR("artifact_editor.gif"),
   ARTIFACT_EXPLORER("artifact_explorer.gif"),
   ARTIFACT_SEARCH("artifact_search.gif"),
   ARTIFACT_VERSION("artifact_versopm.gif"),
   ARROW_RIGHT_YELLOW("nav_forward.gif"),
   ARROW_UP_YELLOW("up.gif"),
   ARROW_DOWN_YELLOW("down.gif"),
   ATTRIBUTE_MOLECULE("molecule.gif"),
   ATTRIBUTE_SUB_A("attribute.gif"),
   ATTRIBUTE_DISABLED("disabled_attribute.gif"),
   APPLICATION_SERVER("appserver.gif"),
   AUTHENTICATED("authenticated.gif"),
   BLAM("blam.gif"),
   BRANCH_CHANGE("branch_change.gif"),
   BRANCH("branch.gif"),
   BRANCH_TOP("top.gif"),
   BRANCH_SYSTEM_ROOT("branchYellow.gif"),
   BRANCH_BASELINE("baseline.gif"),
   BRANCH_MERGE("merge.gif"),
   BRANCH_WORKING("working.gif"),
   BRANCH_FAVORITE_OVERLAY("star_9_9.gif"),
   BRANCH_CHANGE_MANAGED("change_managed_branch.gif"),
   BUG("bug.gif"),
   CHECKBOX_ENABLED("chkbox_enabled.gif"),
   CHECKBOX_DISABLED("chkbox_disabled.gif"),
   CLOCK("clock.gif"),
   COLLAPSE_ALL("collapseAll.gif"),
   CONFLICTING_Deleted("CONFLICTING_Deleted.gif"),
   CONFLICTING_Modified("CONFLICTING_Modified.gif"),
   CONFLICTING_New("CONFLICTING_New.gif"),
   COPYTOCLIPBOARD("copyToClipboard.gif"),
   CUSTOMIZE("customize.gif"),
   DB_ICON_BLUE("DBiconBlue.GIF"),
   DELETE("delete.gif"),
   DIRTY("dirty.gif"),
   DOT_RED("red_light.gif"),
   DOT_YELLOW("yellow_light.gif"),
   DOT_GREEN("green_light.gif"),
   DUPLICATE("duplicate.gif"),
   EDIT("edit.gif"),
   EDIT_BLUE("DBiconBlueEdit.GIF"),
   EDIT_ARTIFACT("edit_artifact.gif"),
   EMAIL("email.gif"),
   ERROR("errorRound.gif"),
   ERROR_OVERLAY("error.gif"),
   EXPAND_ALL("expandAll.gif"),
   EXPORT_DATA("export_data.gif"),
   EXPORT_TABLE("export_table.gif"),
   EXCLAIM_RED("redExclaim.gif"),
   FLASHLIGHT("flashlight.gif"),
   FILTERS("filter.gif"),
   FOLDER("folder.gif"),
   GEAR("gear.gif"),
   GREEN_PLUS("greenPlus.gif"),
   GROUP("group.gif"),
   HELP("help.gif"),
   IMPORT("import.gif"),
   INCOMING_Deleted("INCOMING_Deleted.gif"),
   INCOMING_Modified("INCOMING_Modified.gif"),
   INCOMING_New("INCOMING_New.gif"),
   LASER("laser_16_16.gif"),
   LASER_OVERLAY("laser_16_16.gif"),
   LINE_MATCH("line_match.gif"),
   LOCKED_KEY("lockkey.gif"),
   LOCKED_NO_ACCESS("red_lock.gif"),
   LOCKED_WITH_ACCESS("green_lock.gif"),
   NOT_EQUAL("not_equal.gif"),
   MAGNIFY("magnify.gif"),
   MISSING("missing"),
   MERGE("merge.gif"),
   MERGE_SOURCE("green_s.gif"),
   MERGE_DEST("blue_d.gif"),
   MERGE_YELLOW_M("yellow_m.gif"),
   MERGE_START("conflict.gif"),
   MERGE_INFO("issue.gif"),
   MERGE_MARKED("chkbox_enabled.gif"),
   MERGE_EDITED("chkbox_disabled.gif"),
   MERGE_OUT_OF_DATE("chkbox_red.gif"),
   MERGE_OUT_OF_DATE_COMMITTED("chkbox_enabled_conflicted"),
   MERGE_NO_CONFLICT("accept.gif"),
   MERGE_NOT_RESOLVEABLE("red_light.gif"),
   MERGE_SUCCESS("icon_success.gif"),
   MERGE_CAUTION("icon_warning.gif"),
   OUTGOING_Artifact_Deleted("OUTGOING_Deleted.gif"),
   OUTGOING_Deleted("OUTGOING_Deleted.gif"),
   OUTGOING_Introduced("OUTGOING_New.gif"),
   OUTGOING_Merged("branch_merge.gif"),
   OUTGOING_Modified("OUTGOING_Modified.gif"),
   OUTGOING_New("OUTGOING_New.gif"),
   OPEN("open.gif"),
   PROBLEM("greenBug.gif"),
   PRINT("print.gif"),
   PURPLE("purple.gif"),
   RECTANGLE_16("rectangle16"),
   RECTANGLE_24("rectangle24"),
   REFRESH("refresh.gif"),
   RELATION("relate.gif"),
   REMOVE("remove.gif"),
   REJECT("reject.gif"),
   RUN_EXC("run_exc.gif"),
   X_RED("redRemove.gif"),
   SAVE_NEEDED("needSave.gif"),
   SAVED("saved.gif"),
   SEVERITY_MAJOR("major.gif"),
   SEVERITY_MINOR("minor.gif"),
   SEVERITY_ISSUE("issue.gif"),
   SLASH_RED_OVERLAY("red_slash.gif"),
   TRASH("trash.gif"),
   SKYWALKER("skywalker.gif"),
   SUPPORT("users2.gif"),
   USER("user.gif"),
   USER_SM_RED("red_user_sm.gif"),
   USER_ADD("userAdd.gif"),
   UN_ARCHIVE("unarchive.gif"),
   VERSION("version.gif"),
   WARNING("warn.gif"),
   WARNING_OVERLAY("alert_8_8.gif");

   private final String fileName;

   private FrameworkImage(String fileName) {
      this.fileName = fileName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#getImageDescriptor()
    */
   @Override
   public ImageDescriptor createImageDescriptor() {
      if (this == MISSING) {
         return ImageDescriptor.getMissingImageDescriptor();
      }
      return ImageManager.createImageDescriptor(SkynetGuiPlugin.PLUGIN_ID, "images", fileName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#getImageKey()
    */
   @Override
   public String getImageKey() {
      return SkynetGuiPlugin.PLUGIN_ID + "." + fileName;
   }
}