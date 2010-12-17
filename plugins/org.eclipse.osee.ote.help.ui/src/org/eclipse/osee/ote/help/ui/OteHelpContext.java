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
package org.eclipse.osee.ote.help.ui;

import org.eclipse.osee.framework.ui.plugin.util.HelpContext;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;

/**
 * @author Roberto E. Escobar
 */
public class OteHelpContext {

   private OteHelpContext() {
      // Private Constructor
   }

   private static final String PLUGIN_ID = "org.eclipse.osee.ote.help.ui";

   public static final HelpContext OTE_NAVIGATOR = toContext("ote_navigator");
   public static final HelpContext TEST_MANAGER = toContext("test_manager");
   public static final HelpContext TEST_MANAGER__OVERVIEW = toContext("test_manager_overview_page");
   public static final HelpContext TEST_MANAGER__HOSTS = toContext("test_manager_hosts_page");
   public static final HelpContext TEST_MANAGER__SCRIPTS = toContext("test_manager_scripts_page");
   public static final HelpContext TEST_MANAGER__ADVANCED = toContext("test_manager_advanced_page");
   public static final HelpContext TEST_MANAGER__SOURCE = toContext("test_manager_source_page");
   public static final HelpContext MESSAGE_VIEW = toContext("message_view");
   public static final HelpContext MESSAGE_VIEW__SEARCH = toContext("message_view_search");
   public static final HelpContext MESSAGE_VIEW__WATCH = toContext("message_view_watch");
   public static final HelpContext MESSAGE_WATCH = toContext("message_watch");
   public static final HelpContext MUX_VIEW = toContext("mux_view");

   private static HelpContext toContext(String id) {
      return HelpUtil.asContext(PLUGIN_ID, id);
   }
}
