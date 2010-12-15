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
package org.eclipse.osee.coverage.help.ui;

import org.eclipse.osee.framework.ui.plugin.util.HelpContext;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;

/**
 * @author Roberto E. Escobar
 */
public class CoverageHelpContext {

   private CoverageHelpContext() {
      // Private Constructor
   }

   private static final String PLUGIN_ID = "org.eclipse.osee.framework.help.ui";

   public static final HelpContext NAVIGATOR = toContext("coverage_navigator");

   public static final HelpContext EDITOR = toContext("coverage_editor");
   public static final HelpContext EDITOR__COVERAGE_TAB = toContext("coverage_editor_coverage_tab");
   public static final HelpContext EDITOR__IMPORT_TAB = toContext("coverage_editor_import_tab");
   public static final HelpContext EDITOR__LOADING_TAB = toContext("coverage_editor_loading_tab");
   public static final HelpContext EDITOR__MERGE_TAB = toContext("coverage_editor_merge_tab");
   public static final HelpContext EDITOR__OVERVIEW_TAB = toContext("coverage_editor_overview_tab");
   public static final HelpContext EDITOR__WORK_PRODUCT_TAB = toContext("coverage_editor_work_product_tab");

   private static HelpContext toContext(String id) {
      return HelpUtil.asContext(PLUGIN_ID, id);
   }
}
