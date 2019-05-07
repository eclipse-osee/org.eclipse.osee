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
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Ryan D. Brooks
 */
public enum FrameworkImage implements KeyedImage {
   DELETE_MERGE_BRANCHES("abort_merge.gif"),
   ACCEPT("accept.gif"),
   ADD_GREEN("add.png"),
   ARCHIVE("archive.gif"),
   ARTIFACT_EDITOR("artifact_editor.gif"),
   ARTIFACT_MASS_EDITOR("artifact_mass_editor.gif"),
   ARTIFACT_EXPLORER("artifact_explorer.gif"),
   ARTIFACT_IMPORT_WIZARD("artifact_import_wiz.png"),
   ARTIFACT_SEARCH("artifact_search.gif"),
   ARTIFACT_VERSION("artifact_version.gif"),
   ARROW_RIGHT_YELLOW("nav_forward.gif"),
   ARROW_UP_YELLOW("up.gif"),
   ARROW_DOWN_YELLOW("down.gif"),
   ARROW_LEFT_YELLOW("nav_backward.gif"),
   ATTRIBUTE_MOLECULE("molecule.gif"),
   ATTRIBUTE_SUB_A("attribute.gif"),
   ATTRIBUTE_DISABLED("disabled_attribute.gif"),
   APPLICATION_SERVER("appserver.gif"),
   AUTHENTICATED("authenticated.gif"),
   BACK("back.png"),
   BLAM("blam.gif"),
   BRANCH_CHANGE("branch_change.gif"),
   BRANCH("branch.gif"),
   BRANCH_SYSTEM_ROOT("branchYellow.gif"),
   BRANCH_BASELINE("baseline.gif"),
   BRANCH_CHANGE_DEST("branch_change_dest.gif"),
   BRANCH_CHANGE_SOURCE("branch_change_source.gif"),
   BRANCH_IN_CREATION_OVERLAY("waiting_ovr.gif"),
   BRANCH_MERGE("merge.gif"),
   BRANCH_WORKING("working.gif"),
   BRANCH_FAVORITE_OVERLAY("star_9_9.gif"),
   BRANCH_CHANGE_MANAGED("change_managed_branch.gif"),
   BRANCH_COMMIT("commitBranch.gif"),
   BRANCH_SYNCH("branchSynch.gif"),
   BRANCH_VIEW("branchview.gif"),
   CALENDAR("calendar.png"),
   CHANGE_LOG("changelog_obj.gif"),
   CHECKBOX_CHECK_TRUE("checkbox_check.gif"),
   CHECKBOX_CHECK_FALSE("checkbox_x.png"),
   CHECKBOX_CHECK_UNSET("checkbox_unset.gif"),
   CLEAR_CO("clear_co.gif"),
   CLOCK("clock.gif"),
   COLLAPSE_ALL("collapseAll.gif"),
   COMPARE_DOCUMENTS("compareDocs.gif"),
   COMPARE_HEAD_TX("head.gif"),
   COMPARE_PARENT_BRANCH("branches.gif"),
   COMPARE_OTHER_BRANCH("branches_rep.gif"),
   CONFLICTING_Deleted("CONFLICTING_Deleted.gif"),
   CONFLICTING_Modified("CONFLICTING_Modified.gif"),
   CONFLICTING_New("CONFLICTING_New.gif"),
   COPYTOCLIPBOARD("copyToClipboard.gif"),
   CUSTOMIZE("customize.gif"),
   DELTAS("compare.GIF"),
   DELTAS_BASE_TO_HEAD_TXS("compareBaseToHeadTxs.GIF"),
   DELTAS_DIFFERENT_BRANCHES_WITH_MERGE("compareBranchesWithMerge.GIF"),
   DELTAS_DIFFERENT_BRANCHES("compareBranches.GIF"),
   DELTAS_TXS_SAME_BRANCH("compareTxs.GIF"),
   DB_ICON_BLUE("DBiconBlue.GIF"),
   DB_ICON_BLUE_EDIT("DBiconBlueEdit.GIF"),
   DELETE("delete.png"),
   DELETE_EDIT("delete_edit.gif"),
   DIRTY("dirty.gif"),
   DOCUMENT("docOrder.gif"),
   DOT_RED("red_light.gif"),
   DOT_YELLOW("yellow_light.gif"),
   DOT_GREEN("green_light.gif"),
   DUPLICATE("duplicate.gif"),
   EDIT("edit.gif"),
   EDIT2("edit2.gif"),
   EDIT_BLUE("DBiconBlueEdit.GIF"),
   EDIT_ARTIFACT("edit_artifact.gif"),
   EMAIL("email.gif"),
   ERASE("erase.gif"),
   ERROR("errorRound.gif"),
   ERROR_OVERLAY("error.gif"),
   EXPAND_ALL("expandAll.gif"),
   EXPORT_DATA("export_data.gif"),
   EXPORT_TABLE("export_table.gif"),
   EXCLAIM_RED("redExclaim.gif"),
   FEATURE("feature.gif"),
   FLASHLIGHT("flashlight.gif"),
   FILTERS("filter.gif"),
   GEAR("gear.gif"),
   GREEN_PLUS("greenPlus.gif"),
   GROUP("group.gif"),
   HEADING("heading.gif"),
   HELP("help.gif"),
   IMPLEMENTATION_DETAILS("implementationDetails.gif"),
   IMPLEMENTATION_DETAILS_PROCEDURE("implementation_details_procedure.gif"),
   IMPLEMENTATION_DETAILS_FUNCTION("implementation_details_function.gif"),
   IMPLEMENTATION_DETAILS_DRAWING("implementation_details_drawing.gif"),
   IMPLEMENTATION_DETAILS_DATA_DEFINITION("implementation_details_data_definition.gif"),
   IMPORT("import.gif"),
   INCOMING_APPLICABILITY("INCOMING_Modified.gif"),
   INCOMING_ARTIFACT_DELETED("INCOMING_Deleted.gif"),
   INCOMING_DELETED("INCOMING_Deleted.gif"),
   INCOMING_INTRODUCED("INCOMING_New.gif"),
   INCOMING_MODIFIED("INCOMING_Modified.gif"),
   INCOMING_NEW("INCOMING_New.gif"),
   INFO_SM("info_sm.gif"),
   INFO_LG("info_lg.gif"),
   JAVA_COMPILATION_UNIT("jcu_obj.gif"),
   LASER("laser_16_16.gif"),
   LASER_OVERLAY("laser_8_8.gif"),
   LEFT_ARROW_1("leftarrow1.png"),
   LEFT_ARROW_N("leftarrowN.png"),
   LINE_MATCH("line_match.gif"),
   LINK("link.gif"),
   LOAD("load.gif"),
   LOCKED("lock.gif"),
   LOCK_LOCKED("lock_locked.gif"),
   LOCK_UNLOCKED("lock_unlocked.gif"),
   LOCK_OVERLAY("lock_overlay.gif"),
   LOCKED_KEY("lockkey.gif"),
   LOCKED_NO_ACCESS("red_lock.gif"),
   LOCKED_WITH_ACCESS("green_lock.gif"),
   OSEE_TYPES_LINK("link_obj.gif"),
   PURGE("purge.gif"),
   NOT_EQUAL("not_equal.gif"),
   NAV_BACKWARD("nav_backward.gif"),
   NAV_FORWARD("nav_forward.gif"),
   MAGNIFY("magnify.gif"),
   MERGE("merge.gif"),
   MERGE_SOURCE("green_s.gif"),
   MERGE_DEST("blue_d.gif"),
   MERGE_YELLOW_M("yellow_m.gif"),
   MERGE_START("conflict.gif"),
   MERGE_INFO("issue.gif"),
   MERGE_MARKED(PluginUiImage.CHECKBOX_ENABLED),
   MERGE_EDITED(PluginUiImage.CHECKBOX_DISABLED),
   MERGE_OUT_OF_DATE("chkbox_red.gif"),
   MERGE_OUT_OF_DATE_COMMITTED("chkbox_enabled_conflicted.gif"),
   MERGE_NO_CONFLICT("accept.gif"),
   MERGE_NOT_RESOLVEABLE("red_light.gif"),
   MERGE_SUCCESS("icon_success.gif"),
   MERGE_CAUTION("icon_warning.gif"),
   OPEN("open.gif"),
   OSEE_32_RUN("osee_32_run.gif"),
   OUTGOING_DELETED_ON_DESTINATION("OUTGOING_Deleted.gif"),
   OUTGOING_APPLICABILITY("OUTGOING_Modified.gif"),
   OUTGOING_ARTIFACT_DELETED("OUTGOING_Deleted.gif"),
   OUTGOING_DELETED("OUTGOING_Deleted.gif"),
   OUTGOING_UNDELETED("OUTGOING_New.gif"),
   OUTGOING_INTRODUCED("OUTGOING_New.gif"),
   OUTGOING_MERGED("branch_merge.gif"),
   OUTGOING_MODIFIED("OUTGOING_Modified.gif"),
   OUTGOING_NEW("OUTGOING_New.gif"),
   OUTLINE("outline_co.gif"),
   PASTE_SPECIAL_WIZ("paste_wiz.png"),
   PROBLEM("greenBug.gif"),
   PREVIEW_ARTIFACT("preview_artifact.gif"),
   PRINT("print.gif"),
   PURPLE("purple.gif"),
   QUESTION("question.gif"),
   RECTANGLE_16("rectangle16.gif"),
   RECTANGLE_24("rectangle24.gif"),
   RELATION("relate.gif"),
   RELOAD("reload.gif"),
   REFINEMENT("minor.gif"),
   REFRESH("refresh.gif"),
   RES("res.gif"),
   REMOVE("remove.gif"),
   REMOVE_ALL("removeAll.gif"),
   REPORT("report.gif"),
   REPOSITORY("repository.gif"),
   REJECT("reject.gif"),
   RIGHT_ARROW_1("rightarrow1.png"),
   RIGHT_ARROW_N("rightarrowN.png"),
   ROOT_HIERARCHY("package_obj.gif"),
   RUN_EXC("run_exc.gif"),
   PAGE("page.gif"),
   RULE("rule.gif"),
   SAVE_NEEDED("save.gif"),
   SAVED("saved.gif"),
   SAVE("save.gif"),
   SAVE_AS("saveas.gif"),
   SEVERITY_MAJOR("major.gif"),
   SEVERITY_MINOR("minor.gif"),
   SEVERITY_ISSUE("issue.gif"),
   SLASH_RED_OVERLAY("red_slash.gif"),
   hardware_design("hardware_design.gif"),
   hardware_requirement("hardware_requirement.gif"),
   software_design("software_design.gif"),
   software_requirement("software_requirement.gif"),
   SOFTWARE_REQUIREMENT_DATA_DEFINITION("software_requirement_data_definition.gif"),
   SOFTWARE_REQUIERMENT_DRAWING("software_requirement_drawing.gif"),
   SOFTWARE_REQUIERMENT_FUNCTION("software_requirement_function.gif"),
   SOFTWARE_REQUIERMENT_PROCEDURE("software_requirement_procedure.gif"),
   subsystem_design("subsystem_design.gif"),
   subsystem_requirement("subsystem_requirement.gif"),
   system_requirement("system_requirement.gif"),
   SWITCHED("switched.gif"),
   TRASH("trash.gif"),
   TUPLE("tuple.png"),
   SKYWALKER("skywalker.gif"),
   SUPPORT("users2.gif"),
   USER("userPurple.gif"),
   USER_PURPLE("userPurple.gif"),
   USER_RED("userRed.gif"),
   USER_GREY("userGrey.gif"),
   USER_YELLOW("userYellow.gif"),
   USER_ADD("userAdd.gif"),
   USERS("users2.gif"),
   UN_ARCHIVE("unarchive.gif"),
   VERSION("version.gif"),
   VERSION_NEXT("versionNext.gif"),
   WORKFLOW("workflow.gif"),
   WIDGET("widget.gif"),
   WARNING("warn.gif"),
   WARNING_OVERLAY("alert_8_8.gif"),
   WWW(PluginUiImage.URL),
   X_RED("redRemove.gif"),
   ZOOM_IN("zoom_in.gif"),
   ZOOM_OUT("zoom_out.gif");

   private final String fileName;
   private final KeyedImage alias;

   private FrameworkImage(String fileName) {
      this.fileName = fileName;
      this.alias = null;
   }

   private FrameworkImage(KeyedImage alias) {
      this.fileName = alias.getImageKey();
      this.alias = alias;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      if (alias == null) {
         return ImageManager.createImageDescriptor(Activator.PLUGIN_ID, fileName);
      }
      return alias.createImageDescriptor();
   }

   @Override
   public String getImageKey() {
      if (alias == null) {
         return Activator.PLUGIN_ID + "." + fileName;
      }
      return alias.getImageKey();
   }
}