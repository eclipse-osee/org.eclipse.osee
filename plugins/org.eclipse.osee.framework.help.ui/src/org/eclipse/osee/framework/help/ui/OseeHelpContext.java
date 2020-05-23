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

package org.eclipse.osee.framework.help.ui;

import static org.eclipse.osee.framework.core.data.HelpContextRegistry.asContext;
import org.eclipse.osee.framework.core.data.HelpContext;

/**
 * @author Roberto E. Escobar
 */
public final class OseeHelpContext {

   private OseeHelpContext() {
      // Private Constructor
   }

   private static final String PLUGIN_ID = "org.eclipse.osee.framework.help.ui";

   public static final HelpContext ARTIFACT_EDITOR = toContext("artifact_editor");
   public static final HelpContext ARTIFACT_EDITOR__ATTRIBUTES = toContext("artifact_editor_attributes");
   public static final HelpContext ARTIFACT_EDITOR__RELATIONS = toContext("artifact_editor_relations");
   public static final HelpContext ARTIFACT_EDITOR__DETAILS = toContext("artifact_editor_details");

   public static final HelpContext ARTIFACT_EXPLORER = toContext("artifact_explorer");
   public static final HelpContext ARTIFACT_SEARCH = toContext("artifact_search");
   public static final HelpContext BLAM_EDITOR = toContext("blam_editor");
   public static final HelpContext CHANGE_REPORT_EDITOR = toContext("change_report_editor");
   public static final HelpContext BRANCH_MANAGER = toContext("branch_manager");
   public static final HelpContext DEFINE_NAVIGATOR = toContext("define_navigator");
   public static final HelpContext HISTORY_VIEW = toContext("resource_history_view");
   public static final HelpContext MASS_EDITOR = toContext("mass_artifact_editor");
   public static final HelpContext MERGE_MANAGER = toContext("merge_manager");

   public static final HelpContext QUICK_SEARCH = toContext("quick_search_view");
   public static final HelpContext QUICK_SEARCH_TYPE_FILTER = toContext("quick_search_attribute_type_filter");
   public static final HelpContext QUICK_SEARCH_INCLUDE_DELETED = toContext("quick_search_deleted_option");
   public static final HelpContext QUICK_SEARCH_WORD_ORDER = toContext("quick_search_word_order_option");
   public static final HelpContext QUICK_SEARCH_EXACT_MATCH = toContext("quick_search_exact_match_option");
   public static final HelpContext QUICK_SEARCH_CASE_SENSITIVE = toContext("quick_search_case_sensitive_option");

   public static final HelpContext SKY_WALKER_VIEW = toContext("sky_walker_view");

   public static final HelpContext TEST_RUN_VIEW = toContext("test_run_view");

   public static final HelpContext TABLE_CUSTOMIZATIONS = toContext("xviewer_table_customization");
   public static final HelpContext RESULTS_VIEW = toContext("xviewer_xresult_view");

   private static HelpContext toContext(String id) {
      return asContext(PLUGIN_ID, id);
   }
}
