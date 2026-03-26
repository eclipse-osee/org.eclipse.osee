/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.OseeImage;

/**
 * @author Donald G. Dunne
 */
public class CoreImage extends OseeImage {

   public static List<CoreImage> values = new ArrayList<>();
   private static final Long ENUM_ID = 3442524L;
   private static String IDE_PLUGIN_ID = "org.eclipse.osee.framework.ui.skynet";

   public static CoreImage ACCEPT = new CoreImage("accept.gif");
   public static CoreImage ADD_GREEN = new CoreImage("add.png");
   public static CoreImage APPLICATION_SERVER = new CoreImage("appserver.gif");
   public static CoreImage ARCHIVE = new CoreImage("archive.gif");
   public static CoreImage ARROW_DOWN_YELLOW = new CoreImage("down.gif");
   public static CoreImage ARROW_LEFT_YELLOW = new CoreImage("nav_backward.gif");
   public static CoreImage ARROW_RIGHT_YELLOW = new CoreImage("nav_forward.gif");
   public static CoreImage ARROW_UP_YELLOW = new CoreImage("up.gif");
   public static CoreImage ARTIFACT_EDITOR = new CoreImage("artifact_editor.gif");
   public static CoreImage ARTIFACT_EXPLORER = new CoreImage("artifact_explorer.gif");
   public static CoreImage ARTIFACT_IMPORT_WIZARD = new CoreImage("artifact_import_wiz.png");
   public static CoreImage ARTIFACT_MASS_EDITOR = new CoreImage("artifact_mass_editor.gif");
   public static CoreImage ARTIFACT_SEARCH = new CoreImage("artifact_search.gif");
   public static CoreImage ARTIFACT_VERSION = new CoreImage("artifact_version.gif");
   public static CoreImage ATTRIBUTE_DISABLED = new CoreImage("disabled_attribute.gif");
   public static CoreImage ATTRIBUTE_MOLECULE = new CoreImage("molecule.gif");
   public static CoreImage ATTRIBUTE_SUB_A = new CoreImage("attribute.gif");
   public static CoreImage AUTHENTICATED = new CoreImage("authenticated.gif");
   public static CoreImage BACK = new CoreImage("back.png");
   public static CoreImage BLAM = new CoreImage("blam.gif");
   public static CoreImage BRANCH = new CoreImage("branch.gif");
   public static CoreImage BRANCH_BASELINE = new CoreImage("baseline.gif");
   public static CoreImage BRANCH_CHANGE = new CoreImage("branch_change.gif");
   public static CoreImage BRANCH_CHANGE_DEST = new CoreImage("branch_change_dest.gif");
   public static CoreImage BRANCH_CHANGE_MANAGED = new CoreImage("change_managed_branch.gif");
   public static CoreImage BRANCH_CHANGE_SOURCE = new CoreImage("branch_change_source.gif");
   public static CoreImage BRANCH_COMMIT = new CoreImage("commitBranch.gif");
   public static CoreImage BRANCH_FAVORITE_OVERLAY = new CoreImage("star_9_9.gif");
   public static CoreImage BRANCH_IN_CREATION_OVERLAY = new CoreImage("waiting_ovr.gif");
   public static CoreImage BRANCH_MERGE = new CoreImage("merge.gif");
   public static CoreImage BRANCH_SYNCH = new CoreImage("branchSynch.gif");
   public static CoreImage BRANCH_SYSTEM_ROOT = new CoreImage("branchYellow.gif");
   public static CoreImage BRANCH_VIEW = new CoreImage("branchview.gif");
   public static CoreImage BRANCH_WORKING = new CoreImage("working.gif");
   public static CoreImage CALENDAR = new CoreImage("calendar.png");
   public static CoreImage CHANGE_LOG = new CoreImage("changelog_obj.gif");
   public static CoreImage CHECKBOX_CHECK_FALSE = new CoreImage("checkbox_x.png");
   public static CoreImage CHECKBOX_CHECK_TRUE = new CoreImage("checkbox_check.gif");
   public static CoreImage CHECKBOX_CHECK_UNSET = new CoreImage("checkbox_unset.gif");
   public static CoreImage CLEAR_CO = new CoreImage("clear_co.gif");
   public static CoreImage CLOCK = new CoreImage("clock.gif");
   public static CoreImage COLLAPSE_ALL = new CoreImage("collapseAll.gif");
   public static CoreImage COMPARE_DOCUMENTS = new CoreImage("compareDocs.gif");
   public static CoreImage COMPARE_HEAD_TX = new CoreImage("head.gif");
   public static CoreImage COMPARE_OTHER_BRANCH = new CoreImage("branches_rep.gif");
   public static CoreImage COMPARE_PARENT_BRANCH = new CoreImage("branches.gif");
   public static CoreImage CONFLICTING_Deleted = new CoreImage("CONFLICTING_Deleted.gif");
   public static CoreImage CONFLICTING_Modified = new CoreImage("CONFLICTING_Modified.gif");
   public static CoreImage CONFLICTING_New = new CoreImage("CONFLICTING_New.gif");
   public static CoreImage COPYTOCLIPBOARD = new CoreImage("copyToClipboard.gif");
   public static CoreImage CUSTOMIZE = new CoreImage("customize.gif");
   public static CoreImage DB_ICON_BLUE = new CoreImage("DBiconBlue.GIF");
   public static CoreImage DB_ICON_BLUE_EDIT = new CoreImage("DBiconBlueEdit.GIF");
   public static CoreImage DB_ID = new CoreImage("dbId.gif");
   public static CoreImage DEBUG = new CoreImage("debug.gif");
   public static CoreImage DELETE = new CoreImage("delete.png");
   public static CoreImage DELETE_EDIT = new CoreImage("delete_edit.gif");
   public static CoreImage DELETE_MERGE_BRANCHES = new CoreImage("abort_merge.gif");
   public static CoreImage DELTAS = new CoreImage("compare.GIF");
   public static CoreImage DELTAS_BASE_TO_HEAD_TXS = new CoreImage("compareBaseToHeadTxs.gif");
   public static CoreImage DELTAS_DIFFERENT_BRANCHES = new CoreImage("compareBranches.GIF");
   public static CoreImage DELTAS_DIFFERENT_BRANCHES_WITH_MERGE = new CoreImage("compareBranchesWithMerge.GIF");
   public static CoreImage DELTAS_TXS_SAME_BRANCH = new CoreImage("compareTxs.GIF");
   public static CoreImage DIRTY = new CoreImage("dirty.gif");
   public static CoreImage DOCUMENT = new CoreImage("docOrder.gif");
   public static CoreImage DOT_GREEN = new CoreImage("green_light.gif");
   public static CoreImage DOT_RED = new CoreImage("red_light.gif");
   public static CoreImage DOT_YELLOW = new CoreImage("yellow_light.gif");
   public static CoreImage DUPLICATE = new CoreImage("duplicate.gif");
   public static CoreImage EDIT = new CoreImage("edit.gif");
   public static CoreImage EDIT2 = new CoreImage("edit2.gif");
   public static CoreImage EDIT_ARTIFACT = new CoreImage("edit_artifact.gif");
   public static CoreImage EDIT_BLUE = DB_ICON_BLUE_EDIT;
   public static CoreImage EMAIL = new CoreImage("email.gif");
   public static CoreImage ERASE = new CoreImage("erase.gif");
   public static CoreImage ERROR = new CoreImage("errorRound.gif");
   public static CoreImage ERROR_OVERLAY = new CoreImage("error.gif");
   public static CoreImage EXAMPLE = new CoreImage("example.gif");
   public static CoreImage EXCLAIM_RED = new CoreImage("redExclaim.gif");
   public static CoreImage EXPAND_ALL = new CoreImage("expandAll.gif");
   public static CoreImage EXPORT = new CoreImage("export.gif");
   public static CoreImage EXPORT_DATA = new CoreImage("export_data.gif");
   public static CoreImage EXPORT_TABLE = new CoreImage("export_table.gif");
   public static CoreImage FEATURE = new CoreImage("feature.gif");
   public static CoreImage FILTERS = new CoreImage("filter.gif");
   public static CoreImage FLASHLIGHT = new CoreImage("flashlight.gif");
   public static CoreImage FLAT_LAYOUT = new CoreImage("flat_layout.gif");
   public static CoreImage GEAR = new CoreImage("gear.gif");
   public static CoreImage GEN_ACRONYM = new CoreImage("gen_acronym.gif");
   public static CoreImage GEN_CUST_REQ = new CoreImage("gen_customer_requirement.gif");
   public static CoreImage GEN_REF = new CoreImage("gen_ref.gif");
   public static CoreImage GEN_REQ = new CoreImage("gen_requirement.gif");
   public static CoreImage GEN_SUBSYS_DES = new CoreImage("gen_subsystem_design.gif");
   public static CoreImage GEN_SUBSYS_REQ = new CoreImage("gen_subsystem_requirement.gif");
   public static CoreImage GEN_SYS_DES = new CoreImage("gen_system_design.gif");
   public static CoreImage GEN_SYS_REQ = new CoreImage("gen_system_requirement.gif");
   public static CoreImage GREEN_PLUS = new CoreImage("greenPlus.gif");
   public static CoreImage GROUP = new CoreImage("group.gif");
   public static CoreImage HEADING = new CoreImage("heading.gif");
   public static CoreImage HEALTH = new CoreImage("health.gif");
   public static CoreImage HELP = new CoreImage("help.gif");
   public static CoreImage HOLD = new CoreImage("hold.gif");
   public static CoreImage HORIZONTAL = new CoreImage("horizontal.gif");
   public static CoreImage ID = new CoreImage("id.gif");
   public static CoreImage IMPLEMENTATION_DETAILS = new CoreImage("implementationDetails.gif");
   public static CoreImage IMPLEMENTATION_DETAILS_DATA_DEFINITION =
      new CoreImage("implementation_details_data_definition.gif");
   public static CoreImage IMPLEMENTATION_DETAILS_DRAWING = new CoreImage("implementation_details_drawing.gif");
   public static CoreImage IMPLEMENTATION_DETAILS_FUNCTION = new CoreImage("implementation_details_function.gif");
   public static CoreImage IMPLEMENTATION_DETAILS_PROCEDURE = new CoreImage("implementation_details_procedure.gif");
   public static CoreImage IMPORT = new CoreImage("import.gif");
   public static CoreImage INCOMING_APPLICABILITY = new CoreImage("INCOMING_Modified.gif");
   public static CoreImage INCOMING_ARTIFACT_DELETED = new CoreImage("INCOMING_Deleted.gif");
   public static CoreImage INCOMING_DELETED = INCOMING_ARTIFACT_DELETED;
   public static CoreImage INCOMING_INTRODUCED = new CoreImage("INCOMING_New.gif");
   public static CoreImage INCOMING_MODIFIED = INCOMING_APPLICABILITY;
   public static CoreImage INCOMING_NEW = new CoreImage("INCOMING_New.gif");
   public static CoreImage INFO_LG = new CoreImage("info_lg.gif");
   public static CoreImage INFO_SM = new CoreImage("info_sm.gif");
   public static CoreImage JAVA_COMPILATION_UNIT = new CoreImage("jcu_obj.gif");
   public static CoreImage LASER = new CoreImage("laser_16_16.gif");
   public static CoreImage LASER_OVERLAY = new CoreImage("laser_8_8.gif");
   public static CoreImage LEFT_ARROW_1 = new CoreImage("leftarrow1.png");
   public static CoreImage LEFT_ARROW_N = new CoreImage("leftarrowN.png");
   public static CoreImage LINE_MATCH = new CoreImage("line_match.gif");
   public static CoreImage LINK = new CoreImage("link.gif");
   public static CoreImage LOAD = new CoreImage("load.gif");
   public static CoreImage LOCKED = new CoreImage("lock.gif");
   public static CoreImage LOCKED_KEY = new CoreImage("lockkey.gif");
   public static CoreImage LOCKED_NO_ACCESS = new CoreImage("red_lock.gif");
   public static CoreImage LOCKED_WITH_ACCESS = new CoreImage("green_lock.gif");
   public static CoreImage LOCK_DETAILS = new CoreImage("lock_details.gif");
   public static CoreImage LOCK_LOCKED = new CoreImage("lock_locked.gif");
   public static CoreImage LOCK_OVERLAY = new CoreImage("lock_overlay.gif");
   public static CoreImage LOCK_UNLOCKED = new CoreImage("lock_unlocked.gif");
   public static CoreImage MAGNIFY = new CoreImage("magnify.gif");
   public static CoreImage MERGE = new CoreImage("merge.gif");
   public static CoreImage MERGE_CAUTION = new CoreImage("icon_warning.gif");
   public static CoreImage MERGE_DEST = new CoreImage("blue_d.gif");
   public static CoreImage MERGE_EDITED = new CoreImage("chkbox_disabled.gif");
   public static CoreImage MERGE_INFO = new CoreImage("issue.gif");
   public static CoreImage MERGE_MARKED = new CoreImage("chkbox_enabled.gif");
   public static CoreImage MERGE_MERGED = new CoreImage("yellow_m.gif");
   public static CoreImage MERGE_NOT_RESOLVEABLE = new CoreImage("red_light.gif");
   public static CoreImage MERGE_NO_CONFLICT = new CoreImage("accept.gif");
   public static CoreImage MERGE_OUT_OF_DATE = new CoreImage("chkbox_red.gif");
   public static CoreImage MERGE_OUT_OF_DATE_COMMITTED = new CoreImage("chkbox_enabled_conflicted.gif");
   public static CoreImage MERGE_SOURCE = new CoreImage("green_s.gif");
   public static CoreImage MERGE_START = new CoreImage("conflict.gif");
   public static CoreImage MERGE_SUCCESS = new CoreImage("icon_success.gif");
   public static CoreImage NAV_BACKWARD = new CoreImage("nav_backward.gif");
   public static CoreImage NAV_FORWARD = ARROW_RIGHT_YELLOW;
   public static CoreImage NOT_EQUAL = new CoreImage("not_equal.gif");
   public static CoreImage OPEN = new CoreImage("open.gif");
   public static CoreImage OSEE_32_RUN = new CoreImage("osee_32_run.gif");
   public static CoreImage OSEE_MARKDOWN_EDIT = new CoreImage("oseeMarkdownEdit.gif");
   public static CoreImage OSEE_TYPES_LINK = new CoreImage("link_obj.gif");
   public static CoreImage OUTGOING_APPLICABILITY = new CoreImage("OUTGOING_Modified.gif");
   public static CoreImage OUTGOING_ARTIFACT_DELETED = new CoreImage("OUTGOING_Deleted.gif");
   public static CoreImage OUTGOING_DELETED = new CoreImage("OUTGOING_Deleted.gif");
   public static CoreImage OUTGOING_DELETED_ON_DESTINATION = new CoreImage("OUTGOING_Deleted.gif");
   public static CoreImage OUTGOING_INTRODUCED = new CoreImage("OUTGOING_New.gif");
   public static CoreImage OUTGOING_MERGED = new CoreImage("branch_merge.gif");
   public static CoreImage OUTGOING_MODIFIED = new CoreImage("OUTGOING_Modified.gif");
   public static CoreImage OUTGOING_NEW = new CoreImage("OUTGOING_New.gif");
   public static CoreImage OUTGOING_UNDELETED = new CoreImage("OUTGOING_New.gif");
   public static CoreImage OUTLINE = new CoreImage("outline_co.gif");
   public static CoreImage PAGE = new CoreImage("page.gif");
   public static CoreImage PASTE_SPECIAL_WIZ = new CoreImage("paste_wiz.png");
   public static CoreImage PLE = new CoreImage("ple.gif");
   public static CoreImage PPTX = new CoreImage("pptx.png");
   public static CoreImage PREVIEW_ARTIFACT = new CoreImage("preview_artifact.gif");
   public static CoreImage PRINT = new CoreImage("print.gif");
   public static CoreImage PROBLEM = new CoreImage("greenBug.gif");
   public static CoreImage PURGE = new CoreImage("purge.gif");
   public static CoreImage PURPLE = new CoreImage("purple.gif");
   public static CoreImage QUESTION = new CoreImage("question.gif");
   public static CoreImage RECTANGLE_16 = new CoreImage("rectangle16.gif");
   public static CoreImage RECTANGLE_24 = new CoreImage("rectangle24.gif");
   public static CoreImage REFINEMENT = new CoreImage("minor.gif");
   public static CoreImage REFRESH = new CoreImage("refresh.gif");
   public static CoreImage REJECT = new CoreImage("reject.gif");
   public static CoreImage RELATION = new CoreImage("relate.gif");
   public static CoreImage RELAUNCH = new CoreImage("relaunch.png");
   public static CoreImage RELOAD = new CoreImage("reload.gif");
   public static CoreImage REMOVE = new CoreImage("remove.gif");
   public static CoreImage REMOVE_ALL = new CoreImage("removeAll.gif");
   public static CoreImage REPORT = new CoreImage("report.gif");
   public static CoreImage REPOSITORY = new CoreImage("repository.gif");
   public static CoreImage RES = new CoreImage("res.gif");
   public static CoreImage RIGHT_ARROW_1 = new CoreImage("rightarrow1.png");
   public static CoreImage RIGHT_ARROW_N = new CoreImage("rightarrowN.png");
   public static CoreImage ROOT_HIERARCHY = new CoreImage("package_obj.gif");
   public static CoreImage RULE = new CoreImage("rule.gif");
   public static CoreImage RUN_EXC = new CoreImage("run_exc.gif");
   public static CoreImage SAVE = new CoreImage("save.gif");
   public static CoreImage SAVED = new CoreImage("saved.gif");
   public static CoreImage SAVE_AS = new CoreImage("saveas.gif");
   public static CoreImage SAVE_NEEDED = new CoreImage("save.gif");
   public static CoreImage SEVERITY_ISSUE = new CoreImage("issue.gif");
   public static CoreImage SEVERITY_MAJOR = new CoreImage("major.gif");
   public static CoreImage SEVERITY_MINOR = new CoreImage("minor.gif");
   public static CoreImage SKYWALKER = new CoreImage("skywalker.gif");
   public static CoreImage SLASH_RED_OVERLAY = new CoreImage("red_slash.gif");
   public static CoreImage SOFTWARE_REQUIERMENT_DRAWING = new CoreImage("software_requirement_drawing.gif");
   public static CoreImage SOFTWARE_REQUIERMENT_FUNCTION = new CoreImage("software_requirement_function.gif");
   public static CoreImage SOFTWARE_REQUIERMENT_PROCEDURE = new CoreImage("software_requirement_procedure.gif");
   public static CoreImage SOFTWARE_REQUIREMENT_DATA_DEFINITION =
      new CoreImage("software_requirement_data_definition.gif");
   public static CoreImage SUPPORT = new CoreImage("users2.gif");
   public static CoreImage SWITCHED = new CoreImage("switched.gif");
   public static CoreImage TERMINATE_AND_RELAUNCH = new CoreImage("termAndRelaunch.png");
   public static CoreImage TEST_PROCEDURE = new CoreImage("test_procedure.gif");
   public static CoreImage TRACE = new CoreImage("trace.gif");
   public static CoreImage TRASH = new CoreImage("trash.gif");
   public static CoreImage TREE_LAYOUT = new CoreImage("tree_layout.gif");
   public static CoreImage TUPLE = new CoreImage("tuple.png");
   public static CoreImage UNDO = new CoreImage("undo.gif");
   public static CoreImage UN_ARCHIVE = new CoreImage("unarchive.gif");
   public static CoreImage USER = new CoreImage("userPurple.gif");
   public static CoreImage USERS = new CoreImage("users2.gif");
   public static CoreImage USER_ADD = new CoreImage("userAdd.gif");
   public static CoreImage USER_GREY = new CoreImage("userGrey.gif");
   public static CoreImage USER_PURPLE = new CoreImage("userPurple.gif");
   public static CoreImage USER_RED = new CoreImage("userRed.gif");
   public static CoreImage USER_YELLOW = new CoreImage("userYellow.gif");
   public static CoreImage VERSION = new CoreImage("version.gif");
   public static CoreImage VERSION_NEXT = new CoreImage("versionNext.gif");
   public static CoreImage WARNING = new CoreImage("warn.gif");
   public static CoreImage WARNING_OVERLAY = new CoreImage("alert_8_8.gif");
   public static CoreImage WIDGET = new CoreImage("widget.gif");
   public static CoreImage WORKFLOW = new CoreImage("workflow.gif");
   public static CoreImage WWW = new CoreImage("www.gif");
   public static CoreImage X_RED = new CoreImage("redRemove.gif");
   public static CoreImage ZOOM_IN = new CoreImage("zoom_in.gif");
   public static CoreImage ZOOM_OUT = new CoreImage("zoom_out.gif");
   public static CoreImage hardware_design = new CoreImage("hardware_design.gif");
   public static CoreImage hardware_requirement = new CoreImage("hardware_requirement.gif");
   public static CoreImage software_design = new CoreImage("software_design.gif");
   public static CoreImage software_requirement = new CoreImage("software_requirement.gif");
   public static CoreImage subsystem_design = new CoreImage("subsystem_design.gif");
   public static CoreImage subsystem_requirement = new CoreImage("subsystem_requirement.gif");
   public static CoreImage system_requirement = new CoreImage("system_requirement.gif");

   protected CoreImage(String filename) {
      super(ENUM_ID, ENUM_ID + Long.valueOf(filename.hashCode()), filename);
      values.add(this);
   }

   @Override
   public String getPluginId() {
      return IDE_PLUGIN_ID;
   }

   public static Collection<CoreImage> getValues() {
      return values;
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }
}
